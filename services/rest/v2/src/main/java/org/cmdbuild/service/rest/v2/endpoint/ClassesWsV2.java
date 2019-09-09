package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("classes/")
@Produces(APPLICATION_JSON)
public class ClassesWsV2 {

    private final UserClassService classService;
    private final ClassSerializationHelper helper;

    public ClassesWsV2(UserClassService classService, ClassSerializationHelper helper) {
        this.classService = checkNotNull(classService);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path(EMPTY)
    public Object readMany() {
        List<Classe> all = classService.getAllUserClasses();
        return map("data", all.stream().map(this::serializeResponse).collect(toList()), "meta", map("total", all.size()));
    }

    @GET
    @Path("{classId}/")
    public Object readOne(@PathParam("classId") String classId) {

        Classe classe = classService.getUserClass(classId);
        return map("data", serializeResponse(classe));
    }

    private CmMapUtils.FluentMap<String, Object> serializeResponse(Classe input) {
        return CmMapUtils.<String, Object>map(
                "name", input.getName(),
                "description", input.getDescription(),
                "prototype", input.isSuperclass(),
                "parent", input.getParentOrNull(),
                "defaultFilter", input.getMetadata().getDefaultFilterOrNull()
        );
    }
}
