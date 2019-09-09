-- translation helper functions
-- REQUIRE PATCH 3.0.0-17_lookups


CREATE OR REPLACE FUNCTION _cm3_translation_get(_key varchar, _lang varchar, _default varchar) RETURNS varchar AS $$ 
    SELECT COALESCE((SELECT "Value" FROM "_Translation" WHERE "Code" = _key AND "Lang" = _lang AND "Status" = 'A'), _default);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_translation_get(_key varchar, _lang varchar) RETURNS varchar AS $$ 
    SELECT "Value" FROM "_Translation" WHERE "Code" = _key AND "Lang" = _lang AND "Status" = 'A';
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_translation_lookup_get(_id bigint, _lang varchar) RETURNS varchar AS $$ DECLARE
    _type varchar;
    _code varchar;
    _desc varchar;
BEGIN
    SELECT "Type", "Code", "Description" INTO _type, _code, _desc FROM "LookUp" WHERE "Id" = _id AND "Status" = 'A';
    RETURN _cm3_translation_get(format('lookup.%s.%s.description', _type, _code), _lang, _desc);
END $$ LANGUAGE PLPGSQL;
