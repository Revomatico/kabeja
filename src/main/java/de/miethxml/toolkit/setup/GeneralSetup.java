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
package de.miethxml.toolkit.setup;

import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import de.miethxml.toolkit.component.GuiConfigurable;
import de.miethxml.toolkit.conf.ConfigManager;
import de.miethxml.toolkit.gui.LocaleLabel;


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
public class GeneralSetup implements GuiConfigurable {
    JPanel panel;
    boolean initialized = false;
    private String[] language = new String[] { "english", "deutsch" };
    private JComboBox lang;

    /**
     *
     *
     *
     */
    public GeneralSetup() {
        super();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.GuiConfigurable#getLabel()
     *
     */
    public String getLabel() {
        return "General";
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.GuiConfigurable#getSetupComponent()
     *
     */
    public JComponent getSetupComponent() {
        if (!initialized) {
            init();
        }

        return panel;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.GuiConfigurable#setup()
     *
     */
    public void setup() {
        if (lang != null) {
            if (lang.getSelectedIndex() == 0) {
                ConfigManager.getInstance().setProperty("lang", "en");
            } else if (lang.getSelectedIndex() == 1) {
                ConfigManager.getInstance().setProperty("lang", "de");
            }
        }
    }

    private void init() {
        //TODO this is only for testing replace it later
        panel = new JPanel(new FlowLayout());
        panel.add(new LocaleLabel("setup.general.label.language"));
        lang = new JComboBox(language);
        lang.setEditable(false);
        panel.add(lang);
        initialized = true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.GuiConfigurable#isSetup()
     *
     */
    public boolean isSetup() {
        return false;
    }
}
