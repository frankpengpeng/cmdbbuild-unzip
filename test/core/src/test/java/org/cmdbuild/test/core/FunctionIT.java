/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.IdAndDescription;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.function.StoredFunctionOutputParameterImpl;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class FunctionIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final CacheService cacheService;

    public FunctionIT(DaoService dao, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        this.cacheService = checkNotNull(cacheService);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testStoredFunctionQueryWithReference() {
        Classe myClass = dao.createClass(ClassDefinitionImpl.build(tuid("MyClass")));
        Card one = dao.create(CardImpl.buildCard(myClass, ATTR_CODE, "one", ATTR_DESCRIPTION, "The One"));
        Card two = dao.create(CardImpl.buildCard(myClass, ATTR_CODE, "two", ATTR_DESCRIPTION, "The Other"));

        String myFunctionName = tuid("_test_function").toLowerCase();
        logger.error(myFunctionName);
        dao.getJdbcTemplate().execute(format("CREATE OR REPLACE FUNCTION %s(_param varchar, OUT _out bigint) RETURNS bigint AS $$ BEGIN SELECT INTO _out \"Id\" FROM \"%s\" WHERE \"Code\" = _param AND \"Status\" = 'A'; END $$ LANGUAGE PLPGSQL;"
                + "COMMENT ON FUNCTION %s(_param varchar, OUT _out bigint) IS 'TYPE: function';", myFunctionName, myClass.getName(), myFunctionName));
        cacheService.invalidateAll();
        logger.error(myFunctionName);

        ResultRow row = dao.selectFunction(myFunctionName, list("two"), list(new StoredFunctionOutputParameterImpl("_out", new ForeignKeyAttributeType(myClass)))).getSingleRow();
        logger.info("result = \n\n{}\n", mapToLoggableString(row.asMap()));
        assertEquals(two.getId(), row.get("_out", Long.class));
        assertEquals("The Other", row.get("_out", IdAndDescription.class).getDescription());
        assertEquals("two", row.get("_out", IdAndDescription.class).getCode());
        logger.error(myFunctionName);

        row = dao.selectFunction(myFunctionName, list("one"), list(new StoredFunctionOutputParameterImpl("_out", new ForeignKeyAttributeType(myClass)))).getSingleRow();
        logger.info("result = \n\n{}\n", mapToLoggableString(row.asMap()));
        assertEquals(one.getId(), row.get("_out", Long.class));
        assertEquals("The One", row.get("_out", IdAndDescription.class).getDescription());
        assertEquals("one", row.get("_out", IdAndDescription.class).getCode());
        logger.error(myFunctionName);
    }

}
