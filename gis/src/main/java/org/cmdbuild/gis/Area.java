/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis;

import org.cmdbuild.gis.model.PointImpl;

public interface Area {

    double getX1();

    double getY1();

    double getX2();

    double getY2();

    default Point getCenter() {
        return new PointImpl((getX1() + getX2()) / 2, (getY1() + getY2()) / 2);
    }

}
