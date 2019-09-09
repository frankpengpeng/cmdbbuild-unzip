package org.cmdbuild.dao.query;

import java.util.Map;

import org.cmdbuild.dao.beans.DBFunctionCallOutput;
import org.cmdbuild.dao.query.clause.QueryRelation;
import org.cmdbuild.dao.query.clause.alias.Alias;

import com.google.common.collect.Maps;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.query.clause.alias.EntryTypeAlias.canonicalAlias;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.beans.DatabaseRecordValues;

/*
 * Note: Mutable classes because it is supposed to be used by driver
 *       implementations.
 *
 * TODO Consider named attributes not bounded to a class or a domain to
 *      allow functions or aliases for other attributes
 */
public class DBQueryRow implements CMQueryRow {

	private Long number;
	private final Map<Alias, Card> cards;
	private final Map<Alias, QueryRelation> relations;
	private final Map<Alias, DBFunctionCallOutput> other;

	// Should we have a reference to the QuerySpecs?
	public DBQueryRow() {
		cards = Maps.newHashMap();
		relations = Maps.newHashMap();
		other = Maps.newHashMap();
	}

	@Override
	public Long getNumber() {
		return number;
	}

	public void setNumber(final Long number) {
		this.number = number;
	}

	public void setCard(final Alias alias, final Card card) {
		cards.put(alias, card);
	}

	public void setRelation(final Alias alias, final QueryRelation relation) {
		relations.put(alias, relation);
	}

	public void setFunctionCallOutput(final Alias alias, final DBFunctionCallOutput functionCallOutput) {
		other.put(alias, functionCallOutput);
	}
	
	@Override
	public boolean hasCard(final Classe type) {
		return hasCard(canonicalAlias(type));
	}

	@Override
	public Card getCard(final Classe type) {
		return getCard(canonicalAlias(type));
	}

	@Override
	public boolean hasCard(final Alias alias) {
		return cards.containsKey(alias);
	}
	
	@Override
	public Card getCard(final Alias alias) {
		if (cards.containsKey(alias)) {
			return cards.get(alias);
		} else {
			throw missingAlias(alias);
		}
	}

	@Override
	public QueryRelation getRelation(final Alias alias) {
		if (relations.containsKey(alias)) {
			return relations.get(alias);
		} else {
			throw missingAlias(alias);
		}
	}

	@Override
	public QueryRelation getRelation(final Domain type) {
		return getRelation(canonicalAlias(type));
	}

	@Override
	public DatabaseRecordValues getValueSet(final Alias alias) {
		if (other.containsKey(alias)) {
			return other.get(alias);
		} else {
			return getEntry(alias);
		}
	}

	private DatabaseRecord getEntry(final Alias alias) {
		if (cards.containsKey(alias)) {
			return cards.get(alias);
		} else if (relations.containsKey(alias)) {
			return relations.get(alias).getRelation();
		} else {
			throw missingAlias(alias);
		}
	}

	private RuntimeException missingAlias(final Alias alias) {
		return new IllegalArgumentException("missing alias " + alias);
	}

}
