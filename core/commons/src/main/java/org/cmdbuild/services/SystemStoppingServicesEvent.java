/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.services;

/**
 * system is stopping; services that need to run custom shutdown code
 * pre-shutdown should listen for this event
 */
public enum SystemStoppingServicesEvent {
	INSTANCE

}
