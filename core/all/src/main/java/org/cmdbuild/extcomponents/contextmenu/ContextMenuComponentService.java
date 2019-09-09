/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.extcomponents.contextmenu;

import java.util.List;
import javax.activation.DataHandler;
import org.cmdbuild.extcomponents.commons.ExtComponentInfo;

public interface ContextMenuComponentService {

    List<ExtComponentInfo> getForCurrentUser();

    List<ExtComponentInfo> getActiveForCurrentUser();

    ExtComponentInfo get(Long id);

    void delete(Long id);

    ExtComponentInfo createOrUpdate(byte[] toByteArray);

    ExtComponentInfo create(byte[] toByteArray);

    ExtComponentInfo update(Long id, byte[] toByteArray);

    ExtComponentInfo update(ExtComponentInfo customPage);

    byte[] getContextMenuFile(String contextMenuName, String filePath);

    DataHandler getContextMenuData(String code);

}
