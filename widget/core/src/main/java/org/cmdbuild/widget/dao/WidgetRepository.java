/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.widget.dao;

import java.util.List;
import org.cmdbuild.widget.model.WidgetData;

public interface WidgetRepository {

	List<WidgetData> getAllWidgetsForClass(String className);

	List<WidgetData> getActiveWidgetsForClass(String className);

	void deleteForClass(String className);

	void updateForClass(String className, List<WidgetData> widgets);

}
