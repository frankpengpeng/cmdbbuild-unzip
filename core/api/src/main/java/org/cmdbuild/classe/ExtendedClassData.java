/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe;

import java.util.List;
import org.cmdbuild.contextmenu.ContextMenuItem;
import org.cmdbuild.dao.entrytype.AttributeGroupInfo;
import org.cmdbuild.formtrigger.FormTrigger;
import org.cmdbuild.widget.model.WidgetData;

public interface ExtendedClassData {

	List<FormTrigger> getFormTriggers();

	List<ContextMenuItem> getContextMenuItems();

	List<WidgetData> getWidgets();

	List<AttributeGroupInfo> getAttributeGroups();

}
