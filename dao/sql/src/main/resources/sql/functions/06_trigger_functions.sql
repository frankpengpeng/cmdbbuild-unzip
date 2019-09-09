-- trigger functions
-- REQUIRE PATCH 3.0.0-03a_system_functions

--- TRIGGER HELPERS ---

CREATE OR REPLACE FUNCTION _cm3_trigger_fail_if_reference_value_exists(_class regclass, _attr varchar, _value bigint) RETURNS VOID AS $$ BEGIN
	IF _cm3_attribute_has_value(_class, _attr, _value) THEN
		RAISE EXCEPTION 'CM: cannot complete operation: found reference card of type = % with attr % = % (this card id)', _class, _attr, _value;
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_reference_update(_class regclass, _attr varchar, _card bigint, _value bigint) RETURNS void AS $$ BEGIN
	IF _value IS NULL THEN
		EXECUTE format('UPDATE %s SET %I = NULL WHERE "Status" = ''A'' AND "Id" = %L AND %I IS NOT NULL', _class, _attr, _card, _attr);
	ELSE
		EXECUTE format('UPDATE %s SET %I = %L WHERE "Status" = ''A'' AND "Id" = %L AND %I IS DISTINCT FROM %L', _class, _attr, _value, _card, _attr, _value);
	END IF;
END $$ LANGUAGE PLPGSQL;


--- TRIGGER FUNCTIONS ---

CREATE OR REPLACE FUNCTION _cm3_trigger_simplecard_prepare_record() RETURNS trigger AS $$ BEGIN
	IF (TG_OP='UPDATE') THEN
		IF (NEW."Id" <> OLD."Id") THEN -- Id change
			RAISE EXCEPTION 'CM_FORBIDDEN_OPERATION';
		END IF;
	ELSE
		NEW."IdClass" = TG_RELID;
	END IF;
	NEW."BeginDate" = now();
	NEW."User" = _cm3_utils_operation_user_get();	
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_card_prepare_record() RETURNS trigger AS $$ BEGIN
	IF TG_OP = 'UPDATE' THEN
		IF NEW."Id" <> OLD."Id" THEN
			RAISE EXCEPTION 'CM: operation not allowed: cannot modify card id';
		END IF;
		IF NEW."Status" = 'N' AND OLD."Status" = 'N' THEN
			RAISE EXCEPTION 'CM: operation not allowed: cannot modify this card because its "Status" is ''N''';
		END IF;
	ELSEIF TG_OP = 'INSERT' THEN
		IF NEW."Status" IS NULL THEN
			NEW."Status" = 'A';
		ELSEIF NEW."Status" = 'N' THEN
			RAISE EXCEPTION 'CM: operation not allowed: cannot INSERT a card with status "N"';
		END IF;
		NEW."Id" = _cm3_utils_new_card_id();
		IF _cm3_class_is_domain(TG_RELID::regclass) THEN 
			NEW."IdDomain" = TG_RELID;
            IF NEW."IdClass1" IS NULL THEN
                NEW."IdClass1" = (SELECT "IdClass" FROM "Class" WHERE "Id" = NEW."IdObj1" AND "Status" = 'A');
            END IF;
            IF NEW."IdClass2" IS NULL THEN
                NEW."IdClass2" = (SELECT "IdClass" FROM "Class" WHERE "Id" = NEW."IdObj2" AND "Status" = 'A');
            END IF;
            PERFORM _cm3_domain_source_check(TG_RELID::regclass, NEW."IdClass1");
            PERFORM _cm3_domain_target_check(TG_RELID::regclass, NEW."IdClass2");
		ELSE
			NEW."IdClass" = TG_RELID;
		END IF;
	ELSEIF TG_OP = 'DELETE' AND OLD."Status" = 'N' THEN
        EXECUTE format('DELETE FROM "Map" WHERE ( "IdObj1" = %s OR "IdObj2" = %s ) AND "Status" = ''N''', OLD."Id", OLD."Id");
        EXECUTE format('DELETE FROM "Map" WHERE ( "IdObj1" = %s OR "IdObj2" = %s ) AND "Status" = ''U''', OLD."Id", OLD."Id");
        EXECUTE format('DELETE FROM "%s_history" WHERE "CurrentId" = %s', _cm3_utils_regclass_to_name(TG_RELID::regclass), OLD."Id");
        RETURN OLD;
	ELSE
		RAISE EXCEPTION 'CM: operation not allowed: you cannot execute % on this table', TG_OP;
	END IF;
	IF NEW."Status" !~ '^[AN]$' THEN
		RAISE EXCEPTION 'CM: operation not allowed: invalid card status = %', NEW."Status";
	END IF;
	NEW."CurrentId" = NEW."Id";
	NEW."BeginDate" = now();
	NEW."User" = _cm3_utils_operation_user_get();	
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_superclass_forbid_operations() RETURNS trigger AS $$ BEGIN
	RAISE EXCEPTION 'CM: operation not allowed: you cannot execute % on superclass table', TG_OP;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_card_create_history() RETURNS trigger AS $$ DECLARE
	_attr_list_str varchar;
BEGIN
	OLD."Id" = _cm3_utils_new_card_id();
	OLD."Status" = 'U';
	OLD."EndDate" = now();
	SELECT INTO _attr_list_str string_agg(x.x,',') from (select quote_ident(x) x from _cm3_attribute_list(TG_RELID::regclass) x) x;
	EXECUTE format('INSERT INTO "%s_history" (%s) VALUES ( (%L::%s).* )', _cm3_utils_regclass_to_name(TG_RELID::regclass), _attr_list_str, OLD, TG_RELID::regclass);
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_card_cascade_delete_on_relations() RETURNS trigger AS $$ BEGIN
	IF NEW."Status" = 'N' AND OLD."Status" = 'A' THEN
		UPDATE "Map" SET "Status" = 'N' WHERE "Status" = 'A' AND ( "IdObj1" = OLD."Id" OR "IdObj2" = OLD."Id" );
	END IF;
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_simplecard_cascade_delete_on_relations() RETURNS trigger AS $$ BEGIN
	UPDATE "Map" SET "Status" = 'N' WHERE "Status" = 'A' AND ( "IdObj1" = OLD."Id" OR "IdObj2" = OLD."Id" );
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_card_enforce_foreign_key_for_target() RETURNS trigger AS $$ DECLARE
	_class regclass = TG_ARGV[0]::regclass;
	_attr varchar = TG_ARGV[1];
BEGIN
	IF (TG_OP='UPDATE') THEN
		IF( NEW."Status"='N') THEN
			PERFORM _cm3_trigger_fail_if_reference_value_exists(_class, _attr, OLD."Id");
		END IF;
		RETURN NEW;
	ELSE -- TG_OP='DELETE'
		RETURN OLD;
	END IF;
END $$	LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION _cm3_trigger_card_enforce_foreign_key_for_source() RETURNS trigger AS $$ DECLARE
	_attribute_name varchar = TG_ARGV[0];
	_class regclass = TG_ARGV[1]::regclass;
	_reference_value bigint; 
BEGIN 
	EXECUTE format('SELECT (%L::%s).%I', NEW, TG_RELID::regclass, _attribute_name) INTO _reference_value;
	IF _reference_value IS NOT NULL AND NOT _cm3_card_exists_with_id(_class, _reference_value, NEW."Status" = 'A') THEN
		RAISE 'CM: error while inserting new % record: card not found for class = % card_id = % (referenced from attr %.% )', TG_RELID::regclass, _class, _reference_value, _cm3_utils_regclass_to_name(TG_RELID::regclass), _attribute_name;
	END IF;
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION _cm3_trigger_simplecard_enforce_foreign_key_for_source() RETURNS trigger AS $$ DECLARE
	_attribute_name varchar = TG_ARGV[0];
	_class regclass = TG_ARGV[1]::regclass;
	_reference_value bigint;
BEGIN 
	EXECUTE format('SELECT (%L::%s).%I', NEW, TG_RELID::regclass, _attribute_name) INTO _reference_value;
	IF _reference_value IS NOT NULL AND NOT _cm3_card_exists_with_id(_class, _reference_value, FALSE) THEN
		RAISE 'CM: error while inserting new % record: card not found for class = % card_id = % (referenced from attr %.% )', TG_RELID::regclass, _class, _reference_value, _cm3_utils_regclass_to_name(TG_RELID::regclass), _attribute_name;
	END IF;
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION _cm3_trigger_relation_update_references() RETURNS trigger AS $$ DECLARE
	_attribute_name text = TG_ARGV[1];
	_class regclass = TG_ARGV[0]::regclass;
	_direction varchar = TG_ARGV[2];
	_card_column text = CASE WHEN _direction = 'direct' THEN 'IdObj1' ELSE 'IdObj2' END;
	_reference_column text = CASE WHEN _direction = 'direct' THEN 'IdObj2' ELSE 'IdObj1' END;
	_old_card_id bigint;
	_new_card_id bigint;
	_old_ref_value bigint;
	_new_ref_value bigint;
BEGIN	
	IF NEW."Status" IN ('A','N') THEN 

		RAISE DEBUG 'relation_update_references domain = % to attr = %.%', _cm3_utils_regclass_to_domain_name(TG_RELID::regclass), _cm3_utils_regclass_to_name(_class), _attribute_name;

		EXECUTE format('SELECT (%L::%s).%I', NEW, TG_RELID::regclass, _card_column) INTO _new_card_id;

		IF NEW."Status" = 'A' THEN
			EXECUTE format('SELECT (%L::%s).%I', NEW, TG_RELID::regclass, _reference_column) INTO _new_ref_value;
		END IF;

		IF TG_OP = 'UPDATE' THEN
			EXECUTE format('SELECT (%L::%s).%I', OLD, TG_RELID::regclass, _card_column) INTO _old_card_id;
			IF _old_card_id <> _new_card_id THEN
				PERFORM _cm3_reference_update(_class, _attribute_name, _old_card_id, NULL);
			ELSE
				EXECUTE format('SELECT (%L::%s).%I', OLD, TG_RELID::regclass, _reference_column) INTO _old_ref_value;
			END IF;
		END IF;

		RAISE DEBUG 'relation_update_references domain = %: old rel = % -> %, new rel = % -> % (direction = %)', _cm3_utils_regclass_to_domain_name(TG_RELID::regclass), _old_card_id, _old_ref_value, _new_card_id, _new_ref_value, _direction;

		IF _new_ref_value IS DISTINCT FROM _old_ref_value THEN
			PERFORM _cm3_reference_update( _class, _attribute_name, _new_card_id, _new_ref_value);
		END IF;

	END IF;
	RETURN NEW;
END $$	LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION _cm3_trigger_card_update_relations() RETURNS trigger AS $$ DECLARE
	_attribute_name text = TG_ARGV[0];
	_domain regclass = TG_ARGV[1]::regclass;
	_direction varchar = lower(TG_ARGV[2]); 
	_target_id_old bigint;
	_target_id_new bigint;
	_source_id bigint = NEW."Id";
	_source_class regclass = TG_RELID::regclass;
	_target_class regclass = _cm3_utils_name_to_regclass(_cm3_class_comment_get(_domain, CASE _direction WHEN 'direct' THEN 'CLASS2' ELSE 'CLASS1' END));
BEGIN
	IF TG_OP = 'UPDATE' THEN
		EXECUTE format('SELECT (%L::%s).%I', OLD, TG_RELID::regclass, _attribute_name) INTO _target_id_old;
	END IF;
	EXECUTE format('SELECT (%L::%s).%I', NEW, TG_RELID::regclass, _attribute_name) INTO _target_id_new;
    IF _target_id_new IS NOT NULL AND _cm3_class_is_superclass(_target_class) THEN
        EXECUTE format('SELECT "IdClass" FROM %s WHERE "Id" = %L AND "Status" = ''A''', _target_class, _target_id_new) INTO _target_class;
    END IF;
	RAISE DEBUG 'card_update_relations: direction = %, old target = %, new target = % %', _direction, _target_id_old, _target_class, _target_id_new;
	IF _target_id_new IS DISTINCT FROM _target_id_old AND (_target_id_new IS NOT NULL OR _target_id_old IS NOT NULL) THEN
		IF _target_id_old IS NULL THEN
			RAISE DEBUG 'card_update_relations: insert relation record = % -> % (%)', _source_id, _target_id_new, _direction;
			IF _direction = 'direct' THEN
				EXECUTE format('INSERT INTO %s ("IdDomain","IdClass1","IdObj1","IdClass2","IdObj2","Status") VALUES (%L,%L,%L,%L,%L,''A'') ON CONFLICT DO NOTHING', _domain, _domain, _source_class, _source_id, _target_class, _target_id_new);
			ELSE
				EXECUTE format('INSERT INTO %s ("IdDomain","IdClass2","IdObj2","IdClass1","IdObj1","Status") VALUES (%L,%L,%L,%L,%L,''A'' ) ON CONFLICT DO NOTHING', _domain, _domain, _source_class, _source_id, _target_class, _target_id_new);
			END IF;
		ELSEIF _target_id_new IS NULL THEN
			RAISE DEBUG 'card_update_relations: delete relation record = % -> % (%)', _source_id, _target_id_old, _direction;
			IF _direction = 'direct' THEN
				EXECUTE format('UPDATE %s SET "Status" = ''N'' WHERE "Status" = ''A'' AND "IdObj1" = %L', _domain, _source_id);
			ELSE
				EXECUTE format('UPDATE %s SET "Status" = ''N'' WHERE "Status" = ''A'' AND "IdObj2" = %L', _domain, _source_id);
			END IF;
		ELSE
			RAISE DEBUG 'card_update_relations: update relation record = % -> % (%)', _source_id, _target_id_new, _direction;
			IF _direction = 'direct' THEN
				EXECUTE format('UPDATE %s SET "IdClass2" = %L, "IdObj2" = %L WHERE "IdObj1" = %L AND "Status" = ''A'' AND "IdObj2" <> %L', _domain, _target_class, _target_id_new, _source_id, _target_id_new);
			ELSE
				EXECUTE format('UPDATE %s SET "IdClass1" = %L, "IdObj1" = %L WHERE "IdObj2" = %L AND "Status" = ''A'' AND "IdObj1" <> %L', _domain, _target_class, _target_id_new, _source_id, _target_id_new);
			END IF;
		END IF;
	END IF;
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION _cm3_trigger_lookup() RETURNS trigger AS $$ DECLARE
    _type varchar;
BEGIN
    FOR _type IN SELECT DISTINCT "Type" FROM "LookUp" l1 WHERE "Status" = 'A' AND NOT EXISTS (SELECT * FROM "LookUp" l2 WHERE l1."Type" = l2."Type" AND "Status" = 'A' AND "Code" = 'org.cmdbuild.LOOKUPTYPE') LOOP
        INSERT INTO "LookUp" ("Code", "Type") VALUES ('org.cmdbuild.LOOKUPTYPE', _type);
    END LOOP;
	RETURN NULL;
END $$ LANGUAGE PLPGSQL;


-- REQUIRE PATCH 3.1.0-01_attribute_groups

CREATE OR REPLACE FUNCTION _cm3_trigger_attribute_group() RETURNS trigger AS $$ DECLARE
    _sub_class regclass;
BEGIN
    FOR _sub_class IN SELECT _cm3_class_list_descendant_classes(NEW."Owner") LOOP
        IF NOT EXISTS (SELECT * FROM "_AttributeGroup" WHERE "Status" = 'A' AND "Owner" = _sub_class AND "Code" = NEW."Code") THEN
            INSERT INTO "_AttributeGroup" ("Code", "Description", "Index", "Owner") VALUES (NEW."Code", NEW."Description", COALESCE((SELECT MAX("Index")+1 FROM "_AttributeGroup" WHERE "Status" = 'A' AND "Owner" = _sub_class), 1), _sub_class);
            RAISE NOTICE 'copy attribute group = % from class = % to class = %', NEW."Code", NEW."Owner", _sub_class;
        ELSEIF TG_OP = 'UPDATE' THEN
            IF NEW."Description" <> OLD."Description" THEN
                UPDATE "_AttributeGroup" SET "Description" = NEW."Description" WHERE "Status" = 'A' AND "Owner" = _sub_class AND "Code" = NEW."Code";
            END IF;
            IF NEW."Index" <> OLD."Index" THEN
                UPDATE "_AttributeGroup" SET "Index" = NEW."Index" WHERE "Status" = 'A' AND "Owner" = _sub_class AND "Code" = NEW."Code"; --TODO improve index processing
            END IF;
        END IF;
    END LOOP;
    RETURN NULL;
END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.1.0-13_gis_theme_rules

CREATE OR REPLACE FUNCTION _cm3_trigger_filter_cleanup() RETURNS trigger AS $$ DECLARE
    _record RECORD;
BEGIN
    FOR _record IN SELECT * FROM "_Grant" WHERE "Type" = 'Filter' AND "Status" = 'A' AND "ObjectId" = OLD."Id" LOOP
        RAISE NOTICE 'cascade drop grant record = %', _record;
        EXECUTE format('UPDATE "_Grant" SET "Status" = ''N'' WHERE "Id" = %s', _record."Id"); --TODO cache drop ??
    END LOOP;
    --TODO default filter cleanup from classes ??
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_ietemplate_cleanup() RETURNS trigger AS $$ DECLARE
    _record RECORD;
BEGIN
    FOR _record IN SELECT * FROM "_Grant" WHERE "Type" = 'IETemplate' AND "Status" = 'A' AND "ObjectId" = OLD."Id" LOOP
        RAISE NOTICE 'cascade drop grant record = %', _record;
        EXECUTE format('UPDATE "_Grant" SET "Status" = ''N'' WHERE "Id" = %s', _record."Id"); --TODO cache drop ??
    END LOOP;
    --TODO trigger config cleanup? other cleanup?
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_view_cleanup() RETURNS trigger AS $$ DECLARE
    _record RECORD;
BEGIN
    FOR _record IN SELECT * FROM "_Grant" WHERE "Type" = 'View' AND "Status" = 'A' AND "ObjectId" = OLD."Id" LOOP
        RAISE NOTICE 'cascade drop grant record = %', _record;
        EXECUTE format('UPDATE "_Grant" SET "Status" = ''N'' WHERE "Id" = %s', _record."Id"); --TODO cache drop ??
    END LOOP;
    --TODO menu cleanup? other cleanup?
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_report_cleanup() RETURNS trigger AS $$ DECLARE
    _record RECORD;
BEGIN
    FOR _record IN SELECT * FROM "_Grant" WHERE "Type" = 'Report' AND "Status" = 'A' AND "ObjectId" = OLD."Id" LOOP
        RAISE NOTICE 'cascade drop grant record = %', _record;
        EXECUTE format('UPDATE "_Grant" SET "Status" = ''N'' WHERE "Id" = %s', _record."Id"); --TODO cache drop ??
    END LOOP;
    --TODO menu cleanup? other cleanup?
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_custompage_cleanup() RETURNS trigger AS $$ DECLARE
    _record RECORD;
BEGIN
    FOR _record IN SELECT * FROM "_Grant" WHERE "Type" = 'CustomPage' AND "Status" = 'A' AND "ObjectId" = OLD."Id" LOOP
        RAISE NOTICE 'cascade drop grant record = %', _record;
        EXECUTE format('UPDATE "_Grant" SET "Status" = ''N'' WHERE "Id" = %s', _record."Id"); --TODO cache drop ??
    END LOOP;
    --TODO menu cleanup? other cleanup?
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;
