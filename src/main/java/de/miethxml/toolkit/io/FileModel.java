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


/**
 * 
 * 
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public interface FileModel{
    public boolean isFile();

    public long lastModified();

    public long getLength();

    public String getName();

    public void renameTo(String name);

    public String getPath();

    public FileModel getParent();

    public int getChildCount();

    public FileModel getChild(int index);

    public FileModel[] getChildren();

    public FileModelContent getContent();

    public boolean exists();

    public boolean delete();

    /**
     * Create a directory relative to this FileModel
     * @param name
     * @return the created directory
     */
    public FileModel createDirectory(String name) throws FileModelException;

    /**
     * Create a file relative to this FileModel. If this is a file
     * the new file will created in the same directory.Depending on the 
     * implementation a empty file could be created or not, only if
     * content is written  (by getContent().getOutputStream()) a file should
     * exists.
     * @param name
     * @return the created FileModel 
     * @throws Exception
     */
    public FileModel createFile(String name) throws FileModelException;
}
