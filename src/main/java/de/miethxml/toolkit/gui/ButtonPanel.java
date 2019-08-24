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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.LayoutManager;

import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


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
public class ButtonPanel extends JPanel {
    private Hashtable actions;
    private JPanel toolbar;
    private String localeKey;
    private LocaleSeparator separator;
    private int buttonSize = 28;
    private int hgap = 2;
    private int vgap = 2;

    /**
     *
     *
     *
     */
    public ButtonPanel() {
        super();
        actions = new Hashtable();
    }

    /**
     * @param isDoubleBuffered
     *
     */
    public ButtonPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    /**
     * @param layout
     *
     */
    public ButtonPanel(LayoutManager layout) {
        super(layout);
    }

    /**
     * @param layout
     *
     * @param isDoubleBuffered
     *
     */
    public ButtonPanel(
        LayoutManager layout,
        boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    public ButtonPanel(String localeKey) {
        this();
        this.localeKey = localeKey;
    }

    public void init() {
        //build the layout
        FormLayout layout = new FormLayout("3dlu,fill:pref:grow,3dlu",
                "3dlu,p,2dlu,top:p:grow,3dlu");
        setLayout(layout);

        CellConstraints cc = new CellConstraints();
        LocaleSeparator separator = new LocaleSeparator(localeKey);
        add(separator, cc.xy(2, 2));
        toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
        toolbar.setBorder(BorderFactory.createEmptyBorder());
        add(toolbar, cc.xy(2, 4));
        setVisible(false);
    }

    public void addAction(Action action) {
        if (!isVisible()) {
            setVisible(true);
        }

        JButton button = new JButton(action);
        button.setText("");
        button.setPreferredSize(new Dimension(buttonSize, buttonSize));
        button.setMargin(new Insets(0, 0, 0, 0));

        toolbar.add(button);
        actions.put(action, button);

        layoutToolBar();
    }

    public void removeAction(Action action) {
        if (actions.containsKey(action)) {
            JButton button = (JButton) actions.remove(action);
            toolbar.remove(button);

            if (actions.size() == 0) {
                setVisible(false);
            }

            layoutToolBar();
        }
    }

    public void setButtonSize(int size) {
    }

    private void layoutToolBar() {
        //this will invoke the FlowLayout to
        //create a new row if it needed, the FlowLayout
        //doesnt recodnize the size of the parent, so
        //we have to set a preferredSize
        int height = (int) (
                (
                    Math.floor(((buttonSize + hgap) * actions.size()) / getPreferredSize()
                                                                            .getWidth()) * (
                        buttonSize + vgap
                    )
                ) + vgap
            );

        if (height > (buttonSize + vgap)) {
            toolbar.setPreferredSize(new Dimension(
                    (int) this.getPreferredSize().getWidth(), height));
        } else {
            toolbar.setPreferredSize(new Dimension(
                    (int) this.getPreferredSize().getWidth(),
                    (buttonSize + vgap)));
        }

        this.validate();
        toolbar.validate();
        this.repaint();
    }
}
