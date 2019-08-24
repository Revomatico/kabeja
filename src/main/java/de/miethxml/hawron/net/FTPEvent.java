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
package de.miethxml.hawron.net;

import java.util.EventObject;


/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 * @deprecated
 *
 */
public class FTPEvent extends EventObject {
    public final static int DISCONNECT = 0;
    public final static int CONNECT = 1;
    public final static int ABORT = 2;
    public final static int DOWNLOAD_START = 3;
    public final static int DOWNLOADING = 4;
    public final static int DOWNLOAD_END = 5;
    public final static int UPLOAD_START = 6;
    public final static int UPLOADING = 7;
    public final static int UPLOAD_END = 8;
    public final static int RSYNC_START = 9;
    public final static int RSYNC_END = 10;
    public final static int RSYNC_NEXT = 11;
    int type;
    long size;
    long count;
    long time;
    int rsyncCount;
    int rsyncValue;
    String file;
    String name;

    /**
     *
     * @param arg0
     *
     */
    public FTPEvent(Object arg0) {
        super(arg0);

        type = DISCONNECT;

        size = 0;

        count = 0;

        name = "";

        rsyncCount = 0;

        rsyncValue = 0;
    }

    public int getType() {
        return type;
    }

    /**
     *
     * @return
     */
    public long getCount() {
        return count;
    }

    /**
     *
     * @param count
     *
     */
    public void setCount(long count) {
        this.count = count;
    }

    /**
     *
     * @return
     */
    public long getSize() {
        return size;
    }

    /**
     *
     * @param size
     *
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     *
     * @param type
     *
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public String getFile() {
        return file;
    }

    /**
     *
     * @param file
     *
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     *
     * @return
     */
    public long getTime() {
        return time;
    }

    /**
     *
     * @param time
     *
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return Returns the rsyncCount.
     *
     */
    public int getRsyncCount() {
        return rsyncCount;
    }

    /**
     *
     * @param rsyncCount
     *            The rsyncCount to set.
     *
     */
    public void setRsyncCount(int rsyncCount) {
        this.rsyncCount = rsyncCount;
    }

    /**
     *
     * @return Returns the rsyncValue.
     *
     */
    public int getRsyncValue() {
        return rsyncValue;
    }

    /**
     *
     * @param rsyncValue
     *            The rsyncValue to set.
     *
     */
    public void setRsyncValue(int rsyncValue) {
        this.rsyncValue = rsyncValue;
    }
}
