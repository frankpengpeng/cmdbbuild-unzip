/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import org.cmdbuild.dao.core.q3.PreparedQuery;
import org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.QueryBuilderImpl;

public interface QueryBuilderHelperService {

    PreparedQuery buildQuery(QueryBuilderImpl source);//TODO remove dependency from concrete class QueryBuilderImpl
}
