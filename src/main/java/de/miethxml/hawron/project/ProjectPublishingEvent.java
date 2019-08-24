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
package de.miethxml.hawron.project;

import java.util.EventObject;


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
public class ProjectPublishingEvent extends EventObject {
    String message;
    int fileCount;
    int currentFile;

    /**
     *
     * @param source
     *
     */
    public ProjectPublishingEvent(Object source) {
        super(source);

        message = "";

        fileCount = 0;

        currentFile = 0;
    }

    /**
     *
     * @return Returns the message.
     *
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     *            The message to set.
     *
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return Returns the currentFile.
     *
     */
    public int getCurrentFile() {
        return currentFile;
    }

    /**
     *
     * @param currentFile
     *            The currentFile to set.
     *
     */
    public void setCurrentFile(int currentFile) {
        this.currentFile = currentFile;
    }

    /**
     *
     * @return Returns the fileCount.
     *
     */
    public int getFileCount() {
        return fileCount;
    }

    /**
     *
     * @param fileCount
     *            The fileCount to set.
     *
     */
    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }
}
