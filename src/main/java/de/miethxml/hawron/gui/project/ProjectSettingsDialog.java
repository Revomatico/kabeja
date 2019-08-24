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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.cocoon.BeanConfiguration;
import de.miethxml.hawron.gui.conf.PublishDialog;
import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.Task;

import de.miethxml.toolkit.cache.GUICache;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;


/**
 * @author Simon Mieth
 *
 *
 *
 *
 *
 * @deprecated
 */
public class ProjectSettingsDialog extends JDialog implements ActionListener,
    Serviceable, Initializable, LocaleListener, ProcessSettingsView {
    ServiceManager manager;
    Project project;
    JTextField title;
    JTextArea description;
    JTextField workDir;
    JTextField contextDir;
    JTextField destinationDir;
    JTextField configFile;
    JList targets;
    JList tasks;

    //labels
    Hashtable labels;
    Hashtable allbuttons;

    public ProjectSettingsDialog(Project project) {
        super();

        if (project == null) {
            this.project = new Project();
        } else {
            this.project = project;
        }

        labels = new Hashtable();
        allbuttons = new Hashtable();
        initialize();
        setProject(this.project);
    }

    public ProjectSettingsDialog() {
        super();
        labels = new Hashtable();
        allbuttons = new Hashtable();
    }

    public void initialize() {
        FormLayout layout = new FormLayout("3dlu,right:pref,3dlu,70dlu:grow,3dlu,fill:min,12dlu,pref:grow,3dlu,fill:min,3dlu",
                "3dlu,pref,5dlu,pref,2dlu,pref,2dlu,pref,min:grow,9dlu,pref,5dlu,pref,2dlu,pref,2dlu,pref,2dlu,pref,9dlu");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        //General-part
        builder.addSeparator("General", cc.xywh(2, 2, 5, 1));
        labels.put("dialog.projectsettings.label.title",
            builder.addLabel("", cc.xy(2, 4)));
        title = new JTextField(30);
        builder.add(title, cc.xywh(4, 4, 3, 1));
        labels.put("dialog.projectsettings.label.description",
            builder.addLabel("", cc.xy(2, 6)));
        description = new JTextArea(16, 30);

        JScrollPane sp = new JScrollPane(description);
        builder.add(sp, cc.xywh(4, 6, 3, 4));

        //FTP-sites part
        builder.addSeparator("Publish Targets", cc.xywh(8, 2, 3, 1));

        String[] empty = new String[] {
                "                          ",
                "                                       "
            };
        targets = new JList(empty);
        sp = new JScrollPane(targets);
        builder.add(sp, cc.xywh(8, 4, 1, 6));

        JButton button = new JButton("Add");
        allbuttons.put("dialog.projectsettings.button.publishadd", button);
        button.addActionListener(this);
        button.setActionCommand("target.add");
        builder.add(button, cc.xy(10, 4));
        button = new JButton("Edit");
        allbuttons.put("dialog.projectsettings.button.publishedit", button);
        button.addActionListener(this);
        button.setActionCommand("target.edit");
        builder.add(button, cc.xy(10, 6));
        button = new JButton("Remove");
        allbuttons.put("dialog.projectsettings.button.publishremove", button);
        button.addActionListener(this);
        button.setActionCommand("target.remove");
        builder.add(button, cc.xy(10, 8));

        //the Cocoon-part
        builder.addSeparator("Cocoon", cc.xywh(2, 11, 5, 1));
        labels.put("dialog.projectsettings.label.contextdir",
            builder.addLabel("", cc.xy(2, 13)));
        contextDir = new JTextField(30);
        builder.add(contextDir, cc.xy(4, 13));
        button = new JButton("...");
        allbuttons.put("dialog.projectsettings.button.choosecontextdir", button);
        button.addActionListener(this);
        button.setActionCommand("choose.contextdir");
        builder.add(button, cc.xy(6, 13));
        labels.put("dialog.projectsettings.label.workdir",
            builder.addLabel("", cc.xy(2, 15)));
        workDir = new JTextField(30);
        builder.add(workDir, cc.xy(4, 15));
        button = new JButton("...");
        allbuttons.put("dialog.projectsettings.button.chooseworkdir", button);
        button.addActionListener(this);
        button.setActionCommand("choose.workdir");
        builder.add(button, cc.xy(6, 15));
        labels.put("dialog.projectsettings.label.defaultoutdir",
            builder.addLabel("", cc.xy(2, 17)));
        destinationDir = new JTextField(30);
        builder.add(destinationDir, cc.xy(4, 17));
        button = new JButton("...");
        allbuttons.put("dialog.projectsettings.button.choosedefaultout", button);
        button.addActionListener(this);
        button.setActionCommand("choose.destinationdir");
        builder.add(button, cc.xy(6, 17));
        labels.put("dialog.projectsettings.label.configfile",
            builder.addLabel("", cc.xy(2, 19)));
        configFile = new JTextField(30);
        builder.add(configFile, cc.xy(4, 19));
        button = new JButton("...");
        allbuttons.put("dialog.projectsettings.button.chooseconfigfile", button);
        button.addActionListener(this);
        button.setActionCommand("choose.configfile");
        builder.add(button, cc.xy(6, 19));

        //the task settings
        builder.addSeparator("Tasks", cc.xywh(8, 11, 3, 1));
        empty = new String[] {
                "                          ",
                "                                       "
            };
        tasks = new JList(empty);
        sp = new JScrollPane(tasks);
        builder.add(sp, cc.xywh(8, 13, 1, 7));
        button = new JButton("Remove");
        allbuttons.put("dialog.projectsettings.button.taskremove", button);
        button.addActionListener(this);
        button.setActionCommand("task.remove");
        builder.add(button, cc.xy(10, 13));
        button = new JButton("Edit");
        allbuttons.put("dialog.projectsettings.button.taskedit", button);
        button.addActionListener(this);
        button.setActionCommand("task.edit");
        builder.add(button, cc.xy(10, 15));
        button = new JButton("Add");
        allbuttons.put("dialog.projectsettings.button.taskadd", button);
        button.addActionListener(this);
        button.setActionCommand("task.add");
        builder.add(button, cc.xy(10, 17));
        getContentPane().add(builder.getPanel(), BorderLayout.CENTER);

        //Buttonbar
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
        getContentPane().add(bbuilder.getPanel(), BorderLayout.SOUTH);
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
        if (e.getActionCommand().equals("ok")) {
            getProject();
            setVisible(false);
        } else if (e.getActionCommand().equals("cancel")) {
            setVisible(false);
        } else if (e.getActionCommand().equals("choose.workdir")) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                workDir.setText(fc.getSelectedFile().getPath());
            }
        } else if (e.getActionCommand().equals("choose.contextdir")) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                contextDir.setText(fc.getSelectedFile().getPath());
            }
        } else if (e.getActionCommand().equals("choose.destinationdir")) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                destinationDir.setText(fc.getSelectedFile().getPath());
            }
        } else if (e.getActionCommand().equals("choose.configfile")) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                configFile.setText(fc.getSelectedFile().getPath());
            }
        } else if (e.getActionCommand().equals("task.add")) {
            TaskEditor te;

            if (!GUICache.getInstance().cached("task.editor")) {
                te = new TaskEditor(this.project);
                te.init();
                GUICache.getInstance().addComponent("task.editor", te);
            }

            te = (TaskEditor) GUICache.getInstance().getComponent("task.editor");
            te.setTask(null);
            te.setVisible(true);
        }

        if (e.getActionCommand().equals("task.edit")) {
            int index = tasks.getSelectedIndex();
            TaskEditor te;

            if (!GUICache.getInstance().cached("task.editor")) {
                te = new TaskEditor(this.project);
                te.init();
                GUICache.getInstance().addComponent("task.editor", te);
            }

            te = (TaskEditor) GUICache.getInstance().getComponent("task.editor");
            te.setTask((Task) project.getTasks().get(index));
            te.setVisible(true);
        }

        if (e.getActionCommand().equals("task.remove")) {
            int index = tasks.getSelectedIndex();
            project.removeTask(index);
        }

        if (e.getActionCommand().equals("target.remove")) {
            int index = targets.getSelectedIndex();
            project.removePublishTarget(index);
        }

        if (e.getActionCommand().equals("target.add")) {
            PublishDialog pd;

            if (!GUICache.getInstance().cached("dialog.publish")) {
                pd = new PublishDialog();
                pd.init();
                GUICache.getInstance().addComponent("dialog.publish", pd);
            }

            pd = (PublishDialog) GUICache.getInstance().getComponent("dialog.publish");
            pd.setProject(this.project);
            pd.setPublishTarget(null);
            pd.setVisible(true);
        }

        if (e.getActionCommand().equals("target.edit")) {
            PublishDialog pd;

            if (!GUICache.getInstance().cached("dialog.publish")) {
                pd = new PublishDialog();
                pd.init();
                GUICache.getInstance().addComponent("dialog.publish", pd);
            }

            pd = (PublishDialog) GUICache.getInstance().getComponent("dialog.publish");
            pd.setProject(this.project);

            int index = targets.getSelectedIndex();
            pd.setPublishTarget(project.getPublishTarget(index));
            pd.setVisible(true);
        }
    }

    /**
     * @return Returns the project.
     *
     */
    public Project getProject() {
        BeanConfiguration config = project.getCocoonBeanConfiguration();
        project.setTitle(title.getText());
        config.setContextDir(contextDir.getText());
        config.setWorkDir(workDir.getText());
        project.setDescription(description.getText());
        config.setConfigFile(configFile.getText());
        config.setDestDir(destinationDir.getText());

        return project;
    }

    /**
     * @param project
     *
     * The project to set.
     *
     */
    public void setProject(Project project) {
        if (project == null) {
            this.project = new Project();
        } else {
            this.project = project;
        }

        BeanConfiguration config = project.getCocoonBeanConfiguration();
        title.setText(this.project.getTitle());

        //layout
        title.setColumns(20);
        contextDir.setText(config.getContextDir());
        contextDir.setColumns(20);
        workDir.setText(config.getWorkDir());
        workDir.setColumns(20);
        description.setText(this.project.getDescription());
        description.setColumns(30);
        description.setRows(6);

        //tasks.setModel(this.project.getTaskListModel());
        targets.setModel(this.project.getTargetsListModel());
        configFile.setText(config.getConfigFile());
        configFile.setColumns(20);
        destinationDir.setText(config.getDestDir());

        //Todo add FTP
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#release()
     *
     */
    public void disposeComponent(
        org.apache.avalon.framework.service.ServiceManager newParam) {
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
     * @see de.miethxml.conf.TranslationListener#langChanged()
     *
     */
    public void langChanged() {
        LocaleImpl lang = LocaleImpl.getInstance();
        this.setTitle(lang.getString("dialog.projectsettings.title"));

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

    public void init() {
    }
}
