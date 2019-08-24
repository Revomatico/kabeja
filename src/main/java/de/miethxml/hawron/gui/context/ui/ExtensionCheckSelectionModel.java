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
package de.miethxml.hawron.gui.context.ui;

import javax.swing.JToggleButton;

import de.miethxml.hawron.gui.context.ContextView;
import de.miethxml.hawron.gui.context.ContextViewComponent;

import de.miethxml.toolkit.conf.ConfigManager;


/**
 * @author simon
 *
 *
 *
 */
public class ExtensionCheckSelectionModel
    extends JToggleButton.ToggleButtonModel {
    private ContextViewComponent view;

    public ExtensionCheckSelectionModel(ContextViewComponent view) {
        super();
        this.view = view;
    }

    public boolean isSelected() {
        //TODO move this to ContextView
        return Boolean.valueOf(ConfigManager.getInstance().getProperty("view.context.checkextensions"))
                      .booleanValue();
    }

    public void setSelected(boolean b) {
        view.setCheckSupportedExtensions(b);
        super.setSelected(b);
    }
}
