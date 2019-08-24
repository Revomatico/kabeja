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

import javax.swing.JButton;
import javax.swing.JToolBar;

import com.jgoodies.plaf.HeaderStyle;
import com.jgoodies.plaf.Options;

import de.miethxml.hawron.gui.project.ProjectNewAction;
import de.miethxml.hawron.gui.project.ProjectOpenAction;
import de.miethxml.hawron.gui.project.ProjectSaveAction;
import de.miethxml.hawron.gui.project.ProjectSaveAsAction;
import de.miethxml.hawron.gui.project.ProjectSettingsAction;

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
 * @deprecated
 */
public class ToolBarBuilder implements Serviceable, Initializable {
    private ServiceManager manager;

    /**
     *
     *
     *
     */
    public ToolBarBuilder() {
        super();
    }

    public JToolBar getToolBar() {
        try {
            JToolBar toolbar = new JToolBar();
            toolbar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);

            JButton button;
            ProjectNewAction newaction = (ProjectNewAction) manager.lookup(
                    "de.miethxml.hawron.gui.project.ProjectNewAction");
            button = new JButton(newaction);
            button.setText("");
            toolbar.add(button);

            ProjectOpenAction openAction = (ProjectOpenAction) manager.lookup(
                    "de.miethxml.hawron.gui.project.ProjectOpenAction");
            button = new JButton(openAction);
            button.setText("");
            toolbar.add(button);

            ProjectSaveAction psa = (ProjectSaveAction) manager.lookup(
                    "de.miethxml.hawron.gui.project.ProjectSaveAction");
            button = new JButton(psa);
            button.setText("");
            toolbar.add(button);

            ProjectSaveAsAction saveas = (ProjectSaveAsAction) manager.lookup(
                    "de.miethxml.hawron.gui.project.ProjectSaveAsAction");
            button = new JButton(saveas);
            button.setText("");
            toolbar.add(button);

            ProjectSettingsAction settings = (ProjectSettingsAction) manager
                .lookup("de.miethxml.hawron.gui.project.ProjectSettingsAction");
            button = new JButton(settings);
            button.setText("");
            toolbar.add(button);

            return toolbar;
        } catch (ServiceException se) {
            se.printStackTrace();
        }

        return null;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#init()
     *
     */
    public void initialize() {
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
}
