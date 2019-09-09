package org.cmdbuild.extcomponents.custompage;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.annotation.Nullable;

public interface CustomPageRepository {

	List<CustomPageData> getAll();

	CustomPageData create(CustomPageData customPage);

	CustomPageData update(CustomPageData customPage);

	void delete(long id);

	@Nullable
	CustomPageData getByNameOrNull(String name);

	default CustomPageData getByName(String name) {
		return checkNotNull(getByNameOrNull(name), "page not found for name = %s", name);
	}

	CustomPageData getById(long id);

}
