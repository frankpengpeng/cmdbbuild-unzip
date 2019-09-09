/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm;

import org.cmdbuild.utils.lang.Builder;
import org.cmdbuild.dao.entrytype.Classe;

public interface CardMapperRepository {

	CardMapper get(Classe theClass);

	<T, B extends Builder<T, B>> CardMapper<T, B> get(Class<T> theClass);

	void put(CardMapper cardMapper);

	<T, B extends Builder<T, B>> CardMapper<T, B> get(String classId);

	CardMapper getByBuilderClassOrBeanClass(Class builderClass);
}
