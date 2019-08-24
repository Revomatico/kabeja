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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class FileChooserComponent extends AbstractWizardComponent {
    public static String STORE_KEY = "file.selected";
    private JTextField path;
    private JFileChooser chooser;

    public FileChooserComponent() {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.installer.InstallComponent#getInstallTitle()
     */
    public String getTitle() {
        return resource.getString("wizard.component.filechooser.title");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.installer.InstallComponent#getInstallUIComponent()
     */
    public JComponent getInstallUIComponent() {
        FormLayout layout = new FormLayout("9dlu,pref:grow,2dlu,pref,9dlu",
                "9dlu,pref,3dlu,pref,9dlu");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        //the description
        JEditorPane text = new JEditorPane();
        text.setContentType("text/html");
        text.setText(resource.getString(
                "wizard.component.filechooser.desription"));
        text.setEditable(false);
        text.setOpaque(false);
        builder.add(text, cc.xywh(2, 2, 3, 1));

        //the path-field
        path = new JTextField(20);
        builder.add(path, cc.xy(2, 4));

        //the choose-button
        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }

                    //check the name for .tar.gz or .zip
                    String name = f.getName();

                    if (name.endsWith(".tar.gz") || name.endsWith(".zip")) {
                        return true;
                    }

                    return false;
                }

                public String getDescription() {
                    return "";
                }
            });

        JButton button = new JButton(resource.getString(
                    "wizard.component.filechooser.choosebutton"));
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int value = chooser.showDialog(null,
                            resource.getString(
                                "wizard.component.filechooser.choose"));

                    if (value == JFileChooser.APPROVE_OPTION) {
                        path.setText(chooser.getSelectedFile().getPath());
                    }
                }
            });

        builder.add(button, cc.xy(4, 4));

        return builder.getPanel();
    }

    public void startWizardProcess() {
        File f = new File(path.getText());

        conf.setValue(STORE_KEY, f.getAbsolutePath());
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
        return true;
    }

    public void setup(Hashtable parameters) {
        System.out.println("setup Xhoo");

        if (parameters.containsKey("key")) {
            STORE_KEY = (String) parameters.get("key");
            System.out.println("setSTORE_KEY:" + STORE_KEY);
        }
    }
}
