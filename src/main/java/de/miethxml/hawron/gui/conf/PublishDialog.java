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
package de.miethxml.hawron.gui.conf;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.net.PublishTarget;
import de.miethxml.hawron.project.Project;


/**
 *
 * @author Simon Mieth
 *
 *
 *
 *
 *
 *
 *
 */
public class PublishDialog extends JDialog implements ActionListener {
    private JTextField name;
    private JTextField uri;
    private JTextField username;
    private JPasswordField password;
    private JLabel labelName;
    private JLabel labelUri;
    private JLabel labelPassword;
    private JLabel labelUsername;
    private JLabel labelSupport;
    private Project project;
    private Hashtable allbuttons;
    private Hashtable labels;
    private PublishTarget publishTarget;
    private PublishTarget old;
    private boolean editing;
    private boolean passwdChanged = false;

    /**
     *
     * @throws java.awt.HeadlessException
     *
     */
    public PublishDialog() throws HeadlessException {
        super();

        labels = new Hashtable();
    }

    public void init() {
        getContentPane().setLayout(new BorderLayout());

        FormLayout layout = new FormLayout("3dlu,right:pref,3dlu,pref:grow,3dlu",
                "3dlu,p,9dlu,p,2dlu,p,2dlu,p,9dlu,p,9dlu,p,2dlu,p,3dlu");

        layout.setRowGroups(new int[][] {
                { 1 }
            });

        PanelBuilder builder = new PanelBuilder(layout);

        CellConstraints cc = new CellConstraints();

        builder.addSeparator("Main", cc.xywh(2, 2, 3, 1));

        builder.addLabel("Name", cc.xy(2, 4));

        name = new JTextField(20);

        builder.add(name, cc.xy(4, 4));

        builder.addLabel("(supported file:// ftp:// smb:// sftp:// webdav:// )",
            cc.xy(4, 6));

        builder.addLabel("URI", cc.xy(2, 8));

        uri = new JTextField(20);

        builder.add(uri, cc.xy(4, 8));

        builder.addSeparator("Authentication", cc.xywh(2, 10, 3, 1));

        builder.addLabel("Username", cc.xy(2, 12));

        username = new JTextField(20);

        builder.add(username, cc.xy(4, 12));

        builder.addLabel("Password", cc.xy(2, 14));

        password = new JPasswordField(20);

        builder.add(password, cc.xy(4, 14));

        password.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                    passwdChanged = true;
                }

                public void keyTyped(KeyEvent e) {
                    passwdChanged = true;
                }

                public void keyReleased(KeyEvent e) {
                    passwdChanged = true;
                }
            });

        getContentPane().add(builder.getPanel(), BorderLayout.CENTER);

        JButton[] buttons = new JButton[2];

        buttons[0] = new JButton("cancel");

        buttons[0].setActionCommand("cancel");

        buttons[0].addActionListener(this);

        //buttons.add(button);
        buttons[1] = new JButton("ok");

        buttons[1].setActionCommand("ok");

        buttons[1].addActionListener(this);

        //buttons.add(button);
        JButton button = new JButton("help");

        button.setActionCommand("help");

        button.addActionListener(this);

        ButtonBarBuilder bbuilder = new ButtonBarBuilder();

        bbuilder.addGridded(button);

        bbuilder.addRelatedGap();

        bbuilder.addGlue();

        bbuilder.addGriddedButtons(buttons);

        this.getContentPane().add(bbuilder.getPanel(), BorderLayout.SOUTH);

        pack();
    }

    /*
     *
     * (non-Javadoc)
     *
     *
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("ok")) {
            if (editing) {
                project.replacePublishTarget(old, getPublishTarget());
            } else {
                project.addPublishTarget(getPublishTarget());
            }

            password.setText("");

            this.dispose();
        } else if (command.equals("cancel")) {
            password.setText("");

            this.dispose();
        }
    }

    /**
     *
     * @return Returns the project.
     *
     */
    public Project getProject() {
        return project;
    }

    /**
     *
     * @param project
     *
     * The project to set.
     *
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     *
     * @return Returns the publishTarget.
     *
     */
    private PublishTarget getPublishTarget() {
        publishTarget.setTitle(name.getText());

        publishTarget.setUsername(username.getText());

        String url = uri.getText();

        if (url.indexOf("://") > -1) {
            //with protocol
            publishTarget.setProtocol(url.substring(0, url.indexOf("://") + 3));

            System.out.println("got Protocl: " + publishTarget.getProtocol());

            publishTarget.setURI(url.substring(url.indexOf("://") + 3));

            System.out.println("got URI: " + publishTarget.getURI());
        } else {
            publishTarget.setProtocol("");

            publishTarget.setURI(url);
        }

        if (passwdChanged) {
            publishTarget.setPassword(new String(password.getPassword()));
        }

        if (!editing) {
            publishTarget.setID("" + project.getPublishTargetCount());
        }

        return publishTarget;
    }

    /**
     *
     * @param publishTarget
     *
     * The publishTarget to set.
     *
     */
    public void setPublishTarget(PublishTarget pt) {
        passwdChanged = false;

        if (pt == null) {
            this.publishTarget = new PublishTarget();

            editing = false;
        } else {
            this.publishTarget = (PublishTarget) pt.clone();

            this.old = pt;

            editing = true;
        }

        name.setText(publishTarget.getTitle());

        uri.setText(publishTarget.getProtocol() + publishTarget.getURI());

        username.setText(publishTarget.getUsername());
    }
}
