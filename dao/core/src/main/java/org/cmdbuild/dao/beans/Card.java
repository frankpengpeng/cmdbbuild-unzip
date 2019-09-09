package org.cmdbuild.dao.beans;

import javax.annotation.Nullable;
import org.cmdbuild.dao.entrytype.Classe;

public interface Card extends DatabaseRecord, CardIdAndClassName, IdAndDescription {

    @Override
    Classe getType();

    @Override
    String getCode();

    @Override
    String getDescription();

    default boolean hasAttribute(String key) {
        return getType().hasAttribute(key);
    }

    default boolean hasId() {
        return true;//TODO implement everywhere, remove default
    }

    @Nullable
    default Long getIdOrNull() {
        return hasId() ? getId() : null;
    }

    @Override
    default String getClassName() {
        return getType().getName();
    }

    default CardStatus getCardStatus() {//TODO improve this //TODO implement everywhere, remove default
        if (getEndDate() == null) {
            return CardStatus.A;
        } else {
            return CardStatus.U;
        }
    }

    default boolean isProcess() {
        return getType().isProcess();
    }

    enum CardStatus {
        A, N, U
    }

}
