
CREATE OR REPLACE FUNCTION tuid(_basename varchar) RETURNS VARCHAR AS $$ BEGIN
	RETURN format('%s%s', _basename, initcap(tuid()));
END $$ LANGUAGE PLPGSQL IMMUTABLE;

CREATE OR REPLACE FUNCTION assertTrue(_test boolean) RETURNS VOID AS $$ BEGIN
	IF NOT _test THEN
		RAISE EXCEPTION 'assertTrue failure' USING ERRCODE = 'triggered_action_exception';
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION assertTrue(_test boolean, _message varchar) RETURNS VOID AS $$ BEGIN
	IF NOT _test THEN
		RAISE EXCEPTION 'assertTrue failure: %', _message USING ERRCODE = 'triggered_action_exception';
	END IF; 
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION assertFalse(_test boolean) RETURNS VOID AS $$ BEGIN
	IF _test THEN
		RAISE EXCEPTION 'assertFalse failure' USING ERRCODE = 'triggered_action_exception';
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION assertFalse(_test boolean, _message varchar) RETURNS VOID AS $$ BEGIN
	IF _test THEN
		RAISE EXCEPTION 'assertFalse failure: %', _message USING ERRCODE = 'triggered_action_exception';
	END IF; 
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION assertNull(_item ANYELEMENT) RETURNS VOID AS $$ BEGIN
	IF _item IS NOT NULL THEN
		RAISE EXCEPTION 'assertNull failure' USING ERRCODE = 'triggered_action_exception';
	END IF; 
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION assertNull(_item ANYELEMENT, _message varchar) RETURNS VOID AS $$ BEGIN
	IF _item IS NOT NULL THEN
		RAISE EXCEPTION 'assertNull failure: %', _message USING ERRCODE = 'triggered_action_exception';
	END IF; 
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION assertNotNull(_item ANYELEMENT) RETURNS VOID AS $$ BEGIN
	IF _item IS NULL THEN
		RAISE EXCEPTION 'assertNotNull failure' USING ERRCODE = 'triggered_action_exception';
	END IF; 
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION assertNotNull(_item ANYELEMENT, _message varchar) RETURNS VOID AS $$ BEGIN
	IF _item IS NULL THEN
		RAISE EXCEPTION 'assertNotNull failure: %', _message USING ERRCODE = 'triggered_action_exception';
	END IF; 
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION fail() RETURNS VOID AS $$ BEGIN
	RAISE EXCEPTION 'test failure' USING ERRCODE = 'triggered_action_exception';
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION fail(_message varchar) RETURNS VOID AS $$ BEGIN
	RAISE EXCEPTION 'test failure: %', _message USING ERRCODE = 'triggered_action_exception';
END $$ LANGUAGE PLPGSQL;
