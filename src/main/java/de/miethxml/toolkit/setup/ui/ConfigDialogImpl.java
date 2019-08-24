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
package de.miethxml.toolkit.setup.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;
import de.miethxml.toolkit.gui.LocaleButton;
import de.miethxml.toolkit.setup.ApplicationSetup;
import de.miethxml.toolkit.setup.SetupNode;
import de.miethxml.toolkit.ui.GradientLabel;
import de.miethxml.toolkit.ui.PanelFactory;
import de.miethxml.toolkit.ui.SmallShadowBorder;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;


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
public class ConfigDialogImpl implements ActionListener, Serviceable,
    Initializable, TreeSelectionListener, LocaleListener, ConfigDialog,
    Disposable {
    private ServiceManager manager;
    private Hashtable guiconfigurables;
    private JPanel setuppanel;
    private ApplicationSetup treemodel;
    private JTree setuptree;
    private JDialog dialog;

    /**
     * @throws java.awt.HeadlessException
     *
     */
    public ConfigDialogImpl() {
        super();
        guiconfigurables = new Hashtable();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#setComponentManager(de.miethxml.toolkit.component.ComponentManager)
     *
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public void initialize() {
        dialog = new JDialog();
        dialog.setTitle(LocaleImpl.getInstance().getString("dialog.config.title"));
        LocaleImpl.getInstance().addLocaleListener(this);
        dialog.setTitle(LocaleImpl.getInstance().getString("dialog.config.title"));
        dialog.getContentPane().setLayout(new BorderLayout());

        FormLayout layout = new FormLayout("3dlu,pref:grow,3dlu",
                "3dlu,fill:p:grow,3dlu");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();
        JSplitPane sp = PanelFactory.createDefaultSplitPane();

        sp.setDividerLocation(175);
        sp.setDividerSize(10);

        try {
            treemodel = (ApplicationSetup) manager.lookup(ApplicationSetup.ROLE);
        } catch (ServiceException se) {
            se.printStackTrace();
        }

        setuptree = new JTree(treemodel);
        setuptree.addTreeSelectionListener(this);
        setuptree.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        JScrollPane scroll = new JScrollPane(setuptree);
        scroll.setBorder(new SmallShadowBorder());

        sp.setLeftComponent(scroll);
        setuppanel = new JPanel(new BorderLayout());
        setuppanel.setPreferredSize(new Dimension(400, 300));
        scroll = new JScrollPane(setuppanel);

        scroll.setBorder(new SmallShadowBorder());
        sp.setRightComponent(scroll);
        sp.setPreferredSize(sp.getPreferredSize());

        builder.add(sp, cc.xy(2, 2));

        JPanel setupPanel = builder.getPanel();

        dialog.getContentPane().add(setupPanel, BorderLayout.CENTER);

        LocaleButton[] buttons = new LocaleButton[2];
        buttons[0] = new LocaleButton("common.button.cancel");
        buttons[0].setActionCommand("cancel");
        buttons[0].addActionListener(this);

        //buttons.add(button);
        buttons[1] = new LocaleButton("common.button.ok");
        buttons[1].setActionCommand("ok");
        buttons[1].addActionListener(this);

        //buttons.add(button);
        LocaleButton button = new LocaleButton("common.button.help");
        button.setActionCommand("help");
        button.addActionListener(this);

        ButtonBarBuilder bbuilder = new ButtonBarBuilder();
        bbuilder.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        bbuilder.addGridded(button);
        bbuilder.addRelatedGap();
        bbuilder.addGlue();
        bbuilder.addGriddedButtons(buttons);

        //add a TitleLabel
        GradientLabel titleLabel = new GradientLabel("Preferences");
        titleLabel.setFontHeight(28);
        titleLabel.setStartColor(Color.GRAY);
        titleLabel.setEndColor(Color.WHITE);
        titleLabel.setTextColor(Color.BLACK);
        dialog.getContentPane().add(titleLabel, BorderLayout.NORTH);

        JPanel panel = bbuilder.getPanel();
        dialog.getContentPane().add(panel, BorderLayout.SOUTH);

        dialog.setSize(640, 540);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("ok")) {
            setupAll();
            updateGui();
            dialog.dispose();
        } else if (command.equals("cancel")) {
            dialog.dispose();
        }
    }

    private void setupAll() {
        treemodel.setupNodes();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     *
     */
    public void valueChanged(TreeSelectionEvent e) {
        TreePath tp = e.getPath();
        SetupNode node = (SetupNode) tp.getLastPathComponent();
        setuppanel.removeAll();

        if (node.hasGuiConfigurable()) {
            JComponent comp = node.getGuiConfigurable().getSetupComponent();
            setuppanel.add(comp, BorderLayout.CENTER);
            setuppanel.setPreferredSize(comp.getPreferredSize());
        }

        setuppanel.validate();
        setuppanel.repaint();
    }

    public void setVisible(boolean state) {
        if (setuptree.getRowCount() > 0) {
            setuptree.expandRow(setuptree.getRowCount() - 1);
        }

        dialog.setVisible(state);
    }

    private void updateGui() {
    }

    private void validateSettings() {
        //TODO implement validation here
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.conf.LocaleListener#langChanged()
     *
     */
    public void langChanged() {
        dialog.setTitle(LocaleImpl.getInstance().getString("dialog.config.title"));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     *
     */
    public void dispose() {
    }
}
