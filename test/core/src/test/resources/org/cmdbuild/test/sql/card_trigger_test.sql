

CREATE OR REPLACE FUNCTION test_setup() RETURNS void AS $$ BEGIN
	PERFORM _cm3_class_create('MyClass', NULL, '');
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_teardown() RETURNS void AS $$ BEGIN
	TRUNCATE TABLE "MyClass";
	PERFORM _cm3_class_delete('"MyClass"');
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_card_insert() RETURNS void AS $$ BEGIN

	INSERT INTO "MyClass" ("Code") VALUES ('hello');
	PERFORM assertTrue((SELECT count(*) FROM "MyClass" WHERE "Status" = 'A' AND "Code" = 'hello') = 1);

END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_card_insert_with_classid() RETURNS void AS $$ BEGIN

	INSERT INTO "MyClass" ("IdClass", "Code") VALUES ('"MyClass"', 'hello');
	PERFORM assertTrue((SELECT count(*) FROM "MyClass" WHERE "Status" = 'A' AND "Code" = 'hello') = 1);
	PERFORM assertFalse((SELECT EXISTS(SELECT * FROM "MyClass" WHERE "IdClass" <> '"MyClass"'::regclass)));

END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_card_insert_ignores_incorrect_classid() RETURNS void AS $$ BEGIN
	
	INSERT INTO "MyClass" ("IdClass", "Code") VALUES ('"Class"', 'hello');
	PERFORM assertTrue((SELECT count(*) FROM "MyClass" WHERE "Status" = 'A' AND "Code" = 'hello') = 1);
	PERFORM assertFalse((SELECT EXISTS(SELECT * FROM "MyClass" WHERE "IdClass" <> '"MyClass"'::regclass)));

END $$ LANGUAGE PLPGSQL;

