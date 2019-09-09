/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.activation.DataHandler;
import org.cmdbuild.extcomponents.commons.ExtComponentInfo;
import org.cmdbuild.extcomponents.contextmenu.ContextMenuComponentService;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.toDataHandler;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class ContextMenuIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ContextMenuComponentService service;

    private final DataHandler contextMenu = toDataHandler(getClass().getResourceAsStream("/org/cmdbuild/test/core/firstcomponent.zip"));

    public ContextMenuIT(ContextMenuComponentService service) {
        this.service = checkNotNull(service);
    }

    @Test
    public void testContextMenuCreationDeletion() {
        ExtComponentInfo info = createContextMenu();
        assertEquals("firstcomponent", info.getName());
        service.delete(info.getId());
    }

    @Test
    public void testContextMenuDownload() {
        ExtComponentInfo info = createContextMenu();
        DataHandler downloadedContextMenu = service.getContextMenuData(info.getName());
        assertEquals("firstcomponent.zip", downloadedContextMenu.getName());
        service.delete(info.getId());
    }

    public ExtComponentInfo createContextMenu() {
        return service.create(toByteArray(contextMenu));
    }
}
