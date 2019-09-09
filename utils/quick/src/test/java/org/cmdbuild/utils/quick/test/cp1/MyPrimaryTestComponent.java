/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.test.cp1;

import static junit.framework.Assert.assertTrue;
import org.springframework.stereotype.Component;

@Component
public class MyPrimaryTestComponent {

    public MyPrimaryTestComponent(MyOtherInterface test) {
        assertTrue(test instanceof MyOtherComponentPrimary);
    }

}
