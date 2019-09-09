/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm.services;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.orm.CardMapper;
import org.cmdbuild.dao.orm.CardMapperRepository;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.utils.lang.Builder;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.driver.repository.ClasseRepository;

@Component
@DependsOn("cardMapperLoader")
public class CardMapperServiceImpl implements CardMapperService {

	private final CardMapperRepository mapperRepository;
	private final ClasseRepository classeRepository;

	public CardMapperServiceImpl(CardMapperRepository mapperRepository, ClasseRepository classeRepository) {
		this.mapperRepository = checkNotNull(mapperRepository);
		this.classeRepository = checkNotNull(classeRepository);
	}

	@Override
	public Card objectToCard(Object object) {
		checkNotNull(object, "object instance cannot be null");
		if (object instanceof Builder) {
			object = ((Builder) object).build();
		}
		CardMapper mapper = mapperRepository.get(object.getClass());
		return mapper.objectToCard(CardImpl.builder(), object).withType(classeRepository.getClasse(mapper.getClassId())).build();
	}

	@Override
	public <T> T cardToObject(Card card) {
		CardMapper<T, ?> mapper = mapperRepository.get(card.getType());
		return mapper.cardToObject(card).build();
	}

	@Override
	public <T, B extends Builder<T, B>> CardMapper<T, B> getMapperForModelOrBuilder(Class model) {
		return mapperRepository.getByBuilderClassOrBeanClass(model);
	}

	@Override
	public Classe getClasseForModelOrBuilder(Class builderOrBeanClass) {
		CardMapper mapper = getMapperForModelOrBuilder(builderOrBeanClass);
		String classId = mapper.getClassId();
		return classeRepository.getClasse(classId);
	}

	@Override
	public CardMapper getMapperForClasse(Classe classe) {
		return mapperRepository.get(classe);
	}

}
