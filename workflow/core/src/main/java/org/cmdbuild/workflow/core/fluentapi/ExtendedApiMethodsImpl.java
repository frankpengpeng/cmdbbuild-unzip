/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.fluentapi;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Math.toIntExact;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import javax.annotation.Nullable;
import org.cmdbuild.api.fluent.Card;
import org.cmdbuild.api.fluent.CardDescriptor;
import org.cmdbuild.api.fluent.CardDescriptorImpl;
import org.cmdbuild.api.fluent.ExecutorBasedFluentApi;
import org.cmdbuild.api.fluent.FluentApi;
import org.cmdbuild.api.fluent.FluentApiExecutor;
import org.cmdbuild.common.Constants;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.lookup.Lookup;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.utils.lang.CmConvertUtils;
import org.cmdbuild.workflow.type.LookupType;
import org.cmdbuild.workflow.type.ReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.view.DataView;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import org.cmdbuild.workflow.beans.EntryTypeAttribute;
import org.cmdbuild.workflow.commons.fluentapi.ImpersonateApi;
import org.cmdbuild.workflow.inner.AttributeInfo;
import static org.cmdbuild.workflow.type.utils.WorkflowTypeUtils.emptyToNull;
import org.springframework.context.annotation.Primary;
import org.cmdbuild.api.fluent.ws.AttrTypeVisitor;
import org.cmdbuild.api.fluent.ws.ClassAttribute;
import org.cmdbuild.api.fluent.ws.EntryTypeAttributeImpl;
import org.cmdbuild.api.fluent.ws.FunctionInput;
import org.cmdbuild.api.fluent.ws.FunctionOutput;
import org.cmdbuild.api.fluent.ws.WsFluentApiExecutor.WsType;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.function.StoredFunctionOutputParameter;
import org.cmdbuild.dao.function.StoredFunctionParameter;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;

@Component
@Primary
public class ExtendedApiMethodsImpl implements ExtendedApiMethods {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LookupService lookupService;
    private final FluentApi fluentApi;
    private final DataView dataView;
    private final DaoService dao;
    private final ApiImpersonateHelper impersonateHelper;

    public ExtendedApiMethodsImpl(FluentApiExecutor executor, LookupService lookupService, DataView dataView, ApiImpersonateHelper impersonateHelper, DaoService dao) {
        fluentApi = new ExecutorBasedFluentApi(executor);
        this.lookupService = checkNotNull(lookupService);
        this.dataView = checkNotNull(dataView);
        this.impersonateHelper = checkNotNull(impersonateHelper);
        this.dao = checkNotNull(dao);
    }

    @Override
    public AttributeInfo findAttributeFor(EntryTypeAttribute entryTypeAttribute) {//TODO check and improve this

        return new AttrTypeVisitor() {

            private String entryName;
            private AttributeInfo attributeInfo;

            public AttributeInfo attributeInfo() {
                ((EntryTypeAttributeImpl) entryTypeAttribute).accept(this);//TODO remove cast
                return (attributeInfo == null) ? unknownAttributeInfo(entryName) : attributeInfo;
            }

            private AttributeInfo unknownAttributeInfo(final String entryName) {
                return new AttributeInfo() {

                    @Override
                    public String getName() {
                        return entryName;
                    }

                    @Override
                    public WsType getWsType() {
                        return WsType.UNKNOWN;
                    }

                    @Override
                    public Optional<String> getTargetClassName() {
                        return Optional.empty();
                    }

                };
            }

            @Override
            public void visit(ClassAttribute classAttribute) {
                entryName = classAttribute.getClassName();
                Attribute attribute = dao.getClasse(entryName).getAttributeOrNull(classAttribute.getAttributeName());
                if (attribute != null) {
                    attributeInfo = new AttributeInfo() {

                        @Override
                        public String getName() {
                            return attribute.getName();
                        }

                        @Override
                        public WsType getWsType() {
                            return parseEnum(attribute.getType().getName().name(), WsType.class);
                        }

                        @Override
                        public Optional<String> getTargetClassName() {
                            return Optional.of(attribute.getOwner().getName());
                        }

                    };
                }
            }

            @Override
            public void visit(FunctionInput functionInput) {
                entryName = functionInput.getFunctionName();
                StoredFunctionParameter attribute = dao.getFunctionByName(entryName).getInputParameter(functionInput.getAttributeName());
                attributeInfo = new AttributeInfo() {

                    @Override
                    public String getName() {
                        return attribute.getName();
                    }

                    @Override
                    public WsType getWsType() {
                        return parseEnum(attribute.getType().getName().name(), WsType.class);
                    }

                    @Override
                    public Optional<String> getTargetClassName() {
                        return Optional.empty();
                    }

                };
            }

            @Override
            public void visit(FunctionOutput functionOutput) {
                entryName = functionOutput.getFunctionName();
                StoredFunctionOutputParameter attribute = dao.getFunctionByName(entryName).getOutputParameter(functionOutput.getAttributeName());
                attributeInfo = new AttributeInfo() {

                    @Override
                    public String getName() {
                        return attribute.getName();
                    }

                    @Override
                    public WsType getWsType() {
                        return parseEnum(attribute.getType().getName().name(), WsType.class);
                    }

                    @Override
                    public Optional<String> getTargetClassName() {
                        return Optional.empty();
                    }

                };
            }

        }.attributeInfo();
    }

    @Override
    public ClassInfo findClass(String className) {
        return toClassInfo(dataView.getClasse(className));
    }

    @Override
    public ClassInfo findClass(int oid) {
        return toClassInfo(dataView.getClass(oid));
    }

    @Override
    public LookupType selectLookupById(long id) {
        return convertLookup(lookupService.getLookup(id));
    }

    @Override
    public LookupType selectLookupByCode(String type, String code) {
        return convertLookup(lookupService.getLookupByTypeAndCode(type, code));
    }

    @Override
    public LookupType selectLookupByDescription(String type, String description) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CardDescriptor cardDescriptorFrom(ReferenceType referenceType) {
        checkNotNull(emptyToNull(referenceType), "reference type param is null");
        ClassInfo classInfo;
        try {
            classInfo = checkNotNull(findClass(referenceType.getClassName()));
        } catch (Exception ex) {
            logger.warn("class not found for name = {} (trying fallback query on 'Class'): {}", referenceType.getClassName(), ex);
            ReferenceType fallbackReferenceType = referenceTypeFrom(referenceType.getId());
            classInfo = checkNotNull(findClass(fallbackReferenceType.getClassName()), "class not found for id = %s", referenceType.getClassName());
        }
        return new CardDescriptorImpl(classInfo.getName(), referenceType.getId());
    }

    @Override
    public Card cardFrom(ReferenceType referenceType) {
        return fluentApi.existingCard(cardDescriptorFrom(referenceType)).fetch();
    }

    @Override
    public ReferenceType referenceTypeFrom(Card card) {
        return new ReferenceType(card.getClassName(), card.getId(), ofNullable(card.getDescription()).orElseGet(() -> fluentApi.existingCard(card).limitAttributes(Constants.DESCRIPTION_ATTRIBUTE).fetch().getDescription()), card.getCode());//TODO get code
    }

    @Override
    public ReferenceType referenceTypeFrom(Object idAsObject) {
        Long id = CmConvertUtils.convert(idAsObject, Long.class);
        if (isNotNullAndGtZero(id)) {
            return referenceTypeFrom(fluentApi.existingCard(Constants.BASE_CLASS_NAME, id).limitAttributes(Constants.DESCRIPTION_ATTRIBUTE).fetch());
        } else {
            return new ReferenceType();
        }
    }

    @Override
    public ReferenceType referenceTypeFrom(CardDescriptor cardDescriptor) {
        return referenceTypeFrom(fluentApi.existingCard(cardDescriptor));
    }

    @Override
    public ImpersonateApi<ExtendedApi> impersonate() {
        return new ImpersonateApiHelper();
    }

    private ClassInfo toClassInfo(Classe classe) {
        return new ClassInfo(classe.getName(), classe.getOid());
    }

    private @Nullable
    LookupType convertLookup(@Nullable Lookup in) {
        if (in == null) {
            return null;
        } else {
            LookupType out = new LookupType();
            out.setType(in.getType().getName());
            out.setId(toIntExact(in.getId()));
            out.setCode(in.getCode());
            out.setDescription(in.getDescription());
            return out;
        }
    }

    private class ImpersonateApiHelper implements ImpersonateApi<ExtendedApi> {

        private String username, group;

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
        public ExtendedApi impersonate() {
            return impersonateHelper.buildImpersonateApiWrapper(username, group);
        }
    }
}
