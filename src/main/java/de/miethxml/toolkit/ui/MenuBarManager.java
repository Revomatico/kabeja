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

import javax.swing.Action;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import de.miethxml.toolkit.gui.LocaleMenu;


/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 *
 *
 *
 */
public interface MenuBarManager {
    public static String ROLE = MenuBarManager.class.getName();

    public abstract void addActionListener(
        String role,
        ActionListener l);

    public abstract void removeActionListener(
        String role,
        ActionListener l);

    public abstract void addMenu(
        String parentrole,
        String role,
        String icon);

    public abstract void addMenu(
        String parentrole,
        String role,
        LocaleMenu m);

    public abstract void addMenu(
        String parentrole,
        String role);

    //Add a new Toplevelmenu
    public abstract void addMenu(String role);

    public abstract void addMenuItem(
        String parentrole,
        String role,
        String icon);

    public abstract void addMenuItem(
        String parentrole,
        String role,
        String icon,
        KeyStroke key);

    public abstract void addMenuItem(
        String parentrole,
        String role,
        JMenuItem mi);

    public abstract void addMenuItem(
        String parentrole,
        String role);

    public abstract void removeMenu(String role);

    public abstract void removeMenuItem(String role);

    //remove later we have now LocaleMenus and LocaleMenuItems
    public abstract void addMenuLabel(
        String role,
        String label);

    public abstract void addSeparator(String parentrole);

    public abstract void setMenuItemAction(
        String role,
        Action action);

    public abstract JMenuBar getMenuBar();
}
