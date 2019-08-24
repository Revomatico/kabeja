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
package de.miethxml.hawron.cocoon.helper;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 * 
 */
public class InternalJarClassLoader extends URLClassLoader {
	public static final String JARARCHIV = "resources/classes.jar";

	protected HashSet classes = new HashSet();

	protected Hashtable classCache = new Hashtable();

	protected int CLASS_CACHE_SIZE = 32;

	public InternalJarClassLoader() {
		this(new URL[0]);
	}

	public InternalJarClassLoader(URL[] url) {
		super(url);
		readClassNames();
	}

	public Class loadClass(String name) throws ClassNotFoundException {
		return loadClass(name, false);
	}

	public Class loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		ClassLoader parent = this.getClass().getClassLoader();

		// loaded before
		Class clazz = findLoadedClass(name);

		// load from nested jararchiv
		if (clazz == null) {
			try {
				clazz = loadArchivedClass(name);
			} catch (Exception e) {
			}
		}

		// load from given classpath
		if (clazz == null) {
			try {
				clazz = super.findClass(name);
			} catch (Exception e) {
			}
		}

		// try the parent
		if (clazz == null) {
			try {
				clazz = parent.loadClass(name);
			} catch (Exception e) {
			}
		}

		if (clazz == null) {
			throw new ClassNotFoundException(name);
		}
		
		if (resolve) {
			resolveClass(clazz);
		}
		return clazz;

	}

	private Class loadArchivedClass(String name) throws ClassNotFoundException {
		Class c = null;

		if (classes.contains(name)) {
			// cached?
			if (classCache.contains(name)) {
				return (Class) classCache.get(name);
			}

			try {
				InputStream in = this.getClass().getClassLoader()
						.getResourceAsStream(JARARCHIV);
				JarInputStream jar = new JarInputStream(in);
				JarEntry entry = null;

				while ((c == null) && ((entry = jar.getNextJarEntry()) != null)) {
					if (!entry.isDirectory()
							&& entry.getName().endsWith(".class")) {
						String clazz = entry.getName();
						clazz = clazz.substring(0, clazz.indexOf(".class"));
						clazz = clazz.replaceAll("\\/", ".");

						if (clazz.equals(name)) {
							int size = (int) entry.getSize();
							byte[] b = new byte[size];
							int count = 0;
							int current = 0;

							while (((current = jar.read(b, count, size - count)) != -1)
									&& (count < size)) {
								count += current;
							}

							c = super.defineClass(name, b, 0, b.length);

							if (classCache.size() < CLASS_CACHE_SIZE) {
								// add to cache
								classCache.put(clazz, c);
							}
						}
					}
				}

				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return c;
		} else {
			throw new ClassNotFoundException(name);
		}
	}

	private void readClassNames() {
		InputStream in = null;

		try {
			in = this.getClass().getClassLoader()
					.getResourceAsStream(JARARCHIV);

			JarInputStream jar = new JarInputStream(in);
			JarEntry entry = null;

			while ((entry = jar.getNextJarEntry()) != null) {
				if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
					String clazz = entry.getName();
					clazz = clazz.substring(0, clazz.indexOf(".class"));
					clazz = clazz.replaceAll("\\/", ".");

					classes.add(clazz);
				}
			}

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (Exception e) {
				}
			}
		}
	}
}
