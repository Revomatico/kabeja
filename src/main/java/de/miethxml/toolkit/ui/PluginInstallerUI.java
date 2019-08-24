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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.miethxml.toolkit.plugins.PluginInstaller;
import de.miethxml.toolkit.plugins.PluginInstallerImpl;
import de.miethxml.toolkit.plugins.PluginManager;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 */
public class PluginInstallerUI implements Serviceable, Startable {
    private JPanel panel;
    private JDialog dialog;
    private JTextField location;
    private PluginInstaller installer;
    private MenuBarManager menubar;

    /**
     *
     */
    public PluginInstallerUI() {
        super();
    }

    private void init() {
        panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("Location:"));
        location = new JTextField(20);
        panel.add(location);

        JButton button = new JButton("Install");
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    installPlugin();
                }
            });
        panel.add(button);
        dialog = new JDialog();
        dialog.setTitle("PluginInstaller");
        dialog.getContentPane().add(panel);
        dialog.pack();
    }

    private void installPlugin() {
        try {
            File f = new File(location.getText());
            URL url = f.toURL();
            installer.installPlugin(url);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        try {
            menubar = (MenuBarManager) manager.lookup(MenuBarManager.ROLE);

            PluginManager pmanager = (PluginManager) manager.lookup(PluginManager.ROLE);

            installer = new PluginInstallerImpl();

            installer.setPluginManager(pmanager);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.activity.Startable#start()
     */
    public void start() throws Exception {
        // TODO Auto-generated method stub
        JMenuItem item = new JMenuItem();
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (dialog == null) {
                        init();
                    }

                    dialog.setVisible(true);
                }
            });

        menubar.addMenuItem("menu.plugins", "menu.plugins.plugininstaller", item);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.activity.Startable#stop()
     */
    public void stop() throws Exception {
        // TODO Auto-generated method stub
    }
}
