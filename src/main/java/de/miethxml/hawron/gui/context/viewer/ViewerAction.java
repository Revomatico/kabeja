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
package de.miethxml.hawron.gui.context.viewer;

import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

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
public class ViewerAction extends AbstractAction implements ViewerCloseListener,
    RepositorySelectionListener {
    private Viewer viewer;
    private ArrayList active;
    private ArrayList viewercache;
    private ArrayList closedViewers;
    private int CACHESIZE = 5;
    private boolean directorySelected;
    private String selectedPath;
    private Reloadable model;
    private boolean cacheable = false;
    private boolean selfdestroy = false;
    private boolean checksupportedextensions = true;
    private boolean streamViewer = false;
    private FileModel fileModel;

    public ViewerAction(Viewer viewer) {
        super(viewer.getToolTip(ConfigManager.getInstance().getProperty("lang")),
            viewer.getIcon());
        putValue(SHORT_DESCRIPTION,
            viewer.getToolTip(ConfigManager.getInstance().getProperty("lang")));
        this.viewer = viewer;
        viewercache = new ArrayList();
        active = new ArrayList();
        closedViewers = new ArrayList();

        if (viewer instanceof CacheableViewer) {
            active = new ArrayList();
            viewercache.add(viewer);
            cacheable = true;
        } else {
            cacheable = false;
        }

        //disable by default, wait for events
        setEnabled(false);

        if (viewer instanceof StreamViewer) {
            streamViewer = true;
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
        if (cacheable) {
            if (viewercache.size() > 0) {
                viewer = (Viewer) viewercache.remove(viewercache.size() - 1);
            } else {
                viewer = viewer.createNewViewer();
            }

            //TODO maybe we add null here, handle this?
            active.add(viewer);
        } else {
            viewer = viewer.createNewViewer();
        }

        if (viewer != null) {
            Runnable r = new Runnable() {
                    public void run() {
                        if (directorySelected) {
                            //handle directories here later
                        } else {
                            if (streamViewer) {
                                ((StreamViewer) viewer).open(fileModel);
                            } else {
                                viewer.open(selectedPath);
                            }
                        }

                        viewer.setVisible(true);
                    }
                };

            Thread t = new Thread(r);
            t.start();
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.ViewerCloseListener#close(de.miethxml.gui.viewer.Viewer)
     *
     */
    public void close(CacheableViewer viewer) {
        if (!selfdestroy) {
            active.remove(viewer);

            if (viewercache.size() < (CACHESIZE - 1)) {
                viewercache.add(viewer);
                closedViewers.add(viewer);

                //this is need to avoid a deadlock
                SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            removeListener();
                        }
                    });
            } else {
                viewer = null;
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

        //change if we have viwers that handle directories
        setEnabled(false);
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
        this.fileModel = file;
        directorySelected = false;

        if (!viewer.isSupported(selectedPath) && checksupportedextensions) {
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

    public void destroy() {
        selfdestroy = true;

        Iterator i = viewercache.iterator();

        while (i.hasNext()) {
            Viewer view = (Viewer) i.next();

            if (view instanceof CacheableViewer) {
                CacheableViewer cacheable = (CacheableViewer) view;
                cacheable.destroy();
            }

            i.remove();
        }

        i = active.iterator();

        while (i.hasNext()) {
            Viewer view = (Viewer) i.next();

            if (view instanceof CacheableViewer) {
                CacheableViewer cacheable = (CacheableViewer) view;
                cacheable.destroy();
            }
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

    private void removeListener() {
        Iterator i = closedViewers.iterator();

        while (i.hasNext()) {
            CacheableViewer view = (CacheableViewer) i.next();
            view.removeViewerCloseListener(this);
            i.remove();
        }
    }

    public void setCheckSupportedExtensions(boolean check) {
        this.checksupportedextensions = check;

        if (!check) {
            setEnabled(true);
        }
    }
}
