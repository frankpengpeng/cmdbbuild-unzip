/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.error;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * service that provides {@link ErrorOrWarningEventCollector} instances, to
 * collect error and warning events that may happen in a request and should be
 * reported to user
 *
 * @author davide
 */
public interface ErrorAndWarningCollectorService {

	/**
	 * return a slf4j marker to mark log messages (usally warning messages) that
	 * should be propagated to user
	 *
	 * @return
	 */
	static Marker marker() {
		return MarkerFactory.getMarker("NOTIFY");
	}

	ErrorOrWarningEventCollector getCurrentRequestEventCollector();

}
