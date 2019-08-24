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
package de.miethxml.toolkit.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 */
public class SelectorComponent {
    private JPopupMenu popupmenu = new JPopupMenu();
    private Hashtable actions = new Hashtable();
    private JButton popupButton;
    private JMenuItem selectedItem;
    private LinkedList items = new LinkedList();

    /**
     *
     */
    public SelectorComponent() {
        super();
    }

    public JComponent getView() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setOpaque(false);
        panel.setBorder(new SplineBorder());

        JButton button = new JButton(new ImageIcon("icons/button-back.png"));
        button.setUI(new BasicButtonUI());
        button.setPreferredSize(new Dimension(12, 14));

        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFocusable(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (items.size() > 0) {
                        int index = items.indexOf(selectedItem);

                        if (index > 0) {
                            index--;
                        } else {
                            //get the last
                            index = items.size() - 1;
                        }

                        JMenuItem item = (JMenuItem) items.get(index);
                        item.doClick();
                        setSelectedItem(item);
                    }
                }
            });

        panel.add(button);

        popupButton = new JButton("   ");
        popupButton.setUI(new BasicButtonUI());
        popupButton.setFocusable(false);
        popupButton.setBorder(new SmallTriangleBorder());
        popupButton.setOpaque(false);
        popupButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Component c = (Component) e.getSource();
                    popupmenu.show(c, 0, c.getHeight());
                }
            });
        panel.add(popupButton);

        button = new JButton(new ImageIcon("icons/button-next.png"));
        button.setUI(new BasicButtonUI());
        button.setPreferredSize(new Dimension(12, 14));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFocusable(false);
        button.setOpaque(false);
        panel.add(button);
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (items.size() > 0) {
                        int index = items.indexOf(selectedItem);

                        if (index < (items.size() - 1)) {
                            index++;
                        } else {
                            //get the first
                            index = 0;
                        }

                        JMenuItem item = (JMenuItem) items.get(index);
                        item.doClick();
                        setSelectedItem(item);
                    }
                }
            });

        panel.setMaximumSize(panel.getPreferredSize());
        panel.setAlignmentX(JComponent.RIGHT_ALIGNMENT);

        return panel;
    }

    public void addAction(Action action) {
        JMenuItem item = new JMenuItem(action);
        action.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    Action a = (Action) evt.getSource();

                    if (selectedItem.getAction() != a) {
                        setSelectedAction(a);
                    }
                }
            });
        items.add(item);
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JMenuItem item = (JMenuItem) e.getSource();
                    setSelectedItem(item);
                }
            });
        actions.put(item, action);
        popupmenu.add(item);

        if (items.size() == 1) {
            //activate the first entry
            item.doClick();
            setSelectedItem(item);
        }
    }

    private void setSelectedItem(JMenuItem item) {
        Action action = (Action) actions.get(item);
        Icon icon = (Icon) action.getValue(Action.SMALL_ICON);

        if (icon != null) {
            popupButton.setIcon(icon);
            popupButton.setText(item.getText());
        } else {
            popupButton.setText(item.getText());
            popupButton.setIcon(null);
        }

        selectedItem = item;
    }

    private void setSelectedAction(Action action) {
        Enumeration e = actions.keys();

        while (e.hasMoreElements()) {
            JMenuItem item = (JMenuItem) e.nextElement();
            Action a = (Action) actions.get(item);

            if (a == action) {
                setSelectedItem(item);
            }
        }
    }
}
