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

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.ProjectConfigListener;

import de.miethxml.toolkit.cache.Cacheable;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;
import de.miethxml.toolkit.gui.LocaleBorderPanel;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public class ProjectViewImpl implements Cacheable, ActionListener,
    ListSelectionListener, Serviceable, LocaleListener, ProjectConfigListener,
    ProjectView, Initializable, Disposable, LogEnabled {
    private JTabbedPane tp;
    private Project project;
    private ServiceManager manager;
    private LocaleBorderPanel infopanel;
    private CardLayout cardLayout;
    private JPanel parent;
    private ArrayList components = new ArrayList();
    private boolean initialized = false;
    private JPanel panel;
    private CellConstraints tapCC;
    private Logger log;

    /**
     *
     *
     *
     */
    public ProjectViewImpl() {
    }

    public ProjectViewImpl(Project project) {
    }

    public void initialize() {
        if (!initialized) {
            panel = new JPanel();

            try {
                this.project = (Project) manager.lookup(
                        "de.miethxml.hawron.project.Project");
            } catch (ServiceException se) {
                se.printStackTrace();
            }

            project.addProjectConfigListener(this);

            LocaleImpl trans = LocaleImpl.getInstance();
            trans.addLocaleListener(this);
            panel.removeAll();

            FormLayout layout = new FormLayout("3dlu,left:pref,3dlu,pref:grow,3dlu",
                    "3dlu,fill:pref:grow,3dlu");
            CellConstraints cc = new CellConstraints();
            panel.setLayout(layout);

            //the place for the dockComponents
            infopanel = new LocaleBorderPanel("view.project.border.info");

            FormLayout panellayout = new FormLayout("3dlu,80dlu,3dlu",
                    "3dlu,fill:pref:grow,3dlu");
            infopanel.setLayout(panellayout);
            cardLayout = new CardLayout();
            parent = new JPanel();
            parent.setLayout(cardLayout);

            CellConstraints ccp = new CellConstraints();
            infopanel.add(parent, ccp.xy(2, 2));
            panel.add(infopanel, cc.xy(2, 2));

            tapCC = new CellConstraints();
            tp = new JTabbedPane();

            tp.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        int index = tp.getSelectedIndex();
                        cardLayout.show(parent, "" + (index + 1));

                        ProjectViewComponent comp = (ProjectViewComponent) components
                            .get(index);
                        comp.setEnabled(true);
                    }
                });
            panel.add(tp, cc.xy(4, 2));

            //tp.setPreferredSize(new Dimension(730,550));
            initialized = true;
        }
    }

    /**
     * @return Returns the project.
     *
     */
    public Project getProject() {
        return this.project;
    }

    /**
     * @param project
     *
     * The project to set.
     *
     */
    public void setProject(Project project) {
    }

    public void recycle() {
    }

    public void destroy() {
        tp = null;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
    }

    public void valueChanged(ListSelectionEvent e) {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#init()
     *
     */
    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#setComponentManager(de.miethxml.toolkit.component.ComponentManager)
     *
     */
    public void service(ServiceManager manager) {
        this.manager = manager;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.conf.ProjectConfigListener#configChanged(de.miethxml.conf.Project)
     *
     */
    public void configChanged(Project project) {
        setProject(project);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectView#activateProjectGUIComponent(int)
     *
     */
    public void activateProjectViewComponent(int index) {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectView#activateProjectGUIComponent(de.miethxml.gui.project.ProjectViewComponent)
     *
     */
    public void activateProjectViewComponent(ProjectViewComponent component) {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectView#addProjectGUIComponent(de.miethxml.gui.project.ProjectViewComponent)
     *
     */
    public void addProjectViewComponent(ProjectViewComponent component) {
        components.add(component);

        JComponent dockable = component.getDockComponent();

        if (dockable == null) {
            //create a empty Panel
            //TODO build a default Panel
            dockable = new JPanel();
        }

        parent.add(dockable, "" + components.size());

        JComponent view = component.getViewComponent();

        if (tp.getMinimumSize().getWidth() < view.getPreferredSize().getWidth()) {
            if (log.isDebugEnabled()) {
                log.debug("Change the PreferredSize to:"
                    + component.getViewComponent().getPreferredSize());
            }

            tp.setPreferredSize(view.getPreferredSize());

            //view.setPreferredSize(view.getPreferredSize());
        }

        tp.add(view, null);
        tp.setTitleAt(tp.getTabCount() - 1, component.getLabel("en"));
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectView#getProjectGUIComponents()
     *
     */
    public List getProjectViewComponents() {
        return null;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectView#getActiveProjectGUIComponent()
     *
     */
    public ProjectViewComponent getActiveProjectViewComponent() {
        return null;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectView#activateProjectGUIComponent(java.lang.String)
     *
     */
    public void activateProjectViewComponent(String key) {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectView#removeProjectGUIComponent(de.miethxml.gui.project.ProjectViewComponent)
     *
     */
    public void removeProjectViewComponent(ProjectViewComponent component) {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectView#getProjectView()
     *
     */
    public JComponent getView() {
        if (!initialized) {
            initialize();
        }

        return panel;
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
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     *
     */
    public void dispose() {
        destroy();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.conf.LocaleListener#langChanged()
     *
     */
    public void langChanged() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
     */
    public void enableLogging(Logger logger) {
        this.log = logger;
    }
}
