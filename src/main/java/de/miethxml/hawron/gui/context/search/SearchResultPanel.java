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
package de.miethxml.hawron.gui.context.search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.search.SearchEngineImpl;
import de.miethxml.hawron.search.SearchResultListener;

import de.miethxml.toolkit.gui.FineBorder;


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

/*
 *
 * TODO change this to a Builder, which returns a JComponent
 *      and not extend JPanel.
 *
 */
public class SearchResultPanel extends JPanel implements ActionListener,
    SearchResultListener {
    //	
    private SearchEngineImpl searchEngine;

    /**
     *
     *
     *
     */
    public SearchResultPanel(SearchEngineImpl searchEngine) {
        super();
        setSearchEngine(searchEngine);
    }

    public void initialize() {
        searchEngine.addSearchResultListener(this);

        FineBorder border = new FineBorder("Search Results");

        border.setStartColor(new Color(66, 66, 66));
        border.setEndColor(new Color(175, 175, 175));
        border.setTextColor(Color.WHITE);
        setBorder(border);
        setLayout(new BorderLayout());

        FormLayout panellayout = new FormLayout("3dlu,fill:pref:grow,3dlu",
                "3dlu,70dlu:grow");
        PanelBuilder builder = new PanelBuilder(panellayout);
        CellConstraints cc = new CellConstraints();
        JTable search = new JTable(searchEngine.getResultTableModel());
        search.getSelectionModel().addListSelectionListener(searchEngine);

        JScrollPane sl = new JScrollPane(search);
        builder.add(sl, cc.xy(2, 2));
        add(builder.getPanel(), BorderLayout.CENTER);

        //		JButton[] buttons = new JButton[1];
        //		buttons[0] = new JButton("close");
        //		buttons[0].setActionCommand("hidde");
        //		buttons[0].addActionListener(this);
        //
        //		ButtonBarBuilder bbuilder = new ButtonBarBuilder();
        //		bbuilder.addGriddedButtons(buttons);
        //		add(bbuilder.getPanel(), BorderLayout.SOUTH);
        //setVisible(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("hidde")) {
            setVisible(false);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.search.SearchResultListener#finishSearching(int)
     *
     */
    public void finishSearching(int hits) {
        if (hits > 0) {
            setVisible(true);
            revalidate();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.search.SearchResultListener#startSearching()
     *
     */
    public void startSearching() {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.search.SearchResultListener#finishIndexing()
     *
     */
    public void finishIndexing() {
    }

    /**
     * @param searchEngine
     *            The searchEngine to set.
     */
    public void setSearchEngine(SearchEngineImpl searchEngine) {
        this.searchEngine = searchEngine;
    }
}
