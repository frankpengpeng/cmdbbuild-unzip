/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis;

import java.util.List;
import org.cmdbuild.dao.entrytype.Classe;

public interface GeoserverLayerRepository {

    GeoserverLayer create(GeoserverLayer layer);

    GeoserverLayer get(String name);

    void delete(String name);

    List<GeoserverLayer> getAll();

    List<GeoserverLayer> getForCard(Classe classe, long cardId);

    List<GeoserverLayer> getVisibleFromClass(Classe classe);

    List<GeoserverLayer> getOwnedByClass(Classe classe);

    void delete(long id);

    GeoserverLayer get(long id);

    GeoserverLayer update(GeoserverLayer geoserverLayer);

}
