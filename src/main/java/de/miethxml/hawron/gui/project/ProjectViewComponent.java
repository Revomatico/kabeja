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

import javax.swing.Icon;
import javax.swing.JComponent;

import de.miethxml.hawron.project.ProjectComponent;


/**
 *
 * This interface describes a ViewComponent like the ProcessView or the
 * ContextView. <br/>
 *
 * <b>Initialization: </b> <br/>
 *
 * <ul>
 *
 * <li>setProject(Project project)</li>
 *
 *
 *
 * <li>getViewComponent()</li>
 *
 * <li>getDockComponent()</li>
 *
 * </ul>
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 */
public interface ProjectViewComponent extends ProjectComponent {
    /**
     *
     * Gives the title/label for the current language like "en" for english or
     * "de" for german.
     *
     */
    public String getLabel(String lang);

    /**
     * The accociated icon of this component.
     *
     * @return the icon or null
     */
    public Icon getIcon();

    /**
     *
     * Gives the Component, which will be added to the ProjectView.This should
     * be the main Component.
     *
     *
     *
     */
    public JComponent getViewComponent();

    /**
     *
     * The Defaultimplementation of the ProjectView use a JTabbedPane and each
     * tab
     *
     * is a ProjectViewComponent. If the user activate a tab setEnabled(true)
     * will be called at this
     *
     * ProjectViewComponent. If you want to react, do it here.
     *
     */
    public void setEnabled(boolean state);

    public String getKey();

    /**
     *
     * This JPanel will be added to the left InfoPanel and
     *
     * will be activated with this ProjectViewComponent. If you dont need this
     *
     * feature, simple return null;
     *
     *
     *
     */
    public JComponent getDockComponent();
}
