/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.test.cp1;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MyComponentThree {

    public MyComponentThree(@Qualifier("two") MyInterface myInterface) {
        assertNotNull(myInterface);
        assertTrue(myInterface instanceof MyImplTwo);
    }

}
