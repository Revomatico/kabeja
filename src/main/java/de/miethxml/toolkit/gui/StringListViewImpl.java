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
package de.miethxml.toolkit.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public class StringListViewImpl extends JPanel implements StringListView,
    ActionListener {
    private StringListModel model;
    private JTextField input;
    private JLabel title;
    private JList list;

    public StringListViewImpl() {
        model = new StringListModel();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.gui.StringListView#setStringList(java.util.List)
     *
     */
    public void setStringList(List list) {
        model.setStringList(list);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.gui.StringListView#getStringList()
     *
     */
    public List getStringList() {
        return model.getStringList();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.gui.StringListView#init()
     *
     */
    public void init() {
        FormLayout layout = new FormLayout("right:pref,3dlu,60dlu:grow,3dlu,pref",
                "p,2dlu,p,2dlu,p:grow,2dlu,p");
        CellConstraints cc = new CellConstraints();
        setLayout(layout);
        title = new JLabel("");
        add(title, cc.xy(1, 1));
        list = new JList(model);

        JScrollPane sp = new JScrollPane(list);
        add(sp, cc.xywh(3, 1, 1, 5));

        LocaleButton button = new LocaleButton("view.stringlist.button.add");
        button.setActionCommand("add");
        button.addActionListener(this);
        add(button, cc.xy(5, 1));
        button = new LocaleButton("view.stringlist.button.remove");
        button.setActionCommand("remove");
        button.addActionListener(this);
        add(button, cc.xy(5, 3));

        LocaleLabel label = new LocaleLabel("view.stringlist.label.edit");
        add(label, cc.xy(1, 7));
        input = new JTextField(20);
        input.setActionCommand("add");
        input.addActionListener(this);
        add(input, cc.xy(3, 7));
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("add")) {
            if (input.getText().length() > 0) {
                model.add(input.getText().trim());
                input.setText("");
            }
        } else if (e.getActionCommand().equals("remove")) {
            int index = list.getSelectedIndex();

            if ((index > -1) && (index < model.getSize())) {
                input.setText(model.remove(index));
            }
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.gui.StringListView#getView()
     *
     */
    public JPanel getView() {
        return this;
    }

    private class StringListModel implements ListModel {
        private ArrayList listener;
        private List stringList;

        public StringListModel() {
            listener = new ArrayList();
            stringList = new ArrayList();
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
         *
         */
        public void addListDataListener(ListDataListener arg0) {
            listener.add(arg0);
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ListModel#getElementAt(int)
         *
         */
        public Object getElementAt(int arg0) {
            return stringList.get(arg0);
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ListModel#getSize()
         *
         */
        public int getSize() {
            return stringList.size();
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
         *
         */
        public void removeListDataListener(ListDataListener arg0) {
            listener.remove(arg0);
        }

        private void fireListUpdate() {
            Iterator i = listener.iterator();

            while (i.hasNext()) {
                ListDataListener l = (ListDataListener) i.next();
                l.contentsChanged(new ListDataEvent(this,
                        ListDataEvent.CONTENTS_CHANGED, 0, stringList.size()));
            }
        }

        public void add(String string) {
            stringList.add(string);
            fireListUpdate();
        }

        public String remove(int index) {
            String value = (String) stringList.remove(index);
            fireListUpdate();

            return value;
        }

        /**
         * @return Returns the stringList.
         *
         */
        public List getStringList() {
            return stringList;
        }

        /**
         * @param stringList
         *
         * The stringList to set.
         *
         */
        public void setStringList(List stringList) {
            this.stringList = stringList;
            fireListUpdate();
        }
    }
}
