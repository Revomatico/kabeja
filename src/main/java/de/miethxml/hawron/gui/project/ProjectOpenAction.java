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

import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.ProjectComponent;
import de.miethxml.hawron.project.ProjectConfigListener;

import de.miethxml.toolkit.conf.ConfigManager;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;
import de.miethxml.toolkit.ui.MenuBarManager;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;


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
public class ProjectOpenAction extends AbstractAction implements LocaleListener,
    Parameterizable, Serviceable, Initializable, Startable, ProjectComponent,
    ProjectConfigListener {
    public static final String CLI_PROJECTFILE = "projectfile";
    public static String MENU_ROLE = "menu.file.open.recent";
    public static String DEFAULT_ACTION_COMMAND = "menu.file.open";
    public static String CONFIG_PROPERTY_COUNT = "recent.file.count";
    public static String CONFIG_PROPERTY_FILE = "recent.file.";
    private ServiceManager manager;
    private JFileChooser fc;
    private Project project;
    private String cliFilename = null;
    public int MAX_RECENT_OPEN_FILES = 10;
    private ArrayList menuList = new ArrayList();
    private HashSet projectFilenames = new HashSet();
    private String command;

    /**
     * @param name
     *
     */
    public ProjectOpenAction(String name) {
        super(name);
    }

    /**
     * @param name
     *
     * @param icon
     *
     */
    public ProjectOpenAction(
        String name,
        Icon icon) {
        super(name, icon);
    }

    public ProjectOpenAction() {
        super(LocaleImpl.getInstance().getString("menu.file.open"),
            new ImageIcon("icons/open.gif"));
        putValue(SHORT_DESCRIPTION,
            LocaleImpl.getInstance().getString("menu.file.open"));
        LocaleImpl.getInstance().addLocaleListener(this);
        putValue(AbstractAction.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke("control O"));
    }

    public void setProject(Project project) {
        this.project = project;
        project.addProjectConfigListener(this);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
        command = e.getActionCommand();

        Thread t = new Thread(new Runnable() {
                    public void run() {
                        if ((command != null)
                                && (
                                    !command.equals(DEFAULT_ACTION_COMMAND)
                                    && (command.length() > 0)
                                )) {
                            openProject(command);
                        } else {
                            int returnVal = fc.showOpenDialog(null);

                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                fc.setCurrentDirectory(fc.getSelectedFile()
                                                         .getParentFile());
                                openProject(fc.getSelectedFile().getPath());
                            }
                        }
                    }
                });
        t.start();
    }

    public void initialize() throws Exception {
        fc = new JFileChooser(ConfigManager.getInstance().getProperty("project.last.directory"));
        loadRecentFilenames();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.conf.LocaleListener#langChanged()
     *
     */
    public void langChanged() {
        putValue(SHORT_DESCRIPTION,
            LocaleImpl.getInstance().getString("menu.file.open"));
        putValue(NAME, LocaleImpl.getInstance().getString("menu.file.open"));
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public void openProject(String file) {
        project.load(file);
        addProjectFile(file);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.activity.Startable#start()
     */
    public void start() throws Exception {
        if (cliFilename != null) {
            openProject(cliFilename);
        } else if (ConfigManager.getInstance().getProperty(CLI_PROJECTFILE)
                                    .length() > 0) {
            openProject(ConfigManager.getInstance().getProperty(CLI_PROJECTFILE));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.activity.Startable#stop()
     */
    public void stop() throws Exception {
        storeRecentFilenames();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters arg0) throws ParameterException {
        if (arg0.isParameter(CLI_PROJECTFILE)) {
            cliFilename = arg0.getParameter(CLI_PROJECTFILE);
        }
    }

    private void addProjectFile(String file) {
        if (!projectFilenames.contains(file) && (file.length() > 0)) {
            if (menuList.size() < MAX_RECENT_OPEN_FILES) {
                //add a new MenuItem
                JMenuItem menuitem = new JMenuItem();
                menuitem.setActionCommand(file);
                menuitem.addActionListener(this);

                menuList.add(menuitem);

                try {
                    //add to menubar
                    MenuBarManager menubar = (MenuBarManager) manager.lookup(MenuBarManager.ROLE);
                    menubar.addMenuItem(MENU_ROLE,
                        "open.recent." + menuList.size(), menuitem);
                } catch (ServiceException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            String command = file;
            Iterator i = menuList.iterator();

            //the new to top
            while (i.hasNext()) {
                JMenuItem menuitem = (JMenuItem) i.next();
                String old = menuitem.getText();
                menuitem.setText(command);
                menuitem.setActionCommand(command);
                command = old;
            }

            projectFilenames.add(file);
        }
    } /*
    * (non-Javadoc)
    *
    * @see de.miethxml.hawron.project.ProjectConfigListener#configChanged(de.miethxml.hawron.project.Project)
    */
    public void configChanged(Project project) {
        addProjectFile(project.getFilename());
    }

    private void storeRecentFilenames() {
        //TODO change this later to StoreConfigurable
        ConfigManager.getInstance().setProperty(CONFIG_PROPERTY_COUNT,
            "" + menuList.size());

        int count = 0;
        Iterator i = menuList.iterator();
       
        
        while (i.hasNext()) {
            JMenuItem item = (JMenuItem) i.next();
            String file = item.getText();
            
            file = file.replaceAll("\\\\","\\\\\\\\");
            ConfigManager.getInstance().setProperty(CONFIG_PROPERTY_FILE
                + count,file );
            count++;
        }
    }

    private void loadRecentFilenames() {
        if (ConfigManager.getInstance().hasProperty(CONFIG_PROPERTY_COUNT)) {
            int count = Integer.parseInt(ConfigManager.getInstance()
                                                      .getProperty(CONFIG_PROPERTY_COUNT));

            for (int i = 0; i < count; i++) {
                String file = ConfigManager.getInstance().getProperty(CONFIG_PROPERTY_FILE
                        + i);
                addProjectFile(file);
            }
        }
    }
}
