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
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 *
 *
 *
 */
public class PublishDestination {
    public static String CONFIGKEY_PUBLISHROOT = "publish";
    public static String CONFIGKEY_PUBLISHTITLE = "name";
    public static String CONFIGKEY_PUBLISHDEST = "dest-dir";
    public static String CONFIGKEY_PUBLISHSOURCE = "source-dir";
    public static String CONFIGKEY_PUBLISHTARGETID = "target-id";
    private String title;
    private String destination;
    private String source;
    private String targetID;

    /**
     *
     *
     *
     */
    public PublishDestination() {
        super();
        title = "";
        destination = "";
        targetID = "";
        source = "";
    }

    /**
     * @return Returns the destination.
     *
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @param destination
     *
     * The destination to set.
     *
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * @return Returns the title.
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *
     * The title to set.
     *
     */
    public void setTitle(String title) {
        if (title == null) {
            this.title = "Unkown";
        } else {
            this.title = title;
        }
    }

    public void setTargetID(String id) {
        this.targetID = id;
    }

    public String getTargetID() {
        return this.targetID;
    }

    /**
     * @return Returns the source.
     *
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source
     *
     * The source to set.
     *
     */
    public void setSource(String source) {
        this.source = source;
    }

    public Object clone() {
        PublishDestination clone = new PublishDestination();
        clone.setTitle(getTitle());
        clone.setDestination(getDestination());
        clone.setSource(getSource());
        clone.setTargetID(getTargetID());

        return clone;
    }
}
