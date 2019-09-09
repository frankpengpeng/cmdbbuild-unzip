-- lock
-- REQUIRE PATCH 3.0.0-52


--- LOCK FUNCTIONS ---

CREATE OR REPLACE FUNCTION _cm3_lock_aquire_try(_item_id varchar, _session_id varchar, _request_id varchar, _scope varchar, _time_to_live_seconds int) RETURNS TABLE (is_aquired boolean, lock_id bigint) AS $$ DECLARE
    _current record;
BEGIN
    IF _scope NOT IN ('session', 'request') THEN 
        RAISE 'invalid scope = %', _scope;
    END IF;    
    LOCK TABLE "_Lock" IN ACCESS EXCLUSIVE MODE;
    SELECT INTO _current * FROM "_Lock" WHERE "ItemId" = _item_id;
    IF _current IS NOT NULL AND _current."LastActiveDate" + format('%s seconds', _current."TimeToLive")::interval < now() THEN
        DELETE FROM "_Lock" WHERE "Id" = _current."Id";
        RAISE NOTICE 'removed expired lock = %', _current;
        _current = NULL;
    END IF;
    IF _current IS NULL THEN
        INSERT INTO "_Lock" ("ItemId", "SessionId", "RequestId", "Scope", "TimeToLive", "LastActiveDate") VALUES (_item_id, _session_id, _request_id, _scope, _time_to_live_seconds, now());
        SELECT INTO _current * FROM "_Lock" WHERE "ItemId" = _item_id;
        RAISE NOTICE 'aquired new lock = %', _current;
        RETURN QUERY SELECT TRUE, _current."Id";
    ELSE
        IF ( _current."Scope" = 'session' AND _scope = 'session' AND _current."SessionId" = _session_id ) 
            OR ( _current."Scope" = 'request' AND _scope = 'request' AND _current."SessionId" = _session_id AND _current."RequestId" = _request_id ) THEN
                UPDATE "_Lock" SET "LastActiveDate" = now(), "TimeToLive" = _time_to_live_seconds WHERE "Id" = _current."Id";
                RAISE NOTICE 'aquired existing lock = %', _current;
                RETURN QUERY SELECT TRUE, _current."Id";
        ELSE
            RAISE NOTICE 'unable to aquire lock, already aquired by = %', _current;
            RETURN QUERY SELECT FALSE, _current."Id";
        END IF;
    END IF;
END $$ LANGUAGE PLPGSQL;

