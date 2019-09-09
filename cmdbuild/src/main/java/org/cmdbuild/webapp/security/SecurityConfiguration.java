/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.security;

import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.cmdbuild.common.error.ErrorAndWarningCollectorService;
import static org.cmdbuild.service.rest.v3.providers.ExceptionHandlerService.buildResponseMessages;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import static org.springframework.web.cors.CorsConfiguration.ALL;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.cmdbuild.services.SystemService;

/**
 *
 *
 * TODO: there is a lot of duplicated configuration code in this class; should
 * refactor to remove duplication.
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//		configuration.setAllowedOrigins(asList("*"));
//		configuration.setAllowedMethods(asList("GET", "POST", "HEAD", "PUT", "DELETE"));
//		configuration.setAllowedHeaders(asList("cmdbuild-authorization", "cmdbuild-localization", "cmdbuild-localized", "content-type", "x-requested-with"));
        configuration.setAllowedOrigins(asList(ALL));
        configuration.setAllowedMethods(asList(ALL));
        configuration.setAllowedHeaders(asList(ALL));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Configuration
    @Order(1)
    public static class BootSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private SystemService bootService;
        @Autowired
        private ErrorAndWarningCollectorService errorAndWarningCollectorService;

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    .antMatcher("/services/rest/v*/boot/**")
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .cors()
                    .and()
                    .csrf().disable() //TODO re-enable and fix this
                    .headers()
                    .frameOptions().sameOrigin()
                    .contentTypeOptions().disable()//this breaks legacy ui; may be removed when legacy ui is removed
                    .and()
                    .authorizeRequests().anyRequest().permitAll().accessDecisionManager(new UnanimousBased(asList(new AccessDecisionVoter() {
                        @Override
                        public boolean supports(ConfigAttribute attribute) {
                            return true;
                        }

                        @Override
                        public boolean supports(Class clazz) {
                            return true;
                        }

                        @Override
                        public int vote(Authentication authentication, Object object, Collection attributes) {
                            return bootService.isSystemReady() ? ACCESS_DENIED : ACCESS_GRANTED;
                        }

                    })))
                    .and()
                    .logout().disable()
                    .exceptionHandling()
                    .authenticationEntryPoint((request, response, e) -> {

                        Map<String, Object> map = map("success", false, "messages", buildResponseMessages(errorAndWarningCollectorService.getCurrentRequestEventCollector().withError(e)));

                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(toJson(map));
                    });
        }

        @Override
        public void configure(WebSecurity webSecurity) throws Exception {
            webSecurity
                    .ignoring()
                    .antMatchers(HttpMethod.GET, "/services/rest/v*/boot/status", "/services/rest/v*/boot/status/");
        }
    }

    @Configuration
    @Order(2)
    public static class WsSecurityConfigForRestSession extends WebSecurityConfigurerAdapter {

        @Autowired
        private LenientSessionTokenFilter authenticationFilter;// use lenient filter, so an user can use sessions ws even if it does not have a default group set
        @Autowired
        private ErrorAndWarningCollectorService errorAndWarningCollectorService;

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    .antMatcher("/services/rest/v*/sessions/**") //TODO cleanup this code; we actually need to intercept only PUT on session/{mysession} for session update on login, when the session is still invalid, and POST on sessions for session create
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .cors()
                    .and()
                    .csrf().disable() //TODO re-enable and fix this
                    .headers()
                    .frameOptions().sameOrigin()
                    .contentTypeOptions().disable()//this breaks legacy ui; may be removed when legacy ui is removed
                    .and()
                    .addFilterBefore(authenticationFilter, BasicAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/services/rest/v*/sessions", "/services/rest/v*/sessions/").permitAll() //rest ws login; FIXME: the double syntax 'sessions','sessions/' is a workaround, maybe there is a better way :/
                    .antMatchers(HttpMethod.GET, "/services/rest/v*/sessions/challenge").permitAll() //challenge for challenge-response rsa auth
                    .anyRequest().authenticated()
                    .and()
                    .logout().disable()
                    .exceptionHandling()
                    .authenticationEntryPoint((request, response, e) -> {

                        Map<String, Object> map = map("success", false, "messages", buildResponseMessages(errorAndWarningCollectorService.getCurrentRequestEventCollector().withError(e)));

                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(toJson(map));
                    });
        }
    }

    @Configuration
    @Order(3)
    public static class WsSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private SessionTokenFilter authenticationFilter;
        @Autowired
        private ErrorAndWarningCollectorService errorAndWarningCollectorService;

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    .antMatcher("/services/**")
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .cors()
                    .and()
                    .csrf().disable() //TODO re-enable and fix this
                    .headers()
                    .frameOptions().sameOrigin()
                    .contentTypeOptions().disable()//this breaks legacy ui; may be removed when legacy ui is removed
                    .and()
                    .addFilterBefore(authenticationFilter, BasicAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/services/rest/v*/sessions", "/services/rest/v*/sessions/").permitAll() // rest ws login; FIXME: the double syntax 'sessions','sessions/' is a workaround, maybe there is a better way :/
                    .antMatchers("/services/rest/v*/configuration/languages", "/services/rest/v*/configuration/languages/").permitAll()
                    .antMatchers("/services/rest/v*/configuration/public", "/services/rest/v*/configuration/public/").permitAll()
                    .antMatchers("/services/rest/v*/resources/company_logo/**").permitAll()//company logo; TODO: replace with public resources ws
                    .antMatchers("/services/rest/v*/**").authenticated() // rest ws
                    .antMatchers("/services/geoserver/**").authenticated() // geoserver proxy
                    .antMatchers("/services/bimserver/**").authenticated() // bimserver proxy
                    .antMatchers("/services/websocket/v1/main").authenticated() // websocket endpoint

                    .antMatchers("/services/soap/Private").permitAll() //legacy soap services (TODO: secure where required)

                    .anyRequest().denyAll() //default deny

                    .and()
                    .logout().disable()
                    .exceptionHandling()
                    .authenticationEntryPoint((request, response, e) -> {

                        Map<String, Object> map = map("success", false, "messages", buildResponseMessages(errorAndWarningCollectorService.getCurrentRequestEventCollector().withError(e)));

                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(toJson(map));
                    });
        }
    }

    /**
     * implement web security; main difference is that it handles a 'login'
     * page, and redirect unauthenticated requests to login page
     */
    @Configuration
    @Order(4)
    public static class UiSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private UiSessionTokenFilter authenticationFilter;
//		@Autowired
//		private ErrorAndWarningCollectorService errorAndWarningCollectorService;

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .csrf().disable() //TODO re-enable and fix this
                    .headers()
                    .frameOptions().sameOrigin()
                    .contentTypeOptions().disable()//this breaks legacy ui; may be removed when legacy ui is removed
                    .and()
                    .addFilterBefore(authenticationFilter, BasicAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers("/ui/", "/ui/**", "/ui_dev/**", "/ui_dev/").permitAll() //ui

                    .antMatchers("/", "/index").permitAll()
                    .anyRequest().denyAll(); //default deny

//					.and()
//					.exceptionHandling()
//					.authenticationEntryPoint((request, response, e) -> {
//
//						Map<String, Object> map = Maps.newLinkedHashMap();
//						map.put("success", false);
//						map.put("message", errorAndWarningCollectorService.getEventCollectorForThisThreadSafe().withError(e).getMessage()); 
//
//						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//						response.setContentType("application/json");
//						response.setCharacterEncoding("UTF-8");
//						gson.toJson(map, response.getWriter());
//					});
        }
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
    }

}
