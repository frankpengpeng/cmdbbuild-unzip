/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.task.cardeventprocessing;

import java.util.Map;
import org.cmdbuild.event.CardEvent;

public interface ScriptCommandService {

	void executeScript(ScriptCommand script, CardEvent event);

	void executeScript(ScriptCommand script, Map<String, Object> context);

}
