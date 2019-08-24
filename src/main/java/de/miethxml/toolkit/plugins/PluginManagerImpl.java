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
package de.miethxml.toolkit.plugins;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import de.miethxml.toolkit.classloader.PluginClassLoader;
import de.miethxml.toolkit.plugins.xml.SAXPluginBuilder;

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
public class PluginManagerImpl implements PluginManager {
    static PluginManagerImpl instance;

    private Logger log;

    private String pluginDir;

    private ArrayList plugins;

    private ArrayList listeners;

    private Hashtable objects;

    private ArrayList pluginLocations;

    private HashSet pluginNames = new HashSet();

    /**
     * 
     * 
     *  
     */
    public PluginManagerImpl() {
        super();
        pluginDir = "";
        plugins = new ArrayList();
        listeners = new ArrayList();
        objects = new Hashtable();
        pluginLocations = new ArrayList();
    }

    public static synchronized PluginManagerImpl getInstance() {
        if (instance == null) {
            instance = new PluginManagerImpl();
        }

        return instance;
    }

    public void findPlugins(String pluginDir) {
        File dir = new File(pluginDir);

        if (dir.isDirectory()) {
            pluginLocations.add(dir.getAbsolutePath());

            SAXPluginBuilder saxloader = new SAXPluginBuilder();
            File[] plugindirs = dir.listFiles();

            for (int i = 0; i < plugindirs.length; i++) {
                if (plugindirs[i].isDirectory()
                        && !isAlreadyLoadedPlugin(plugindirs[i]
                                .getAbsolutePath())) {
                    File descriptor = new File(plugindirs[i].getAbsolutePath()
                            + File.separator
                            + PluginManager.DEFAULT_PLUGIN_DESCRIPTOR);

                    if (descriptor.exists()) {
                        addPlugin(plugindirs[i]);
                    } else {
                        log.info("no plugin.xml inside plugindirectory: "
                                + plugindirs[i].getAbsolutePath());
                    }
                }
            }
        }
    }

    public List getPluginsByInterface(String interfaceName) {
        ArrayList searchedPlugins = new ArrayList();
        Iterator i = plugins.iterator();

        while (i.hasNext()) {
            PluginConfig plugin = (PluginConfig) i.next();

            if (plugin.getInterfaceName().equals(interfaceName)) {
                searchedPlugins.add(plugin);
            }
        }

        return searchedPlugins;
    }

    private Object getPluginObject(PluginConfig plugin) {
        Object obj = null;
        log.debug("create new PluginObject for plugin:" + plugin.getName());

        //create object with own classloader
        PluginClassLoader cl = new PluginClassLoader(getClass()
                .getClassLoader());
        String path = plugin.getBaseUri();
        String[] jars = plugin.getClasspath();

        for (int x = 0; x < jars.length; x++) {
            log.debug("add plugin jar=" + jars[x] + " for plugin: "
                    + plugin.getName());
            cl.addJarLibrary(path + File.separator + jars[x]);
        }

        //now libs are setup -> create object instance
        try {
            Class c = cl.loadClass(plugin.getInstanceClass());

            try {
                obj = c.newInstance();

                if (obj instanceof Plugin) {
                    log.debug("Plugin=" + plugin.getName()
                            + " is Plugin setContextPath="
                            + plugin.getBaseUri());
                    ((Plugin) obj).setContextPath(plugin.getBaseUri());
                }
            } catch (InstantiationException e1) {
                log.error("could not load plugin: " + plugin.getName());
                log.error(e1.getMessage());
            } catch (IllegalAccessException e1) {
                log.error("could not load plugin: " + plugin.getName());
                log.error(e1.getMessage());
            }
        } catch (ClassNotFoundException e) {
            log.error("could not load plugin: " + plugin.getName());
            log.error(e.getMessage());
        }

        return obj;
    }

    public void addPluginReceiver(PluginReceiver listener) {
        listeners.add(listener);

        if (log.isDebugEnabled()) {
            log.debug("addPluginReciver:" + listener.getClass().getName());
        }

        verifyPluginsForListener(listener);
    }

    public void removePluginReceiver(PluginReceiver listener) {
        listeners.remove(listener);
    }

    private void verifyPluginsForListener(PluginReceiver listener) {
        Collection c = listener.getInterfaces();
        Iterator f = c.iterator();

        if (log.isDebugEnabled()) {
            while (f.hasNext()) {
                log.debug("PluginListener:" + listener.getClass().getName()
                        + " looking for=" + (String) f.next());
            }
        }

        //search for used plugins
        Enumeration e = objects.keys();

        while (e.hasMoreElements()) {
            PluginConfig p = (PluginConfig) e.nextElement();

            if (c.contains(p.getInterfaceName())
                    || c.contains(PluginReceiver.RECEIVE_ALL_PLUGINS)) {
                Object obj = objects.get(p);

                if (log.isDebugEnabled()) {
                    log.debug("Found Plugin:" + obj.getClass().getName()
                            + " for:" + listener.getClass().getName());
                }

                listener.addPlugin(obj);
            }
        }

        //Search for unused plugins
        Iterator i = plugins.iterator();

        while (i.hasNext()) {
            PluginConfig p = (PluginConfig) i.next();

            if (c.contains(p.getInterfaceName())
                    || c.contains(PluginReceiver.RECEIVE_ALL_PLUGINS)) {
                Object obj = getPluginObject(p);
                listener.addPlugin(obj);
                objects.put(p, obj);
                i.remove();
            }
        }
    }

    private synchronized void verifyListnersForPlugin(PluginConfig plugin) {
        //boolean created = false;
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            PluginReceiver l = (PluginReceiver) i.next();
            Collection c = l.getInterfaces();

            if (c.contains(plugin.getInterfaceName())
                    || c.contains(PluginReceiver.RECEIVE_ALL_PLUGINS)) {
                Object obj = objects.get(plugin);
                l.addPlugin(obj);
            }
        }
    }

    private boolean isAlreadyLoadedPlugin(String path) {
        Iterator i = plugins.iterator();

        while (i.hasNext()) {
            PluginConfig plugin = (PluginConfig) i.next();

            if (plugin.getBaseUri().equals(path)) {
                //this plugin is unused and can overiden by a new version
                //without problems
                i.remove();
            }
        }

        Enumeration e = objects.keys();

        while (e.hasMoreElements()) {
            PluginConfig plugin = (PluginConfig) e.nextElement();

            if (plugin.getBaseUri().equals(path)) {
                //an already loaded plugin can not changed a runtime
                //at the moment. The new will used after a restart of the
                //application
                return true;
            }
        }

        return false;
    }

    private void addPlugin(File dir) {
        SAXPluginBuilder saxloader = new SAXPluginBuilder();
        PluginConfig pluginConfig = saxloader.parseURI(dir.getAbsolutePath()
                + File.separator + PluginManager.DEFAULT_PLUGIN_DESCRIPTOR);

       //check if the plugin is loaded before
        if (!pluginNames.contains(pluginConfig.getName())) {
            
            pluginNames.add(pluginConfig.getName());
            
            pluginConfig.setBaseUri(dir.getAbsolutePath());

            //the systemID is the lasmodified
            pluginConfig.setSystemID("" + dir.lastModified());

            //generate a Object and store
            Object obj = getPluginObject(pluginConfig);
            objects.put(pluginConfig, obj);

            //check now for listeners
            verifyListnersForPlugin(pluginConfig);
            
        }else if(log.isDebugEnabled()){
            log.debug("Plugin:"+pluginConfig.getName()+" with path:"+dir.getAbsolutePath()+" always loaded before.");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.miethxml.toolkit.plugins.PluginManager#getPluginDirectories()
     */
    public List getPluginDirectories() {
        return pluginLocations;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.miethxml.toolkit.plugins.PluginManager#getPluginInstallLocation()
     */
    public String getPluginInstallLocation() {
        return PluginManager.DEFAULT_PLUGIN_DIRECTORY;
    }

    public void setLogger(Logger log) {
        this.log = log;
    }
}