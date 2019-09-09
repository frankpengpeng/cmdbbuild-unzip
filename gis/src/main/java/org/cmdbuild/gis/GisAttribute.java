package org.cmdbuild.gis;

public interface GisAttribute extends GisLayer {

	String getOwnerClassName();
        
	boolean getActive();

	String getDescription();

	String getMapStyle();

	String getType();

}
