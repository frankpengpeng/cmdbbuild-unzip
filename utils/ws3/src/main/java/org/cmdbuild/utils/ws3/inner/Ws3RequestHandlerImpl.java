/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import static java.util.stream.Collectors.toList;
import javax.activation.DataHandler;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.MediaType.TEXT_XML;
import org.apache.commons.lang3.StringUtils;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.cmdbuild.utils.ws3.api.Ws3Method;
import org.cmdbuild.utils.ws3.api.Ws3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

public class Ws3RequestHandlerImpl implements Ws3RequestHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, MethodHanlder> handlers = map();

    public Ws3RequestHandlerImpl(Collection<Object> services) {
        services.forEach(this::addHandlersFromServiceBean);
    }

    @Override
    public DataHandler handleRequest(Ws3Request request) {
        MethodHanlder handler = checkNotNull(handlers.get(key(request.getService(), request.getMethod())), "ws3 handler not found for service =< %s > and method =< %s >", request.getService(), request.getMethod());
        return handler.handleRequest(request);
    }

    private void addHandlersFromServiceBean(Object service) {
        list(service.getClass().getMethods()).stream().filter(m -> m.isAnnotationPresent(Ws3Method.class)).forEach(m -> addHandlersFromMethod(service, m));
    }

    private void addHandlersFromMethod(Object service, Method method) {
        Ws3Method annotation = checkNotNull(method.getAnnotation(Ws3Method.class));
        String serviceName = getServicename(service),
                methodName = firstNotBlank(annotation.value(), method.getName());
        handlers.put(key(serviceName, methodName), new MethodHanlder(service, method));
    }

    private String getServicename(Object service) {
        List<Supplier<String>> names = list(() -> {

            if (service.getClass().isAnnotationPresent(Component.class)) {
                return service.getClass().getAnnotation(Component.class).value();
            } else {
                return null;
            }
        }, () -> {

            if (service.getClass().isAnnotationPresent(Ws3Service.class)) {
                return service.getClass().getAnnotation(Ws3Service.class).value();
            } else {
                return null;
            }
        }, () -> StringUtils.uncapitalize(service.getClass().getName()));
        return names.stream().map(Supplier::get).filter(StringUtils::isNotBlank).findFirst().get();
    }

    private class MethodHanlder {

        private final Object service;
        private final Method method;

        private final List<Function<Ws3Request, Object>> methodArgMappers;
        private final Function<Object, DataHandler> responseMapper;

        public MethodHanlder(Object service, Method method) {
            this.service = checkNotNull(service);
            this.method = checkNotNull(method);

            methodArgMappers = list(method.getParameters()).stream().map(this::prepareHandlerForMethodParam).collect(toImmutableList());
            responseMapper = buildResponseMapper();
        }

        public DataHandler handleRequest(Ws3Request request) {
            try {
                Object response = method.invoke(service, buildMethodArgs(request));
                return responseToDataHandler(response);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw runtime(ex);
            }
        }

        private Object[] buildMethodArgs(Ws3Request request) {
            return methodArgMappers.stream().map(f -> f.apply(request)).collect(toList()).toArray();
        }

        private DataHandler responseToDataHandler(Object response) {
            return responseMapper.apply(response);
        }

        private Function<Ws3Request, Object> prepareHandlerForMethodParam(Parameter param) {
            String name = checkNotBlank(param.getAnnotation(QueryParam.class).value());
            Type type = param.getParameterizedType();
            return (r) -> {
                logger.trace("processing request param = {} with type = {}", name, type);
                Object value = r.getParam(name);
                logger.trace("raw value =< {} >", value);
                value = convert(value, type);
                logger.trace("converted value =< {} >", value);
                return value;
            };
        }

        private Function<Object, DataHandler> buildResponseMapper() {
            if (DataHandler.class.isAssignableFrom(method.getReturnType())) {
                return (Function) Function.identity();
            } else {
                String contentType;
                if (method.isAnnotationPresent(Produces.class)) {
                    contentType = checkNotBlank(getOnlyElement(list(method.getAnnotation(Produces.class).value())));
                } else if (service.getClass().isAnnotationPresent(Produces.class)) {
                    contentType = checkNotBlank(getOnlyElement(list(service.getClass().getAnnotation(Produces.class).value())));
                } else {
                    contentType = APPLICATION_JSON;
                }
                switch (contentType) {
                    case APPLICATION_JSON:
                        return this::serializeJson;
                    case TEXT_PLAIN:
                    case TEXT_XML:
                    case TEXT_HTML:
                        return (x) -> serializeText(contentType, x);
                    default:
                        return (x) -> serializeBinary(contentType, x);
                }
            }
        }

        private DataHandler serializeJson(Object value) {
            String json;
            if (value instanceof String) {
                json = (String) value;
            } else {
                json = toJson(value);
            }
            return newDataHandler(json, APPLICATION_JSON);
        }

        private DataHandler serializeText(String contentType, Object value) {
            String text = toStringOrEmpty(value);
            return newDataHandler(text, contentType);
        }

        private DataHandler serializeBinary(String contentType, Object value) {
            byte[] data = convert(value, byte[].class);
            return newDataHandler(data, contentType);
        }
    }

}
