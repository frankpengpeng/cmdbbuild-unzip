package org.cmdbuild.api.fluent;

public class NewCardImpl extends AbstractActiveCard implements NewCard {

    public NewCardImpl(FluentApiExecutor executor, String className) {
        super(executor, className, null);
    }

    public NewCardImpl withCode(String value) {
        super.setCode(value);
        return this;
    }

    public NewCardImpl withDescription(String value) {
        super.setDescription(value);
        return this;
    }

    public NewCardImpl with(String name, Object value) {
        return withAttribute(name, value);
    }

    public NewCardImpl withAttribute(String name, Object value) {
        super.set(name, value);
        return this;
    }

    public CardDescriptor create() {
        return executor().create(this);
    }

}
