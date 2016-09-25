/*
   Copyright 2008 Simon Mieth

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
package org.kabeja.ui.model.adapter;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;


public class DefaultLeafAdapter implements TreeNode {
    protected String name;
    protected TreeNode parent;

    public DefaultLeafAdapter(String name, TreeNode parent) {
        this.name = name;
        this.parent = parent;
    }

    @Override
    public Enumeration<?> children() {
        return null;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public int getIndex(TreeNode node) {
        return 0;
    }

    @Override
    public TreeNode getParent() {
        return this.parent;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
