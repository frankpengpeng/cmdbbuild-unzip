/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.inner;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class DocumentPathUtils {

	public static String buildDocumentPath(Classe classe, long cardId) {
		return list(classe.getAncestorsAndSelf().stream().filter(not(equalTo(BASE_CLASS_NAME))).collect(toList())).with(format("Id%s", cardId)).stream().collect(joining("/"));
	}

}
