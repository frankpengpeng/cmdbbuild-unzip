

CREATE OR REPLACE FUNCTION test_teardown() RETURNS void AS $$ BEGIN
	PERFORM _cm3_class_delete('"MyClass"');
	PERFORM _cm3_class_delete('"MyParentClass"');
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_class_inheritance() RETURNS void AS $$ BEGIN
	PERFORM _cm3_class_create('MyParentClass', NULL, 'SUPERCLASS: true');
	PERFORM _cm3_class_create('MyClass', '"MyParentClass"', '');

	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"Class"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target' AND trigger_name = '_cm3_card_enforce_fk_Email_Card')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"Class"'))=2);

	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyParentClass"'))=2);

	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClass"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target' AND trigger_name = '_cm3_card_enforce_fk_Email_Card')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClass"'))=4);
END $$ LANGUAGE PLPGSQL;
