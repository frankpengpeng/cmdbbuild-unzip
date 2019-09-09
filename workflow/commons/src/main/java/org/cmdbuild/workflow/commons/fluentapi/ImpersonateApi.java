package org.cmdbuild.workflow.commons.fluentapi;

public interface ImpersonateApi<T> {

    ImpersonateApi username(String username);

    ImpersonateApi group(String group);

    T impersonate();

}
