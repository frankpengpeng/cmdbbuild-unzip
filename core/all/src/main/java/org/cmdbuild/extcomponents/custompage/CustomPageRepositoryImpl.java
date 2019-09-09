package org.cmdbuild.extcomponents.custompage;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.onlyElement;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;

@Component
public class CustomPageRepositoryImpl implements CustomPageRepository {

	private final DaoService dao;
	private final Holder<List<CustomPageData>> customPagesHolder;

	public CustomPageRepositoryImpl(DaoService dao, CacheService cacheService) {
		this.dao = checkNotNull(dao);
		customPagesHolder = cacheService.newHolder("all_custom_pages");
	}

	private void invalidateCache() {
		customPagesHolder.invalidate();
	}

	@Override
	public List<CustomPageData> getAll() {
		return customPagesHolder.get(this::doReadAll);
	}

	@Override
	@Nullable
	public CustomPageData getByNameOrNull(String name) {
		checkNotBlank(name);
		return getAll().stream().filter((c) -> equal(c.getName(), name)).collect(toOptional()).orElse(null);
	}

	@Override
	public CustomPageData getById(long id) {
		return getAll().stream().filter((c) -> equal(c.getId(), id)).collect(onlyElement());
	}

	@Override
	public CustomPageData create(CustomPageData customPage) {
		customPage = dao.create(customPage);
		invalidateCache();
		return customPage;
	}

	@Override
	public CustomPageData update(CustomPageData customPage) {
		customPage = dao.update(customPage);
		invalidateCache();
		return customPage;
	}

	@Override
	public void delete(long id) {
		dao.delete(CustomPageDataImpl.class, id);
		invalidateCache();
	}

	private List<CustomPageData> doReadAll() {
		return dao.selectAll().from(CustomPageDataImpl.class).asList();
	}

}
