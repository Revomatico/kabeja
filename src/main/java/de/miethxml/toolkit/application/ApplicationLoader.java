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
package de.miethxml.toolkit.application;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.Iterator;


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
public class ApplicationLoader extends URLClassLoader {
    private ArrayList nativeLibraryPath = new ArrayList();
    private ClassLoader parent;

    public ApplicationLoader() {
        super(new URL[] {  });
        this.parent = this.getClass().getClassLoader();
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        Class clazz = null;

        //delegate all jre-classes to the parent, but not the xml-stuff
        if (name.startsWith("java.") || name.startsWith("javax.swing.")) {
            return parent.loadClass(name);
        }

        clazz = findLoadedClass(name);

        if (clazz == null) {
            try {
                clazz = super.findClass(name);
            } catch (ClassNotFoundException e) {
                clazz = parent.loadClass(name);
            } catch (Error er) {
                clazz = parent.loadClass(name);
            }
        }

        if (clazz != null) {
            return clazz;
        }

        throw new ClassNotFoundException(name);
    }

    public synchronized void addJarDirectory(String dir) {
        File libDir = new File(dir);

        if (libDir.isDirectory()) {
            File[] files = libDir.listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().endsWith(".jar")) {
                    try {
                        URL url = files[i].toURI().toURL();
                        super.addURL(url);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void addJarLibrary(String lib) {
        File libfile = new File(lib);

        if (libfile.isFile()) {
            try {
                URL url = libfile.toURI().toURL();
                super.addURL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * add single jar-file or directory with jarfiles or a classes-directory to
     * classpath
     *
     * @param path
     */
    public void addPathElement(String path) {
        File f = new File(path);

        if (f.isFile()) {
            addJarLibrary(path);

            return;
        } else if (f.isDirectory()) {
            //add to both maybe there are classes and jar mixed in one
            // directory
            addClassDirectory(path);
            addJarDirectory(path);
        }
    }

    public void addClassDirectory(String path) {
        File libDir = new File(path);

        if (libDir.isDirectory()) {
            try {
                URL url = libDir.toURI().toURL();
                super.addURL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public URL findResource(String name) {
        URL url = super.findResource(name);

        if (url == null) {
            ClassLoader parent = this.getClass().getClassLoader();

            return parent.getResource(name);
        }

        return url;
    }

    public String findLibrary(String name) {
        //must return an absolute path
        String libname = System.mapLibraryName(name);
        Iterator i = nativeLibraryPath.iterator();

        while (i.hasNext()) {
            String path = (String) i.next();
            File f = new File(path + File.separator + libname);

            if (f.exists() && f.isFile()) {
                return f.getAbsolutePath();
            }
        }

        return super.findLibrary(name);
    }

    public void addNativeLibraryPath(String path) {
        //this will be removed there is no need for this
        File p = new File(path);

        if (p.exists() && p.isDirectory()) {
            nativeLibraryPath.add(p.getAbsolutePath());
        }
    }

    public void loadLibrary(String library) throws Error {
        String systemlib = findLibrary(library);
        System.load(library);
    }

    public void load(String library) throws Error {
        System.load(library);
    }
}
