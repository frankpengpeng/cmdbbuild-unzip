/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.requestcontext;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public interface RequestContextService {

    default <T> RequestContextHolder<T> createRequestContextHolder() {
        return createRequestContextHolder(Suppliers.ofInstance(null));
    }

    <T> RequestContextHolder<T> createRequestContextHolder(Supplier<T> initialValueSupplier);

    RequestContext getRequestContext();

    void initCurrentRequestContext(String identifier);

    void destroyCurrentRequestContext();

    default String getRequestContextId() {
        return getRequestContext().getId();
    }

}
