/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.minions;

import org.cmdbuild.services.MinionComponent;

public class MinionUtils {

	public static boolean isMinionBean(Object innerBean) {
		return innerBean.getClass().isAnnotationPresent(MinionComponent.class);
	}

}
