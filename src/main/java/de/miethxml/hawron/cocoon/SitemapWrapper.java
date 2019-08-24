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
package de.miethxml.hawron.cocoon;

import java.util.ArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public class SitemapWrapper implements ListModel {
    private ArrayList matcher;
    private ArrayList listlisteners;

    public SitemapWrapper() {
        super();

        matcher = new ArrayList();

        listlisteners = new ArrayList();
    }

    public void addMatcher(MatcherWrapper mw) {
        this.matcher.add(mw);

        fireListEvent();
    }

    public int getMatcherCount() {
        return this.matcher.size();
    }

    public MatcherWrapper getMatcher(int index) {
        if ((index >= 0) && (index < this.matcher.size())) {
            return (MatcherWrapper) matcher.get(index);
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
     *
     */
    public void addListDataListener(ListDataListener arg0) {
        listlisteners.add(arg0);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.ListModel#getElementAt(int)
     *
     */
    public Object getElementAt(int arg0) {
        if ((arg0 > -1) && (arg0 < matcher.size())) {
            MatcherWrapper match = (MatcherWrapper) matcher.get(arg0);

            return match.getMatch();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.ListModel#getSize()
     *
     */
    public int getSize() {
        return matcher.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
     *
     */
    public void removeListDataListener(ListDataListener arg0) {
        for (int i = 0; i < listlisteners.size(); i++) {
            ListDataListener ldl = (ListDataListener) listlisteners.get(i);

            if (ldl.equals(arg0)) {
                listlisteners.remove(i);

                return;
            }
        }
    }

    private void fireListEvent() {
        for (int i = 0; i < listlisteners.size(); i++) {
            ListDataListener ldl = (ListDataListener) listlisteners.get(i);

            ldl.contentsChanged(new ListDataEvent(this,
                    ListDataEvent.CONTENTS_CHANGED, 0, matcher.size()));
        }
    }
}
