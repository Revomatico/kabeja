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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.cocoon.SitemapWrapper;
import de.miethxml.hawron.gui.conf.PublishDestinationDialog;
import de.miethxml.hawron.project.ProcessURI;
import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.PublishDestination;
import de.miethxml.hawron.project.Task;
import de.miethxml.hawron.xml.SAXSitemapBuilder;

import de.miethxml.toolkit.cache.GUICache;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;
import de.miethxml.toolkit.ui.GradientLabel;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 *
 * @deprecated
 */
public class TaskEditor extends JDialog implements ActionListener,
    ListSelectionListener, LocaleListener {
    private JTextField title;
    private JTextField dest;
    private JTextField docroot;
    private JTextArea description;
    private JCheckBox followLinks;
    private JCheckBox confirmExtensions;
    private JCheckBox buildChanges;
    private JCheckBox clear;
    private JList publish;
    private JList uris;
    private JList sitemap;
    private JTextField sitemapedit;
    private JPanel main;
    private Task task;
    private Task oldtask;
    private Project project;
    private boolean newTask;
    private boolean editURI;
    private String oldURI;

    //labels
    Hashtable labels;

    //buttons
    Hashtable allbuttons;
    private StringBuffer validateMsg;

    /**
     * @throws java.awt.HeadlessException
     *
     */
    public TaskEditor(Project project) throws HeadlessException {
        super();
        task = new Task();
        this.project = project;
        labels = new Hashtable();
        allbuttons = new Hashtable();
        validateMsg = new StringBuffer();
    }

    /**
     * @param owner
     *
     * @throws java.awt.HeadlessException
     *
     */
    public TaskEditor(Dialog owner) throws HeadlessException {
        super(owner);
    }

    /**
     * @param owner
     *
     * @param modal
     *
     * @throws java.awt.HeadlessException
     *
     */
    public TaskEditor(
        Dialog owner,
        boolean modal) throws HeadlessException {
        super(owner, modal);
    }

    /**
     * @param owner
     *
     * @throws java.awt.HeadlessException
     *
     */
    public TaskEditor(Frame owner) throws HeadlessException {
        super(owner);
    }

    /**
     * @param owner
     *
     * @param modal
     *
     * @throws java.awt.HeadlessException
     *
     */
    public TaskEditor(
        Frame owner,
        boolean modal) throws HeadlessException {
        super(owner, modal);
    }

    /**
     * @param owner
     *
     * @param title
     *
     * @throws java.awt.HeadlessException
     *
     */
    public TaskEditor(
        Dialog owner,
        String title) throws HeadlessException {
        super(owner, title);
    }

    /**
     * @param owner
     *
     * @param title
     *
     * @param modal
     *
     * @throws java.awt.HeadlessException
     *
     */
    public TaskEditor(
        Dialog owner,
        String title,
        boolean modal) throws HeadlessException {
        super(owner, title, modal);
    }

    /**
     * @param owner
     *
     * @param title
     *
     * @throws java.awt.HeadlessException
     *
     */
    public TaskEditor(
        Frame owner,
        String title) throws HeadlessException {
        super(owner, title);
    }

    /**
     * @param owner
     *
     * @param title
     *
     * @param modal
     *
     * @throws java.awt.HeadlessException
     *
     */
    public TaskEditor(
        Frame owner,
        String title,
        boolean modal) throws HeadlessException {
        super(owner, title, modal);
    }

    /**
     * @param owner
     *
     * @param title
     *
     * @param modal
     *
     * @param gc
     *
     * @throws java.awt.HeadlessException
     *
     */
    public TaskEditor(
        Dialog owner,
        String title,
        boolean modal,
        GraphicsConfiguration gc) throws HeadlessException {
        super(owner, title, modal, gc);
    }

    /**
     * @param owner
     *
     * @param title
     *
     * @param modal
     *
     * @param gc
     *
     */
    public TaskEditor(
        Frame owner,
        String title,
        boolean modal,
        GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
    }

    public void init() {
        this.setTitle("");

        FormLayout mainlayout = new FormLayout("right:pref,3dlu,70dlu:grow,3dlu,min,3dlu,fill:pref,3dlu,right:min,1dlu,left:70dlu:grow",
                "p,3dlu,p,2dlu,p,2dlu,p,9dlu,p,3dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,9dlu,p,3dlu,p,2dlu,p,2dlu,p,2dlu,p,3dlu");
        mainlayout.setRowGroups(new int[][] {
                { 1, 3, 5 }
            });

        PanelBuilder builder = new PanelBuilder(mainlayout);
        builder.setDefaultDialogBorder();

        CellConstraints cc = new CellConstraints();
        builder.addSeparator("Main", cc.xywh(1, 1, 11, 1));
        labels.put("dialog.tasksettings.label.title",
            builder.addLabel("", cc.xy(1, 3)));
        title = new JTextField(20);
        builder.add(title, cc.xywh(3, 3, 3, 1));
        labels.put("dialog.tasksettings.label.description",
            builder.addLabel("",
                cc.xy(7, 3, CellConstraints.RIGHT, CellConstraints.DEFAULT)));
        description = new JTextArea(20, 4);

        JScrollPane sp = new JScrollPane(description);
        builder.add(sp, cc.xywh(9, 3, 3, 5));
        labels.put("dialog.tasksettings.label.documentroot",
            builder.addLabel("", cc.xy(1, 5)));
        docroot = new JTextField();
        builder.add(docroot, cc.xy(3, 5));

        JButton button = new JButton("...");
        allbuttons.put("dialog.tasksettings.button.docrootchoose", button);
        button.setActionCommand("docroot.choose");
        button.addActionListener(this);
        builder.add(button, cc.xy(5, 5));
        labels.put("dialog.tasksettings.label.buildpath",
            builder.addLabel("", cc.xy(1, 7)));
        dest = new JTextField();
        builder.add(dest, cc.xy(3, 7));
        button = new JButton("...");
        allbuttons.put("dialog.tasksettings.button.destinationchoose", button);
        button.setActionCommand("destination.choose");
        button.addActionListener(this);
        builder.add(button, cc.xy(5, 7));
        builder.addSeparator("Proccessing", cc.xywh(1, 9, 11, 1));
        labels.put("dialog.tasksettings.label.uris",
            builder.addLabel("", cc.xy(3, 11)));
        labels.put("dialog.tasksettings.label.sitemap",
            builder.addLabel("", cc.xy(9, 11)));
        uris = new JList(task.getProcessListModel());
        uris.setPrototypeCellValue("Index 12345678910111213      ");
        sp = new JScrollPane(uris);
        builder.add(sp, cc.xywh(3, 13, 3, 5));
        button = new JButton("");
        allbuttons.put("dialog.tasksettings.button.uriadd", button);
        button.setActionCommand("sitemap.uri.add");
        button.addActionListener(this);
        builder.add(button, cc.xy(7, 13));
        button = new JButton("");
        allbuttons.put("dialog.tasksettings.button.uriedit", button);
        button.setActionCommand("sitemap.uri.edit");
        button.addActionListener(this);
        builder.add(button, cc.xy(7, 15));
        button = new JButton("");
        allbuttons.put("dialog.tasksettings.button.uriremove", button);
        button.setActionCommand("sitemap.uri.remove");
        button.addActionListener(this);
        builder.add(button, cc.xy(7, 17));

        ListModel lm = buildSitemap();

        if (lm != null) {
            sitemap = new JList(lm);
        } else {
            sitemap = new JList();
        }

        sitemap.setPrototypeCellValue("Index 12345678910111213      ");
        sitemap.addListSelectionListener(this);
        sitemap.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sp = new JScrollPane(sitemap);
        builder.add(sp, cc.xywh(9, 13, 3, 5));
        sitemapedit = new JTextField(20);
        builder.add(sitemapedit, cc.xywh(9, 19, 3, 1));
        builder.addSeparator("Publishing", cc.xywh(1, 21, 7, 1));
        labels.put("dialog.tasksettings.label.destinations",
            builder.addLabel("", cc.xy(1, 23)));
        publish = new JList(task.getPublishListModel());
        sp = new JScrollPane(publish);
        builder.add(sp, cc.xywh(3, 23, 3, 7));
        button = new JButton("");
        allbuttons.put("dialog.tasksettings.button.publishadd", button);
        button.setActionCommand("publish.add");
        button.addActionListener(this);
        builder.add(button, cc.xy(7, 23));
        button = new JButton("");
        allbuttons.put("dialog.tasksettings.button.publishedit", button);
        button.setActionCommand("publish.edit");
        button.addActionListener(this);
        builder.add(button, cc.xy(7, 25));
        button = new JButton("");
        allbuttons.put("dialog.tasksettings.button.publishremove", button);
        button.setActionCommand("publish.remove");
        button.addActionListener(this);
        builder.add(button, cc.xy(7, 27));
        builder.addSeparator("Cocoon Settings", cc.xywh(9, 21, 3, 1));
        labels.put("dialog.tasksettings.label.followlinks",
            builder.addLabel("", cc.xy(11, 23)));
        followLinks = new JCheckBox();
        builder.add(followLinks, cc.xy(9, 23));
        labels.put("dialog.tasksettings.label.confirmextensions",
            builder.addLabel("confirm Extensions", cc.xy(11, 25)));
        confirmExtensions = new JCheckBox();
        builder.add(confirmExtensions, cc.xy(9, 25));
        labels.put("dialog.tasksettings.label.buildchanges",
            builder.addLabel("", cc.xy(11, 27)));
        buildChanges = new JCheckBox();
        builder.add(buildChanges, cc.xy(9, 27));
        labels.put("dialog.tasksettings.label.clearbuilddirectory",
            builder.addLabel("clear build directory", cc.xy(11, 29)));
        clear = new JCheckBox();
        builder.add(clear, cc.xy(9, 29));
        this.getContentPane().add(builder.getPanel(), BorderLayout.CENTER);

        //JPanel buttons = new JPanel(new FlowLayout());
        JButton[] buttons = new JButton[2];
        buttons[0] = new JButton("");
        allbuttons.put("common.button.cancel", buttons[0]);
        buttons[0].setActionCommand("cancel");
        buttons[0].addActionListener(this);

        //buttons.add(button);
        buttons[1] = new JButton("");
        allbuttons.put("common.button.ok", buttons[1]);
        buttons[1].setActionCommand("ok");
        buttons[1].addActionListener(this);

        //buttons.add(button);
        button = new JButton("");
        allbuttons.put("common.button.help", button);

        ButtonBarBuilder bbuilder = new ButtonBarBuilder();
        bbuilder.addGridded(button);
        bbuilder.addRelatedGap();
        bbuilder.addGlue();
        bbuilder.addGriddedButtons(buttons);
        LocaleImpl.getInstance().addLocaleListener(this);
        langChanged();

        GradientLabel titleLabel = new GradientLabel("Task Settings");
        titleLabel.setFontHeight(28);
        this.getContentPane().add(titleLabel, BorderLayout.NORTH);

        this.getContentPane().add(bbuilder.getPanel(), BorderLayout.SOUTH);
        pack();
    }

    /**
     * @return Returns the task.
     *
     */
    public Task getTask() {
        task.setTitle(title.getText());
        task.setDescription(description.getText());
        task.setBuildDir(dest.getText());
        task.setDocRoot(docroot.getText());
        task.setDiffBuild(buildChanges.isSelected());
        task.setConfirmExtensions(confirmExtensions.isSelected());
        task.setFollowLinks(followLinks.isSelected());
        task.setCleanBuild(clear.isSelected());

        if (newTask) {
            task.setID(Integer.toString(project.getTasks().size()));
        }

        return task;
    }

    /**
     * @param task
     *
     * The task to set.
     *
     */
    public void setTask(Task t) {
        if (t != null) {
            this.oldtask = t;
            this.task = (Task) t.clone();
            newTask = false;
        } else {
            this.task = new Task();
            this.task.setBeanConfiguration(project.getCocoonBeanConfiguration());
            newTask = true;
        }

        setDefaults();
        title.setText(task.getTitle());
        description.setText(task.getDescription());
        dest.setText(task.getBuildDir());
        docroot.setText(task.getDocRoot());
        buildChanges.setSelected(task.isDiffBuild());
        followLinks.setSelected(task.isFollowLinks());
        confirmExtensions.setSelected(task.isConfirmExtensions());
        clear.setSelected(task.isCleanBuild());
        uris.setModel(task.getProcessListModel());
        publish.setModel(task.getPublishListModel());
        sitemapedit.setText("");
        sitemap.setSelectedIndex(0);
    }

    private ListModel buildSitemap() {
        if (project != null) {
            SAXSitemapBuilder sax = new SAXSitemapBuilder();
            SitemapWrapper sitemapmodel = sax.parseURI(project.getContextPath()
                    + File.separator + "sitemap.xmap");

            return sitemapmodel;
        }

        return null;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("cancel")) {
            setVisible(false);
        }

        if (e.getActionCommand().equals("sitemap.uri.add")) {
            if (editURI) {
                //task.replaceProcessURI(oldURI, sitemapedit.getText());
                editURI = false;
                oldURI = null;
            } else {
                ProcessURI uri = new ProcessURI();
                uri.setUri(sitemapedit.getText());
                task.addProcessURI(uri);
            }

            sitemapedit.setText("");
        }

        if (e.getActionCommand().equals("sitemap.uri.remove")) {
            int index = uris.getSelectedIndex();
            task.removeProcessURI(index);
        }

        if (e.getActionCommand().equals("sitemap.uri.edit")) {
            editURI = true;
            sitemapedit.setText((String) uris.getSelectedValue());
            oldURI = (String) uris.getSelectedValue();
        }

        if (e.getActionCommand().equals("ok")) {
            if (!validateInput()) {
                validateMsg.append("Add this task to project?");

                int option = JOptionPane.showConfirmDialog(this, validateMsg);

                switch (option) {
                case JOptionPane.CANCEL_OPTION:
                    return;

                case JOptionPane.NO_OPTION:
                    setVisible(false);

                    return;
                }
            }

            if (newTask) {
                project.addTask(getTask());
            } else {
                project.replaceTask(oldtask, getTask());
                oldtask = null;
            }

            setVisible(false);
        } else if (e.getActionCommand().equals("destination.choose")) {
            JFileChooser fc = new JFileChooser(".");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                dest.setText(fc.getSelectedFile().getPath());
            }
        } else if (e.getActionCommand().equals("docroot.choose")) {
            JFileChooser fc = new JFileChooser(project.getContextPath());
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                docroot.setText(fc.getSelectedFile().getPath());
            }
        } else if (e.getActionCommand().equals("publish.add")) {
            PublishDestinationDialog pd;

            if (!GUICache.getInstance().cached("dialog.publishdestination")) {
                pd = new PublishDestinationDialog(project, task);
                pd.init();
                GUICache.getInstance().addComponent("dialog.publishdestination",
                    pd);
            }

            pd = (PublishDestinationDialog) GUICache.getInstance().getComponent("dialog.publishdestination");
            pd.setPublishDestination(null);
            pd.setProject(this.project);
            pd.setVisible(true);
        } else if (e.getActionCommand().equals("publish.remove")) {
            int index = publish.getSelectedIndex();
            task.removePublishDestination((PublishDestination) task.getPublishDestinations()
                                                                   .get(index));
        } else if (e.getActionCommand().equals("publish.edit")) {
            PublishDestinationDialog pd;

            if (!GUICache.getInstance().cached("dialog.publishdestination")) {
                pd = new PublishDestinationDialog(project, task);
                pd.init();
                GUICache.getInstance().addComponent("dialog.publishdestination",
                    pd);
            }

            pd = (PublishDestinationDialog) GUICache.getInstance().getComponent("dialog.publishdestination");
            pd.setProject(this.project);

            int index = publish.getSelectedIndex();
            pd.setPublishDestination((PublishDestination) task.getPublishDestinations()
                                                              .get(index));
            pd.setVisible(true);
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     *
     */
    public void valueChanged(ListSelectionEvent e) {
        //int first = e.getFirstIndex();
        //int last = e.getLastIndex();
        String uri = (String) sitemap.getSelectedValue();
        sitemapedit.setText(uri);
    }

    private void setDefaults() {
        clear.setSelected(false);
        confirmExtensions.setSelected(false);
        followLinks.setSelected(true);
        buildChanges.setSelected(false);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.conf.TranslationListener#langChanged()
     *
     */
    public void langChanged() {
        LocaleImpl lang = LocaleImpl.getInstance();
        this.setTitle(lang.getString("dialog.tasksettings.title"));

        Enumeration e = labels.keys();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            JLabel label = (JLabel) labels.get(key);
            label.setText(lang.getString(key));
        }

        e = allbuttons.keys();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            JButton button = (JButton) allbuttons.get(key);
            button.setText(lang.getString(key));
        }
    }

    public boolean validateInput() {
        boolean valid = true;
        validateMsg.delete(0, validateMsg.length());

        if ((title.getText() == null) || (title.getText().length() == 0)) {
            valid = false;
            validateMsg.append("You must choose a title.\n");
        }

        if ((dest.getText() == null) || (dest.getText().length() == 0)) {
            valid = false;
            validateMsg.append("You must set a build-directory.\n");
        }

        if ((task.getProcessURI().size() == 0)
                && (task.getPublishDestinations().size() == 0)) {
            valid = false;
            validateMsg.append(
                "This task has no processURI and publish-destination.\n");
        }

        return valid;
    }
}
