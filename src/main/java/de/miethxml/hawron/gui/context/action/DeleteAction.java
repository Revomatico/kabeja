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

import de.miethxml.toolkit.io.FileModel;


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
public class DeleteAction implements Action, FileModelAction {
    /**
     *
     *
     *
     */
    public DeleteAction() {
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
        File f = new File(uri);
        String msg = "";

        if (f.isDirectory()) {
            msg = "Are you sure to delete directory and all subdirectories "
                + uri;
        } else if (f.isFile()) {
            msg = "Are you sure to delete file " + uri;
        }

        int option = JOptionPane.showConfirmDialog(null, msg, "Delete?",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            delete(f);
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
        ImageIcon icon = new ImageIcon("icons/delete.gif");

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
        return "Delete";
    }

    private void delete(File f) {
        if (f.exists() && f.isDirectory()) {
            File[] entries = f.listFiles();

            for (int i = 0; i < entries.length; i++) {
                delete(entries[i]);
            }

            f.delete();
        } else {
            f.delete();
        }
    }

    /* (non-Javadoc)
     * @see de.miethxml.hawron.gui.context.action.FileModelAction#doAction(de.miethxml.toolkit.io.FileModel)
     */
    public void doAction(FileModel model) {
        String msg = "";

        if (!model.isFile()) {
            msg = "Are you sure to delete directory and all subdirectories "
                + model.getPath();
        } else {
            msg = "Are you sure to delete file " + model.getPath();
        }

        int option = JOptionPane.showConfirmDialog(null, msg, "Delete?",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            delete(model);
        }
    }

    private void delete(FileModel f) {
        if (f.exists()) {
            if (f.isFile()) {
                f.delete();
            } else {
                //directory first delete all children
                FileModel[] children = f.getChildren();

                for (int i = 0; i < children.length; i++) {
                    delete(children[i]);
                }

                //empty now delete self
                f.delete();
            }
        }
    }
}
