package org.cmdbuild.dao.function;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.CMEntryTypeVisitor;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.dao.entrytype.EntryType.EntryTypeType.ET_FUNCTION;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public interface StoredFunction extends EntryType {

    List<StoredFunctionParameter> getInputParameters();

    List<StoredFunctionOutputParameter> getOutputParameters();

    boolean returnsSet();

    Iterable<Category> getCategories();

    Map<String, Object> getMetadataExt();

    @Override
    FunctionMetadata getMetadata();

    @Override
    public default Map<String, Attribute> getAllAttributesAsMap() {
        return getOutputParameters().stream().map(p -> {

            AtomicInteger index = new AtomicInteger(0);

            return AttributeImpl.builder()
                    .withName(p.getName())
                    .withOwner(StoredFunction.this)
                    .withType(p.getType())
                    .withMeta((b) -> {
                        b
                                .withIndex(index.getAndIncrement())
                                .withShowInGrid(p.getBasedsp());
                    })
                    .build();

        }).collect(toMap(Attribute::getName, identity()));
    }

    @Override
    public default void accept(CMEntryTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public default boolean hasHistory() {
        return false;
    }

    @Override
    public default String getPrivilegeId() {
        return format("fun:%s", getId());
    }

    default StoredFunctionOutputParameter getOutputParameter(String name) {
        checkNotBlank(name);
        return getOutputParameters().stream().filter((p) -> equal(p.getName(), name)).collect(onlyElement());
    }

    default StoredFunctionOutputParameter getOnlyOutputParameter() {
        return getOnlyElement(getOutputParameters());
    }

    default boolean hasOnlyOneOutputParameter() {
        return getOutputParameters().size() == 1;
    }

    default List<String> getInputParameterNames() {
        return getInputParameters().stream().map(StoredFunctionParameter::getName).collect(toList());
    }

    @Override
    default EntryTypeType getEtType() {
        return ET_FUNCTION;
    }

    default StoredFunctionParameter getInputParameter(String name) {
        return getInputParameters().stream().filter(p -> equal(p.getName(), name)).collect(onlyElement("input parameter not found for name =< %s >", name));
    }

    default Set<String> getTags() {
        return getMetadata().getTags();
    }

    @Nullable
    default String getSourceClassName() {
        return getMetadata().getSource();
    }

    default boolean hasSourceClassName() {
        return isNotBlank(getSourceClassName());
    }

    enum Category {
        SYSTEM,
        UNDEFINED;

        public static Category of(String text) {
            for (Category category : values()) {
                if (category.name().equalsIgnoreCase(text)) {
                    return category;
                }
            }
            return UNDEFINED;
        }
    }

}
