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
package de.miethxml.toolkit.ui.event;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import de.miethxml.toolkit.application.ApplicationShutdown;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;
import de.miethxml.toolkit.ui.ApplicationFrame;

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
 *
 *
 */
public class DefaultApplicationShutdownAction extends AbstractAction
    implements Serviceable, Initializable, LocaleListener, WindowListener {
    private ServiceManager manager;
    private ApplicationShutdown appShutdown;
    private ApplicationFrame frame;

    /**
     *
     *
     *
     */
    public DefaultApplicationShutdownAction() {
        super(LocaleImpl.getInstance().getString("menu.file.quit"),
            new ImageIcon("icons/exit.gif"));
        putValue(SHORT_DESCRIPTION,
            LocaleImpl.getInstance().getString("menu.file.quit"));
        LocaleImpl.getInstance().addLocaleListener(this);
        putValue(AbstractAction.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke("control Q"));
    }

    /**
     * @param name
     *
     */
    public DefaultApplicationShutdownAction(String name) {
        super(name);
    }

    /**
     * @param name
     *
     * @param icon
     *
     */
    public DefaultApplicationShutdownAction(
        String name,
        Icon icon) {
        super(name, icon);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
        shutdown();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#init()
     *
     */
    public void initialize() {
        try {
            appShutdown = (ApplicationShutdown) manager.lookup(ApplicationShutdown.ROLE);
            frame = (ApplicationFrame) manager.lookup(ApplicationFrame.ROLE);
            frame.addWindowListener(this);

            //saveAction = (ProjectSaveAction)
            // manager.lookup("de.miethxml.hawron.gui.project.ProjectSaveAction");
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#setComponentManager(de.miethxml.toolkit.component.ComponentManager)
     *
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.conf.LocaleListener#langChanged()
     *
     */
    public void langChanged() {
        putValue(SHORT_DESCRIPTION,
            LocaleImpl.getInstance().getString("menu.file.quit"));
        putValue(NAME, LocaleImpl.getInstance().getString("menu.file.quit"));
    }

    public void shutdown() {
        try {
            ApplicationShutdown app = (ApplicationShutdown) manager.lookup(ApplicationShutdown.ROLE);
            app.shutdownApplication();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    public void windowActivated(WindowEvent e) {
        //we ignore this
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    public void windowClosed(WindowEvent e) {
        //thats to late
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    public void windowClosing(WindowEvent e) {
        // our job here
        shutdown();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    public void windowDeactivated(WindowEvent e) {
        //we ignore this
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    public void windowDeiconified(WindowEvent e) {
        // we ignore this
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    public void windowIconified(WindowEvent e) {
        //we ignore this
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    public void windowOpened(WindowEvent e) {
        // ah someone use the application,but we ignore this
    }
}
