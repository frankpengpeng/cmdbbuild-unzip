

CREATE OR REPLACE FUNCTION test_setup() RETURNS void AS $$ BEGIN
	PERFORM _cm3_class_create('MyClassOne', NULL, 'SUPERCLASS: true');
	PERFORM _cm3_class_create('MyClassTwo', NULL, 'SUPERCLASS: true');
	PERFORM _cm3_class_create('MyClassOneChild', '"MyClassOne"', '');
	PERFORM _cm3_class_create('MyClassTwoChild', '"MyClassTwo"', '');
	PERFORM _cm3_domain_create('MyDomain', jsonb_build_object('CLASS1','MyClassOne','CLASS2','MyClassTwo','CARDIN','1:N'));
	PERFORM _cm3_attribute_create('"MyClassTwo"', 'One', 'bigint', jsonb_build_object('REFERENCEDOM','MyDomain','REFERENCEDIR','inverse'));

	INSERT INTO "MyClassOneChild" ("Code") VALUES ('ally');
	INSERT INTO "MyClassOneChild" ("Code") VALUES ('bob');
	INSERT INTO "MyClassTwoChild" ("Code") VALUES ('charlie');
	INSERT INTO "MyClassTwoChild" ("Code") VALUES ('della');
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION test_teardown() RETURNS void AS $$ BEGIN
    DROP TABLE IF EXISTS "Map_MyDomain" CASCADE;
    DROP TABLE IF EXISTS "MyClassOne" CASCADE;
    DROP TABLE IF EXISTS "MyClassTwo" CASCADE;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION test_domain_idclass_with_subclasses() RETURNS void AS $$ BEGIN

    PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"Map_MyDomain"') WHERE 
        trigger_name = '_cm3_rel_update_refs_MyClassTwo_One' 
        AND trigger_function = '_cm3_trigger_relation_update_references' 
        AND trigger_params = ARRAY['"MyClassTwo"','One','inverse']::varchar[]) = 1);

    PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassOne"') WHERE 
        trigger_name = '_cm3_card_enforce_fk_MyClassTwo_One' 
        AND trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target' 
        AND trigger_params = ARRAY['"MyClassTwo"','One']::varchar[]) = 1);

    PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassTwo"') WHERE 
        trigger_name = '_cm3_card_enforce_fk_One' 
        AND trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_source' 
        AND trigger_params = ARRAY['One','"MyClassOne"']::varchar[]) = 1);
    PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassTwo"') WHERE 
        trigger_name = '_cm3_card_update_rels_One' 
        AND trigger_function = '_cm3_trigger_card_update_relations' 
        AND trigger_params = ARRAY['One','"Map_MyDomain"','inverse']::varchar[]) = 1);

    PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassOneChild"') WHERE 
        trigger_name = '_cm3_card_enforce_fk_MyClassTwo_One' 
        AND trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_target' 
        AND trigger_params = ARRAY['"MyClassTwo"','One']::varchar[]) = 1);

    PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassTwoChild"') WHERE 
        trigger_name = '_cm3_card_enforce_fk_One' 
        AND trigger_function = '_cm3_trigger_card_enforce_foreign_key_for_source' 
        AND trigger_params = ARRAY['One','"MyClassOne"']::varchar[]) = 1);
    PERFORM assertTrue((SELECT COUNT(*) FROM _cm3_class_triggers_list_detailed('"MyClassTwoChild"') WHERE 
        trigger_name = '_cm3_card_update_rels_One' 
        AND trigger_function = '_cm3_trigger_card_update_relations' 
        AND trigger_params = ARRAY['One','"Map_MyDomain"','inverse']::varchar[]) = 1);

    UPDATE "MyClassTwoChild" SET "One" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A') WHERE "Code" = 'charlie' AND "Status" = 'A';
    
    PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" = 'A') = 1);
    PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE "Status" <> 'A') = 0);
    PERFORM assertTrue((SELECT COUNT(*) FROM "Map_MyDomain" WHERE 
        "IdClass1" =  '"MyClassOneChild"'::regclass
        AND "IdObj1" = (SELECT "Id" FROM "MyClassOne" WHERE "Code" = 'ally' AND "Status" = 'A')
        AND "IdClass2" =  '"MyClassTwoChild"'::regclass
        AND "IdObj2" = (SELECT "Id" FROM "MyClassTwoChild" WHERE "Code" = 'charlie' AND "Status" = 'A')
        AND "Status" = 'A') = 1);

END $$ LANGUAGE PLPGSQL;
