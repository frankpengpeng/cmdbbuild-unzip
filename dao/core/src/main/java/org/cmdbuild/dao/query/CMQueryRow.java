package org.cmdbuild.dao.query;

import org.cmdbuild.dao.query.clause.QueryRelation;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecordValues;

/**
 * Immutable interface to mask result object building.
 */
public interface CMQueryRow {

	/**
	 * Gets the row number.
	 * 
	 * @return the row number if {@link QuerySpecs#numbered()} has been
	 *         specified, {@code null} otherwise.
	 */
	Long getNumber();

	DatabaseRecordValues getValueSet(Alias alias);

	boolean hasCard(Alias alias);

	Card getCard(Alias alias);

	boolean hasCard(Classe type);

	Card getCard(Classe type);

	QueryRelation getRelation(Alias alias);

	QueryRelation getRelation(Domain type);

}
