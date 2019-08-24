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

import java.io.File;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import de.miethxml.hawron.io.Directory;
import de.miethxml.hawron.io.UniqueFile;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.io.DefaultFileModel;
import de.miethxml.toolkit.io.FileModelFilter;
import de.miethxml.toolkit.repository.Reloadable;
import de.miethxml.toolkit.repository.RepositoryModel;
import de.miethxml.toolkit.repository.RepositorySelectionListener;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *@deprecated
 */
public class FileSystemModel implements TreeModel, TableModel,
    TreeSelectionListener, ListSelectionListener, Reloadable, RepositoryModel {
    private String baseDir;
    private ArrayList treelistener;
    private ArrayList tablelistener;
    private ArrayList fsmListener;
    private Directory root;
    private Directory currentDir;
    private Directory selection;
    private SimpleDateFormat dateformat;
    private Date date;
    private NumberFormat sizeformat;
    private File selectedFile;
    private Boolean isFile = new Boolean(true);
    private Boolean isDirectory = new Boolean(false);

    /**
     *
     *
     *
     */
    public FileSystemModel() {
        this("");
    }

    public FileSystemModel(String baseDir) {
        super();
        this.baseDir = baseDir;
        root = new Directory(new File(baseDir));
        treelistener = new ArrayList();
        tablelistener = new ArrayList();
        fsmListener = new ArrayList();
        currentDir = root;
        dateformat = new SimpleDateFormat(" EEE, dd. MMM yyyy hh:mm:ss");
        date = new Date();
        sizeformat = NumberFormat.getInstance();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
     *
     */
    public void addTreeModelListener(TreeModelListener arg0) {
        treelistener.add(arg0);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     *
     */
    public Object getChild(
        Object arg0,
        int arg1) {
        Directory d = (Directory) arg0;

        if (arg1 < d.getDirCount()) {
            Directory child = d.getDir(arg1);
            child.listDirectory();

            return child;
        }

        return null;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     *
     */
    public int getChildCount(Object arg0) {
        Directory d = (Directory) arg0;

        return d.getDirCount();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,
     *
     * java.lang.Object)
     *
     */
    public int getIndexOfChild(
        Object arg0,
        Object arg1) {
        Directory d = (Directory) arg0;
        Directory child = (Directory) arg1;

        for (int i = 0; i < d.getDirCount(); i++) {
            Directory subdir = d.getDir(i);

            if (subdir.equals(child)) {
                return i;
            }
        }

        return 0;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeModel#getRoot()
     *
     */
    public Object getRoot() {
        return root;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     *
     */
    public boolean isLeaf(Object arg0) {
        Directory d = (Directory) arg0;

        if (d.getDirCount() == 0) {
            return true;
        }

        return false;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
     *
     */
    public void removeTreeModelListener(TreeModelListener arg0) {
        for (int i = 0; i < treelistener.size(); i++) {
            TreeModelListener tml = (TreeModelListener) treelistener.get(i);

            if (tml.equals(arg0)) {
                treelistener.remove(i);

                return;
            }
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath,
     *
     * java.lang.Object)
     *
     */
    public void valueForPathChanged(
        TreePath arg0,
        Object arg1) {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
     *
     */
    public void addTableModelListener(TableModelListener arg0) {
        tablelistener.add(arg0);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getColumnClass(int)
     *
     */
    public Class getColumnClass(int arg0) {
        return "".getClass();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getColumnCount()
     *
     */
    public int getColumnCount() {
        return 4;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getColumnName(int)
     *
     */
    public String getColumnName(int arg0) {
        switch (arg0) {
        case 0:
            return " ";

        case 1:
            return LocaleImpl.getInstance().getString("panel.filesystem.table.column.name");

        case 2:
            return LocaleImpl.getInstance().getString("panel.filesystem.table.column.size");

        case 3:
            return LocaleImpl.getInstance().getString("panel.filesystem.table.column.modifieddate");
        }

        return "";
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getRowCount()
     *
     */
    public int getRowCount() {
        return currentDir.getCount();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     *
     */
    public Object getValueAt(
        int arg0,
        int arg1) {
        File f;

        boolean parentDir = false;
        Boolean type;

        if (currentDir.getEntry(arg0).getClass().isInstance(root)) {
            Directory d = (Directory) currentDir.getEntry(arg0);
            f = d.getFile();
            parentDir = d.isParentDirectory();
            type = isDirectory;
        } else {
            //is a UniqueFile
            UniqueFile uf = (UniqueFile) currentDir.getEntry(arg0);
            f = uf.getFile();
            type = isFile;
        }

        switch (arg1) {
        case 0:
            return type;

        case 1:

            if (parentDir) {
                return "..";
            }

            return f.getName();

        case 2:
            return sizeformat.format(f.length());

        case 3:
            date.setTime(f.lastModified());

            return dateformat.format(date);
        }

        return "";
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     *
     */
    public boolean isCellEditable(
        int arg0,
        int arg1) {
        if (arg1 == 1) {
            return true;
        }

        return false;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
     *
     */
    public void removeTableModelListener(TableModelListener arg0) {
        for (int i = 0; i < tablelistener.size(); i++) {
            TableModelListener tml = (TableModelListener) tablelistener.get(i);

            if (tml.equals(arg0)) {
                tablelistener.remove(i);

                return;
            }
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     *
     */
    public void setValueAt(
        Object arg0,
        int arg1,
        int arg2) {
        if ((arg2 == 1) && (arg0 != null)) {
            String name = (String) arg0;
            File f = null;

            if (currentDir.getEntry(arg1).getClass().isInstance(root)) {
                Directory d = (Directory) currentDir.getEntry(arg1);
                f = d.getFile();
            } else {
                //is a UniqueFile
                UniqueFile uf = (UniqueFile) currentDir.getEntry(arg1);
                f = uf.getFile();
            }

            if (f != null) {
                File newfile = new File(f.getParentFile().getAbsolutePath()
                        + File.separator + name);
                f.renameTo(newfile);
                reload();
            } else {
            }
        }
    }

    /**
     * @return
     */
    public String getBaseDir() {
        return baseDir;
    }

    /**
     * @param baseDir
     *
     */
    public void setBase(String baseDir) {
        this.baseDir = baseDir;
        root.setFile(new File(baseDir));
        currentDir = root;
        fireTreeUpdate();
        fireTableUpdate();
        reload();
    }

    private void fireTreeUpdate() {
        for (int i = 0; i < treelistener.size(); i++) {
            TreeModelListener tml = (TreeModelListener) treelistener.get(i);
            Directory[] dir = new Directory[1];

            //check if we have to change the root
            if (currentDir.getPath().length() < root.getPath().length()) {
                root = currentDir;
                root.setParentDirectory(false);
            }

            dir[0] = root;
            tml.treeStructureChanged(new TreeModelEvent(this, dir));
        }
    }

    private void fireCurrentTreeUpdate() {
        //create the treepath
        ArrayList list = new ArrayList();
        Directory p = currentDir;

        while ((p != null) && (p != root)) {
            list.add(p);

            //go one step up
            p = p.getParent();
        }

        //add the root as last
        list.add(root);
        Collections.reverse(list);

        Object[] path = list.toArray();

        //create the TreeEvent and fire to all listeners
        TreeModelEvent event = new TreeModelEvent(this, path);
        Iterator i = treelistener.iterator();

        while (i.hasNext()) {
            TreeModelListener l = (TreeModelListener) i.next();
            l.treeStructureChanged(event);
        }
    }

    private void fireTableUpdate() {
        for (int i = 0; i < tablelistener.size(); i++) {
            TableModelListener tml = (TableModelListener) tablelistener.get(i);
            tml.tableChanged(new TableModelEvent(this));
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     *
     */
    public void valueChanged(TreeSelectionEvent arg0) {
        TreePath tp = arg0.getPath();
        currentDir = (Directory) tp.getLastPathComponent();
        currentDir.listDirectory();
        selectedFile = currentDir.getFile();
        fireDirectorySelectedEvent(selectedFile.getAbsolutePath());
        fireTableUpdate();
    }

    /**
     *
     * Returns the current selected directory
     *
     * @return current directory
     *
     */
    public String getCurrentDirectory() {
        return currentDir.getPath();
    }

    public void reload() {
        if (currentDir.removed() && currentDir.hasParent()) {
            currentDir = currentDir.getParent();
        }

        //reload the directory
        currentDir.listDirectory();
        fireCurrentTreeUpdate();
        fireTableUpdate();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     *
     */
    public void valueChanged(ListSelectionEvent e) {
        //int last = e.getLastIndex();
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        int first = lsm.getMinSelectionIndex();

        if (first > -1) {
            if (currentDir.getEntry(first).getClass().isInstance(root)) {
                if (currentDir.getEntry(first) != null) {
                    //the selection is not the currentDir
                    selection = (Directory) currentDir.getEntry(first);
                    selectedFile = selection.getFile();
                    fireDirectorySelectedEvent(selectedFile.getAbsolutePath());
                } else {
                    fireUnselectEvent();
                }
            } else {
                selectedFile = ((UniqueFile) currentDir.getEntry(first))
                    .getFile();
                fireFileSelectedEvent(selectedFile.getAbsolutePath());
            }
        } else {
            //there was no selection
            fireUnselectEvent();
        }
    }

    public void expandSelectedFileModel() {
        if ((selection != null) && selection.getFile().isDirectory()) {
            boolean updateTree = false;

            //check if the tree shows the parent directory
            if (currentDir.getPath().length() > selection.getPath().length()) {
                updateTree = true;
            }

            currentDir = selection;
            currentDir.listDirectory();
            fireTableUpdate();

            if (updateTree) {
                fireTreeUpdate();
            }

            fireDirectorySelectedEvent(currentDir.getPath());
        }
    }

    public void addRepositorySelectionListener(RepositorySelectionListener l) {
        fsmListener.add(l);
    }

    public void removeRepositorySelectionListener(
        RepositorySelectionListener l) {
        fsmListener.remove(l);
    }

    private void fireDirectorySelectedEvent(String path) {
        for (int i = 0; i < fsmListener.size(); i++) {
            RepositorySelectionListener l = (RepositorySelectionListener) fsmListener
                .get(i);
            l.directorySelected(this, new DefaultFileModel(new File(path), null));
        }
    }

    private void fireFileSelectedEvent(String path) {
        for (int i = 0; i < fsmListener.size(); i++) {
            RepositorySelectionListener l = (RepositorySelectionListener) fsmListener
                .get(i);
            File f = new File(path);
            l.fileSelected(this,
                new DefaultFileModel(f,
                    new DefaultFileModel(f.getParentFile(), null)));
        }
    }

    private void fireUnselectEvent() {
        for (int i = 0; i < fsmListener.size(); i++) {
            RepositorySelectionListener l = (RepositorySelectionListener) fsmListener
                .get(i);
            l.unselect();
        }
    }

    /**
     *
     * Returns the selected file or directory
     *
     * @return the selected file or directory
     *
     */
    public File getSelectedFile() {
        return selectedFile;
    }
    public void addFileModelFilter(FileModelFilter filter) {
        // TODO Auto-generated method stub

    }
    public void removeFileModelFilter(FileModelFilter filter) {
        // TODO Auto-generated method stub

    }
}
