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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.miethxml.toolkit.conf.ConfigManager;
import de.miethxml.toolkit.io.FileModel;
import de.miethxml.toolkit.repository.Reloadable;
import de.miethxml.toolkit.repository.RepositoryModel;
import de.miethxml.toolkit.repository.RepositorySelectionListener;


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
public class MenuAction extends AbstractAction
    implements RepositorySelectionListener {
    private Action action;
    private boolean directorySelected;
    private String selectedPath;
    private Reloadable model;
    private FileModel selectedModel;
    private boolean fileModelAction = false;
    private boolean checksupportedextensions = true;

    public MenuAction(Action action) {
        super(action.getToolTip(ConfigManager.getInstance().getProperty("lang")),
            action.getIcon());
        putValue(SHORT_DESCRIPTION,
            action.getToolTip(ConfigManager.getInstance().getProperty("lang")));
        this.action = action;
        selectedPath = "";

        //default is disabled, wait for an selection
        setEnabled(false);

        if (action instanceof FileModelAction) {
            fileModelAction = true;
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
        Runnable r = new Runnable() {
                public void run() {
                    if (fileModelAction) {
                        ((FileModelAction) action).doAction(selectedModel);
                    } else {
                        action.doAction(selectedPath);
                    }

                    model.reload();
                }
            };

        Thread t = new Thread(r);
        t.start();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.event.FileSystemSelectionListener#directorySelected(de.miethxml.gui.io.FileSystemModel,
     *
     * java.lang.String)
     *
     */
    public void directorySelected(
        Reloadable model,
        FileModel directory) {
        this.selectedPath = directory.getPath();
        this.model = model;
        directorySelected = true;
        this.selectedModel = directory;

        if (action.isHandleDirectory()) {
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.event.FileSystemSelectionListener#fileSelected(de.miethxml.gui.io.FileSystemModel,
     *
     * java.lang.String)
     *
     */
    public void fileSelected(
        Reloadable model,
        FileModel file) {
        this.selectedPath = file.getPath();
        this.model = model;
        directorySelected = false;
        this.selectedModel = file;

        if (!action.isSupported(selectedPath) && action.isHandleFile()
                && checksupportedextensions) {
            setEnabled(false);
        } else {
            setEnabled(true);
        }
    }

    public void setFileSystemModel(RepositoryModel model) {
        if (this.model != null) {
            //this.model.removeFileSystemSelectionListener(this);
        }

        //this.model = model;
        model.addRepositorySelectionListener(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.event.FileSystemSelectionListener#unselect()
     *
     */
    public void unselect() {
        setEnabled(false);
    }

    public void setCheckSupportedExtensions(boolean check) {
        this.checksupportedextensions = check;

        if (!check) {
            setEnabled(true);
        }
    }
}
