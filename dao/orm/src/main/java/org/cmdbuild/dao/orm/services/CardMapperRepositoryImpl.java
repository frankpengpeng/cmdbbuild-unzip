/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm.services;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.dao.orm.CardMapper;
import org.cmdbuild.dao.orm.CardMapperRepository;
import org.cmdbuild.utils.lang.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

@Component
public class CardMapperRepositoryImpl implements CardMapperRepository {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, CardMapper> mappersByClassName = new ConcurrentHashMap<>();
	private final Map<Class, CardMapper> mappersByObjectClass = new ConcurrentHashMap<>();
	private final Map<Class, CardMapper> mappersByRelatedClass = new ConcurrentHashMap<>();

	@Override
	public CardMapper get(Classe theClass) {
		String classId = checkNotNull(theClass, "class param is null").getName();
		return get(classId);
	}

	@Override
	public <T, B extends Builder<T, B>> CardMapper<T, B> get(String classId) {
		checkNotNull(classId, "classId param is null");
		return checkNotNull(mappersByClassName.get(classId), "card mapper not found for class name = %s", classId);
	}

	@Override
	public <T, B extends Builder<T, B>> CardMapper<T, B> get(Class<T> theClass) {
		checkNotNull(theClass, "class param is null");
		return checkNotNull(mappersByObjectClass.get(theClass), "card mapper not found for object class = %s", theClass);
	}

	@Override
	public void put(CardMapper cardMapper) {
		logger.debug("register card mapper = {}", cardMapper);
		String classId = cardMapper.getClassId();
		if (isNotBlank(classId)) {
			CardMapper other = mappersByClassName.get(classId);
			if (other == null || (cardMapper.isPrimaryMapper() && !other.isPrimaryMapper())) {
				mappersByClassName.put(cardMapper.getClassId(), cardMapper);
			} else if (other.isPrimaryMapper() && !cardMapper.isPrimaryMapper()) {
				//do nothing, keep other
			} else {
				throw runtime("duplicate card mapper found for class id = %s (remove one or use the @Primary annotation)", classId);
			}
		}
		Collection<Class> classes = getAllClasses(cardMapper.getTargetClass());
		logger.debug("register mapper for target classes = {}", classes);
		classes.forEach((iface) -> {
			checkArgument(mappersByObjectClass.put(iface, cardMapper) == null, "duplicate mapper found for interface = %s", iface);
			checkArgument(mappersByRelatedClass.put(iface, cardMapper) == null, "duplicate mapper found for interface = %s", iface);
		});
		checkArgument(mappersByRelatedClass.put(cardMapper.getBuilderClass(), cardMapper) == null, "duplicate mapper found for interface = %s", cardMapper.getBuilderClass());
	}

	private Collection<Class> getAllClasses(Class targetClass) {
		Collection<Class> classes = new HashSet<>();
		classes.add(targetClass);
		classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));
		classes.removeIf((thisClass) -> !thisClass.getPackage().getName().startsWith("org.cmdbuild"));
		return classes;
	}

	@Override
	public CardMapper getByBuilderClassOrBeanClass(Class builderClassOrBeanClass) {
		checkNotNull(builderClassOrBeanClass, "builderClassOrBeanClass param is null");
		return checkNotNull(mappersByRelatedClass.get(builderClassOrBeanClass), "card mapper not found for bean or builder class = %s", builderClassOrBeanClass);
	}

}
