-- entity metadata upgrade 1


SELECT cm_create_class('_ClassMetadata', 'Class', 'MODE: reserved|TYPE: class|SUPERCLASS: false');
SELECT cm_create_class_attribute('_ClassMetadata', 'Owner', 'regclass', null, true, true, '');
SELECT cm_create_class_attribute('_ClassMetadata', 'Metadata', 'jsonb', '''{}''::jsonb', true, false, ''); 


SELECT cm_create_class('_AttributeMetadata', 'Class', 'MODE: reserved|TYPE: class|SUPERCLASS: false');
SELECT cm_create_class_attribute('_AttributeMetadata', 'Owner', 'regclass', null, true, false, '');
SELECT cm_create_class_attribute('_AttributeMetadata', 'Metadata', 'jsonb', '''{}''::jsonb', true, false, ''); 


SELECT cm_create_class('_CardMetadata', 'Class', 'MODE: reserved|TYPE: class|SUPERCLASS: false');
SELECT cm_create_class_attribute('_CardMetadata', 'OwnerClass', 'regclass', null, true, false, '');
SELECT cm_create_class_attribute('_CardMetadata', 'OwnerCard', 'bigint', null, true, true, '');
SELECT cm_create_class_attribute('_CardMetadata', 'Data', 'jsonb', '''{}''::jsonb', true, false, ''); 


SELECT cm_create_class('_FunctionMetadata', 'Class', 'MODE: reserved|TYPE: class|SUPERCLASS: false');
SELECT cm_create_class_attribute('_FunctionMetadata', 'OwnerFunction', 'integer', null, true, true, '');
SELECT cm_create_class_attribute('_FunctionMetadata', 'Data', 'jsonb', '''{}''::jsonb', true, false, ''); 


INSERT INTO  "Metadata" ("IdClass","Code","Description","Notes","Status")  SELECT 
	'"Metadata"',
	regexp_replace("Element",'[^.]+[.]',''),
	'cm_class_icon',
	'images/'||regexp_replace("Path",'^[\\/]+',''),
	'A'
FROM "_Icon";

DROP TABLE "_Icon";

DO $$ DECLARE
	_function int;
	_attr varchar;
	_value varchar;
	_metadata jsonb;
BEGIN
	FOR _function,_attr,_value IN
		SELECT (SELECT oid FROM pg_proc WHERE proname=regexp_replace("Code",'(.*)[.].*','\1')),regexp_replace("Code",'.*[.](.*)','\1'),"Notes" FROM "Metadata" WHERE "Status" = 'A' AND "Description" = 'system.function.hideColumn'
	LOOP
		PERFORM _cm3_function_metadata_set(_function,_metadata,'attribute.'||_attr||'.basedsp', (NOT _value::boolean)::varchar);
	END LOOP;
	UPDATE "Metadata" SET "Status" = 'N' WHERE "Description" = 'system.function.hideColumn' AND "Status" = 'A';
END $$ LANGUAGE PLPGSQL;

DO $$ DECLARE
	_attr varchar;
	_owner regclass;
	_record RECORD;
BEGIN
	FOR _record IN SELECT * FROM "Metadata" WHERE "Status" = 'A' AND "Code" IS NOT NULL AND "Code" LIKE '%.%' LOOP
		BEGIN
			_owner = _cm3_utils_name_to_regclass(split_part(_record."Code",'.',1));
			IF _owner IS NULL THEN
				RAISE NOTICE 'error processing Metadata record %: table not found for name "%"', _record."Id", _record."Code";
			ELSE
				_attr = split_part(_record."Code",'.',2);
				PERFORM _cm3_attribute_metadata_set(_owner, _attr, _record."Description", _record."Notes");
			END IF;
		EXCEPTION WHEN others THEN
			RAISE EXCEPTION 'error processing Metadata record %: %', _record."Id", SQLERRM;
		END;
	END LOOP;
END $$ LANGUAGE PLPGSQL;

CREATE TABLE _patch_card_metadata_aux AS 
	SELECT "Description" AS "Code","Notes" AS "Value",cm."IdClass1" ownerclass,cm."IdObj1" ownercard 
	FROM "Metadata" m 
	JOIN "Map_ClassMetadata" cm ON m."Id" = cm."IdObj2" 
	WHERE m."Status" = 'A' and cm."Status" = 'A';

DO $$ DECLARE
	_record RECORD;
BEGIN
	FOR _record IN SELECT * FROM _patch_card_metadata_aux LOOP
		PERFORM _cm3_card_metadata_set(_record.ownerclass, _record.ownercard, _record."Code", _record."Value");
	END LOOP;
END $$ LANGUAGE PLPGSQL;

DO $$ DECLARE
	_owner regclass;
	_record RECORD;
BEGIN
	FOR _record IN SELECT * FROM "Metadata" WHERE "Status" = 'A' AND "Code" IS NOT NULL AND "Code" NOT LIKE '%.%' LOOP
		BEGIN
			_owner = _cm3_utils_name_to_regclass(_record."Code");
			IF _owner IS NULL THEN
				RAISE NOTICE 'error processing Metadata record %: table not found for name "%"', _record."Id", _record."Code";
			ELSE
				PERFORM _cm3_class_metadata_set(_owner, _record."Description", _record."Notes");
			END IF;
		EXCEPTION WHEN others THEN
			RAISE EXCEPTION 'error processing Metadata record %: %', _record."Id", SQLERRM;
		END;
	END LOOP;
END $$ LANGUAGE PLPGSQL;

DROP TABLE "Map_ClassMetadata" CASCADE;
DROP TABLE "Metadata" CASCADE;
DROP TABLE _patch_card_metadata_aux;
