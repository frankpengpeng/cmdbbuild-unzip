/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.activation.DataHandler;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserDataImpl;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.extcomponents.commons.ExtComponentInfo;
import org.cmdbuild.extcomponents.custompage.CustomPageService;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.io.CmIoUtils.toDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class CustomPagesIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CustomPageService service;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SessionService sessionService;

    private final DataHandler customPage = toDataHandler(getClass().getResourceAsStream("/org/cmdbuild/test/core/myfirstcp.zip"));

    public CustomPagesIT(CustomPageService service, UserRepository userRepository, RoleRepository roleRepository, SessionService sessionService) {
        this.service = checkNotNull(service);
        this.userRepository = checkNotNull(userRepository);
        this.roleRepository = checkNotNull(roleRepository);
        this.sessionService = checkNotNull(sessionService);
    }

    @Before
    public void init() {
        prepareTuid();

        UserData user = userRepository.create(UserDataImpl.builder().withUsername(tuid("my_username")).build());
        roleRepository.setUserGroupsByName(user.getId(), list("SuperUser"), null);

        sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired(user.getUsername()));
    }

    @Test
    public void testCustomPageCreationDeletion() {
        ExtComponentInfo info = createCustomPage();
        assertEquals("myfirstcp", info.getName());
        service.delete(info.getId());
    }

    @Test
    public void testCustomPageDownload() {
        ExtComponentInfo info = createCustomPage();
        DataHandler downloadedCustomPage = service.getCustomPageData(info.getName());
        assertEquals("myfirstcp.zip", downloadedCustomPage.getName());
        service.delete(info.getId());
    }

    public ExtComponentInfo createCustomPage() {
        return service.create(toByteArray(customPage));
    }

}
