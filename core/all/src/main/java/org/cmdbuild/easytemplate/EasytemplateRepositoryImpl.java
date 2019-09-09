package org.cmdbuild.easytemplate;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;

import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.easytemplate.EasytemplateImpl.TEMPLATE_NAME;
import org.cmdbuild.easytemplate.store.EasytemplateRepository;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EasytemplateRepositoryImpl implements EasytemplateRepository {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final DaoService dao;

	public EasytemplateRepositoryImpl(DaoService dao) {
		this.dao = checkNotNull(dao);
	}

	@Override
	@Nullable
	public String getTemplate(String name) {
		Easyemplate template = dao.selectAll().from(EasytemplateImpl.class).where(TEMPLATE_NAME, EQ, checkNotBlank(name)).asModelOrNull();
		if (template == null) {
			logger.warn("template not found for name = {}", name);
			return null;
		} else {
			return template.getValue();
		}
	}

}
