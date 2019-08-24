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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.gui.context.action.ActionManagerImpl;
import de.miethxml.hawron.gui.context.bookmark.BookmarkView;
import de.miethxml.hawron.gui.context.bookmark.BookmarkViewImpl;
import de.miethxml.hawron.gui.context.editor.EditorManagerImpl;
import de.miethxml.hawron.gui.context.search.SearchPanel;
import de.miethxml.hawron.gui.context.search.SearchResultPanel;
import de.miethxml.hawron.gui.context.ui.ButtonPanelSelectionModel;
import de.miethxml.hawron.gui.context.ui.ExtensionCheckSelectionModel;
import de.miethxml.hawron.gui.context.viewer.ViewerManagerImpl;
import de.miethxml.hawron.search.SearchEngineImpl;
import de.miethxml.hawron.search.SearchResultListener;
import de.miethxml.toolkit.component.GuiConfigurable;
import de.miethxml.toolkit.conf.ConfigManager;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.io.DefaultFileModelHandler;
import de.miethxml.toolkit.io.FileModel;
import de.miethxml.toolkit.io.FileModelException;
import de.miethxml.toolkit.io.FileModelHandler;
import de.miethxml.toolkit.io.FileModelManager;
import de.miethxml.toolkit.plugins.PluginManager;
import de.miethxml.toolkit.repository.Reloadable;
import de.miethxml.toolkit.repository.RepositoryModel;
import de.miethxml.toolkit.repository.RepositoryModelImpl;
import de.miethxml.toolkit.repository.RepositorySelectionListener;
import de.miethxml.toolkit.repository.ui.RepositoryViewBuilder;
import de.miethxml.toolkit.setup.ApplicationSetup;
import de.miethxml.toolkit.ui.PanelFactory;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 * 
 * 
 * 
 */
public class ContextViewComponentImpl implements RepositorySelectionListener,
		Serviceable, Initializable, Disposable, GuiConfigurable,
		ContextViewComponent, Configurable, LogEnabled {
	private ServiceManager manager;

	private JButton edit;

	private JTable table;

	private JTextField currentPathField;

	private SearchResultPanel searchResult;

	private JSplitPane vsplit;

	private JPopupMenu popupmenu;

	private boolean resultView = false;

	private boolean taskView = false;

	private boolean projectView = true;

	private JPanel buttonpanel;

	private JPanel mainpanel;

	private String directoryCache = "";

	private JPanel dockComponent;

	protected String contextBase = "";

	protected Logger logger;

	protected JComboBox destinationList;

	protected JComboBox taskBuildDir;

	private JPanel view;

	protected boolean initialized = false;

	// own components
	protected RepositoryModel fsModel;

	protected PluginManager pluginManager;

	protected SearchEngineImpl searchEngine;

	protected EditorManagerImpl editmanager;

	protected ViewerManagerImpl viewermanager;

	protected ActionManagerImpl actionmanager;

	protected BookmarkView bookmarkView;

	protected ApplicationSetup appSetup;

	protected ContextSetup setup;

	protected FileModelManager fileModelManager = new FileModelManager();

	protected boolean showingTree = false;

	protected boolean showingTable = false;

	/**
	 * 
	 * 
	 * 
	 */
	public ContextViewComponentImpl() {
		this("");
	}

	public ContextViewComponentImpl(String baseDir) {
		super();
		this.contextBase = baseDir;
	}

	public void initialize() {
	
		init();
	}

	public void setBaseLocation(String uri) {
		if (!directoryCache.equals(uri)) {
			currentPathField.setText(uri);

			try {
				fsModel.setBase(uri);
				directoryCache = uri;
			} catch (FileModelException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
						JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.gui.event.FileSystemSelectionListener#directorySelected(de.miethxml.gui.io.FileSystemModel,
	 * 
	 * java.lang.String)
	 * 
	 */
	public void directorySelected(Reloadable model, FileModel fm) {
		currentPathField.setText(fm.getPath());
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.gui.event.FileSystemSelectionListener#fileSelected(de.miethxml.gui.io.FileSystemModel,
	 * 
	 * java.lang.String)
	 * 
	 */
	public void fileSelected(Reloadable model, FileModel fm) {
		currentPathField.setText(fm.getPath());
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.toolkit.component.Component#setComponentManager(de.miethxml.toolkit.component.ComponentManager)
	 * 
	 */
	public void service(ServiceManager manager) throws ServiceException {
		this.manager = manager;

		try {
			pluginManager = (PluginManager) manager.lookup(PluginManager.ROLE);
			appSetup = (ApplicationSetup) manager.lookup(ApplicationSetup.ROLE);
			
			fileModelManager.setPluginManager(pluginManager);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void showButtonPanels(boolean state) {
		if (Boolean.valueOf(
				ConfigManager.getInstance().getProperty(
						"view.context.showbuttonpanel")).booleanValue() != state) {
			ConfigManager.getInstance().setProperty(
					"view.context.showbuttonpanel", Boolean.toString(state));
		}

		buttonpanel.setVisible(state);
	}

	public boolean isShowButtonPanels() {
		return buttonpanel.isVisible();
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.gui.project.ProjectViewComponent#getLabel(java.lang.String)
	 * 
	 */
	public String getLabel(String lang) {
		return LocaleImpl.getInstance().getString("view.project.tab.source");
	}

	public Icon getIcon() {
		return null;
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.gui.project.ProjectViewComponent#getViewComponent()
	 * 
	 */
	public JComponent getViewComponent() {
		return vsplit;
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.gui.project.ProjectViewComponent#setProject(de.miethxml.project.Project)
	 * 
	 */

	public void init() {

		fsModel = new RepositoryModelImpl(fileModelManager);

		fsModel.addRepositorySelectionListener(this);
		searchEngine = new de.miethxml.hawron.search.SearchEngineImpl();
		bookmarkView = new BookmarkViewImpl();
		setBaseLocation(contextBase);

		popupmenu = new JPopupMenu();

		// TODO change the locales
		// the locationpanel
		FormLayout panellayout = new FormLayout(
				"3dlu,pref,3dlu,pref,3dlu,fill:10dlu:grow,3dlu,pref,3dlu",
				"3dlu,p,3dlu");
		PanelBuilder builder = new PanelBuilder(panellayout);
		CellConstraints ccp = new CellConstraints();

		// the home button
		JButton button = new JButton(new ImageIcon("icons/home.gif"));

		button.setBorder(BorderFactory.createEmptyBorder());
		button.setToolTipText("Context-Home");
		button.setFocusPainted(false);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						directoryCache = "";
						setBaseLocation(contextBase);
					}
				});
			}
		});

		builder.add(button, ccp.xy(2, 2));

		builder.addLabel("Location:", ccp.xy(4, 2));

		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						setBaseLocation(currentPathField.getText());
					}
				});
			}
		};

		currentPathField = new JTextField(40);
		currentPathField.addActionListener(action);
		builder.add(currentPathField, ccp.xy(6, 2));

		JButton go = new JButton("Go");
		go.addActionListener(action);
		builder.add(go, ccp.xy(8, 2));

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(builder.getPanel(), BorderLayout.NORTH);

		// the resourceview and the editors,views,actions
		panellayout = new FormLayout("3dlu,80dlu:grow,6dlu,pref,3dlu",
				"3dlu,fill:60dlu:grow,3dlu");

		builder = new PanelBuilder(panellayout);
		ccp = new CellConstraints();

		// the buttonbox
		FormLayout buttonlayout = new FormLayout("80dlu",
				"fill:12dlu:grow,3dlu,fill:12dlu:grow,3dlu,fill:20dlu:grow");
		PanelBuilder buttonbuilder = new PanelBuilder(buttonlayout);
		CellConstraints ccbutton = new CellConstraints();

		// the editorpanel
		editmanager = new EditorManagerImpl(pluginManager, searchEngine,
				fsModel, appSetup);

		editmanager.initialize();
		popupmenu.add(editmanager.getEditorMenu());

		JComponent editorpanel = editmanager.getButtonPanel();
		buttonbuilder.add(editorpanel, ccbutton.xy(1, 1));

		// the viewerpanel
		viewermanager = new ViewerManagerImpl(pluginManager, searchEngine,
				fsModel, appSetup);
		viewermanager.initialize();

		popupmenu.add(viewermanager.getViewerMenu());

		buttonbuilder.add(viewermanager.getButtonPanel(), ccbutton.xy(1, 3));

		// ActionPanel
		actionmanager = new ActionManagerImpl(pluginManager, searchEngine,
				fsModel, appSetup);
		actionmanager.initialize();
		popupmenu.add(actionmanager.getActionMenu());

		JComponent actionpanel = actionmanager.getButtonPanel();
		buttonbuilder.add(actionpanel, ccbutton.xy(1, 5));
		buttonpanel = buttonbuilder.getPanel();

		buttonpanel.setPreferredSize(buttonpanel.getPreferredSize());

		actionpanel.setPreferredSize(buttonpanel.getPreferredSize());
		editorpanel.setPreferredSize(buttonpanel.getPreferredSize());
		buttonpanel.setPreferredSize(buttonpanel.getPreferredSize());
		builder.add(buttonpanel, ccp.xy(4, 2));

		// the repository view
		RepositoryViewBuilder rview = new RepositoryViewBuilder(
				(RepositoryModelImpl) fsModel, popupmenu);
		if (showingTable && showingTree) {

			// a tree with directories and a table with files and directories
			builder.add(rview.getTreeAndTableView(), ccp.xy(2, 2));

		} else if (showingTable) {
			JScrollPane scroll = new JScrollPane(rview.getRepositoryTable());
			scroll.getViewport().setBackground(Color.white);
			scroll.getViewport().setOpaque(true);

			// only table of files and directories
			builder.add(scroll, ccp.xy(2, 2));
		} else if (showingTree) {
			// show only the tree with files and directories
			builder.add(new JScrollPane(rview.getRepositoryTree(true)), ccp.xy(
					2, 2));
		}

		// new test
		vsplit = PanelFactory
				.createOneTouchSplitPane(JSplitPane.VERTICAL_SPLIT);
		vsplit.setDividerSize(10);

		mainpanel = builder.getPanel();
		mainpanel.setPreferredSize(mainpanel.getPreferredSize());
		panel.add(mainpanel, BorderLayout.CENTER);

		// create a titled panel with popupmenu
		JPopupMenu popupmenu = new JPopupMenu();
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(LocaleImpl.getInstance()
				.getString("view.context.setup.checkbox.showbuttonpanel"));
		item.setModel(new ButtonPanelSelectionModel(this));
		popupmenu.add(item);

		item = new JCheckBoxMenuItem(LocaleImpl.getInstance().getString(
				"view.context.setup.checkbox.checkextensions"));
		item.setModel(new ExtensionCheckSelectionModel(this));
		popupmenu.add(item);

		JPanel context = PanelFactory.createTitledPanel(panel, "Context",
				"icons/text_edit.gif", popupmenu);

		context.setMinimumSize(new Dimension(0, 0));

		vsplit.setTopComponent(context);

		JPanel searchPanel = new SearchPanel(searchEngine);
		searchPanel.setPreferredSize(searchPanel.getPreferredSize());
		vsplit.setBottomComponent(searchPanel);

		// switch on/off of the searchResultpanel
		searchEngine.addSearchResultListener(new SearchResultListener() {
			public void startSearching() {
			}

			public void finishIndexing() {
			}

			public void finishSearching(int hits) {
				if (hits > 0) {
					vsplit.setResizeWeight(0.5);

					// the to the lowest size of the filesystem-view
					vsplit.setDividerLocation((int) (vsplit.getTopComponent()
							.getPreferredSize().getHeight()));
				}
			}
		});

		// this avoid an auto-resize and gives a little more space
		Dimension dim = vsplit.getPreferredSize();
		vsplit.setPreferredSize(new Dimension((int) (dim.getWidth() * 1.1),
				(int) (dim.getHeight() * 1.3)));

		setCheckSupportedExtensions(Boolean.valueOf(
				ConfigManager.getInstance().getProperty(
						"view.context.checkextensions", "true")).booleanValue());
		showButtonPanels(Boolean.valueOf(
				ConfigManager.getInstance().getProperty(
						"view.context.showbuttonpanel", "true")).booleanValue());

		// only the top-component should auto-resize
		vsplit.setResizeWeight(1.0);
		initialized = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.gui.event.FileSystemSelectionListener#unselect()
	 * 
	 */
	public void unselect() {
		currentPathField.setText("");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.gui.project.ProjectViewComponent#setEnabled(boolean)
	 */
	public void setEnabled(boolean state) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.avalon.framework.activity.Disposable#dispose()
	 */
	public void dispose() {
		editmanager.dispose();
		viewermanager.dispose();
		actionmanager.dispose();

		// this invoke the bookmarkmanager to
		// save the bookmarks
		bookmarkView.setConfigLocation("");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.toolkit.component.GuiConfigurable#getLabel()
	 */
	public String getLabel() {
		return "ContextView";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.toolkit.component.GuiConfigurable#getSetupComponent()
	 */
	public JComponent getSetupComponent() {
		if (setup == null) {
			setup = new ContextSetup(this);
			setup.init();
			setup.setSearchEngine(searchEngine);
		}

		return setup.getView();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.toolkit.component.Configurable#isSetup()
	 */
	public boolean isSetup() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.toolkit.component.Configurable#setup()
	 */
	public void setup() {
		// TODO change this
		// the setup maybe null because, no one
		// changed the settings
		if (setup != null) {
			setup.update();
		}
	}

	public void setCheckSupportedExtensions(boolean b) {
		if (Boolean.valueOf(
				ConfigManager.getInstance().getProperty(
						"view.context.checkextensions")).booleanValue() != b) {
			ConfigManager.getInstance().setProperty(
					"view.context.checkextensions", Boolean.toString(b));
		}

		editmanager.setCheckSupportedExtensions(b);
		viewermanager.setCheckSupportedExtensions(b);
		actionmanager.setCheckSupportedExtensions(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration conf) throws ConfigurationException {

	
		if (conf.getChild("handlers") != null) {

			Configuration[] handlers = conf.getChild("handlers").getChildren(
					"handler");

			if (handlers != null && handlers.length > 0) {
				for (int i = 0; i < handlers.length; i++) {
					try {
						String classname = handlers[i].getAttribute("class");
						Class clazz = this.getClass().getClassLoader()
								.loadClass(classname);
						FileModelHandler handler = (FileModelHandler) clazz
								.newInstance();
						if (logger.isDebugEnabled()) {
							logger
									.debug("adding FileModelHandler:"
											+ classname);
						}
						fileModelManager.addFileModelHandler(handler);
					} catch (Exception e) {
						logger.error("configure: adding FileModelHandler", e);
						e.printStackTrace();
					}

				}
			}
		} else {
			// the default: only local filesystem
			fileModelManager.addFileModelHandler(new DefaultFileModelHandler());
		}

		// the view settings
		Configuration config = conf.getChild("repository-tree");
		if (config != null) {
			try {
				setShowingTree(config.getAttributeAsBoolean("enabled"));
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		config = conf.getChild("repository-table");
		if (config != null) {
			try {
				setShowingTable(config.getAttributeAsBoolean("enabled"));
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger log) {
		this.logger = log;

	}

	/**
	 * @return Returns the showingTable.
	 */
	public boolean isShowingTable() {
		return showingTable;
	}

	/**
	 * @param showingTable
	 *            The showingTable to set.
	 */
	public void setShowingTable(boolean showingTable) {
		this.showingTable = showingTable;
	}

	/**
	 * @return Returns the showingTree.
	 */
	public boolean isShowingTree() {
		return showingTree;
	}

	/**
	 * @param showingTree
	 *            The showingTree to set.
	 */
	public void setShowingTree(boolean showingTree) {
		this.showingTree = showingTree;
	}
}
