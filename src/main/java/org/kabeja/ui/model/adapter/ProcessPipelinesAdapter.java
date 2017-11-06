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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeNode;


public class ProcessPipelinesAdapter implements TreeNode {
    private Map<String, ?> pipelines;
    private TreeNode[] nodes;

    public ProcessPipelinesAdapter(Map<String, ?> pipelines) {
        this.pipelines = pipelines;
    }

    protected void buildNodeList() {
        nodes = new TreeNode[this.pipelines.size()];

        List<String> list = new ArrayList<String>(this.pipelines.keySet());
        Iterator<String> i = list.iterator();
        int count = 0;

        while (i.hasNext()) {
            String name = i.next();
            nodes[count] = new DefaultLeafAdapter(name, this);
        }
    }

    @Override
    public int getChildCount() {
        return this.pipelines.size();
    }

    @Override
    public Enumeration<? extends TreeNode> children() {
        return null;
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return nodes[childIndex];
    }

    @Override
    public int getIndex(TreeNode node) {
        return 0;
    }

    @Override
    public TreeNode getParent() {
        return null;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }
}
