/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.repository;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.singletonList;
import java.util.List;
import org.cmdbuild.dao.entrytype.Attribute;

public interface AttributeRepository {

	/**
	 * Creates a new attribute.
	 *
	 * @param definition contains the definition needed for creating the new
	 * attribute.
	 *
	 * @return the created attribute.
	 */
	Attribute createAttribute(Attribute definition);

	List<Attribute> updateAttributes(List<Attribute> attributes);

	/**
	 * Updates an existing attribute.
	 *
	 * @param definition contains the definition needed for updating the
	 * existing attribute.
	 *
	 * @return the created attribute.
	 */
	default Attribute updateAttribute(Attribute definition) {
		return getOnlyElement(updateAttributes(singletonList(definition)));
	}

	/**
	 * Delete an existing attribute.
	 *
	 * @param dbAttribute the existing attribute.
	 */
	void deleteAttribute(Attribute dbAttribute);
}
