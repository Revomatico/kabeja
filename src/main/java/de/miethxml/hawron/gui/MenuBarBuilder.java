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
package de.miethxml.hawron.gui;

import javax.swing.JMenuBar;

import de.miethxml.hawron.gui.event.ApplicationShutdownAction;
import de.miethxml.hawron.gui.project.ProjectNewAction;
import de.miethxml.hawron.gui.project.ProjectOpenAction;
import de.miethxml.hawron.gui.project.ProjectSaveAction;
import de.miethxml.hawron.gui.project.ProjectSaveAsAction;
import de.miethxml.hawron.gui.project.ProjectSettingsAction;

import de.miethxml.toolkit.gui.help.HelpAction;
import de.miethxml.toolkit.setup.ConfigAction;
import de.miethxml.toolkit.ui.MenuBarManager;

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
 * @deprecated
 */
public class MenuBarBuilder implements Serviceable, Initializable {
    private ServiceManager manager;

    /**
     *
     *
     *
     */
    public MenuBarBuilder() {
        super();
    }

    public JMenuBar getMenuBar() {
        try {
            MenuBarManager mb = (MenuBarManager) manager.lookup(MenuBarManager.ROLE);
            ProjectSaveAction psa = (ProjectSaveAction) manager.lookup(
                    "de.miethxml.hawron.gui.project.ProjectSaveAction");
            psa.setEnabled(false);
            mb.setMenuItemAction("menu.file.save", psa);

            ProjectSaveAsAction saveas = (ProjectSaveAsAction) manager.lookup(
                    "de.miethxml.hawron.gui.project.ProjectSaveAsAction");
            mb.setMenuItemAction("menu.file.save_as", saveas);

            ProjectOpenAction openAction = (ProjectOpenAction) manager.lookup(
                    "de.miethxml.hawron.gui.project.ProjectOpenAction");
            mb.setMenuItemAction("menu.file.open", openAction);

            HelpAction helpAction = (HelpAction) manager.lookup(
                    "de.miethxml.toolkit.gui.help.HelpAction");
            helpAction.setDefaultURL("index.html");
            mb.setMenuItemAction("menu.help.index", helpAction);

            ProjectNewAction newaction = (ProjectNewAction) manager.lookup(
                    "de.miethxml.hawron.gui.project.ProjectNewAction");
            mb.setMenuItemAction("menu.file.new", newaction);

            ProjectSettingsAction settings = (ProjectSettingsAction) manager
                .lookup("de.miethxml.hawron.gui.project.ProjectSettingsAction");
            mb.setMenuItemAction("menu.project.settings", settings);

            ApplicationShutdownAction exit = (ApplicationShutdownAction) manager
                .lookup(
                    "de.miethxml.hawron.gui.event.ApplicationShutdownAction");
            mb.setMenuItemAction("menu.file.quit", exit);

            ConfigAction config = (ConfigAction) manager.lookup(
                    "de.miethxml.hawron.gui.conf.ConfigAction");
            mb.setMenuItemAction("menu.edit.preferences", config);

            AboutAction about = new AboutAction();
            mb.setMenuItemAction("menu.help.about", about);

            return mb.getMenuBar();
        } catch (ServiceException se) {
            se.printStackTrace();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#init()
     *
     */
    public void initialize() {
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
}
