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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.ProjectComponent;
import de.miethxml.hawron.project.ProjectConfigListener;

import de.miethxml.toolkit.component.AbstractServiceable;
import de.miethxml.toolkit.component.GuiConfigurable;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.plugins.PluginManager;
import de.miethxml.toolkit.plugins.PluginReceiver;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;


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
public class ProjectViewComponentManagerImpl extends AbstractServiceable
    implements Initializable, ProjectViewComponentManager, PluginReceiver,
        ProjectComponent, LogEnabled, Configurable, ProjectConfigListener,
        Disposable, GuiConfigurable {
    private Collection interfaces = new HashSet();
    private ProjectView projectView;
    private ArrayList components = new ArrayList();
    private ArrayList pluginComponents = new ArrayList();
    private Project project;
    private Logger log;

    /**
     *
     *
     *
     */
    public ProjectViewComponentManagerImpl() {
        super();
        interfaces.add(ProjectViewComponent.class.getName());
    }

    public void initialize() {
        //in order to get plugins, we have to registrate us to the
        // PluginManager
        try {
            projectView = (ProjectView) manager.lookup(ProjectView.ROLE);

            //project.addProjectConfigListener(this);
            Iterator i = components.iterator();

            while (i.hasNext()) {
                try {
                    ProjectViewComponent comp = (ProjectViewComponent) i.next();

                    if (log.isDebugEnabled()) {
                        log.debug("add " + comp.getClass().getName()
                            + " to ProjectView");
                    }

                   

                    //ContainerUtil.contextualize();
                  
                    ContainerUtil.initialize(comp);
                    projectView.addProjectViewComponent(comp);
                } catch (Exception e) {
                    log.error("Initialize ProjectViewComponets:"
                        + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (ServiceException e) {
            log.error("Initialize ProjectViewComponents" + e.getMessage());
        }

        try {
            PluginManager pm = (PluginManager) manager.lookup(PluginManager.ROLE);
            pm.addPluginReceiver(this);
        } catch (ServiceException se) {
            log.error("Registration PluginReceiver", se);
        }
    }

    public List getProjectViewComponents() {
        return components;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.plugins.PluginListener#addPlugin(java.lang.Object)
     *
     */
    public void addPlugin(Object obj) {
        ProjectViewComponent comp = (ProjectViewComponent) obj;

        if (log.isDebugEnabled()) {
            log.debug("add  ProjectViewComponentPlugin:"
                + obj.getClass().getName());
        }

        if (project != null) {
            comp.setProject(project);
        }

        //the plugins are handled by the 
        //application-Container (Life-cycle), so we store it
        //separate
        pluginComponents.add(comp);

        if (projectView != null) {
            projectView.addProjectViewComponent(comp);
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.plugins.PluginListener#getInterfaces()
     *
     */
    public Collection getInterfaces() {
        return interfaces;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.plugins.PluginListener#removePlugin(java.lang.Object)
     *
     */
    public void removePlugin(Object obj) {
        ProjectViewComponent comp = (ProjectViewComponent) obj;
        components.remove(comp);
        projectView.removeProjectViewComponent(comp);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectViewComponentManager#getProjectView()
     *
     */
    public ProjectView getProjectView() {
        return projectView;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectViewComponentManager#setProjectView(de.miethxml.gui.project.ProjectView)
     *
     */
    public void setProjectView(ProjectView view) {
        this.projectView = view;

        //add the components
        Iterator i = components.iterator();

        while (i.hasNext()) {
            ProjectViewComponent comp = (ProjectViewComponent) i.next();
            view.addProjectViewComponent(comp);
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectViewComponentManager#setProject(de.miethxml.project.Project)
     *
     */
    public void setProject(Project project) {
        this.project = project;
        project.addProjectConfigListener(this);
        updateProject();
    }

    private void updateProject() {
        Iterator i = components.iterator();

        while (i.hasNext()) {
            ProjectViewComponent comp = (ProjectViewComponent) i.next();
            comp.setProject(project);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.project.ProjectConfigListener#configChanged(de.miethxml.project.Project)
     */
    public void configChanged(Project project) {
        updateProject();
    }

    public void configure(Configuration conf) throws ConfigurationException {
        Configuration c = conf.getChild("project-view");
        Configuration[] confs = c.getChildren();

        for (int i = 0; i < confs.length; i++) {
            try {
                ProjectViewComponent comp = (ProjectViewComponent) Class.forName(confs[i]
                        .getAttribute("class")).newInstance();
                components.add(comp);
                
                //the life-cycle
                ContainerUtil.enableLogging(comp, this.log);
                comp.setProject(this.project);
                ContainerUtil.service(comp, this.manager);
                ContainerUtil.configure(comp, confs[i]);
                
                
            } catch (Exception e) {
                log.error("Creating ProjectViewComponent:"
                    + confs[i].getAttribute("class") + e, e);
                e.printStackTrace();
            }
        }
    }

    public void enableLogging(Logger arg0) {
        this.log = arg0;
    }

    public void dispose() {
        //dispose the components
        Iterator i = components.iterator();

        while (i.hasNext()) {
            ProjectViewComponent comp = (ProjectViewComponent) i.next();
            ContainerUtil.dispose(comp);
        }
    }

    public String getLabel() {
        return LocaleImpl.getInstance().getString("view.setup.label");
    }

    public JComponent getSetupComponent() {
        JTabbedPane tp = new JTabbedPane(JTabbedPane.BOTTOM);
        Iterator i = components.iterator();

        while (i.hasNext()) {
            ProjectViewComponent comp = (ProjectViewComponent) i.next();

            if (comp instanceof GuiConfigurable) {
                tp.addTab(comp.getLabel("en"),
                    ((GuiConfigurable) comp).getSetupComponent());
            }
        }

        return tp;
    }

    public boolean isSetup() {
        boolean b = true;
        Iterator i = components.iterator();

        while (i.hasNext()) {
            ProjectViewComponent comp = (ProjectViewComponent) i.next();

            if (comp instanceof de.miethxml.toolkit.component.Configurable) {
                if (!((de.miethxml.toolkit.component.Configurable) comp)
                        .isSetup()) {
                    b = false;
                }
            }
        }

        return b;
    }

    public void setup() {
        Iterator i = components.iterator();

        while (i.hasNext()) {
            ProjectViewComponent comp = (ProjectViewComponent) i.next();

            if (comp instanceof de.miethxml.toolkit.component.Configurable) {
                ((de.miethxml.toolkit.component.Configurable) comp).setup();
            }
        }
    }
}
