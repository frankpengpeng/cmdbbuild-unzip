/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.legacy.relationquery;

import java.util.Map;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.query.clause.QueryDomain;
import org.joda.time.DateTime;
import org.cmdbuild.dao.beans.Card;

public interface RelationInfo {

	RelationInfo swapped();

	String getSourceDescription();

	String getSourceCode();

	Card getSourceCard();

	Long getSourceId();

	Classe getSourceType();

	String getTargetDescription();

	String getTargetCode();

	Card getTargetCard();

	Long getTargetId();

	Classe getTargetType();

	Long getRelationId();

	DateTime getRelationBeginDate();

	DateTime getRelationEndDate();

	Iterable<Map.Entry<String, Object>> getRelationAttributes();

	CMRelation getRelation();

	QueryDomain getQueryDomain();

}
