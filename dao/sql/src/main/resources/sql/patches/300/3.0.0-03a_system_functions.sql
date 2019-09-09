-- mixed system access function


--- PLACEHOLDERS (db objects that are required here to create functions, but will be replaced in other patches later) ---

CREATE TABLE "SimpleClass" (id int);


-- legacy functions cleanup
 
DROP FUNCTION IF EXISTS _cm3_function_list(
		OUT function_name text,
		OUT function_id oid,
		OUT arg_io char[],
		OUT arg_names text[],
		OUT arg_types text[],
		OUT returns_set boolean,
		OUT comment text,
		OUT metadata jsonb
	);

