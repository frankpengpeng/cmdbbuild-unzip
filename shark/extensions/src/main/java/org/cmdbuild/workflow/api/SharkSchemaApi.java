package org.cmdbuild.workflow.api;

import org.cmdbuild.workflow.inner.AttributeInfo;
import java.util.Optional;

import org.cmdbuild.api.fluent.ws.WsFluentApiExecutor.WsType;

import com.google.common.collect.ForwardingObject;
import org.cmdbuild.workflow.inner.SchemaApiForWorkflowExt;

/**
 * API to query the database structure.
 */
public interface SharkSchemaApi extends SchemaApiForWorkflowExt {

    abstract class ForwardingAttributeInfo extends ForwardingObject implements AttributeInfo {

        /**
         * Usable by subclasses only.
         */
        protected ForwardingAttributeInfo() {
        }

        @Override
        protected abstract AttributeInfo delegate();

        @Override
        public String getName() {
            return delegate().getName();
        }

        @Override
        public WsType getWsType() {
            return (WsType) delegate().getWsType();
        }

        @Override
        public Optional<String> getTargetClassName() {
            return delegate().getTargetClassName();
        }

    }
}
