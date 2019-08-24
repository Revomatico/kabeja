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
package de.miethxml.hawron.gui.context.bookmark;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.toolkit.gui.LocaleButton;
import de.miethxml.toolkit.gui.LocaleLabel;


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
public class BookmarkDialog extends JDialog {
    private JTextField name;
    private JTextField location;
    private BookmarkManager bookmarkManager;

    /**
     * @throws java.awt.HeadlessException
     *
     */
    public BookmarkDialog() throws HeadlessException {
        super();
    }

    public void init() {
        //generate the view
        getContentPane().setLayout(new BorderLayout());

        FormLayout layout = new FormLayout("3dlu,p,3dlu,p:grow,3dlu",
                "3dlu,p,2dlu,p,3dlu");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();
        LocaleLabel label = new LocaleLabel("dialog.bookmark.label.name");
        builder.add(label, cc.xy(2, 2));
        name = new JTextField(20);
        builder.add(name, cc.xy(4, 2));
        label = new LocaleLabel("dialog.bookmark.label.location");
        builder.add(label, cc.xy(2, 4));
        location = new JTextField(20);
        builder.add(location, cc.xy(4, 4));


        JButton[] buttons = new JButton[2];
        
        buttons[0] = new LocaleButton("common.button.ok");
        buttons[0].addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {      
                    addBookmark();
                    setVisible(false);
        	}
        	
        });
        
        
        buttons[1] = new LocaleButton("common.button.cancel");
        buttons[1].addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
                    setVisible(false);
        	}
        });


        ButtonBarBuilder bbuilder = new ButtonBarBuilder();

        //bbuilder.addGridded(button);
        bbuilder.addRelatedGap();
        bbuilder.addGlue();
        bbuilder.addGriddedButtons(buttons);
        getContentPane().add(builder.getPanel(), BorderLayout.CENTER);
        getContentPane().add(bbuilder.getPanel(), BorderLayout.SOUTH);
        pack();
    }

    /**
     * @param bookmarkManager
     *
     * The bookmarkManager to set.
     *
     */
    public void setBookmarkManager(BookmarkManager bookmarkManager) {
        this.bookmarkManager = bookmarkManager;
    }

    private void addBookmark() {
        if ((name.getText().length() > 0) && (location.getText().length() > 0)) {
            Bookmark bookmark = new Bookmark();
            bookmark.setName(name.getText());
            bookmark.setSource(location.getText());
            bookmarkManager.addBookmark(bookmark);
        }
    }

    public void setBookmarkAndShow(Bookmark bm) {
        name.setText(bm.getName());
        location.setText(bm.getSource());
        super.setVisible(true);
    }

    public void setVisible(boolean state) {
        if (state) {
            name.setText("");
            location.setText("");
        }

        super.setVisible(state);
    }

  
}
