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

import de.miethxml.toolkit.classloader.PluginClassLoader;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 *
 *
 *
 */
public class CocoonLoader extends PluginClassLoader {
    Logger log = Hierarchy.getDefaultHierarchy().getLoggerFor(this.getClass()
                                                                  .getName());
    private String cocoonLibPath;

    //this is the old implementation and deprecated
    private String CLASSNAME = "de.miethxml.hawron.cocoon.CocoonWrapperImpl";
    private ClassLoader parent = getClass().getClassLoader();

    /**
     *
     *
     *
     */
    public CocoonLoader() {
        super();
        init();
    }

    public CocoonLoader(String libpath) {
        this();
        this.cocoonLibPath = libpath;
        super.addJarDirectory(cocoonLibPath);
        Thread.currentThread().setContextClassLoader(this);
    }

    //	public void addJarDirectory(String dir) {
    //		File libDir = new File(dir);
    //
    //		if (libDir.isDirectory()) {
    //			File[] files = libDir.listFiles();
    //
    //			for (int i = 0; i < files.length; i++) {
    //				if (files[i].getName().endsWith(".jar")) {
    //					try {
    //						URL url = files[i].getCanonicalFile().toURL();
    //
    //						//log.debug("add Library: " + url.toExternalForm());
    //						super.addURL(url);
    //					} catch (MalformedURLException e) {
    //						log.error(e.getMessage());
    //						e.printStackTrace();
    //					} catch (IOException e) {
    //						log.error(e.getMessage());
    //						e.printStackTrace();
    //					}
    //				}
    //			}
    //		}
    //
    //		ClassLoader parent = getClass().getClassLoader();
    //
    //		if (parent instanceof CustomClassLoader) {
    //			((CustomClassLoader) parent).addJarDirectory(dir);
    //		}
    //	}
    //	public Class loadClass(String name) throws ClassNotFoundException {
    //	
    //		Class clazz = null;
    //
    //		if (name.startsWith("java") && !name.startsWith("javax.xml")) {
    //			//this should handle the parent
    //			return parent.loadClass(name);
    //		}
    //
    //		clazz = findLoadedClass(name);
    //
    //		if (clazz == null) {
    //			try {
    //				clazz = super.findClass(name);
    //			} catch (ClassNotFoundException e) {
    //				clazz = parent.loadClass(name);
    //			}
    //		}
    //
    //		if (clazz != null) {
    //			return clazz;
    //		}
    //
    //		throw new ClassNotFoundException(name);
    ////	}
    //
    //	public Class findClass(String name) throws ClassNotFoundException {
    //		return super.findClass(name);
    //	}

    /**
     * @return Returns the cocoonLibPath.
     *
     */
    public String getCocoonLibPath() {
        return cocoonLibPath;
    }

    /**
     * @param cocoonLibPath
     *
     * The cocoonLibPath to set.
     *
     */
    public void setCocoonLibPath(String cocoonLibPath) {
        this.cocoonLibPath = cocoonLibPath;
        addJarDirectory(cocoonLibPath);
    }

    /**
     * @deprecated
     */
    public CocoonWrapper getWrapperInstance() {
        try {
            Class c = loadClass(CLASSNAME);

            try {
                CocoonWrapper cw = (CocoonWrapper) c.newInstance();

                return cw;
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void init() {
        addJarLibrary("lib/cocoonLauncher.jar");
    }

    public CocoonBeanController getCocoonBeanController() {
        try {
            Class c = loadClass(
                    "de.miethxml.hawron.cocoon.CocoonBeanControllerImpl");

            try {
                CocoonBeanController beanController = (CocoonBeanController) c
                    .newInstance();

                return beanController;
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
