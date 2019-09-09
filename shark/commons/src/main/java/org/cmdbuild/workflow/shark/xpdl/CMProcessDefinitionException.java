package org.cmdbuild.workflow.shark.xpdl;

import org.cmdbuild.workflow.model.WorkflowException;

public class CMProcessDefinitionException extends WorkflowException {

    private static final long serialVersionUID = -780868577745391671L;

    public CMProcessDefinitionException(Throwable nativeException) {
        super(nativeException);
    }

    public CMProcessDefinitionException(String message) {
        super(message);
    }

    public CMProcessDefinitionException(String message, Throwable nativeException) {
        super(nativeException, message);
    }
}
