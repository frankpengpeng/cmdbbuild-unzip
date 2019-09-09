/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.framework;

import org.junit.Test;

/**
 *
 * @author davide
 */
public class EnvTest {

    @Test
    public void testEnv() {
        System.out.println("env param cmdbuild.test.database.url = " + System.getProperty("cmdbuild.test.database.url"));
    }

}
