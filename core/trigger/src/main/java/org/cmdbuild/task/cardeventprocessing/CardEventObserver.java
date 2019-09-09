package org.cmdbuild.task.cardeventprocessing;

import org.cmdbuild.dao.beans.Card;

@Deprecated
public interface CardEventObserver {

	void afterCreate(Card current);

	void beforeUpdate(Card current, Card next);

	void afterUpdate(Card previous, Card current);

	void beforeDelete(Card current);

}