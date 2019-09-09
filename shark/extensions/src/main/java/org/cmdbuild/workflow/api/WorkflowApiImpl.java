package org.cmdbuild.workflow.api;

import org.cmdbuild.workflow.inner.AttributeInfo;
import org.cmdbuild.workflow.commons.fluentapi.ImpersonateApi;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Math.toIntExact;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

import java.util.function.Supplier;
import javax.naming.Context;

import org.cmdbuild.api.fluent.Card;
import org.cmdbuild.api.fluent.CardDescriptor;
import org.cmdbuild.api.fluent.CardDescriptorImpl;
import org.cmdbuild.api.fluent.FluentApi;
import org.cmdbuild.api.fluent.ForwardingFluentApi;
import org.cmdbuild.api.fluent.ws.EntryTypeAttributeImpl;
import org.cmdbuild.common.Constants;
import org.cmdbuild.api.fluent.MailApi;
import org.cmdbuild.services.soap.client.beans.Private;
import org.cmdbuild.workflow.beans.EntryTypeAttribute;
import org.cmdbuild.workflow.type.LookupType;
import org.cmdbuild.workflow.type.ReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.api.fluent.NewMail;

public class WorkflowApiImpl extends ForwardingFluentApi implements WorkflowApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WorkflowApiServicesProvider context;
    private final FluentApi delegate;
    private final Private proxy;
    private final SharkSchemaApi schemaApi;
    private final MailApi mailApi;

    public WorkflowApiImpl(WorkflowApiServicesProvider context) {
        this.context = context;
        this.delegate = context.fluentApi();
        this.proxy = context.proxy();
        this.schemaApi = context.schemaApi();
        this.mailApi = context.mailApi();
        context.callback(this);
    }

    @Override
    protected FluentApi delegate() {
        return delegate;
    }

    @Override
    public ImpersonateApi<WorkflowApi> impersonate() {
        return new ImpersonateApi<WorkflowApi>() {

            private String username;
            private String group;

            @Override
            public ImpersonateApi username(String username) {
                this.username = username;
                return this;
            }

            @Override
            public ImpersonateApi group(String group) {
                this.group = group;
                return this;
            }

            @Override
            public WorkflowApi impersonate() {
                return new WorkflowApiImpl(context.impersonate(username, group));
            }

        };
    }

    @Override
    public Private soap() {
        return proxy;
    }

    @Override
    public ClassInfo findClass(String className) {
        return schemaApi.findClass(className);
    }

    @Override
    public ClassInfo findClass(int classId) {
        return schemaApi.findClass(classId);
    }

    @Override
    public AttributeInfo findAttributeFor(EntryTypeAttribute entryTypeAttribute) {
        return schemaApi.findAttributeFor(entryTypeAttribute);
    }

    @Override
    public LookupType selectLookupById(long id) {
        return (id <= 0) ? new LookupType() : schemaApi.selectLookupById(id);
    }

    @Override
    public LookupType selectLookupByCode(String type, String code) {
        return schemaApi.selectLookupByCode(type, code);
    }

    @Override
    public LookupType selectLookupByDescription(String type, String description) {
        return schemaApi.selectLookupByDescription(type, description);
    }

    @Override
    public NewMail newMail() {
        return mailApi.newMail();
    }

//	@Override
//	public NewMailQueue newMailQueue() {
//		return mailApi.newMailQueue();
//	}
//
//	@Override
//	public SelectFolder selectFolder(String folder) {
//		return mailApi.selectFolder(folder);
//	}
//
//	@Override
//	public SelectMail selectMail(FetchedMail mail) {
//		return mailApi.selectMail(mail);
//	}
//TODO duplicate code with LocalApiImpl, merge code!
    @Override
    public ReferenceType referenceTypeFrom(Card card) {
        return new ReferenceType( //
                toIntExact(card.getId()), //
                Long.valueOf(findClass(card.getClassName()).getId()).intValue(), //
                ofNullable(card.getDescription()).orElseGet(new Supplier<String>() {

                    @Override
                    public String get() {
                        return existingCard(card) //
                                .limitAttributes(Constants.DESCRIPTION_ATTRIBUTE) //
                                .fetch() //
                                .getDescription();
                    }

                }));
    }

    @Override
    public ReferenceType referenceTypeFrom(CardDescriptor cardDescriptor) {
        return referenceTypeFrom(existingCard(cardDescriptor));
    }

    @Override
    public ReferenceType referenceTypeFrom(Object idAsObject) {
        int id = objectToInt(idAsObject);
        return (id <= 0) ? new ReferenceType()
                : referenceTypeFrom( //
                        existingCard(Constants.BASE_CLASS_NAME, id) //
                                .limitAttributes(Constants.DESCRIPTION_ATTRIBUTE) //
                                .fetch());
    }

    private int objectToInt(Object id) {
        int idAsInt;
        if (id instanceof String) {
            idAsInt = Integer.parseInt(String.class.cast(id));
        } else if (id instanceof Number) {
            idAsInt = Number.class.cast(id).intValue();
        } else {
            throw new IllegalArgumentException(format("invalid class '%s' for id", id.getClass()));
        }
        return idAsInt;
    }

    @Override
    public CardDescriptor cardDescriptorFrom(ReferenceType referenceType) {
        ClassInfo classInfo;
        try {
            classInfo = checkNotNull(findClass(referenceType.getIdClass()));
        } catch (Exception ex) {
            logger.warn("class not found for id = {} (trying fallback query on 'Class')", referenceType.getIdClass(), ex);
            ReferenceType fallbackReferenceType = referenceTypeFrom(referenceType.getId());
            classInfo = checkNotNull(findClass(fallbackReferenceType.getIdClass()), "class not found for id = %s", referenceType.getIdClass());
        }
        return new CardDescriptorImpl(classInfo.getName(), referenceType.getId());
    }

    @Override
    public Card cardFrom(ReferenceType referenceType) {
        return existingCard(cardDescriptorFrom(referenceType)).fetch();
    }

    /**
     * @deprecated use an instance of {@link Context} instead.
     */
    @Deprecated
    public static WorkflowApiServicesProvider context(FluentApi fluentApi, Private proxy, SharkSchemaApi schemaApi, MailApi mailApi) {
        return new WorkflowApiServicesProvider() {

            @Override
            public FluentApi fluentApi() {
                return fluentApi;
            }

            @Override
            public Private proxy() {
                return proxy;
            }

            @Override
            public SharkSchemaApi schemaApi() {
                return schemaApi;
            }

            @Override
            public MailApi mailApi() {
                return mailApi;
            }

            @Override
            public void callback(WorkflowApiImpl object) {
                // nothing to do
            }

            @Override
            public WorkflowApiServicesProvider impersonate(String username, String group) {
                throw new UnsupportedOperationException();
            }

        };
    }

}
