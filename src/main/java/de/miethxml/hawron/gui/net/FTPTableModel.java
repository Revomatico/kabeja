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
package de.miethxml.hawron.gui.net;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import de.miethxml.toolkit.conf.LocaleImpl;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 * @deprecated
 */
public class FTPTableModel implements TableModel {
    ArrayList listeners;
    String user;
    String password;
    String ftpsite;
    FTPFile[] currentDir;
    FTPClient ftp;

    /**
     *
     *
     *
     */
    public FTPTableModel() {
        super();

        listeners = new ArrayList();

        user = "";

        password = "";

        ftpsite = "localhost";

        currentDir = new FTPFile[0];

        //init();
    }

    public FTPTableModel(
        String ftpsite,
        String user,
        String password) {
        listeners = new ArrayList();

        this.ftpsite = ftpsite;

        this.user = user;

        this.password = password;

        currentDir = new FTPFile[0];

        //init();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getColumnCount()
     *
     */
    public int getColumnCount() {
        return 4;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getRowCount()
     *
     */
    public int getRowCount() {
        if ((currentDir != null) && (currentDir.length > 0)) {
            return currentDir.length;
        }

        return 1;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     *
     */
    public boolean isCellEditable(
        int arg0,
        int arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getColumnClass(int)
     *
     */
    public Class getColumnClass(int arg0) {
        if (arg0 > -1) {
            return "".getClass();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     *
     */
    public Object getValueAt(
        int arg0,
        int arg1) {
        String value = "";

        if ((currentDir != null) && (currentDir.length > 0)) {
            switch (arg1) {
            case 0:

                if (currentDir[arg0].isDirectory()) {
                    value = "DIR";
                } else if (currentDir[arg0].isFile()) {
                    value = "FILE";
                } else {
                    value = "OTHER";
                }

                break;

            case 1:
                value = currentDir[arg0].getName();

                break;

            case 2:
                value = Long.toString(currentDir[arg0].getSize());

                break;

            case 3:

                Calendar cal = currentDir[arg0].getTimestamp();
                DateFormat df = DateFormat.getDateInstance();

                value = df.format(cal.getTime());

                break;
            }
        } else {
            if (arg1 == 1) {
                value = "-- empty --";
            }
        }

        return value;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     *
     */
    public void setValueAt(
        Object arg0,
        int arg1,
        int arg2) {
    }

    /*
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
            return LocaleImpl.getInstance().getString("ftp.table.column.name");

        case 2:
            return LocaleImpl.getInstance().getString("ftp.table.column.size");

        case 3:
            return LocaleImpl.getInstance().getString("ftp.table.column.modifieddate");
        }

        return "";
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
     *
     */
    public void addTableModelListener(TableModelListener arg0) {
        listeners.add(arg0);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
     *
     */
    public void removeTableModelListener(TableModelListener arg0) {
        for (int i = 0; i < listeners.size(); i++) {
            TableModelListener tml = (TableModelListener) listeners.get(i);

            if (tml.equals(arg0)) {
                listeners.remove(i);

                return;
            }
        }
    }

    public void init() {
        fireTableUpdate();
    }

    private void fireTableUpdate() {
        for (int i = 0; i < listeners.size(); i++) {
            TableModelListener tml = (TableModelListener) listeners.get(i);

            tml.tableChanged(new TableModelEvent(this));
        }
    }

    public void setFTPDir(FTPFile[] dir) {
        this.currentDir = dir;

        fireTableUpdate();
    }

    public String getSelectedName(int index) {
        if ((index >= 0) && (index < currentDir.length)) {
            return currentDir[index].getName();
        }

        return "";
    }
}
