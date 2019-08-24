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
package de.miethxml.hawron.gui.context.event;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

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
public class FileSystemSelectionController implements ListSelectionListener,
		TreeSelectionListener {
	private JTable fileSystemTable;

	private JTree fileSystemTree;

	private boolean tableExists = false;

	private boolean treeExists = false;

	/**
	 * 
	 * 
	 * 
	 */
	public FileSystemSelectionController() {
		super();
	}

	/**
	 * @param fileSystemTable
	 * 
	 * The fileSystemTable to set.
	 * 
	 */
	public void setFileSystemTable(JTable fileSystemTable) {
		tableExists = true;
		this.fileSystemTable = fileSystemTable;
		fileSystemTable.getSelectionModel().addListSelectionListener(this);
	}

	/**
	 * @param fileSystemTree
	 * 
	 * The fileSystemTree to set.
	 * 
	 */
	public void setFileSystemTree(JTree fileSystemTree) {
		treeExists = true;
		this.fileSystemTree = fileSystemTree;
		fileSystemTree.addTreeSelectionListener(this);
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 * 
	 */
	public void valueChanged(ListSelectionEvent e) {
		// List selection switch off the JTree selection now
		clearTreeSelection();
	}

	/*
	 * 
	 * Clears the selection on JTree and JList of the FileSystemView
	 * 
	 */
	public void clearSelection() {
		clearTableSelection();
		clearTreeSelection();
	}

	private void clearTableSelection() {
		if (tableExists) {
			fileSystemTable.clearSelection();
		}
	}

	private void clearTreeSelection() {
		if (treeExists) {
			if (!fileSystemTree.isSelectionEmpty()) {
				fileSystemTree.clearSelection();
			}
		}
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 * 
	 */
	public void valueChanged(TreeSelectionEvent e) {
		// switch off the list selection
		clearTableSelection();
	}
}
