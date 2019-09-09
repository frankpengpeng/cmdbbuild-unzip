/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class DaoIT {

    private final DaoService dao;

    public DaoIT(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Test
    public void testDao() {
        List<ResultRow> rows = dao.selectAll().from("User").run();
        assertTrue(rows.size() >= 1);
    }

    @Test
    public void testDao2() {
        List<ResultRow> rows = dao.selectAll().from("Role").run();
        assertTrue(rows.size() >= 1);
    }

    @Test
    public void testDao4() {
        List<Classe> list = dao.getAllClasses();
        assertFalse(list.isEmpty());
    }

}
