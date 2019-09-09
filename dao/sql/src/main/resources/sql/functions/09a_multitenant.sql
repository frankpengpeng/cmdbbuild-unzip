-- multitenant
-- REQUIRE PATCH 3.0.0-03a_system_functions

CREATE OR REPLACE FUNCTION _cm3_multitenant_mode_set(_class regclass, _mode varchar) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_multitenant_mode_change(_class, _cm3_class_comment_get(_class, 'MTMODE'), _mode);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_multitenant_mode_change(_class regclass, _previous_mode varchar, _mode varchar) RETURNS VOID AS $$ DECLARE
	_has_data boolean; 
BEGIN	
	_mode = coalesce(_mode, 'never');
	_previous_mode = coalesce(_previous_mode, 'never');
	IF _mode NOT IN ('never','mixed','always') THEN
		RAISE EXCEPTION 'CM: error configuring multitenant for class = % : unsupported multitenant mode = %', _class, _mode;
	END IF;
	IF _mode <> _previous_mode THEN
        RAISE NOTICE 'set multitenant mode for class = %, cur mode = %, new mode = %', _class, _previous_mode, _mode;
		IF _cm3_class_is_superclass(_class) THEN
			RAISE 'CM: unable to set multitenant mode on superclass % : operation not allowed', _cm3_utils_regclass_to_name(_class);
		ELSE
			IF _mode = 'never' THEN 
				IF _cm3_class_is_simple(_class) THEN
					EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE "IdTenant" IS NOT NULL AND "IdTenant" <> -1)', _class) INTO _has_data;
				ELSE
					EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE "IdTenant" IS NOT NULL AND "IdTenant" <> -1 AND "Status" = ''A'')', _class) INTO _has_data;
				END IF;
				IF _has_data THEN
					RAISE 'CM: unable to set multitenant mode to "never" for class % : class contains some cards with non-null IdTenant (suggestion: set mode to "mixed" and set all card tenants to NULL, then set mode to "never")', _class;
				END IF;
			END IF; 
			IF _previous_mode IN ('mixed','always') THEN
				EXECUTE format('DROP POLICY IF EXISTS "%s_policy" ON %s', _cm3_utils_regclass_to_name(_class), _class);
				EXECUTE format('ALTER TABLE %s DISABLE ROW LEVEL SECURITY', _class);
			END IF;
			IF _mode IN ('mixed','always') THEN
				EXECUTE format('CREATE INDEX IF NOT EXISTS "%s_idtenant" ON %s ("IdTenant")', _cm3_utils_regclass_to_name(_class), _class);
				EXECUTE format('ALTER TABLE %s ENABLE ROW LEVEL SECURITY, FORCE ROW LEVEL SECURITY', _class);
				EXECUTE format('CREATE POLICY "%s_policy" ON %s USING (current_setting(''cmdbuild.ignore_tenant_policies'') = ''true'' OR "IdTenant" IS NULL OR "IdTenant" = ANY (current_setting(''cmdbuild.user_tenants'')::bigint[]))', 
					_cm3_utils_regclass_to_name(_class), _class);
				EXECUTE format('ALTER TABLE %s DISABLE TRIGGER USER', _class);
				EXECUTE format('ALTER TABLE %s DISABLE TRIGGER USER', _cm3_utils_regclass_to_history(_class));
				IF _mode = 'always' THEN
					EXECUTE format('UPDATE %s SET "IdTenant" = -1 WHERE "IdTenant" IS NULL', _class);
				ELSEIF _mode = 'mixed' THEN
					EXECUTE format('UPDATE %s SET "IdTenant" = NULL WHERE "IdTenant" = -1', _class);
				END IF;
				EXECUTE format('ALTER TABLE %s ENABLE TRIGGER USER', _class);
				EXECUTE format('ALTER TABLE %s ENABLE TRIGGER USER', _cm3_utils_regclass_to_history(_class));
				PERFORM _cm3_class_comment_set(_class, 'MTMODE', _mode);
			ELSEIF _mode = 'never' THEN 
				IF _previous_mode = 'always' THEN
					EXECUTE format('ALTER TABLE %s DISABLE TRIGGER USER', _class);
					EXECUTE format('UPDATE %s SET "IdTenant" = NULL WHERE "IdTenant" = -1', _class);
					EXECUTE format('ALTER TABLE %s ENABLE TRIGGER USER', _class);
				END IF;
				EXECUTE format('DROP INDEX IF EXISTS "%s_idtenant"', _cm3_utils_regclass_to_name(_class), _class);
				PERFORM _cm3_class_comment_delete(_class, 'MTMODE');
			END IF;
		END IF;
    ELSE
        RAISE DEBUG 'no need to set multitenant mode for class = %, cur mode = %, new mode = %', _class, _previous_mode, _mode;
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_multitenant_superclass_policy_set(_class regclass) RETURNS VOID AS $$ BEGIN
	IF NOT _cm3_class_is_superclass(_class) THEN
		RAISE 'CM: unable to set multitenant superclassclass policy on class % : operation not allowed', _cm3_utils_regclass_to_name(_class);
	END IF;
	EXECUTE format('ALTER TABLE %s ENABLE ROW LEVEL SECURITY, FORCE ROW LEVEL SECURITY', _class);
	EXECUTE format('CREATE POLICY "%s_policy" ON %s USING (current_setting(''cmdbuild.ignore_tenant_policies'') = ''true'' OR "IdTenant" IS NULL OR "IdTenant" = ANY (current_setting(''cmdbuild.user_tenants'')::bigint[]))', _cm3_utils_regclass_to_name(_class), _class);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_multitenant_tenant_class_helper_trigger() RETURNS trigger AS $$ DECLARE
	_class_mode varchar;
BEGIN
	_class_mode = coalesce(_cm3_class_comment_get(TG_RELID::regclass, 'MTMODE'), 'never');
	IF _class_mode = 'always' AND NEW."IdTenant" IS NULL THEN
		NEW."IdTenant" = NEW."Id";
	END IF;
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_multitenant_tenant_class_trigger_install(_class regclass) RETURNS VOID AS $$ BEGIN
	EXECUTE format('CREATE TRIGGER "_cm3_multitenant_tenant_class_helper" BEFORE INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_multitenant_tenant_class_helper_trigger()', _class);
END $$ LANGUAGE PLPGSQL;