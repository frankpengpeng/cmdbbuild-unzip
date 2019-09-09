

CREATE OR REPLACE FUNCTION test_teardown() RETURNS void AS $$ BEGIN
	PERFORM _cm3_attribute_delete('"MyClassTwo"', 'One');
	PERFORM _cm3_domain_delete('"Map_MyDomain"');
	PERFORM _cm3_class_delete('"MyChildOne"');
	PERFORM _cm3_class_delete('"MyChildTwo"');
	PERFORM _cm3_class_delete('"MyOtherChildTwo"');
	PERFORM _cm3_class_delete('"MyClassOne"');
	PERFORM _cm3_class_delete('"MyClassTwo"');
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_class_inheritance_2() RETURNS void AS $$ BEGIN
	PERFORM _cm3_class_create('MyClassOne', NULL, 'SUPERCLASS: true');
	PERFORM _cm3_class_create('MyClassTwo', NULL, 'SUPERCLASS: true');
	PERFORM _cm3_domain_create('MyDomain', jsonb_build_object('CLASS1','MyClassOne','CLASS2','MyClassTwo','CARDIN','1:N'));
	PERFORM _cm3_attribute_create('"MyClassTwo"', 'One', 'bigint', jsonb_build_object('REFERENCEDOM','MyDomain','REFERENCEDIR','inverse'));

	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassOne"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target' and trigger_name = '_cm3_card_enforce_fk_MyClassTwo_One')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassOne"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target')=2);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassOne"'))=3);

	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassTwo"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_source')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassTwo"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassTwo"') WHERE trigger_function = '_cm3_trigger_card_update_relations')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassTwo"'))=4);

	PERFORM _cm3_class_create('MyChildTwo', '"MyClassTwo"', '');

	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyChildTwo"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_source')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyChildTwo"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyChildTwo"') WHERE trigger_function = '_cm3_trigger_card_update_relations')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyChildTwo"'))=6);

	PERFORM _cm3_class_create('MyOtherChildTwo', '"MyClassTwo"', '');

	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyOtherChildTwo"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_source')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyOtherChildTwo"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyOtherChildTwo"') WHERE trigger_function = '_cm3_trigger_card_update_relations')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyOtherChildTwo"'))=6);

	PERFORM _cm3_class_create('MyChildOne', '"MyClassOne"', '');

	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyChildOne"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target' and trigger_name = '_cm3_card_enforce_fk_MyClassTwo_One')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyChildOne"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target')=2);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyChildOne"'))=5);
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_class_inheritance_3() RETURNS void AS $$ BEGIN
	PERFORM _cm3_class_create('MyClassOne', NULL, 'SUPERCLASS: true');
	PERFORM _cm3_class_create('MyClassTwo', NULL, 'SUPERCLASS: true');
	PERFORM _cm3_class_create('MyChildTwo', '"MyClassTwo"', '');
	PERFORM _cm3_class_create('MyOtherChildTwo', '"MyClassTwo"', '');
	PERFORM _cm3_class_create('MyChildOne', '"MyClassOne"', '');

	PERFORM _cm3_domain_create('MyDomain', jsonb_build_object('CLASS1','MyClassOne','CLASS2','MyClassTwo','CARDIN','1:N'));
	PERFORM _cm3_attribute_create('"MyClassTwo"', 'One', 'bigint', jsonb_build_object('REFERENCEDOM','MyDomain','REFERENCEDIR','inverse'));

	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassOne"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target' and trigger_name = '_cm3_card_enforce_fk_MyClassTwo_One')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassOne"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target')=2);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassOne"'))=3);

	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassTwo"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_source')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassTwo"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassTwo"') WHERE trigger_function = '_cm3_trigger_card_update_relations')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassTwo"'))=4);

	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyChildTwo"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_source')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyChildTwo"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyChildTwo"') WHERE trigger_function = '_cm3_trigger_card_update_relations')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyChildTwo"'))=6);

	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyOtherChildTwo"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_source')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyOtherChildTwo"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyOtherChildTwo"') WHERE trigger_function = '_cm3_trigger_card_update_relations')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyOtherChildTwo"'))=6);

	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyChildOne"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target' and trigger_name = '_cm3_card_enforce_fk_MyClassTwo_One')=1);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyChildOne"') WHERE trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target')=2);
	PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyChildOne"'))=5);
END $$ LANGUAGE PLPGSQL;
