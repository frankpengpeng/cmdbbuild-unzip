/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.query.clause.alias;

public abstract class AbstractAlias implements Alias {

	@Override
	public int hashCode() {
		return asString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Alias)) {
			return false;
		}
		Alias other = (Alias) obj;
		return asString().equals(other.asString());
	}
}
