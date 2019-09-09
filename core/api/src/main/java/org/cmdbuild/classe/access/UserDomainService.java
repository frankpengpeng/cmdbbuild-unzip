/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Domain;

public interface UserDomainService {

    List<Domain> getUserDomains();

    Domain getUserDomain(String domainId);

    PagedElements<CMRelation> getUserRelations(String domainId, DaoQueryOptions queryOptions);

    PagedElements<CMRelation> getUserRelationsForCard(String classId, long cardId, DaoQueryOptions queryOptions);

    CMRelation getUserRelation(String domainId, long relationId);

    default List<Domain> getActiveUserDomains() {
        return getUserDomains().stream().filter(Domain::isActive).collect(toList());
    }
}
