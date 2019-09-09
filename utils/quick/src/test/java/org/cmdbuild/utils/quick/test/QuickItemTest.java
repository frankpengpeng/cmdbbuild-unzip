/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import org.cmdbuild.utils.quick.Quick;
import org.cmdbuild.utils.quick.QuickApplicationContext;
import org.cmdbuild.utils.quick.test.cp1.MyComponentFour;
import org.cmdbuild.utils.quick.test.cp1.MyComponentOne;
import org.cmdbuild.utils.quick.test.cp1.MyComponentThree;
import org.cmdbuild.utils.quick.test.cp1.MyComponentTwo;
import org.cmdbuild.utils.quick.test.cp1.MyComponentWithStartupEvent;
import org.cmdbuild.utils.quick.test.cp1.MyPrimaryTestComponent;
import org.junit.Test;

public class QuickItemTest {

    @Test
    public void testCp1() {
        QuickApplicationContext context = Quick.scanAndStart("org.cmdbuild.utils.quick.test.cp1");

        assertNotNull(context.getItem(MyComponentThree.class));
        assertNotNull(context.getItem(MyComponentTwo.class));
        assertNotNull(context.getItem(MyComponentOne.class));
        assertNotNull(context.getItem(MyComponentFour.class));
        assertNotNull(context.getItem(MyPrimaryTestComponent.class));
        assertTrue(context.getItem(MyComponentWithStartupEvent.class).receivedContextRefreshEvent);
    }
}
