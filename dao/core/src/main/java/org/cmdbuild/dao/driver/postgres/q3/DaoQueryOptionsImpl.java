/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.postgres.q3;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static java.util.Collections.emptyList;
import java.util.List;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.SorterElement;
import static org.cmdbuild.data.filter.SorterElement.SorterElementDirection.DESC;
import org.cmdbuild.data.filter.beans.CmdbSorterImpl;
import org.cmdbuild.data.filter.beans.SorterElementImpl;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import static org.cmdbuild.data.filter.utils.CmdbFilterUtils.noopFilter;
import org.cmdbuild.data.filter.utils.CmdbSorterUtils;
import static org.cmdbuild.data.filter.utils.CmdbSorterUtils.noopSorter;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class DaoQueryOptionsImpl implements DaoQueryOptions {

    private final List<String> attrs;
    private final Long offset, limit, positionOfCard;
    private final CmdbSorter sorter;
    private final CmdbFilter filter;
    private final Boolean goToPage;

    private DaoQueryOptionsImpl(DaoQueryOptionsImplBuilder builder) {
        this.attrs = ImmutableList.copyOf(firstNonNull(builder.attrs, emptyList()));
        this.offset = firstNonNull(builder.offset, 0l);
        this.limit = builder.limit;
        this.sorter = firstNonNull(builder.sorter, noopSorter());
        this.filter = firstNonNull(builder.filter, noopFilter());
        this.positionOfCard = builder.positionOfCard;
        if (positionOfCard != null) {
            this.goToPage = checkNotNull(builder.goToPage);
            checkArgument(isNotNullAndGtZero(limit), "must set valid 'limit' along with 'positionOf'");
        } else {
            goToPage = null;
        }
    }

    @Override
    public List<String> getAttrs() {
        return attrs;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    @Nullable
    public Long getLimit() {
        return limit;
    }

    @Override
    public CmdbSorter getSorter() {
        return sorter;
    }

    @Override
    public CmdbFilter getFilter() {
        return filter;
    }

    @Override
    public boolean hasPositionOf() {
        return positionOfCard != null;
    }

    @Override
    public long getPositionOf() {
        return positionOfCard;
    }

    @Override
    public boolean getGoToPage() {
        return goToPage;
    }

    public static DaoQueryOptions emptyOptions() {
        return builder().build();
    }

    public static DaoQueryOptionsImplBuilder builder() {
        return new DaoQueryOptionsImplBuilder();
    }

    public static DaoQueryOptionsImplBuilder copyOf(DaoQueryOptions source) {
        return new DaoQueryOptionsImplBuilder()
                .withOffset(source.getOffset())
                .withLimit(source.getLimit())
                .withSorter(source.getSorter())
                .withFilter(source.getFilter())
                .withAttrs(source.getAttrs())
                .accept((b) -> {
                    if (source.hasPositionOf()) {
                        b.withPositionOf(source.getPositionOf(), source.getGoToPage());
                    }
                });
    }

    public static class DaoQueryOptionsImplBuilder implements Builder<DaoQueryOptionsImpl, DaoQueryOptionsImplBuilder> {

        private List<String> attrs;
        private Long offset, positionOfCard, limit;
        private CmdbSorter sorter;
        private CmdbFilter filter;
        private Boolean goToPage;

        public DaoQueryOptionsImplBuilder withPaging(@Nullable Long offset, @Nullable Long limit) {
            return this.withOffset(offset).withLimit(limit);
        }

        public DaoQueryOptionsImplBuilder withPaging(@Nullable Integer offset, @Nullable Integer limit) {
            return this.withOffset(toLongOrNull(offset)).withLimit(toLongOrNull(limit));
        }

        public DaoQueryOptionsImplBuilder withOffset(@Nullable Long offset) {
            this.offset = offset;
            return this;
        }

        public DaoQueryOptionsImplBuilder withLimit(@Nullable Long limit) {
            this.limit = limit;
            return this;
        }

        public DaoQueryOptionsImplBuilder withSorter(CmdbSorter sorter) {
            this.sorter = sorter;
            return this;
        }

        public DaoQueryOptionsImplBuilder withAttrs(@Nullable List<String> attrs) {
            this.attrs = attrs;
            return this;
        }

        public DaoQueryOptionsImplBuilder withSorter(@Nullable String sorter) {
            return this.withSorter(CmdbSorterUtils.parseSorter(sorter));
        }

        public DaoQueryOptionsImplBuilder withFilter(CmdbFilter filter) {
            this.filter = filter;
            return this;
        }

        public DaoQueryOptionsImplBuilder withFilter(@Nullable String filter) {
            return this.withFilter(CmdbFilterUtils.parseFilter(filter));
        }

        public DaoQueryOptionsImplBuilder withPositionOf(@Nullable Long positionOfCard, @Nullable Boolean goToPage) {
            this.positionOfCard = positionOfCard;
            this.goToPage = goToPage;
            return this;
        }

        @Override
        public DaoQueryOptionsImpl build() {
            return new DaoQueryOptionsImpl(this);
        }

        public DaoQueryOptionsImplBuilder orderBy(String attr, SorterElement.SorterElementDirection dir) {
            SorterElementImpl element = new SorterElementImpl(ATTR_BEGINDATE, DESC);
            if (sorter == null) {
                sorter = new CmdbSorterImpl(element);
            } else {
                sorter = new CmdbSorterImpl(list(sorter.getElements()).with(element));
            }
            return this;
        }

    }
}
