/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

public interface QueryBuilderService {

	QueryBuilder query();

	default QueryBuilder selectAll() {
		return query().selectAll();
	}

	default QueryBuilder selectCount() {
		return query().selectCount();
	}

	default RowNumberQueryBuilder selectRowNumber() {
		return query().selectRowNumber();
	}

}
