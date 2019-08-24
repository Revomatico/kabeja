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
package de.miethxml.hawron.gui.process;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.gui.project.ProjectViewComponent;
import de.miethxml.hawron.gui.project.TabbedProjectView;
import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.ProjectConfigListener;
import de.miethxml.hawron.project.Task;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;
import de.miethxml.toolkit.gui.LocaleButton;
import de.miethxml.toolkit.gui.LocaleSeparator;
import de.miethxml.toolkit.ui.GradientLabel;
import de.miethxml.toolkit.ui.SmallShadowBorder;

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
public class SimpleProcessView implements ActionListener, ListSelectionListener,
    LocaleListener, Serviceable, Initializable, ProjectViewComponent,
    ProjectConfigListener {
    private Project project;
    private JTable tasks;
    private JPanel p;
    private Hashtable allbuttons;
    private ServiceManager manager;
    private JPanel panel;
    private JPanel dockComponent;

    //internal views,dialogs
    private StatusPanel status;
    private TaskDialog taskdialog;
    private boolean initialized = false;

    //gui elements
    private JTextArea projectDescription;
    private JTextArea description;
    private JTextArea tip;
    private JLabel projectTitle;
    private JButton processButton;

    /**
     *
     *
     *
     */
    public SimpleProcessView() {
        super();
        allbuttons = new Hashtable();
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
     *
     * The project to set.
     *
     */
    public void setProject(Project project) {
        if (initialized) {
            taskdialog.setProject(project);
            status.setProject(project);
            tasks.setModel(project.getTaskModel());

            //invoke the setting of the title and description
            configChanged(project);

            //the first colum should be fixed
            TableColumnModel tcm = tasks.getColumnModel();
            TableColumn column = tcm.getColumn(0);
            column.setPreferredWidth(20);
            column.setMaxWidth(20);
            column.setMinWidth(20);
        }

        this.project = project;
        project.addProjectConfigListener(this);
    }

    public void setProjectView(TabbedProjectView pv) {
    }

    public void initialize() {
        //prebuild gui
        init();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("task.add")) {
            taskdialog.newTask();
            taskdialog.setVisible(true);
        }

        if (e.getActionCommand().equals("task.edit")) {
            int index = tasks.getSelectedRow();
            taskdialog.setTask((Task) project.getTasks().get(index));
            taskdialog.setVisible(true);
        }

        if (e.getActionCommand().equals("task.remove")) {
            int index = tasks.getSelectedRow();
            project.removeTask(index);
        }

        if (e.getActionCommand().equals("task.process")) {
        	processButton.setEnabled(false);
            Thread t = new Thread(new Runnable() {
                        public void run() {
                            status.setVisible(true);
                            project.processTasks();
                            processButton.setEnabled(true);
                        }
                    });
            t.start();
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
        int index = tasks.getSelectedRow();

        if (index > -1) {
            Task t = (Task) project.getTasks().get(index);
            description.setText(t.getDescription());
        }
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
    }

    /*
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
     * @see de.miethxml.gui.project.ProjectViewComponent#getDockComponents()
     *
     */
    public JComponent getDockComponent() {
        if (dockComponent == null) {
            return buildDockComponent();
        }

        return dockComponent;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectViewComponent#getGUIComponent()
     *
     */
    public JComponent getViewComponent() {
        return panel;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectViewComponent#getKey()
     *
     */
    public String getKey() {
        return "task.panel";
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectViewComponent#getLabel(java.lang.String)
     *
     */
    public String getLabel(String localeKey) {
        return LocaleImpl.getInstance().getString("view.project.tab.task");
    }

    public Icon getIcon() {
        return null;
    }

    private JPanel buildDockComponent() {
        dockComponent = new JPanel();

        FormLayout panellayout = new FormLayout("3dlu,74dlu:grow,3dlu",
                "3dlu,top:pref,9dlu,p,2dlu,p,9dlu,p,2dlu,fill:9dlu:grow,p,3dlu");
        dockComponent.setLayout(panellayout);

        CellConstraints ccp = new CellConstraints();
        JLabel iconlabel = new JLabel(new ImageIcon("icons/task01.png"));
        dockComponent.add(iconlabel, ccp.xy(2, 2));

        dockComponent.add(new LocaleSeparator(
                "view.process.separator.projecttitle"), ccp.xy(2, 4));
        projectTitle = new JLabel("");
        dockComponent.add(projectTitle, ccp.xy(2, 6));
        dockComponent.add(new LocaleSeparator(
                "view.process.separator.projectdescription"), ccp.xy(2, 8));
        projectDescription = new JTextArea(15, 10);
        projectDescription.setWrapStyleWord(true);
        projectDescription.setLineWrap(true);
        projectDescription.setEditable(false);
        projectDescription.setOpaque(false);
        projectDescription.setText("");

        //tip.setText(LocaleImpl.getInstance().getDisplayName("view.project.infopanel.tasks"));
        dockComponent.add(projectDescription, ccp.xy(2, 10));

        //dockComponent.add(new JLabel(new
        // ImageIcon("icons/cocoon.png")),ccp.xy(2,11));
        return dockComponent;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectViewComponent#init()
     *
     */
    public void init() {
        if (!initialized) {
            //p = new JPanel(new BorderLayout());
            panel = new JPanel();
            panel.setBorder(new SmallShadowBorder());
            taskdialog = new TaskDialogImpl();
            taskdialog.initialize();

            FormLayout layout = new FormLayout("fill:pref:grow",
                    "top:80dlu:grow,6dlu,bottom:60dlu:grow");
            panel.setLayout(layout);

            CellConstraints cc = new CellConstraints();

            //the taskview
            panel.add(buildView(), cc.xy(1, 1));

            status = new ProjectProcessMonitorPanel();
            status.initialize();
            panel.add(status.getView(), cc.xy(1, 3));
            status.getView().setPreferredSize(status.getView().getPreferredSize());
            status.setVisible(false);

            LocaleImpl.getInstance().addLocaleListener(this);

            if (project != null) {
                taskdialog.setProject(project);
                status.setProject(project);
            }

            panel.setPreferredSize(panel.getPreferredSize());

            initialized = true;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectViewComponent#setEnabled(boolean)
     *
     */
    public void setEnabled(boolean state) {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.project.ProjectConfigListener#configChanged(de.miethxml.project.Project)
     */
    public void configChanged(Project project) {
        if (initialized && (dockComponent != null)) {
            projectTitle.setText(project.getTitle());
            projectDescription.setText(project.getDescription());
        }
    }

    private JComponent buildView() {
        FormLayout layout = new FormLayout("9dlu,fill:80dlu:grow(0.5),3dlu,pref,9dlu,fill:65dlu:grow,9dlu",
                "p,9dlu,p,2dlu,p,2dlu,p,fill:2dlu:grow(0.2),p,9dlu");

        CellConstraints cc = new CellConstraints();
        PanelBuilder builder = new PanelBuilder(layout);

        builder.add(new GradientLabel("Offline Generation"), cc.xywh(1, 1, 7, 1));

        tasks = new JTable();
        tasks.setSelectionBackground(Color.LIGHT_GRAY);
        tasks.setSelectionForeground(Color.BLACK);

        tasks.getSelectionModel().addListSelectionListener(this);
        tasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane sp = new JScrollPane(tasks);
        sp.getViewport().setBackground(Color.white);
        sp.getViewport().setOpaque(true);
        builder.add(sp, cc.xywh(2, 3, 1, 7));

        LocaleButton button = new LocaleButton(
                "view.mainproject.button.taskadd");
        button.addActionListener(this);
        button.setActionCommand("task.add");
        builder.add(button, cc.xy(4, 3));

        button = new LocaleButton("view.mainproject.button.taskedit");
        button.addActionListener(this);
        button.setActionCommand("task.edit");
        builder.add(button, cc.xy(4, 5));

        button = new LocaleButton("view.mainproject.button.taskremove");
        button.addActionListener(this);
        button.setActionCommand("task.remove");
        builder.add(button, cc.xy(4, 7));

        button = new LocaleButton("view.mainproject.button.taskprocess");
        allbuttons.put("view.mainproject.button.taskprocess", button);
        button.addActionListener(this);
        button.setActionCommand("task.process");

        
         processButton=button;
        
        //button.setBackground(new Color(160,160,200));
        builder.add(button, cc.xy(4, 9));

        builder.add(new LocaleSeparator(
                "view.process.separator.projectdescription"), cc.xy(6, 3));

        description = new JTextArea(4, 4);
        description.setWrapStyleWord(true);
        description.setLineWrap(true);
        description.setEditable(false);
        description.setOpaque(false);
        description.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        description.setText("");

        //description.setMargin(new Insets(2,2,2,2));
        builder.add(description, cc.xywh(6, 5, 1, 5));

        return builder.getPanel();
    }
}
