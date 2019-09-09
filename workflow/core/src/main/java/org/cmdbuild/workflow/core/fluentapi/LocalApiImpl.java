/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.fluentapi;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.api.fluent.Card;
import org.cmdbuild.api.fluent.CardDescriptor;
import org.cmdbuild.api.fluent.ExecutorBasedFluentApi;
import org.cmdbuild.api.fluent.FluentApiExecutor;
import org.cmdbuild.api.fluent.MailApi;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.workflow.beans.EntryTypeAttribute;
import org.cmdbuild.workflow.commons.fluentapi.ImpersonateApi;
import org.cmdbuild.workflow.core.fluentapi.beans.ApiRole;
import org.cmdbuild.workflow.core.fluentapi.beans.ApiUser;
import org.cmdbuild.workflow.inner.AttributeInfo;
import org.cmdbuild.workflow.type.LookupType;
import org.cmdbuild.workflow.type.ReferenceType;
import org.springframework.stereotype.Component;
import org.cmdbuild.api.fluent.NewMail;

@Component
public class LocalApiImpl extends ExecutorBasedFluentApi implements ExtendedApi {

    private final ExtendedApiMethods extendedApi;
    private final OperationUserSupplier user;
    private final RoleRepository roleRepository;
    private final SystemApi systemApi;
    private final MailApi mailApi;

    public LocalApiImpl(OperationUserSupplier user, FluentApiExecutor executor, ExtendedApiMethods extendedApi, RoleRepository roleRepository, SystemApi systemApi, MailApi mailApi) {
        super(executor);
        this.extendedApi = checkNotNull(extendedApi);
        this.user = checkNotNull(user);
        this.roleRepository = checkNotNull(roleRepository);
        this.systemApi = checkNotNull(systemApi);
        this.mailApi = checkNotNull(mailApi);
    }

    @Override
    public NewMail newMail() {
        return mailApi.newMail();
    }

    @Override
    public AttributeInfo findAttributeFor(EntryTypeAttribute entryTypeAttribute) {
        return extendedApi.findAttributeFor(entryTypeAttribute);
    }

    @Override
    public ImpersonateApi<ExtendedApi> impersonate() {
        return extendedApi.impersonate();
    }

    @Override
    public SystemApi getSystemApi() {
        //TODO auth
        return systemApi;
    }

    @Override
    public ApiUser getCurrentUser() {//TODO improve this
        return new ApiUser() {
            @Override
            public String getUsername() {
                return user.getUsername();
            }

            @Override
            public String getEmail() {
                return user.getUser().getLoginUser().getEmail();
            }

        };
    }

    @Override
    public ApiRole getRole(String name) { //TODO improve this
        Role role = roleRepository.getByNameOrId(name);
        return new ApiRole() {
            @Override
            public String getName() {
                return role.getName();
            }

            @Override
            public String getEmail() {
                return role.getEmail();
            }
        };
    }

    @Override
    public ReferenceType referenceTypeFrom(Card card) {
        return extendedApi.referenceTypeFrom(card);
    }

    @Override
    public ReferenceType referenceTypeFrom(CardDescriptor cardDescriptor) {
        return extendedApi.referenceTypeFrom(cardDescriptor);
    }

    @Override
    public ReferenceType referenceTypeFrom(Object idAsObject) {
        return extendedApi.referenceTypeFrom(idAsObject);
    }

    @Override
    public CardDescriptor cardDescriptorFrom(ReferenceType referenceType) {
        return extendedApi.cardDescriptorFrom(referenceType);
    }

    @Override
    public Card cardFrom(ReferenceType referenceType) {
        return extendedApi.cardFrom(referenceType);
    }

    @Override
    public ClassInfo findClass(String className) {
        return extendedApi.findClass(className);
    }

    @Override
    public ClassInfo findClass(int classId) {
        return extendedApi.findClass(classId);
    }

    @Override
    public LookupType selectLookupById(long id) {
        return extendedApi.selectLookupById(id);
    }

    @Override
    public LookupType selectLookupByCode(String type, String code) {
        return extendedApi.selectLookupByCode(type, code);
    }

    @Override
    public LookupType selectLookupByDescription(String type, String description) {
        return extendedApi.selectLookupByDescription(type, description);
    }

}
