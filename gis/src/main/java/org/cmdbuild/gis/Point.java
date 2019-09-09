/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis;

public interface Point extends Geometry {

	@Override
	default GisValueType getType() {
		return GisValueType.POINT;
	}

	double getX();

	double getY();

}
