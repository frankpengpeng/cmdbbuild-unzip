-- fix view format in menu


CREATE OR REPLACE FUNCTION _patch_fix_menu_element(_menu jsonb) RETURNS jsonb AS $$ DECLARE
	_target varchar;
BEGIN
	IF _menu->>'type' = 'view' THEN
		SELECT INTO _target "Name" FROM "_View" WHERE "Id" = (_menu->>'target')::bigint;
		IF _target IS NULL THEN
			RAISE WARNING 'invalid menu element = % with orphan view id = %, set target to null', _menu->>'code', _menu->'target';
		END IF;
		_menu = _menu || jsonb_build_object('target', _target);
	END IF;	
	_menu = _menu || jsonb_build_object('children', (SELECT coalesce(jsonb_agg(z.y),'[]'::jsonb) FROM ( SELECT _patch_fix_menu_element(x) y FROM jsonb_array_elements(_menu->'children') x ) z));
	RETURN _menu;
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_disable('"_Menu"');

UPDATE "_Menu" SET "Data" = _patch_fix_menu_element("Data");

SELECT _cm3_class_triggers_enable('"_Menu"');	

DROP FUNCTION _patch_fix_menu_element(_menu jsonb);

