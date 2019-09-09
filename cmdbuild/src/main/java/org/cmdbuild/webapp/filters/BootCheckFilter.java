/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.filters;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.eventbus.Subscribe;
import java.io.IOException;
import static java.util.Arrays.stream;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_WFY_PASSTOKEN_HEADER_OR_COOKIE;
import org.cmdbuild.config.CoreConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.cmdbuild.services.SystemReadyEvent;
import org.cmdbuild.system.SystemEventService;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNullOrNull;
import org.cmdbuild.services.SystemService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Configuration("BootCheckFilter")
public class BootCheckFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Holder<byte[]> wfyPageCache;

    private final SystemService bootService;
    private final CoreConfiguration coreConfig;

    private final Object lock = new Object();

    private boolean systemReady = false;

    public BootCheckFilter(CoreConfiguration config, SystemService bootService, SystemEventService systemEventService, CacheService cacheService) {
        this.bootService = checkNotNull(bootService);
        this.coreConfig = checkNotNull(config);
        this.wfyPageCache = cacheService.newHolder("wfy_page");
        systemEventService.getEventBus().register(new Object() {
            @Subscribe
            public void handleSystemReadyEvent(SystemReadyEvent event) {
                synchronized (lock) {
                    logger.info("system running, disable boot services");
                    systemReady = true;
                    lock.notifyAll();
                }
            }
        });
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (systemReady) {
            doFilterAfterBoot(request, response, filterChain);
        } else {
            handleBootServices(request, response, filterChain);
        }
    }

    private void handleBootServices(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        logger.trace("system booting, handle boot services");
        if (getRequestPath(request).matches("/(index|ui|ui_dev|services/rest/v./boot)(/.*)?|")) { //allow only boot services and ui resources
            filterChain.doFilter(request, response);
        } else {
            logger.debug("system boot in progress, stalling request for path = {}", getRequestPath(request));
            if (bootService.isWaitingForUser()) {
                response.setContentType("application/json");
                response.setStatus(503);
                response.getWriter().write(toJson(map("success", false, "messages", list(map("level", "ERROR", "message", "system boot is waiting for operator intervention, unable to process this request", "show_user", true)))));
            } else {
                synchronized (lock) {
                    while (!systemReady) {
                        try {
                            lock.wait();
                        } catch (InterruptedException ex) {
                            throw runtime(ex);
                        }
                    }
                }
                doFilterAfterBoot(request, response, filterChain);
            }
        }
    }

    private void doFilterAfterBoot(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (coreConfig.isWorkingForYouModeEnabled()) {
            handleWfyMode(request, response, filterChain);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void handleWfyMode(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        logger.trace("wfy mode enabled");
        String passToken = firstNotNullOrNull(
                trimToNull(request.getParameter(CMDBUILD_WFY_PASSTOKEN_HEADER_OR_COOKIE)),
                trimToNull(request.getHeader(CMDBUILD_WFY_PASSTOKEN_HEADER_OR_COOKIE)),
                request.getCookies() == null ? null : stream(request.getCookies()).filter((c) -> c.getName().equalsIgnoreCase(CMDBUILD_WFY_PASSTOKEN_HEADER_OR_COOKIE)).collect(toOptional()).map(Cookie::getValue).map(StringUtils::trimToNull).orElse(null));

        boolean hasPassToken;
        if (isNotBlank(passToken)) {
            if (equal(passToken, coreConfig.getWorkingForYouModePasstoken())) {
                hasPassToken = true;
            } else {
                logger.warn("invalid wfy pass token");
                hasPassToken = false;
            }
        } else {
            hasPassToken = false;
        }

        if (hasPassToken) {
            filterChain.doFilter(request, response);
        } else {
            String requestPath = getRequestPath(request);
            if (requestPath.matches("^(/index|/|/ui(/|/index.html)?)?([&#].*)?$")) {
                response.setContentType("text/html");
                response.setStatus(200);
                response.getOutputStream().write(getWfyPageContent());
            } else {
                response.setContentType("application/json");
                response.setStatus(503);
                response.getWriter().write(toJson(map("success", false, "messages", list(map("level", "ERROR", "message", "system maintenance in progress, unable to process this request. Please retry later", "show_user", true)))));
            }
        }
    }

    private byte[] getWfyPageContent() {
        return wfyPageCache.get(() -> toByteArray(getClass().getResourceAsStream("/org/cmdbuild/webapp/wfy.html")));
    }

    private static String getRequestPath(HttpServletRequest request) {
        return request.getServletPath() + nullToEmpty(request.getPathInfo());
    }
}
