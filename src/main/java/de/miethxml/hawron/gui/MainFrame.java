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
package de.miethxml.hawron.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
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

import com.jgoodies.plaf.BorderStyle;
import com.jgoodies.plaf.plastic.Plastic3DLookAndFeel;
import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.theme.DesertBlue;

import de.miethxml.hawron.ApplicationConstants;
import de.miethxml.hawron.gui.event.ApplicationShutdownAction;
import de.miethxml.hawron.gui.project.ProjectSaveAction;
import de.miethxml.hawron.gui.project.ProjectView;
import de.miethxml.hawron.project.Project;

import de.miethxml.toolkit.conf.ConfigManager;
import de.miethxml.toolkit.ui.MenuBarManager;
import de.miethxml.toolkit.ui.ToolBarManager;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
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
 */
public class MainFrame implements Serviceable, Initializable, Disposable,
    Parameterizable, ApplicationFrame {
    public static String PARAMETER_LOOK_N_FEEL = "lookandfeel";
    public static String PARAMETER_FRAME_X = "frame.location.x";
    public static String PARAMETER_FRAME_Y = "frame.location.y";
    public static String PARAMETER_FRAME_WIDTH = "frame.location.width";
    public static String PARAMETER_FRAME_HEIGHT = "frame.location.height";
    private ServiceManager serviceManager;
    private ServiceManager manager;
    private MenuBarManager mb;
    private JComponent toolbar;
    private Project project;
    private JComponent desktop;
    private Dimension size;
    private boolean initialized = false;
    private JButton save;

    //private String appName = "Hawron";
    private ProjectSaveAction saveAction;
    private JFrame frame;
    private ApplicationShutdownAction shutdownAction;
    private boolean projectViewAdded = false;
    private String lookNFeel;
    private JComponent view;

    /**
     * @throws java.awt.HeadlessException
     *
     */
    public MainFrame() {
    }

    public void initialize() {
        try {
            ProjectView view = (ProjectView) manager.lookup(ProjectView.ROLE);
            MenuBarManager menubar = (MenuBarManager) manager.lookup(MenuBarManager.ROLE);

            ToolBarManager toolbar = (ToolBarManager) manager.lookup(ToolBarManager.ROLE);

            frame = new JFrame();
            frame.setTitle(ApplicationConstants.APPLICATION_NAME + " "
                + ApplicationConstants.APPLICATION_VERSION);
            frame.addWindowListener(shutdownAction);
            frame.setSize(new Dimension(800, 600));
            frame.getContentPane().setLayout(new BorderLayout());

            //	
            JMenuBar mb = menubar.getMenuBar();
            SwingUtilities.updateComponentTreeUI(mb);
            frame.setJMenuBar(mb);

            JComponent comp = toolbar.getToolBar();
            SwingUtilities.updateComponentTreeUI(comp);
            frame.getContentPane().add(comp, BorderLayout.NORTH);

            setProjectView(view);
        } catch (HeadlessException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public void addWindowListener(WindowListener l) {
        frame.addWindowListener(l);
    }

    /**
     * @param toolbar
     *
     * The toolbar to set.
     * @deprecated
     */
    public void setToolBar(JComponent toolbar) {
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

    public void setProjectView(ProjectView pv) {
        view = pv.getView();

        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(BorderFactory.createEmptyBorder());
        //desktop = view;
        desktop=sp;

        frame.getContentPane().add(desktop, BorderLayout.CENTER);
        projectViewAdded = true;
    }

    /**
     * @deprecated
     */
    public void setMenuBar(JMenuBar menubar) {
        //		frame.setJMenuBar(menubar);
        //		SwingUtilities.updateComponentTreeUI(menubar);
    }

    public void setVisible(boolean b) {
        if (b) {
            if (ConfigManager.getInstance().hasProperty(PARAMETER_FRAME_X)) {
                restoreSizeAndLocation();
            } else {
                frame.pack();

                //check for size and enlarge the application
                Dimension dim = frame.getSize();
                Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
                double w = dim.getWidth();
                double h = dim.getHeight();

                if (screenDim.getWidth() > w) {
                    //add 40% of the free space
                    w += ((screenDim.getWidth() - w) * 0.4);
                }

                if (screenDim.getHeight() > h) {
                    //add 60% of the free space
                    h += ((screenDim.getHeight() - h) * 0.6);
                }

                //center the application
                dim.setSize(w, h);
                frame.setSize(dim);
                frame.setLocation((int) (
                        (screenDim.getWidth() - dim.getWidth()) / 2
                    ), (int) ((screenDim.getHeight() - dim.getHeight()) / 2));
            }
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
        storeSizeAndLocation();
    }

    public boolean isProjectViewShowing() {
        return projectViewAdded;
    }

    private void setLookAndFeel() {
        try {
            if (lookNFeel == null) {
                lookNFeel = ConfigManager.getInstance().getProperty(PARAMETER_LOOK_N_FEEL,
                        "");
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

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters arg0) throws ParameterException {
        if (arg0.isParameter(PARAMETER_LOOK_N_FEEL)) {
            lookNFeel = arg0.getParameter(PARAMETER_LOOK_N_FEEL);
        }

        setLookAndFeel();
    }

    private void restoreSizeAndLocation() {
        int x = Integer.parseInt(ConfigManager.getInstance().getProperty(PARAMETER_FRAME_X));
        int y = Integer.parseInt(ConfigManager.getInstance().getProperty(PARAMETER_FRAME_Y));
        int width = Integer.parseInt(ConfigManager.getInstance().getProperty(PARAMETER_FRAME_WIDTH));
        int height = Integer.parseInt(ConfigManager.getInstance().getProperty(PARAMETER_FRAME_HEIGHT));
        frame.setLocation(x, y);
        frame.setSize(width, height);
    }

    private void storeSizeAndLocation() {
        Point p = frame.getLocationOnScreen();
        Dimension dim = frame.getSize();

        ConfigManager.getInstance().setProperty(PARAMETER_FRAME_X,
            "" + ((int) p.getX()));
        ConfigManager.getInstance().setProperty(PARAMETER_FRAME_Y,
            "" + ((int) p.getY()));
        ConfigManager.getInstance().setProperty(PARAMETER_FRAME_WIDTH,
            "" + ((int) dim.getWidth()));
        ConfigManager.getInstance().setProperty(PARAMETER_FRAME_HEIGHT,
            "" + ((int) dim.getHeight()));
    }
}
