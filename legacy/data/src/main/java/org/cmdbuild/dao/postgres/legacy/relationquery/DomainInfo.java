/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.legacy.relationquery;

import org.cmdbuild.dao.query.clause.QueryDomain;

public interface DomainInfo extends Iterable<RelationInfo> {

	QueryDomain getQueryDomain();

	String getDescription();

}
