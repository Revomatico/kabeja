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
package de.miethxml.hawron.gui.context.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JPanel;

import de.miethxml.hawron.gui.context.AbstractContextViewHandler;
import de.miethxml.hawron.search.SearchEngine;

import de.miethxml.toolkit.component.Configurable;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;
import de.miethxml.toolkit.gui.ButtonPanel;
import de.miethxml.toolkit.plugins.PluginManager;
import de.miethxml.toolkit.plugins.PluginReceiver;
import de.miethxml.toolkit.repository.RepositoryModel;
import de.miethxml.toolkit.setup.ApplicationSetup;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 */
public class ActionManagerImpl extends AbstractContextViewHandler
    implements Configurable, LocaleListener, PluginReceiver, ActionManager,
        Initializable, Disposable {
    private ArrayList brokenActions = new ArrayList();
    private ArrayList setupActions = new ArrayList();
    private Collection interfaces = new HashSet();
    private ArrayList actions = new ArrayList();
    private ButtonPanel buttonpanel;
    private JMenu menu;
    private boolean checksupportedextensions = true;

    public ActionManagerImpl() {
        super();
        interfaces.add(Action.class.getName());
    }

    public ActionManagerImpl(PluginManager pluginManager,SearchEngine search,RepositoryModel fsModel, ApplicationSetup setup){
        this();
        setPluginManager(pluginManager);
        setSearchEngine(search);
        setRepositoryModel(fsModel);
        setApplicationSetup(setup);
        
    }
    
    
    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#init()
     *
     */
    public void initialize() {
        //create a ButtonPanel
        buttonpanel = new ButtonPanel("action.buttonpanel.menu.title");
        buttonpanel.init();
        menu = new JMenu(LocaleImpl.getInstance().getString("action.buttonpanel.menu.title"));
        LocaleImpl.getInstance().addLocaleListener(this);
        initActions();
        pluginManager.addPluginReceiver(this);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Configurable#isSetup()
     *
     */
    public boolean isSetup() {
        return false;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Configurable#setup()
     *
     */
    public void setup() {
        Iterator i = setupActions.iterator();

        while (i.hasNext()) {
            Configurable conf = (Configurable) i.next();
            conf.setup();

            //TODO implement here add and remove of the actions
        }

        i = brokenActions.iterator();

        while (i.hasNext()) {
            Action act = (Action) i.next();
            Configurable conf = (Configurable) act;
            conf.setup();

            if (conf.isSetup()) {
                addAction(act);
                i.remove();
                setupActions.add(act);
            }
        }

        registrateActions();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.conf.LocaleListener#langChanged()
     *
     */
    public void langChanged() {
        menu.setText(LocaleImpl.getInstance().getString("action.buttonpanel.menu.title"));
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.plugins.PluginListener#addPlugin(java.lang.Object)
     *
     */
    public void addPlugin(Object obj) {
        Action action = (Action) obj;
        addAction(action);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.plugins.PluginListener#getInterfaces()
     *
     */
    public Collection getInterfaces() {
        return interfaces;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.plugins.PluginListener#removePlugin(java.lang.Object)
     *
     */
    public void removePlugin(Object obj) {
    }

    private void addAction(Action act) {
        if (act instanceof Configurable) {
            Configurable conf = (Configurable) act;
            conf.setup();

            if (conf.isSetup()) {
                setupActions.add(act);
            } else {
                brokenActions.add(act);

                return;
            }
        }

        MenuAction action = new MenuAction(act);
        action.setCheckSupportedExtensions(checksupportedextensions);
        searchEngine.addFileSystemSelectionListener(action);
        fsModel.addRepositorySelectionListener(action);

        //add to ButtonPanel
        buttonpanel.addAction(action);

        //add to menu
        menu.add(action);
    }

    /*
     *
     * Create and add the buildin Actions
     *
     */
    private void initActions() {
        actions.clear();

        Action action = new CreateDirectoryAction();
        addAction(action);
        action = new RefreshAction();
        addAction(action);
        action = new DeleteAction();
        addAction(action);
        action = new ValidationAction();
        addAction(action);
        action = new XMLFormatAction();
        addAction(action);
    }

    public JPanel getButtonPanel() {
        return buttonpanel;
    }

    public JMenu getActionMenu() {
        return menu;
    }

    private void registrateActions() {
        Iterator i = actions.iterator();

        while (i.hasNext()) {
            MenuAction action = (MenuAction) i.next();
            searchEngine.addFileSystemSelectionListener(action);
            fsModel.addRepositorySelectionListener(action);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     *
     */
    public void dispose() {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.context.action.ActionManager#setCheckSupportedExtensions(boolean)
     */
    public void setCheckSupportedExtensions(boolean b) {
        this.checksupportedextensions = b;

        Iterator i = actions.iterator();

        while (i.hasNext()) {
            MenuAction action = (MenuAction) i.next();
            action.setCheckSupportedExtensions(b);
        }
    }
}
