package org.cmdbuild.gis;

public interface GeoserverLayer extends GisLayer {

    String getDescription();

    String getType();

    String getGeoserverName();

    String getOwnerClassId();
    
    boolean getActive();

    long getOwnerCardId();

}
