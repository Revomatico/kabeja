/*
   Copyright 2005 Simon Mieth

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package de.miethxml.hawron.cocoon;

import java.io.File;

import java.net.URL;

import java.util.ArrayList;

import de.miethxml.hawron.cocoon.helper.InternalJarClassLoader;


/**
 * @author simon
 *
 */
public class CocoonBeanFactory {
    public static String IMPLEMENTATION_CLASS = "de.miethxml.hawron.cocoon.impl.CocoonBeanControllerImpl";

    public static CocoonBeanController createCocoonBeanController(
        String contextdir) {
        try {
            ArrayList urls = new ArrayList();
            File f = new File(contextdir + File.separator + "WEB-INF"
                    + File.separator + "lib");

            if (f.exists()) {
                File[] files = f.listFiles();

                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().endsWith(".jar")) {
                        urls.add(files[i].toURL());
                    }
                }
            }

            f = new File(contextdir + File.separator + "WEB-INF"
                    + File.separator + "classes");

            if (f.exists()) {
                urls.add(f.toURL());
            }

            ClassLoader cl = new InternalJarClassLoader((URL[]) urls.toArray(
                        new URL[0]));

            Thread.currentThread().setContextClassLoader(cl);
            Class clazz = cl.loadClass(IMPLEMENTATION_CLASS);

            Object obj = clazz.newInstance();

            return (CocoonBeanController) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
