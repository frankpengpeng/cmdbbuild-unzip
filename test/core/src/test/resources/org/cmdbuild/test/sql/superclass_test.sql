

-- EXPECTED: CM: operation not allowed: you cannot execute INSERT
CREATE OR REPLACE FUNCTION test_superclass_insert_is_forbidden() RETURNS void AS $$ BEGIN
	PERFORM _cm3_class_create('MyParentClass', NULL, 'SUPERCLASS: true');
	PERFORM _cm3_class_create('MyClass', '"MyParentClass"', '');

	INSERT INTO "MyParentClass" ("Code","IdClass","Status") VALUES ('asd','"MyClass"','A');
END $$ LANGUAGE PLPGSQL;

-- EXPECTED: CM: operation not allowed: you cannot execute DELETE
CREATE OR REPLACE FUNCTION test_superclass_delete_is_forbidden() RETURNS void AS $$ BEGIN
	PERFORM _cm3_class_create('MyParentClass', NULL, 'SUPERCLASS: true');
	PERFORM _cm3_class_create('MyClass', '"MyParentClass"', '');

	INSERT INTO "MyClass" ("Code") VALUES ('asd');
	DELETE FROM "MyParentClass";
END $$ LANGUAGE PLPGSQL;
