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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 */
public class StoreableConfigurationImpl implements StoreableConfiguration {
    private DefaultConfiguration conf;

    public StoreableConfigurationImpl(Configuration conf) {
        this.conf = new DefaultConfiguration("hawron");
        this.conf.addAll(conf);
    }

    public StoreableConfigurationImpl() {
        this.conf = new DefaultConfiguration("hawron");
        wrapConfig();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.conf.StorableConfiguration#setConfiguration(java.lang.Class,
     *      org.apache.avalon.framework.configuration.Configuration)
     */
    public void setConfiguration(
        Class clazz,
        Configuration conf) {
        this.conf.addChild(conf);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.conf.StorableConfiguration#setConfiguration(java.lang.String,
     *      java.util.Hashtable)
     */
    public void setConfiguration(
        String key,
        Hashtable parameters) {
    }

    public void store() {
    }

    public Configuration getConfiguration() {
        return conf;
    }

    private void wrapConfig() {
        Properties props = ConfigManager.getInstance().getProperties();
        Enumeration e = props.keys();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            conf.setAttribute(key, (String) props.get(key));
        }
    }
}
