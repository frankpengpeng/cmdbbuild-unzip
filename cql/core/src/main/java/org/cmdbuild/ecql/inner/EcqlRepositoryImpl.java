/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.ecql.inner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.List;
import org.cmdbuild.cql.EcqlException;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.ecql.EcqlExpression;
import org.cmdbuild.ecql.EcqlId;
import org.cmdbuild.ecql.EcqlRepository;
import static org.cmdbuild.ecql.utils.EcqlUtils.parseEcqlId;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.ecql.utils.EcqlUtils.getEcqlExpressionFromClassAttributeFilter;

@Component
public class EcqlRepositoryImpl implements EcqlRepository {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ClasseRepository classeRepository;

	public EcqlRepositoryImpl(ClasseRepository classeRepository) {
		this.classeRepository = checkNotNull(classeRepository);
	}

	@Override
	public EcqlExpression getById(String encodedId) {
		logger.debug("get expression for ecql id = {}", encodedId);
		try {
			EcqlId<List<String>> ecqlId = parseEcqlId(encodedId);
			logger.debug("decoded ecql id = {}", ecqlId);
			switch (ecqlId.getSource()) {
				case CLASS_ATTRIBUTE:
					checkArgument(ecqlId.getId().size() == 2);
					String classId = checkNotBlank(ecqlId.getId().get(0));
					String attributeId = checkNotBlank(ecqlId.getId().get(1));
					return getFromClassAttribute(classId, attributeId);
				case EMBEDDED:
					return new EcqlExpressionImpl(getOnlyElement(ecqlId.getId()), emptyMap());
				default:
					throw new UnsupportedOperationException(format("unsupported ecql source = %s", ecqlId.getSource()));
			}
		} catch (Exception ex) {
			throw new EcqlException(ex, "ecql expression not found for id = %s", encodedId);
		}
	}

	private EcqlExpression getFromClassAttribute(String classId, String attributeId) {
		Classe classe = classeRepository.getClasse(classId);
		Attribute attribute = classe.getAttribute(attributeId);
		return getEcqlExpressionFromClassAttributeFilter(attribute);
	}

}
