package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Maps.filterKeys;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.translation.TranslationService;
import org.cmdbuild.translation.dao.Translation;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.security.access.prepost.PreAuthorize;

import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;

@Path("translations/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class TranslationsWs {

    private final TranslationService translationService;

    public TranslationsWs(TranslationService translationService) {
        this.translationService = checkNotNull(translationService);
    }

    @GET
    @Path("")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object getAll(@Nullable @QueryParam(LIMIT) Integer limit, @Nullable @QueryParam(START) Integer offset, @Nullable @QueryParam(FILTER) String filter) {
        PagedElements<Translation> translations = translationService.getTranslations(filter, offset, limit);
        return response(translations.stream().map((t) -> map("code", t.getCode(), "lang", t.getLang(), "value", t.getValue())).collect(toList()), translations.totalSize());
    }

    @GET
    @Path("{code}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object getTranslationForKeyAndLang(@PathParam("code") String code, @Nullable @QueryParam("lang") String lang) {
        if (isNotBlank(lang)) {
            String value = translationService.getTranslationValueForCodeAndLang(code, lang);
            return response(map("code", code, "lang", lang, "value", value));
        } else {
            return serializeResponse(code, translationService.getTranslationValueMapByLangForCode(code));
        }
    }

    @PUT
    @Path("{code}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object setTranslation(@PathParam("code") String code, Map<String, String> data) {
        filterKeys(data, not(equalTo("_id"))).forEach((k, v) -> {
            if (isNotBlank(v)) {
                translationService.setTranslation(code, k, v);
            } else {
                translationService.deleteTranslationIfExists(code, k);
            }
        });
        return serializeResponse(code, translationService.getTranslationValueMapByLangForCode(code));
    }

    @DELETE
    @Path("{code}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object deleteTranslation(@PathParam("code") String code, @Nullable @QueryParam("lang") String lang) {
        if (isBlank(lang)) {
            translationService.deleteTranslations(code);
        } else {
            translationService.deleteTranslationIfExists(code, lang);
        }
        return success();
    }

    @GET
    @Path("export")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public DataHandler export(@QueryParam("lang") String languages, @QueryParam("format") String format, @QueryParam(FILTER) String filter, @QueryParam("separator") String separator) {
        checkArgument(isBlank(format) || equal(format.toLowerCase(), "csv"), "invalid format = %s", format);
        //TODO filter
        return translationService.exportHelper()
                .withLanguages(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(nullToEmpty(languages)))
                .withSeparator(separator)
                .export();
    }

    private Object serializeResponse(String code, Map<String, String> map) {
        return response(map("_id", code).with(map));
    }

}
