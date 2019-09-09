/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

public interface SorterElement {

	String getProperty();

	SorterElementDirection getDirection();

	enum SorterElementDirection {
		ASC, DESC
	}
}
