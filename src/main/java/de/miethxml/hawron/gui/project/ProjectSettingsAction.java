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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.ProjectComponent;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;

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
public class ProjectSettingsAction extends AbstractAction
    implements LocaleListener, Serviceable, Initializable, ProjectComponent {
    private ServiceManager manager;
    private Project project;

    /**
     *
     *
     *
     */
    public ProjectSettingsAction() {
        super(LocaleImpl.getInstance().getString("menu.project.settings"),
            new ImageIcon("icons/project.gif"));
        putValue(SHORT_DESCRIPTION,
            LocaleImpl.getInstance().getString("menu.project.settings"));
        LocaleImpl.getInstance().addLocaleListener(this);
    }

    /**
     * @param name
     *
     */
    public ProjectSettingsAction(String name) {
        super(name);
    }

    /**
     * @param name
     *
     * @param icon
     *
     */
    public ProjectSettingsAction(
        String name,
        Icon icon) {
        super(name, icon);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
        try {
            ProcessSettingsView ps = (ProcessSettingsView) manager.lookup(
                    "de.miethxml.hawron.gui.project.ProcessSettingsView");

            ps.setProject(project);
            ps.setVisible(true);
        } catch (ServiceException se) {
            se.printStackTrace();
        }
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public void langChanged() {
        putValue(SHORT_DESCRIPTION,
            LocaleImpl.getInstance().getString("menu.project.settings"));
        putValue(NAME,
            LocaleImpl.getInstance().getString("menu.project.settings"));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#init()
     *
     */
    public void initialize() {
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
