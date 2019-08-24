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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.search.SearchEngine;
import de.miethxml.hawron.search.SearchResultListener;

import de.miethxml.toolkit.gui.JGoodiesSeparator;


//import de.miethxml.gui.search.*;

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
public class SearchQueryPanel extends JPanel implements ActionListener,
    SearchResultListener {
    private JTextField query;
    private JButton button;
    private JLabel hits;
    private JCheckBox checkbox;
    private JGoodiesSeparator separator;
    private SearchEngine searchEngine;

    /**
     *
     *
     *
     */
    public SearchQueryPanel() {
        super();
    }

    public SearchQueryPanel(SearchEngine searchEngine) {
        super();
        this.searchEngine = searchEngine;
    }

    public void initialize() {
        FormLayout panellayout = new FormLayout("3dlu,fill:pref:grow,2dlu,right:pref,3dlu",
                "3dlu,p,2dlu,p,2dlu,p,2dlu,p,3dlu");
        CellConstraints cc = new CellConstraints();
        setLayout(panellayout);
        separator = new JGoodiesSeparator("Search");
        add(separator, cc.xywh(2, 2, 3, 1));
        query = new JTextField(12);
        query.setActionCommand("search");
        query.addActionListener(this);
        add(query, cc.xywh(2, 4, 3, 1));
        hits = new JLabel("");
        add(hits, cc.xy(2, 6));
        button = new JButton("search");
        button.setActionCommand("search");
        button.addActionListener(this);
        add(button, cc.xy(4, 6));
        checkbox = new JCheckBox();
        checkbox.setActionCommand("indexing");
        checkbox.addActionListener(this);
        checkbox.setSelected(true);
        add(checkbox,
            cc.xy(2, 8, CellConstraints.RIGHT, CellConstraints.DEFAULT));

        JLabel label = new JLabel("Update index");
        add(label, cc.xy(4, 8));
        searchEngine.addSearchResultListener(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("search")) {
            hits.setText("");
            searchEngine.search(query.getText());
        } else if (e.getActionCommand().equals("cancel")) {
            searchEngine.interruptSearch();
            this.hits.setText("canceled");
            button.setText("Search");
            button.setActionCommand("search");
        } else if (e.getActionCommand().equals("indexing")) {
            if (checkbox.isSelected()) {
                searchEngine.setUpdateIndex(true);
            } else {
                searchEngine.setUpdateIndex(false);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.search.SearchResultListener#finishSearching(int)
     *
     */
    public void finishSearching(int hits) {
        button.setEnabled(true);
        this.hits.setText("hits: " + hits);
        button.setText("Search");
        button.setActionCommand("search");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.search.SearchResultListener#startSearching()
     *
     */
    public void startSearching() {
        this.hits.setText("Index ...");
        button.setText("Cancel");
        button.setActionCommand("cancel");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.search.SearchResultListener#finishIndexing()
     *
     */
    public void finishIndexing() {
        this.hits.setText("searching...");
    }

    /**
     * @param searchEngine
     *            The searchEngine to set.
     */
    public void setSearchEngine(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }
}
