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

import java.util.List;


/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 *
 *
 *
 */
public interface PluginManager {
    public static String ROLE = PluginManager.class.getName();
    public static final String DEFAULT_PLUGIN_DIRECTORY = "plugins";
    public static final String DEFAULT_PLUGIN_DESCRIPTOR = "plugin.xml";

    /**
     * Search in all subdirectories for plugins
     *
     * @param pluginDir
     * @return a List of founded plugins
     */
    public abstract void findPlugins(String pluginDir);

    /**
     *
     * @return the List of all currently used plugin-locations
     */
    public List getPluginDirectories();

    /**
     *
     * @return the location where new plugins can be installed
     */
    public String getPluginInstallLocation();

    /**
     * @deprecated Will removed
     * @param interfaceName
     * @return
     */
    public abstract List getPluginsByInterface(String interfaceName);

    public abstract void addPluginReceiver(PluginReceiver listener);

    public abstract void removePluginReceiver(PluginReceiver listener);
}
