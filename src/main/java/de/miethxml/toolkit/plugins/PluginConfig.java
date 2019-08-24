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
public class PluginConfig {
    private String name;
    private String instanceClass;
    private String interfaceName;
    private String description;
    private String[] classpath;
    private String[] resources;
    private String baseUri;
    private String systemID;
    private String ID;

    /**
     *
     *
     *
     */
    public PluginConfig() {
        super();

        name = "";

        instanceClass = "";

        interfaceName = "";

        description = "";

        classpath = new String[0];

        resources = new String[0];

        baseUri = "";

        systemID = "";

        ID = "";
    }

    /**
     *
     * @return Returns the description.
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     *
     * The description to set.
     *
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return Returns the instanceClass.
     *
     */
    public String getInstanceClass() {
        return instanceClass;
    }

    /**
     *
     * @param instanceClass
     *
     * The instanceClass to set.
     *
     */
    public void setInstanceClass(String instanceClass) {
        this.instanceClass = instanceClass;
    }

    /**
     *
     * @return Returns the interfaceName.
     *
     */
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     *
     * @param interfaceName
     *
     * The interfaceName to set.
     *
     */
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    /**
     *
     * @return Returns the name.
     *
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *
     * The name to set.
     *
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return Returns the classpath.
     *
     */
    public String[] getClasspath() {
        return classpath;
    }

    /**
     *
     * @param classpath
     *
     * The classpath to set.
     *
     */
    public void setClasspath(String[] classpath) {
        this.classpath = classpath;
    }

    /**
     *
     * @return Returns the resources.
     *
     */
    public String[] getResources() {
        return resources;
    }

    /**
     *
     * @param resources
     *
     * The resources to set.
     *
     */
    public void setResources(String[] resources) {
        this.resources = resources;
    }

    /**
     *
     * @return Returns the baseUri.
     *
     */
    public String getBaseUri() {
        return baseUri;
    }

    /**
     *
     * @param baseUri
     *
     * The baseUri to set.
     *
     */
    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    /**
     *
     * @return Returns the iD.
     *
     */
    public String getID() {
        return ID;
    }

    /**
     *
     * @param id
     *            The iD to set.
     *
     */
    public void setID(String id) {
        ID = id;
    }

    /**
     *
     * @return Returns the systemID.
     *
     */
    public String getSystemID() {
        return systemID;
    }

    /**
     *
     * @param systemID
     *            The systemID to set.
     *
     */
    public void setSystemID(String systemID) {
        this.systemID = systemID;
    }
}
