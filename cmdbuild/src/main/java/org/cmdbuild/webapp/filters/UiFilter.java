package org.cmdbuild.webapp.filters;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import java.io.IOException;
import static java.lang.String.format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.Charsets;
import static org.cmdbuild.config.UiConfigurationImpl.AUTO_URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.UiFilterConfiguration;

/**
 * this filters is used to embed global configuration inside main javascript
 * file; it is mostly used to set the 'baseUrl' param, that javascript code will
 * use to call rest ws.
 * <br><br>
 * TODO: cache response (or put this whole filter behind a caching filter)
 */
@Component
public class UiFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UiFilterConfiguration config;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //do nothing; this init method is not invoked by spring configured filters
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        logger.debug("ui filter doFilter BEGIN");
        try {
            String resourceRequestUrl = ((HttpServletRequest) request).getRequestURL().toString();

            String uiManifest = checkNotBlank(config.getUiManifest(), "missing config ui manifest");

            String baseRequestUrl;
            if (equal(config.getBaseUrl(), AUTO_URL)) {
                String uiFilterUrlPattern = "^(.*)/(ui|ui_dev)(/.*)?$";
                Matcher matcher = Pattern.compile(uiFilterUrlPattern).matcher(resourceRequestUrl);
                checkArgument(matcher.find(), "resource request url = %s does not match this filter url pattern = %s", resourceRequestUrl, uiFilterUrlPattern);
                baseRequestUrl = checkNotBlank(matcher.group(1), "request url parsing failed, using pattern = '%s'", uiFilterUrlPattern);
            } else {
                baseRequestUrl = checkNotBlank(config.getBaseUrl().replaceFirst("/$", ""));
            }
            String content = format("window.cmdbuildConfig=%s;", toJson(map(
                    "baseUrl", baseRequestUrl + "/services/rest/v3",
                    "geoserverBaseUrl", baseRequestUrl + "/services/geoserver",
                    "bimserverBaseUrl", baseRequestUrl + "/services/bimserver",
                    "socketUrl", baseRequestUrl.replaceFirst("http", "ws") + "/services/websocket/v1/main",
                    "manifest", uiManifest
            )));
            logger.debug("return ui config = {}", content);
            byte[] responseData = content.getBytes(Charsets.UTF_8);
            response.setContentType("application/javascript");
            response.setContentLength(responseData.length);
            response.getOutputStream().write(responseData);

            logger.debug("ui filter doFilter END");
        } catch (Exception ex) {
            logger.error("error in ui filter", ex);
            throw ex;
        }
    }

}
