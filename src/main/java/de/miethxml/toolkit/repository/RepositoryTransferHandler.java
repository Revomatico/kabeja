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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.TransferHandler;

import de.miethxml.toolkit.io.FileModel;
import de.miethxml.toolkit.io.FileModelFlavor;
import de.miethxml.toolkit.repository.ui.RepositoryImportView;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 * 
 * 
 * 
 */
public class RepositoryTransferHandler extends TransferHandler implements
		RepositorySelectionListener, ActionListener {
	private RepositoryImportView view;

	private boolean droptargetTree = true;

	private FileModel selectedModel;

	private RepositoryModel model;

	public static String ACTION_IMPORT = "repository.import";

	public static String ACTION_PASTE = "repository.clipboard.paste";


	public RepositoryTransferHandler(RepositoryModel model) {
		super(null);
		view = new RepositoryImportView();
		view.initialize();
		this.model = model;
		model.addRepositorySelectionListener(this);
	}

	public boolean importData(JComponent c, Transferable t) {

		// support for FileModel[]
		DataFlavor flavor = getFileModelArrayFlavor();
		if (t.isDataFlavorSupported(flavor)) {
			try {
				FileModel[] modelArray = (FileModel[]) t
						.getTransferData(flavor);
		
				Point p = c.getLocationOnScreen();
				int x = (int) p.getX() + (int) (c.getWidth() / 2);
				int y = (int) p.getY() + (int) (c.getHeight() / 2);
				view.setDialogLocation(x, y);

				// invoke GUI
				view.importFileModel(modelArray, selectedModel, model);
				return true;

			} catch (UnsupportedFlavorException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		List files = getFileList(t);

		if (files.size() > 0) {
			// set the location
			Point p = c.getLocationOnScreen();
			int x = (int) p.getX() + (int) (c.getWidth() / 2);
			int y = (int) p.getY() + (int) (c.getHeight() / 2);
			view.setDialogLocation(x, y);

			// invoke GUI
			view.importFileList(files, selectedModel, model);
			return true;
		}

		return false;
	}

	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		for (int i = 0; i < flavors.length; i++) {
			if (flavors[i].isFlavorJavaFileListType()) {
				return true;
			}
		}

		// for linux, there is the FileListFlavor not supported
		for (int i = 0; i < flavors.length; i++) {
			if (flavors[i].isFlavorTextType()) {
				return true;
			}
		}

		// support for FileModel[]
		DataFlavor flavor = getFileModelArrayFlavor();

		// for the FileModelFlavor
		for (int i = 0; i < flavors.length; i++) {
			if (flavors[i].equals(flavor)) {

				return true;
			}
		}

		return false;
	}

	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}

	protected Transferable createTransferable(JComponent c) {
		FileListTransferable trans = null;

		if (selectedModel != null) {
			trans = new FileListTransferable();

			File f = new File(selectedModel.getPath());

			if (f.exists()) {
				List files = new ArrayList();
				files.add(f);
				trans.setFileList(files);
			}

			trans.setFileModelArray(new FileModel[] { selectedModel });
		}

		return trans;
	}

	/**
	 * @return Returns the droptargetTree.
	 */
	public boolean isDroptargetTree() {
		return droptargetTree;
	}

	/**
	 * @param droptargetTree
	 *            The droptargetTree to set.
	 */
	public void setDroptargetTree(boolean droptargetTree) {
		this.droptargetTree = droptargetTree;
	}

	public void directorySelected(Reloadable model, FileModel directory) {
		this.selectedModel = directory;
	}

	public void fileSelected(Reloadable model, FileModel file) {
		this.selectedModel = file;
	}

	public void unselect() {
		this.selectedModel = null;
	}

	public class FileListTransferable implements Transferable {
		private DataFlavor[] flavors;

		private List files;

		private FileModel[] models;

		public FileListTransferable() {

			flavors = new DataFlavor[] { DataFlavor.javaFileListFlavor,
					getFileModelArrayFlavor() };

		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
		 * 
		 */
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {

			try {
				if (flavor.equals(new FileModelFlavor())) {
					return models;
				}
			} catch (ClassNotFoundException e) {

				e.printStackTrace();
			}

			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}

			return files;
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
		 * 
		 */
		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		/*
		 * 
		 * (non-Javadoc)
		 * 
		 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
		 * 
		 */
		public boolean isDataFlavorSupported(DataFlavor flavor) {
		
			if (flavor.equals(getFileModelArrayFlavor())) {
				return true;
			}

			if (flavor.equals(DataFlavor.javaFileListFlavor)) {
				return true;
			}

			return false;
		}

		public void setFileList(List files) {
			this.files = files;
		}

		public void setFileModelArray(FileModel[] array) {
			this.models = array;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(ACTION_PASTE)) {
			Thread t = new Thread(new Runnable() {

				public void run() {

					// import data from clipboard
					Clipboard clipboard = Toolkit.getDefaultToolkit()
							.getSystemClipboard();
					Transferable t = clipboard.getContents(null);
					if (t != null) {

						List files = getFileList(clipboard.getContents(null));

						if (files.size() > 0) {

							// set the location center on screen
							Dimension dim = Toolkit.getDefaultToolkit()
									.getScreenSize();
							int x = (int) (dim.getWidth() / 2);
							int y = (int) (dim.getHeight() / 2);
							view.setDialogLocation(x, y);

							// invoke GUI
							view.importFileList(files, selectedModel, model);
						}
					}

				}
			});
			t.start();

		} else if (e.getActionCommand().equals(ACTION_IMPORT)) {
			Thread t = new Thread(new Runnable() {

				public void run() {

					JFileChooser fc = new JFileChooser();

					int choise = fc.showDialog(null, "Import");
					if (choise == JFileChooser.APPROVE_OPTION) {
						List files = new ArrayList();
						files.add(fc.getSelectedFile());

						// set the location center on screen
						Dimension dim = Toolkit.getDefaultToolkit()
								.getScreenSize();
						int x = (int) (dim.getWidth() / 2);
						int y = (int) (dim.getHeight() / 2);
						view.setDialogLocation(x, y);

						// invoke GUI
						view.importFileList(files, selectedModel, model);
					}
				}

			});
			t.start();
		}

	}

	private List getFileList(Transferable t) {
		ArrayList files = new ArrayList();

		if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			try {
				List list = (List) t
						.getTransferData(DataFlavor.javaFileListFlavor);

				if (list.size() > 0) {
					Iterator i = list.iterator();

					while (i.hasNext()) {
						File file = (File) i.next();

						files.add(file);
					}

				}

			} catch (UnsupportedFlavorException ufe) {
				ufe.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			// this is for linux and will only works with Nautilus/Gnome
			// filemanager
			// KDE/Konqueror will not work
			try {
				String text = (String) t
						.getTransferData(DataFlavor.stringFlavor);
				String[] list = text.split("\n");

				if (list.length > 0) {

					for (int i = 0; i < list.length; i++) {
						File f = new File(list[i]);
						if (f.exists()) {
							files.add(f);
						} else if (list[i].startsWith("file:")) {
							try {
								f = new File(new URI(list[i].trim()));
								files.add(f);
							} catch (URISyntaxException e1) {

								e1.printStackTrace();
							}
						}
					}

				}
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return files;
	}

	private DataFlavor getFileModelArrayFlavor() {
		DataFlavor flavor = null;
		try {
			flavor = new FileModelFlavor();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return flavor;
	}

}