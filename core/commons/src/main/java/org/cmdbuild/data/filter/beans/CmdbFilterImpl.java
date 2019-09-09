/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import static com.google.common.base.Preconditions.checkArgument;
import org.cmdbuild.data.filter.AttributeFilter;
import org.cmdbuild.data.filter.CqlFilter;
import org.cmdbuild.data.filter.FulltextFilter;
import org.cmdbuild.data.filter.RelationFilter;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import org.cmdbuild.utils.lang.Builder;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CompositeFilter;
import org.cmdbuild.data.filter.EcqlFilter;
import org.cmdbuild.data.filter.FunctionFilter;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import static org.cmdbuild.data.filter.utils.CmdbFilterUtils.mapNamesInFilter;

public class CmdbFilterImpl implements CmdbFilter {

	private final AttributeFilter attributeFilter;
	private final RelationFilter relationFilter;
	private final FulltextFilter fulltextFilter;
	private final CqlFilter cqlFilter;
	private final EcqlFilter ecqlFilter;
	private final FunctionFilter functionFilter;
	private final CompositeFilter compositeFilter;

	private CmdbFilterImpl(CmdbFilterBuilder builder) {
		this.attributeFilter = builder.attributeFilter;
		this.relationFilter = builder.relationFilter;
		this.fulltextFilter = builder.fulltextFilter;
		this.cqlFilter = builder.cqlFilter;
		this.ecqlFilter = builder.ecqlFilter;
		this.functionFilter = builder.functionFilter;
		this.compositeFilter = builder.compositeFilter;
		checkArgument(ecqlFilter == null || cqlFilter == null, "cannot set both cqlFilter and ecqlFilter");
		checkArgument(compositeFilter == null || (attributeFilter == null && relationFilter == null && fulltextFilter == null && cqlFilter == null && ecqlFilter == null && functionFilter == null), "cannot set both composite filter and any other filter type");
	}

	@Override
	public FunctionFilter getFunctionFilter() {
		return checkNotNull(functionFilter);
	}

	@Override
	public AttributeFilter getAttributeFilter() {
		return checkNotNull(attributeFilter);
	}

	@Override
	public RelationFilter getRelationFilter() {
		return checkNotNull(relationFilter);
	}

	@Override
	public FulltextFilter getFulltextFilter() {
		return checkNotNull(fulltextFilter);
	}

	@Override
	public CqlFilter getCqlFilter() {
		return checkNotNull(cqlFilter);
	}

	@Override
	public EcqlFilter getEcqlFilter() {
		return checkNotNull(ecqlFilter);
	}

	@Override
	public CompositeFilter getCompositeFilter() {
		return checkNotNull(compositeFilter);
	}

	@Override
	public boolean hasAttributeFilter() {
		return attributeFilter != null;
	}

	@Override
	public boolean hasRelationFilter() {
		return relationFilter != null;
	}

	@Override
	public boolean hasFulltextFilter() {
		return fulltextFilter != null;
	}

	@Override
	public boolean hasCqlFilter() {
		return cqlFilter != null;
	}

	@Override
	public boolean hasEcqlFilter() {
		return ecqlFilter != null;
	}

	@Override
	public boolean hasFunctionFilter() {
		return functionFilter != null;
	}

	@Override
	public boolean hasCompositeFilter() {
		return compositeFilter != null;
	}

	@Override
	public CmdbFilter mapNames(Map<String, String> map) {
		return mapNamesInFilter(this, map);
	}

	@Override
	public String toString() {
		return "CmdbFilterImpl{" + "asJson=" + CmdbFilterUtils.serializeFilter(this) + '}';//TODO composite filter is serializable with this method? fix
	}

	public static CmdbFilterBuilder builder() {
		return new CmdbFilterBuilder();
	}

	private final static CmdbFilter NOOP = builder().build();

	public static CmdbFilter noopFilter() {
		return NOOP;
	}

	public static CmdbFilterBuilder copyOf(CmdbFilter filter) {
		return new CmdbFilterBuilder()
				.withAttributeFilter(((CmdbFilterImpl) filter).attributeFilter)
				.withFunctionFilter(((CmdbFilterImpl) filter).functionFilter)
				.withRelationFilter(((CmdbFilterImpl) filter).relationFilter)
				.withFulltextFilter(((CmdbFilterImpl) filter).fulltextFilter)
				.withCqlFilter(((CmdbFilterImpl) filter).cqlFilter)
				.withEcqlFilter(((CmdbFilterImpl) filter).ecqlFilter)
				.withCompositeFilter(((CmdbFilterImpl) filter).compositeFilter);
	}

	public static class CmdbFilterBuilder implements Builder<CmdbFilterImpl, CmdbFilterBuilder> {

		private AttributeFilter attributeFilter;
		private RelationFilter relationFilter;
		private FulltextFilter fulltextFilter;
		private CqlFilter cqlFilter;
		private EcqlFilter ecqlFilter;
		private FunctionFilter functionFilter;
		private CompositeFilter compositeFilter;

		public CmdbFilterBuilder withFunctionFilter(FunctionFilter functionFilter) {
			this.functionFilter = functionFilter;
			return this;
		}

		public CmdbFilterBuilder withAttributeFilter(AttributeFilter attributeFilter) {
			this.attributeFilter = attributeFilter;
			return this;
		}

		public CmdbFilterBuilder withRelationFilter(RelationFilter relationFilter) {
			this.relationFilter = relationFilter;
			return this;
		}

		public CmdbFilterBuilder withFulltextFilter(String fulltextFilter) {
			return withFulltextFilter(new FulltextFilterImpl(fulltextFilter));
		}

		public CmdbFilterBuilder withFulltextFilter(FulltextFilter fulltextFilter) {
			this.fulltextFilter = fulltextFilter;
			return this;
		}

		public CmdbFilterBuilder withCqlFilter(String cqlFilter) {
			return withCqlFilter(CqlFilterImpl.build(cqlFilter));
		}

		public CmdbFilterBuilder withCqlFilter(CqlFilter cqlFilter) {
			this.cqlFilter = cqlFilter;
			return this;
		}

		public CmdbFilterBuilder withEcqlFilter(EcqlFilter ecqlFilter) {
			this.ecqlFilter = ecqlFilter;
			return this;
		}

		public CmdbFilterBuilder withCompositeFilter(CompositeFilter compositeFilter) {
			this.compositeFilter = compositeFilter;
			return this;
		}

		@Override
		public CmdbFilterImpl build() {
			return new CmdbFilterImpl(this);
		}

	}
}
