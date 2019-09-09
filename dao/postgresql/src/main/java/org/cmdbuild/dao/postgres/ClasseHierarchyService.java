/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres;

import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.graph.ClasseHierarchy;

public interface ClasseHierarchyService {

	ClasseHierarchy getClasseHierarchy(Classe classe);

}
