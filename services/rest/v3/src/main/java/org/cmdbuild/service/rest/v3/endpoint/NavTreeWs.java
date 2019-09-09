package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.singletonList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import org.cmdbuild.data.filter.AttributeFilterProcessor;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import org.cmdbuild.navtree.NavTree;
import org.cmdbuild.navtree.NavTreeImpl;
import org.cmdbuild.navtree.NavTreeImpl.NavTreeDataImplBuilder;
import org.cmdbuild.navtree.NavTreeNode;
import org.cmdbuild.navtree.NavTreeNodeImpl;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;

import static org.cmdbuild.service.rest.v3.endpoint.NavTreeWs.TreeMode.TREE;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.navtree.NavTreeService;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

@Path("domainTrees")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class NavTreeWs {

    private final NavTreeService service;

    public NavTreeWs(NavTreeService service) {
        this.service = checkNotNull(service);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(FILTER) String filterStr, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        CmdbFilter filter = CmdbFilterUtils.parseFilter(filterStr);
        List<NavTree> list = isAdminViewMode(viewMode) ? service.getAll() : service.getAllActive();
        if (filter.hasFilter()) {
            filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            list = AttributeFilterProcessor.<NavTree>builder()
                    .withKeyToValueFunction((key, tree) -> {
                        switch (checkNotBlank(key)) {
                            case "targetClass":
                                return tree.getData().getTargetClassName();
                            default:
                                throw new IllegalArgumentException("invalid attribute filter key = " + key);
                        }
                    })
                    .withFilter(filter.getAttributeFilter()).build().filter(list);
        }
        return response(paged(list, offset, limit).map((tree) -> map(
                "_id", tree.getName(),
                "description", tree.getDescription(),
                "active", tree.getActive())));
    }

    @GET
    @Path("{treeId}/")
    public Object read(@PathParam("treeId") String id, @QueryParam("treeMode") @DefaultValue("flat") String treeMode) {
        NavTree root = service.getTree(id);
        return response(serializeTree(root, parseEnum(treeMode, TreeMode.class)));
    }

    @POST
    @Path("")
    public Object create(WsTreeData data) {
        NavTree tree = data.toTreeNode().build();
        tree = service.create(tree);
        return response(serializeTree(tree, TREE));
    }

    @PUT
    @Path("{treeId}")
    public Object update(@PathParam("treeId") String id, WsTreeData data) {
        NavTree tree = data.toTreeNode().withName(id).build();
        tree = service.update(tree);
        return response(serializeTree(tree, TREE));
    }

    @DELETE
    @Path("{treeId}")
    public Object delete(@PathParam("treeId") String id) {
        service.removeTree(id);
        return success();
    }

    private FluentMap serializeTree(NavTree root, TreeMode mode) {
        List nodes;
        switch (mode) {
            case FLAT:
                nodes = root.getData().getThisNodeAndAllDescendants().stream().map(this::serializeNode).collect(toList());
                break;
            case TREE:
                nodes = singletonList(serializeNodeAndDescendants(root.getData()));
                break;
            default:
                throw new IllegalArgumentException();
        }
        return map(
                "_id", root.getName(),
                "name", root.getName(),
                "description", root.getDescription(),
                "active", root.getActive(),
                "nodes", nodes
        );
    }

    private FluentMap serializeNodeAndDescendants(NavTreeNode node) {
        return serializeNode(node).with("nodes", node.getChildNodes().stream().map(this::serializeNodeAndDescendants).collect(toList()));
    }

    private FluentMap serializeNode(NavTreeNode node) {
        return map(
                "_id", node.getId(),
                "filter", node.getTargetFilter(),
                "targetClass", node.getTargetClassName(),
                "recursionEnabled", node.getEnableRecursion(),
                "domain", node.getDomainName(),
                "showOnlyOne", node.getShowOnlyOne()
        ).skipNullValues().with(
                "parent", node.getParentId(),
                "direction", isBlank(node.getDomainName()) ? null : (node.getDirect() ? "_1" : "_2")
        ).then();
    }

    enum TreeMode {
        FLAT, TREE
    }

    public static class WsTreeData {

        private final String name, description;
        private final WsTreeNodeData data;
        private final boolean active;

        public WsTreeData(@JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("nodes") List<WsTreeNodeData> nodes,
                @JsonProperty("active") boolean active) {
            this.name = checkNotBlank(name, "nav tree name cannot be null");
            this.description = nullToEmpty(description);
            this.data = checkNotNull(getOnlyElement(nodes, null), "a nav tree must have a root node");
            this.active = active;
        }

        public NavTreeDataImplBuilder toTreeNode() {
            return NavTreeImpl.builder()
                    .withName(name)
                    .withDescription(description)
                    .withData(data.toTreeNode())
                    .withActive(active);
        }

    }

    public static class WsTreeNodeData {

        private final String id;
        private final String filter, targetClass, domain, direction;
        private final Boolean recursionEnabled, showOnlyOne;
        private final List<WsTreeNodeData> nodes;

        public WsTreeNodeData(@JsonProperty("_id") String id,
                @JsonProperty("filter") String filter,
                @JsonProperty("targetClass") String targetClass,
                @JsonProperty("domain") String domain,
                @JsonProperty("direction") String direction,
                @JsonProperty("recursionEnabled") Boolean recursionEnabled,
                @JsonProperty("showOnlyOne") Boolean showOnlyOne,
                @JsonProperty("nodes") List<WsTreeNodeData> nodes) {
            this.id = id;
            this.filter = filter;
            this.targetClass = targetClass;
            this.domain = domain;
            this.direction = firstNotBlank(direction, "_1");
            this.recursionEnabled = recursionEnabled;
            this.showOnlyOne = showOnlyOne;
            this.nodes = ImmutableList.copyOf(nodes);
        }

        public NavTreeNode toTreeNode() {
            return NavTreeNodeImpl.builder()
                    .withId(firstNotBlank(id, randomId()))
                    .withTargetFilter(filter)
                    .withTargetClassName(targetClass)
                    .withTargetClassDescription(targetClass)
                    .withDomainName(domain)
                    .withDirection(parseDirection(direction) ? RD_DIRECT : RD_INVERSE)
                    .withEnableRecursion(recursionEnabled)
                    .withShowOnlyOne(showOnlyOne)
                    .withChildNodes(nodes.stream().map(WsTreeNodeData::toTreeNode).collect(toImmutableList()))
                    .build();
        }

    }

    private static boolean parseDirection(String direction) {
        switch (nullToEmpty(direction)) {
            case "_1":
                return true;
            case "_2":
                return false;
            default:
                throw new IllegalArgumentException("invalid direction = " + direction);
        }
    }

}
