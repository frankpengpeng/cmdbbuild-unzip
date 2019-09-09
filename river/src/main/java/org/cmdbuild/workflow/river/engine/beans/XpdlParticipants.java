/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.beans;

import static com.google.common.collect.Maps.uniqueIndex;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;

public class XpdlParticipants {

	private final Map<String, XpdlParticipant> xpdlParticipants;

	public XpdlParticipants(Collection<XpdlParticipant> xpdlParticipants) {
		this.xpdlParticipants = uniqueIndex(xpdlParticipants, XpdlParticipant::getName);
	}

	public @Nullable
	XpdlParticipant getXpdlParticipant(String name) {
		return xpdlParticipants.get(name);
	}
}
