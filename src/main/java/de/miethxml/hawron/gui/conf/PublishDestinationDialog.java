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
package de.miethxml.hawron.gui.conf;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.net.PublishTarget;
import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.PublishDestination;
import de.miethxml.hawron.project.Task;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;


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
public class PublishDestinationDialog extends JDialog implements ActionListener,
    LocaleListener {
    Project project;
    PublishDestination publishDestination;
    PublishDestination old;
    Task task;
    JPanel main;
    JPanel ftpsite;
    JTextField title;
    JTextField destination;
    JTextField source;
    JComboBox targets;
    boolean newpublish;
    Hashtable allbuttons;
    Hashtable labels;

    /**
     * @throws java.awt.HeadlessException
     *
     */
    public PublishDestinationDialog(
        Project project,
        Task task) throws HeadlessException {
        super();
        this.project = project;
        this.task = task;
        publishDestination = new PublishDestination();
        labels = new Hashtable();
        allbuttons = new Hashtable();
    }

    /**
     * @param owner
     *
     * @throws java.awt.HeadlessException
     *
     */
    public PublishDestinationDialog(Dialog owner) throws HeadlessException {
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
    public PublishDestinationDialog(
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
    public PublishDestinationDialog(Frame owner) throws HeadlessException {
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
    public PublishDestinationDialog(
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
    public PublishDestinationDialog(
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
    public PublishDestinationDialog(
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
    public PublishDestinationDialog(
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
    public PublishDestinationDialog(
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
    public PublishDestinationDialog(
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
    public PublishDestinationDialog(
        Frame owner,
        String title,
        boolean modal,
        GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
    }

    public void init() {
        this.setTitle("");

        FormLayout mainlayout = new FormLayout("3dlu,right:pref,3dlu,pref:grow,3dlu,pref,3dlu",
                "3dlu,p,2dlu,p,2dlu,p,2dlu,p,3dlu");

        //		mainlayout.setRowGroups(new int[][] { { 1, 3, 5 }
        //		});
        PanelBuilder builder = new PanelBuilder(mainlayout);
        builder.setDefaultDialogBorder();

        CellConstraints cc = new CellConstraints();
        labels.put("dialog.publishdestination.label.title",
            builder.addLabel("", cc.xy(2, 2)));
        title = new JTextField(20);
        builder.add(title, cc.xy(4, 2));
        labels.put("dialog.publishdestination.label.source",
            builder.addLabel("", cc.xy(2, 4)));
        source = new JTextField(20);
        builder.add(source, cc.xy(4, 4));

        JButton button = new JButton("");
        allbuttons.put("dialog.publishdestination.button.choosesource", button);
        button.addActionListener(this);
        button.setActionCommand("choose.source");
        builder.add(button, cc.xy(6, 4));
        labels.put("dialog.publishdestination.label.destination",
            builder.addLabel("", cc.xy(2, 6)));
        destination = new JTextField(20);
        builder.add(destination, cc.xy(4, 6));
        labels.put("dialog.publishdestination.label.targets",
            builder.addLabel("", cc.xy(2, 8)));
        targets = new JComboBox();
        builder.add(targets, cc.xy(4, 8));
        this.getContentPane().add(builder.getPanel(), BorderLayout.CENTER);

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
        this.getContentPane().add(bbuilder.getPanel(), BorderLayout.SOUTH);
        pack();
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
        } else if (e.getActionCommand().equals("ok")) {
            if (newpublish) {
                task.addPublishDestination(getPublishDestination());
            } else {
                task.replacePublishDestination(old, getPublishDestination());
            }

            setVisible(false);
        } else if (e.getActionCommand().equals("choose.source")) {
            JFileChooser fc = new JFileChooser(task.getBuildDir());
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                source.setText(fc.getSelectedFile().getPath());
            }
        }
    }

    /**
     * @return Returns the publishDestination.
     *
     */
    public PublishDestination getPublishDestination() {
        //set values
        publishDestination.setTitle(title.getText());
        publishDestination.setDestination(destination.getText());
        publishDestination.setSource(source.getText());

        if (targets.getSelectedIndex() > -1) {
            PublishTarget pt = project.getPublishTarget(targets
                    .getSelectedIndex());
            publishDestination.setTargetID(pt.getID());
        } else {
            //alert
        }

        return publishDestination;
    }

    /**
     * @param publishDestination
     *
     * The publishDestination to set.
     *
     */
    public void setPublishDestination(PublishDestination publishDestination) {
        if (publishDestination == null) {
            this.publishDestination = new PublishDestination();
            newpublish = true;
        } else {
            this.publishDestination = (PublishDestination) publishDestination
                .clone();
            newpublish = false;
            old = publishDestination;
        }

        title.setText(this.publishDestination.getTitle());
        destination.setText(this.publishDestination.getDestination());
        source.setText(this.publishDestination.getSource());
        targets.setModel(project.getTargetsListModel());
    }

    private PublishDestination clonePublish(PublishDestination source) {
        PublishDestination clone = new PublishDestination();
        clone.setTitle(source.getTitle());
        clone.setDestination(source.getDestination());
        clone.setSource(source.getSource());
        clone.setTargetID(source.getTargetID());

        return clone;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.conf.TranslationListener#langChanged()
     *
     */
    public void langChanged() {
        LocaleImpl lang = LocaleImpl.getInstance();
        this.setTitle(lang.getString("dialog.publishdestination.title"));

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

    /**
     * @return Returns the task.
     *
     */
    public Task getTask() {
        return task;
    }

    /**
     * @param task
     *            The task to set.
     *
     */
    public void setTask(Task task) {
        this.task = task;
    }

    /**
     * @return Returns the project.
     *
     */
    public Project getProject() {
        return project;
    }

    /**
     * @param project
     *            The project to set.
     *
     */
    public void setProject(Project project) {
        this.project = project;
    }
}
