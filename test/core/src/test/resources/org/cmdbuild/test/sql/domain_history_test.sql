

CREATE OR REPLACE FUNCTION test_setup() RETURNS void AS $$ BEGIN
	PERFORM _cm3_class_create('MyClassOne', NULL, '');
	PERFORM _cm3_class_create('MyClassTwo', NULL, 'SUPERCLASS: true');
	PERFORM _cm3_class_create('MyClassTwoChild', '"MyClassTwo"', '');
	PERFORM _cm3_domain_create('MyDomain', jsonb_build_object('CLASS1','MyClassOne','CLASS2','MyClassTwo','CARDIN','1:N')); 
	PERFORM _cm3_attribute_create('"MyClassTwo"', 'One', 'bigint', jsonb_build_object('REFERENCEDOM','MyDomain','REFERENCEDIR','inverse'));

	INSERT INTO "MyClassOne" ("Code") VALUES ('ally');
	INSERT INTO "MyClassOne" ("Code") VALUES ('bob');
	INSERT INTO "MyClassTwoChild" ("Code") VALUES ('charlie');
	INSERT INTO "MyClassTwoChild" ("Code") VALUES ('della');
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_teardown() RETURNS void AS $$ BEGIN
    DROP TABLE IF EXISTS "Map_MyDomain" CASCADE;
    DROP TABLE IF EXISTS "MyClassOne" CASCADE;
    DROP TABLE IF EXISTS "MyClassTwo" CASCADE;
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_domain_history() RETURNS void AS $$ BEGIN

    INSERT INTO "Map_MyDomain" ("IdClass1","IdObj1","IdClass2","IdObj2") VALUES (
        '"MyClassOne"'::regclass,(SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A'),'"MyClassTwoChild"'::regclass,(SELECT "Id" FROM "MyClassTwoChild" WHERE "Code" = 'charlie' AND "Status" = 'A'));

    PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A') = 1);
    PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" <> 'A') = 0);
    PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE 
        "IdClass1" =  '"MyClassOne"'::regclass
        AND "IdObj1" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A')
        AND "IdClass2" =  '"MyClassTwoChild"'::regclass
        AND "IdObj2" = (SELECT "Id" FROM "MyClassTwoChild" WHERE "Code" = 'charlie' AND "Status" = 'A')
        AND "Status" = 'A') = 1);

    UPDATE "Map_MyDomain" SET "IdObj1" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'bob' AND "Status" = 'A') WHERE "Status" = 'A';

    PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A') = 1);
    PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" <> 'A') = 1);
    PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE 
        "IdClass1" =  '"MyClassOne"'::regclass
        AND "IdObj1" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'bob' AND "Status" = 'A')
        AND "IdClass2" =  '"MyClassTwoChild"'::regclass
        AND "IdObj2" = (SELECT "Id" FROM "MyClassTwoChild" WHERE "Code" = 'charlie' AND "Status" = 'A')
        AND "Status" = 'A') = 1);
    PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE 
        "IdClass1" =  '"MyClassOne"'::regclass
        AND "IdObj1" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A')
        AND "IdClass2" =  '"MyClassTwoChild"'::regclass
        AND "IdObj2" = (SELECT "Id" FROM "MyClassTwoChild" WHERE "Code" = 'charlie' AND "Status" = 'A')
        AND "Status" = 'U') = 1);

    UPDATE "Map_MyDomain" SET "IdObj2" = (SELECT "Id" FROM "MyClassTwoChild" WHERE "Code" = 'della' AND "Status" = 'A') WHERE "Status" = 'A';

    PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A') = 1);
    PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" <> 'A') = 2);
    PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE 
        "IdClass1" =  '"MyClassOne"'::regclass
        AND "IdObj1" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'bob' AND "Status" = 'A')
        AND "IdClass2" =  '"MyClassTwoChild"'::regclass
        AND "IdObj2" = (SELECT "Id" FROM "MyClassTwoChild" WHERE "Code" = 'della' AND "Status" = 'A')
        AND "Status" = 'A') = 1);
    PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE 
        "IdClass1" =  '"MyClassOne"'::regclass
        AND "IdObj1" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'bob' AND "Status" = 'A')
        AND "IdClass2" =  '"MyClassTwoChild"'::regclass
        AND "IdObj2" = (SELECT "Id" FROM "MyClassTwoChild" WHERE "Code" = 'charlie' AND "Status" = 'A')
        AND "Status" = 'U') = 1);
    PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE 
        "IdClass1" =  '"MyClassOne"'::regclass
        AND "IdObj1" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A')
        AND "IdClass2" =  '"MyClassTwoChild"'::regclass
        AND "IdObj2" = (SELECT "Id" FROM "MyClassTwoChild" WHERE "Code" = 'charlie' AND "Status" = 'A')
        AND "Status" = 'U') = 1);

END $$ LANGUAGE PLPGSQL;



