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
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import de.miethxml.toolkit.conf.ConfigManager;
import de.miethxml.toolkit.gui.LocaleLabel;

import org.apache.avalon.framework.activity.Initializable;
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
 * @deprecated
 *
 */
public class ApplicationFrameImpl extends JFrame implements ApplicationFrame,
    Serviceable, Initializable {
    private ServiceManager manager;
    private JScrollPane desktop;

    /**
     * @throws java.awt.HeadlessException
     *
     */
    public ApplicationFrameImpl() throws HeadlessException {
        super();
    }

    /**
     * @param gc
     *
     */
    public ApplicationFrameImpl(GraphicsConfiguration gc) {
        super(gc);
    }

    /**
     * @param title
     *
     * @throws java.awt.HeadlessException
     *
     */
    public ApplicationFrameImpl(String title) throws HeadlessException {
        super(title);
    }

    /**
     * @param title
     *
     * @param gc
     *
     */
    public ApplicationFrameImpl(
        String title,
        GraphicsConfiguration gc) {
        super(title, gc);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.ApplicationFrame#setMenuBar(javax.swing.JMenuBar)
     *
     */
    public void setMenuBar(JMenuBar menubar) {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.ApplicationFrame#setToolBar(javax.swing.JToolBar)
     *
     */
    public void setToolBar(JToolBar toolbar) {
        this.getContentPane().add(toolbar, BorderLayout.NORTH);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.ApplicationFrame#setMainPanel(javax.swing.JPanel)
     *
     */
    public void setMainPanel(JPanel panel) {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     *
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     *
     */
    public void initialize() throws Exception {
        setSize(new Dimension(640, 480));

        //		try{
        //			//this.project =
        // (Project)manager.lookup("de.miethxml.hawron.project.Project");
        //		}catch(ServiceException se){
        //			se.printStackTrace();
        //		}
        //		try{
        //			//mb =
        // (EditableMenuBar)this.manager.lookup("de.miethxml.hawron.gui.MenuBar");
        //		}catch(ServiceException se){
        //			se.printStackTrace();
        //		}
        //setJMenuBar(mb);
        // create Toolbar and MenuBar
        this.getContentPane().setLayout(new BorderLayout());

        //this.getContentPane().add(getToolBar(), BorderLayout.NORTH);
        //remove later, only test
        ConfigManager.getInstance().setProperty("lang", "en");

        //create the desktop scrollpane
        desktop = new JScrollPane();

        JPanel panel = new JPanel(new BorderLayout());
        LocaleLabel label = new LocaleLabel("main.label.welcome");
        panel.add(label, BorderLayout.CENTER);
        desktop.add(panel);
        getContentPane().add(desktop, BorderLayout.CENTER);
        validate();
    }
}
