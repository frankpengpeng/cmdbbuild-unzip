/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick;

import org.cmdbuild.utils.quick.loader.QuickApplicationContextLoader;
import org.cmdbuild.utils.quick.scanner.QuickClasspathScanner;
import static org.cmdbuild.utils.quick.utils.QuickClasspathSourceUtils.buildReflectionsClasspathSource;

public class Quick {

    public static QuickContextBindings scan(String packageName) {
        return QuickClasspathScanner.scanClasspathAndBuildContextBindings(buildReflectionsClasspathSource(packageName));
    }

    public static QuickApplicationContext start(QuickContextBindings config) {
        return QuickApplicationContextLoader.loadApplicationContext(config);
    }

    public static QuickApplicationContext scanAndStart(String packageName) {
        return start(scan(packageName));
    }

}
