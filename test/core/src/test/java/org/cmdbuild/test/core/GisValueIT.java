/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_MANY;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisAttributeImpl;
import org.cmdbuild.gis.GisNavTreeNode;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.gis.GisValue;
import org.cmdbuild.gis.model.GisValueImpl;
import org.cmdbuild.gis.model.PointImpl;
import org.cmdbuild.navtree.NavTree;
import org.cmdbuild.navtree.NavTreeImpl;
import org.cmdbuild.navtree.NavTreeNodeImpl;
import org.cmdbuild.navtree.NavTreeService;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class GisValueIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final GlobalConfigService configService;
    private final GisService gisService;
    private final NavTreeService navTreeService;

    private Classe myBuilding, myFloor, myRoom;
    private Domain buildingFloor, floorRoom, buildingRoom;
    private GisAttribute myBuildingGisAttr, myFloorGisAttr, myRoomGisAttr;
    private Card one, two, three;

    public GisValueIT(DaoService dao, GlobalConfigService configService, GisService gisService, NavTreeService navTreeService) {
        this.dao = checkNotNull(dao);
        this.configService = checkNotNull(configService);
        this.gisService = checkNotNull(gisService);
        this.navTreeService = checkNotNull(navTreeService);
    }

    @Before
    public void init() {
        prepareTuid();
        configService.putString("org.cmdbuild.gis.enabled", "true");

        myBuilding = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyBuilding")).build());
        myFloor = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyFloor")).build());
        myRoom = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyRoom")).build());

        buildingFloor = dao.createDomain(DomainDefinitionImpl.builder().withName(tuid("MyBuildingFloor_"))
                .withSourceClass(myBuilding).withTargetClass(myFloor).withCardinality(ONE_TO_MANY).build());

        floorRoom = dao.createDomain(DomainDefinitionImpl.builder().withName(tuid("MyFloorRoom_"))
                .withSourceClass(myFloor).withTargetClass(myRoom).withCardinality(ONE_TO_MANY).build());

        buildingRoom = dao.createDomain(DomainDefinitionImpl.builder().withName(tuid("MyBuildingRoom_"))
                .withSourceClass(myBuilding).withTargetClass(myRoom).withCardinality(ONE_TO_MANY).build());

    }

    @Test
    public void testGisValueWithNavTree() {
        List<String> layerNameCreation = list();
        myBuildingGisAttr = createGeoAttribute("MyBuildingLayer", myBuilding.getName());
        myFloorGisAttr = createGeoAttribute("MyFloorLayer", myFloor.getName());
        myRoomGisAttr = createGeoAttribute("MyRoomLayer", myRoom.getName());
        layerNameCreation.add(myBuildingGisAttr.getLayerName());
        layerNameCreation.add(myFloorGisAttr.getLayerName());
        layerNameCreation.add(myRoomGisAttr.getLayerName());

        NavTree navTree = navTreeService.create(NavTreeImpl.builder().withName("gisnavigation").withDescription("Test navTree")
                .withData(
                        NavTreeNodeImpl.builder()
                                .withId(randomId())
                                .withTargetClassName(myBuilding.getName())
                                .withChildNodes(list(
                                        NavTreeNodeImpl.builder()
                                                .withId(randomId())
                                                .withTargetClassName(myFloor.getName())
                                                .withDomainName(buildingFloor.getName())
                                                .withChildNodes(list(
                                                        NavTreeNodeImpl.builder()
                                                                .withId(randomId())
                                                                .withTargetClassName(myRoom.getName())
                                                                .withDomainName(floorRoom.getName())
                                                                .build()
                                                ))
                                                .build(),
                                        NavTreeNodeImpl.builder()
                                                .withId(randomId())
                                                .withTargetClassName(myRoom.getName())
                                                .withDomainName(buildingRoom.getName())
                                                .build()
                                ))
                                .build()
                ).build());

        one = dao.create(CardImpl.buildCard(myBuilding, ATTR_CODE, "BuildingOne"));
        two = dao.create(CardImpl.buildCard(myRoom, ATTR_CODE, "RoomOne"));
        three = dao.create(CardImpl.buildCard(myFloor, ATTR_CODE, "FloorOne"));

        GisValue value = GisValueImpl.builder()
                .withOwnerClassId(myBuilding.getName())
                .withOwnerCardId(one.getId())
                .withLayerName(myBuildingGisAttr.getLayerName())
                .accept((b) -> {
                    b.withGeometry(new PointImpl(1471608.81366569, 5800360.59200655));
                }).build();

        GisValue value2 = GisValueImpl.builder()
                .withOwnerClassId(myFloor.getName())
                .withOwnerCardId(three.getId())
                .withLayerName(myFloorGisAttr.getLayerName())
                .accept((b) -> {
                    b.withGeometry(new PointImpl(1471608.81366569, 5800360.59200655));
                }).build();

        GisValue value3 = GisValueImpl.builder()
                .withOwnerClassId(myRoom.getName())
                .withOwnerCardId(two.getId())
                .withLayerName(myRoomGisAttr.getLayerName())
                .accept((b) -> {
                    b.withGeometry(new PointImpl(1471608.81366569, 5800360.59200655));
                }).build();

        gisService.setValue(value);
        gisService.setValue(value2);
        gisService.setValue(value3);

        Pair<List<GisValue>, List<GisNavTreeNode>> geoValuesAndNavTree
                = gisService.getGeoValuesAndNavTree(list(
                        myBuildingGisAttr.getId(),
                        myRoomGisAttr.getId(),
                        myFloorGisAttr.getId()), "1471549.2639205048,5799949.687418456,1471849.0403908107,5900444.139445175", CmdbFilterImpl.builder().build());

        List<String> layerNames = list();
        geoValuesAndNavTree.getLeft().forEach((p) -> layerNames.add(p.getLayerName()));
        assertEquals(true, layerNames.containsAll(layerNameCreation));
        assertEquals(3, geoValuesAndNavTree.getLeft().size());
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
