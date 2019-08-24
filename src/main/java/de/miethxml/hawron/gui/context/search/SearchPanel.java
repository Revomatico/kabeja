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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.search.SearchEngineImpl;
import de.miethxml.hawron.search.SearchResultListener;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.gui.LocaleButton;
import de.miethxml.toolkit.gui.LocaleLabel;
import de.miethxml.toolkit.ui.ButtonGradientTitledLabel;
import de.miethxml.toolkit.ui.SmallShadowBorder;


/**
 * @author simon
 *
 *
 *
 */
public class SearchPanel extends JPanel implements SearchResultListener {
    private JTextField query;
    private JButton button;
    private JLabel hits;
    private JCheckBox checkbox;
    private JTable resultTable;
    private SearchEngineImpl searchEngine;
    private boolean interuptSearching;
    private JScrollPane sp;
    private JPopupMenu popupmenu;

    public SearchPanel(SearchEngineImpl searchEngine) {
        super();
        this.searchEngine = searchEngine;
        initialize();
    }

    private void initialize() {
        setBorder(new SmallShadowBorder());
        setLayout(new BorderLayout());

        ButtonGradientTitledLabel bpanel = new ButtonGradientTitledLabel(
                "Search");

        bpanel.addComponent(createPopupMenuButton());

        bpanel.addComponent(new JLabel(new ImageIcon("icons/search_src.gif")),
            SwingConstants.LEFT);
        add(bpanel, BorderLayout.NORTH);

        FormLayout panellayout = new FormLayout("3dlu,fill:pref:grow,2dlu,right:pref,3dlu",
                "3dlu,p,3dlu,fill:0dlu:grow,3dlu");
        CellConstraints cc = new CellConstraints();

        JPanel main = new JPanel(panellayout);
        query = new JTextField(20);
        query.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Thread t = new Thread(new Runnable() {
                                public void run() {
                                    invokeSearch();
                                }
                            });
                    t.start();
                }
            });

        main.add(query, cc.xy(2, 2));

        button = new LocaleButton("common.button.search");
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Thread t = new Thread(new Runnable() {
                                public void run() {
                                    invokeSearch();
                                }
                            });
                    t.start();
                }
            });

        main.add(button, cc.xy(4, 2));

        searchEngine.addSearchResultListener(this);

        resultTable = new JTable(searchEngine.getResultTableModel());
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        resultTable.getSelectionModel().addListSelectionListener(searchEngine);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        renderer.setHorizontalTextPosition(SwingConstants.RIGHT);
        resultTable.getColumnModel().getColumn(0).setCellRenderer(renderer);

        renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
        resultTable.getColumnModel().getColumn(1).setCellRenderer(renderer);

        sp = new JScrollPane(resultTable);
        sp.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                 updateTableSize();
            }
        });


        sp.setVisible(false);
        main.add(sp, cc.xywh(2, 4, 3, 1));

        add(main, BorderLayout.CENTER);
    }

    private void invokeSearch() {
        if (interuptSearching) {
            searchEngine.interruptSearch();
            interuptSearching = false;
        } else {
            query.setEnabled(false);
            searchEngine.search(query.getText());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.search.SearchResultListener#finishSearching(int)
     *
     */
    public void finishSearching(int hits) {
        interuptSearching = false;

        button.setText(LocaleImpl.getInstance().getString("common.button.search"));
        query.setEnabled(true);
        
        updateTableSize();
        sp.setVisible(true);

      
    }
    
    
    

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.search.SearchResultListener#startSearching()
     *
     */
    public void startSearching() {
        button.setText(LocaleImpl.getInstance().getString("common.button.cancel"));
        interuptSearching = true;
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

    private JButton createPopupMenuButton() {
        popupmenu = new JPopupMenu();

        JCheckBoxMenuItem item = new JCheckBoxMenuItem(LocaleImpl.getInstance()
                                                                 .getString("search.label.updateindex"));
        item.setSelected(true);
        item.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        searchEngine.setUpdateIndex(true);
                    } else {
                        searchEngine.setUpdateIndex(false);
                    }

                    validate();
                }
            });
        popupmenu.add(item);

        JButton button = new JButton(new ImageIcon("icons/prefs01.gif"));
        button.setUI(new BasicButtonUI());
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Component c = (Component) e.getSource();
                    int h = (c.getParent().getY() + c.getParent().getHeight())
                        - c.getY() - 1;
                    popupmenu.show(c, 0, h);
                }
            });

        return button;
    }
    
    
    private void updateTableSize(){
        double width = sp.getViewport().getSize().getWidth();
        double columnwidth = resultTable.getColumnModel().getColumn(1).getPreferredWidth();
      
        if(columnwidth<width){
            resultTable.getColumnModel().getColumn(0).setPreferredWidth((int)(width-columnwidth));
            resultTable.getColumnModel().getColumn(1).setPreferredWidth((int)(columnwidth));
        }else{
        resultTable.getColumnModel().getColumn(0).setPreferredWidth((int)(width*0.8));
        resultTable.getColumnModel().getColumn(1).setPreferredWidth((int)(width*0.2));
        }
    }
}
