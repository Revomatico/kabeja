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
package de.miethxml.hawron.io;

import java.io.File;

import java.util.ArrayList;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *  @deprecated
 */
public class Directory {
    private String name;
    private ArrayList dir = new ArrayList();
    private ArrayList all = new ArrayList();
    private File file;
    private Directory parent;
    private boolean parentDirectory = false;

    /**
     *
     *
     *
     */
    public Directory() {
        this(new File(""));
    }

    public Directory(String filename) {
        this(new File(filename));
    }

    public Directory(File file) {
        this.file = file;
    }

    public String toString() {
        if (parentDirectory) {
            return "..";
        }

        return file.getName();
    }

    /**
     * @return
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file
     *
     */
    public void setFile(File file) {
        this.file = file;
        listDirectory();
    }

    public int getDirCount() {
        return dir.size();
    }

    public Directory getDir(int index) {
        if ((index >= 0) && (index < dir.size())) {
            return (Directory) dir.get(index);
        }

        return null;
    }

    /**
     * List the directory content an return a List with removed directories
     *
     */
    public void listDirectory() {
        dir.clear();
        all.clear();

        if ((file != null) && file.exists()) {
            File[] entries = file.listFiles();

            //add the parent directory ..
            File parent = file.getParentFile();

            if (parent != null) {
                Directory p = new Directory(parent);

                p.setParentDirectory(true);

                all.add(p);
                this.parent = p;
            } else {
                this.parent = null;
            }

            if (entries != null) {
                for (int i = 0; i < entries.length; i++) {
                    if (entries[i].isDirectory()) {
                        Directory d = new Directory(entries[i]);

                        d.setParent(this);

                        dir.add(d);

                        all.add(d);
                    } else if (entries[i].isFile()) {
                        UniqueFile f = new UniqueFile(entries[i]);

                        all.add(f);
                    }
                }
            }
        }
    }

    public int getCount() {
        return all.size();
    }

    public Object getEntry(int index) {
        if ((index >= 0) && (index < all.size())) {
            return all.get(index);
        }

        return null;
    }

    public String getPath() {
        return file.getPath();
    }

    /**
     * @return Returns the parent.
     *
     */
    public Directory getParent() {
        return parent;
    }

    /**
     * @param parent
     *            The parent to set.
     *
     */
    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public boolean hasParent() {
        if (parent != null) {
            return true;
        }

        return false;
    }

    /**
     * @param parentDirectory
     *            The parentDirectory to set.
     *
     */
    public void setParentDirectory(boolean parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    /**
     * @return Returns the parentDirectory.
     *
     */
    public boolean isParentDirectory() {
        return parentDirectory;
    }

    public boolean removed() {
        return !file.exists();
    }
}
