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
package de.miethxml.toolkit.repository;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.io.FileModel;
import de.miethxml.toolkit.io.FileModelException;
import de.miethxml.toolkit.io.FileModelFilter;
import de.miethxml.toolkit.io.FileModelManager;
import de.miethxml.toolkit.io.FileModelSorter;

/**
 * @author simon
 * 
 * 
 * 
 */
public class RepositoryModelImpl implements TreeSelectionListener,
		RepositoryModel, ListSelectionListener, Reloadable {
	private FileModel base;

	private FileModel currentModel;

	private FileModel selectedModel;

	private ArrayList fsmListener = new ArrayList();

	private ArrayList filters = new ArrayList();

	private ArrayList sorters = new ArrayList();

	private boolean showMixed = false;

	private RepositoryTreeModel treeModel = new RepositoryTreeModel();

	private RepositoryTableModel tableModel = new RepositoryTableModel();

	private FileModel[] currentList = new FileModel[0];

	private FileModelManager manager;

	public RepositoryModelImpl(FileModelManager manager) {
		super();
		this.manager = manager;
	}

	public void setBase(String base) throws FileModelException {

		this.base = manager.createFileModel(base);

		fireUnselectEvent();

		treeModel.fireTreeUpdate();
		currentModel = this.base;
		currentList = currentModel.getChildren();
		tableModel.fireTableUpdate();
	}

	public TreeModel getTreeModel(boolean showMixed) {
		this.showMixed = showMixed;

		return treeModel;
	}

	public TableModel getTableModel() {
		return tableModel;
	}

	public void addRepositorySelectionListener(RepositorySelectionListener l) {
		fsmListener.add(l);
	}

	public void removeRepositorySelectionListener(RepositorySelectionListener l) {
		fsmListener.remove(l);
	}

	public void reload() {
		// reload the current model
		if (!currentModel.exists()) {
			currentList = new FileModel[0];
		} else {
			currentList = currentModel.getChildren();
		}

		treeModel.clearCache();
		tableModel.fireTableUpdate();
		treeModel.fireSelectedNodeUpdate();
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 * 
	 */
	public void valueChanged(ListSelectionEvent e) {
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		int first = lsm.getMinSelectionIndex();

		if (first == 0) {
			// the parent-directory link is selected
			selectedModel = currentModel.getParent();

			return;
		}

		if (first > 0) {
			selectedModel = currentList[first - 1];

			if (!currentList[first - 1].isFile()) {
				fireDirectorySelectedEvent(currentList[first - 1]);
			} else {
				fireFileSelectedEvent(currentList[first - 1]);
			}
		} else {
			// there was no selection
			fireUnselectEvent();
		}
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 * 
	 */
	public void valueChanged(TreeSelectionEvent arg0) {
		TreePath tp = arg0.getPath();
		FileModel fm = (FileModel) tp.getLastPathComponent();
		currentModel = fm;
		selectedModel = fm;
		currentList = currentModel.getChildren();

		if (!currentModel.isFile()) {
			fireDirectorySelectedEvent(fm);
		} else {
			fireFileSelectedEvent(fm);
		}

		tableModel.fireTableUpdate();
	}

	private void fireDirectorySelectedEvent(FileModel directory) {
		for (int i = 0; i < fsmListener.size(); i++) {
			RepositorySelectionListener l = (RepositorySelectionListener) fsmListener
					.get(i);
			l.directorySelected(this, directory);
		}
	}

	private void fireUnselectEvent() {
		for (int i = 0; i < fsmListener.size(); i++) {
			RepositorySelectionListener l = (RepositorySelectionListener) fsmListener
					.get(i);
			l.unselect();
		}
	}

	private void fireFileSelectedEvent(FileModel file) {
		for (int i = 0; i < fsmListener.size(); i++) {
			RepositorySelectionListener l = (RepositorySelectionListener) fsmListener
					.get(i);
			l.fileSelected(this, file);
		}
	}

	public void expandSelectedFileModel() {
		if (selectedModel != null && !selectedModel.isFile()) {
			currentModel = selectedModel;
			currentList = currentModel.getChildren();
			tableModel.fireTableUpdate();

			if (currentModel.getPath().length() < base.getPath().length()) {
				// moved to parent-directory of base -> change the base
				base = currentModel;
			}
		}

	}

	private void filterAndSortFileModelList(FileModel[] list) {
		// apply filters first

		//list = applyFilters(list);
		applySorters(list);

	}

	

	// the TreeModel
	public class RepositoryTreeModel implements TreeModel {
		private ArrayList treelistener = new ArrayList();

		private FileModel cachedModel;

		private FileModel[] cachedList;

		private FileModel[][] totalCachedLists;

		private FileModel[] totalCachedModels;

		private int cacheSize = 3;

		private int lastCacheInsert = -1;

		public RepositoryTreeModel() {
			totalCachedLists = new FileModel[cacheSize][];
			totalCachedModels = new FileModel[cacheSize];
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
		 * 
		 */
		public void addTreeModelListener(TreeModelListener arg0) {
			treelistener.add(arg0);
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
		 * 
		 */
		public Object getChild(Object obj, int index) {
			FileModel fm = (FileModel) obj;

			updateCache(fm);

			if (showMixed) {
				if (index < cachedList.length) {
					return cachedList[index];
				}
			} else {
				int count = 0;

				for (int i = 0; i < cachedList.length; i++) {
					if (!cachedList[i].isFile()) {
						if (count == index) {
							return cachedList[i];
						} else {
							count++;
						}
					}
				}
			}

			return null;
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
		 * 
		 */
		public int getChildCount(Object obj) {
			FileModel fm = (FileModel) obj;
			updateCache(fm);

			if (showMixed) {
				return cachedList.length;
			} else {
				int count = 0;

				for (int i = 0; i < cachedList.length; i++) {
					if (!cachedList[i].isFile()) {
						count++;
					}
				}

				return count;
			}
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,
		 * 
		 * java.lang.Object)
		 * 
		 */
		public int getIndexOfChild(Object p, Object c) {
			FileModel parent = (FileModel) p;
			FileModel child = (FileModel) c;
			updateCache(parent);

			if (showMixed) {
				for (int i = 0; i < cachedList.length; i++) {
					if (cachedList[i].getName().equals(child.getName())) {
						return i;
					}
				}
			} else {
				int index = 0;

				for (int i = 0; i < cachedList.length; i++) {
					if (!cachedList[i].isFile()) {
						if (cachedList[i].getName().equals(child.getName())) {
							return index;
						} else {
							index++;
						}
					}
				}
			}

			return 0;
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.tree.TreeModel#getRoot()
		 * 
		 */
		public Object getRoot() {
			return base;
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
		 * 
		 */
		public boolean isLeaf(Object obj) {
			FileModel fm = (FileModel) obj;

			if (showMixed) {
				return fm.isFile();
			}
			return false;
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
		 * 
		 */
		public void removeTreeModelListener(TreeModelListener l) {
			treelistener.remove(l);
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath,
		 * 
		 * java.lang.Object)
		 * 
		 */
		public void valueForPathChanged(TreePath arg0, Object arg1) {
		}

		public void fireTreeUpdate() {
			clearCache();

			Iterator i = ((ArrayList) treelistener.clone()).iterator();

			while (i.hasNext()) {
				TreeModelListener tml = (TreeModelListener) i.next();
				FileModel[] fm = new FileModel[1];
				fm[0] = base;
				tml.treeStructureChanged(new TreeModelEvent(this, fm));
			}
		}

		private void updateCache(FileModel fm) {
			for (int i = 0; i < cacheSize; i++) {
				if (totalCachedModels[i] == fm) {
					cachedModel = fm;
					cachedList = totalCachedLists[i];

					return;
				}
			}

			// nothing in cache create new entry
			lastCacheInsert++;

			if (lastCacheInsert >= (cacheSize)) {
				lastCacheInsert = 0;
			}

			totalCachedModels[lastCacheInsert] = fm;
			totalCachedLists[lastCacheInsert] = fm.getChildren();
			filterAndSortFileModelList(totalCachedLists[lastCacheInsert]);
			cachedModel = fm;
			cachedList = totalCachedLists[lastCacheInsert];

			return;
		}

		public void clearCache() {
			for (int i = 0; i < cacheSize; i++) {
				totalCachedLists[i] = null;
				totalCachedModels[i] = null;
			}
		}

		public void fireSelectedNodeUpdate() {
			// create the treepath
			ArrayList list = new ArrayList();
			FileModel p = selectedModel;

			if (!p.exists()) {
				p = p.getParent();
			}

			while ((p != null) && (p != base)) {
				list.add(p);

				// go one step up
				p = p.getParent();
			}

			// add the root as last
			list.add(base);
			Collections.reverse(list);

			Object[] path = list.toArray();

			// create the TreeEvent and fire to all listeners
			TreeModelEvent event = new TreeModelEvent(this, path);
			Iterator i = treelistener.iterator();

			while (i.hasNext()) {
				TreeModelListener l = (TreeModelListener) i.next();
				l.treeStructureChanged(event);
			}
		}
	}

	public class RepositoryTableModel implements TableModel {
		private Boolean isFile = new Boolean(true);

		private Boolean isDirectory = new Boolean(false);

		private ArrayList tablelistener = new ArrayList();

		private NumberFormat sizeformat = NumberFormat.getInstance();

		private SimpleDateFormat dateformat = new SimpleDateFormat(
				" EEE, dd. MMM yyyy hh:mm:ss");

		private Date date = new Date();

		private String parentDir = "..";

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
		 * 
		 */
		public void addTableModelListener(TableModelListener arg0) {
			tablelistener.add(arg0);
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getColumnClass(int)
		 * 
		 */
		public Class getColumnClass(int arg0) {
			return "".getClass();
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getColumnCount()
		 * 
		 */
		public int getColumnCount() {
			return 4;
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 * 
		 */
		public String getColumnName(int arg0) {
			switch (arg0) {
			case 0:
				return " ";

			case 1:
				return LocaleImpl.getInstance().getString(
						"panel.filesystem.table.column.name");

			case 2:
				return LocaleImpl.getInstance().getString(
						"panel.filesystem.table.column.size");

			case 3:
				return LocaleImpl.getInstance().getString(
						"panel.filesystem.table.column.modifieddate");
			}

			return "";
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getRowCount()
		 * 
		 */
		public int getRowCount() {

			return currentList.length + 1;
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 * 
		 */
		public Object getValueAt(int row, int column) {
			if (row == 0) {
				switch (column) {
				case 0:
					return isDirectory;

				case 1:
					return parentDir;

				case 2:
					return "";

				case 3:
					return "";
				}
			}

			// the default file
			FileModel fm = currentList[row - 1];

			switch (column) {
			case 0:

				if (fm.isFile()) {
					return isFile;
				} else {
					return isDirectory;
				}

			case 1:
				return fm.getName();

			case 2:

				if (fm.isFile()) {
					return sizeformat.format(fm.getLength());
				} else {
					return "";
				}

			case 3:
				date.setTime(fm.lastModified());

				return dateformat.format(date);
			}

			return "";
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 * 
		 */
		public boolean isCellEditable(int row, int column) {
			if (column == 1 && row > 0) {
				return true;
			}

			return false;
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
		 * 
		 */
		public void removeTableModelListener(TableModelListener l) {
			tablelistener.remove(l);
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int,
		 *      int)
		 * 
		 */
		public void setValueAt(Object obj, int arg1, int arg2) {
			if ((arg2 == 1) && (obj != null)) {
				String name = (String) obj;
				FileModel fm = currentList[arg1 - 1];
				fm.renameTo(name);
			}
		}

		public void fireTableUpdate() {
			filterAndSortFileModelList(currentList);

			Iterator i = ((ArrayList) tablelistener.clone()).iterator();

			while (i.hasNext()) {
				TableModelListener l = (TableModelListener) i.next();
				l.tableChanged(new TableModelEvent(this));
			}
		}
	}

	public void addFileModelFilter(FileModelFilter filter) {
		filters.add(filter);

	}

	public void removeFileModelFilter(FileModelFilter filter) {
		filters.remove(filter);
	}

	private FileModel[] applyFilters(FileModel[] data) {

		if (filters.size() > 0) {
			int count = data.length;
			Iterator i = filters.iterator();
			while (i.hasNext()) {
				FileModelFilter filter = (FileModelFilter) i.next();
				// process all FileModels
				for (int x = 0; x < data.length; x++) {
					if (data[x] != null && !filter.accept(data[x])) {
						count--;
						data[x] = null;
					}
				}

			}

			FileModel[] filtered = new FileModel[count];
			count = 0;
			for (int x = 0; x < data.length; x++) {
				if (data[x] != null) {
					filtered[count] = data[x];

					count++;
				}
			}
			return filtered;
		}

		return data;

	}

	public FileModel getBase() {
		return this.base;
	}

	public void addFileModelSorter(FileModelSorter sorter) {
		sorters.add(sorter);
	}

	public void removeFileModelSorter(FileModelSorter sorter) {
		sorters.remove(sorter);
	}

	private void applySorters(FileModel[] list) {

		Iterator i = sorters.iterator();
		FileModel[] result = list;
		while (i.hasNext()) {
			FileModelSorter sorter = (FileModelSorter) i.next();
			sorter.sort(list);
		}

	}

}