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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;

import javax.swing.JPanel;


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
public class BorderPanel extends JPanel {
    String borderTitle;
    GridBagLayout gbl;
    FineBorder border;

    /**
     *
     *
     *
     */
    public BorderPanel(String title) {
        super();
        this.borderTitle = title;
        border = new FineBorder(title);
        setBorder(border);
        gbl = new GridBagLayout();
        setLayout(gbl);
    }

    public BorderPanel(
        String title,
        String icon) {
        super();
        this.borderTitle = title;
        border = new FineBorder(title, icon, this);
        setBorder(border);
        gbl = new GridBagLayout();
        setLayout(gbl);
    }

    /**
     * @param isDoubleBuffered
     *
     */
    public BorderPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    /**
     * @param layout
     *
     */
    public BorderPanel(LayoutManager layout) {
        super(layout);
    }

    /**
     * @param layout
     *
     * @param isDoubleBuffered
     *
     */
    public BorderPanel(
        LayoutManager layout,
        boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    public void addComponent(
        Component c,
        int x,
        int y,
        int width,
        int height) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbl.setConstraints(c, gbc);
        add(c);
    }

    public void setTitle(String title) {
        border.setTitle(title);
        this.borderTitle = title;
    }
}
