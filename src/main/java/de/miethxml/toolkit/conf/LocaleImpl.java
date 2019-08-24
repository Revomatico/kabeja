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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;


/**
 * Manage locales at runtime, so you can change locales at runtime. You need
 * default
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 */
public class LocaleImpl implements ConfigListener {
    private static LocaleImpl instance;
    public static String NOLABEL = "!!-fixme-no-label-!!";
    private Properties locale;
    private String lang = "en";
    private ArrayList listeners = new ArrayList();
    private ArrayList repositories = new ArrayList();
    private Hashtable currentRepositories = new Hashtable();
    private String location = "lang";
    public static String DEFAULT_LANG="en";
    

    private LocaleImpl() {
        locale = new Properties();
        init();
      
    }

    private void load() {
        String file = location + File.separator + lang + ".txt";
  
        try {
            locale.load(new FileInputStream(file));
        } catch (IOException ioe) {
            System.out.println(
                "Error: could not load the language-file.\nFile: " + file);

            //TODO fallback to a default
        }
    }

    public String getString(String key) {
        synchronized (locale) {
            String value = locale.getProperty(key);

            if (value == null) {
                Enumeration e = currentRepositories.elements();

                while (e.hasMoreElements()) {
                    Hashtable locales = (Hashtable) e.nextElement();

                    if (locales.containsKey(key)) {
                        return (String) locales.get(key);
                    }
                }

                return NOLABEL;
            } else {
                return value;
            }
        }
    }

    public static synchronized LocaleImpl getInstance() {
    	if(instance == null){
    		instance = new LocaleImpl();
    		  ConfigManager.getInstance().addConfigListener(instance);
    	}
        return instance;
    }

    public static synchronized void configureLocaleLocation(String dir) {
        instance.setLocation(dir);
    }

    private void init() {
        locale.clear();

        lang = ConfigManager.getInstance().getProperty("lang");

        if ((lang == null) || (lang.length() == 0)) {
            lang = DEFAULT_LANG;
        } 

        Locale.setDefault(new Locale(lang));
        load();
        fireLangChanged();
    }

    /*
     *
     * (non-Javadoc)
     *
     *
     *
     * @see de.miethxml.conf.ConfigListener#configChanged(java.lang.String)
     *
     */
    public void configChanged(String configKey) {
        if (configKey.equals("lang")) {
            init();

            fireLangChanged();
        }
    }

    public void addLocaleListener(LocaleListener l) {
        listeners.add(l);
    }

    public void removeLocaleListener(LocaleListener l) {
        for (int i = 0; i < listeners.size(); i++) {
            LocaleListener tl = (LocaleListener) listeners.get(i);

            if (tl.equals(l)) {
                listeners.remove(i);

                return;
            }
        }
    }

    private void fireLangChanged() {
        //System.out.println("Langupdate update ");
        for (int i = 0; i < listeners.size(); i++) {
            LocaleListener l = (LocaleListener) listeners.get(i);

            l.langChanged();
        }
    }

    public void setString(
        String key,
        String t) {
        synchronized (locale) {
            locale.setProperty(key, t);
        }
    }

    public void addLocaleRepository(LocaleRepository rep) {
        repositories.add(rep);
        currentRepositories.put(rep, rep.getLocales(lang));
    }

    public void removeLocaleRepository(LocaleRepository rep) {
        repositories.remove(rep);
        currentRepositories.remove(rep);
    }

    public void setLocation(String location) {
        this.location = location;
        init();
        fireLangChanged();
    }
    
    
  
}
