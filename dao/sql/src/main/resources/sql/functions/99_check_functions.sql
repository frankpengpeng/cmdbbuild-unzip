-- check functions
-- REQUIRE PATCH 3.1.0-16_email_table_add_status_skipped

CREATE OR REPLACE FUNCTION _cm3_grant_attribute_priviledges_check(attribute_priviledge jsonb) RETURNS boolean AS $$ DECLARE
    element record;
BEGIN
	FOR element IN (SELECT value FROM jsonb_each(attribute_priviledge)) LOOP
		IF (element.value NOT IN ('"write"', '"read"', '"none"')) THEN
			RAISE WARNING 'GrantAttributePrivilege % is not valid', element.value;
			RETURN false;
		END IF;
	END LOOP;
	RETURN true;
END $$ LANGUAGE PLPGSQL;
