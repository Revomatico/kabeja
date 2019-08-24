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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.ProjectConfigListener;

import de.miethxml.toolkit.cache.Cacheable;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;
import de.miethxml.toolkit.ui.MenuBarManager;
import de.miethxml.toolkit.ui.PanelFactory;
import de.miethxml.toolkit.ui.SelectorComponent;
import de.miethxml.toolkit.ui.ToolBarManager;

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
public class SelectorProjectView implements Cacheable, Serviceable,
    LocaleListener, ProjectConfigListener, ProjectView, Initializable,
    Disposable, LogEnabled {
    private JPanel mainPanel;
    private CardLayout mainContainer;
    private Project project;
    private ServiceManager manager;
    private JPanel infoPanel;
    private CardLayout dockletContainer;
    private JPanel dockletPanel;
    private ArrayList components = new ArrayList();
    private boolean initialized = false;
    private JPanel panel;
    private Logger log;
    private MenuBarManager menubar;
    private ToolBarManager toolbar;
    private SelectorComponent selector;
    private ProjectViewComponent activeComponent;

    /**
     *
     *
     *
     */
    public SelectorProjectView() {
    }

    public SelectorProjectView(Project project) {
    }

    public void initialize() {
        if (!initialized) {
            panel = new JPanel();
            panel.setBorder(BorderFactory.createEmptyBorder());

            try {
                this.menubar = (MenuBarManager) manager.lookup(MenuBarManager.ROLE);
                this.toolbar = (ToolBarManager) manager.lookup(ToolBarManager.ROLE);
            } catch (ServiceException se) {
                se.printStackTrace();
            }

            project.addProjectConfigListener(this);

            LocaleImpl trans = LocaleImpl.getInstance();
            trans.addLocaleListener(this);

            FormLayout layout = new FormLayout("3dlu,pref:grow,3dlu",
                    "3dlu,fill:pref:grow,3dlu");
            CellConstraints cc = new CellConstraints();
            panel.setLayout(layout);

            JSplitPane sp = PanelFactory.createOneTouchSplitPane();

            //the place for the dockComponents
            infoPanel = new JPanel();

            FormLayout panellayout = new FormLayout("3dlu,80dlu:grow,3dlu",
                    "3dlu,fill:pref:grow,3dlu");

            infoPanel.setLayout(panellayout);
            dockletContainer = new CardLayout();
            dockletPanel = new JPanel();
            dockletPanel.setLayout(dockletContainer);

            CellConstraints ccp = new CellConstraints();
            infoPanel.add(dockletPanel, ccp.xy(2, 2));

            //allow resize 
            //infoPanel.setMinimumSize(new Dimension(0,0));
            //wrap infopanel with decorated panel
            //  sp.setLeftComponent(infoPanel);
            sp.setLeftComponent(PanelFactory.createTitledPanel(infoPanel,
                    "Info", null));

            mainContainer = new CardLayout();
            mainPanel = new JPanel(mainContainer);

            sp.setRightComponent(mainPanel);

            panel.add(sp, cc.xy(2, 2));

            //the menu entry
            menubar.addMenu("menu.view", "menu.view.showview");

            //the toolbar
            selector = new SelectorComponent();
            toolbar.addComponent(selector.getView(), ToolBarManager.LAST);

            initialized = true;

            if (components.size() > 0) {
                Iterator i = components.iterator();

                while (i.hasNext()) {
                    addProjectViewComponent((ProjectViewComponent) i.next());
                }
            }
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
        this.project = project;
    }

    public void recycle() {
    }

    public void destroy() {
        mainPanel = null;
        dockletPanel = null;

        //TODO release all components
    }

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

    public void activateProjectViewComponent(int index) {
        dockletContainer.show(dockletPanel, "" + index);
        mainContainer.show(mainPanel, "" + index);

        ProjectViewComponent comp = (ProjectViewComponent) components.get(index);
        comp.setEnabled(true);
        activeComponent = comp;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectView#activateProjectGUIComponent(de.miethxml.gui.project.ProjectViewComponent)
     *
     */
    public void activateProjectViewComponent(ProjectViewComponent component) {
        int index = components.indexOf(component);
        activateProjectViewComponent(index);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectView#addProjectGUIComponent(de.miethxml.gui.project.ProjectViewComponent)
     *
     */
    public void addProjectViewComponent(ProjectViewComponent component) {
        int index = components.size();
        components.add(component);

        JComponent dockable = component.getDockComponent();

        if (dockable == null) {
            //create a empty Panel
            //TODO build a default Panel with name
            dockable = new JPanel();
        }

        dockletPanel.add(dockable, "" + index);

        JComponent view = component.getViewComponent();

        if (mainPanel.getMinimumSize().getWidth() < view.getPreferredSize()
                                                            .getWidth()) {
            if (log.isDebugEnabled()) {
                log.debug("Change the PreferredSize to:"
                    + component.getViewComponent().getPreferredSize());
            }

            mainPanel.setPreferredSize(view.getPreferredSize());

            //view.setPreferredSize(view.getPreferredSize());
        }

        mainPanel.add(view, "" + index);

        Icon icon = component.getIcon();
        ProjectViewAction action = null;

        if (icon != null) {
            action = new ProjectViewAction(component.getLabel("en"), icon,
                    index, this);
        } else {
            action = new ProjectViewAction(component.getLabel("en"), index, this);
        }

        action.setKeyStroke("control " + components.size());

        //add new action to the menu
        menubar.addMenuLabel("menu.view.showview." + index,
            component.getLabel("en"));
        menubar.addMenuItem("menu.view.showview",
            "menu.view.showview." + index, new JMenuItem(action));

        //add to selector component
        selector.addAction(action);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectView#getProjectGUIComponents()
     *
     */
    public List getProjectViewComponents() {
        return components;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectView#getActiveProjectGUIComponent()
     *
     */
    public ProjectViewComponent getActiveProjectViewComponent() {
        return activeComponent;
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
