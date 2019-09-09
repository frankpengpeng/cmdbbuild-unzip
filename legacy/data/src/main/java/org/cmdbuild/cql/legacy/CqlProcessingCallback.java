/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.cql.legacy;

import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.query.clause.QueryAliasAttribute;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.query.clause.join.Over;
import org.cmdbuild.dao.query.clause.where.WhereClause;

public interface CqlProcessingCallback {

	void from(Classe source);

	void attributes(Iterable<QueryAliasAttribute> attributes);

	void distinct();

	void leftJoin(Classe target, Alias alias, Over over);

	void join(Classe target, Alias alias, Over over);

	void where(WhereClause clause);

}
