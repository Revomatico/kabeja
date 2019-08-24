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
package de.miethxml.hawron.gui.conf;

import java.awt.event.ActionEvent;

import java.beans.PropertyChangeListener;

import javax.swing.Action;


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
public class PublishAction implements Action {
    /**
     *
     *
     *
     */
    public PublishAction() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.Action#isEnabled()
     *
     */
    public boolean isEnabled() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.Action#setEnabled(boolean)
     *
     */
    public void setEnabled(boolean b) {
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.Action#addPropertyChangeListener(java.beans.PropertyChangeListener)
     *
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.Action#removePropertyChangeListener(java.beans.PropertyChangeListener)
     *
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.Action#getValue(java.lang.String)
     *
     */
    public Object getValue(String key) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.Action#putValue(java.lang.String, java.lang.Object)
     *
     */
    public void putValue(
        String key,
        Object value) {
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
    }
}
