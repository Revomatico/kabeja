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
package de.miethxml.toolkit.wizard.component;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class FinishComponent extends AbstractWizardComponent {
    private JEditorPane text;
    private JPanel panel;

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.installer.InstallComponent#getInstallTitle()
     */
    public String getTitle() {
        return resource.getString("wizard.component.finish.title");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.installer.InstallComponent#getInstallUIComponent()
     */
    public JComponent getInstallUIComponent() {
        FormLayout layout = new FormLayout("9dlu,fill:pref,9dlu,",
                "15dlu,fill:25dlu:grow,9dlu");
        PanelBuilder panelbuilder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();
        text = new JEditorPane();
        text.setContentType("text/html");
        text.setText("");
        text.setEditable(false);
        text.setOpaque(false);
        panelbuilder.add(text, cc.xy(2, 2));
        panel = panelbuilder.getPanel();

        return panel;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.installer.InstallComponent#startInstallProcess()
     */
    public void startWizardProcess() {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.installer.InstallComponent#stopInstallProcess()
     */
    public void stopWizardProcess() {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.installer.InstallComponent#isConfigurable()
     */
    public boolean isModifiable() {
        text.setText(resource.getString("wizard.component.finish.message"));

        panel.validate();

        return false;
    }
}
