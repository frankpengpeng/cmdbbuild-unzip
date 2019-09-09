/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm;

import com.google.common.base.Function;
import java.sql.ResultSet;
import javax.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import org.cmdbuild.dao.beans.CardDefinition;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;

public interface CardMapper<T, B extends Builder<T, B>> {

	Class<T> getTargetClass();

	String getClassId();

	boolean isPrimaryMapper();

	@Nullable
	Long getCardId(T object);

	@Deprecated
	CardDefinition objectToCard(CardDefinition cardDefinition, T object);

	CardImpl.CardImplBuilder objectToCard(CardImpl.CardImplBuilder builder, T object);

	B cardToObject(Card card);

	B dataToObject(Function<String, Object> dataSource);

	B sqlToObject(ResultSet resultSet);

	Class<B> getBuilderClass();

}
