/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.template;

import com.google.common.base.Function;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.transformValues;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import org.cmdbuild.api.fluent.CmApiService;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserRepository;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import static org.cmdbuild.cql.CqlUtils.getCqlSelectElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.IdAndDescription;
import org.cmdbuild.dao.beans.LookupValue;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.data.filter.beans.CqlFilterImpl;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import org.cmdbuild.easytemplate.EasytemplateProcessor.ExprProcessingMode;
import static org.cmdbuild.easytemplate.EasytemplateProcessor.ExprProcessingMode.EPM_DEFAULT;
import static org.cmdbuild.easytemplate.EasytemplateProcessor.ExprProcessingMode.EPM_JAVASCRIPT;
import org.cmdbuild.easytemplate.EasytemplateProcessorImpl;
import org.cmdbuild.easytemplate.EasytemplateService;
import org.cmdbuild.easytemplate.FtlTemplateService;
import org.cmdbuild.easytemplate.FtlTemplateService.FtlTemplateMode;
import static org.cmdbuild.easytemplate.FtlTemplateService.FtlTemplateMode.FTM_AUTO;
import static org.cmdbuild.easytemplate.FtlTemplateService.FtlTemplateMode.FTM_HTML;
import org.cmdbuild.easytemplate.store.EasytemplateRepository;
import org.cmdbuild.email.Email;
import static org.cmdbuild.email.EmailStatus.ES_DRAFT;
import org.cmdbuild.email.EmailTemplate;
import org.cmdbuild.email.EmailTemplateBindings;
import org.cmdbuild.email.EmailTemplateBindingsImpl;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.job.MapperConfig;
import org.cmdbuild.email.utils.EmailUtils;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDate;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoTime;
import static org.cmdbuild.utils.date.CmDateUtils.toUserReadableDateTime;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.json.CmJsonUtils.prettifyIfJsonLazy;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

@Component
public class EmailTemplateProcessorServiceImpl implements EmailTemplateProcessorService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EasytemplateRepository easytemplateRepository;
    private final EasytemplateService easytemplateService;
    private final UserRepository userRepository;
    private final OperationUserSupplier userSupplier;
    private final RoleRepository roleRepository;
    private final DaoService dao;
    private final FtlTemplateService ftlTemplateService;
    private final CmApiService apiService;

    public EmailTemplateProcessorServiceImpl(EasytemplateRepository easytemplateRepository, EasytemplateService easytemplateService, UserRepository userRepository, OperationUserSupplier userSupplier, RoleRepository roleRepository, DaoService dao, FtlTemplateService ftlTemplateService, CmApiService apiService) {
        this.easytemplateRepository = checkNotNull(easytemplateRepository);
        this.easytemplateService = checkNotNull(easytemplateService);
        this.userRepository = checkNotNull(userRepository);
        this.userSupplier = checkNotNull(userSupplier);
        this.roleRepository = checkNotNull(roleRepository);
        this.dao = checkNotNull(dao);
        this.ftlTemplateService = checkNotNull(ftlTemplateService);
        this.apiService = checkNotNull(apiService);
    }

    @Override
    public Email createEmailFromTemplate(EmailTemplate template, Map<String, Object> map) {
        Email email = EmailImpl.builder().withStatus(ES_DRAFT).build();
        return new EmailTemplateProcessor(null, null, template, null, null, map).processEmail(email);
    }

    @Override
    public Email createEmailFromTemplate(EmailTemplate template, Card card) {
        Email email = EmailImpl.builder().withStatus(ES_DRAFT).build();
        return applyEmailTemplate(email, template, card);
    }

    @Override
    public Email applyEmailTemplate(Email email, EmailTemplate template, Card card) {
        checkNotNull(card);
        checkNotNull(template);
        return new EmailTemplateProcessor(card, card, template, null, null, null).processEmail(email);//TODO client,server card data
    }

    @Override
    public Email applyEmailTemplate(Email email, EmailTemplate template) {
        checkNotNull(email);
        checkNotNull(template);
        return new EmailTemplateProcessor(null, null, template, null, null, null).processEmail(email);
    }

    @Override
    public String applyEmailTemplateExpr(String expr, EmailTemplate template, Card card) {
        checkNotNull(card);
        checkNotNull(template);
        return new EmailTemplateProcessor(card, card, template, null, null, null).processExpression(expr);//TODO client,server card data
    }

    @Override
    public Email applyEmailTemplate(Email email, EmailTemplate template, Card card, Email receivedEmail) {
        checkNotNull(card);
        checkNotNull(template);
        return new EmailTemplateProcessor(card, card, template, checkNotNull(receivedEmail), null, null).processEmail(email);//TODO client,server card data
    }

    @Override
    public EmailTemplateBindings getEmailTemplateBindings(EmailTemplate template) {
        checkNotNull(template);
        return new EmailTemplateProcessor(null, null, template, null, null, null).getBindings();
    }

    @Override
    public Map<String, Object> applyEmailTemplate(Map<String, String> expressions, Email receivedEmail, @Nullable MapperConfig mapperConfig) {
        checkNotNull(expressions);
        checkNotNull(receivedEmail);
        EmailTemplateProcessor emailTemplateProcessor = new EmailTemplateProcessor(null, null, null, receivedEmail, mapperConfig, null);
        return map(transformValues(expressions, v -> emailTemplateProcessor.processExpression(v)));
    }

    private class EmailTemplateProcessor {

        private final EmailTemplate template;
        private final EasytemplateProcessor processor;
        private final Email receivedEmail;
        private final MapperConfig mapperConfig;
        private final Map<String, Object> otherData, ftlTemplateData;

        public EmailTemplateProcessor(@Nullable Card clientCard, @Nullable Card serverCard, @Nullable EmailTemplate template, @Nullable Email receivedEmail, @Nullable MapperConfig mapperConfig, @Nullable Map<String, Object> otherData) {
            this.template = template;
            this.receivedEmail = receivedEmail;
            this.mapperConfig = mapperConfig;
            this.otherData = firstNotNull(otherData, emptyMap());

            logger.trace("'client' card data for email template = \n\n{}\n", clientCard == null ? null : mapToLoggableStringLazy(clientCard.getAllValuesAsMap()));
            logger.trace("'server' card data for email template = \n\n{}\n", serverCard == null ? null : mapToLoggableStringLazy(serverCard.getAllValuesAsMap()));

            String jsContext = buildJsContext(clientCard, serverCard);
            logger.trace("js context for email template = \n\n{}\n", prettifyIfJsonLazy(jsContext));

            logger.trace("template context data = \n\n{}\n", template == null ? "<no template>" : mapToLoggableStringLazy(template.getData()));

            Map<String, Function<String, Object>> resolvers = EasytemplateProcessorImpl.copyOf(easytemplateService.getDefaultProcessorWithJsContext(jsContext))
                    .withResolver("js", this::processJsExpr)
                    .withResolver("user", this::processUserEmailExpr)//TODO check this
                    .withResolver("group", this::processGroupEmailExpr)//TODO check this
                    .withResolver("groupUsers", this::processGroupUsersExpr)
                    .withResolver("email", this::processReceivedEmailExpr)
                    .withResolver("card", this::processCardExpr)
                    .withResolver("", this::processCardExpr)
                    .withResolver("cql", this::processCqlExpr)
                    .withResolver("dbtmpl", this::processDbTmplExpr)
                    .withResolver("data", this::processOtherDataExpr)
                    .accept((b) -> {
                        if (mapperConfig != null) {
                            b.withResolver("mapper", this::processMapperExpr);
                        }
                    }).build().getResolvers();

            resolvers = transformValues(resolvers, f -> {
                return (x) -> {
                    try {
                        return f.apply(x);
                    } catch (Exception ex) {
                        logger.warn(marker(), "CM: error processing email template expression = {} for template = {}", x, template, ex);
                        return "";
                    }
                };
            });

            processor = EasytemplateProcessorImpl.builder().withResolvers(resolvers).build();

            ftlTemplateData = (Map) map().accept((m) -> {
                Map clientData = clientCard != null ? clientCard.getAllValuesAsMap() : emptyMap(),
                        serverData = serverCard != null ? serverCard.getAllValuesAsMap() : emptyMap(),
                        cardData;
                if (clientCard != null) {
                    cardData = clientData;
                } else if (serverCard != null) {
                    cardData = serverData;
                } else {
                    cardData = emptyMap();
                }
                cardData = map(cardData).with("_client", clientData, "_new", clientData, "_server", serverData, "_old", serverData);
                m.put(
                        "card", cardData,
                        "data", map(cardData).with(this.otherData),
                        "cmdb", apiService.getCmApi()
                );
            });
        }

        private EmailTemplateProcessor(EasytemplateProcessor processor, @Nullable EmailTemplate template) {
            this.processor = checkNotNull(processor);
            this.template = template;
            this.receivedEmail = null;
            this.mapperConfig = null;//TODO check this 
            this.otherData = ftlTemplateData = emptyMap();
        }

        private String processExpression(String expr) {
            return processEmailTemplateValue(expr, null);
        }

        public Email processEmail(Email email) {
            checkNotNull(template, "email template is null");
            logger.debug("processing email = {} with template = {}", email, template);
            email = EmailImpl.copyOf(email).accept((builder) -> {
                processEmailTemplate(builder::withFromAddress, template.getFrom());
                processEmailTemplate(builder::withBccAddresses, template.getBcc());
                processEmailTemplate(builder::withContent, template.getBody(), template.getContentType());
                processEmailTemplate(builder::withCcAddresses, template.getCc());
                processEmailTemplate(builder::withSubject, template.getSubject());
                processEmailTemplate(builder::withToAddresses, template.getTo());
            }).withAccount(template.getAccount())
                    .withContentType(template.getContentType())
                    .withDelay(template.getDelay())
                    .withKeepSynchronization(template.getKeepSynchronization())
                    .withPromptSynchronization(template.getPromptSynchronization())
                    .withTemplate(template.getId())
                    .build();

            return email;
        }

        public EmailTemplateBindings getBindings() {
            List<String> clientBindings = list(), serverBindings = list();

            new EmailTemplateProcessor(EasytemplateProcessorImpl.copyOf(processor)
                    .withResolver("client", clientBindings::add)
                    .withResolver("server", serverBindings::add)
                    .build(), template).doProcessForBindings();

            return new EmailTemplateBindingsImpl(clientBindings, serverBindings);
        }

        private void doProcessForBindings() {
            checkNotNull(template, "email template is null");
            list(
                    template.getFrom(),
                    template.getBcc(),
                    template.getBody(),
                    template.getCc(),
                    template.getSubject(),
                    template.getTo()
            ).with(
                    template.getData().values() //note: process all data values because they _may_ contain bindings... this is quite rough and may/will produce a bungh of warnings
            ).stream().filter(StringUtils::isNotBlank).forEach(processor::processExpression);
        }

        private void processEmailTemplate(Consumer<String> setter, @Nullable String expression) {
            processEmailTemplate(setter, expression, null);
        }

        private void processEmailTemplate(Consumer<String> setter, @Nullable String expression, @Nullable String contentType) {
            if (isBlank(expression)) {
                // do nothing
            } else {
                String value = processEmailTemplateValue(expression, contentType);
                setter.accept(value);
            }
        }

        private String processMapperExpr(String expression) {
            checkNotNull(receivedEmail, "missing received email");
            checkNotNull(mapperConfig, "missing mapper config");
            return EmailUtils.processMapperExpr(mapperConfig, receivedEmail.getContentPlaintext(), expression);
        }

        private String processDbTmplExpr(String expression) {
            return processor.processExpression(easytemplateRepository.getTemplate(checkNotBlank(expression, "dbttemplate expr is blank")));
        }

        private String processEmailTemplateValue(String expression, @Nullable String contentType) {
            logger.trace("process email template expr = {}", abbreviate(expression));
            String value;
            if (ftlTemplateService.isFtlTemplate(expression)) {
                FtlTemplateMode mode = equal("text/html", contentType) ? FTM_HTML : FTM_AUTO;//TODO improve this ??
                value = ftlTemplateService.executeFtlTemplate(expression, mode, ftlTemplateData);
            } else {
                value = processor.processExpression(expression);
            }
            logger.trace("processsed email template expr = {}, output value = {}", abbreviate(expression), abbreviate(value));
            return value;
        }

        private String getTemplateContextValueOrDbtemplateValue(String key, ExprProcessingMode mode) {
            String expr = null;
            if (template != null) {
                expr = template.getData().get(key);
                logger.trace("trying to resolve key = {} with template context data, resolved to value = {}", key, expr);
//                easytemplateRepository.getTemplate
            }
            if (isBlank(expr)) {
                expr = easytemplateRepository.getTemplate(key);
                logger.trace("trying to resolve key = {} with dbtemplate data, resolved to value = {}", key, expr);
            }
            checkNotBlank(expr, "unable to resolve expr = %s for template context data or dbtemplate data", key);
            return processor.processExpression(expr, mode);
        }

        private String processJsExpr(String jsExpr) {
            checkNotBlank(jsExpr, "js expr is blank");
            if (template != null && isTemplateContextKey(jsExpr)) {
                jsExpr = getTemplateContextValueOrDbtemplateValue(jsExpr, EPM_JAVASCRIPT);
            }
            logger.trace("evaluate js expression = {}", jsExpr);
            return toStringOrNull(easytemplateService.evalJavascriptCode(jsExpr));
        }

        private String processUserEmailExpr(String username) {
            checkNotBlank(username, "username expr is blank");
            return userRepository.getUserByUsername(username).getEmail();
        }

        private String processGroupEmailExpr(String group) {
            checkNotBlank(group, "group expr is blank");
            return roleRepository.getByNameOrId(group).getEmail();
        }

        private String processGroupUsersExpr(String group) {
            checkNotBlank(group, "groupUsers expr is blank");
            long roleId;
            if (isNumber(group)) {
                roleId = toLong(group);
            } else {
                roleId = roleRepository.getGroupWithName(group).getId();
            }
            return userRepository.getAllWithRole(roleId).stream().map(UserData::getEmail).filter(StringUtils::isNotBlank).distinct().sorted().collect(joining(","));
        }

        private String processOtherDataExpr(String expr) {
            checkNotBlank(expr, "other data expr is blank");
            return toStringOrEmpty(otherData.get(expr));
        }

        private String processReceivedEmailExpr(String expr) {
            checkNotNull(receivedEmail, "invalid email expr - no received email is available");
            checkNotBlank(expr, "email expr is blank");
            switch (expr) {
                case "from":
                    return receivedEmail.getFromAddress();
                case "to":
                    return receivedEmail.getToAddresses();
                case "cc":
                    return receivedEmail.getCcAddresses();
                case "bcc":
                    return receivedEmail.getBccAddresses();
                case "date":
                    return toUserReadableDateTime(receivedEmail.getDate());
                case "subject":
                    return receivedEmail.getSubject();
                case "content":
                    return readToString(receivedEmail.getContentHtmlOrRawPlaintext());
                case "content_plain":
                    return receivedEmail.getContentPlaintext();
                case "content_html":
                    return readToString(receivedEmail.getContentHtmlOrWrappedPlaintext());
                default:
                    throw new IllegalArgumentException(format("unsupported email expr = %s", expr));
            }
        }

        @Nullable
        private String processCqlExpr(@Nullable String expr) {
            logger.trace("process cql expr = {}", expr);
            checkNotBlank(expr, "cql expr is blank");
            String field = null;
            Matcher matcher = Pattern.compile("^([^.]+)[.]([^.]+)$").matcher(expr);
            if (matcher.find()) {
                expr = checkNotBlank(matcher.group(1));
                field = checkNotBlank(matcher.group(2));
                expr = getTemplateContextValueOrDbtemplateValue(expr, EPM_DEFAULT);
            } else if (isTemplateContextKey(expr)) {
                expr = getTemplateContextValueOrDbtemplateValue(expr, EPM_DEFAULT);
            }
            logger.trace("execute cql query = {}", expr);
            Card card = dao.selectAll().where(CqlFilterImpl.build(expr).toCmdbFilter()).getCardOrNull();
            logger.trace("cql query output card = {}", card);
            String value;
            if (card == null) {
                value = null;
            } else {
                if (isBlank(field)) {
                    field = getOnlyElement(getCqlSelectElements(expr));
                }
                value = card.getString(field);
            }
            logger.trace("cql query output value = {} for key = {}", value, field);
            return value;
        }

        private String processCardExpr(@Nullable String expr) {
            logger.trace("process card expr = {}", expr);
            switch (checkNotBlank(expr, "card expr is blank")) {
                case "CurrentRole":
                    return Optional.ofNullable(userSupplier.getUser().getDefaultGroupOrNull()).map(r -> toStringOrEmpty(r.getId())).orElse("");
                default:
                    return processor.processExpression(format("{client:%s}", expr));
            }
        }

        private boolean isTemplateContextKey(String expr) {
            return template != null && template.getData().containsKey(expr);
        }
    }

    private static String buildJsContext(@Nullable Card clientCard, @Nullable Card serverCard) {
        return toJson(map("client", cardToJsContext(clientCard), "server", cardToJsContext(serverCard)));
    }

    private static Object cardToJsContext(@Nullable Card card) {
        if (card == null) {
            return emptyMap();
        } else {
            return card.getType().getAllAttributes().stream().collect(toMap(Attribute::getName, (a) -> cardAttrToJsContext(a, card.get(a.getName()))));
        }
    }

    private static Object cardAttrToJsContext(Attribute attr, @Nullable Object value) {
        if (value == null) {
            return null;
        } else {
            switch (attr.getType().getName()) {
                case REFERENCE:
                case FOREIGNKEY:
                    IdAndDescription reference = rawToSystem(attr, value);
                    return map("Id", reference.getId()).skipNullValues().with("Description", reference.getDescription(), "Code", reference.getCode());
                case LOOKUP:
                    LookupValue lookup = rawToSystem(attr, value);
                    return map("Id", lookup.getId()).skipNullValues().with("Description", lookup.getDescription(), "Code", lookup.getCode());
                case DATE:
                    return toIsoDate(value);
                case TIME:
                    return toIsoTime(value);
                case TIMESTAMP:
                    return toIsoDateTime(value);
                default:
                    return value;
            }
        }
    }
}
