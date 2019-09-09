/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.dao.entrytype.ClassDefinition;

public interface ExtendedClassDefinition extends ExtendedClassData {

	ClassDefinition getClassDefinition();

	List<Pair<String, Direction>> getDefaultClassOrdering();

	enum Direction {
		ASC, DESC
	}
}
