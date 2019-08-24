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

import java.io.Serializable;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;


/**
 * @author simon
 *
 *
 *
 */
public class VFSFileModel implements FileModel{
    private FileObject f;
    private int childCount = -1;
    private FileModel parent;

    public VFSFileModel(
        FileObject f,
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
        try {
            if (f.getType().equals(FileType.FILE)) {
                return true;
            }
        } catch (FileSystemException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.io.FileModel#lastModified()
     */
    public long lastModified() {
        try {
            return f.getContent().getLastModifiedTime();
        } catch (FileSystemException e) {
            
            //e.printStackTrace();
        }

        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.io.FileModel#getLength()
     */
    public long getLength() {
        try {
            return f.getContent().getSize();
        } catch (FileSystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.io.FileModel#getName()
     */
    public String getName() {
        return f.getName().getBaseName();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.io.FileModel#setName(java.lang.String)
     */
    public void renameTo(String name) {
        try {
            FileObject parent = f;

            if (f.getType().equals(FileType.FILE)) {
                parent = f.getParent();
            }

            FileObject newFile = parent.resolveFile(name);

            int count = 0;

            while (!f.canRenameTo(newFile) && (count < 20)) {
                newFile = parent.resolveFile(name + count);
                count++;
            }

            f.moveTo(newFile);
            f = newFile;
        } catch (FileSystemException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.io.FileModel#getPath()
     */
    public String getPath() {
    	String path = f.getName().getURI();
    	if(path.indexOf('@')!= -1){
    		//remove the username and password
    		path = path.replaceAll("\\/\\/(.*?)\\@","//");
    	}
    	
        return path;
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
            try {
                childCount = f.getChildren().length;
            } catch (FileSystemException e) {
                childCount = 0;
                e.printStackTrace();
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
        try {
            return new VFSFileModel(f.getChildren()[index], this);
        } catch (FileSystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.io.FileModel#getChildren()
     */
    public FileModel[] getChildren() {
        try {
            FileObject[] children = f.getChildren();
            FileModel[] models = new VFSFileModel[children.length];

            for (int i = 0; i < children.length; i++) {
                models[i] = new VFSFileModel(children[i], this);
            }

            return models;
        } catch (FileSystemException e) {
            e.printStackTrace();
        }

        return new VFSFileModel[0];
    }

    public String toString() {
        return getName();
    }

    public FileModelContent getContent() {
        return new VFSFileModelContent(f);
    }

    public boolean delete() {
        try {
            return f.delete();
        } catch (FileSystemException e) {
            return false;
        }
    }

    public boolean exists() {
        try {
            return f.exists();
        } catch (FileSystemException e) {
            return false;
        }
    }

    public FileModel createDirectory(String name) throws FileModelException {
        FileObject dir = null;

        try {
            FileObject parent = f;

            if (parent.getType().equals(FileType.FILE)) {
                parent = parent.getParent();
            }

            dir = parent.resolveFile(name);
            dir.createFolder();

            return new VFSFileModel(dir, this);
        } catch (FileSystemException e) {
            throw new FileModelException("Could not create directory:" + name
                + " " + e.getMessage());
        }
    }

    public FileModel createFile(String name) throws FileModelException {
        FileObject file;

        try {
            file = f.resolveFile(name);

            return new VFSFileModel(file, this);
        } catch (FileSystemException e) {
            throw new FileModelException("Could not create file:" + name + " "
                + e.getMessage());
        }
    }
}
