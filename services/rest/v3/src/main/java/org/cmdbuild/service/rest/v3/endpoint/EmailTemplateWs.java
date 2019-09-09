package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.data.filter.AttributeFilterProcessor;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.FilterType;
import static org.cmdbuild.data.filter.SorterProcessor.sorted;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import static org.cmdbuild.data.filter.utils.CmdbFilterUtils.parseFilter;
import org.cmdbuild.data.filter.utils.CmdbSorterUtils;
import org.cmdbuild.email.EmailTemplate;
import org.cmdbuild.email.EmailTemplateBindings;
import org.cmdbuild.email.EmailTemplateService;
import org.cmdbuild.email.beans.EmailTemplateImpl;
import org.cmdbuild.email.beans.EmailTemplateImpl.EmailTemplateImplBuilder;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.v3.endpoint.CardWs.mapSorterAttributeNames;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.security.access.prepost.PreAuthorize;

@Path("email/templates/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class EmailTemplateWs {

    private final EmailTemplateService service;

    public EmailTemplateWs(EmailTemplateService service) {
        this.service = checkNotNull(service);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@QueryParam(FILTER) String filterStr, @QueryParam(SORT) String sort, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam(DETAILED) @DefaultValue(FALSE) boolean detailed, @QueryParam("includeBindings") @DefaultValue(FALSE) Boolean includeBindings) {
        List<EmailTemplate> list = service.getMany(parseFilter(filterStr).mapNames("name", ATTR_CODE), offset, limit);
        CmdbSorter sorter = mapSorterAttributeNames(CmdbSorterUtils.parseSorter(sort));
        if (!sorter.isNoop()) {
            list = sorted(list, sorter, (key, template) -> {
                switch (key) {
                    case "name":
                        return template.getName();
                    case "description":
                        return template.getDescription();
                    default:
                        throw new IllegalArgumentException("unsupported filter key = " + key);
                }
            });
        }
        CmdbFilter filter = CmdbFilterUtils.parseFilter(filterStr);
        if (filter.hasFilter()) {
            filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            list = AttributeFilterProcessor.<EmailTemplate>builder()
                    .withKeyToValueFunction((key, template) -> {
                        switch (checkNotBlank(key)) {
                            case "name":
                                return template.getName();
                            case "description":
                                return template.getDescription();
                            default:
                                throw new IllegalArgumentException("invalid attribute filter key = " + key);
                        }
                    })
                    .withFilter(filter.getAttributeFilter()).build().filter(list);
        }
        return response(paged(list, t -> serializeTemplate(t, detailed, includeBindings), offset, limit));
    }

    @GET
    @Path("{templateId}/")
    public Object read(@PathParam("templateId") String id, @QueryParam("includeBindings") @DefaultValue(FALSE) Boolean includeBindings) {
        EmailTemplate element;
        if (isNumber(id)) {
            element = service.getOne(toLong(id));
        } else {
            element = service.getByName(id);
        }
        return response(serializeTemplate(element, true, includeBindings));
    }

    @POST
    @Path(EMPTY)
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object create(WsEmailTemplateData data) {
        return response(serializeDetailedTemplate(service.createEmailTemplate(data.toEmailTemplate().build())));
    }

    @PUT
    @Path("{templateId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object update(@PathParam("templateId") Long templateId, WsEmailTemplateData data) {
        return response(serializeDetailedTemplate(service.updateEmailTemplate(data.toEmailTemplate().withId(templateId).build())));
    }

    @DELETE
    @Path("{templateId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object delete(@PathParam("templateId") Long templateId) {
        service.deleteEmailTemplate(templateId);
        return success();
    }

    private FluentMap<String, Object> serializeBasicTemplate(EmailTemplate t) {
        return map(
                "_id", t.getId(),
                "name", t.getName(),
                "description", t.getDescription()
        );
    }

    private FluentMap<String, Object> serializeDetailedTemplate(EmailTemplate t) {
        return serializeBasicTemplate(t).with(
                "from", t.getFrom(),
                "to", t.getTo(),
                "cc", t.getCc(),
                "bcc", t.getBcc(),
                "subject", t.getSubject(),
                "body", t.getBody(),
                "contentType", t.getContentType(),
                "account", t.getAccount(),
                "keepSynchronization", t.getKeepSynchronization(),
                "promptSynchronization", t.getPromptSynchronization(),
                "delay", t.getDelay(),
                "data", t.getData()
        );
    }

    private FluentMap<String, Object> serializeTemplate(EmailTemplate t, boolean detailed, boolean includeBindings) {
        return (detailed ? serializeDetailedTemplate(t) : serializeBasicTemplate(t)).accept((m) -> {
            if (includeBindings) {
                EmailTemplateBindings bindings = service.getEmailTemplateBindings(t);
                m.put("_bindings", map(
                        "client", bindings.getClientBindings(),
                        "server", bindings.getServerBindings()
                ));
            }
        });
    }

    public static class WsEmailTemplateData {

        private final Long delay, account;
        private final String from, to, cc, bcc, subject, body, name, description, contentType;
        private final boolean keepSynchronization, promptSynchronization;
        private final Map<String, String> data;

        public WsEmailTemplateData(
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("delay") Long delay,
                @JsonProperty("from") String from,
                @JsonProperty("to") String to,
                @JsonProperty("cc") String cc,
                @JsonProperty("bcc") String bcc,
                @JsonProperty("subject") String subject,
                @JsonProperty("contentType") String contentType,
                @JsonProperty("body") String body,
                @JsonProperty("account") Long account,
                @JsonProperty("keepSynchronization") Boolean keepSynchronization,
                @JsonProperty("promptSynchronization") Boolean promptSynchronization,
                @JsonProperty("data") Map<String, String> data) {
            this.delay = delay;
            this.from = from;
            this.to = to;
            this.cc = cc;
            this.bcc = bcc;
            this.subject = subject;
            this.body = body;
            this.account = account;
            this.name = name;
            this.contentType = contentType;
            this.description = description;
            this.keepSynchronization = keepSynchronization;
            this.promptSynchronization = promptSynchronization;
            this.data = nullToEmpty(data);//TODO change this to checknotnull
        }

        public EmailTemplateImplBuilder toEmailTemplate() {
            return EmailTemplateImpl.builder()
                    .withDelay(delay)
                    .withAccount(account)
                    .withBcc(bcc)
                    .withCc(cc)
                    .withBody(body)
                    .withDelay(delay)
                    .withFrom(from)
                    .withKeepSynchronization(keepSynchronization)
                    .withPromptSynchronization(promptSynchronization)
                    .withSubject(subject)
                    .withTo(to)
                    .withDescription(description)
                    .withName(name)
                    .withContentType(contentType)
                    .withData(data);
        }
    }
}
