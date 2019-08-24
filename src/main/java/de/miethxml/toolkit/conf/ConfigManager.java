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
package de.miethxml.toolkit.conf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 * 
 * 
 */
public class ConfigManager {
	static ConfigManager instance;

	public static String DEFAULT_CONFIGNAME = "conf/main.conf";

	private Properties props;

	private ArrayList listeners = new ArrayList();;

	private String confFile;

	/**
	 * 
	 * 
	 * 
	 */
	private ConfigManager() {
		props = new Properties();
		confFile = DEFAULT_CONFIGNAME;
		load();
		
	}

	private void load() {
		try {
			props.load(new FileInputStream(confFile));
		} catch (IOException ioe) {
			System.out.println("Error: no config-file found, try default.");
			try {
				props.load(new FileInputStream(DEFAULT_CONFIGNAME));
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
		fireAllChanged();
	}

	public void storeConfiguration() {
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(confFile));
			pw.println("#Main Config File");

			Enumeration e = props.keys();

			while(e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = (String) props.get(key);
				pw.println(key + " = " + value);
			}

			pw.flush();
			pw.close();
		} catch (IOException ioe) {
			System.out.println("Could not write config-File!");
			ioe.printStackTrace();
		}
	}

	static public synchronized ConfigManager getInstance() {
		if (instance == null) {
			instance = new ConfigManager();
		}

		return instance;
	}

	public String getProperty(String key) {
		if (props.containsKey(key)) {
			return props.getProperty(key);
		}

		return "";
	}

	public String getProperty(String key, String defaultvalue) {
		if (props.containsKey(key)) {
			return props.getProperty(key);
		}

		return defaultvalue;
	}

	public boolean hasProperty(String key) {
		return props.containsKey(key);
	}

	public void setProperty(String key, String value) {
		props.put(key, value);
		storeConfiguration();
		fireConfigChange(key);
	}

	public void addConfigListener(ConfigListener l) {
		listeners.add(l);
	}

	public void removeConfigListener(ConfigListener l) {
		listeners.remove(l);
	}

	private void fireConfigChange(String configKey) {
		Iterator i = ((ArrayList) listeners.clone()).iterator();

		while (i.hasNext()) {
			ConfigListener cl = (ConfigListener) i.next();
			cl.configChanged(configKey);
		}
	}

	public Properties getProperties() {
		return props;
	}

	public void setConfigFile(String file) {
		this.confFile = file;
		load();
	}

	private void fireAllChanged() {
		Enumeration e = ((Properties) props.clone()).keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			fireConfigChange(key);
		}
	}

}
