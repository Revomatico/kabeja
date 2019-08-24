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
package de.miethxml.hawron.gui.context.editor;

import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.SwingUtilities;

import de.miethxml.toolkit.conf.ConfigManager;
import de.miethxml.toolkit.io.FileModel;
import de.miethxml.toolkit.repository.Reloadable;
import de.miethxml.toolkit.repository.RepositoryModel;
import de.miethxml.toolkit.repository.RepositorySelectionListener;

import org.apache.avalon.framework.service.ServiceManager;


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
public class EditorAction extends AbstractAction implements EditorCloseListener,
    RepositorySelectionListener {
    private ServiceManager manager;

    //	private Logger log = Hierarchy.getDefaultHierarchy().getLoggerFor(
    //			this.getClass().getName());
    private List editorcache;
    private List active;
    private List closedEditors;
    private int CACHESIZE = 5;
    private boolean directorySelected;
    private String selectedPath;
    private Reloadable model;
    private Editor editor;
    private boolean destroySelf = false;
    private EditorCloseListener listener;
    private boolean cacheable = false;
    private Editor nonCacheable;
    private CacheableEditor cacheableEditor;
    private boolean checksupportedextensions = true;
    private boolean streamEditor = false;
    private FileModel fileModel;

    /**
     *
     *
     *
     */
    public EditorAction() {
        super();
        active = Collections.synchronizedList(new ArrayList());
        editorcache = Collections.synchronizedList(new ArrayList());
        closedEditors = Collections.synchronizedList(new ArrayList());
        listener = this;
    }

    /**
     * @param name
     *
     */
    public EditorAction(String name) {
        super(name);
    }

    /**
     * @param name
     *
     * @param icon
     *
     */
    public EditorAction(
        String name,
        Icon icon) {
        super(name, icon);
    }

    public EditorAction(Editor editor) {
        super(editor.getToolTip(ConfigManager.getInstance().getProperty("lang")),
            editor.getIcon());
        putValue(SHORT_DESCRIPTION,
            editor.getToolTip(ConfigManager.getInstance().getProperty("lang")));
        this.editor = editor;
        active = Collections.synchronizedList(new ArrayList());
        editorcache = Collections.synchronizedList(new ArrayList());
        closedEditors = Collections.synchronizedList(new ArrayList());

        if (editor instanceof CacheableEditor) {
            cacheable = true;
            editorcache.add(editor);
        } else {
            cacheable = false;
            nonCacheable = editor;
        }

        listener = this;

        //disable for default an wait for events
        setEnabled(false);

        if (editor instanceof StreamEditor) {
            streamEditor = true;
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
                    Thread.currentThread().setContextClassLoader(editor.getClass()
                                                                       .getClassLoader());

                    if (cacheable) {
                        if (editorcache.size() > 0) {
                            //log.debug("got editor from cache");
                            editor = (Editor) editorcache.remove((editorcache
                                    .size() - 1));
                        } else {
                            editor = editor.createNewEditor();
                        }

                        if (editor != null) {
                            ((CacheableEditor) editor).addEditorCloseListener(listener);
                            active.add(editor);
                        }
                    } else {
                        editor = nonCacheable.createNewEditor();
                    }

                    if (directorySelected) {
                        if (streamEditor) {
                            ((StreamEditor) editor).newFile(fileModel);
                        } else {
                            editor.newFile(selectedPath);
                        }
                    } else {
                        if (streamEditor) {
                            ((StreamEditor) editor).open(fileModel);
                        } else {
                            editor.open(selectedPath);
                        }
                    }

                    editor.setVisible(true);
                }
            };

        Thread t = new Thread(r);
        t.start();
    }

    public void close(CacheableEditor editor) {
        if (!destroySelf) {
            active.remove(editor);

            if (editorcache.size() < (CACHESIZE - 1)) {
                editorcache.add(editor);
                closedEditors.add(editor);

                //this is need to avoid a deadlock
                SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            removeListener();
                        }
                    });
            } else {
                //TODO change this to dispose
                //editor.dispose();
                editor = null;
            }

            if (model != null) {
                //model.reload();
            }
        }
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
        this.fileModel = directory;
        setEnabled(true);
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
        this.fileModel = file;
        this.model = model;
        directorySelected = false;

        if (!editor.isSupported(selectedPath) && checksupportedextensions) {
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

    public String getToolTip() {
        //change to the current language
        return editor.getToolTip("");
    }

    public void destroy() {
        //log.debug("destroy Editor Action");
        destroySelf = true;

        Iterator i = editorcache.iterator();

        while (i.hasNext()) {
            Editor edit = (Editor) i.next();

            if (edit instanceof CacheableEditor) {
                CacheableEditor cacheable = (CacheableEditor) edit;

                //log.debug("destroy editor from cache:" +
                // edit.getToolTip("en"));
                cacheable.destroy();
            }

            i.remove();
        }

        i = active.iterator();

        while (i.hasNext()) {
            Editor edit = (Editor) i.next();

            //log.debug("destroy editor from active editors: "
            //		+ edit.getToolTip("en"));
            if (edit instanceof CacheableEditor) {
                CacheableEditor cacheable = (CacheableEditor) edit;

                //log.debug("destroy editor from cache:" +
                // edit.getToolTip("en"));
                cacheable.destroy();
            }
        }
    }

    private void removeListener() {
        Iterator i = closedEditors.iterator();

        while (i.hasNext()) {
            CacheableEditor edit = (CacheableEditor) i.next();
            edit.removeEditorCloseListener(this);
            i.remove();
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.event.FileSystemSelectionListener#unselect()
     *
     */
    public void unselect() {
        setEnabled(false);
    }

    /**
     * @return Returns the supportAll.
     */
    public boolean isCheckSupportedExtensions() {
        return checksupportedextensions;
    }

    /**
     * @param supportAll
     *            The supportAll to set.
     */
    public void setCheckSupportedExtensions(boolean check) {
        this.checksupportedextensions = check;

        if (!check) {
            setEnabled(true);
        }
    }
}
