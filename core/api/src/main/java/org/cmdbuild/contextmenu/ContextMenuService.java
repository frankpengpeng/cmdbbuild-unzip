/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.contextmenu;

import java.util.List;
import org.cmdbuild.dao.entrytype.Classe;

public interface ContextMenuService {

    List<ContextMenuItem> getContextMenuItems(Classe classe);

    void updateContextMenuItems(Classe classe, List<ContextMenuItem> items);

    void deleteForClass(Classe classe);

}
