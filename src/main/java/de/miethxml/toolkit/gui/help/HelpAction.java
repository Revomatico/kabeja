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
package de.miethxml.toolkit.gui.help;

import java.awt.event.ActionEvent;

import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import de.miethxml.toolkit.cache.GUICache;
import de.miethxml.toolkit.conf.ConfigManager;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;

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
public class HelpAction extends AbstractAction implements LocaleListener,
    Serviceable, Initializable {
    private String defaultURL = "index.htm";
    private ServiceManager manager;

    /**
     *
     *
     *
     */
    public HelpAction() {
        super(LocaleImpl.getInstance().getString("common.help"),
            new ImageIcon("icons/help.gif"));
        putValue(SHORT_DESCRIPTION,
            LocaleImpl.getInstance().getString("common.help"));
        LocaleImpl.getInstance().addLocaleListener(this);

        putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F1"));
    }

    /**
     * @param name
     *
     */
    public HelpAction(String url) {
        this();
        setDefaultURL(url);
    }

    /**
     * @param name
     *
     * @param icon
     *
     */
    public HelpAction(
        String name,
        Icon icon) {
        super(name, icon);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
        String url = "file:"
            + ConfigManager.getInstance().getProperty("help.path")
            + File.separator + ConfigManager.getInstance().getProperty("lang")
            + File.separator;

        if (defaultURL != null) {
            url = url + defaultURL;
        } else {
            url = url + e.getActionCommand();
        }

        HelpBrowser hb;

        if (!GUICache.getInstance().cached("help.browser")) {
            hb = new HelpBrowser();
            GUICache.getInstance().addComponent("help.browser", hb);
        }

        hb = (HelpBrowser) GUICache.getInstance().getComponent("help.browser");

        if (hb != null) {
            hb.show();
            hb.setPage(url);
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.conf.LocaleListener#langChanged()
     *
     */
    public void langChanged() {
        putValue(SHORT_DESCRIPTION,
            LocaleImpl.getInstance().getString("common.help"));
        putValue(NAME, LocaleImpl.getInstance().getString("common.help"));
    }

    /**
     * @param defaultURL
     *            The defaultURL to set.
     *
     */
    public void setDefaultURL(String defaultURL) {
        this.defaultURL = defaultURL;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#init()
     *
     */
    public void initialize() {
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
}
