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

import de.miethxml.toolkit.component.GuiConfigurable;


/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 *
 *
 *
 */
public class SetupNode {
    String key;
    String label;
    SetupNode parent;
    GuiConfigurable guiConfigurable;
    ArrayList children;

    /**
     *
     *
     *
     */
    public SetupNode() {
        super();

        label = "";

        children = new ArrayList();
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String toString() {
        return getLabel();
    }

    /**
     *
     * @return Returns the parent.
     *
     */
    public SetupNode getParent() {
        return parent;
    }

    /**
     *
     * @param parent
     *
     * The parent to set.
     *
     */
    public void setParent(SetupNode parent) {
        this.parent = parent;
    }

    public boolean hasParent() {
        if (parent != null) {
            return true;
        }

        return false;
    }

    /**
     *
     * @return Returns the guiConfigurable.
     *
     */
    public GuiConfigurable getGuiConfigurable() {
        return guiConfigurable;
    }

    /**
     *
     * @param guiConfigurable
     *
     * The guiConfigurable to set.
     *
     */
    public void setGuiConfigurable(GuiConfigurable guiConfigurable) {
        this.guiConfigurable = guiConfigurable;
    }

    public boolean hasGuiConfigurable() {
        if (guiConfigurable != null) {
            return true;
        }

        return false;
    }

    /**
     *
     * @return Returns the key.
     *
     */
    public String getKey() {
        return key;
    }

    /**
     *
     * @param key
     *
     * The key to set.
     *
     */
    public void setKey(String key) {
        this.key = key;
    }

    public int getChildCount() {
        return children.size();
    }

    public SetupNode getChild(int index) {
        if ((index >= 0) && (index < children.size())) {
            return (SetupNode) children.get(index);
        }

        return null;
    }

    public SetupNode getChild(String key) {
        for (int i = 0; i < children.size(); i++) {
            SetupNode child = (SetupNode) children.get(i);

            if (child.getKey().equals(key)) {
                return child;
            }
        }

        return null;
    }

    public boolean hasChildren() {
        if (children.size() > 0) {
            return true;
        }

        return false;
    }

    public void addSetupNode(SetupNode node) {
        node.setParent(this);

        children.add(node);
    }

    public void setup() {
        if (hasGuiConfigurable()) {
            guiConfigurable.setup();
        }

        if (hasChildren()) {
            for (int i = 0; i < children.size(); i++) {
                SetupNode child = (SetupNode) children.get(i);

                child.setup();
            }
        }
    }
}
