package org.cmdbuild.workflow.api;

import org.cmdbuild.workflow.inner.AttributeInfo;
import org.cmdbuild.workflow.type.LookupType;

import com.google.common.collect.ForwardingObject;
import org.cmdbuild.workflow.beans.EntryTypeAttribute;

public abstract class ForwardingSchemaApi extends ForwardingObject implements SharkSchemaApi {

    /**
     * Usable by subclasses only.
     */
    protected ForwardingSchemaApi() {
    }

    @Override
    protected abstract SharkSchemaApi delegate();

    @Override
    public ClassInfo findClass(String className) {
        return delegate().findClass(className);
    }

    @Override
    public ClassInfo findClass(int classId) {
        return delegate().findClass(classId);
    }

    @Override
    public AttributeInfo findAttributeFor(EntryTypeAttribute entryTypeAttribute) {
        return delegate().findAttributeFor(entryTypeAttribute);
    }

    @Override
    public LookupType selectLookupById(long id) {
        return delegate().selectLookupById(id);
    }

    @Override
    public LookupType selectLookupByCode(String type, String code) {
        return delegate().selectLookupByCode(type, code);
    }

    @Override
    public LookupType selectLookupByDescription(String type, String description) {
        return delegate().selectLookupByDescription(type, description);
    }

}
