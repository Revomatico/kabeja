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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;


/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 *
 *
 *
 */
public class JGoodiesSeparator extends JPanel {
    private String text = "";
    private JLabel label;

    public JGoodiesSeparator() {
        this("");
    }

    public JGoodiesSeparator(String text) {
        super();

        if (text != null) {
            this.text = text;
        }

        init();
    }

    private void init() {
        createLabel();

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.weightx = 0.0;

        gbc.weighty = 1.0;

        gbc.anchor = GridBagConstraints.SOUTHWEST;

        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridwidth = 1;

        gbc.gridheight = 3;

        label.setVerticalAlignment(SwingConstants.CENTER);

        label.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 4));

        label.setText(text);

        add(label, gbc);

        gbc.weightx = 1.0;

        gbc.weighty = 1.0;

        gbc.gridwidth = GridBagConstraints.REMAINDER;

        gbc.gridheight = 1;

        JSeparator separator = new JSeparator();

        add(Box.createGlue(), gbc);

        gbc.weighty = 0.0;

        //gbc.fill = GridBagConstraints.BOTH;
        add(separator, gbc);

        gbc.weighty = 1.0;

        add(Box.createGlue(), gbc);
    }

    public void setText(String text) {
        this.text = text;

        label.setText(text);

        validate();
    }

    public void updateUI() {
        super.updateUI();

        createLabel();

        Color foreground = UIManager.getColor("TitledBorder.titleColor");

        if (foreground != null) {
            label.setForeground(foreground);
        }

        label.setFont(UIManager.getFont("TitledBorder.font"));
    }

    private synchronized void createLabel() {
        if (label == null) {
            label = new JLabel();
        }
    }
}
