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
package de.miethxml.toolkit.io;

import java.io.File;
import java.io.Serializable;


/**
 * @author simon
 *
 *
 *
 */
public class DefaultFileModel implements FileModel{
    private File f;
    private int childCount = -1;
    private FileModel parent;

    public DefaultFileModel(
        File f,
        FileModel parent) {
        this.f = f;
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.io.FileModel#isFile()
     */
    public boolean isFile() {
        return f.isFile();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.io.FileModel#lastModified()
     */
    public long lastModified() {
        return f.lastModified();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.io.FileModel#getName()
     */
    public String getName() {
        return f.getName();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.io.FileModel#getPath()
     */
    public String getPath() {
        return f.getAbsolutePath();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.io.FileModel#getParent()
     */
    public FileModel getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.io.FileModel#getChildCount()
     */
    public int getChildCount() {
        if (childCount == -1) {
            File[] children = f.listFiles();

            if (children != null) {
                childCount = f.listFiles().length;
            } else {
                childCount = 0;
            }
        }

        return childCount;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.io.FileModel#getChild(int)
     */
    public FileModel getChild(int index) {
        if (index < getChildCount()) {
            File[] childs = f.listFiles();

            return new DefaultFileModel(childs[index], this);
        }

        return null;
    }

    public long getLength() {
        return f.length();
    }

    public FileModel[] getChildren() {
        File[] childs = f.listFiles();

        if (childs != null) {
            FileModel[] children = new FileModel[childs.length];

            for (int i = 0; i < childs.length; i++) {
                children[i] = new DefaultFileModel(childs[i], this);
            }

            return children;
        }

        return new FileModel[0];
    }

    public String toString() {
        return getName();
    }

    public void renameTo(String name) {
        File newfile = new File(f.getParentFile().getAbsolutePath()
                + File.separator + name);
        f.renameTo(newfile);
        f = newfile;
        childCount = -1;
    }

    public FileModelContent getContent() {
        return new DefaultFileModelContent(f);
    }

    public boolean delete() {
        return f.delete();
    }

    public boolean exists() {
        return f.exists();
    }

    public FileModel createDirectory(String name) throws FileModelException {
        File parent = f;

        if (f.isFile()) {
            parent = f.getParentFile();
        }

        File dir = new File(parent, name);

        if (dir.mkdir()) {
            return new DefaultFileModel(dir, this);
        } else {
            throw new FileModelException("Could not create directory:" + name);
        }
    }

    public FileModel createFile(String name) throws FileModelException {
        File parent = f;

        if (f.isFile()) {
            parent = f.getParentFile();
        }

        File file = new File(parent, name);

        return new DefaultFileModel(file, this);
    }
}
