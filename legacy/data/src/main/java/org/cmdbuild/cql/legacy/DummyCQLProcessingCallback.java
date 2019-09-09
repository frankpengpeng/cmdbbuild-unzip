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

public class DummyCQLProcessingCallback implements CqlProcessingCallback {

	@Override
	public void from(Classe source) {
	}

	@Override
	public void attributes(Iterable<QueryAliasAttribute> attributes) {
	}

	@Override
	public void distinct() {
	}

	@Override
	public void leftJoin(Classe target, Alias alias, Over over) {
	}

	@Override
	public void join(Classe target, Alias alias, Over over) {
	}

	@Override
	public void where(WhereClause clause) {
	}

}
