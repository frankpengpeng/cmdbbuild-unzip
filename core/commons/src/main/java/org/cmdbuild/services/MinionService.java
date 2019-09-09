/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.services;

import java.util.Collection;

public interface MinionService {

	Collection<Minion> getMinions();

	Minion getMinion(String id);

	default void startMinion(String id) {
		getMinion(id).startService();
	}

	default void stopMinion(String id) {
		getMinion(id).stopService();
	}

}
