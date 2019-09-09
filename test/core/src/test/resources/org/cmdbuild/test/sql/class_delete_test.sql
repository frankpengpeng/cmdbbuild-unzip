

CREATE OR REPLACE FUNCTION test_class_delete() RETURNS void AS $$ BEGIN

	PERFORM _cm3_class_create('MyClass', NULL, '');

	PERFORM _cm3_class_delete('"MyClass"');

END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_class_delete_2() RETURNS void AS $$ BEGIN

	PERFORM _cm3_class_create(tuid('MyClassOne'), NULL, '');

	PERFORM assertTrue(_cm3_class_exists(tuid('MyClassOne')));

	PERFORM _cm3_class_delete(_cm3_utils_name_to_regclass(tuid('MyClassOne')));
	
	PERFORM assertFalse(_cm3_class_exists(tuid('MyClassOne')));

END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_class_delete_with_reference() RETURNS void AS $$ BEGIN

	PERFORM _cm3_class_create('MyClassOne', NULL, '');
	PERFORM _cm3_class_create('MyClassTwo', NULL, '');
	PERFORM _cm3_domain_create('MyDomain', jsonb_build_object('CLASS1','MyClassOne','CLASS2','MyClassTwo','CARDIN','1:N'));
	PERFORM _cm3_attribute_create('"MyClassTwo"', 'One', 'bigint', jsonb_build_object('REFERENCEDOM','MyDomain','REFERENCEDIR','inverse'));

	PERFORM assertTrue(_cm3_class_exists('MyClassOne'));
	PERFORM assertTrue(_cm3_class_exists('MyClassTwo'));

	PERFORM _cm3_attribute_delete('"MyClassTwo"', 'One');
	PERFORM _cm3_domain_delete('"Map_MyDomain"');
	PERFORM _cm3_class_delete('"MyClassOne"');
	--TODO check triggers
	PERFORM _cm3_class_delete('"MyClassTwo"');

	PERFORM assertFalse(_cm3_class_exists('MyClassOne'));
	PERFORM assertFalse(_cm3_class_exists('MyClassTwo'));

END $$ LANGUAGE PLPGSQL;
