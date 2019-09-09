/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.utils;

import java.util.Collection;
import org.cmdbuild.utils.quick.QuickClasspathSource;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class QuickClasspathSourceUtils {

    public static QuickClasspathSource buildReflectionsClasspathSource(String packageName) {
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
//                new Reflections(list().with(list((Object[]) packages)).with(new TypeElementsScanner(), new SubTypesScanner()).toArray(new Object[]{}));
        return () -> (Collection) reflections.getSubTypesOf(Object.class);
    }

}
