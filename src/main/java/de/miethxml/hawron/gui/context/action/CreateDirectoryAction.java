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

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.io.FileModel;
import de.miethxml.toolkit.io.FileModelException;


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
public class CreateDirectoryAction implements Action, FileModelAction {
    /**
     *
     *
     *
     */
    public CreateDirectoryAction() {
        super();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#doAction(java.lang.String)
     *
     */
    public void doAction(String uri) {
        //ask for the new Folder
        File f = new File(uri);

        if (f.isDirectory()) {
            f = new File(f.getAbsolutePath() + File.separator + "NewFolder");
            f.mkdir();
        } else {
            String path = f.getParent() + File.separator + "NewFolder";
            f = new File(path);
            f.mkdir();
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#getIcon()
     *
     */
    public Icon getIcon() {
        ImageIcon icon = new ImageIcon("icons/newdir.png");

        return icon;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#isSupported(java.lang.String)
     *
     */
    public boolean isSupported(String extention) {
        return true;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#isHandleDirectory()
     *
     */
    public boolean isHandleDirectory() {
        return true;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#isHandleFile()
     *
     */
    public boolean isHandleFile() {
        return true;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#getTooltip(java.lang.String)
     *
     */
    public String getToolTip(String lang) {
        return "New Folder";
    }

    public void doAction(FileModel model) {
        try {
            String name = JOptionPane.showInputDialog(LocaleImpl.getInstance()
                                                                .getString("action.newfolder"));

            if ((name != null) && (name.length() > 0)) {
                model.createDirectory(name);
            }
        } catch (FileModelException e) {
            e.printStackTrace();
        }
    }
}
