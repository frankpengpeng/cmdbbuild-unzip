-- system functions (read access and utils)
-- REQUIRE PATCH 3.0.0-03a_system_functions


--- SYSTEM UTILS ---

CREATE OR REPLACE FUNCTION _cm3_utils_random_id() RETURNS varchar AS $$ 
    SELECT md5(random()::text);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_message_send(_data jsonb) RETURNS VOID AS $$
	LISTEN cminfo;
	SELECT pg_notify('cmevents', _data::varchar);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_message_send(_type varchar, _data jsonb) RETURNS VOID AS $$
	SELECT _cm3_system_message_send(_data || jsonb_build_object('type', _type, 'id', (SELECT substring(_cm3_utils_random_id() from 0 for 8))));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_command_send(_action varchar, _data jsonb) RETURNS VOID AS $$
	SELECT _cm3_system_message_send('command', _data || jsonb_build_object('action', _action));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_command_send(_action varchar) RETURNS VOID AS $$
	SELECT _cm3_system_command_send(_action, '{}'::jsonb);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_command_send(_action varchar, VARIADIC _args varchar[]) RETURNS VOID AS $$
	SELECT _cm3_system_command_send(_action, jsonb_build_object('args', _args));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_event_send(_event varchar) RETURNS VOID AS $$
	SELECT _cm3_system_message_send('event', jsonb_build_object('event', _event));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_event_send(_event varchar, VARIADIC _args varchar[]) RETURNS VOID AS $$ BEGIN
	SELECT _cm3_system_message_send('event', jsonb_build_object(_args) || jsonb_build_object('event', _event));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_system_reload() RETURNS VOID AS $$
	SELECT _cm3_system_command_send('reload');
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_restart() RETURNS VOID AS $$
	SELECT _cm3_system_command_send('restart');
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_shutdown() RETURNS VOID AS $$
	SELECT _cm3_system_command_send('shutdown');
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_email_send() RETURNS VOID AS $$
	SELECT _cm3_system_command_send('email_queue_trigger', jsonb_build_object('cluster_mode', 'run_on_single_node'));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_email_send(_id bigint) RETURNS VOID AS $$
	SELECT _cm3_system_command_send('email_queue_send_single', jsonb_build_object('cluster_mode', 'run_on_single_node', '_email_id', _id));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_login() RETURNS VOID AS $$ BEGIN
	SET SESSION cmdbuild.operation_user = 'postgres';
	SET SESSION cmdbuild.user_tenants = '{}';
	SET SESSION cmdbuild.ignore_tenant_policies = 'true';
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_build_sqltype_string(_sqltype oid, _typemod integer) RETURNS varchar AS $$
	SELECT pg_type.typname::text || CASE
		WHEN _typemod IS NULL THEN ''
		WHEN pg_type.typname IN ('varchar','bpchar') AND _typemod < 0 THEN ''
		WHEN pg_type.typname IN ('varchar','bpchar') THEN '(' || _typemod - 4 || ')'
		WHEN pg_type.typname = 'numeric' THEN '(' || _typemod / 65536 || ',' || _typemod - _typemod / 65536 * 65536 - 4|| ')'
		ELSE ''
	END FROM pg_type WHERE pg_type.oid = _sqltype;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_utils_operation_user_get() RETURNS varchar AS $$ BEGIN
	RETURN current_setting('cmdbuild.operation_user');
EXCEPTION WHEN undefined_object THEN
	RETURN 'postgres';
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_new_card_id() RETURNS bigint AS $$
	SELECT nextval('class_seq')::bigint;
$$ LANGUAGE SQL VOLATILE;

CREATE OR REPLACE FUNCTION _cm3_utils_name_escape(_class_name varchar) RETURNS varchar AS $$ BEGIN
	IF _class_name ~ '^".*"$' THEN
		RETURN _class_name;
	ELSE
		IF _class_name ~ '^[^.]+[.][^.]+$' THEN
			RETURN regexp_replace(_class_name, '^([^.]+)[.]([^.]+)$', '"\1"."\2"');
		ELSE
			RETURN format('"%s"', _class_name);
		END IF;
	END IF; 
END $$ LANGUAGE PLPGSQL IMMUTABLE RETURNS NULL ON NULL INPUT;

CREATE OR REPLACE FUNCTION _cm3_utils_name_to_basename(_class_name varchar) RETURNS varchar AS $$ BEGIN
	return regexp_replace(regexp_replace(_class_name,'"','','g'),'^([^.]+)[.]','');
END $$ LANGUAGE PLPGSQL IMMUTABLE RETURNS NULL ON NULL INPUT;

CREATE OR REPLACE FUNCTION _cm3_utils_name_to_regclass(_class_name varchar) RETURNS regclass AS $$ BEGIN
	_class_name = _cm3_utils_name_escape(_class_name);
	IF (SELECT pg_get_function_arguments(oid) FROM pg_proc WHERE proname = 'to_regclass') = 'cstring' THEN
		RETURN to_regclass(_class_name::cstring);
	ELSE
		RETURN to_regclass(_class_name::text);
	END IF;
END $$ LANGUAGE PLPGSQL IMMUTABLE RETURNS NULL ON NULL INPUT;

CREATE OR REPLACE FUNCTION _cm3_utils_regclass_to_name(_class regclass) RETURNS varchar AS $$ BEGIN
	RETURN _cm3_utils_name_to_basename(_class::varchar);
END $$ LANGUAGE PLPGSQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_utils_regclass_to_domain_name(_class regclass) RETURNS varchar AS $$ BEGIN
	RETURN regexp_replace(_cm3_utils_regclass_to_name(_class), '^Map_', '');
END $$ LANGUAGE PLPGSQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_utils_regclass_to_history(_class regclass) RETURNS regclass AS $$ 
	SELECT format('"%s_history"',_cm3_utils_regclass_to_name(_class))::regclass;
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_utils_shrink_name(_name varchar, _maxlength int) RETURNS varchar AS $$ BEGIN
	IF length(_name) <= _maxlength THEN
		RETURN _name;
	ELSE
		RETURN substring( _name from 1 for ( _maxlength / 3 ) ) 
			|| substring( md5(_name) from 1 for ( _maxlength / 3 + _maxlength % 3 ) ) 
			|| substring( _name from ( length(_name) - _maxlength / 3 + 1 ) for ( _maxlength / 3 ) );
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_shrink_name(_name varchar) RETURNS varchar AS $$
	SELECT _cm3_utils_shrink_name(_name, 20);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_utils_shrink_name_lon(_name varchar) RETURNS varchar AS $$
	SELECT _cm3_utils_shrink_name(_name, 40);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_utils_strip_null_or_empty(_data jsonb) RETURNS jsonb AS $$
	SELECT coalesce(jsonb_object_agg(key, value), '{}'::jsonb) FROM jsonb_each(_data) WHERE coalesce(_data->>key,'') <> '';
$$ LANGUAGE SQL; 

CREATE OR REPLACE FUNCTION _cm3_utils_disk_usage_detailed() 
	RETURNS TABLE (schema varchar, item regclass, type varchar, row_estimate real, total_size bigint, total_size_pretty varchar) AS $$ BEGIN
	RETURN QUERY WITH q AS (SELECT a.schema, oid::regclass item,
			(CASE WHEN _cm3_class_is_simple(oid) THEN 'simpleclass'
				WHEN _cm3_class_is_standard(oid) THEN 'class'
				WHEN _cm3_class_is_domain(oid) THEN 'map'
				WHEN _cm3_class_is_history(oid) THEN 'history'
				ELSE 'other' END)::varchar AS type,
			a.row_estimate,
			total_bytes,
			pg_size_pretty(total_bytes)::varchar AS total
		FROM (
			SELECT 
				c.oid oid,
				nspname::varchar AS schema,
				c.reltuples AS row_estimate,
				pg_total_relation_size(c.oid) AS total_bytes, 
				pg_indexes_size(c.oid) AS index_bytes,
				COALESCE(pg_total_relation_size(reltoastrelid),0) AS toast_bytes
			FROM pg_class c LEFT JOIN pg_namespace n ON n.oid = c.relnamespace WHERE relkind = 'r' AND nspname IN ('public','gis')
		) a
  ) SELECT * FROM q ORDER BY schema, type, q.item::varchar;
END $$ LANGUAGE PLPGSQL;

-- CREATE OR REPLACE FUNCTION _cm3_utils_select_without_columns(_token ANYELEMENT, VARIADIC _without_columns varchar[]) RETURNS SETOF ANYELEMENT AS $$ DECLARE
--     _class regclass = pg_typeof(_token)::varchar::regclass; 
-- BEGIN 
--     RETURN QUERY EXECUTE format('SELECT %s FROM %s', (SELECT string_agg(CASE 
--             WHEN attname = ANY (_without_columns) 
--                 THEN format('NULL::%s AS %I', atttypid::regtype, attname)
--                 ELSE format('%I', attname)
--             END , ',' ORDER  BY attnum) FROM pg_attribute WHERE attrelid = _class AND attnum > 0 AND NOT attisdropped), _class); 
-- END $$ LANGUAGE PLPGSQL;

-- CREATE OR REPLACE FUNCTION _cm3_utils_select_without_columns(_class regclass, VARIADIC _without_columns varchar[]) RETURNS SETOF RECORD AS $$ BEGIN
--     RETURN QUERY EXECUTE format('SELECT %s FROM %s', (SELECT string_agg(format('%I', attname), ',' ORDER  BY attnum) FROM pg_attribute WHERE attrelid = _class AND attnum > 0 AND NOT attisdropped AND NOT attname = ANY (_without_columns)), _class);
-- END $$ LANGUAGE PLPGSQL;

--- SYSTEM CONFIG UTILS ---

CREATE OR REPLACE FUNCTION _cm3_system_config_get(_key varchar) RETURNS varchar AS $$ BEGIN
    RETURN (SELECT coalesce("Value",'') FROM "_SystemConfig" WHERE "Code" = _key AND "Status" = 'A');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_system_config_get() RETURNS jsonb AS $$ BEGIN
    RETURN (SELECT coalesce(jsonb_object_agg("Code", "Value"), '{}'::jsonb) FROM "_SystemConfig" WHERE "Status" = 'A');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_system_config_set(_key varchar, _value varchar) RETURNS VOID AS $$ BEGIN
    INSERT INTO "_SystemConfig" ("Code", "Value") VALUES (_key, _value) ON CONFLICT ("Code") WHERE "Status" = 'A' DO UPDATE SET "Value" = EXCLUDED."Value";
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_system_config_delete(_key varchar) RETURNS VOID AS $$ BEGIN
    UPDATE "_SystemConfig" SET "Status" = 'N' WHERE "Code" = _key AND "Status" = 'A';
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_email_utils_field_contains_value(_field_value varchar, _email varchar) RETURNS boolean AS $$ DECLARE
    _res boolean;
BEGIN
    _email = regexp_replace(_email, '^ *(.*<)?(.*?)>? *$', '\2');
    IF _field_value IS NULL THEN
        RETURN FALSE;
    ELSE
        SELECT INTO _res EXISTS (WITH q AS (SELECT regexp_replace(x, '^ *(.*<)?(.*?)>? *$', '\2') e FROM regexp_split_to_table(_field_value, ',') x) SELECT * FROM q WHERE e ILIKE _email);
        RETURN _res;
    END IF;
END $$ LANGUAGE PLPGSQL IMMUTABLE;


--- COMMENT UTILS ---

CREATE OR REPLACE FUNCTION _cm3_comment_to_jsonb(_comment varchar) RETURNS jsonb AS $$  DECLARE
	_map jsonb;
	_part varchar[];
BEGIN
	_map = '{}'::jsonb;
	FOR _part IN SELECT regexp_matches(unnest(string_to_array(_comment,'|')),'^ *([^:]+) *: *(.*)$') LOOP
		_map = jsonb_set(_map,ARRAY[(_part[1])],to_jsonb(_part[2]));
	END LOOP;
	RETURN _map;
END $$ LANGUAGE PLPGSQL IMMUTABLE; 

CREATE OR REPLACE FUNCTION _cm3_comment_get_part(_comment varchar,_key varchar) RETURNS varchar AS $$
	SELECT _cm3_comment_to_jsonb(_comment)->>_key;
$$ LANGUAGE SQL IMMUTABLE;
 
CREATE OR REPLACE FUNCTION _cm3_comment_from_jsonb(_map jsonb) RETURNS varchar AS $$ 
	SELECT COALESCE((SELECT string_agg(format('%s: %s',key,value),'|') from jsonb_each_text(_map)), '');
$$ LANGUAGE SQL IMMUTABLE;


--- TRIGGER LIST ---

CREATE OR REPLACE FUNCTION _cm3_trigger_utils_tgargs_to_string_array(_args bytea) RETURNS varchar[] AS $$
    SELECT array(SELECT param FROM(SELECT regexp_split_to_table(encode(_args, 'escape'), E'\\\\000') AS param) AS q WHERE q.param <> '')::varchar[]
$$ LANGUAGE SQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_class_triggers_list_detailed(_class regclass) RETURNS TABLE (trigger_name varchar, trigger_when varchar, trigger_for_each varchar, trigger_function varchar, trigger_params varchar[]) AS $$ BEGIN
	RETURN QUERY SELECT
			t.tgname::varchar AS trigger_name,
			(CASE t.tgtype::int2 & cast(2 as int2) WHEN 0 THEN 'AFTER' ELSE 'BEFORE' END || ' ' || CASE t.tgtype::int2 & cast(28 as int2)
				WHEN 16 THEN 'UPDATE'
				WHEN  8 THEN 'DELETE'
				WHEN  4 THEN 'INSERT'
				WHEN 20 THEN 'INSERT OR UPDATE'
				WHEN 28 THEN 'INSERT OR UPDATE OR DELETE'
				WHEN 24 THEN 'UPDATE OR DELETE'
				WHEN 12 THEN 'INSERT OR DELETE'
			END)::varchar AS trigger_when,	
				(CASE t.tgtype::int2 & cast(1 as int2)
				WHEN 0 THEN 'STATEMENT'
				ELSE 'ROW'
			END)::varchar AS trigger_for_each,
			p.proname::varchar AS trigger_function,
			_cm3_trigger_utils_tgargs_to_string_array(tgargs) AS trigger_params
		FROM pg_trigger t, pg_proc p
		WHERE tgrelid = _class AND t.tgfoid = p.oid AND tgisinternal = false
		ORDER BY t.tgname;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_triggers_list_detailed() RETURNS TABLE (owner regclass, trigger_name varchar, trigger_when varchar, trigger_for_each varchar, trigger_function varchar, trigger_params varchar[]) AS $$ BEGIN
	RETURN QUERY EXECUTE (SELECT string_agg(format('SELECT %L::regclass AS owner,* FROM _cm3_class_triggers_list_detailed(%L)',c.x,c.x),' UNION ALL ')  FROM (SELECT x FROM _cm3_class_list() x UNION SELECT x FROM _cm3_domain_list() x) c);--TODO improve this
END $$ LANGUAGE PLPGSQL;


--- TRIGGER CHECK ---

CREATE OR REPLACE FUNCTION _cm3_class_triggers_are_enabled(_class regclass) RETURNS BOOLEAN AS $$
	SELECT (SELECT DISTINCT tgenabled FROM pg_trigger where tgrelid = _class AND tgisinternal = false) <> 'D'; --TODO check only cm triggers (??)
$$ LANGUAGE SQL;


--- CLASS COMMENT ---

CREATE OR REPLACE FUNCTION _cm3_class_comment_keys() RETURNS SETOF varchar AS $$
	SELECT DISTINCT unnest(ARRAY[
            'LABEL','CLASS1','CLASS2','TYPE','DESCRDIR','DESCRINV','CARDIN','MASTERDETAIL','MDLABEL','MDFILTER','DISABLED1','DISABLED2','INDEX1','INDEX2','ACTIVE','MODE', -- domain
            'DESCR','SUPERCLASS','TYPE','MTMODE','USERSTOPPABLE','WFSTATUSATTR','WFSAVE','ATTACHMENT_TYPE_LOOKUP','ATTACHMENT_DESCRIPTION_MODE','ACTIVE','MODE' -- class
        ]);
$$ LANGUAGE SQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_class_comment_get(_class regclass) RETURNS varchar AS $$
	SELECT description FROM pg_description WHERE objoid = _class AND objsubid = 0;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_comment_get_jsonb(_class regclass) RETURNS jsonb AS $$
	SELECT _cm3_comment_to_jsonb(_cm3_class_comment_get(_class));
$$ LANGUAGE SQL STABLE; 

CREATE OR REPLACE FUNCTION _cm3_class_comment_get(_class regclass, _key varchar) RETURNS varchar AS $$
	SELECT coalesce(_cm3_class_comment_get_jsonb(_class)->>_key, '');
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_comment_set(_class regclass, _comment jsonb) RETURNS void AS $$ DECLARE	 
	_key varchar; 
BEGIN
	FOR _key IN SELECT x FROM jsonb_object_keys(_comment) x WHERE CASE WHEN _cm3_utils_regclass_to_name(_class) LIKE 'Map_%' 
		THEN x NOT IN ('LABEL','CLASS1','CLASS2','TYPE','DESCRDIR','DESCRINV','CARDIN','MASTERDETAIL','MDLABEL','MDFILTER','DISABLED1','DISABLED2','INDEX1','INDEX2','ACTIVE','MODE')
		ELSE x NOT IN ('DESCR','SUPERCLASS','TYPE','MTMODE','USERSTOPPABLE','WFSTATUSATTR','WFSAVE','ATTACHMENT_TYPE_LOOKUP','ATTACHMENT_DESCRIPTION_MODE','ACTIVE','MODE') END
	LOOP
		RAISE WARNING 'CM: invalid comment for class = %: invalid comment key = %', _class, _key;
	END LOOP;
	RAISE NOTICE 'set class comment % = %', _class, _comment;
	EXECUTE format('COMMENT ON TABLE %s IS %L', _class, _cm3_comment_from_jsonb(_comment));
END $$ LANGUAGE PLPGSQL; 

CREATE OR REPLACE FUNCTION _cm3_class_comment_set(_class regclass, _key varchar, _value varchar) RETURNS void AS $$
	SELECT _cm3_class_comment_set(_class, _cm3_class_comment_get_jsonb(_class) || jsonb_build_object(_key, _value));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_class_comment_delete(_class regclass, _key varchar) RETURNS void AS $$
	SELECT _cm3_class_comment_set(_class, _cm3_class_comment_get_jsonb(_class) - _key);
$$ LANGUAGE SQL;


--- CLASS FEATURE ---

CREATE OR REPLACE FUNCTION _cm3_class_features_get(_classe regclass) RETURNS jsonb AS $$ BEGIN
	RETURN _cm3_class_metadata_get(_classe) || _cm3_class_comment_get_jsonb(_classe);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_features_get(_classe regclass, _key varchar) RETURNS varchar AS $$ BEGIN
	RETURN coalesce(_cm3_class_features_get(_classe)->>_key, '');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_features_set(_classe regclass, _features jsonb) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_class_comment_set(_classe, (SELECT coalesce(jsonb_object_agg(key, value), '{}'::jsonb) FROM jsonb_each_text(_features) WHERE key IN (SELECT _cm3_class_comment_keys())));
	PERFORM _cm3_class_metadata_set(_classe, (SELECT coalesce(jsonb_object_agg(key, value), '{}'::jsonb) FROM jsonb_each_text(_features) WHERE key NOT IN (SELECT _cm3_class_comment_keys())));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_features_set(_classe regclass, _key varchar, _value varchar) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_class_features_set(_classe, _cm3_class_features_get(_classe) || jsonb_build_object(_key, _value));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_features_delete(_classe regclass, _key varchar) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_class_features_set(_classe, _cm3_class_features_get(_classe) - _key);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_features_update(_classe regclass, _features jsonb) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_class_features_set(_classe, _cm3_class_features_get(_classe) || _features);
END $$ LANGUAGE PLPGSQL;


--- CLASS METADATA ---

CREATE OR REPLACE FUNCTION _cm3_class_metadata_get(_classe regclass) RETURNS jsonb AS $$ BEGIN
	RETURN COALESCE((SELECT "Metadata" FROM "_ClassMetadata" WHERE "Owner" = _classe AND "Status" = 'A'),'{}'::jsonb);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_metadata_get(_classe regclass,_key varchar) RETURNS varchar AS $$ BEGIN
	RETURN jsonb_extract_path_text(_cm3_class_metadata_get(_classe),_key::text);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_metadata_set(_classe regclass,_metadata jsonb) RETURNS VOID AS $$ BEGIN
	_metadata = _cm3_utils_strip_null_or_empty(_metadata);
	IF EXISTS (SELECT 1 FROM "_ClassMetadata" WHERE "Owner" = _classe AND "Status" = 'A') THEN
		IF _metadata = '{}'::jsonb THEN
			UPDATE "_ClassMetadata" SET "Status" = 'N' WHERE "Owner" = _classe AND "Status" = 'A';
		ELSE
			UPDATE "_ClassMetadata" SET "Metadata" = _metadata WHERE "Owner" = _classe AND "Status" = 'A';
		END IF;
	ELSE
		IF _metadata <> '{}'::jsonb THEN
			INSERT INTO "_ClassMetadata" ("Owner","Metadata") VALUES (_classe,_metadata);
		END IF;
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_metadata_set(_classe regclass, _key varchar, _value varchar) RETURNS VOID AS $$ DECLARE
	_metadata jsonb;
BEGIN
	_metadata = _cm3_class_metadata_get(_classe);
	_metadata = jsonb_set(_metadata,ARRAY[_key::text],to_jsonb(_value));
	PERFORM _cm3_class_metadata_set(_classe,_metadata);
END $$ LANGUAGE PLPGSQL;


--- CLASS MISC ---

CREATE OR REPLACE FUNCTION _cm3_class_parent_get(_classe regclass) RETURNS regclass AS $$ BEGIN
	RETURN COALESCE((SELECT inhparent::regclass FROM pg_inherits WHERE inhrelid = _classe AND _cm3_class_comment_get(inhparent::regclass, 'TYPE') IS NOT NULL LIMIT 1), NULL);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_utils_class_for_card(_superclass regclass, _card_id bigint) RETURNS regclass AS $$ DECLARE
	_res regclass;
BEGIN
	EXECUTE format('SELECT tableoid FROM %s WHERE "Id"= $1 LIMIT 1', _superclass) USING _card_id INTO _res;
	RETURN _res;
END $$ LANGUAGE PLPGSQL STABLE RETURNS NULL ON NULL INPUT;

CREATE OR REPLACE FUNCTION _cm3_class_ancestor_list(_class regclass) RETURNS SETOF regclass AS $$ DECLARE
	_ancestor regclass;
	_parent regclass;
BEGIN
	IF _cm3_utils_regclass_to_name(_class) <> 'Class' THEN
		_parent = _cm3_class_parent_get(_class);
		IF _parent IS NOT NULL THEN
			FOR _ancestor IN SELECT _cm3_class_ancestor_list(_parent) LOOP
				RETURN NEXT _ancestor;
			END LOOP;
			RETURN NEXT _parent;
		END IF;
	END IF;
END $$ LANGUAGE PLPGSQL;


--- CLASS CHECK ---

CREATE OR REPLACE FUNCTION _cm3_class_is_process(_class regclass) RETURNS BOOLEAN AS $$
	SELECT '"Activity"'::regclass IN (SELECT _cm3_class_ancestor_list(_class)) OR '"Activity"'::regclass = _class;
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_class_type_get(_class regclass) RETURNS varchar AS $$
    SELECT CASE _cm3_class_comment_get(_class,'TYPE') 
        WHEN 'domain' THEN 'domain'
        WHEN 'class' THEN 'class'
        WHEN 'simpleclass' THEN 'class'
        WHEN 'function' THEN 'function'
        ELSE 'unknown' END;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_is_simple(_class regclass) RETURNS boolean AS $$
	SELECT _cm3_class_comment_get(_class,'TYPE') = 'simpleclass';
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_is_domain(_class regclass) RETURNS boolean AS $$
	SELECT _cm3_class_comment_get(_class,'TYPE') = 'domain';
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_is_standard(_class regclass) RETURNS boolean AS $$
	SELECT _cm3_class_comment_get(_class,'TYPE') = 'class';
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_is_history(_class regclass) RETURNS boolean AS $$
	SELECT _cm3_utils_regclass_to_name(_class) LIKE '%_history';
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_is_superclass(_class regclass) RETURNS boolean AS $$
	SELECT _cm3_class_comment_get(_class,'SUPERCLASS') = 'true';
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_has_history(_class regclass) RETURNS boolean AS $$
	SELECT ( _cm3_class_is_standard(_class) OR _cm3_class_is_domain(_class) ) AND NOT _cm3_class_is_superclass(_class);
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_is_simple_or_standard(_class regclass) RETURNS boolean AS $$
	SELECT  _cm3_class_comment_get(_class,'TYPE') IN ('class','simpleclass');
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_exists(_class regclass) RETURNS BOOLEAN AS $$
	SELECT NULLIF(_cm3_class_comment_get(_class, 'TYPE'), '') IS NOT NULL;--TODO check this
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_exists(_class_name varchar) RETURNS BOOLEAN AS $$
	SELECT _cm3_utils_name_to_regclass(_class_name) IS NOT NULL;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_exists(_class_name text) RETURNS BOOLEAN AS $$
	SELECT _cm3_class_exists(_class_name::varchar);
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_has_records(_class regclass) RETURNS boolean AS $$ DECLARE
	_has_records boolean;
BEGIN
	EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s)', _class) INTO _has_records;
	RETURN _has_records;
END $$ LANGUAGE PLPGSQL;


--- CARD CHECK ---

CREATE OR REPLACE FUNCTION _cm3_card_exists_with_value(_class regclass,_attr varchar,_value bigint) RETURNS BOOLEAN AS $$ BEGIN
	RETURN _cm3_card_exists_with_value(_class,_attr,_value,FALSE);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_exists_with_value(_class regclass,_attr varchar,_value bigint,_include_deleted boolean) RETURNS BOOLEAN AS $$ DECLARE
	_query varchar;
	_res boolean;
BEGIN
	IF _value IS NULL THEN
		RETURN FALSE;
	ELSE
		_query = 'SELECT EXISTS (SELECT "Id" FROM ' || _class::varchar || ' WHERE ' || quote_ident(_attr) || ' = ' || _value;
		IF _cm3_class_is_standard(_class) AND NOT _include_deleted THEN
			_query = _query || ' AND "Status"=''A''';
		END IF;
		_query = _query || ')';
		EXECUTE _query INTO _res;
		RETURN _res; 
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_exists_with_id(_class regclass,_card_id bigint,_include_deleted boolean) RETURNS BOOLEAN AS $$
	SELECT _cm3_card_exists_with_value(_class, 'Id', _card_id, _include_deleted);
$$ LANGUAGE SQL STABLE;


--- CLASS LIST ---

CREATE OR REPLACE FUNCTION _cm3_class_list() RETURNS SETOF regclass AS $$
	SELECT oid::regclass FROM pg_class WHERE _cm3_class_is_simple_or_standard(oid) AND relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public') AND oid::regclass <> '"SimpleClass"'::regclass;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_list_simple() RETURNS SETOF regclass AS $$
	SELECT oid::regclass FROM pg_class WHERE _cm3_class_is_simple(oid) AND relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public') AND oid::regclass <> '"SimpleClass"'::regclass;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_list_standard() RETURNS SETOF regclass AS $$
	SELECT oid::regclass FROM pg_class WHERE _cm3_class_is_standard(oid) AND relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public');
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_list_descendants_and_self(_class regclass) RETURNS SETOF regclass AS $$
	SELECT _class
		UNION
	SELECT i.inhrelid::regclass FROM pg_catalog.pg_inherits i WHERE i.inhparent = _class
		UNION
	SELECT _cm3_class_list_descendants_and_self(i.inhrelid) FROM pg_catalog.pg_inherits i WHERE i.inhparent = _class;
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_class_list_descendant_classes_and_self(_class regclass) RETURNS SETOF regclass AS $$
	SELECT c FROM _cm3_class_list_descendants_and_self(_class) c WHERE _cm3_class_is_standard(c);
$$ LANGUAGE SQL; 

CREATE OR REPLACE FUNCTION _cm3_class_list_descendant_classes(_class regclass) RETURNS SETOF regclass AS $$
	SELECT c FROM _cm3_class_list_descendant_classes_and_self(_class) c WHERE c <> _class;
$$ LANGUAGE SQL; 

CREATE OR REPLACE FUNCTION _cm3_class_list_descendant_classes_and_self_not_superclass(_class regclass) RETURNS SETOF regclass AS $$
	SELECT c FROM _cm3_class_list_descendant_classes_and_self(_class) c WHERE NOT _cm3_class_is_superclass(c);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_class_has_descendants(_class regclass) RETURNS boolean AS $$
	SELECT EXISTS(SELECT c FROM _cm3_class_list_descendant_classes_and_self(_class) c WHERE c <> _class);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_process_list() RETURNS SETOF regclass AS $$
	SELECT _cm3_class_list_descendants_and_self('"Activity"'::regclass);
$$ LANGUAGE SQL STABLE; 

CREATE OR REPLACE FUNCTION _cm3_class_list_detailed() RETURNS TABLE (table_id int, table_name varchar, features jsonb, is_process boolean, parent_id int, parent_name varchar, ancestor_ids int[], ancestor_names varchar[]) AS $$
	WITH _classes AS (SELECT 
                c, 
                _cm3_class_parent_get(c) AS p, 
                _cm3_class_features_get(c) AS features,
                (SELECT array_agg(a) FROM _cm3_class_ancestor_list(c) a) ancestors
             FROM _cm3_class_list() c) 
        SELECT 
            c::oid::int, 
            _cm3_utils_regclass_to_name(c),
            features,
            (SELECT '"Activity"'::regclass = ANY (ancestors) OR '"Activity"'::regclass = c),
            p::oid::int, 
            _cm3_utils_regclass_to_name(p),
            (SELECT array_agg(a::oid::int) FROM unnest(ancestors) a),
            (SELECT array_agg(_cm3_utils_regclass_to_name(a)) FROM unnest(ancestors) a)
        FROM _classes;
$$ LANGUAGE SQL STABLE;


--- DOMAIN LIST ---

CREATE OR REPLACE FUNCTION _cm3_domain_list() RETURNS SETOF regclass AS $$
	SELECT oid::regclass FROM pg_class WHERE _cm3_class_is_domain(oid) AND relnamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public') AND oid <> '"Map"'::regclass;
$$ LANGUAGE SQL STABLE;


--- ATTRIBUTE COMMENT ---

CREATE OR REPLACE FUNCTION _cm3_attribute_comment_keys() RETURNS SETOF varchar AS $$
	SELECT unnest(ARRAY['DESCR','BASEDSP','CLASSORDER','EDITORTYPE','GROUP','INDEX','LOOKUP','REFERENCEDIR','REFERENCEDOM','FKTARGETCLASS','FILTER','IP_TYPE','ACTIVE','MODE','DOMAINKEY']);
$$ LANGUAGE SQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_comment_get_raw(_class regclass, _attr varchar) RETURNS varchar AS $$
	SELECT description FROM pg_description
		JOIN pg_attribute ON pg_description.objoid = pg_attribute.attrelid AND pg_description.objsubid = pg_attribute.attnum
		WHERE attrelid = _class and attname = _attr;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_comment_get(_class regclass, _attr varchar) RETURNS jsonb AS $$
	SELECT _cm3_comment_to_jsonb(_cm3_attribute_comment_get_raw(_class, _attr));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_comment_set(_class regclass, _attr varchar, _comment jsonb) RETURNS void AS $$ DECLARE	
	_ref_attr int; 
	_key varchar; 
BEGIN
	_comment = _cm3_utils_strip_null_or_empty(_comment);
	FOR _key IN SELECT x FROM jsonb_object_keys(_comment) x WHERE x NOT IN (SELECT _cm3_attribute_comment_keys()) LOOP
		RAISE WARNING 'CM: invalid comment for class = % attr = %: invalid comment key = %', _class, _attr, _key;
	END LOOP;
	_ref_attr = (SELECT count(x) FROM jsonb_object_keys(_comment) x WHERE x IN ('REFERENCEDOM','LOOKUP','FKTARGETCLASS') AND NULLIF(_comment->>x, '') IS NOT NULL );
	IF _ref_attr > 1 THEN
		RAISE EXCEPTION 'CM: invalid comment for class = % attr = %, comment = %: attribute type error (too many attribute type specified)', _class, _attr, _comment;
	END IF;
	RAISE NOTICE 'update class attribute comment %.% = %', _class, _attr, _comment;
	EXECUTE format('COMMENT ON COLUMN %s.%I IS %L', _class, _attr, _cm3_comment_from_jsonb(_comment));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_comment_update(_class regclass, _attr varchar, _comment jsonb) RETURNS void AS $$ BEGIN	
	PERFORM _cm3_attribute_comment_set(_class, _attr, _cm3_attribute_comment_get(_class, _attr) || _comment);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_comment_get(_class regclass, _attr varchar, _part varchar) RETURNS varchar AS $$
	SELECT coalesce(_cm3_attribute_comment_get(_class,_attr)->>_part,'');
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_comment_set(_class regclass, _attr varchar, _key varchar, _value varchar) RETURNS void AS $$
	SELECT _cm3_attribute_comment_update(_class, _attr, jsonb_build_object(_key, _value));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_comment_delete(_class regclass, _attr varchar, _key varchar) RETURNS void AS $$
	SELECT _cm3_attribute_comment_set(_class, _attr, _cm3_attribute_comment_get(_class, _attr) - _key);
$$ LANGUAGE SQL;


--- ATTRIBUTE METADATA ---

CREATE OR REPLACE FUNCTION _cm3_attribute_metadata_get(_classe regclass, _attr varchar) RETURNS jsonb AS $$ BEGIN
	RETURN COALESCE((SELECT "Metadata" FROM "_AttributeMetadata" WHERE "Owner" = _classe AND "Code" = _attr AND "Status" = 'A'),'{}'::jsonb);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_metadata_set(_class regclass, _attr varchar, _metadata jsonb) RETURNS void AS $$ BEGIN
	_metadata = _cm3_utils_strip_null_or_empty(_metadata);
	IF EXISTS (SELECT * FROM "_AttributeMetadata" WHERE "Owner" = _class AND "Code" = _attr AND "Status" = 'A') THEN
		IF _metadata = '{}'::jsonb THEN
			UPDATE "_AttributeMetadata" SET "Status" = 'N' WHERE "Owner" = _class AND "Code" = _attr AND "Status" = 'A';
		ELSE
			UPDATE "_AttributeMetadata" SET "Metadata" = _metadata WHERE "Owner" = _class AND "Code" = _attr AND "Status" = 'A';
		END IF;
	ELSE
		IF _metadata <> '{}'::jsonb THEN
			INSERT INTO "_AttributeMetadata" ("Owner","Code","Status","Metadata") VALUES (_class,_attr,'A',_metadata);
		END IF;
	END IF;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION _cm3_attribute_metadata_set(_class regclass, _attr varchar, _key varchar, _value varchar) RETURNS void AS $$
	SELECT _cm3_attribute_metadata_set(_class, _attr, _cm3_attribute_metadata_get(_class, _attr) || jsonb_build_object(_key, _value));
$$ LANGUAGE SQL;


--- ATTRIBUTE FEATURES ---

CREATE OR REPLACE FUNCTION _cm3_attribute_features_get(_classe regclass, _attr varchar) RETURNS jsonb AS $$ BEGIN
	RETURN _cm3_attribute_metadata_get(_classe, _attr) || _cm3_attribute_comment_get(_classe, _attr);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_features_get(_classe regclass, _attr varchar, _key varchar) RETURNS varchar AS $$ BEGIN
	RETURN coalesce(_cm3_attribute_features_get(_classe, _attr)->>_key, '');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_features_set(_classe regclass, _attr varchar, _features jsonb) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_attribute_comment_set(_classe, _attr, (SELECT coalesce(jsonb_object_agg(key, value), '{}'::jsonb) FROM jsonb_each_text(_features) WHERE key IN (SELECT _cm3_attribute_comment_keys())));
	PERFORM _cm3_attribute_metadata_set(_classe, _attr, (SELECT coalesce(jsonb_object_agg(key, value), '{}'::jsonb) FROM jsonb_each_text(_features) WHERE key NOT IN (SELECT _cm3_attribute_comment_keys())));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_features_set(_classe regclass, _attr varchar, _key varchar, _value varchar) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_attribute_features_set(_classe, _attr, _cm3_attribute_features_get(_classe, _attr) || jsonb_build_object(_key, _value));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_features_delete(_classe regclass, _attr varchar, _key varchar) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_attribute_features_set(_classe, _attr, _cm3_attribute_features_get(_classe, _attr) - _key);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_features_update(_classe regclass, _attr varchar, _features jsonb) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_attribute_features_set(_classe, _attr, _cm3_attribute_features_get(_classe, _attr) || _features);
END $$ LANGUAGE PLPGSQL;


--- ATTRIBUTE CHECK ---
 
CREATE OR REPLACE FUNCTION _cm3_attribute_unique_get(_class regclass, _attr varchar) RETURNS boolean AS $$ BEGIN
    RETURN EXISTS (SELECT * FROM pg_attribute a JOIN pg_index i ON a.attrelid = i.indrelid AND a.attnum = i.indkey[0] WHERE i.indnatts = 1 AND i.indisunique AND a.attrelid = _class AND a.attname = _attr);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_notnull_get(_class regclass, _attr varchar) RETURNS boolean AS $$
	SELECT pg_attribute.attnotnull OR c.oid IS NOT NULL
	FROM pg_attribute
	LEFT JOIN pg_constraint AS c ON c.conrelid = pg_attribute.attrelid AND c.conname::text = format('_cm3_%s_notnull', pg_attribute.attname)
	WHERE pg_attribute.attrelid = _class AND pg_attribute.attname = _attr;
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_is_inherited(_class regclass, _attr varchar) RETURNS boolean AS $$
	SELECT pg_attribute.attinhcount <> 0 FROM pg_attribute WHERE pg_attribute.attrelid = _class AND pg_attribute.attname = _attr;
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_has_data(_class regclass, _attr varchar) RETURNS boolean AS $$ DECLARE
	_has_data boolean;
BEGIN
	IF _cm3_class_is_simple(_class) THEN
		EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE %I IS NOT NULL AND %I::text <> '''')', _class, _attr, _attr) INTO _has_data;
	ELSE
		EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE %I IS NOT NULL AND %I::text <> '''' AND "Status" = ''A'')', _class, _attr, _attr) INTO _has_data;
	END IF;
	RETURN _has_data;
END $$ LANGUAGE PLPGSQL; 

CREATE OR REPLACE FUNCTION _cm3_attribute_has_value(_class regclass, _attr varchar, _value bigint) RETURNS boolean AS $$
DECLARE
	_has_value boolean;
BEGIN
	IF _cm3_class_is_simple(_class) THEN
		EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE %I = %L)', _class, _attr, _value) INTO _has_value;
	ELSE
		EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE %I = %L AND "Status" = ''A'')', _class, _attr, _value) INTO _has_value;
	END IF;
	RETURN _has_value;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_sqltype_get(_class regclass, _attr varchar) RETURNS varchar AS $$
	SELECT _cm3_utils_build_sqltype_string(pg_attribute.atttypid, pg_attribute.atttypmod) FROM pg_attribute WHERE pg_attribute.attrelid = _class AND pg_attribute.attname = _attr;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_default_get(_class regclass, _attr varchar) RETURNS varchar AS $$
	SELECT pg_attrdef.adsrc FROM pg_attribute JOIN pg_attrdef ON pg_attrdef.adrelid = pg_attribute.attrelid AND pg_attrdef.adnum = pg_attribute.attnum WHERE pg_attribute.attrelid = _class AND pg_attribute.attname = _attr;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_is_reference(_class regclass, _attr varchar) RETURNS BOOLEAN AS $$ 
	SELECT _cm3_attribute_comment_get(_class, _attr, 'REFERENCEDOM') <> '';
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_reference_domain_get(_class regclass, _attr varchar) RETURNS regclass AS $$ 
	SELECT _cm3_utils_name_to_regclass('Map_' || _cm3_attribute_comment_get(_class, _attr, 'REFERENCEDOM'))
$$ LANGUAGE SQL; 

CREATE OR REPLACE FUNCTION _cm3_attribute_reference_direction_get(_class regclass, _attr varchar) RETURNS varchar AS $$ DECLARE
	_direction varchar;
BEGIN
	_direction = _cm3_attribute_comment_get(_class, _attr, 'REFERENCEDIR');
	IF NOT _direction IN ('direct','inverse') THEN
		RAISE EXCEPTION 'invalid reference dir for class = % attr = % value = %', _class, _attr, _direction;
	END IF;
	RETURN _direction;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_direction_inverse(_direction varchar) RETURNS varchar AS $$ BEGIN
    RETURN CASE _direction WHEN 'direct' THEN 'inverse' ELSE 'direct' END;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_reference_is_direct(_class regclass, _attr varchar) RETURNS boolean AS $$ BEGIN
	RETURN _cm3_attribute_reference_direction_get(_class, _attr) = 'direct';
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_is_lookup(_class regclass, _attr varchar) RETURNS BOOLEAN AS $$ 
	SELECT _cm3_attribute_comment_get(_class, _attr, 'LOOKUP') <> '';
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_is_foreignkey(_class regclass, _attr varchar) RETURNS BOOLEAN AS $$ 
	SELECT _cm3_attribute_comment_get(_class, _attr, 'FKTARGETCLASS') <> '';
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_foreignkey_target_get(_class regclass, _attr varchar) RETURNS regclass AS $$ 
	SELECT _cm3_utils_name_to_regclass(_cm3_attribute_comment_get(_class, _attr, 'FKTARGETCLASS'))
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_reference_target_class_get(_class regclass, _attr varchar) RETURNS regclass AS $$ BEGIN
	RETURN _cm3_domain_target_class_get(_cm3_attribute_reference_domain_get(_class, _attr), _cm3_attribute_reference_is_direct(_class, _attr));
END $$ LANGUAGE PLPGSQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_foreignkey_or_reference_target_class_get(_class regclass, _attr varchar) RETURNS regclass AS $$ BEGIN
	IF _cm3_attribute_is_foreignkey(_class, _attr) THEN
		RETURN _cm3_attribute_foreignkey_target_get(_class, _attr);
	ELSEIF _cm3_attribute_is_reference(_class, _attr) THEN
		RETURN  _cm3_attribute_reference_target_class_get(_class, _attr);
	ELSE
		RAISE EXCEPTION 'attribute is not reference nor foreignkey for class = % attr = %', _class, _attr;
	END IF;
END $$ LANGUAGE PLPGSQL STABLE;


--- ATTRIBUTE LIST ---

CREATE OR REPLACE FUNCTION _cm3_attribute_exists(_class regclass, _attr varchar) RETURNS boolean AS $$
	SELECT EXISTS (SELECT * FROM pg_attribute WHERE attrelid = _class AND attnum > 0 AND atttypid > 0 AND attname::varchar = _attr);
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_list(_class regclass) RETURNS SETOF varchar AS $$
	SELECT attname::varchar FROM pg_attribute WHERE attrelid = _class AND attnum > 0 AND atttypid > 0 ORDER BY attnum;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_list() RETURNS TABLE(owner regclass, name varchar) AS $$ BEGIN
	RETURN QUERY EXECUTE (SELECT string_agg(format('SELECT %L::regclass,* FROM _cm3_attribute_list(%L) x',c.x,c.x),' UNION ALL ')  FROM (SELECT x FROM _cm3_class_list() x UNION SELECT x FROM _cm3_domain_list() x) c); --TODO improve this query, rewrite wihtout string_agg/format
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_list_detailed(_class regclass) RETURNS TABLE(
	owner regclass, 
	name varchar, 
	comment jsonb, 
	not_null_constraint boolean, 
	unique_constraint boolean,
	sql_type varchar,
	inherited boolean,
	default_value varchar,
	metadata jsonb
) AS $$ BEGIN
	RETURN QUERY WITH q AS (SELECT
		_class::regclass AS owner,
		attr_name::varchar AS name,
		_cm3_attribute_comment_get(_class, attr_name) AS comment,
		_cm3_attribute_notnull_get(_class, attr_name) AS not_null_constraint, 
		_cm3_attribute_unique_get(_class, attr_name) AS unique_constraint, 
		_cm3_attribute_sqltype_get(_class, attr_name)::varchar AS sql_type, 
		_cm3_attribute_is_inherited(_class, attr_name) AS inherited, 
		_cm3_attribute_default_get(_class, attr_name)::varchar AS default_value, 
		_cm3_attribute_metadata_get(_class, attr_name) AS metadata,
		COALESCE(NULLIF(_cm3_attribute_comment_get(_class, attr_name, 'INDEX'), ''), '2147483647')::int AS _index
	FROM (SELECT attr_name FROM _cm3_attribute_list(_class) attr_name) x
	) SELECT q.owner,q.name,q.comment,q.not_null_constraint,q.unique_constraint,q.sql_type,q.inherited,q.default_value,q.metadata FROM q ORDER BY _index;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_list_detailed() RETURNS TABLE(
	owner regclass, 
	name varchar, 
	comment jsonb, 
	not_null_constraint boolean, 
	unique_constraint boolean,
	sql_type varchar,
	inherited boolean,
	default_value varchar,
	metadata jsonb
) AS $$ BEGIN
	RETURN QUERY EXECUTE (SELECT string_agg(format('SELECT * FROM _cm3_attribute_list_detailed(%L)',c.x),' UNION ALL ')  FROM (SELECT x FROM _cm3_class_list() x UNION SELECT x FROM _cm3_domain_list() x) c); --TODO improve this query, rewrite wihtout string_agg/format
END $$ LANGUAGE PLPGSQL;


--- DOMAIN CHECK ---

CREATE OR REPLACE FUNCTION _cm3_domain_target_class_get(_domain regclass, _direct boolean) RETURNS regclass AS $$ BEGIN
	RETURN CASE
		WHEN _direct 
		THEN _cm3_utils_name_to_regclass(_cm3_class_comment_get(_domain, 'CLASS2')) 
		ELSE _cm3_utils_name_to_regclass(_cm3_class_comment_get(_domain, 'CLASS1')) 
	END;
END $$ LANGUAGE PLPGSQL STABLE;


--- CARD MISC ---

CREATE OR REPLACE FUNCTION _cm3_card_description_get(_class regclass, _card_id bigint) RETURNS varchar AS $$ DECLARE
	_description VARCHAR;
	_ignore_tenant_policies VARCHAR;
BEGIN
	IF _card_id IS NULL THEN
		RETURN NULL;
	ELSE
		_ignore_tenant_policies = current_setting('cmdbuild.ignore_tenant_policies');
		SET SESSION cmdbuild.ignore_tenant_policies = 'true';
		IF _cm3_class_is_simple(_class) THEN
			EXECUTE format('SELECT "Description" FROM %s WHERE "Id" = %L', _class, _card_id) INTO _description;
		ELSE
			EXECUTE format('SELECT "Description" FROM %s WHERE "Id" = %L AND "Status" = ''A''', _class, _card_id) INTO _description;
		END IF;
		SET SESSION cmdbuild.ignore_tenant_policies = _ignore_tenant_policies;
		RETURN _description;
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_list_at_date(_any anyelement, _date date) RETURNS setof anyelement AS $$ BEGIN
    RETURN QUERY EXECUTE format('SELECT * FROM %s WHERE "BeginDate" < (%L::date+1)::timestamptz AND ("Status" = ''A'' OR ("Status" = ''U'' AND "EndDate" >= (%L::date+1)::timestamptz))', pg_typeof(_any)::varchar::regclass, _date, _date);
END $$ LANGUAGE PLPGSQL;


--- CARD METADATA ---

CREATE OR REPLACE FUNCTION _cm3_card_metadata_set(_classe regclass,_card bigint,_metadata jsonb) RETURNS VOID AS $$ BEGIN
	IF NOT EXISTS (SELECT * FROM "_CardMetadata" WHERE "OwnerClass" = _classe AND "OwnerCard" = _card AND "Status" = 'A') THEN
		INSERT INTO "_CardMetadata" ("OwnerClass","OwnerCard","Data") VALUES (_classe,_card,_metadata);
	ELSE
		UPDATE "_CardMetadata" SET "Data" = _metadata WHERE "OwnerClass" = _classe AND "OwnerCard" = _card AND "Status" = 'A';
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_metadata_get(_classe regclass,_card bigint) RETURNS jsonb AS $$ BEGIN
	RETURN COALESCE((SELECT "Data" FROM "_CardMetadata" WHERE "OwnerClass" = _classe AND "OwnerCard" = _card AND "Status" = 'A'),'{}'::jsonb);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_metadata_set(_classe regclass, _card bigint, _key varchar, _value varchar) RETURNS VOID AS $$ 
	SELECT _cm3_card_metadata_set( _classe, _card, _cm3_card_metadata_get(_classe, _card) || jsonb_build_object(_key, _value));
$$ LANGUAGE SQL;


--- FUNCTION COMMENT ---

CREATE OR REPLACE FUNCTION _cm3_function_comment_get(_function oid) RETURNS varchar AS $$
	SELECT description FROM pg_description WHERE objoid = _function;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_function_comment_get_jsonb(_function oid) RETURNS jsonb AS $$
	SELECT _cm3_comment_to_jsonb(_cm3_function_comment_get(_function));
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_function_comment_get(_function oid, _key varchar) RETURNS varchar AS $$
	SELECT _cm3_function_comment_get_jsonb(_function)->>_key;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_function_definition_get(_fun oid) RETURNS varchar AS $$
	SELECT format('%I(%s)', (SELECT proname FROM pg_proc WHERE oid = _fun), pg_get_function_identity_arguments(_fun));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_function_comment_set(_fun oid, _comment jsonb) RETURNS void AS $$ DECLARE	
	_ref_attr int; 
	_key varchar; 
BEGIN
	FOR _key IN SELECT x FROM jsonb_object_keys(_comment) x 
		WHERE x NOT IN ('CATEGORIES','MASTERTABLE','TAGS','ACTIVE','MODE','TYPE')
	LOOP
		RAISE WARNING 'CM: invalid comment for function = %: invalid comment key = %', _fun, _key;
	END LOOP;
	EXECUTE format('COMMENT ON FUNCTION %s IS %L', _cm3_function_definition_get(_fun), _cm3_comment_from_jsonb(_comment));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_function_comment_set(_fun oid, _key varchar, _value varchar) RETURNS void AS $$
	SELECT _cm3_function_comment_set(_fun, _cm3_function_comment_get_jsonb(_fun) || jsonb_build_object(_key, _value));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_function_comment_delete(_fun oid, _key varchar) RETURNS void AS $$
	SELECT _cm3_function_comment_set(_fun, _cm3_function_comment_get_jsonb(_fun) - _key);
$$ LANGUAGE SQL;
 

--- FUNCTION LIST ---

CREATE OR REPLACE FUNCTION _cm3_function_list() RETURNS SETOF oid AS $$
	SELECT oid FROM pg_proc WHERE LOWER(_cm3_function_comment_get_jsonb(oid)->>'TYPE') = 'function' AND pronamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public');
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_function_list_detailed() RETURNS TABLE(
		function_name varchar,
		function_id oid,
		arg_io char[],
		arg_names varchar[],
		arg_types varchar[],
		returns_set boolean,
		comment jsonb,
		metadata jsonb
	) AS $$ DECLARE
	_arg_io char[];
	_arg_names varchar[];
	_arg_types varchar[];
	_record record;
	_index integer;
BEGIN
	FOR _record IN SELECT *,oid FROM pg_proc WHERE oid IN (SELECT _cm3_function_list()) LOOP
		IF _record.proargmodes IS NULL THEN
			_arg_io = '{}'::char[];
			_arg_types = '{}'::varchar[];
			_arg_names = '{}'::varchar[];
			FOR _index IN SELECT generate_series(1, array_upper(_record.proargtypes,1)) LOOP
				_arg_io = _arg_io || 'i'::char;
				_arg_types = _arg_types || _cm3_utils_build_sqltype_string(_record.proargtypes[_index], NULL);
				_arg_names = _arg_names || COALESCE(_record.proargnames[_index]::varchar,('$'||_index)::varchar);
			END LOOP;
			_arg_io = _arg_io || 'o'::char;
			_arg_types = _arg_types || _cm3_utils_build_sqltype_string(_record.prorettype, NULL);
			_arg_names = _arg_names || _record.proname::varchar;
		ELSE
			_arg_io = _record.proargmodes;
			_arg_types = '{}'::varchar[];
			_arg_names = _record.proargnames;
			FOR _index IN SELECT generate_series(1, array_upper(_arg_io,1)) LOOP
				IF _arg_io[_index] = 't' THEN
					_arg_io[_index] = 'o';
				ELSEIF _arg_io[_index] = 'b' THEN
					_arg_io[_index] = 'io';
				ELSEIF _arg_io[_index] NOT IN ('i','o') THEN
					RAISE 'unsupported arg io value = % for function = %', _arg_io[_index], _record.proname;
				END IF;
				_arg_types = _arg_types || _cm3_utils_build_sqltype_string(_record.proallargtypes[_index], NULL);
				IF _arg_names[_index] = '' THEN
					IF _arg_io[_index] = 'i' THEN
						_arg_names[_index] = '$'||_index;
					ELSE
						_arg_names[_index] = 'column'||_index;
					END IF;
				END IF;
			END LOOP;
		END IF;
		RETURN QUERY SELECT
			_record.proname::varchar,
			_record.oid,
			_arg_io,
			_arg_names,
			_arg_types,
			_record.proretset,
			_cm3_function_comment_get_jsonb(_record.oid),
			_cm3_function_metadata_get(_record.oid);
	END LOOP;
END $$ LANGUAGE PLPGSQL STABLE;


--- FUNCTION METADATA ---

CREATE OR REPLACE FUNCTION _cm3_function_metadata_get(_function oid) RETURNS jsonb AS $$ BEGIN
	RETURN COALESCE((SELECT "Data" FROM "_FunctionMetadata" WHERE "OwnerFunction" = _function AND "Status" = 'A'),'{}'::jsonb);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_function_metadata_get(_function oid,_key varchar) RETURNS varchar AS $$ BEGIN
	RETURN jsonb_extract_path_text(_cm3_class_metadata_get(_function),_key::text);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_function_metadata_set(_function oid,_metadata jsonb) RETURNS VOID AS $$ BEGIN
	IF EXISTS (SELECT 1 FROM "_FunctionMetadata" WHERE "Owner" = _function AND "Status" = 'A') THEN
		UPDATE "_FunctionMetadata" SET "Data" = _metadata WHERE "OwnerFunction" = _function AND "Status" = 'A';
	ELSE
		INSERT INTO "_FunctionMetadata" ("OwnerFunction","Data") VALUES (_function,_metadata);
	END IF;
END $$ LANGUAGE PLPGSQL;


--- USER CONFIG ---

CREATE OR REPLACE FUNCTION _cm3_user_config_get(_userid bigint) RETURNS jsonb AS $$ BEGIN
	RETURN COALESCE((SELECT "Data" FROM "_UserConfig" WHERE "Owner" = _userid AND "Status" = 'A'),'{}'::jsonb);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_user_config_set(_userid bigint,_data jsonb) RETURNS VOID AS $$ BEGIN
 	IF EXISTS (SELECT 1 FROM "_UserConfig" WHERE "Owner" = _userid AND "Status" = 'A') THEN	
		UPDATE "_UserConfig" SET "Data" = _data WHERE "Owner" = _userid AND "Status" = 'A';
	ELSE
		INSERT INTO "_UserConfig" ("Owner","Data") VALUES (_userid,_data);
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_user_config_get(_username varchar) RETURNS jsonb AS $$ BEGIN
	RETURN (SELECT _cm3_user_config_get((SELECT "Id" FROM "User" WHERE "Username" = _username AND "Status" = 'A')));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_user_config_set(_username varchar, _data jsonb) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_user_config_set((SELECT "Id" FROM "User" WHERE "Username" = _username AND "Status" = 'A'),_data);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_user_config_set(_username varchar, _key varchar, _value varchar) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_user_config_set(_username, _cm3_user_config_get(_username) || jsonb_build_object(_key, _value));
END $$ LANGUAGE PLPGSQL;


-- UTILITY PATCH FUNCTIONS

CREATE OR REPLACE FUNCTION _cm3_utils_store_and_drop_dependant_views(_table regclass) RETURNS VOID AS $$ DECLARE
	_view regclass;
	_sub_table regclass;
BEGIN
    IF to_regclass('_utils_dependant_views_aux') IS NULL THEN
        CREATE TEMPORARY TABLE _utils_dependant_views_aux(index int, viewname varchar, viewdef varchar);
    END IF;
	FOR _view IN
		select distinct(r.ev_class::regclass) as views
			from pg_depend d join pg_rewrite r on r.oid = d.objid 
			where refclassid = 'pg_class'::regclass
				and refobjid = _table
				and classid = 'pg_rewrite'::regclass 
				and ev_class != _table
	LOOP
		PERFORM _cm3_utils_store_and_drop_dependant_views(_view);
		IF _view::varchar <> _view::int::varchar THEN --TODO check this
            INSERT INTO _utils_dependant_views_aux (index, viewname, viewdef) VALUES ( (SELECT COALESCE(MAX(index)+1,0) FROM _utils_dependant_views_aux), _view::varchar, pg_get_viewdef(_view, true) );
			RAISE NOTICE 'store and drop view %', _view;
			EXECUTE format('DROP VIEW %s', _view);
		END IF;
	END LOOP;
	FOR _sub_table IN SELECT i.inhrelid::regclass FROM pg_catalog.pg_inherits i WHERE i.inhparent = _table LOOP
		PERFORM _cm3_utils_store_and_drop_dependant_views(_sub_table);
	END LOOP;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_restore_dependant_views() RETURNS VOID AS $$ DECLARE
    _record record;
BEGIN
    FOR _record IN SELECT * FROM _utils_dependant_views_aux ORDER BY index DESC LOOP
        RAISE NOTICE 'restore view %', _record.viewname;
        EXECUTE format('CREATE VIEW %s AS %s', _record.viewname, _record.viewdef);
    END LOOP;
    DROP TABLE _utils_dependant_views_aux;
END $$ LANGUAGE PLPGSQL;


-- UTILITY FUNCTIONS

CREATE OR REPLACE FUNCTION _cm3_system_housekeeping() RETURNS VOID AS $$ DECLARE
	_record RECORD;
    _invalid_record RECORD;
BEGIN
	PERFORM _cm3_system_lock_aquire('housekeeping');
	FOR _record IN SELECT * FROM "_Grant" WHERE "Status" = 'A' AND (( 
				"Type" = 'Class' AND NOT _cm3_class_exists("ObjectClass") 
			) OR (
				"Type" = 'Report' AND NOT EXISTS (SELECT * FROM "_Report" WHERE "Id" = "_Grant"."ObjectId" AND "Status" = 'A')
			))
	LOOP
		RAISE WARNING 'removing invalid grant record = %', _record;
		UPDATE "_Grant" SET "Status" = 'N' WHERE "Id" = _record."Id" AND "Status" = 'A';
	END LOOP;
    FOR _record IN SELECT owner class_name, name attribute_name, comment->>'LOOKUP' _lookupType FROM _cm3_attribute_list_detailed() AS  x WHERE NULLIF(comment->>'LOOKUP','') IS NOT NULL ORDER BY owner, name LOOP
        FOR _invalid_record IN EXECUTE format('SELECT "Id" id, %I attributeId FROM %s WHERE %I IS NOT NULL AND %I NOT IN (SELECT "Id" FROM "LookUp" WHERE "Type" = %L AND "Status" = ''A'')' || CASE WHEN _cm3_class_is_simple(_record.class_name) THEN '' ELSE ' AND "Status" = ''A''' END,
                _record.attribute_name, _record.class_name::regclass, _record.attribute_name, _record.attribute_name, _record._lookupType)  
		LOOP
			RAISE WARNING 'found invalid LookUp value for attr = %.% card = %, invalid value = %: will set value to NULL', _cm3_utils_regclass_to_name(_record.class_name), _record.attribute_name, _invalid_record.id, _invalid_record.attributeId;
			BEGIN
				EXECUTE format('UPDATE %s SET "%s" = NULL WHERE "Id" = %s', _record.class_name, _record.attribute_name, _invalid_record.id);
			EXCEPTION WHEN others THEN
				RAISE WARNING 'unable to clear invalid value for attr = %.% card = %: %', _cm3_utils_regclass_to_name(_record.class_name), _record.attribute_name, _invalid_record.id, SQLERRM;
			END;
		END LOOP;
    END LOOP;  
	FOR _record IN SELECT * FROM "Class" WHERE "Id" IN (WITH _records AS (SELECT "Id",COUNT(*) "Count" FROM (SELECT "Id" FROM "Class" UNION ALL SELECT "Id" FROM "Map" UNION ALL SELECT "Id" FROM "SimpleClass") x GROUP BY "Id") SELECT "Id" FROM _records WHERE "Count" > 1) ORDER BY "Id","IdClass"::varchar LOOP 
		RAISE WARNING 'found duplicate card id = % for record = %', _record."Id", _record; 
	END LOOP;
	FOR _record IN SELECT * FROM "Map" WHERE "Id" IN (WITH _records AS (SELECT "Id",COUNT(*) "Count" FROM (SELECT "Id" FROM "Class" UNION ALL SELECT "Id" FROM "Map" UNION ALL SELECT "Id" FROM "SimpleClass") x GROUP BY "Id") SELECT "Id" FROM _records WHERE "Count" > 1) ORDER BY "Id","IdDomain"::varchar LOOP 
		RAISE WARNING 'found duplicate map id = % for record = %', _record."Id", _record; 
	END LOOP;
	FOR _record IN SELECT * FROM "SimpleClass" WHERE "Id" IN (WITH _records AS (SELECT "Id",COUNT(*) "Count" FROM (SELECT "Id" FROM "Class" UNION ALL SELECT "Id" FROM "Map" UNION ALL SELECT "Id" FROM "SimpleClass") x GROUP BY "Id") SELECT "Id" FROM _records WHERE "Count" > 1) ORDER BY "Id","IdClass"::varchar LOOP 
		RAISE WARNING 'found duplicate simple class id = % for record = %', _record."Id", _record; 
	END LOOP;	
	PERFORM _cm3_system_lock_release('housekeeping');
END $$ LANGUAGE PLPGSQL;
