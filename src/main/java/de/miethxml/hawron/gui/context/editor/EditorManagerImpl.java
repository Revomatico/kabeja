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
package de.miethxml.hawron.gui.context.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPanel;

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
import org.apache.avalon.framework.activity.Initializable;

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
public class EditorManagerImpl extends AbstractContextViewHandler implements
        Initializable, Configurable, LocaleListener, PluginReceiver,
        EditorManager, SetupProcessListener {
    private Hashtable editors = new Hashtable();

    private ArrayList brokenEditors = new ArrayList();

    private ArrayList setupEditors = new ArrayList();

    private ArrayList actions = new ArrayList();

    private ArrayList externalEditors = new ArrayList();

    private ButtonPanel buttonpanel;

    private JMenu menu;

    private Collection interfaces = new HashSet();

    private boolean initialized = false;

    private ExternalEditorManager exManager;

    private boolean checksupportedextensions = true;

    /**
     * 
     * 
     *  
     */
    public EditorManagerImpl() {
        super();
        interfaces.add(Editor.class.getName());
        //interfaces.add(CacheableEditor.class.getName());
    }
    
    public EditorManagerImpl(PluginManager pluginManager,SearchEngine search,RepositoryModel fsModel, ApplicationSetup setup){
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
        exManager = new ExternalEditorManager();
        exManager.initialize();

        //	add the ExternalEditorSetup to the ApplicationSetup
        ExternalEditorSetup setup = new ExternalEditorSetup();
        setup.setExternalEditorManager(exManager);

        setup.initialize();
        appSetup.addGuiConfigurable("setup.plugins", (GuiConfigurable) setup);

        appSetup.addSetupProcessListener(this);

        //create a ButtonPanel
        buttonpanel = new ButtonPanel("editor.buttonpanel.menu.title");
        buttonpanel.init();
        menu = new JMenu(LocaleImpl.getInstance().getString(
                "editor.buttonpanel.menu.title"));
        LocaleImpl.getInstance().addLocaleListener(this);
        initEditors();
        if (pluginManager != null) {
            pluginManager.addPluginReceiver(this);
        }
    }

    public void dispose() {
        //destroy all editors now
        Enumeration e = editors.elements();

        while (e.hasMoreElements()) {
            EditorAction action = (EditorAction) e.nextElement();

            //log.debug("destroy Editor");
            action.destroy();
        }
    }

    public void initEditors() {
        editors.clear();
        actions.clear();

        //add buildin Editors first
        //		Editor pe = new PlainTextEditor();
        //		addEditor(pe);
        //add the external editors now
        for (int x = 0; x < exManager.getEditorCount(); x++) {
            ExternalEditor edit = exManager.getEditor(x);

            if (edit.isSupportedPlatform()) {
                addEditor(edit);

                //TODO this works at the moment change this later
                externalEditors.add(edit);
            }
        }
    }

    public JPanel getButtonPanel() {
        return buttonpanel;
    }

    private void registrateActions() {
        Iterator i = actions.iterator();

        while (i.hasNext()) {
            EditorAction action = (EditorAction) i.next();
            searchEngine.addFileSystemSelectionListener(action);
            fsModel.addRepositorySelectionListener(action);
        }
    }

    public void setup() {
        Iterator i = setupEditors.iterator();

        while (i.hasNext()) {
            Configurable conf = (Configurable) i.next();

            //TODO maybe remove it if setup fails
            if (!conf.isSetup()) {
                Editor edit = (Editor) conf;
                buttonpanel.removeAction((Action) editors.get(edit));
            }
        }

        i = brokenEditors.iterator();

        while (i.hasNext()) {
            Editor edit = (Editor) i.next();
            Configurable conf = (Configurable) edit;
            conf.setup();

            if (conf.isSetup()) {
                addEditor(edit);
                i.remove();
                setupEditors.add(edit);
            }
        }

        //TODO change this later
        //we remove now first all external Editors and add again
        //but we have to change this.
        i = externalEditors.iterator();

        while (i.hasNext()) {
            Editor edit = (Editor) i.next();
            buttonpanel.removeAction((Action) editors.get(edit));
            i.remove();
        }

        externalEditors.clear();

        for (int x = 0; x < exManager.getEditorCount(); x++) {
            ExternalEditor edit = exManager.getEditor(x);

            if (edit.isSupportedPlatform()) {
                addEditor(edit);
                externalEditors.add(edit);
            }
        }

        registrateActions();
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
     * @see de.miethxml.toolkit.conf.LocaleListener#langChanged()
     *  
     */
    public void langChanged() {
        menu.setText(LocaleImpl.getInstance().getString(
                "editor.buttonpanel.menu.title"));
    }

    public JMenu getEditorMenu() {
        return menu;
    }

    /*
     * 
     * (non-Javadoc)
     * 
     * @see de.miethxml.toolkit.plugins.PluginListener#addPlugin(java.lang.Object)
     *  
     */
    public void addPlugin(Object obj) {
        Editor edit = (Editor) obj;
        addEditor(edit);
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

    private void addEditor(Editor edit) {
        if (edit instanceof Configurable) {
            Configurable conf = (Configurable) edit;
            conf.setup();

            if (conf.isSetup()) {
                setupEditors.add(edit);
            } else {
                brokenEditors.add(edit);

                return;
            }
        }

        edit.init();

        EditorAction action = new EditorAction(edit);
        action.setCheckSupportedExtensions(checksupportedextensions);
        actions.add(action);
        editors.put(edit, action);
        searchEngine.addFileSystemSelectionListener(action);
        fsModel.addRepositorySelectionListener(action);

        //add to ButtonPanel
        buttonpanel.addAction(action);

        //add to menu
        menu.add(action);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.miethxml.gui.setup.SetupProcessListener#endSetup()
     */
    public void endSetup() {
        //TODO maybe better to handle SwingUtil.invokeLater here
        setup();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.miethxml.gui.setup.SetupProcessListener#startSetup()
     */
    public void startSetup() {
    }

    public void setCheckSupportedExtensions(boolean b) {
        this.checksupportedextensions = b;

        Iterator i = actions.iterator();

        while (i.hasNext()) {
            EditorAction action = (EditorAction) i.next();
            action.setCheckSupportedExtensions(b);
        }
    }
}