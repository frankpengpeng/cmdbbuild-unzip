package org.cmdbuild.api.fluent;

public interface ExistingProcessInstance extends Card {

    ExistingProcessInstance withProcessInstanceId(String value);

    ExistingProcessInstance withDescription(String value);

    ExistingProcessInstance with(String name, Object value);

    ExistingProcessInstance withAttribute(String name, Object value);

    void update();

    void advance();

    void suspend();

    void resume();

    Attachments attachments();

    void abort();

}
