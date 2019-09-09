package org.cmdbuild.dao.guava;

import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.query.CMQueryRow;
import org.cmdbuild.dao.query.clause.alias.Alias;

import com.google.common.base.Function;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecordValues;

public class Functions {

	public static Function<CMQueryRow, Card> toCard(final Classe type) {
		return new Function<CMQueryRow, Card>() {

			@Override
			public Card apply(final CMQueryRow input) {
				return input.getCard(type);
			}

		};
	}

	public static Function<CMQueryRow, Card> toCard(final Alias alias) {
		return new Function<CMQueryRow, Card>() {

			@Override
			public Card apply(final CMQueryRow input) {
				return input.getCard(alias);
			}

		};
	}
	
	public static Function<CMQueryRow, CMRelation> toRelation(final Domain type) {
		return new Function<CMQueryRow, CMRelation>() {

			@Override
			public CMRelation apply(final CMQueryRow input) {
				return input.getRelation(type).getRelation();
			}

		};
	}

	public static Function<CMQueryRow, CMRelation> toRelation(final Alias alias) {
		return new Function<CMQueryRow, CMRelation>() {

			@Override
			public CMRelation apply(final CMQueryRow input) {
				return input.getRelation(alias).getRelation();
			}

		};
	}

	public static Function<CMQueryRow, DatabaseRecordValues> toValueSet(final Alias alias) {
		return new Function<CMQueryRow, DatabaseRecordValues>() {

			@Override
			public DatabaseRecordValues apply(final CMQueryRow input) {
				return input.getValueSet(alias);
			}

		};
	}

	public static <T> Function<Card, T> toAttribute(final String name, final Class<T> type) {
		return new Function<Card, T>() {

			@Override
			public T apply(final Card input) {
				return input.get(name, type);
			}

		};
	}

	private Functions() {
		// prevents instantiation
	}

}
