/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

/**
 *
 * @author davide
 */
public interface RequestTrackingService {

	/**
	 * track (log, store) request begin
	 *
	 * @param data
	 */
	void requestBegin(RequestData data);

	/**
	 * track (log, store) request complete
	 *
	 * @param data
	 */
	void requestComplete(RequestData data);

	/**
	 * cleanup request table (as per configured params)
	 */
	void cleanupRequestTable();

	/**
	 * drop all collected tracking data
	 */
	void dropAllData();

}
