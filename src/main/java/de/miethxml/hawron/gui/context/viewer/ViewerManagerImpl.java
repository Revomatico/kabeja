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
package de.miethxml.hawron.gui.context.viewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;

import de.miethxml.hawron.gui.context.AbstractContextViewHandler;
import de.miethxml.hawron.search.SearchEngine;

import de.miethxml.toolkit.component.Configurable;
import de.miethxml.toolkit.component.GuiConfigurable;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;
import de.miethxml.toolkit.gui.ButtonPanel;
import de.miethxml.toolkit.plugins.PluginManager;
import de.miethxml.toolkit.plugins.PluginReceiver;
import de.miethxml.toolkit.repository.RepositoryModel;
import de.miethxml.toolkit.setup.SetupProcessListener;
import de.miethxml.toolkit.setup.ApplicationSetup;

/**
 * This is the implementaion of ViewerManager and can handle buildin Viewer,
 * PluginViewer and ExternalViewer (native applications).
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public class ViewerManagerImpl extends AbstractContextViewHandler
    implements PluginReceiver, Configurable, LocaleListener, ViewerManager,
        SetupProcessListener {
    private Hashtable viewers = new Hashtable();
    private ArrayList brokenViewers = new ArrayList();
    private ArrayList setupViewers = new ArrayList();
    private ArrayList actions = new ArrayList();
    private ArrayList externalViewers = new ArrayList();
    private ButtonPanel buttonpanel;
    private JMenu menu;
    private ExternalViewerManager exManager;
    private Collection interfaces = new HashSet();
    private boolean checksupportedextensions = true;

    public ViewerManagerImpl() {
        super();
        interfaces.add(Viewer.class.getName());
        interfaces.add(CacheableViewer.class.getName());
    }

    
    
    public ViewerManagerImpl(PluginManager pluginManager,SearchEngine search,RepositoryModel fsModel, ApplicationSetup setup){
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
        buttonpanel = new ButtonPanel("viewer.buttonpanel.menu.title");
        buttonpanel.init();
        menu = new JMenu(LocaleImpl.getInstance().getString("viewer.buttonpanel.menu.title"));
        LocaleImpl.getInstance().addLocaleListener(this);

        exManager = new ExternalViewerManager();
        exManager.initialize();

        //	add the ExternalEditorSetup to the ApplicationSetup
        ExternalViewerSetup setup = new ExternalViewerSetup();
        setup.setExternalViewerManager(exManager);
        setup.initialize();

        appSetup.addGuiConfigurable("setup.plugins", (GuiConfigurable) setup);
        appSetup.addSetupProcessListener(this);

        initViewers();

        pluginManager.addPluginReceiver(this);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#release()
     *
     */
    public void dispose() {
        //destroy all editors now
        Enumeration e = viewers.elements();

        while (e.hasMoreElements()) {
            ViewerAction action = (ViewerAction) e.nextElement();
            action.destroy();
        }
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
     * @see de.miethxml.toolkit.plugins.PluginListener#addPlugin(java.lang.Object)
     *
     */
    public void addPlugin(Object obj) {
        Viewer viewer = (Viewer) obj;
        addViewer(viewer);
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

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Configurable#isSetup()
     *
     */
    public boolean isSetup() {
        return true;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Configurable#setup()
     *
     */
    public void setup() {
        //first check if there are some editors now working
        Iterator i = brokenViewers.iterator();

        while (i.hasNext()) {
            Viewer viewer = (Viewer) i.next();
            Configurable conf = (Configurable) viewer;
            conf.setup();

            if (conf.isSetup()) {
                addViewer(viewer);
                i.remove();
                setupViewers.add(viewer);
            }
        }

        i = setupViewers.iterator();

        while (i.hasNext()) {
            Configurable conf = (Configurable) i.next();
            conf.setup();

            //TODO maybe remove it if setup fails
            if (!conf.isSetup()) {
                Viewer view = (Viewer) conf;
                buttonpanel.removeAction((Action) viewers.get(view));
                i.remove();
                brokenViewers.add(view);
            }
        }

        //we remove now first all external Viewer and add again
        //but we have to change this.
        i = externalViewers.iterator();

        while (i.hasNext()) {
            Viewer view = (Viewer) i.next();
            buttonpanel.removeAction((Action) viewers.get(view));
            i.remove();
        }

        externalViewers.clear();

        for (int x = 0; x < exManager.getViewerCount(); x++) {
            ExternalViewer view = exManager.getViewer(x);

            if (view.isSupportedPlatform()) {
                addViewer(view);
                externalViewers.add(view);
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
        menu.setText(LocaleImpl.getInstance().getString("viewer.buttonpanel.menu.title"));
    }

    /*
     *
     * create buildin Viewer
     *
     */
    private void initViewers() {
        viewers.clear();
        actions.clear();

        //TODO remove the HTMLViewer 
        //simple add this as external viewer or create a plugin
        //Viewer view = new HTMLViewer();
        //addViewer(view);

        for (int x = 0; x < exManager.getViewerCount(); x++) {
            ExternalViewer viewer = exManager.getViewer(x);

            if (viewer.isSupportedPlatform()) {
                addViewer(viewer);
                externalViewers.add(viewer);
            }
        }
    }

    private void addViewer(Viewer viewer) {
        if (viewer instanceof Configurable) {
            Configurable conf = (Configurable) viewer;
            conf.setup();

            if (conf.isSetup()) {
                setupViewers.add(viewer);
            } else {
                brokenViewers.add(viewer);

                return;
            }
        }

        viewer.init();

        ViewerAction action = new ViewerAction(viewer);
        action.setCheckSupportedExtensions(checksupportedextensions);
        actions.add(action);
        viewers.put(viewer, action);
        searchEngine.addFileSystemSelectionListener(action);
        fsModel.addRepositorySelectionListener(action);

        //add to ButtonPanel
        buttonpanel.addAction(action);

        //add to menu
        menu.add(action);
    }

    public JComponent getButtonPanel() {
        return buttonpanel;
    }

    public JMenu getViewerMenu() {
        return menu;
    }

    private void registrateActions() {
        Iterator i = actions.iterator();

        while (i.hasNext()) {
            ViewerAction action = (ViewerAction) i.next();
            searchEngine.addFileSystemSelectionListener(action);
            fsModel.addRepositorySelectionListener(action);
        }
    }

    public void setCheckSupportedExtensions(boolean b) {
        this.checksupportedextensions = b;

        Iterator i = actions.iterator();

        while (i.hasNext()) {
            ViewerAction action = (ViewerAction) i.next();
            action.setCheckSupportedExtensions(b);
        }
    }

    public void endSetup() {
        setup();
    }

    public void startSetup() {
        // TODO Auto-generated method stub
    }
}
