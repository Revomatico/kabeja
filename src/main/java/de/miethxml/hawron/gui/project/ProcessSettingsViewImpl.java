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
package de.miethxml.hawron.gui.project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.File;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.cocoon.BeanConfiguration;
import de.miethxml.hawron.gui.conf.PublishDialog;
import de.miethxml.hawron.net.PublishTarget;
import de.miethxml.hawron.project.Project;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;
import de.miethxml.toolkit.gui.LocaleButton;
import de.miethxml.toolkit.gui.LocaleLabel;
import de.miethxml.toolkit.gui.LocaleSeparator;
import de.miethxml.toolkit.gui.StringListView;
import de.miethxml.toolkit.gui.StringListViewImpl;
import de.miethxml.toolkit.gui.help.HelpAction;
import de.miethxml.toolkit.ui.GradientLabel;
import de.miethxml.toolkit.util.ListUtils;

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
public class ProcessSettingsViewImpl implements ProcessSettingsView,
    Serviceable, Initializable, LocaleListener {
    //actioncommands
    private static String ACTION_OK = "ok";
    private static String ACTION_CANCEL = "cancel";
    private static String ACTION_CHOOSE_CONTEXTDIR = "choose.contextdir";
    private static String ACTION_CHOOSE_CONFIGDIR = "choose.configdir";
    private static String ACTION_CHOOSE_WORKDIR = "choose.workdir";
    private static String ACTION_CHOOSE_DESTDIR = "choose.destdir";
    private static String ACTION_CHOOSE_XCONFFILE = "choose.xconffile";
    private static String ACTION_CHOOSE_CHECHSUMURI = "choose.checksumuri";
    private static String ACTION_CHOOSE_LOGKIT = "choose.logkit";
    private static String ACTION_CHOOSE_BROKENLINKREPOERTFILE = "choose.brokenlinkreportfile";
    private static String ACTION_PUBLISHTARGET_ADD = "target.add";
    private static String ACTION_PUBLISHTARGET_EDIT = "target.edit";
    private static String ACTION_PUBLISHTARGET_REMOVE = "target.remove";
    private static String ACTION_PUBLISHTARGET_CHANGE = "target.change";
    private ServiceManager manager;

    //all needed GUI-elements for get/set
    private JDialog dialog;
    private JTabbedPane tp;
    private LocaleButton changePublish;

    //	the project settings
    private JTextField name;
    private JTextField configDir;
    private JTextField ID;
    private JTextArea description;

    //core settings
    private JTextField contextDir;
    private JTextField workDir;
    private JTextField destDir;
    private JTextField xconfFile;
    private JTextField checksumURI;
    private JCheckBox verbose;
    private JCheckBox followLinks;
    private JCheckBox confirmExtensions;
    private JCheckBox precompileOnly;

    //logging/reporting
    private JTextField logKit;
    private JComboBox logger;
    private JComboBox logLevel;
    private JTextField brokenLinkFile;
    private JTextField brokenLinkExtension;
    private JComboBox brokenLinkType;
    private JCheckBox generateBrokenLinks;

    //advanced processSetting
    private JTextField userAgent;
    private JTextField accept;
    private JTextField defaultFilename;
    private StringListView includes;
    private StringListView excludes;
    private StringListView loadClasses;

    //the publish settings
    private JList targets;
    private JTextField publishName;
    private JTextField publishURL;
    private JTextField publishUsername;
    private JPasswordField publishPassword;

    //some data
    private String[] loggerData = { "cli", "core" };
    private String[] loglevelData = {
            "DEBUG", "INFO", "WARN", "ERROR", "FATAL_ERROR"
        };
    private String[] brokenlinkData = { "xml", "text", "none" };

    //internal
    private Controller controller;
    private boolean initialized = false;
    private JFileChooser fc;
    private PublishDialog publishDialog;
    private boolean passwdChanged = false;

    public ProcessSettingsViewImpl() {
        super();
        LocaleImpl.getInstance().addLocaleListener(this);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProcessSettingsView#init()
     *
     */
    public void initialize() {
        dialog = new JDialog();
        dialog.setTitle("Project Settings");

        //often used
        FormLayout layout;
        PanelBuilder builder;
        CellConstraints cc;
        LocaleLabel label;
        LocaleButton button;

        //init main
        controller = new Controller(dialog);

        tp = new JTabbedPane();
        tp.setBorder(BorderFactory.createEmptyBorder());

        dialog.getContentPane().setLayout(new BorderLayout());
        fc = new JFileChooser();
        publishDialog = new PublishDialog();
        publishDialog.init();

        //the main project Settings
        layout = new FormLayout("3dlu,right:pref,2dlu,pref:grow,2dlu,pref,3dlu",
                "3dlu,p,2dlu,p,2dlu,p,9dlu,p,2dlu,fill:p:grow,3dlu");
        builder = new PanelBuilder(layout);
        cc = new CellConstraints();
        label = new LocaleLabel("view.process.settings.label.name");
        builder.add(label, cc.xy(2, 2));
        name = new JTextField(20);
        builder.add(name, cc.xy(4, 2));

        //	optional maybe activate later
        label = new LocaleLabel("view.process.settings.label.id");
        label.setVisible(false);
        builder.add(label, cc.xy(2, 4));
        ID = new JTextField(20);
        ID.setVisible(false);
        builder.add(ID, cc.xy(4, 4));
        label = new LocaleLabel("view.process.settings.label.configdir");
        builder.add(label, cc.xy(2, 6));
        configDir = new JTextField(20);
        builder.add(configDir, cc.xy(4, 6));
        button = new LocaleButton("common.button.choose");
        button.setActionCommand(ACTION_CHOOSE_CONFIGDIR);
        button.addActionListener(controller);
        builder.add(button, cc.xy(6, 6));
        builder.add(new LocaleSeparator(
                "view.process.settings.separator.description"),
            cc.xywh(2, 8, 5, 1));
        description = new JTextArea(5, 20);

        JScrollPane sp = new JScrollPane(description);
        builder.add(sp, cc.xy(4, 10));

        //change to a Locale
        tp.addTab(LocaleImpl.getInstance().getString("view.process.settings.tab.main"),
            builder.getPanel());

        //the cocoon-core settings
        //layout = new
        // FormLayout("3dlu,pref,2dlu,fill:pref:grow,2dlu,pref,9dlu,right:pref,2dlu,fill:pref:grow,3dlu","3dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,3dlu");
        layout = new FormLayout("3dlu,right:pref,2dlu,fill:pref:grow,2dlu,pref,3dlu",
                "3dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,9dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,3dlu");
        builder = new PanelBuilder(layout);
        cc = new CellConstraints();
        builder.add(new LocaleSeparator(
                "view.process.settings.separator.cocoonmain"),
            cc.xywh(2, 2, 5, 1));
        builder.add(new LocaleLabel("view.process.settings.label.contextdir"),
            cc.xy(2, 4));
        contextDir = new JTextField(20);
        builder.add(contextDir, cc.xy(4, 4));
        button = new LocaleButton("common.button.choose");
        button.setActionCommand(ACTION_CHOOSE_CONTEXTDIR);
        button.addActionListener(controller);
        builder.add(button, cc.xy(6, 4));
        builder.add(new LocaleLabel("view.process.settings.label.workdir"),
            cc.xy(2, 6));
        workDir = new JTextField(20);
        builder.add(workDir, cc.xy(4, 6));
        button = new LocaleButton("common.button.choose");
        button.setActionCommand(ACTION_CHOOSE_WORKDIR);
        button.addActionListener(controller);
        builder.add(button, cc.xy(6, 6));
        builder.add(new LocaleLabel("view.process.settings.label.destdir"),
            cc.xy(2, 8));
        destDir = new JTextField(20);
        builder.add(destDir, cc.xy(4, 8));
        button = new LocaleButton("common.button.choose");
        button.setActionCommand(ACTION_CHOOSE_DESTDIR);
        button.addActionListener(controller);
        builder.add(button, cc.xy(6, 8));
        builder.add(new LocaleLabel("view.process.settings.label.xconffile"),
            cc.xy(2, 10));
        xconfFile = new JTextField(20);
        builder.add(xconfFile, cc.xy(4, 10));
        button = new LocaleButton("common.button.choose");
        button.setActionCommand(ACTION_CHOOSE_XCONFFILE);
        button.addActionListener(controller);
        builder.add(button, cc.xy(6, 10));
        builder.add(new LocaleSeparator(
                "view.process.settings.separator.processoptions"),
            cc.xywh(2, 12, 5, 1));
        verbose = new JCheckBox();
        builder.add(verbose, cc.xy(2, 14));
        builder.add(new LocaleLabel("view.process.settings.label.verbose"),
            cc.xy(4, 14));
        followLinks = new JCheckBox();
        builder.add(followLinks, cc.xy(2, 16));
        builder.add(new LocaleLabel("view.process.settings.label.followlinks"),
            cc.xy(4, 16));
        confirmExtensions = new JCheckBox();
        builder.add(confirmExtensions, cc.xy(2, 18));
        builder.add(new LocaleLabel(
                "view.process.settings.label.confirmextensions"), cc.xy(4, 18));
        precompileOnly = new JCheckBox();
        builder.add(precompileOnly, cc.xy(2, 20));
        builder.add(new LocaleLabel(
                "view.process.settings.label.precompileonly"), cc.xy(4, 20));
        tp.addTab(LocaleImpl.getInstance().getString("view.process.settings.tab.cocoon.main"),
            builder.getPanel());

        //the advanced settings
        //loadClasses
        layout = new FormLayout("3dlu,right:pref,2dlu,fill:pref:grow,2dlu,pref,3dlu",
                "3dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,9dlu,p,2dlu,fill:p:grow,3dlu");
        builder = new PanelBuilder(layout);
        cc = new CellConstraints();
        builder.add(new LocaleSeparator(
                "view.process.settings.separator.advanced"), cc.xywh(2, 2, 3, 1));
        builder.add(new LocaleLabel("view.process.settings.label.checksumuri"),
            cc.xy(2, 4));
        checksumURI = new JTextField(20);
        builder.add(checksumURI, cc.xy(4, 4));
        button = new LocaleButton("common.button.choose");
        button.setActionCommand(ACTION_CHOOSE_CHECHSUMURI);
        button.addActionListener(controller);
        builder.add(button, cc.xy(6, 4));
        builder.add(new LocaleLabel(
                "view.process.settings.label.defaulfilename"), cc.xy(2, 6));
        defaultFilename = new JTextField(20);
        builder.add(defaultFilename, cc.xy(4, 6));
        builder.add(new LocaleLabel("view.process.settings.label.useragent"),
            cc.xy(2, 8));
        userAgent = new JTextField(20);
        builder.add(userAgent, cc.xy(4, 8));
        builder.add(new LocaleLabel("view.process.settings.label.accept"),
            cc.xy(2, 10));
        accept = new JTextField(20);
        builder.add(accept, cc.xy(4, 10));
        builder.add(new LocaleSeparator(
                "view.process.settings.separator.loadclasses"),
            cc.xywh(2, 12, 3, 1));
        loadClasses = new StringListViewImpl();
        loadClasses.init();
        builder.add(loadClasses.getView(), cc.xywh(2, 14, 5, 1));
        tp.addTab(LocaleImpl.getInstance().getString("view.process.settings.tab.cocoon.advanced"),
            builder.getPanel());

        //the logging/reporting settings
        layout = new FormLayout("3dlu,right:pref,2dlu,pref,fill:10dlu:grow,2dlu,pref,3dlu",
                "3dlu,p,2dlu,p,2dlu,p,2dlu,p,9dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,3dlu");
        builder = new PanelBuilder(layout);
        cc = new CellConstraints();
        builder.add(new LocaleSeparator(
                "view.process.settings.separator.logging"), cc.xywh(2, 2, 6, 1));
        builder.add(new LocaleLabel("view.process.settings.label.logkit"),
            cc.xy(2, 4));
        logKit = new JTextField(20);
        builder.add(logKit, cc.xywh(4, 4, 2, 1));
        button = new LocaleButton("common.button.choose");
        button.setActionCommand(ACTION_CHOOSE_LOGKIT);
        button.addActionListener(controller);
        builder.add(button, cc.xy(7, 4));
        builder.add(new LocaleLabel("view.process.settings.label.logger"),
            cc.xy(2, 6));
        logger = new JComboBox(loggerData);
        logger.setSelectedIndex(0);
        builder.add(logger, cc.xy(4, 6));
        builder.add(new LocaleLabel("view.process.settings.label.loglevel"),
            cc.xy(2, 8));
        logLevel = new JComboBox(loglevelData);
        logLevel.setEditable(false);
        logLevel.setSelectedIndex(0);
        builder.add(logLevel, cc.xy(4, 8));
        builder.add(new LocaleSeparator(
                "view.process.settings.separator.brokenlinks"),
            cc.xywh(2, 10, 6, 1));
        builder.add(new LocaleLabel(
                "view.process.settings.label.brokenlinkfile"), cc.xy(2, 12));
        brokenLinkFile = new JTextField(20);
        builder.add(brokenLinkFile, cc.xywh(4, 12, 2, 1));
        button = new LocaleButton("common.button.choose");
        button.setActionCommand(ACTION_CHOOSE_BROKENLINKREPOERTFILE);
        button.addActionListener(controller);
        builder.add(button, cc.xy(7, 12));
        builder.add(new LocaleLabel(
                "view.process.settings.label.brokenlinktype"), cc.xy(2, 14));
        brokenLinkType = new JComboBox(brokenlinkData);
        brokenLinkType.setEditable(false);
        builder.add(brokenLinkType, cc.xy(4, 14));
        builder.add(new LocaleLabel(
                "view.process.settings.label.brokenlinkextension"), cc.xy(2, 16));
        brokenLinkExtension = new JTextField(20);
        builder.add(brokenLinkExtension, cc.xy(4, 16));
        builder.add(new LocaleLabel(
                "view.process.settings.label.brokenlinkgenerate"), cc.xy(2, 18));
        generateBrokenLinks = new JCheckBox();
        builder.add(generateBrokenLinks, cc.xy(4, 18));
        tp.addTab(LocaleImpl.getInstance().getString("view.process.settings.tab.cocoon.logging"),
            builder.getPanel());

        //the pattern settings like include/exclude pattern
        layout = new FormLayout("3dlu,pref:grow,3dlu",
                "3dlu,p,2dlu,fill:p:grow,9dlu,p,2dlu,fill:p:grow,3dlu");
        builder = new PanelBuilder(layout);
        cc = new CellConstraints();
        builder.add(new LocaleSeparator(
                "view.process.settings.separator.includes"), cc.xy(2, 2));
        includes = new StringListViewImpl();
        includes.init();
        builder.add(includes.getView(), cc.xy(2, 4));
        builder.add(new LocaleSeparator(
                "view.process.settings.separator.excludes"), cc.xy(2, 6));
        excludes = new StringListViewImpl();
        excludes.init();
        builder.add(excludes.getView(), cc.xy(2, 8));

        //change this to a Locale
        tp.addTab(LocaleImpl.getInstance().getString("view.process.settings.tab.cocoon.pattern"),
            builder.getPanel());

        //the publishing settings here
        layout = new FormLayout("3dlu,right:pref,2dlu,fill:pref:grow,2dlu,pref,3dlu",
                "3dlu,p,3dlu,p,2dlu,p,fill:5dlu:grow,9dlu,p,3dlu,p,2dlu,p,9dlu,p,2dlu,p,2dlu,p,3dlu");
        builder = new PanelBuilder(layout);
        cc = new CellConstraints();
        builder.add(new LocaleSeparator(
                "view.process.settings.separator.publishtargets"),
            cc.xywh(2, 2, 5, 1));

        String[] empty = new String[] {
                "                          ",
                "                                       "
            };
        targets = new JList(empty);
        sp = new JScrollPane(targets);
        builder.add(sp, cc.xywh(4, 4, 1, 4));
        button = new LocaleButton("common.button.edit");
        button.addActionListener(controller);
        button.setActionCommand(ACTION_PUBLISHTARGET_EDIT);
        builder.add(button, cc.xy(6, 4));
        button = new LocaleButton("common.button.remove");
        button.addActionListener(controller);
        button.setActionCommand(ACTION_PUBLISHTARGET_REMOVE);
        builder.add(button, cc.xy(6, 6));
        builder.add(new LocaleSeparator(
                "view.process.settings.separator.publishsettings"),
            cc.xywh(2, 9, 5, 1));
        builder.add(new LocaleLabel("view.process.settings.label.publishtitle"),
            cc.xy(2, 11));
        publishName = new JTextField(20);
        builder.add(publishName, cc.xy(4, 11));
        button = new LocaleButton("view.process.settings.button.publishadd");
        button.addActionListener(controller);
        button.setActionCommand(ACTION_PUBLISHTARGET_ADD);
        builder.add(button, cc.xy(6, 11));
        builder.add(new LocaleLabel("view.process.settings.label.publishurl"),
            cc.xy(2, 13));
        publishURL = new JTextField(20);
        builder.add(publishURL, cc.xy(4, 13));
        changePublish = new LocaleButton(
                "view.process.settings.button.publishchange");
        changePublish.addActionListener(controller);
        changePublish.setActionCommand(ACTION_PUBLISHTARGET_CHANGE);
        changePublish.setEnabled(false);
        builder.add(changePublish, cc.xy(6, 13));
        builder.add(new LocaleSeparator(
                "view.process.settings.separator.publishauth"),
            cc.xywh(2, 15, 3, 1));
        builder.add(new LocaleLabel(
                "view.process.settings.label.publishusername"), cc.xy(2, 17));
        publishUsername = new JTextField(20);
        builder.add(publishUsername, cc.xy(4, 17));
        builder.add(new LocaleLabel(
                "view.process.settings.label.publishpassword"), cc.xy(2, 19));
        publishPassword = new JPasswordField(20);
        publishPassword.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                    passwdChanged = true;
                }

                public void keyTyped(KeyEvent e) {
                    passwdChanged = true;
                }

                public void keyReleased(KeyEvent e) {
                    passwdChanged = true;
                }
            });
        builder.add(publishPassword, cc.xy(4, 19));
        tp.addTab(LocaleImpl.getInstance().getString("view.process.settings.tab.publishing"),
            builder.getPanel());

        //the buttonbar
        JButton[] buttons = new JButton[2];
        buttons[0] = new LocaleButton("common.button.cancel");
        buttons[0].setActionCommand(ACTION_CANCEL);
        buttons[0].addActionListener(controller);
        buttons[1] = new LocaleButton("common.button.ok");
        buttons[1].setActionCommand(ACTION_OK);
        buttons[1].addActionListener(controller);
        button = new LocaleButton("common.button.help");

        //the help system
        button.addActionListener(new HelpAction("processing.htm"));

        ButtonBarBuilder bbuilder = new ButtonBarBuilder();
        bbuilder.addGridded(button);
        bbuilder.addRelatedGap();
        bbuilder.addGlue();
        bbuilder.addGriddedButtons(buttons);

        //add a TitleLabel
        GradientLabel titleLabel = new GradientLabel("Project Settings");
        titleLabel.setFontHeight(28);
        titleLabel.setStartColor(Color.GRAY);
        titleLabel.setEndColor(Color.WHITE);
        titleLabel.setTextColor(Color.BLACK);
        dialog.getContentPane().add(titleLabel, BorderLayout.NORTH);

        //remove the border
        tp.setBorder(BorderFactory.createEmptyBorder());

        //add to Dialog
        dialog.getContentPane().add(tp, BorderLayout.CENTER);

        //the buttonpanel
        JPanel panel = bbuilder.getPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        dialog.getContentPane().add(panel, BorderLayout.SOUTH);
        dialog.setSize(new Dimension(600, 500));

        //dialog.pack();
        initialized = true;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProcessSettingsView#setProject(de.miethxml.project.Project)
     *
     */
    public void setProject(Project project) {
        controller.setProject(project);
    }

    public void init() {
        if (!initialized) {
            initialize();
        }
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

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProcessSettingsView#setVisible(boolean)
     *
     */
    public void setVisible(boolean state) {
        dialog.setVisible(state);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.conf.LocaleListener#langChanged()
     */
    public void langChanged() {
        tp.setTitleAt(0,
            LocaleImpl.getInstance().getString("view.process.settings.tab.main"));
        tp.setTitleAt(1,
            LocaleImpl.getInstance().getString("view.process.settings.tab.cocoon.main"));
        tp.setTitleAt(2,
            LocaleImpl.getInstance().getString("view.process.settings.tab.cocoon.advanced"));
        tp.setTitleAt(3,
            LocaleImpl.getInstance().getString("view.process.settings.tab.cocoon.logging"));
        tp.setTitleAt(4,
            LocaleImpl.getInstance().getString("view.process.settings.tab.cocoon.pattern"));
        tp.setTitleAt(5,
            LocaleImpl.getInstance().getString("view.process.settings.tab.publishing"));
    }

    /**
     *
     * This is the controller for the dialog.
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
    private class Controller implements ActionListener {
        private Project project;
        private JDialog dialog;
        private boolean publishEdit = false;
        private PublishTarget old;

        public Controller(JDialog dialog) {
            this.dialog = dialog;
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

            if (command.equals(ACTION_CANCEL)) {
                setVisible(false);
            } else if (command.equals(ACTION_OK)) {
                Vector validateMsg = validateInput();

                if (validateMsg.size() > 0) {
                    JList list = new JList(validateMsg);
                    list.setOpaque(false);
                    list.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                    int option = JOptionPane.showConfirmDialog(dialog, list,
                            LocaleImpl.getInstance().getString("view.process.validate.message"),
                            JOptionPane.YES_NO_OPTION);

                    if (option == JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                updateProject();
                setVisible(false);
            } else if (command.equals(ACTION_CHOOSE_CONTEXTDIR)) {
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int option = fc.showOpenDialog(dialog);

                if (option == JFileChooser.APPROVE_OPTION) {
                    contextDir.setText(fc.getSelectedFile().getAbsolutePath());

                    //set a logkit
                    if (logKit.getText().length() == 0) {
                        logKit.setText(contextDir.getText() + File.separator
                            + "WEB-INF" + File.separator + "logkit.xconf");
                    }
                }
            } else if (command.equals(ACTION_CHOOSE_WORKDIR)) {
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int option = fc.showOpenDialog(dialog);

                if (option == JFileChooser.APPROVE_OPTION) {
                    workDir.setText(fc.getSelectedFile().getAbsolutePath());
                }
            } else if (command.equals(ACTION_CHOOSE_DESTDIR)) {
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int option = fc.showOpenDialog(dialog);

                if (option == JFileChooser.APPROVE_OPTION) {
                    destDir.setText(fc.getSelectedFile().getAbsolutePath());
                }
            } else if (command.equals(ACTION_CHOOSE_CONFIGDIR)) {
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int option = fc.showOpenDialog(dialog);

                if (option == JFileChooser.APPROVE_OPTION) {
                    configDir.setText(fc.getSelectedFile().getAbsolutePath());
                }
            } else if (command.equals(ACTION_CHOOSE_BROKENLINKREPOERTFILE)) {
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int option = fc.showOpenDialog(dialog);

                if (option == JFileChooser.APPROVE_OPTION) {
                    brokenLinkFile.setText(fc.getSelectedFile().getAbsolutePath());
                }
            } else if (command.equals(ACTION_CHOOSE_XCONFFILE)) {
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int option = fc.showOpenDialog(dialog);

                if (option == JFileChooser.APPROVE_OPTION) {
                    xconfFile.setText(fc.getSelectedFile().getAbsolutePath());
                }
            } else if (command.equals(ACTION_CHOOSE_CHECHSUMURI)) {
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int option = fc.showOpenDialog(dialog);

                if (option == JFileChooser.APPROVE_OPTION) {
                    checksumURI.setText(fc.getSelectedFile().getAbsolutePath());
                }
            } else if (command.equals(ACTION_CHOOSE_LOGKIT)) {
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int option = fc.showOpenDialog(dialog);

                if (option == JFileChooser.APPROVE_OPTION) {
                    logKit.setText(fc.getSelectedFile().getAbsolutePath());
                }
            } else if (command.equals(ACTION_PUBLISHTARGET_ADD)) {
                project.addPublishTarget(getPublishTarget());
                setPublishDefaults();
                changePublish.setEnabled(false);
                publishEdit = false;
                passwdChanged = false;
            } else if (command.equals(ACTION_PUBLISHTARGET_EDIT)) {
                int index = targets.getSelectedIndex();

                if (index > -1) {
                    publishEdit = true;

                    PublishTarget target = project.getPublishTarget(index);
                    publishName.setText(target.getTitle());
                    publishURL.setText(target.getProtocol() + target.getURI());
                    publishUsername.setText(target.getUsername());

                    if (target.getPassword().length() > 0) {
                        publishPassword.setText("++++");
                    } else {
                        publishPassword.setText("");
                    }

                    old = target;
                    passwdChanged = false;
                    changePublish.setEnabled(true);
                }
            } else if (command.equals(ACTION_PUBLISHTARGET_REMOVE)) {
                int index = targets.getSelectedIndex();
                project.removePublishTarget(index);
                setPublishDefaults();
            } else if (command.equals(ACTION_PUBLISHTARGET_CHANGE)) {
                int index = targets.getSelectedIndex();
                project.replacePublishTarget(old, getPublishTarget());
                setPublishDefaults();
                publishEdit = false;
                changePublish.setEnabled(false);
            }
        }

        private void updateProject() {
            //set only changes to the project
            String value;

            //the projectsettings
            value = name.getText();

            if ((value.length() > 0) && !value.equals(project.getTitle())) {
                project.setTitle(value);
            }

            value = configDir.getText();

            if ((value.length() > 0)
                    && !value.equals(project.getConfigLocation())) {
                project.setConfigLocation(value);
            }

            value = ID.getText();

            if ((value.length() > 0) && !value.equals(project.getID())) {
                project.setID(value);
            }

            value = description.getText();

            if ((value.length() > 0) && !value.equals(project.getDescription())) {
                project.setDescription(value);
            }

            //the main CocoonBean settings
            BeanConfiguration config = project.getCocoonBeanConfiguration();
            value = contextDir.getText();

            if ((value.length() > 0) && !value.equals(config.getContextDir())) {
                project.destroyCocoonBean();
                config.setContextDir(value);
            }

            value = workDir.getText();

            if ((value.length() > 0) && !value.equals(config.getWorkDir())) {
                project.destroyCocoonBean();
                config.setWorkDir(value);
            }

            value = destDir.getText();

            if ((value.length() > 0) && !value.equals(config.getDestDir())) {
                config.setDestDir(value);
            }

            value = xconfFile.getText();

            if ((value.length() > 0) && !value.equals(config.getConfigFile())) {
                project.destroyCocoonBean();
                config.setConfigFile(value);
            }

            config.setFollowlinks(followLinks.isSelected());
            config.setVerbose(verbose.isSelected());
            config.setConfirmextenions(confirmExtensions.isSelected());
            config.setPrecompileonly(precompileOnly.isSelected());

            //advanced settings
            value = checksumURI.getText();

            if ((value.length() > 0) && !value.equals(config.getChecksumURI())) {
                config.setChecksumURI(value);
            }

            value = userAgent.getText();

            if ((value.length() > 0) && !value.equals(config.getUserAgent())) {
                project.destroyCocoonBean();
                config.setUserAgent(value);
            }

            value = accept.getText();

            if ((value.length() > 0) && !value.equals(config.getAccept())) {
                project.destroyCocoonBean();
                config.setAccept(value);
            }

            value = defaultFilename.getText();

            if ((value.length() > 0)
                    && !value.equals(config.getDefaultFilename())) {
                config.setDefaultFilename(value);
            }

            config.setLoadClasses(loadClasses.getStringList());

            //logging/reporting
            value = logKit.getText();

            if ((value.length() > 0) && !value.equals(config.getLogKit())) {
                project.destroyCocoonBean();
                config.setLogKit(value);
            }

            value = (String) logger.getSelectedItem();

            if ((value.length() > 0) && !value.equals(config.getLogger())) {
                project.destroyCocoonBean();
                config.setLogger(value);
            }

            value = (String) logLevel.getSelectedItem();

            if ((value.length() > 0) && !value.equals(config.getLogLevel())) {
                project.destroyCocoonBean();
                config.setLogLevel(value);
            }

            value = brokenLinkFile.getText();

            if ((value.length() > 0)
                    && !value.equals(config.getBrokenLinkReportFile())) {
                config.setBrokenLinkReportFile(value);
                project.destroyCocoonBean();
            }

            value = brokenLinkExtension.getText();

            if ((value.length() > 0)
                    && !value.equals(config.getBrokenLinkReportExtension())) {
                config.setBrokenLinkReportExtension(value);
            }

            value = (String) brokenLinkType.getSelectedItem();

            if ((value.length() > 0)
                    && !value.equals(config.getBrokenLinkReportType())) {
                config.setBrokenLinkReportType(value);
            }

            //the include/exclude patterns
            //TODO if this is changed, we need a
            //new CocoonBean here too
            config.setBrokeLinksReport(generateBrokenLinks.isSelected());
            config.setIncludePatterns(includes.getStringList());
            config.setExcludePatterns(excludes.getStringList());

            //the project is changed
            project.setSaved(false);
        }

        public void setProject(Project project) {
            this.project = project;
            setPublishDefaults();

            //set the GUI elements
            //project
            name.setText(project.getTitle());
            configDir.setText(project.getConfigLocation());
            ID.setText(project.getID());
            description.setText(project.getDescription());

            //the CocoonBean config
            //main settings
            BeanConfiguration config = project.getCocoonBeanConfiguration();
            contextDir.setText(config.getContextDir());
            workDir.setText(config.getWorkDir());
            destDir.setText(config.getDestDir());
            xconfFile.setText(config.getConfigFile());
            verbose.setSelected(config.isVerbose());
            followLinks.setSelected(config.isFollowlinks());
            confirmExtensions.setSelected(config.isConfirmextenions());
            precompileOnly.setSelected(config.isPrecompileonly());

            //advanced settings
            checksumURI.setText(config.getChecksumURI());
            defaultFilename.setText(config.getDefaultFilename());
            userAgent.setText(config.getUserAgent());
            accept.setText(config.getAccept());
            loadClasses.setStringList(ListUtils.cloneStringList(
                    config.getLoadClasses()));

            //logging/reporting
            logKit.setText(config.getLogKit());
            logger.setSelectedItem(config.getLogger());
            logLevel.setSelectedItem(config.getLogLevel());
            brokenLinkFile.setText(config.getBrokenLinkReportFile());
            brokenLinkExtension.setText(config.getBrokenLinkReportExtension());
            brokenLinkType.setSelectedItem(config.getBrokenLinkReportType());
            generateBrokenLinks.setSelected(config.isBrokenLinkReporting());

            //the include/exclude patterns
            includes.setStringList(ListUtils.cloneStringList(
                    config.getIncludePatterns()));
            excludes.setStringList(ListUtils.cloneStringList(
                    config.getExcludePatterns()));

            //publishing
            //TODO check if the table add a listener every time -> avoid this
            targets.setModel(project.getTargetsListModel());

            //the filechooserpath
            fc.setCurrentDirectory(new File(config.getContextDir()));

            //the the first tab selected
            tp.setSelectedIndex(0);
        }

        private void setPublishDefaults() {
            publishEdit = false;
            publishName.setText("");
            publishURL.setText("");
            publishUsername.setText("");
            publishPassword.setText("");
        }

        private PublishTarget getPublishTarget() {
            PublishTarget publishTarget = new PublishTarget();
            publishTarget.setTitle(publishName.getText());
            publishTarget.setUsername(publishUsername.getText());

            String url = publishURL.getText();

            if (url.indexOf("://") > -1) {
                //with protocol
                publishTarget.setProtocol(url.substring(0,
                        url.indexOf("://") + 3));
                System.out.println("got Protocl: "
                    + publishTarget.getProtocol());
                publishTarget.setURI(url.substring(url.indexOf("://") + 3));
                System.out.println("got URI: " + publishTarget.getURI());
            } else {
                publishTarget.setProtocol("");
                publishTarget.setURI(url);
            }

            if (passwdChanged) {
                publishTarget.setNewPassword(new String(
                        publishPassword.getPassword()));
            }

            if (!publishEdit) {
                publishTarget.setID("" + project.getPublishTargetCount());
            } else {
                publishTarget.setID(old.getID());
            }

            return publishTarget;
        }

        private Vector validateInput() {
            Vector msg = new Vector();

            File f = new File(contextDir.getText());

            if (!f.exists() || !f.isDirectory()) {
                msg.add(LocaleImpl.getInstance().getString("view.process.validate.no_context_dir"));
            }

            return msg;
        }
    }
}
