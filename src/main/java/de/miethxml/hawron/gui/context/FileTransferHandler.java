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
package de.miethxml.hawron.gui.context;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public class FileTransferHandler extends TransferHandler {
    private FileImportView view;
    private ContextView contextView;
    private boolean droptargetTree = true;

    public FileTransferHandler(String property) {
        super(property);
    }

    public boolean importData(
        JComponent c,
        Transferable t) {
        if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                List list = (List) t.getTransferData(DataFlavor.javaFileListFlavor);

                if (list.size() > 0) {
                    Iterator i = list.iterator();
                    ArrayList files = new ArrayList();

                    while (i.hasNext()) {
                        File file = (File) i.next();

                        files.add(file);
                    }

                    //invoke GUI
                    view.setImportFileList(files);
                }

                return true;
            } catch (UnsupportedFlavorException ufe) {
                ufe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            //this is for linux and will only works with Nautilus/Gnome
            // filemanager
            //KDE/Konqueror will not work
            try {
                String text = (String) t.getTransferData(DataFlavor.stringFlavor);
                String[] list = text.split("\n");

                if (list.length > 0) {
                    ArrayList files = new ArrayList();

                    for (int i = 0; i < list.length; i++) {
                        if (list[i].startsWith("file:")) {
                            try {
                                File file = new File(new URI(list[i].trim()));
                                files.add(file);
                            } catch (URISyntaxException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    //invoke GUI
                    view.setImportFileList(files);

                    return true;
                }
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean canImport(
        JComponent c,
        DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].isFlavorJavaFileListType()) {
                return true;
            }
        }

        //for linux there is the FileListFlavor not supported
        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].isFlavorTextType()) {
                //String text = flavors[i].toString();
                return true;
            }
        }

        return false;
    }

    public void setView(FileImportView view) {
        this.view = view;
    }

    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }

    protected Transferable createTransferable(JComponent c) {
        FileListTransferable trans = null;

        if (contextView != null) {
            trans = new FileListTransferable();

            File f = contextView.getSelectedFile();

            if (f != null) {
                List files = new ArrayList();
                files.add(f);
                trans.setFileList(files);
            }
        }

        return trans;
    }

    /**
     * @param contextView
     *            The contextView to set.
     *
     */
    public void setContextView(ContextView contextView) {
        this.contextView = contextView;
    }

    /**
     * @return Returns the droptargetTree.
     */
    public boolean isDroptargetTree() {
        return droptargetTree;
    }

    /**
     * @param droptargetTree
     *            The droptargetTree to set.
     */
    public void setDroptargetTree(boolean droptargetTree) {
        this.droptargetTree = droptargetTree;
    }

    public class FileListTransferable implements Transferable {
        private DataFlavor[] flavors;
        private List files;

        public FileListTransferable() {
            flavors = new DataFlavor[] { DataFlavor.javaFileListFlavor };
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
         *
         */
        public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }

            return files;
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
         *
         */
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
         *
         */
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            if (flavor.equals(DataFlavor.javaFileListFlavor)) {
                return true;
            }

            return false;
        }

        public void setFileList(List files) {
            this.files = files;
        }
    }
}
