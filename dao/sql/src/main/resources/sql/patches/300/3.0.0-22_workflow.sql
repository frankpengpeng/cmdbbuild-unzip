-- upgrade workflow

DO $$ DECLARE
	_new_provider varchar;
	_process regclass;
	_has_instances boolean;
BEGIN
	FOR _process IN SELECT x FROM _cm3_process_list() x WHERE NOT _cm3_class_is_superclass(x) LOOP
		EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s)',_process) INTO _has_instances;
		IF _cm3_class_metadata_get(_process,'cm_workflow_provider') IS NULL THEN
			IF _has_instances THEN _new_provider = 'shark'; ELSE _new_provider = 'river'; END IF;
			PERFORM _cm3_class_metadata_set(_process,'cm_workflow_provider',_new_provider);
		END IF;
	END LOOP;
END $$ LANGUAGE PLPGSQL;

