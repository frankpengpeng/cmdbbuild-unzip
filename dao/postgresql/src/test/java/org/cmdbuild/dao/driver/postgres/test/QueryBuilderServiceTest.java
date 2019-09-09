/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.postgres.test;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import javax.inject.Provider;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.dao.beans.FunctionMetadataImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.core.q3.QueryBuilderService;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.driver.repository.ClasseReadonlyRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.dao.postgres.QueryService;
import org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl;
import org.cmdbuild.ecql.EcqlService;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.cmdbuild.dao.driver.repository.FunctionRepository;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwnerImpl;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.function.StoredFunctionImpl;
import org.cmdbuild.dao.postgres.q3.QueryBuilderHelperServiceImpl;
import org.cmdbuild.dao.postgres.q3.PreparedQueryHelperServiceImpl;
import org.cmdbuild.dao.postgres.q3.QueryBuilderHelperService;
import org.cmdbuild.dao.postgres.q3.PreparedQueryHelperService;
import static org.cmdbuild.data.filter.SorterElement.SorterElementDirection.ASC;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.data.filter.beans.FunctionFilterImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.junit.Before;

public class QueryBuilderServiceTest {

    private final ClasseReadonlyRepository classeRepository = mock(ClasseReadonlyRepository.class);
    private final FunctionRepository functionRepository = mock(FunctionRepository.class);
    private final DomainRepository domainRepository = mock(DomainRepository.class);
    private final QueryService queryService = mock(QueryService.class);
    private final CardMapperService mapper = mock(CardMapperService.class);
    private final EcqlService ecqlService = mock(EcqlService.class);
    private final OperationUser user = mock(OperationUser.class);
    private final Provider<OperationUserSupplier> operationUserSupplier = () -> () -> user;

    private final PreparedQueryHelperService executorService = new PreparedQueryHelperServiceImpl(classeRepository, domainRepository, queryService, mapper);
    private final QueryBuilderHelperService processorService = new QueryBuilderHelperServiceImpl(operationUserSupplier, executorService, functionRepository, classeRepository, domainRepository, ecqlService);
    private final QueryBuilderService service = new QueryBuilderServiceImpl(classeRepository, functionRepository, domainRepository, mapper, processorService);

//    private final List<AttributeWithoutOwner> attrs = list();//AttributeWithoutOwnerImpl.builder().withName(ATTR_ID).withType(LongAttributeType.INSTANCE).build());
    private final Classe one = ClasseImpl.builder()
            .withId(1l)
            .withName("MyClass")
            .build(),
            two = ClasseImpl.builder()
                    .withId(2l)
                    .withName("OtherClass")
                    .withAttributes(list(AttributeWithoutOwnerImpl.builder().withName("MyAttr").withType(new ForeignKeyAttributeType(one)).build()))
                    .build(),
            three = ClasseImpl.builder()
                    .withId(3l)
                    .withName("ThirdClass")
                    .withAttributes(list(AttributeWithoutOwnerImpl.builder().withName(ATTR_CODE).withType(new StringAttributeType()).build(),
                            AttributeWithoutOwnerImpl.builder().withName(ATTR_DESCRIPTION).withType(new StringAttributeType()).build()))
                    .build(),
            four = ClasseImpl.builder()
                    .withId(4l)
                    .withName("FourthClass")
                    .withAttributes(list(AttributeWithoutOwnerImpl.builder().withName("MyAttr").withType(new ForeignKeyAttributeType(three)).build()))
                    .build(),
            five = ClasseImpl.builder()
                    .withId(5l)
                    .withName("FifthClass")
                    .withAttributes(list(AttributeWithoutOwnerImpl.builder().withName("MyLoAttr").withType(new LookupAttributeType("Loo")).build()))
                    .build(),
            lookup = ClasseImpl.builder()
                    .withId(6l)
                    .withName("LookUp")
                    .withAttributes(list(AttributeWithoutOwnerImpl.builder().withName(ATTR_CODE).withType(new StringAttributeType()).build(),
                            AttributeWithoutOwnerImpl.builder().withName(ATTR_DESCRIPTION).withType(new StringAttributeType()).build()))
                    .build(),
            six = ClasseImpl.builder()
                    .withId(7l)
                    .withName("SeClass")
                    .withAttributes(list(AttributeWithoutOwnerImpl.builder().withName("MyLoAttr").withType(new LookupAttributeType("Loo")).build()))
                    .build();

    private String actualQuery;

    @Before
    public void init() {
        list(one, two, three, four, five, lookup, six).forEach(c -> when(classeRepository.getClasse(c.getName())).thenReturn(c));
        when(queryService.query(anyString(), anyObject())).then((InvocationOnMock invocation) -> {
            actualQuery = invocation.getArgumentAt(0, String.class);
            return emptyList();
        });

        when(user.getId()).thenReturn(1l);
        when(user.hasDefaultGroup()).thenReturn(false);

        StoredFunction fun = StoredFunctionImpl.builder().withName("my_function").withId(1l).withReturnSet(false).withMetadata(new FunctionMetadataImpl(emptyMap())).build();
        when(functionRepository.getFunctionByName("my_function")).thenReturn(fun);
    }

    @Test
    public void testSimpleQuery() {
        List<ResultRow> result = service.query().selectAll().from(one).run();

        assertTrue(result.isEmpty());
        assertEquals("SELECT \"Id\" _id, \"IdClass\" _idclass, \"CurrentId\" _currentid, \"User\" _user, \"BeginDate\" _begindate, \"EndDate\" _enddate, \"Status\" _status FROM \"MyClass\" _myclass WHERE \"Status\" = 'A'", actualQuery);
    }

    @Test
    public void testSimpleJoinQuery1() {
        List<ResultRow> result = service.query().selectAll().from(one)
                .join(two).onEq(ATTR_ID, "MyAttr").then().run();

        assertTrue(result.isEmpty());
        assertEquals("SELECT _myclass.\"Id\" _id, _myclass.\"IdClass\" _idclass, _myclass.\"CurrentId\" _currentid, _myclass.\"User\" _user, _myclass.\"BeginDate\" _begindate, _myclass.\"EndDate\" _enddate, _myclass.\"Status\" _status FROM \"MyClass\" _myclass LEFT JOIN \"OtherClass\" _otherclass ON _myclass.\"Id\" = _otherclass.\"MyAttr\" AND _otherclass.\"Status\" = 'A' WHERE _myclass.\"Status\" = 'A'", actualQuery);
    }

    @Test
    public void testSimpleJoinQuery2() {
        List<ResultRow> result = service.query().selectAll().from(one)
                .join(two).onEq(ATTR_ID, "MyAttr").select(ATTR_CODE).run();

        assertTrue(result.isEmpty());
        assertEquals("SELECT _myclass.\"Id\" _id, _myclass.\"IdClass\" _idclass, _myclass.\"CurrentId\" _currentid, _myclass.\"User\" _user, _myclass.\"BeginDate\" _begindate, _myclass.\"EndDate\" _enddate, _myclass.\"Status\" _status, _otherclass.\"Code\" _code FROM \"MyClass\" _myclass LEFT JOIN \"OtherClass\" _otherclass ON _myclass.\"Id\" = _otherclass.\"MyAttr\" AND _otherclass.\"Status\" = 'A' WHERE _myclass.\"Status\" = 'A'", actualQuery);
    }

    @Test
    public void testSimpleJoinQuery3() {
        List<ResultRow> result = service.query().selectAll().from(one)
                .join(two).onEq(ATTR_ID, "MyAttr").selectAll().run();

        assertTrue(result.isEmpty());
        assertEquals("SELECT _myclass.\"Id\" _id, _myclass.\"IdClass\" _idclass, _myclass.\"CurrentId\" _currentid, _myclass.\"User\" _user, _myclass.\"BeginDate\" _begindate, _myclass.\"EndDate\" _enddate, _myclass.\"Status\" _status, _otherclass.\"Id\" _id2p, _otherclass.\"IdClass\" _idclass2r, _otherclass.\"CurrentId\" _currentid2t, _otherclass.\"User\" _user2v, _otherclass.\"BeginDate\" _begindate2x, _otherclass.\"EndDate\" _enddate2z, _otherclass.\"Status\" _status31, _otherclass.\"MyAttr\" _myattr FROM \"MyClass\" _myclass LEFT JOIN \"OtherClass\" _otherclass ON _myclass.\"Id\" = _otherclass.\"MyAttr\" AND _otherclass.\"Status\" = 'A' WHERE _myclass.\"Status\" = 'A'", actualQuery);
    }

    @Test
    public void testCodeDescJoinQuery1() {
        List<ResultRow> result = service.query().selectAll().from(five).run();

        assertTrue(result.isEmpty());
        assertEquals("SELECT _fifthclass.\"Id\" _id, _fifthclass.\"IdClass\" _idclass, _fifthclass.\"CurrentId\" _currentid, _fifthclass.\"User\" _user, _fifthclass.\"BeginDate\" _begindate, _fifthclass.\"EndDate\" _enddate, _fifthclass.\"Status\" _status, _fifthclass.\"MyLoAttr\" _myloattr, _myloattr2p.\"Code\" _myloattrcode, _myloattr2p.\"Description\" _myloattrdescription FROM \"FifthClass\" _fifthclass LEFT JOIN \"LookUp\" _myloattr2p ON _myloattr2p.\"Id\" = _fifthclass.\"MyLoAttr\" AND _myloattr2p.\"Status\" = 'A' WHERE _fifthclass.\"Status\" = 'A'", actualQuery);
    }

    @Test
    public void testDistinctQuery() {
        List<ResultRow> result = service.query().selectDistinct(ATTR_CODE).from(five).run();

        assertTrue(result.isEmpty());
        assertEquals("SELECT DISTINCT _code FROM ( SELECT \"Code\" _code, \"Id\" _id, \"IdClass\" _idclass, \"CurrentId\" _currentid, \"User\" _user, \"BeginDate\" _begindate, \"EndDate\" _enddate, \"Status\" _status FROM \"FifthClass\" _fifthclass WHERE \"Status\" = 'A' ) _y", actualQuery);
    }

    @Test
    public void testDistinctQueryWithLookup() {
        List<ResultRow> result = service.query().selectDistinct("MyLoAttr").from(five).run();

        assertTrue(result.isEmpty());
        assertEquals("SELECT DISTINCT _myloattr, _myloattrcode, _myloattrdescription FROM ( SELECT _fifthclass.\"MyLoAttr\" _myloattr, _fifthclass.\"Id\" _id, _fifthclass.\"IdClass\" _idclass, _fifthclass.\"CurrentId\" _currentid, _fifthclass.\"User\" _user, _fifthclass.\"BeginDate\" _begindate, _fifthclass.\"EndDate\" _enddate, _fifthclass.\"Status\" _status, _myloattr2p.\"Code\" _myloattrcode, _myloattr2p.\"Description\" _myloattrdescription FROM \"FifthClass\" _fifthclass LEFT JOIN \"LookUp\" _myloattr2p ON _myloattr2p.\"Id\" = _fifthclass.\"MyLoAttr\" AND _myloattr2p.\"Status\" = 'A' WHERE _fifthclass.\"Status\" = 'A' ) _y", actualQuery);
    }

    @Test
    public void testDistinctCountQuery() {
        List<ResultRow> result = service.query().selectDistinct(ATTR_CODE).selectCount().from(five).run();

        assertTrue(result.isEmpty());
        assertEquals("SELECT _code, COUNT(*) _cardcount FROM ( SELECT \"Code\" _code, \"Id\" _id, \"IdClass\" _idclass, \"CurrentId\" _currentid, \"User\" _user, \"BeginDate\" _begindate, \"EndDate\" _enddate, \"Status\" _status FROM \"FifthClass\" _fifthclass WHERE \"Status\" = 'A' ) _y GROUP BY _code", actualQuery);
    }

    @Test
    public void testJoinQueryForOrder() {
        service.query().select(ATTR_ID).from(six).run();
        assertEquals("SELECT \"Id\" _id, \"IdClass\" _idclass, \"CurrentId\" _currentid, \"User\" _user, \"BeginDate\" _begindate, \"EndDate\" _enddate, \"Status\" _status FROM \"SeClass\" _seclass WHERE \"Status\" = 'A'", actualQuery);

        service.query().select(ATTR_ID).from(six).orderBy("MyLoAttr", ASC).run();
        assertEquals("SELECT _seclass.\"Id\" _id, _seclass.\"IdClass\" _idclass, _seclass.\"CurrentId\" _currentid, _seclass.\"User\" _user, _seclass.\"BeginDate\" _begindate, _seclass.\"EndDate\" _enddate, _seclass.\"Status\" _status, _seclass.\"MyLoAttr\" _myloattr, _myloattr2p.\"Code\" _myloattrcode, _myloattr2p.\"Description\" _myloattrdescription FROM \"SeClass\" _seclass LEFT JOIN \"LookUp\" _myloattr2p ON _myloattr2p.\"Id\" = _seclass.\"MyLoAttr\" AND _myloattr2p.\"Status\" = 'A' WHERE _seclass.\"Status\" = 'A' ORDER BY _myloattrdescription ASC", actualQuery);
    }

    @Test
    public void testQuery2() {
        List<ResultRow> result = service.query().select(ATTR_ID, ATTR_CODE, ATTR_DESCRIPTION).from(five).where(ATTR_ID, EQ, 123).run();

        assertTrue(result.isEmpty());
        assertEquals("SELECT \"Id\" _id, \"Code\" _code, \"Description\" _description, \"IdClass\" _idclass, \"CurrentId\" _currentid, \"User\" _user, \"BeginDate\" _begindate, \"EndDate\" _enddate, \"Status\" _status FROM \"FifthClass\" _fifthclass WHERE \"Id\" = 123 AND \"Status\" = 'A'", actualQuery);
    }

    @Test
    public void testQueryWithExpr() {
        List<ResultRow> result = service.query().selectAll().selectExpr("my_expr", "lower(\"Code\")").from(five).where(ATTR_ID, EQ, 123).run();

        assertTrue(result.isEmpty());
        assertEquals("SELECT lower(_fifthclass.\"Code\") _myexpr, _fifthclass.\"Id\" _id, _fifthclass.\"IdClass\" _idclass, _fifthclass.\"CurrentId\" _currentid, _fifthclass.\"User\" _user, _fifthclass.\"BeginDate\" _begindate, _fifthclass.\"EndDate\" _enddate, _fifthclass.\"Status\" _status, _fifthclass.\"MyLoAttr\" _myloattr, _myloattr2p.\"Code\" _myloattrcode, _myloattr2p.\"Description\" _myloattrdescription FROM \"FifthClass\" _fifthclass LEFT JOIN \"LookUp\" _myloattr2p ON _myloattr2p.\"Id\" = _fifthclass.\"MyLoAttr\" AND _myloattr2p.\"Status\" = 'A' WHERE _fifthclass.\"Id\" = 123 AND _fifthclass.\"Status\" = 'A'", actualQuery);
    }

    @Test
    public void testQueryWithExpr2() {
        List<ResultRow> result = service.query().selectAll().selectExpr("_fordomain_hasthisrelation", "(Q3_MASTER.\"Id\" IN (SELECT \"IdObj2\" FROM \"MyMap\" WHERE \"IdObj1\" = 123 AND \"Status\" = 'A'))").from(five).where(ATTR_ID, EQ, 123).run();

        assertTrue(result.isEmpty());
        assertEquals("SELECT (_fifthclass.\"Id\" IN (SELECT \"IdObj2\" FROM \"MyMap\" WHERE \"IdObj1\" = 123 AND \"Status\" = 'A')) _fordomainhasthisrelation, _fifthclass.\"Id\" _id, _fifthclass.\"IdClass\" _idclass, _fifthclass.\"CurrentId\" _currentid, _fifthclass.\"User\" _user, _fifthclass.\"BeginDate\" _begindate, _fifthclass.\"EndDate\" _enddate, _fifthclass.\"Status\" _status, _fifthclass.\"MyLoAttr\" _myloattr, _myloattr2p.\"Code\" _myloattrcode, _myloattr2p.\"Description\" _myloattrdescription FROM \"FifthClass\" _fifthclass LEFT JOIN \"LookUp\" _myloattr2p ON _myloattr2p.\"Id\" = _fifthclass.\"MyLoAttr\" AND _myloattr2p.\"Status\" = 'A' WHERE _fifthclass.\"Id\" = 123 AND _fifthclass.\"Status\" = 'A'", actualQuery);
    }

    @Test
    public void testQueryWithFunctionFilterAndJoin() {

        List<ResultRow> result = service.query().selectAll().from(five).where(CmdbFilterImpl.builder().withFunctionFilter(new FunctionFilterImpl("my_function")).build()).run();

        assertTrue(result.isEmpty());
        assertEquals("SELECT _fifthclass.\"Id\" _id, _fifthclass.\"IdClass\" _idclass, _fifthclass.\"CurrentId\" _currentid, _fifthclass.\"User\" _user, _fifthclass.\"BeginDate\" _begindate, _fifthclass.\"EndDate\" _enddate, _fifthclass.\"Status\" _status, _fifthclass.\"MyLoAttr\" _myloattr, _myloattr2p.\"Code\" _myloattrcode, _myloattr2p.\"Description\" _myloattrdescription FROM \"FifthClass\" _fifthclass LEFT JOIN \"LookUp\" _myloattr2p ON _myloattr2p.\"Id\" = _fifthclass.\"MyLoAttr\" AND _myloattr2p.\"Status\" = 'A' WHERE _fifthclass.\"Id\" IN (SELECT my_function(1,NULL,'FifthClass')) AND _fifthclass.\"Status\" = 'A'", actualQuery);
    }

    @Test
    public void testQueryWithFunctionFilterWithoutJoin() {
        when(user.getId()).thenReturn(1l);
        when(user.hasDefaultGroup()).thenReturn(false);

        StoredFunction fun = StoredFunctionImpl.builder().withName("my_function").withId(1l).withReturnSet(false).withMetadata(new FunctionMetadataImpl(emptyMap())).build();
        when(functionRepository.getFunctionByName("my_function")).thenReturn(fun);

        List<ResultRow> result = service.query().selectAll().from(one).where(CmdbFilterImpl.builder().withFunctionFilter(new FunctionFilterImpl("my_function")).build()).run();

        assertTrue(result.isEmpty());
        assertEquals("SELECT \"Id\" _id, \"IdClass\" _idclass, \"CurrentId\" _currentid, \"User\" _user, \"BeginDate\" _begindate, \"EndDate\" _enddate, \"Status\" _status FROM \"MyClass\" _myclass WHERE \"Id\" IN (SELECT my_function(1,NULL,'MyClass')) AND \"Status\" = 'A'", actualQuery);
    }

}
