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
package de.miethxml.toolkit.repository.ui;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import de.miethxml.hawron.gui.context.FileTableCellRenderer;
import de.miethxml.toolkit.gui.LocaleMenuItem;
import de.miethxml.toolkit.io.DefaultFileModel;
import de.miethxml.toolkit.io.FileModel;
import de.miethxml.toolkit.io.FileModelByNameSorter;
import de.miethxml.toolkit.repository.Reloadable;
import de.miethxml.toolkit.repository.RepositoryModelImpl;
import de.miethxml.toolkit.repository.RepositorySelectionController;
import de.miethxml.toolkit.repository.RepositorySelectionListener;
import de.miethxml.toolkit.repository.RepositoryTransferHandler;
import de.miethxml.toolkit.ui.PanelFactory;

/**
 * 
 * 
 * 
 * @author simon
 * 
 * 
 *  
 */
public class RepositoryViewBuilder {
    private RepositoryModelImpl model;

    private JPopupMenu popupmenu;

    private RepositoryTransferHandler dndHandler;

    private RepositorySelectionController selectionController = new RepositorySelectionController();

    private MouseController mouseController = new MouseController();

    private boolean popupEnabled = false;

    /**
     * Build a view (tree,table or tree and table) for the given RepositoryModelImpl. This builder adds a MouseController and 
     * 'Drag and Drop'-Handler. 
     * 
     * @param model The RepositoryModelImpl 
     * @param menu  The popupmenu or null 
     */
    
    
    
    public RepositoryViewBuilder(RepositoryModelImpl model, JPopupMenu menu) {
      
    	
    	this.model = model;

    	//add sorting by names and directories and files
    	
    	model.addFileModelSorter(new FileModelByNameSorter());
    	
        model.addRepositorySelectionListener(mouseController);
        dndHandler = new RepositoryTransferHandler(model);

        if (menu != null) {
            this.popupmenu = menu;
            this.popupEnabled = true;
            //the popupmenu-items
            JMenuItem menuitem = new LocaleMenuItem(
                    "view.context.popupmenu.paste", new ImageIcon(
                            "icons/paste_edit.gif"));
            menuitem.setAccelerator(KeyStroke.getKeyStroke("control v"));
            menuitem.setActionCommand(RepositoryTransferHandler.ACTION_PASTE);
            menuitem.addActionListener(dndHandler);
            popupmenu.add(menuitem);

            menuitem = new LocaleMenuItem("view.context.popupmenu.import");
            menuitem.setActionCommand(RepositoryTransferHandler.ACTION_IMPORT);
            menuitem.addActionListener(dndHandler);
            popupmenu.add(menuitem);
            
            
            menuitem = new JMenuItem(new ArchivAction(model));
            popupmenu.add(menuitem);
        } else {
            this.popupEnabled = false;
        }
    }

    public JComponent getTreeAndTableView() {

        JSplitPane sp = PanelFactory.createDefaultSplitPane();
        

        JScrollPane scroll = new JScrollPane(getRepositoryTree(false));
        sp.setLeftComponent(scroll);

        //the context-table
        JScrollPane scroll2 = new JScrollPane(getRepositoryTable());
        scroll2.getViewport().setBackground(Color.white);
        scroll2.getViewport().setOpaque(true);
        sp.setRightComponent(scroll2);

        //avoid auto-resizing by table-updates
        sp.setPreferredSize(sp.getPreferredSize());

        //set the tree-side to 30% of the view component
        sp.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                ((JSplitPane) e.getSource()).setDividerLocation(0.35);
            }
        });

        return sp;
    }

    /**
     * 
     * @param mixed
     *            show files and direcotries
     * @return
     */

    public JTree getRepositoryTree(boolean mixed) {

        if (mixed) {
            UIManager.put("Tree.leafIcon", new ImageIcon("icons/file.gif"));
            UIManager.put("Tree.closedIcon", new ImageIcon("icons/dir.gif"));
            UIManager.put("Tree.openIcon", new ImageIcon("icons/opendir.gif"));
        } else {
            UIManager.put("Tree.leafIcon", new ImageIcon("icons/dir.gif"));
            UIManager.put("Tree.closedIcon", new ImageIcon("icons/dir.gif"));
            UIManager.put("Tree.openIcon", new ImageIcon("icons/opendir.gif"));
        }

        JTree tree = new JTree(model.getTreeModel(mixed));
        tree.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        tree.setDragEnabled(true);
        tree.setTransferHandler(dndHandler);
        selectionController.setFileSystemTree(tree);

        // tree.setSize(400, 550);
        tree.addTreeSelectionListener(model);
        tree.addMouseListener(mouseController);
        tree.addMouseMotionListener(mouseController);
        
        return tree;
    }

    public JTable getRepositoryTable() {

        JTable table = new JTable(model.getTableModel());
        table.setDragEnabled(true);
        table.setTransferHandler(dndHandler);
        selectionController.setFileSystemTable(table);
        table.getSelectionModel().addListSelectionListener(model);
        table.setShowVerticalLines(false);

        TableColumnModel tcm = table.getColumnModel();
        TableColumn column = tcm.getColumn(0);
        column.setCellRenderer(new FileTableCellRenderer());
        column.setPreferredWidth(16);
        column.setMaxWidth(16);
        column.setMinWidth(16);
        table.setRowHeight(16);

        table.addMouseListener(mouseController);
        table.addMouseMotionListener(mouseController);

        return table;
    }

    private class MouseController extends MouseAdapter implements
            MouseMotionListener, RepositorySelectionListener {
        private boolean prepareDnD = false;

        private String selectedFile = "";

        private boolean popupprocessed = false;

        private FileModel selection;

        private FileModel dummy = new DefaultFileModel(new File(""), null);

        /*
         * 
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
         *  
         */
        public void mouseDragged(MouseEvent e) {
            if (!prepareDnD && !selectedFile.equals(selection.getPath())) {
                JComponent c = (JComponent) e.getSource();
                dndHandler.exportAsDrag(c, e, TransferHandler.COPY);
                prepareDnD = true;
            }
        }

        /*
         * 
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
         *  
         */
        public void mouseMoved(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
            prepareDnD = false;

            if (e.getClickCount() == 2) {
                model.expandSelectedFileModel();
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                //handle Windows
                if (!popupprocessed) {
                    showPopup(e);
                } else {
                    popupprocessed = false;
                }
            }
        }

        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                //handle Linux
                popupprocessed = true;
                showPopup(e);
            } else {
                prepareDnD = false;
                selectedFile = selection.getPath();
            }
        }

        private void showPopup(MouseEvent e) {
            if (popupEnabled) {
                popupmenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        public void directorySelected(Reloadable model, FileModel directory) {
            selection = directory;
        }

        public void fileSelected(Reloadable model, FileModel file) {
            selection = file;
        }

        public void unselect() {
            selection = dummy;
        }
    }
}