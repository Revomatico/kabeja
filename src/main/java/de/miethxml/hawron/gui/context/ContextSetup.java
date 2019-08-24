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
package de.miethxml.hawron.gui.context;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.gui.context.ui.ButtonPanelSelectionModel;
import de.miethxml.hawron.gui.context.ui.ExtensionCheckSelectionModel;
import de.miethxml.hawron.search.SearchEngine;

import de.miethxml.toolkit.gui.LocaleLabel;
import de.miethxml.toolkit.gui.LocaleSeparator;
import de.miethxml.toolkit.gui.StringListView;
import de.miethxml.toolkit.gui.StringListViewImpl;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 */
public class ContextSetup {
    private JTabbedPane tp;
    private SearchEngine searchEngine;
    private StringListView searchFilter;
    private JCheckBox checkextensions;
    private JCheckBox showbuttonpanel;
    private ContextViewComponent contextview;

    public ContextSetup(ContextViewComponent view) {
        this.contextview = view;
    }

    public void init() {
        tp = new JTabbedPane();

        //the main settings
        FormLayout layout = new FormLayout("3dlu,pref,2dlu,fill:pref:grow,3dlu",
                "3dlu,p,2dlu,p,3dlu");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        showbuttonpanel = new JCheckBox();

        //showbuttonpanel.setSelected(contextview.isShowButtonPanels());
        showbuttonpanel.setModel(new ButtonPanelSelectionModel(contextview));

        builder.add(showbuttonpanel, cc.xy(2, 2));
        builder.add(new LocaleLabel(
                "view.context.setup.checkbox.showbuttonpanel"), cc.xy(4, 2));

        checkextensions = new JCheckBox();
        checkextensions.setModel(new ExtensionCheckSelectionModel(contextview));
        builder.add(checkextensions, cc.xy(2, 4));
        builder.add(new LocaleLabel(
                "view.context.setup.checkbox.checkextensions"), cc.xy(4, 4));
        tp.addTab("Main", builder.getPanel());

        //the settings for the search engine
        layout = new FormLayout("3dlu,fill:p:grow,3dlu", "3dlu,p,2dlu,p,3dlu");
        builder = new PanelBuilder(layout);
        cc = new CellConstraints();

        builder.add(new LocaleSeparator(
                "view.context.setup.separator.search.extensions"), cc.xy(2, 2));
        searchFilter = new StringListViewImpl();
        searchFilter.init();
        builder.add(searchFilter.getView(), cc.xy(2, 4));
        tp.addTab("Search", builder.getPanel());
    }

    public JComponent getView() {
        return tp;
    }

    public void setSearchEngine(SearchEngine engine) {
        this.searchEngine = engine;
        searchFilter.setStringList(engine.getFileExtensions());
    }

    public void update() {
        searchEngine.setFileExtensions(searchFilter.getStringList());
        contextview.showButtonPanels(showbuttonpanel.isSelected());
        contextview.setCheckSupportedExtensions(checkextensions.isSelected());
    }
}
