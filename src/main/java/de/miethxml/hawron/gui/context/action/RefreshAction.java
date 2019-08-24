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
package de.miethxml.hawron.gui.context.action;

import javax.swing.Icon;
import javax.swing.ImageIcon;


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
public class RefreshAction implements Action {
    /**
     *
     *
     *
     */
    public RefreshAction() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#doAction(java.lang.String)
     *
     */
    public void doAction(String uri) {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#getIcon()
     *
     */
    public Icon getIcon() {
        ImageIcon icon = new ImageIcon("icons/refresh.gif");

        return icon;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#isSupported(java.lang.String)
     *
     */
    public boolean isSupported(String extention) {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#isHandleDirectory()
     *
     */
    public boolean isHandleDirectory() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#isHandleFile()
     *
     */
    public boolean isHandleFile() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#getTooltip(java.lang.String)
     *
     */
    public String getToolTip(String lang) {
        return "Refresh";
    }
}
