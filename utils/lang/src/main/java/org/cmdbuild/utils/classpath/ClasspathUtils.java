/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.classpath;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClasspathUtils {

    public static ClassLoader buildClassloaderWithJarOverride(Collection<File> jars, ClassLoader parent) {
        return new ClasspathHandler(jars, parent).getClassLoader();
    }

    private static class ClasspathHandler {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final ClassLoader parentClassLoader, customClassLoader;

        public ClasspathHandler(Collection<File> extraJars, ClassLoader parentClassLoader) {
            try {
                this.parentClassLoader = checkNotNull(parentClassLoader);
                extraJars.forEach(f -> checkArgument(f.exists() && f.isFile(), "invalid jar file name = %s", f));
                URL[] urls = extraJars.stream().map(rethrowFunction(f -> f.toURI().toURL())).collect(toList()).toArray(new URL[]{});
//                extraJarsLoader = new URLClassLoader(, null);
                logger.debug("loading custom classes from jars = {}", extraJars);
                customClassLoader = new CustomClassLoader(urls, parentClassLoader);
            } catch (MalformedURLException ex) {
                throw runtime(ex);
            }
        }

        public ClassLoader getClassLoader() {
            return customClassLoader;
        }

        private class CustomClassLoader extends URLClassLoader {

            public CustomClassLoader(URL[] urls, ClassLoader parent) {
                super(urls, parent);
            }

            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                logger.debug("load class = {}", name);

                synchronized (getClassLoadingLock(name)) {
                    Class classe = findLoadedClass(name);
                    if (classe == null) {
                        try {
                            logger.trace("try to load class = {} from custom jars", name);
                            classe = findClass(name);
                            logger.trace("loaded class = {} from custom jars", classe);
                            return classe;
                        } catch (ClassNotFoundException | NoClassDefFoundError ex) {
                            logger.trace("class not found in custom jars for name = {}", name);
                        }
                        logger.trace("try to load class = {} from parent classloader", name);
                        classe = parentClassLoader.loadClass(name);
                        logger.trace("loaded class = {} from parent classloader", classe);
                        return classe;
                    } else {
                        return classe;
                    }
                }
            }
        }
    }
}
