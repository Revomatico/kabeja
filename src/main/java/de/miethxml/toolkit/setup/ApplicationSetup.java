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
package de.miethxml.toolkit.setup;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import de.miethxml.toolkit.component.GuiConfigurable;
import de.miethxml.toolkit.conf.LocaleImpl;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;


/**
 * All GUIConfiurable componets will automatic registrate here from the
 * ApplicatioContainer.
 *
 *
 *
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 */
public class ApplicationSetup implements TreeModel, Serviceable, Initializable {
    public final static String ROLE = ApplicationSetup.class.getName();
    private ServiceManager manager;
    private SetupNode root;
    private ArrayList treelistener;
    private ArrayList setupProcessListener;

    /**
     *
     *
     *
     */
    public ApplicationSetup() {
        super();
        treelistener = new ArrayList();
        root = new SetupNode();
        root.setLabel(LocaleImpl.getInstance().getString("setup.tree.root"));
        root.setKey("setup");
        setupProcessListener = new ArrayList();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeModel#getRoot()
     *
     */
    public Object getRoot() {
        return root;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     *
     */
    public int getChildCount(Object parent) {
        SetupNode node = (SetupNode) parent;

        return node.getChildCount();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     *
     */
    public boolean isLeaf(Object obj) {
        SetupNode node = (SetupNode) obj;

        if (node.getChildCount() == 0) {
            return true;
        }

        return false;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
     *
     */
    public void addTreeModelListener(TreeModelListener l) {
        treelistener.add(l);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
     *
     */
    public void removeTreeModelListener(TreeModelListener l) {
        for (int i = 0; i < treelistener.size(); i++) {
            TreeModelListener tml = (TreeModelListener) treelistener.get(i);

            if (tml.equals(l)) {
                treelistener.remove(i);

                return;
            }
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     *
     */
    public Object getChild(
        Object parent,
        int index) {
        SetupNode node = (SetupNode) parent;

        if (node.hasChildren() && (index >= 0)
                && (index < node.getChildCount())) {
            return node.getChild(index);
        }

        return null;
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
    public int getIndexOfChild(
        Object parent,
        Object child) {
        SetupNode node = (SetupNode) parent;
        SetupNode childnode = (SetupNode) child;

        if (node.hasChildren()) {
            for (int i = 0; i < node.getChildCount(); i++) {
                SetupNode setupchild = (SetupNode) node.getChild(i);

                if (setupchild.equals(childnode)) {
                    return i;
                }
            }
        }

        return 0;
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
    public void valueForPathChanged(
        TreePath path,
        Object newValue) {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#init()
     *
     */
    public void initialize() {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#release()
     *
     */
    public void disposeComponent(ServiceManager newParam) {
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
    }

    public void addGuiConfigurable(
        String key,
        GuiConfigurable config) {
        String[] path = key.split("\\.");
        SetupNode node = root;

        if (path[0].equals("setup")) {
            StringBuffer buf = new StringBuffer();
            buf.append("setup.tree.root");

            for (int i = 1; i < path.length; i++) {
                SetupNode child = node.getChild(path[i]);
                buf.append("." + path[i]);

                if (child == null) {
                    //create a new Node
                    child = new SetupNode();
                    child.setKey(path[i]);
                    child.setLabel(LocaleImpl.getInstance().getString(buf
                            .toString()));
                    node.addSetupNode(child);
                }

                node = child;
            }

            SetupNode child = new SetupNode();
            child.setKey("" + node.getChildCount());
            child.setLabel(config.getLabel());
            child.setGuiConfigurable(config);
            node.addSetupNode(child);
            fireTreeEvent();
        } else {
        }
    }

    private void fireTreeEvent() {
        for (int i = 0; i < treelistener.size(); i++) {
            TreeModelListener tml = (TreeModelListener) treelistener.get(i);
            SetupNode[] node = new SetupNode[1];
            node[0] = root;
            tml.treeStructureChanged(new TreeModelEvent(this, node));
        }
    }

    public void setupNodes() {
        fireSetupProcessStart();
        root.setup();
        fireSetupProcessEnd();

        //manager.configure();
    }

    public void addSetupProcessListener(SetupProcessListener l) {
        setupProcessListener.add(l);
    }

    public void removeSetupProcessListener(SetupProcessListener l) {
        setupProcessListener.remove(l);
    }

    private void fireSetupProcessStart() {
        synchronized (setupProcessListener) {
            Iterator i = setupProcessListener.iterator();

            while (i.hasNext()) {
                SetupProcessListener l = (SetupProcessListener) i.next();
                l.startSetup();
            }
        }
    }

    private void fireSetupProcessEnd() {
        synchronized (setupProcessListener) {
            Iterator i = setupProcessListener.iterator();

            while (i.hasNext()) {
                SetupProcessListener l = (SetupProcessListener) i.next();
                l.endSetup();
            }
        }
    }
}
