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

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import de.miethxml.hawron.net.PublishTarget;

import de.miethxml.toolkit.conf.LocaleImpl;


/**
 * @author simon
 *
 *
 *
 */
public class PasswordDialog {
    public static char[] showDialog(PublishTarget pt) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(LocaleImpl.getInstance().getString("publish.require.password")
                + pt.getUsername() + "@" + pt.getURI()), BorderLayout.CENTER);

        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordField, BorderLayout.SOUTH);
        JOptionPane.showMessageDialog(null, panel, "",
            JOptionPane.QUESTION_MESSAGE);

        char[] password = passwordField.getPassword();
        passwordField = null;
        panel = null;

        return password;
    }
}
