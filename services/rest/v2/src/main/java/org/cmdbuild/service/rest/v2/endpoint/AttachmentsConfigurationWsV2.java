package org.cmdbuild.service.rest.v2.endpoint;

import java.util.ArrayList;
import java.util.List;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.cmdbuild.dms.DmsService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Path("configuration/attachments/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class AttachmentsConfigurationWsV2 {

    private final DmsService dmsService;
    
    public AttachmentsConfigurationWsV2(DmsService dmsService){
        this.dmsService = dmsService;
    }

    @GET
    @Path("categories/")
    public Object readCategories() {
        List categories = list();
        categories.add(map("description","Documento","_id","Documento"));
        categories.add(map("description","Immagine","_id","Immagine"));
        return map("data", categories, "meta", map("total", categories.size()));
    }

    @GET
    @Path("categories/{categoryId}/attributes/")
    public Object readCategoryAttributes(@PathParam("categoryId") String categoryId) {
        List list = new ArrayList();
        return map("data", list);
    }
}
