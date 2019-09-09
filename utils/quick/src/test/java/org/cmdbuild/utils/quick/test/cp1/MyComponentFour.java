/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.test.cp1;

import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import org.springframework.stereotype.Component;

@Component
public class MyComponentFour {

    public MyComponentFour(List<MyInterface> list) {
        assertNotNull(list);
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(MyImplOne.class::isInstance));
        assertTrue(list.stream().anyMatch(MyImplTwo.class::isInstance));
    }

}
