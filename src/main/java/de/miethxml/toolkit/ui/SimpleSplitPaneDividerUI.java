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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;


/**
 * @author simon
 *
 *
 *
 */
public class SimpleSplitPaneDividerUI extends BasicSplitPaneDivider {
    private JButton button;
    protected int expandedLocation = -1;
    protected boolean expanded = true;
    protected int buttonSize = 10;

    /**
     * @param ui
     */
    public SimpleSplitPaneDividerUI(BasicSplitPaneUI ui) {
        super(ui);
        button = createTouchButton();
        add(button);
        setBorder(null);
    }

    public void paint(Graphics g) {
        if (splitPane.isOneTouchExpandable()) {
            g.setColor(getBackground());

            if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
                button.setBounds(2, 1, 5, 10);
                ;
            } else {
                int y = splitPane.getDividerSize();
                button.setBounds(1, y - 7, 10, 5);
            }

            super.paintComponents(g);
        }
    }

    protected JButton createLeftOneTouchButton() {
        return null;
    }

    protected JButton createRightOneTouchButton() {
        return null;
    }

    protected JButton createTouchButton() {
        JButton b = new JButton() {
                protected void paintComponent(Graphics g) {
                    Polygon p = new Polygon();
                    g.setColor(getBackground());

                    if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
                        //clear
                        g.fillRect(0, 0, 5, 10);

                        if (expanded) {
                            p.addPoint(5, 0);
                            p.addPoint(0, 5);
                            p.addPoint(5, 10);
                        } else {
                            p.addPoint(0, 0);
                            p.addPoint(5, 5);
                            p.addPoint(0, 10);
                        }
                    } else {
                        //Vertical
                        //clear
                        g.fillRect(0, 0, 10, 5);

                        if (expanded) {
                            p.addPoint(0, 0);
                            p.addPoint(5, 5);
                            p.addPoint(10, 0);
                        } else {
                            p.addPoint(0, 5);
                            p.addPoint(5, 0);
                            p.addPoint(10, 5);
                        }
                    }

                    g.setColor(Color.GRAY);
                    g.fillPolygon(p);
                }
            };

        b.setToolTipText("Hide");
        b.setSize(7, 21);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        b.setBorderPainted(false);
        b.setFocusable(false);
        b.setUI(new BasicButtonUI());
        b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int location = splitPane.getDividerLocation();

                    if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
                        if (location > 0) {
                            expandedLocation = location;
                            expanded = false;
                            button.setToolTipText("Show");
                            splitPane.setDividerLocation(0);
                        } else {
                            expanded = true;
                            button.setToolTipText("Hide");
                            splitPane.setDividerLocation(expandedLocation);
                        }
                    } else {
                        if (location == (
                                    splitPane.getHeight()
                                    - splitPane.getDividerSize()
                                )) {
                            //show
                            expanded = true;
                            button.setToolTipText("Hide");
                            splitPane.setDividerLocation(expandedLocation);
                        } else {
                            //hide
                            expandedLocation = location;
                            expanded = false;
                            button.setToolTipText("Show");
                            splitPane.setDividerLocation(splitPane.getHeight());
                        }
                    }

                    splitPane.setLastDividerLocation(location);
                }
            });

        return b;
    }
}
