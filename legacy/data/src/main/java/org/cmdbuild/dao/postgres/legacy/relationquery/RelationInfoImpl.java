/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.legacy.relationquery;

import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.dao.postgres.legacy.relationquery.RelationInfo;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.query.clause.QueryDomain;
import org.cmdbuild.dao.query.clause.QueryRelation;
import org.joda.time.DateTime;
import org.cmdbuild.dao.beans.Card;

public class RelationInfoImpl implements RelationInfo {

	private final QueryRelation rel;
	private final Card src;
	private final Card dst;

	public RelationInfoImpl(QueryRelation rel, Card src, Card dst) {
		this.rel = rel;
		this.src = src;
		this.dst = dst;
	}

	@Override
	public RelationInfoImpl swapped() {
		return new RelationInfoImpl(rel, dst, src);
	}

	@Override
	public String getSourceDescription() {
		return ObjectUtils.defaultIfNull(src.get(AbstractGetRelation.DESCRIPTION), StringUtils.EMPTY).toString();
	}

	@Override
	public String getSourceCode() {
		return ObjectUtils.defaultIfNull(src.get(AbstractGetRelation.CODE), StringUtils.EMPTY).toString();
	}

	@Override
	public Card getSourceCard() {
		return src;
	}

	@Override
	public Long getSourceId() {
		return src.getId();
	}

	@Override
	public Classe getSourceType() {
		return src.getType();
	}

	@Override
	public String getTargetDescription() {
		return ObjectUtils.defaultIfNull(dst.get(AbstractGetRelation.DESCRIPTION), StringUtils.EMPTY).toString();
	}

	@Override
	public String getTargetCode() {
		return ObjectUtils.defaultIfNull(dst.get(AbstractGetRelation.CODE), StringUtils.EMPTY).toString();
	}

	@Override
	public Card getTargetCard() {
		return dst;
	}

	@Override
	public Long getTargetId() {
		return dst.getId();
	}

	@Override
	public Classe getTargetType() {
		return dst.getType();
	}

	@Override
	public Long getRelationId() {
		return rel.getRelation().getId();
	}

	@Override
	public DateTime getRelationBeginDate() {
		return rel.getRelation().getBeginDate();
	}

	@Override
	public DateTime getRelationEndDate() {
		return rel.getRelation().getEndDate();
	}

	@Override
	public Iterable<Map.Entry<String, Object>> getRelationAttributes() {
		return rel.getRelation().getAttributeValues();
	}

	@Override
	public CMRelation getRelation() {
		return rel.getRelation();
	}

	@Override
	public QueryDomain getQueryDomain() {
		return rel.getQueryDomain();
	}

}
