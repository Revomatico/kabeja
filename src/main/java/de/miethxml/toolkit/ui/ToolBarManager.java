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
package de.miethxml.toolkit.ui;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.JComponent;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 */
public interface ToolBarManager {
    public static final String ROLE = ToolBarManager.class.getName();
    public static final int LAST = -3;
    public static final int APPEND = -1;
    public static final int BEFORE_LAST = -2;

    public void addAction(Action action);

    public void addAction(
        Action action,
        int index);

    public void addComponent(Component comp);

    public void addComponent(
        Component comp,
        int index);

    public JComponent getToolBar();
}
