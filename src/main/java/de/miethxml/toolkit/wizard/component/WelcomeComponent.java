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

import java.awt.BorderLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

import de.miethxml.toolkit.wizard.WizardException;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class WelcomeComponent extends AbstractWizardComponent {
    public String getTitle() {
        return resource.getString("wizard.component.welcome.title");
    }

    public JComponent getInstallUIComponent() {
        JPanel panel = new JPanel(new BorderLayout());
        JEditorPane text = new JEditorPane();
        text.setContentType("text/html");

        //text.setWrapStyleWord(true);
        //text.setLineWrap(true);
        text.setEditable(false);
        text.setOpaque(false);
        text.setMargin(new Insets(40, 40, 40, 40));
        text.setText(resource.getString("wizard.component.welcome.text"));
        panel.add(text);

        return panel;
    }

    public void startWizardProcess() throws WizardException {
    }

    public void stopWizardProcess() throws WizardException {
    }

    public boolean isModifiable() {
        return true;
    }
}
