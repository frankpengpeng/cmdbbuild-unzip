/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

import java.util.List;

public interface RelationFilterRule {

	String getDomain();

	String getSource();

	String getDestination();

	RelationFilterRuleType getType();

	RelationFilterDirection getDirection();

	List<RelationFilterCardInfo> getCardInfos();

	default boolean isOneOf() {
		return RelationFilterRuleType.ONEOF.equals(getType());
	}

	default boolean isNoone() {
		return RelationFilterRuleType.NOONE.equals(getType());
	}

	enum RelationFilterRuleType {
		NOONE, ONEOF, ANY
	}

	enum RelationFilterDirection {
		_1, _2
	}

}
