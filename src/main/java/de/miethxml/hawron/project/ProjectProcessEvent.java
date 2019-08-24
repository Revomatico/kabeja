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

import java.util.EventObject;


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
public class ProjectProcessEvent extends EventObject {
    int links;
    int page;
    String uri;

    /**
     * @param source
     *
     */
    public ProjectProcessEvent(Object source) {
        super(source);
    }

    /**
     * @return Returns the links.
     *
     */
    public int getLinks() {
        return links;
    }

    /**
     * @param links
     *            The links to set.
     *
     */
    public void setLinks(int links) {
        this.links = links;
    }

    /**
     * @return Returns the uri.
     *
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri
     *            The uri to set.
     *
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return Returns the page.
     *
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page
     *            The page to set.
     *
     */
    public void setPage(int page) {
        this.page = page;
    }
}
