/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import java.util.List;
import org.cmdbuild.dao.core.q3.PreparedQuery;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.orm.CardMapper;

public interface PreparedQueryHelperService {

    PreparedQuery prepareQuery(String query, List<SelectElement> preparedQuerySelect, List<WhereElement> where, EntryType from, CardMapper cardMapper);

}
