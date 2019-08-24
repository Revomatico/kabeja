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

import java.awt.event.ActionListener;

import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.gui.LocaleMenu;
import de.miethxml.toolkit.gui.LocaleMenuItem;
import de.miethxml.toolkit.ui.builder.SAXMenuBuilder;

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
 */
public class EditableMenuBar extends JMenuBar implements Serviceable,
    MenuBarManager {
    private ServiceManager manager;
    private Hashtable menus;
    private Hashtable menuitems;
    private Hashtable relations;
    private boolean helpMenuPresent = false;
    private String HELPMENUROLE = "menu.help";
    public String MENU_CONFIG = "conf/gui.xml";

    /**
     *
     *
     *
     */
    public EditableMenuBar() {
        super();
        menus = new Hashtable();
        menuitems = new Hashtable();
        relations = new Hashtable();
        initialize();
    }

    public void addActionListener(
        String role,
        ActionListener l) {
        if (menuitems.containsKey(role)) {
            LocaleMenuItem mi = (LocaleMenuItem) menuitems.get(role);
            mi.addActionListener(l);
        } else if (menus.containsKey(role)) {
            LocaleMenu m = (LocaleMenu) menus.get(role);
            m.addActionListener(l);
        }
    }

    public void removeActionListener(
        String role,
        ActionListener l) {
        LocaleMenuItem m = (LocaleMenuItem) menuitems.get(role);

        if (m == null) {
            m = (LocaleMenuItem) menus.get(role);
        }

        if (m != null) {
            m.removeActionListener(l);
        }
    }

    public void addMenu(
        String parentrole,
        String role,
        String icon) {
        LocaleMenu m = null;

        if (!menus.containsKey(role)) {
            m = new LocaleMenu(role);

            if ((icon != null) && (icon.length() > 0)) {
                m.setIcon(new ImageIcon(icon));
            }

            if (parentrole.length() > 0) {
                LocaleMenu menu = (LocaleMenu) menus.get(parentrole);
                menu.add(m);
            } else if (role.equals(HELPMENUROLE)) {
                setHelpMenu(m);
            } else if (helpMenuPresent) {
                this.add(m, getComponentCount() - 2);
            } else {
                this.add(m);
            }

            menus.put(role, m);
            relations.put(role, parentrole);
            m.setText(LocaleImpl.getInstance().getString(role));
            m.setActionCommand(role);
        }
    }

    public void addMenu(
        String parentrole,
        String role,
        LocaleMenu m) {
        if (parentrole.length() > 0) {
            LocaleMenu menu = (LocaleMenu) menus.get(parentrole);
            menu.add(m);
        } else if (role.equals(HELPMENUROLE)) {
            setHelpMenu(m);
        } else if (helpMenuPresent) {
            this.add(m, getComponentCount() - 2);
        } else {
            this.add(m);
        }

        menus.put(role, m);
        relations.put(role, parentrole);

        m.setActionCommand(role);
    }

    public void addMenu(
        String parentrole,
        String role) {
        addMenu(parentrole, role, "");
    }

    //Add a new Toplevelmenu
    public void addMenu(String role) {
        addMenu("", role, "");
    }

    public void addMenuItem(
        String parentrole,
        String role,
        String icon) {
        addMenuItem(parentrole, role, icon, null);
    }

    public void addMenuItem(
        String parentrole,
        String role,
        String icon,
        KeyStroke key) {
        LocaleMenuItem mi = null;

        if (menuitems.containsKey(role)) {
            mi = (LocaleMenuItem) menuitems.get(role);
        } else {
            mi = new LocaleMenuItem(role);

            if ((icon != null) && (icon.length() > 0)) {
                mi.setIcon(new ImageIcon(icon));
            }

            JMenu menu = (JMenu) menus.get(parentrole);
            menu.add(mi);
            menuitems.put(role, mi);
            relations.put(role, parentrole);
        }

        mi.setActionCommand(role);

        if (key != null) {
            mi.setAccelerator(key);
        }
    }

    public void addMenuItem(
        String parentrole,
        String role,
        JMenuItem mi) {
        if (menuitems.containsKey(role)) {
            mi = (LocaleMenuItem) menuitems.get(role);
        } else {
            JMenu menu = (JMenu) menus.get(parentrole);
            menu.add(mi);
            menuitems.put(role, mi);
            relations.put(role, parentrole);
        }
    }

    public void addMenuItem(
        String parentrole,
        String role) {
        addMenuItem(parentrole, role, "");
    }

    public void removeMenu(String role) {
        String parent = (String) relations.get(role);

        if (parent.length() > 0) {
            LocaleMenu menu = (LocaleMenu) menus.get(parent);
            menu.remove((LocaleMenu) menus.get(role));
            menus.remove(role);
        } else {
            this.remove((LocaleMenu) menus.get(role));
        }
    }

    public void removeMenuItem(String role) {
        String parent = (String) relations.get(role);
        LocaleMenu menu = (LocaleMenu) menus.get(parent);
        menu.remove((LocaleMenuItem) menuitems.get(role));
        menuitems.remove(role);
    }

    public void addSeparator(String parent) {
        LocaleMenu menu = (LocaleMenu) menus.get(parent);
        menu.add(new JSeparator());
    }

    public void addMenuLabel(
        String role,
        String label) {
        LocaleImpl.getInstance().setString(role, label);
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

    public void initialize() {
        SAXMenuBuilder sb = new SAXMenuBuilder();
        sb.setMenuBar(this);
        sb.buildMenu(MENU_CONFIG);
    }

    public void setHelpMenu(LocaleMenu help) {
        if (!helpMenuPresent) {
            add(Box.createHorizontalGlue());
            add(help);
            helpMenuPresent = true;
        } else {
            //replace the current helpmenu
            remove(getComponentCount() - 1);
            add(help);
        }
    }

    public void setMenuAction(
        String role,
        Action action) {
        LocaleMenu m = (LocaleMenu) menus.get(role);
        m.setAction(action);
        m.setActionCommand(role);
    }

    public void setMenuItemAction(
        String role,
        Action action) {
        LocaleMenuItem mi = (LocaleMenuItem) menuitems.get(role);
        mi.setAction(action);
        mi.setActionCommand(role);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.MenuBarManager#getMenuBar()
     *
     */
    public JMenuBar getMenuBar() {
        return this;
    }
}
