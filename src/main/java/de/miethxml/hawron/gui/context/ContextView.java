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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.gui.context.FileImportView;
import de.miethxml.hawron.gui.context.action.ActionManagerImpl;
import de.miethxml.hawron.gui.context.bookmark.BookmarkView;
import de.miethxml.hawron.gui.context.bookmark.BookmarkViewImpl;
import de.miethxml.hawron.gui.context.editor.EditorManagerImpl;
import de.miethxml.hawron.gui.context.search.SearchPanel;
import de.miethxml.hawron.gui.context.search.SearchResultPanel;
import de.miethxml.hawron.gui.context.ui.ButtonPanelSelectionModel;
import de.miethxml.hawron.gui.context.ui.ExtensionCheckSelectionModel;
import de.miethxml.hawron.gui.context.viewer.ViewerManagerImpl;
import de.miethxml.hawron.gui.project.ProjectViewComponent;
import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.search.SearchEngineImpl;
import de.miethxml.hawron.search.SearchResultListener;

import de.miethxml.toolkit.component.GuiConfigurable;
import de.miethxml.toolkit.conf.ConfigManager;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.io.FileModel;
import de.miethxml.toolkit.io.FileModelException;
import de.miethxml.toolkit.plugins.PluginManager;
import de.miethxml.toolkit.repository.Reloadable;
import de.miethxml.toolkit.repository.RepositoryModel;
import de.miethxml.toolkit.repository.RepositoryModelImpl;
import de.miethxml.toolkit.repository.RepositorySelectionController;
import de.miethxml.toolkit.repository.RepositorySelectionListener;
import de.miethxml.toolkit.setup.ApplicationSetup;
import de.miethxml.toolkit.ui.PanelFactory;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *  @deprecated
 */
public class ContextView implements RepositorySelectionListener, Serviceable,
    Initializable, ProjectViewComponent, Disposable, GuiConfigurable,
    ContextViewComponent {
    private ServiceManager manager;
    private String baseDir;
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
    private FileImportView fileImportView;
    private ContextView self;
    private FileTransferHandler dndHandler;
    private JPanel view;

    //own components
    private FileSystemModel fsModel;
    private PluginManager pluginManager;
    private SearchEngineImpl searchEngine;
    private EditorManagerImpl editmanager;
    private ViewerManagerImpl viewermanager;
    private ActionManagerImpl actionmanager;
    private BookmarkView bookmarkView;
    private ApplicationSetup appSetup;
    private ContextSetup setup;
  

    /**
     *
     *
     *
     */
    public ContextView() {
        this("");
    }

    public ContextView(String baseDir) {
        super();
        this.baseDir = baseDir;
        self = this;
        fsModel = new FileSystemModel(this.baseDir);

//        try {
//            //model.setBase(this.baseDir);
//        } catch (FileModelException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
//                JOptionPane.WARNING_MESSAGE);
//        }

        searchEngine = new SearchEngineImpl();
        bookmarkView = new BookmarkViewImpl();
    }

    public void initialize() {
        init();
    }

    public void setBaseLocation(String dir) {
        if (!directoryCache.equals(dir)) {
            fsModel.setBase(dir);
            fsModel.reload();
            currentPathField.setText("");
            directoryCache = dir;
        }

//        try {
//            model.setBase(dir);
//        } catch (FileModelException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
//                JOptionPane.WARNING_MESSAGE);
//        }
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
    public void directorySelected(
        Reloadable model,
        FileModel directory) {
        currentPathField.setText(fsModel.getCurrentDirectory());
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
    public void fileSelected(
        Reloadable model,
        FileModel file) {
        currentPathField.setText(fsModel.getCurrentDirectory());
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
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public void showButtonPanels(boolean state) {
        if (Boolean.valueOf(ConfigManager.getInstance().getProperty("view.context.showbuttonpanel"))
                       .booleanValue() != state) {
            ConfigManager.getInstance().setProperty("view.context.showbuttonpanel",
                Boolean.toString(state));
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
     * @see de.miethxml.gui.project.ProjectViewComponent#getDockComponents()
     *
     */
    public JComponent getDockComponent() {
        if (dockComponent == null) {
            return buildDockComponent();
        }

        return dockComponent;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.ProjectViewComponent#getKey()
     *
     */
    public String getKey() {
        return "panel.contextview";
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
    public void setProject(Project project) {
        String contextPath = project.getCocoonBeanConfiguration().getContextDir();

        if (contextPath != null) {
            setBaseLocation(project.getContextPath());
            searchEngine.setSearchRootPath(project.getCocoonBeanConfiguration()
                                                  .getContextDir());
            searchEngine.setConfigLocation(project.getConfigLocation());
            bookmarkView.setConfigLocation(project.getConfigLocation());
            bookmarkView.setBaseURL(project.getCocoonBeanConfiguration()
                                           .getContextDir());
        }
    }

    private JPanel buildDockComponent() {
        dockComponent = new JPanel();

        FormLayout panellayout = new FormLayout("3dlu,74dlu:grow,3dlu",
                "3dlu,top:pref,9dlu,top:pref:grow,3dlu,fill:pref:grow,3dlu,bottom:pref,3dlu");
        dockComponent.setLayout(panellayout);

        CellConstraints ccp = new CellConstraints();
        JLabel iconlabel = new JLabel(new ImageIcon("icons/editing.png"));
        dockComponent.add(iconlabel, ccp.xy(2, 2));

        bookmarkView.setContextViewComponent(this);
        bookmarkView.initialize();
        dockComponent.add(bookmarkView.getBookmarkView(), ccp.xy(2, 4));

        return dockComponent;
    }

    public File getSelectedFile() {
        return fsModel.getSelectedFile();
    }

    public void init() {
        popupmenu = new JPopupMenu();

        //TODO change the locales
        //the locationpanel
        FormLayout panellayout = new FormLayout("3dlu,pref,3dlu,fill:pref:grow,3dlu,pref,3dlu",
                "3dlu,p,3dlu");
        PanelBuilder builder = new PanelBuilder(panellayout);
        CellConstraints ccp = new CellConstraints();
        builder.addLabel("Location:", ccp.xy(2, 2));

        ActionListener action = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //model.setBase(currentPathField.getText());
                    fsModel.setBase(currentPathField.getText());
                }
            };

        currentPathField = new JTextField(40);
        currentPathField.addActionListener(action);
        builder.add(currentPathField, ccp.xy(4, 2));

        JButton go = new JButton("go");
        go.addActionListener(action);
        builder.add(go, ccp.xy(6, 2));

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(builder.getPanel(), BorderLayout.NORTH);

        //the resourceview and the editors,views,actions
        panellayout = new FormLayout("3dlu,pref:grow,9dlu,pref,3dlu",
                "3dlu,fill:250dlu:grow,3dlu");
        builder = new PanelBuilder(panellayout);
        ccp = new CellConstraints();

        JSplitPane sp = PanelFactory.createDefaultSplitPane();
        sp.setDividerLocation(200);

        UIManager.put("Tree.leafIcon", new ImageIcon("icons/dir.gif"));
        UIManager.put("Tree.closedIcon", new ImageIcon("icons/dir.gif"));
        UIManager.put("Tree.openIcon", new ImageIcon("icons/opendir.gif"));
        fsModel.addRepositorySelectionListener(this);

        //initialize the Drag and Drop handler
        dndHandler = new FileTransferHandler(null);
        dndHandler.setContextView(this);

        FileImportView importView = new FileImportView(fsModel);
        importView.initialize();
        dndHandler.setView(importView);

        RepositorySelectionController selectionController = new RepositorySelectionController();

        JTree tree = new JTree(fsModel);
        tree.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        tree.setDragEnabled(true);
        tree.setTransferHandler(dndHandler);
        selectionController.setFileSystemTree(tree);
        tree.setSize(400, 550);
        tree.addTreeSelectionListener(fsModel);

        //tree.setMinimumSize(new Dimension(300, 500));
        JScrollPane scroll = new JScrollPane(tree);

        //scroll.setSize(400, 500);
        sp.setLeftComponent(scroll);

        //the context-table
        table = new JTable(fsModel);
        table.setDragEnabled(true);
        table.setTransferHandler(dndHandler);
        selectionController.setFileSystemTable(table);
        table.getSelectionModel().addListSelectionListener(fsModel);
        table.setShowVerticalLines(false);

        TableColumnModel tcm = table.getColumnModel();
        TableColumn column = tcm.getColumn(0);
        column.setCellRenderer(new FileTableCellRenderer());
        column.setPreferredWidth(16);
        column.setMaxWidth(16);
        column.setMinWidth(16);
        table.setRowHeight(16);

        MouseController controller = new MouseController();
        table.addMouseListener(controller);
        table.addMouseMotionListener(controller);
        tree.addMouseListener(controller);
        tree.addMouseMotionListener(controller);

        JScrollPane scroll2 = new JScrollPane(table);
        scroll2.getViewport().setBackground(Color.white);
        scroll2.getViewport().setOpaque(true);
        sp.setRightComponent(scroll2);
        builder.add(sp, ccp.xy(2, 2));

        //the buttonbox
        FormLayout buttonlayout = new FormLayout("90dlu",
                "fill:20dlu:grow,9dlu,fill:20dlu:grow,9dlu,fill:20dlu:grow");
        PanelBuilder buttonbuilder = new PanelBuilder(buttonlayout);
        CellConstraints ccbutton = new CellConstraints();

        //the editorpanel
        editmanager = new EditorManagerImpl();
        editmanager.setRepositoryModel(fsModel);
        editmanager.setSearchEngine(searchEngine);
        editmanager.setPluginManager(pluginManager);
        editmanager.setApplicationSetup(appSetup);
        editmanager.initialize();
        popupmenu.add(editmanager.getEditorMenu());

        JComponent editorpanel = editmanager.getButtonPanel();
        buttonbuilder.add(editorpanel, ccbutton.xy(1, 1));

        //the viewerpanel
        viewermanager = new ViewerManagerImpl();

        viewermanager.setRepositoryModel(fsModel);

        viewermanager.setSearchEngine(searchEngine);

        viewermanager.setPluginManager(pluginManager);

        viewermanager.setApplicationSetup(appSetup);

        viewermanager.initialize();

        popupmenu.add(viewermanager.getViewerMenu());

        buttonbuilder.add(viewermanager.getButtonPanel(), ccbutton.xy(1, 3));

        //ActionPanel
        actionmanager = new ActionManagerImpl();
        actionmanager.setRepositoryModel(fsModel);
        actionmanager.setSearchEngine(searchEngine);
        actionmanager.setPluginManager(pluginManager);
        actionmanager.setApplicationSetup(appSetup);
        actionmanager.initialize();
        popupmenu.add(actionmanager.getActionMenu());

        JComponent actionpanel = actionmanager.getButtonPanel();
        buttonbuilder.add(actionpanel, ccbutton.xy(1, 5));
        buttonpanel = buttonbuilder.getPanel();

        actionpanel.setPreferredSize(buttonpanel.getMinimumSize());
        editorpanel.setPreferredSize(buttonpanel.getMinimumSize());
        builder.add(buttonpanel, ccp.xy(4, 2));

        //new test
        vsplit = PanelFactory.createOneTouchSplitPane(JSplitPane.VERTICAL_SPLIT);
        vsplit.setDividerSize(10);

        mainpanel = builder.getPanel();
        panel.add(mainpanel, BorderLayout.CENTER);

        //create a titled panel with popupmenu
        JPopupMenu popupmenu = new JPopupMenu();
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(LocaleImpl.getInstance()
                                                                 .getString("view.context.setup.checkbox.showbuttonpanel"));
        item.setModel(new ButtonPanelSelectionModel(this));
        popupmenu.add(item);

        item = new JCheckBoxMenuItem(LocaleImpl.getInstance().getString("view.context.setup.checkbox.checkextensions"));
        item.setModel(new ExtensionCheckSelectionModel(this));
        popupmenu.add(item);

        JPanel context = PanelFactory.createTitledPanel(panel, "Context",
                "icons/text_edit.gif", popupmenu);

        context.setMinimumSize(new Dimension(0, 0));

        vsplit.setTopComponent(context);

        JPanel searchPanel = new SearchPanel(searchEngine);
        searchPanel.setPreferredSize(searchPanel.getPreferredSize());
        vsplit.setBottomComponent(searchPanel);

        //switch on/off of the searchResultpanel
        searchEngine.addSearchResultListener(new SearchResultListener() {
                public void startSearching() {
                }

                public void finishIndexing() {
                }

                public void finishSearching(int hits) {
                    if (hits > 0) {
                        vsplit.setResizeWeight(0.5);

                        //the to the lowest size of the filesystem-view
                        vsplit.setDividerLocation((int) (
                                vsplit.getTopComponent().getPreferredSize()
                                      .getHeight()
                            ));
                    }
                }
            });

        //this avoid an auto-resize and gives a little more space
        Dimension dim = vsplit.getPreferredSize();
        vsplit.setPreferredSize(new Dimension((int) (dim.getWidth() * 1.1),
                (int) (dim.getHeight() * 1.3)));

        setCheckSupportedExtensions(Boolean.valueOf(ConfigManager.getInstance()
                                                                 .getProperty("view.context.checkextensions",
                    "true")).booleanValue());
        showButtonPanels(Boolean.valueOf(ConfigManager.getInstance()
                                                      .getProperty("view.context.showbuttonpanel",
                    "true")).booleanValue());

        //only the top-component should auto-resize
        vsplit.setResizeWeight(1.0);
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

        //this invoke the bookmarkmanager to
        //save the bookmarks
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
        //TODO change this
        //the setup maybe null because, no one
        //changed the settings
        if (setup != null) {
            setup.update();
        }
    }

    public void setCheckSupportedExtensions(boolean b) {
        if (Boolean.valueOf(ConfigManager.getInstance().getProperty("view.context.checkextensions"))
                       .booleanValue() != b) {
            ConfigManager.getInstance().setProperty("view.context.checkextensions",
                Boolean.toString(b));
        }

        editmanager.setCheckSupportedExtensions(b);
        viewermanager.setCheckSupportedExtensions(b);
        actionmanager.setCheckSupportedExtensions(b);
    }

    private class MouseController extends MouseAdapter
        implements MouseMotionListener {
        private boolean prepareDnD = false;
        private String selectedFile = "";
        private boolean popupprocessed = false;

        /*
         *
         * (non-Javadoc)
         *
         * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
         *
         */
        public void mouseDragged(MouseEvent e) {
            if (!prepareDnD
                    && !(selectedFile.equals(fsModel.getCurrentDirectory()))) {
                JComponent c = (JComponent) e.getSource();
                dndHandler.exportAsDrag(c, e, TransferHandler.COPY);
                prepareDnD = true;
            }
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
         *
         */
        public void mouseMoved(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
            prepareDnD = false;

            if (e.getClickCount() == 2) {
                fsModel.expandSelectedFileModel();
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                //handle Windows
                if (!popupprocessed) {
                    showPopup(e);
                } else {
                    popupprocessed = false;
                }
            }
        }

        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                //handle Linux
                popupprocessed = true;
                showPopup(e);
            } else {
                prepareDnD = false;
                selectedFile = fsModel.getCurrentDirectory();
            }
        }

        private void showPopup(MouseEvent e) {
            popupmenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
