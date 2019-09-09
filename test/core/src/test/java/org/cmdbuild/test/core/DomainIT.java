/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_MANY;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class DomainIT {

    private final DaoService dao;

    public DomainIT(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testDomainAttrs() {
        Classe one = dao.createClass(ClassDefinitionImpl.build(tuid("One")));
        Classe two = dao.createClass(ClassDefinitionImpl.build(tuid("Two")));

        Domain domain1 = dao.createDomain(DomainDefinitionImpl.builder().withName(tuid("MyDomain1_")).withSourceClass(one).withTargetClass(two).withCardinality(MANY_TO_MANY)
                .withMetadata(b -> b.withInline(true).withDefaultClosed(true)).build());

        assertTrue(domain1.getMetadata().isInline());
        assertTrue(domain1.getMetadata().isDefaultClosed());

        Domain domain2 = dao.createDomain(DomainDefinitionImpl.builder().withName(tuid("MyDomain2_")).withSourceClass(one).withTargetClass(two).withCardinality(MANY_TO_MANY)
                .withMetadata(b -> b.withInline(true).withDefaultClosed(false)).build());

        assertTrue(domain2.getMetadata().isInline());
        assertFalse(domain2.getMetadata().isDefaultClosed());

        Domain domain3 = dao.createDomain(DomainDefinitionImpl.builder().withName(tuid("MyDomain3_")).withSourceClass(one).withTargetClass(two).withCardinality(MANY_TO_MANY)
                .withMetadata(b -> b.withInline(false).withDefaultClosed(false)).build());

        assertFalse(domain3.getMetadata().isInline());
        assertFalse(domain3.getMetadata().isDefaultClosed());
    }

}
