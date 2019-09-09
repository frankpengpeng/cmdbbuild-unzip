/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.emptyList;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisAttributeImpl;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class GeoAttributeIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final GisService gisService;
    private final GlobalConfigService configService;

    private Classe myClass;
    private GisAttribute myGisAttr;

    public GeoAttributeIT(DaoService dao, GisService gisService, GlobalConfigService configService) {
        this.dao = checkNotNull(dao);
        this.gisService = checkNotNull(gisService);
        this.configService = checkNotNull(configService);
    }

    @Before
    public void init() {
        prepareTuid();
        configService.putString("org.cmdbuild.gis.enabled", "true");
        myClass = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyTestClass")).build());
    }

    @Test
    public void createGeoAttribute() {
        myGisAttr = createGeoAttribute("MyTestGisLayer", myClass.getName());
        assertEquals(1, gisService.getGeoAttributeByOwnerClass(myClass.getName()).size());
        assertEquals("MyTestGisLayer", getOnlyElement(gisService.getGeoAttributeByOwnerClass(myClass.getName())).getLayerName());
        assertEquals(true, getOnlyElement(gisService.getGeoAttributeByOwnerClass(myClass.getName())).getActive());
        assertEquals(myClass.getName(), getOnlyElement(gisService.getGeoAttributeByOwnerClass(myClass.getName())).getOwnerClassName());
    }

    @Test
    public void createDeleteGeoAttribute() {
        myGisAttr = createGeoAttribute("MyTestGisLayer", myClass.getName());
        gisService.deleteGeoAttribute(myClass.getName(), myGisAttr.getLayerName());
        assertEquals(0, gisService.getGeoAttributeByOwnerClass(myClass.getName()).size());
    }

    public GisAttribute createGeoAttribute(String name, String ownerClass) {
        return gisService.createGeoAttribute(GisAttributeImpl.builder().withLayerName(name)
                .withOwnerClassName(ownerClass)
                .withActive(true)
                .withDefaultZoom(3)
                .withDescription("desc")
                .withIndex(0)
                .withMapStyle("{}")
                .withMaximumZoom(9)
                .withMinimumZoom(1)
                .withType("POINT")
                .withVisibility(emptyList())
                .build());
    }
}
