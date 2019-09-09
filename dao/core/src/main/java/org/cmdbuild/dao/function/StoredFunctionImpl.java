package org.cmdbuild.dao.function;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;

import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.toList;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import static org.cmdbuild.dao.function.FunctionMetadata.CATEGORIES;
import static org.cmdbuild.dao.function.FunctionMetadata.MASTERTABLE;
import static org.cmdbuild.dao.function.FunctionMetadata.TAGS;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class StoredFunctionImpl implements StoredFunction {

    private final long id;
    private final String name;
    private final List<StoredFunctionParameter> inputParameters;
    private final List<StoredFunctionOutputParameter> outputParameters;
    private final boolean returnsSet;
    private final List<Category> categories;
    private final Map<String, Object> metadata;
    private final FunctionMetadata functionMetadata;

    private StoredFunctionImpl(FunctionBuilder builder) {
        this.name = checkNotBlank(builder.identifier);
        this.id = checkNotNullAndGtZero(builder.id);
        this.inputParameters = ImmutableList.copyOf(builder.inputParameters);
        this.outputParameters = ImmutableList.copyOf(builder.outputParameters);
        this.returnsSet = builder.returnsSet;
        this.functionMetadata = checkNotNull(builder.functionMetadata);
        this.categories = toList(firstNonNull(builder.functionMetadata.getCategories(), emptyList()));
        this.metadata = ImmutableMap.of(CATEGORIES, functionMetadata.getCategories(), MASTERTABLE, functionMetadata.getMasterTable(), TAGS, functionMetadata.getTags());//TODO check this
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public FunctionMetadata getMetadata() {
        return functionMetadata;
    }

    @Override
    public boolean returnsSet() {
        return returnsSet;
    }

    @Override
    public List<StoredFunctionParameter> getInputParameters() {
        return inputParameters;
    }

    @Override
    public List<StoredFunctionOutputParameter> getOutputParameters() {
        return outputParameters;
    }

    @Override
    public Iterable<Category> getCategories() {
        return categories;
    }

    @Override
    public Map<String, Object> getMetadataExt() {
        return metadata;
    }

    @Override
    public String toString() {
        return "StoredFunction{" + "id=" + id + ", name=" + name + '}';
    }

    public static FunctionBuilder builder() {
        return new FunctionBuilder();
    }

    public static class FunctionBuilder implements Builder<StoredFunctionImpl, FunctionBuilder> {

        private String identifier;
        private Long id;
        private Boolean returnsSet;
        private FunctionMetadata functionMetadata;
        private final List<StoredFunctionParameter> inputParameters = list();
        private final List<StoredFunctionOutputParameter> outputParameters = list();

        public FunctionBuilder withName(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public String getName() {
            return checkNotNull(identifier);
        }

        public FunctionBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public FunctionBuilder withReturnSet(Boolean returnsSet) {
            this.returnsSet = returnsSet;
            return this;
        }

        public FunctionBuilder withMetadata(FunctionMetadata functionMetadata) {
            this.functionMetadata = functionMetadata;
            return this;
        }

        public FunctionBuilder withInputParameter(String name, CardAttributeType<?> type) {
            inputParameters.add(new StoredFunctionParameterImpl(name, type));
            return this;
        }

        public FunctionBuilder withOutputParameter(String name, CardAttributeType<?> type, Boolean basedsp) {
            outputParameters.add(new StoredFunctionOutputParameterImpl(name, type, basedsp));
            return this;
        }

        public FunctionMetadata getFunctionMetadata() {
            return checkNotNull(functionMetadata);
        }

        @Override
        public StoredFunctionImpl build() {
            return new StoredFunctionImpl(this);
        }

    }

}
