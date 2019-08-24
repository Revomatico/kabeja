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

import java.util.Collection;


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
public interface PluginReceiver {
    public static final String RECEIVE_ALL_PLUGINS = "ALL PLUGINS";

    /*
     *
     * A Collection which interface(String like
     * "de.miethxml.gui.viewer.Viewer"), the Listener is interested to get.
     *
     */
    public abstract Collection getInterfaces();

    /*
     *
     * Add a new Plugin to the Reciver. If the Plugin implements helper
     * interfaces, like "de.miethxml.toolkit.plugin.Plugin" and from Avalon (not
     * all handled yet, so the are ignored) this is handled before.
     *
     */
    public abstract void addPlugin(Object obj);

    /*
     *
     * This Plugin will removed from the application.
     *
     */
    public abstract void removePlugin(Object obj);
}
