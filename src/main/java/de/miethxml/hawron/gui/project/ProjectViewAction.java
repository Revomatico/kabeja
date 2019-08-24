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
import javax.swing.KeyStroke;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 */
public class ProjectViewAction extends AbstractAction {
    public static final String ACTION_PROPERTY = "ACTIONPERFORMED";
    private int componentIndex;
    private ProjectView projectView;

    /**
     * @param name
     */
    public ProjectViewAction(
        String name,
        int componentIndex,
        ProjectView projectView) {
        super(name);
        this.componentIndex = componentIndex;
        setProjectView(projectView);
    }

    /**
     * @param name
     * @param icon
     */
    public ProjectViewAction(
        String name,
        Icon icon,
        int componentIndex,
        ProjectView projectView) {
        super(name, icon);
        this.componentIndex = componentIndex;
        setProjectView(projectView);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        projectView.activateProjectViewComponent(componentIndex);

        //this is more a workaround
        firePropertyChange(ACTION_PROPERTY, "old", "");
    }

    public void setProjectView(ProjectView projectView) {
        this.projectView = projectView;
    }

    public void setKeyStroke(String keystroke) {
        putValue(AbstractAction.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(keystroke));
    }
}
