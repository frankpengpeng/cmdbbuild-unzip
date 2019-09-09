package org.cmdbuild.api.fluent;

public class RelationImpl implements Relation {

    private final String domainName;
    private CardDescriptor card1;
    private CardDescriptor card2;

    public RelationImpl(String domainName) {
        this.domainName = domainName;
    }

    public RelationImpl(String domainName, CardDescriptor card1, CardDescriptor card2) {
        this.domainName = domainName;
        this.card1 = card1;
        this.card2 = card2;
    }

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public String getClassName1() {
        return card1.getClassName();
    }

    @Override
    public long getCardId1() {
        return card1.getId();
    }

    @Override
    public RelationImpl setCard1(String className, long id) {
        this.card1 = new CardDescriptorImpl(className, id);
        return this;
    }

    @Override
    public String getClassName2() {
        return card2.getClassName();
    }

    @Override
    public long getCardId2() {
        return card2.getId();
    }

    @Override
    public RelationImpl setCard2(String className, long id) {
        this.card2 = new CardDescriptorImpl(className, id);
        return this;
    }

}
