/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

public enum RelationDirection {
	RD_DIRECT, RD_INVERSE;

	public RelationDirection inverse() {
		switch (this) {
			case RD_DIRECT:
				return RD_INVERSE;
			case RD_INVERSE:
				return RD_DIRECT;
			default:
				throw new IllegalStateException();
		}
	}

}
