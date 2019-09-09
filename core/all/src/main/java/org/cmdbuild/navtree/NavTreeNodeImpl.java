/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.navtree;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Objects.equal;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.beans.RelationDirectionUtils.serializeRelationDirection;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Lists.transform;
import static java.util.Collections.emptyList;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import static org.cmdbuild.dao.beans.RelationDirectionUtils.parseRelationDirectionOrNull;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

@JsonAutoDetect(getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class NavTreeNodeImpl implements NavTreeNode {

    private final String targetClassName, targetClassDescription, domainName, targetFilter, id, parentId;
    private final boolean showOnlyOne, enableRecursion;
    private final RelationDirection direction;
    private final List<NavTreeNode> nodes;

    @JsonCreator
    public NavTreeNodeImpl(
            @JsonProperty("_id") String id,
            @JsonProperty("targetClass") String targetClassName,
            @JsonProperty("description") String targetClassDescription,
            @JsonProperty("filter") String targetFilter,
            @JsonProperty("domain") String domainName,
            @JsonProperty("direction") String direction,
            @JsonProperty("showOnlyOne") Boolean showOnlyOne,
            @JsonProperty("enableRecursion") Boolean enableRecursion,
            @JsonProperty("nodes") List<NavTreeNodeImpl> nodes) {
        this.targetClassName = checkNotBlank(targetClassName);
        this.targetClassDescription = nullToEmpty(targetClassDescription);
        this.domainName = domainName;
        this.targetFilter = targetFilter;
        this.id = checkNotBlank(id);
        this.showOnlyOne = firstNonNull(showOnlyOne, false);
        this.enableRecursion = firstNonNull(enableRecursion, false);
        this.direction = firstNotNull(parseRelationDirectionOrNull(direction), RD_DIRECT);
        this.parentId = null;
        this.nodes = ImmutableList.copyOf(addParent(firstNotNull(nodes, emptyList()), this.id));
    }

    private NavTreeNodeImpl(NavTreeNodeImplBuilder builder) {
        this.targetClassName = checkNotBlank(builder.targetClassName);
        this.targetClassDescription = nullToEmpty(builder.targetClassDescription);
        this.domainName = builder.domainName;
        this.targetFilter = builder.targetFilter;
        this.id = checkNotBlank(builder.id);
        this.showOnlyOne = firstNonNull(builder.showOnlyOne, false);
        this.enableRecursion = firstNonNull(builder.enableRecursion, false);
        this.direction = firstNonNull(builder.direction, RD_DIRECT);
        this.parentId = builder.parentId;
        this.nodes = ImmutableList.copyOf(addParent(firstNotNull(builder.nodes, emptyList()), id));
    }

    private static List<NavTreeNode> addParent(List<? extends NavTreeNode> nodes, String parentId) {
        return transform(nodes, (n) -> copyOf(n).withParentId(parentId).build());
    }

    @Override
    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    @Override
    @Nullable
    public String getParentId() {
        return parentId;
    }

    @Override
    @JsonProperty("targetClass")
    public String getTargetClassName() {
        return targetClassName;
    }

    @Override
    @JsonProperty("description")
    public String getTargetClassDescription() {
        return targetClassDescription;
    }

    @Override
    @Nullable
    @JsonProperty("domain")
    public String getDomainName() {
        return domainName;
    }

    @Override
    @Nullable
    @JsonProperty("filter")
    public String getTargetFilter() {
        return targetFilter;
    }

    @JsonProperty("direction")
    public String getDirection() {
        return serializeRelationDirection(direction);
    }

    @Override
    public boolean getDirect() {
        return equal(direction, RD_DIRECT);
    }

    @Override
    @JsonProperty("showOnlyOne")
    public boolean getShowOnlyOne() {
        return showOnlyOne;
    }

    @Override
    @JsonProperty("enableRecursion")
    public boolean getEnableRecursion() {
        return enableRecursion;
    }

    @Override
    @JsonProperty("nodes")
    public List<NavTreeNode> getChildNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return "NavTreeNode{" + "targetClass=" + targetClassName + ", id=" + id + '}';
    }

    public static NavTreeNodeImplBuilder builder() {
        return new NavTreeNodeImplBuilder();
    }

    public static NavTreeNodeImplBuilder copyOf(NavTreeNode source) {
        return new NavTreeNodeImplBuilder()
                .withTargetClassName(source.getTargetClassName())
                .withTargetClassDescription(source.getTargetClassDescription())
                .withDomainName(source.getDomainName())
                .withTargetFilter(source.getTargetFilter())
                .withId(source.getId())
                .withParentId(source.getParentId())
                .withShowOnlyOne(source.getShowOnlyOne())
                .withEnableRecursion(source.getEnableRecursion())
                .withDirection(source.getDirect() ? RD_DIRECT : RD_INVERSE)
                .withChildNodes(source.getChildNodes());
    }

    public static class NavTreeNodeImplBuilder implements Builder<NavTreeNodeImpl, NavTreeNodeImplBuilder> {

        private String targetClassName;
        private String targetClassDescription;
        private String domainName;
        private String targetFilter;
        private String id, parentId;
        private Boolean showOnlyOne;
        private Boolean enableRecursion;
        private RelationDirection direction;
        private List<NavTreeNode> nodes;

        public NavTreeNodeImplBuilder withTargetClassName(String targetClassName) {
            this.targetClassName = targetClassName;
            return this;
        }

        public NavTreeNodeImplBuilder withTargetClassDescription(String targetClassDescription) {
            this.targetClassDescription = targetClassDescription;
            return this;
        }

        public NavTreeNodeImplBuilder withDomainName(String domainName) {
            this.domainName = domainName;
            return this;
        }

        public NavTreeNodeImplBuilder withTargetFilter(String targetFilter) {
            this.targetFilter = targetFilter;
            return this;
        }

        public NavTreeNodeImplBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public NavTreeNodeImplBuilder withParentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public NavTreeNodeImplBuilder withShowOnlyOne(Boolean showOnlyOne) {
            this.showOnlyOne = showOnlyOne;
            return this;
        }

        public NavTreeNodeImplBuilder withEnableRecursion(Boolean enableRecursion) {
            this.enableRecursion = enableRecursion;
            return this;
        }

        public NavTreeNodeImplBuilder withDirection(RelationDirection direction) {
            this.direction = direction;
            return this;
        }

        public NavTreeNodeImplBuilder withChildNodes(List<NavTreeNode> nodes) {
            this.nodes = nodes;
            return this;
        }

        @Override
        public NavTreeNodeImpl build() {
            return new NavTreeNodeImpl(this);
        }

    }
}
