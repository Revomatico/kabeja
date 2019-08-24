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
package de.miethxml.hawron.gui.context.bookmark;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;


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
public class BookmarkTransferHandler extends TransferHandler {
    private BookmarkViewImpl view;

    /**
     * @param property
     *
     */
    public BookmarkTransferHandler(String property) {
        super(property);
    }

    public void setBookmarkViewImpl(BookmarkViewImpl view) {
        this.view = view;
    }

    public boolean importData(
        JComponent c,
        Transferable t) {
        if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                List list = (List) t.getTransferData(DataFlavor.javaFileListFlavor);

                if ((list != null) && (list.size() > 0)) {
                    File file = (File) list.get(0);
                    Bookmark bm = new Bookmark();

                    if (!file.isDirectory()) {
                        bm.setSource(file.getParent());
                        bm.setName(file.getParentFile().getName());
                    } else {
                        bm.setSource(file.getAbsolutePath());
                        bm.setName(file.getName());
                    }

                    view.addBookmark(bm);

                    return true;
                }
            } catch (UnsupportedFlavorException ufe) {
                ufe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String text = (String) t.getTransferData(DataFlavor.stringFlavor);
                String[] list = text.split("\n");

                if ((list.length > 0) && list[0].startsWith("file:")) {
                    try {
                        File file = new File(new URI(list[0].trim()));
                        Bookmark bm = new Bookmark();

                        if (!file.isDirectory()) {
                            bm.setSource(file.getParent());
                            bm.setName(file.getParentFile().getName());
                        } else {
                            bm.setSource(file.getAbsolutePath());
                            bm.setName(file.getName());
                        }

                        view.addBookmark(bm);

                        return true;
                    } catch (URISyntaxException e1) {
                        e1.printStackTrace();
                    }
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
                String text = flavors[i].toString();

                return true;
            }
        }

        return false;
    }
}
