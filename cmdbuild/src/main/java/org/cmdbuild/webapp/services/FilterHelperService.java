/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.services;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import javax.servlet.http.HttpServletRequest;
import org.cmdbuild.config.UiFilterConfiguration;
import static org.cmdbuild.config.UiFilterConfiguration.AUTO_URL;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
public class FilterHelperService {

    private final UiFilterConfiguration config;

    public FilterHelperService(UiFilterConfiguration config) {
        this.config = checkNotNull(config);
    }

    public String getLoginRedirectUrl(HttpServletRequest request) {
        String baseRequestUrl;
        if (equal(config.getBaseUrl(), AUTO_URL)) {
            baseRequestUrl = request.getContextPath();
        } else {
            baseRequestUrl = checkNotBlank(config.getBaseUrl().replaceFirst("/$", ""));
        }
        return format("%s/ui/#login", baseRequestUrl);
    }
}
