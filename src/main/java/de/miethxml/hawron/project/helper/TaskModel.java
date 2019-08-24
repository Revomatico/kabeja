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
package de.miethxml.hawron.project.helper;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.Task;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public class TaskModel implements TableModel, ListModel, LocaleListener {
    private Project project;
    private ArrayList listListeners = new ArrayList();
    private ArrayList tableListeners = new ArrayList();

    /**
     *
     */
    public TaskModel() {
        super();
        LocaleImpl.getInstance().addLocaleListener(this);
    }

    public TaskModel(Project project) {
        this.project = project;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 2;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        // TODO Auto-generated method stub
        return project.getTasks().size();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(
        int rowIndex,
        int columnIndex) {
        if (columnIndex < 2) {
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Boolean.class;
        }

        return String.class;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(
        int rowIndex,
        int columnIndex) {
        Task task = (Task) project.getTasks().get(rowIndex);

        switch (columnIndex) {
        case 0:
            return Boolean.valueOf(task.isEnabled());

        case 1:

            if (task.getPublishDestinations().size() > 0) {
                return task.getTitle() + " (p)";
            }

            return task.getTitle();

        case 2:

            String value = "";

            if (task.getProcessURI().size() > 0) {
                value = value
                    + LocaleImpl.getInstance().getString("view.process.table.task.info.processing");
            }

            if (task.getPublishDestinations().size() > 0) {
                value = value
                    + LocaleImpl.getInstance().getString("view.process.table.task.info.publishing");
            }

            return value;
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(
        Object aValue,
        int rowIndex,
        int columnIndex) {
        Task task = (Task) project.getTasks().get(rowIndex);

        switch (columnIndex) {
        case 0:

            Boolean b = (Boolean) aValue;
            task.setEnabled(b.booleanValue());

            return;

        case 1:

            String title = (String) aValue;
            task.setTitle(title);

            return;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return " ";

        //return
        // LocaleImpl.getInstance().getDisplayName("view.process.table.task.header.process");
        case 1:
            return LocaleImpl.getInstance().getString("view.process.table.task.header.name");

        case 2:
            return LocaleImpl.getInstance().getString("view.process.table.task.header.info");
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
     */
    public void addTableModelListener(TableModelListener l) {
        tableListeners.add(l);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
     */
    public void removeTableModelListener(TableModelListener l) {
        tableListeners.remove(l);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        return project.getTasks().size();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index) {
        if ((index >= 0) && (index < project.getTasks().size())) {
            String name = ((Task) project.getTasks().get(index)).getTitle();

            return name;
        }

        return "";
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
     */
    public void addListDataListener(ListDataListener l) {
        listListeners.add(l);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
     */
    public void removeListDataListener(ListDataListener l) {
        listListeners.remove(l);
    }

    /**
     * @param project
     *            The project to set.
     */
    public void setProject(Project project) {
        this.project = project;
    }

    public void fireListUpdate() {
        Iterator i = listListeners.iterator();

        while (i.hasNext()) {
            ListDataListener l = (ListDataListener) i.next();
            l.contentsChanged(new ListDataEvent(this,
                    ListDataEvent.CONTENTS_CHANGED, 0, project.getTasks().size()));
        }
    }

    public void fireTableUpdate() {
        Iterator i = tableListeners.iterator();

        while (i.hasNext()) {
            TableModelListener l = (TableModelListener) i.next();
            l.tableChanged(new TableModelEvent(this));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.conf.LocaleListener#langChanged()
     */
    public void langChanged() {
        //update the table header
        Iterator i = tableListeners.iterator();

        while (i.hasNext()) {
            TableModelListener l = (TableModelListener) i.next();
            l.tableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
        }
    }
}
