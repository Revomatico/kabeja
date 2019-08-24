/*
   Copyright 2004 Simon Mieth

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
package de.miethxml.toolkit.ui.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.log.Hierarchy;
import org.apache.log.Logger;
import org.apache.tools.ant.Project;

import com.jgoodies.plaf.BorderStyle;
import com.jgoodies.plaf.plastic.Plastic3DLookAndFeel;
import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.theme.DesertBlue;

import de.miethxml.hawron.gui.project.ProjectSaveAction;
import de.miethxml.toolkit.conf.ConfigManager;
import de.miethxml.toolkit.ui.ApplicationFrame;
import de.miethxml.toolkit.ui.MenuBarManager;
import de.miethxml.toolkit.ui.ToolBarManager;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public class MainFrame implements Serviceable, Initializable, Disposable,
    Configurable, ApplicationFrame, Startable {
    public final static String PARAMETER_LOOK_N_FEEL = "look-and-feel";
    private Logger log = Hierarchy.getDefaultHierarchy().getLoggerFor(this.getClass()
                                                                          .getName());
    private ServiceManager serviceManager;
    private ServiceManager manager;
    private MenuBarManager mb;
    private JComponent toolbar;
    private Project project;
    private JComponent desktop;
    private Dimension size;
    private boolean initialized = false;
    private JButton save;
    private String appName = "";
   // private ProjectSaveAction saveAction;
    private JFrame frame;
    private boolean viewAdded = false;
    private String lookNFeel;
    private String title = "";
    private JComponent view;

    /**
     * @throws java.awt.HeadlessException
     *
     */
    public MainFrame() {
    }

    public void initialize() {
        try {
            MenuBarManager menubar = (MenuBarManager) manager.lookup(MenuBarManager.ROLE);
            ToolBarManager toolbar = (ToolBarManager) manager.lookup(ToolBarManager.ROLE);

            frame = new JFrame();
            frame.setTitle(title);
            frame.setSize(new Dimension(640, 480));
            frame.getContentPane().setLayout(new BorderLayout());

            JMenuBar m = menubar.getMenuBar();
            SwingUtilities.updateComponentTreeUI(m);
            frame.setJMenuBar(m);

            JComponent comp = toolbar.getToolBar();
            SwingUtilities.updateComponentTreeUI(comp);
            frame.getContentPane().add(comp, BorderLayout.NORTH);
        } catch (HeadlessException e) { 
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public void addWindowListener(WindowListener l) {
        frame.addWindowListener(l);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#setComponentManager(de.miethxml.toolkit.component.ComponentManager)
     *
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public void setApplicationView(JComponent view) {
        this.view = view;

        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(BorderFactory.createEmptyBorder());
        desktop = sp;

        frame.getContentPane().add(desktop, BorderLayout.CENTER);
        viewAdded = true;
    }

    public void setVisible(boolean b) {
        if (b) {
            frame.pack();
        }

        frame.setVisible(b);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     *
     */
    public void dispose() {
    }

    private void setLookAndFeel() {
        try {
            if (lookNFeel == null) {
                lookNFeel = ConfigManager.getInstance().getProperty(PARAMETER_LOOK_N_FEEL);
            }

            // set Look&Feel
            if (lookNFeel.equals("platform")) {
                String nativeLF = UIManager.getSystemLookAndFeelClassName();
                setLookNFeel(nativeLF);
            } else if (lookNFeel.length() > 0) {
                setLookNFeel(lookNFeel);
            } else {
                //default look and feel
                PlasticLookAndFeel.setMyCurrentTheme(new DesertBlue());
                UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
                UIManager.put("jgoodies.popupDropShadowEnabled", Boolean.TRUE);
                UIManager.put("Plastic3DLookAndFeel.BORDER_STYLE_KEY",
                    BorderStyle.EMPTY);

                if (frame != null) {
                    SwingUtilities.updateComponentTreeUI(frame);
                }
            }
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public void setLookNFeel(String value) {
        try {
            UIManager.setLookAndFeel(value);

            if (frame != null) {
                SwingUtilities.updateComponentTreeUI(frame);
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void start() throws Exception {
        setVisible(true);
    }

    public void stop() throws Exception {
    }

    public void configure(Configuration conf) throws ConfigurationException {
        Configuration c = conf.getChild("title");

        if (c != null) {
            this.title = c.getValue();
        }

        c = conf.getChild("look-and-feel");

        if ((c != null) && (c.getAttribute("value", null) != null)) {
            lookNFeel = c.getAttribute("value");
        }

        setLookAndFeel();
    } 
}
