

CREATE OR REPLACE FUNCTION test_setup() RETURNS void AS $$ BEGIN
	PERFORM _cm3_class_create('MyClassOne', NULL, '');
	PERFORM _cm3_class_create('MyClassTwo', NULL, '');
	PERFORM _cm3_domain_create('MyDomain', jsonb_build_object('CLASS1','MyClassOne','CLASS2','MyClassTwo','CARDIN','1:N'));
	PERFORM _cm3_attribute_create('"MyClassTwo"', 'One', 'bigint', jsonb_build_object('REFERENCEDOM','MyDomain','REFERENCEDIR','inverse'));

	INSERT INTO "MyClassOne" ("Code") VALUES ('ally');
	INSERT INTO "MyClassOne" ("Code") VALUES ('bob');
	INSERT INTO "MyClassTwo" ("Code") VALUES ('charlie');
	INSERT INTO "MyClassTwo" ("Code") VALUES ('della');
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_teardown() RETURNS void AS $$ BEGIN
    DROP TABLE IF EXISTS "Map_MyDomain" CASCADE;
    DROP TABLE IF EXISTS "MyClassOne" CASCADE;
    DROP TABLE IF EXISTS "MyClassTwo" CASCADE;
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_reference_cascade_update_to_domain() RETURNS void AS $$ BEGIN

	UPDATE "MyClassTwo" SET "One" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A') WHERE "Code" = 'della' AND "Status" = 'A';

	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A' 
		AND "IdObj1" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A')
		AND "IdObj2" = (SELECT "Id" FROM "MyClassTwo" WHERE "Code" = 'della' AND "Status" = 'A')
		AND "IdClass1" = '"MyClassOne"'::regclass
		AND "IdClass2" = '"MyClassTwo"'::regclass
		) = 1);

	UPDATE "MyClassTwo" SET "One" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'bob' AND "Status" = 'A') WHERE "Code" = 'charlie' AND "Status" = 'A';

	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A') = 2);
	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A' 
		AND "IdObj1" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'bob' AND "Status" = 'A')
		AND "IdObj2" = (SELECT "Id" FROM "MyClassTwo" WHERE "Code" = 'charlie' AND "Status" = 'A')
		AND "IdClass1" = '"MyClassOne"'::regclass
		AND "IdClass2" = '"MyClassTwo"'::regclass
		) = 1);

	UPDATE "MyClassTwo" SET "One" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A') WHERE "Code" = 'charlie' AND "Status" = 'A';

	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A') = 2);
	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'U') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'U' 
		AND "IdObj1" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'bob' AND "Status" = 'A')
		AND "IdObj2" = (SELECT "Id" FROM "MyClassTwo" WHERE "Code" = 'charlie' AND "Status" = 'A')
		AND "IdClass1" = '"MyClassOne"'::regclass
		AND "IdClass2" = '"MyClassTwo"'::regclass
		) = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A' 
		AND "IdObj1" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A')
		AND "IdObj2" = (SELECT "Id" FROM "MyClassTwo" WHERE "Code" = 'charlie' AND "Status" = 'A')
		AND "IdClass1" = '"MyClassOne"'::regclass
		AND "IdClass2" = '"MyClassTwo"'::regclass
		) = 1);

END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_reference_cascade_update_null_to_domain() RETURNS void AS $$ BEGIN

	UPDATE "MyClassTwo" SET "One" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A') WHERE "Code" = 'della' AND "Status" = 'A';

	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A' 
		AND "IdObj1" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A')
		AND "IdObj2" = (SELECT "Id" FROM "MyClassTwo" WHERE "Code" = 'della' AND "Status" = 'A')
		AND "IdClass1" = '"MyClassOne"'::regclass
		AND "IdClass2" = '"MyClassTwo"'::regclass
		) = 1);

	UPDATE "MyClassTwo" SET "One" = NULL WHERE "Code" = 'della' AND "Status" = 'A';

	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A') = 0);
	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'U') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'N') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'N' 
		AND "IdObj1" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A')
		AND "IdObj2" = (SELECT "Id" FROM "MyClassTwo" WHERE "Code" = 'della' AND "Status" = 'A')
		AND "IdClass1" = '"MyClassOne"'::regclass
		AND "IdClass2" = '"MyClassTwo"'::regclass
		) = 1);

END $$ LANGUAGE PLPGSQL; 


-- CREATE OR REPLACE FUNCTION test_domain_delete_fail_if_reference_exists() RETURNS void AS $$ BEGIN
--  TODO
-- END $$ LANGUAGE PLPGSQL;



CREATE OR REPLACE FUNCTION test_domain_cascade_insert_to_reference_attr() RETURNS void AS $$ BEGIN

	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" IS NOT NULL) = 0);

	INSERT INTO "Map_MyDomain" ("IdClass1","IdObj1","IdClass2","IdObj2") VALUES (
		'"MyClassOne"',(SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A'),
		'"MyClassTwo"',(SELECT "Id" FROM "MyClassTwo" WHERE "Code" = 'della' AND "Status" = 'A'));

	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A') AND "Code" = 'della') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A') AND "Code" <> 'della') = 0);

END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_domain_cascade_target_update_to_reference_attr() RETURNS void AS $$ BEGIN

	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" IS NOT NULL) = 0);

	INSERT INTO "Map_MyDomain" ("IdClass1","IdObj1","IdClass2","IdObj2") VALUES (
		'"MyClassOne"',(SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A'),
		'"MyClassTwo"',(SELECT "Id" FROM "MyClassTwo" WHERE "Code" = 'della' AND "Status" = 'A'));

	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A') AND "Code" = 'della') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A') AND "Code" <> 'della') = 0);

	UPDATE "Map_MyDomain" SET "IdObj1" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'bob' AND "Status" = 'A') WHERE "Status" = 'A';

	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'U') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'bob' AND "Status" = 'A') AND "Code" = 'della') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" IS NOT NULL AND "Code" <> 'della') = 0);

END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_domain_cascade_source_update_to_reference_attr() RETURNS void AS $$ BEGIN

	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" IS NOT NULL) = 0);

	INSERT INTO "Map_MyDomain" ("IdClass1","IdObj1","IdClass2","IdObj2") VALUES (
		'"MyClassOne"',(SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A'),
		'"MyClassTwo"',(SELECT "Id" FROM "MyClassTwo" WHERE "Code" = 'della' AND "Status" = 'A'));

	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'U') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A') AND "Code" = 'della') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A') AND "Code" <> 'della') = 0);

	UPDATE "Map_MyDomain" SET "IdObj2" = (SELECT "Id" FROM "MyClassTwo" WHERE "Code" = 'charlie' AND "Status" = 'A') WHERE "Status" = 'A';

	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'U') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'U') = 3);
	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A') AND "Code" = 'charlie') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" IS NOT NULL AND "Code" <> 'charlie') = 0);

END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_domain_cascade_delete_to_reference_attr() RETURNS void AS $$ BEGIN

	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" IS NOT NULL) = 0);

	INSERT INTO "Map_MyDomain" ("IdClass1","IdObj1","IdClass2","IdObj2") VALUES (
		'"MyClassOne"',(SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A'),
		'"MyClassTwo"',(SELECT "Id" FROM "MyClassTwo" WHERE "Code" = 'della' AND "Status" = 'A'));

	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A') AND "Code" = 'della') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A') AND "Code" <> 'della') = 0);

	UPDATE "Map_MyDomain" SET "Status" = 'N' WHERE "Status" = 'A';

	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A') = 0);
	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'U') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'N') = 1);
	PERFORM assertTrue((SELECT COUNT(*) FROM "MyClassTwo" WHERE "Status" = 'A' AND "One" IS NOT NULL) = 0);

END $$ LANGUAGE PLPGSQL;
