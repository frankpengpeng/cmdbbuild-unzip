package org.cmdbuild.api.fluent;

public interface NewRelation extends Relation {

    NewRelation withCard1(String className, long cardId);

    NewRelation withCard2(String className, long cardId);

    void create();

}
