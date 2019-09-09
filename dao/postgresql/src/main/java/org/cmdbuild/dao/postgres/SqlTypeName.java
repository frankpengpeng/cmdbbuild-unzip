package org.cmdbuild.dao.postgres;

/**
 * Missing DAO types: Lookup, Reference, ForeignKey
 *
 * Missing SQL types: POINT, LINESTRING, POLYGON (use sqlToJavaValue)
 *
 * Not used: regclass, bytea, _int4, _varchar
 */
public enum SqlTypeName {

	bool,
	date,
	float8,
	inet,
	int4,
	int8,
	_int8,
	numeric,
	regclass,
	text,
	time,
	timestamp,
	timestamptz,
	varchar,
	_varchar,
	jsonb,
	bpchar,
	bytea,
	_bytea,
	undefined;

}
