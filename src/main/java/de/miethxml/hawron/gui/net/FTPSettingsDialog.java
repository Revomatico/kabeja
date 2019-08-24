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
package de.miethxml.hawron.gui.net;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import de.miethxml.hawron.io.FTPsite;
import de.miethxml.hawron.project.Project;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 *
 *
 * @deprecated
 */
public class FTPSettingsDialog extends JDialog {
    Project project;
    FTPsite ftp;
    FTPsite old;
    JTextField title;
    JTextField username;
    protected JPasswordField password;
    JTextField url;
    boolean newftp;

    /**
     * @throws java.awt.HeadlessException
     *
     */
    public FTPSettingsDialog() throws HeadlessException {
        super();
    }

    /**
     * @param owner
     *
     * @throws java.awt.HeadlessException
     *
     */
    public FTPSettingsDialog(Dialog owner) throws HeadlessException {
        super(owner);
    }

    /**
     * @param owner
     *
     * @param modal
     *
     * @throws java.awt.HeadlessException
     *
     */
    public FTPSettingsDialog(
        Dialog owner,
        boolean modal) throws HeadlessException {
        super(owner, modal);
    }

    /**
     * @param owner
     *
     * @throws java.awt.HeadlessException
     *
     */
    public FTPSettingsDialog(Frame owner) throws HeadlessException {
        super(owner);
    }

    /**
     * @param owner
     *
     * @param modal
     *
     * @throws java.awt.HeadlessException
     *
     */
    public FTPSettingsDialog(
        Frame owner,
        boolean modal) throws HeadlessException {
        super(owner, modal);
    }

    /**
     * @param owner
     *
     * @param title
     *
     * @throws java.awt.HeadlessException
     *
     */
    public FTPSettingsDialog(
        Dialog owner,
        String title) throws HeadlessException {
        super(owner, title);
    }

    /**
     * @param owner
     *
     * @param title
     *
     * @param modal
     *
     * @throws java.awt.HeadlessException
     *
     */
    public FTPSettingsDialog(
        Dialog owner,
        String title,
        boolean modal) throws HeadlessException {
        super(owner, title, modal);
    }

    /**
     * @param owner
     *
     * @param title
     *
     * @throws java.awt.HeadlessException
     *
     */
    public FTPSettingsDialog(
        Frame owner,
        String title) throws HeadlessException {
        super(owner, title);
    }

    /**
     * @param owner
     *
     * @param title
     *
     * @param modal
     *
     * @throws java.awt.HeadlessException
     *
     */
    public FTPSettingsDialog(
        Frame owner,
        String title,
        boolean modal) throws HeadlessException {
        super(owner, title, modal);
    }

    /**
     * @param owner
     *
     * @param title
     *
     * @param modal
     *
     * @param gc
     *
     * @throws java.awt.HeadlessException
     *
     */
    public FTPSettingsDialog(
        Dialog owner,
        String title,
        boolean modal,
        GraphicsConfiguration gc) throws HeadlessException {
        super(owner, title, modal, gc);
    }

    /**
     * @param owner
     *
     * @param title
     *
     * @param modal
     *
     * @param gc
     *
     */
    public FTPSettingsDialog(
        Frame owner,
        String title,
        boolean modal,
        GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
    }

    public void init() {
        getContentPane().setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(4, 2));
        JLabel label = new JLabel("Title");
        panel.add(label);
        title = new JTextField(30);
        panel.add(title);
        label = new JLabel("Username");
        panel.add(label);
        username = new JTextField(30);
        panel.add(username);
        label = new JLabel("Password");
        panel.add(label);
        password = new JPasswordField(30);
        panel.add(password);
        label = new JLabel("URL");
        panel.add(label);
        url = new JTextField(30);
        panel.add(url);
        getContentPane().add(panel, BorderLayout.CENTER);

        //buttons
        panel = new JPanel(new FlowLayout());

        JButton button = new JButton("Cancel");
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
        panel.add(button);
        button = new JButton("Ok");
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            });
        panel.add(button);
        getContentPane().add(panel, BorderLayout.SOUTH);
        pack();
    }

    /**
     * @return Returns the ftp.
     *
     */
    public FTPsite getFtp() {
        ftp.setTitle(title.getText());
        ftp.setUser(username.getText());
        ftp.setUri(url.getText());

        //ftp.setPassword(PasswordManager.getInstance().encrypt(new
        // String(password.getPassword())));
        return ftp;
    }

    /**
     * @param ftp
     *
     * The ftp to set.
     *
     */
    public void setFtp(FTPsite ftp) {
        if (ftp == null) {
            newftp = true;
            this.ftp = new FTPsite();
        } else {
            newftp = false;
            this.ftp = cloneFtp(ftp);
            old = ftp;
        }

        title.setText(this.ftp.getTitle());
        username.setText(this.ftp.getUser());
        url.setText(this.ftp.getUri());

        //password.setText(PasswordManager.getInstance().decrypt(this.ftp.getPassword()));
    }

    /**
     * @return Returns the project.
     *
     */
    public Project getProject() {
        return project;
    }

    /**
     * @param project
     *
     * The project to set.
     *
     */
    public void setProject(Project project) {
        this.project = project;
    }

    private FTPsite cloneFtp(FTPsite f) {
        FTPsite clone = new FTPsite();
        clone.setTitle(f.getTitle());
        clone.setUser(f.getUser());
        clone.setUri(f.getUri());
        clone.setPassword(f.getPassword());
        clone.setID(f.getID());

        return clone;
    }
}
