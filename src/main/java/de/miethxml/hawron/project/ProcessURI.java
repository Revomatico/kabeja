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
package de.miethxml.hawron.project;


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
public class ProcessURI {
    public static String CONFIGKEY_SRCPREFIX = "src-prefix";
    public static String CONFIGKEY_SRC = "src";
    public static String CONFIGKEY_DEST = "dest";
    public static String CONFIGKEY_TYPE = "type";
    private String uri = "";
    private String type = "append";
    private String dest = "";
    private String srcPrefix = "";
    private Task parent;

    /**
     *
     *
     *
     */
    public ProcessURI() {
        super();
    }

    /**
     *
     * @return Returns the dest.
     *
     */
    public String getDest() {
        return dest;
    }

    /**
     *
     * @param dest
     *            The dest to set.
     *
     */
    public void setDest(String dest) {
        this.dest = dest;
    }

    /**
     *
     * @return Returns the srcPrefix.
     *
     */
    public String getSrcPrefix() {
        return srcPrefix;
    }

    /**
     *
     * @param srcPrefix
     *            The srcPrefix to set.
     *
     */
    public void setSrcPrefix(String srcPrefix) {
        this.srcPrefix = srcPrefix;
    }

    /**
     *
     * @return Returns the type.
     *
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     *            The type to set.
     *
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return Returns the uri.
     *
     */
    public String getUri() {
        return uri;
    }

    /**
     *
     * @param uri
     *            The uri to set.
     *
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setTask(Task task) {
        this.parent = task;

        if (dest.length() > 0) {
            parent.setBuildDir(dest);
        }
    }

    public Task getTask() {
        return parent;
    }

    public Object clone() {
        ProcessURI clone = new ProcessURI();

        clone.setUri(getUri());

        clone.setSrcPrefix(getSrcPrefix());

        clone.setDest(getDest());

        clone.setType(getType());

        clone.setTask(getTask());

        return clone;
    }
}
