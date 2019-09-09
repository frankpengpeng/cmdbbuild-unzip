/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableMap;
import java.util.Map;
import javax.annotation.Nullable;

public class SimpleCard implements Card {

	private final String cardId, classId;
	private final Map<String, Object> attributes;

	public SimpleCard(String classId, String cardId, Map<String, ? extends Object> attributes) {
		this.cardId = cardId;
		this.classId = classId;
		this.attributes = unmodifiableMap(checkNotNull(attributes));
	}
//
//	public SimpleCard(String cardId, Map<String, ? extends Object> attributes) {
//		this(null, cardId, attributes);
//	}

	public SimpleCard(Map<String, ? extends Object> attributes) {
		this(null, null, attributes);
	}

	@Override
	public @Nullable
	String getCardId() {
		return cardId;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String toString() {
		return "SimpleCard{" + "cardId=" + cardId + ", classId=" + classId + ", attributes=" + attributes + '}';
	}

	@Override
	public String getClassId() {
		return classId;
	}

}
