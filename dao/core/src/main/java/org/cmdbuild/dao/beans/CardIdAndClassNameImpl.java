/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import java.util.Objects;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

public class CardIdAndClassNameImpl implements CardIdAndClassName {

    private final String className;
    private final Long cardId;

    public CardIdAndClassNameImpl(String className, Long cardId) {
        this.className = checkNotBlank(className);
        this.cardId = checkNotNullAndGtZero(cardId);
    }

    @Override
    public Long getId() {
        return cardId;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return "CardIdAndClassName{" + "className=" + className + ", cardId=" + cardId + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.cardId);
        hash = 67 * hash + Objects.hashCode(this.className);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CardIdAndClassName)) {
            return false;
        }
        final CardIdAndClassName other = (CardIdAndClassName) obj;
        if (!Objects.equals(this.className, other.getClassName())) {
            return false;
        }
        if (!Objects.equals(this.cardId, other.getId())) {
            return false;
        }
        return true;
    }

    public static CardIdAndClassNameImpl copyOf(CardIdAndClassName card) {
        return new CardIdAndClassNameImpl(card.getClassName(), card.getId());
    }

    public static CardIdAndClassNameImpl card(String className, Long cardId) {
        return new CardIdAndClassNameImpl(className, cardId);
    }

}
