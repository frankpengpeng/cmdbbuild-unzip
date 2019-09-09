/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toSet;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisAttributeImpl;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.gis.stylerules.GisStyleRulesService;
import org.cmdbuild.gis.stylerules.GisStyleRuleset;
import static org.cmdbuild.gis.stylerules.GisStyleRulesetAnalysisType.AT_GRADUAL;
import static org.cmdbuild.gis.stylerules.GisStyleRulesetAnalysisType.AT_INTERVALS;
import static org.cmdbuild.gis.stylerules.GisStyleRulesetAnalysisType.AT_PUNCTUAL;
import org.cmdbuild.gis.stylerules.GisStyleRulesetImpl;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class GeoStyleRulesIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final GisStyleRulesService service;
    private final GlobalConfigService configService;
    private final GisService gisService;
    private final CacheService cacheService;

    private Classe myClass;
    private GisAttribute gisAttribute;
    private Card one, two, three;

    public GeoStyleRulesIT(CacheService cacheService, DaoService dao, GisStyleRulesService service, GlobalConfigService configService, GisService gisService) {
        this.dao = checkNotNull(dao);
        this.service = checkNotNull(service);
        this.configService = checkNotNull(configService);
        this.gisService = checkNotNull(gisService);
        this.cacheService = checkNotNull(cacheService);
    }

    @Before
    public void init() {
        prepareTuid();
        configService.putString("org.cmdbuild.gis.enabled", "true");

        myClass = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClass")).build());

        gisAttribute = gisService.createGeoAttribute(GisAttributeImpl.builder().withLayerName("MyLayer")
                .withOwnerClassName(myClass.getName())
                .withActive(true)
                .withDefaultZoom(3)
                .withDescription("desc")
                .withIndex(0)
                .withMapStyle("{}")
                .withMaximumZoom(9)
                .withMinimumZoom(1)
                .withType("POINT")//TODO
                .withVisibility(emptyList())
                .build());

        one = dao.create(CardImpl.buildCard(myClass, ATTR_CODE, "one"));
        two = dao.create(CardImpl.buildCard(myClass, ATTR_CODE, "two"));
        three = dao.create(CardImpl.buildCard(myClass, ATTR_CODE, "three"));
    }

    @Test
    public void testGeoStyleRules() {

        GisStyleRuleset ruleset = service.create(GisStyleRulesetImpl.builder().withCode(tuid("test")).withGisAttribute(gisAttribute).withRules((List) list(
                Pair.of(AttributeFilterConditionImpl.eq(ATTR_CODE, "one").toAttributeFilter().toCmdbFilters(), map("color", "red")),
                Pair.of(AttributeFilterConditionImpl.eq(ATTR_CODE, "two").toAttributeFilter().toCmdbFilters(), map("color", "blue")),
                Pair.of(CmdbFilterImpl.noopFilter(), map("color", "green"))
        )).build());

        {
            Map<Long, Map<String, Object>> res = service.applyRulesOnCards(ruleset.getId());

            assertEquals(3, res.size());
            assertEquals(list(one, two, three).stream().map(Card::getId).collect(toSet()), res.keySet());
            assertEquals("red", res.get(one.getId()).get("color"));
            assertEquals("blue", res.get(two.getId()).get("color"));
            assertEquals("green", res.get(three.getId()).get("color"));
        }

        {
            Map<Long, Map<String, Object>> res = service.applyRulesOnCards(ruleset.getId(), set(one.getId(), three.getId()));

            assertEquals(2, res.size());
            assertEquals(list(one, three).stream().map(Card::getId).collect(toSet()), res.keySet());
            assertEquals("red", res.get(one.getId()).get("color"));
            assertEquals("green", res.get(three.getId()).get("color"));
        }
    }

    @Test
    public void testGeoStyleRulesWithFunction() {
        String functionName = tuid("_test");
        dao.getJdbcTemplate().execute(format("CREATE OR REPLACE FUNCTION \"%s\"(_card bigint,OUT _value varchar) returns setof varchar AS $$ BEGIN RETURN QUERY select (\"Code\"||'_fun')::varchar from \"Class\" where \"Id\" = _card; END $$ LANGUAGE PLPGSQL", functionName));
        dao.getJdbcTemplate().execute(format("COMMENT ON FUNCTION \"%s\"(_card bigint,OUT _value varchar) IS 'TYPE: FUNCTION'", functionName));
        cacheService.invalidateAll();

        GisStyleRuleset ruleset = service.create(GisStyleRulesetImpl.builder().withCode(tuid("test")).withGisAttribute(gisAttribute).withFunction(functionName).withRules((List) list(
                Pair.of(AttributeFilterConditionImpl.eq("_value", "one_fun").toAttributeFilter().toCmdbFilters(), map("color", "red")),
                Pair.of(AttributeFilterConditionImpl.eq("_value", "two_fun").toAttributeFilter().toCmdbFilters(), map("color", "blue")),
                Pair.of(CmdbFilterImpl.noopFilter(), map("color", "green"))
        )).build());

        {
            Map<Long, Map<String, Object>> res = service.applyRulesOnCards(ruleset.getId());

            assertEquals(3, res.size());
            assertEquals(list(one, two, three).stream().map(Card::getId).collect(toSet()), res.keySet());
            assertEquals("red", res.get(one.getId()).get("color"));
            assertEquals("blue", res.get(two.getId()).get("color"));
            assertEquals("green", res.get(three.getId()).get("color"));
        }

        {
            Map<Long, Map<String, Object>> res = service.applyRulesOnCards(ruleset.getId(), set(one.getId(), three.getId()));

            assertEquals(2, res.size());
            assertEquals(list(one, three).stream().map(Card::getId).collect(toSet()), res.keySet());
            assertEquals("red", res.get(one.getId()).get("color"));
            assertEquals("green", res.get(three.getId()).get("color"));
        }
    }

    @Test
    public void testGeoStyleRulesParamsCreateUpdate() {

        List<Pair<CmdbFilter, Map<String, Object>>> rules = list(
                Pair.of(AttributeFilterConditionImpl.eq(ATTR_CODE, "one").toAttributeFilter().toCmdbFilters(), map("color", "red")),
                Pair.of(AttributeFilterConditionImpl.eq(ATTR_CODE, "two").toAttributeFilter().toCmdbFilters(), map("color", "blue")),
                Pair.of(CmdbFilterImpl.noopFilter(), map("color", "green"))
        );

        GisStyleRuleset ruleset = service.create(GisStyleRulesetImpl.builder().withCode(tuid("test1")).withGisAttribute(gisAttribute).withRules(rules).build());

        assertNull(ruleset.getAnalysisType());

        ruleset = service.update(GisStyleRulesetImpl.copyOf(ruleset).withParams(b -> b.withAnalysisType(AT_INTERVALS)).build());

        assertEquals(AT_INTERVALS, ruleset.getAnalysisType());

        ruleset = service.update(GisStyleRulesetImpl.copyOf(ruleset).withParams(b -> b.withAnalysisType(AT_PUNCTUAL)).build());

        assertEquals(AT_PUNCTUAL, ruleset.getAnalysisType());

        ruleset = service.update(GisStyleRulesetImpl.copyOf(ruleset).withParams(b -> b.withAnalysisType(null)).build());

        assertNull(ruleset.getAnalysisType());

        ruleset = service.create(GisStyleRulesetImpl.builder().withCode(tuid("test2")).withGisAttribute(gisAttribute).withRules(rules).withParams(b -> b.withAnalysisType(AT_GRADUAL)).build());

        assertEquals(AT_GRADUAL, ruleset.getAnalysisType());
    }

}
