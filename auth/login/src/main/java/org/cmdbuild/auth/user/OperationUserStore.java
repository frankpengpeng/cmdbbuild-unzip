/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.user;

/**
 *
 * @author davide
 */
public interface OperationUserStore extends OperationUserSupplier {

	/**
	 * Sets the operation user in this session.
	 *
	 * @param user
	 */
	void setUser(OperationUser user);

	/**
	 * remove session user from store
	 */
	default void remove() {
		setUser(null);
	}
}
