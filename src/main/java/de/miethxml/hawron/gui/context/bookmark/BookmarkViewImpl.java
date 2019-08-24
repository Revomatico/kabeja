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

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.File;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.gui.context.ContextViewComponent;

import de.miethxml.toolkit.component.AbstractServiceable;
import de.miethxml.toolkit.gui.LocaleButton;
import de.miethxml.toolkit.gui.LocaleSeparator;

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
public class BookmarkViewImpl extends AbstractServiceable implements
		Initializable, BookmarkView {
	public static String BOOKMARKFILE = "bookmarks.txt";

	private JPanel view;

	private BookmarkManager bookmarkManager;

	private JList list;

	private BookmarkDialog dialog;

	private String configLocation;

	private ContextViewComponent contextview;

	/**
	 * 
	 * 
	 * 
	 */
	public BookmarkViewImpl() {
		super();
		bookmarkManager = new BookmarkManager();
		configLocation = "";
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.toolkit.component.Component#init()
	 * 
	 */
	public void initialize() {
		// generate the view
		FormLayout layout = new FormLayout(
				"fill:pref:grow,2dlu,fill:pref:grow",
				"p,2dlu,fill:5dlu:grow,3dlu,p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		LocaleSeparator separator = new LocaleSeparator("panel.bookmarks.title");
		builder.add(separator, cc.xywh(1, 1, 3, 1));
		list = new JList(bookmarkManager.getListModel());

		// double click open the
		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = list.locationToIndex(e.getPoint());

					if ((index > -1)
							&& (index < bookmarkManager.getBookmarkCount())) {
						setSelectedBookmark(index);
					}
				}
			}
		};

		list.addMouseListener(mouseListener);

		// make KeyListener too
		KeyAdapter keyListener = new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				System.out.println("released");

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					System.out.println("was enter");

					int index = list.getSelectedIndex();
					System.out.println("index=" + index);

					if ((index > -1)
							&& (index < bookmarkManager.getBookmarkCount())) {
						setSelectedBookmark(index);
					}
				}
			}
		};

		list.addKeyListener(keyListener);

		BookmarkTransferHandler dndHandler = new BookmarkTransferHandler(null);
		dndHandler.setBookmarkViewImpl(this);
		list.setDragEnabled(true);
		list.setTransferHandler(dndHandler);

		JScrollPane sp = new JScrollPane(list);
		builder.add(sp, cc.xywh(1, 3, 3, 1));

		LocaleButton button = new LocaleButton("panel.bookmarks.button.add");
		button.setActionCommand("add");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(true);
			}
		});

		builder.add(button, cc.xy(1, 5));

		button = new LocaleButton("panel.bookmarks.button.remove");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = list.getSelectedIndex();
				if ((index > -1)
						&& (index < bookmarkManager.getBookmarkCount())) {
					bookmarkManager.removeBookmark(index);
				}
			}
		});

		builder.add(button, cc.xy(3, 5));

		view = builder.getPanel();

		// build dialog
		dialog = new BookmarkDialog();
		dialog.setBookmarkManager(bookmarkManager);
		dialog.init();
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.toolkit.gui.BookmarkView#getBookmarkView()
	 * 
	 */
	public JComponent getBookmarkView() {
		if (view == null) {
			initialize();
		}

		return view;
	}

	public void addBookmark(Bookmark bm) {
		Point p = view.getLocationOnScreen();
		dialog.setLocation(p);
		dialog.setBookmarkAndShow(bm);
	}

	private void setSelectedBookmark(int index) {
		Bookmark bm = bookmarkManager.getBookmark(index);

		contextview.setBaseLocation(bm.getSource());
	}

	public void setContextViewComponent(ContextViewComponent fsModel) {
		this.contextview = fsModel;
	}

	public void setConfigLocation(String location) {
		// store the old
		if (!configLocation.equals(location) && (configLocation.length() > 0)) {
			bookmarkManager.store(configLocation + File.separator
					+ BOOKMARKFILE);
		}

		if (location.length() > 0) {
			
			bookmarkManager.load(location + File.separator + BOOKMARKFILE);
		} else {
			bookmarkManager.removeAllBookmarks();
		}

		// set the new path
		configLocation = location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.gui.context.bookmark.BookmarkView#setBaseURL(java.lang.String)
	 */
	public void setBaseURL(String baseURL) {
		bookmarkManager.setBaseURL(baseURL);
	}

	private class ActionHandler implements ActionListener {
		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 * 
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("add")) {
				dialog.setVisible(true);
			} else if (e.getActionCommand().equals("remove")) {
				int index = list.getSelectedIndex();

				if ((index > -1)
						&& (index < bookmarkManager.getBookmarkCount())) {
					bookmarkManager.removeBookmark(index);
				}
			}
		}
	}
}
