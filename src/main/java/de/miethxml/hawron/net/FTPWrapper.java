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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;


/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth
 *         </a>.mieth@t-online.de This class wrapps the connection and
 *
 * logging/exceptions from the FTPClient
 *
 *
 *
 * @deprecated
 *
 */
public class FTPWrapper implements Runnable {
    private String ftpsite;
    private String user;
    private String password;
    FTPFile[] currentDir;
    FTPClient ftp;
    private String path;
    private final int DOWNLOAD = 1;
    private final int UPLOAD = 2;
    private final int RSYNC = 3;
    int filecommand;
    private String localfile;
    private String remotefile;
    private boolean abort = false;
    private ArrayList listeners;
    FTPEvent ftpe;
    private int rsyncValue;
    Logger log = Hierarchy.getDefaultHierarchy().getLoggerFor(this.getClass()
                                                                  .getName());

    /**
     *
     *
     *
     */
    public FTPWrapper() {
        this.ftpsite = "";

        this.user = "";

        this.password = "";

        path = "";

        listeners = new ArrayList();

        ftpe = new FTPEvent(this);
    }

    public FTPWrapper(
        String ftpsite,
        String user,
        String password) {
        this.ftpsite = ftpsite;

        this.user = user;

        this.password = password;

        path = "";

        listeners = new ArrayList();

        ftpe = new FTPEvent(this);
    }

    /**
     *
     * @param ftpsite
     *
     */
    public void setFTPSite(String ftpsite) {
        this.ftpsite = ftpsite;
    }

    /**
     *
     * @param password
     *
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @param user
     *
     */
    public void setUser(String user) {
        this.user = user;
    }

    public FTPFile[] connect() {
        if (ftpe == null) {
            ftpe = new FTPEvent(this);
        }

        if (ftp == null) {
            ftp = new FTPClient();
        } else if (ftp.isConnected()) {
            path = "";

            try {
                ftp.disconnect();
            } catch (IOException e1) {
                log.error("could not disconnect -" + e1.getMessage());
            }
        }

        currentDir = new FTPFile[0];

        log.debug("try to connect");

        try {
            int reply;

            ftp.connect(ftpsite);

            // After connection attempt, you should check the reply code to
            // verify
            // success.
            reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();

                log.error("FTP server refused connection.");

                //System.exit(1);
            }
        } catch (IOException e) {
            log.error("FTPConnection error: " + e.getMessage());

            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException f) {
                    // do nothing
                }
            }
        }

        try {
            if (!ftp.login(user, password)) {
                log.error("could not login with: " + user);

                ftp.logout();
            }

            log.debug("Remote system is " + ftp.getSystemName());

            ftp.enterLocalPassiveMode();

            currentDir = ftp.listFiles();
        } catch (FTPConnectionClosedException e) {
            log.error("FTPConnectionClosedException: " + e.getMessage());
        } catch (IOException e) {
            log.error("IOException: " + e.getMessage());
        }

        ftpe.setType(FTPEvent.CONNECT);

        fireFTPEvent(ftpe);

        return currentDir;
    }

    public FTPFile[] changeDir(String path) {
        if (!ftp.isConnected()) {
            connect();
        }

        log.debug("changeDir to " + path);

        try {
            if (this.path.length() > 0) {
                path = this.path + "/" + path;
            }

            currentDir = ftp.listFiles(path);

            this.path = path;

            log.debug("new path=" + this.path);
        } catch (IOException e) {
            log.error("IOException: " + e.getMessage());
        }

        return currentDir;
    }

    public FTPFile[] changeDir(int index) {
        if ((index < currentDir.length) && currentDir[index].isDirectory()) {
            return changeDir(currentDir[index].getName());
        }

        return currentDir;
    }

    private void storeFile(
        String filename,
        String remote) {
        log.debug("try to upload " + filename + " to remote " + remote);

        if (!ftp.isConnected()) {
            connect();
        }

        File file = new File(filename);

        if (file.exists()) {
            try {
                ftp.setFileType(FTP.BINARY_FILE_TYPE);

                ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);

                FileInputStream in = new FileInputStream(file);

                OutputStream out = ftp.storeFileStream(remote);

                ftpe.setCount(0);

                ftpe.setType(FTPEvent.UPLOAD_START);

                ftpe.setSize(file.length());

                ftpe.setName(file.getName());

                fireFTPEvent(ftpe);

                if (out != null) {
                    int off = 0;

                    byte[] b = new byte[1024];

                    long start = System.currentTimeMillis();

                    long bytecount = 0;

                    while (((off = in.read(b, 0, b.length)) > -1) && !abort) {
                        bytecount += off;

                        out.write(b, 0, off);

                        ftpe.setType(FTPEvent.UPLOADING);

                        ftpe.setCount(bytecount);

                        ftpe.setTime(System.currentTimeMillis() - start);

                        fireFTPEvent(ftpe);
                    }

                    ftpe.setType(FTPEvent.UPLOAD_END);

                    fireFTPEvent(ftpe);

                    in.close();

                    out.flush();

                    out.close();

                    abort = false;

                    if (!ftp.completePendingCommand()) {
                        log.error("could not finalize the upload");
                    }

                    out.flush();

                    out.close();
                }
            } catch (FileNotFoundException e) {
                log.error("File " + filename + " not found " + e.getMessage());
            } catch (IOException ioe) {
                log.error("FTP file store error " + ioe.getMessage());
            }
        }
    }

    public void deleteFile(String filename) {
        if (!ftp.isConnected()) {
            connect();
        }

        try {
            if ((this.path.length() > 0) && (filename.indexOf("/") < 0)) {
                filename = this.path + "/" + filename;
            }

            ftp.deleteFile(filename);
        } catch (IOException e) {
            log.error("could not delete file " + filename + " "
                + e.getMessage());
        }
    }

    public void deleteEntry(int index) {
        if ((index >= 0) && (index < currentDir.length)) {
            if (!ftp.isConnected()) {
                connect();
            }

            if (currentDir[index].isDirectory()) {
                String dir = currentDir[index].getName();

                if (path.length() > 0) {
                    dir = path + "/" + dir;
                }

                try {
                    ftp.removeDirectory(dir);
                } catch (IOException e) {
                    log.error("could not remove dir " + dir + " "
                        + e.getMessage());
                }
            } else if (currentDir[index].isFile()) {
                String file = currentDir[index].getName();

                if (path.length() > 0) {
                    file = path + "/" + file;
                }

                try {
                    ftp.deleteFile(file);
                } catch (IOException e) {
                    log.error("could not remove file " + file + " "
                        + e.getMessage());
                }
            }
        }
    }

    public FTPFile[] goUp() {
        log.debug("go up from path " + this.path);

        if (this.path.length() > 0) {
            if (this.path.indexOf("/") > -1) {
                this.path = this.path.substring(0, this.path.lastIndexOf("/"));
            } else {
                path = "";
            }

            log.debug("path now " + this.path);

            if (!ftp.isConnected()) {
                connect();
            }

            try {
                currentDir = ftp.listFiles(path);
            } catch (IOException e) {
                log.error("could not go back to " + path + " " + e.getMessage());
            }
        }

        return currentDir;
    }

    public FTPFile[] goHome() {
        if (!ftp.isConnected()) {
            connect();
        }

        try {
            this.path = "";

            currentDir = ftp.listFiles(path);
        } catch (IOException e) {
            log.error("could not go home " + path + " " + e.getMessage());
        }

        return currentDir;
    }

    public FTPFile[] reload() {
        if (!ftp.isConnected()) {
            connect();
        }

        log.debug("reload " + path);

        try {
            currentDir = ftp.listFiles(path);
        } catch (IOException e) {
            log.error("IOException: " + e.getMessage());
        }

        return currentDir;
    }

    public void mkDir(String dir) {
        if (!ftp.isConnected()) {
            connect();
        }

        try {
            if ((this.path.length() > 0) && (dir.indexOf("/") < 0)) {
                dir = this.path + "/" + dir;
            }

            ftp.makeDirectory(dir);
        } catch (IOException e) {
            log.error("could not go make dir " + dir + " " + e.getMessage());
        }
    }

    private void retrieveFile(
        String local,
        String remote) {
        log.debug("try to download " + remote + " to local " + local);

        try {
            FileOutputStream out = new FileOutputStream(local);

            if (!ftp.isConnected()) {
                connect();
            }

            InputStream in = ftp.retrieveFileStream(remote);

            int off = 0;

            byte[] b = new byte[8192];

            long start = System.currentTimeMillis();

            long bytecount = 0;

            ftpe.setCount(0);

            ftpe.setType(FTPEvent.DOWNLOAD_START);

            fireFTPEvent(ftpe);

            while (((off = in.read(b, 0, b.length)) > -1) && !abort) {
                bytecount += off;

                out.write(b, 0, off);

                //System.out.print(".");
                ftpe.setType(FTPEvent.DOWNLOADING);

                ftpe.setCount(bytecount);

                ftpe.setTime(System.currentTimeMillis() - start);

                fireFTPEvent(ftpe);
            }

            ftpe.setType(FTPEvent.DOWNLOAD_END);

            fireFTPEvent(ftpe);

            abort = false;

            in.close();

            if (!ftp.completePendingCommand()) {
                log.error("could not finalize the download");
            }

            out.flush();

            out.close();
        } catch (IOException ioe) {
        }
    }

    public void download(
        String local,
        String remote) {
        filecommand = DOWNLOAD;

        localfile = local;

        remotefile = remote;

        Thread t = new Thread(this);

        t.start();
    }

    public void download(
        String local,
        int index) {
        abort = false;

        if ((index >= 0) && (index < currentDir.length)) {
            if (currentDir[index].isFile()) {
                ftpe.setSize(currentDir[index].getSize());

                ftpe.setFile(currentDir[index].getName());

                if (this.path.length() > 0) {
                    download(local,
                        this.path + "/" + currentDir[index].getName());
                } else {
                    download(local, currentDir[index].getName());
                }
            }
        }
    }

    public void upload(
        String local,
        String remote) {
        abort = false;

        if ((path.length() > 0) && (remote.indexOf("/") < 0)) {
            remote = this.path + "/" + remote;
        }

        localfile = local;

        remotefile = remote;

        filecommand = UPLOAD;

        Thread t = new Thread(this);

        t.start();
    }

    public void run() {
        if (filecommand == UPLOAD) {
            storeFile(localfile, remotefile);
        } else if (filecommand == DOWNLOAD) {
            retrieveFile(localfile, remotefile);
        } else if (filecommand == RSYNC) {
            rsync(localfile, remotefile);

            ftpe.setType(FTPEvent.RSYNC_END);

            fireFTPEvent(ftpe);
        }
    }

    public void abort() {
        this.abort = true;
    }

    public void addFTPListener(FTPListener l) {
        listeners.add(l);
    }

    public void removeFTPListener(FTPListener l) {
        for (int i = 0; i < listeners.size(); i++) {
            FTPListener ftpl = (FTPListener) listeners.get(i);

            if (ftpl.equals(l)) {
                listeners.remove(i);

                return;
            }
        }
    }

    private void fireFTPEvent(FTPEvent e) {
        for (int i = 0; i < listeners.size(); i++) {
            FTPListener ftpl = (FTPListener) listeners.get(i);

            ftpl.ftpStatePerformed(e);
        }
    }

    public void disconnect() {
        try {
            ftp.disconnect();

            ftpe.setType(FTPEvent.DISCONNECT);

            fireFTPEvent(ftpe);
        } catch (IOException e) {
            log.error("Could not disconnect " + e.getMessage());
        }
    }

    public void synchronize(String local) {
        synchronize(local, this.path);
    }

    public void synchronize(
        String local,
        String remote) {
        log.debug("synchronize " + local + " to " + remote);

        File f = new File(local);

        if (f.exists() && f.isDirectory()) {
            int count = countFiles(f.getPath());

            ftpe.setType(FTPEvent.RSYNC_START);

            ftpe.setRsyncCount(count);

            ftpe.setRsyncValue(0);

            rsyncValue = 0;

            fireFTPEvent(ftpe);

            this.localfile = local;

            this.remotefile = remote;

            this.filecommand = RSYNC;

            Thread t = new Thread(this);

            t.start();
        } else {
            if (!f.exists()) {
                log.error(local + " not exists");
            }

            if (!f.isDirectory()) {
                log.error(local + " is not a  directory");
            }
        }
    }

    private void rsync(
        String local,
        String remote) {
        //get the directory listing
        File dir = new File(local);

        File[] entries = dir.listFiles();

        //check if the remoteDir exits
        checkDirectory(remote, "");

        for (int i = 0; i < entries.length; i++) {
            log.debug("look at " + entries[i].getAbsolutePath());

            if (entries[i].isDirectory()) {
                log.debug("rsync subdir=" + entries[i].getName());

                rsync(entries[i].getAbsolutePath(),
                    remote + "/" + entries[i].getName());
            } else if (entries[i].isFile()) {
                log.debug("check file=" + entries[i].getAbsolutePath());

                checkFile(entries[i], remote + "/" + entries[i].getName());
            }
        }
    }

    void checkDirectory(
        String dir,
        String part) {
        if (dir.length() == 0) {
            return;
        }

        log.debug("checkDir we got " + dir + " part " + part);

        if (dir.indexOf("/", part.length()) > 0) {
            part = dir.substring(0, dir.indexOf("/", part.length()));
        } else if (dir.length() > part.length()) {
            //these is the last subdir->filesystemendknode
            part = dir;
        }

        log.debug("check subdir=" + part);

        try {
            FTPFile[] test = ftp.listFiles(part);

            if (test == null) {
                try {
                    //the directory not exits so we make it
                    ftp.makeDirectory(part);
                } catch (IOException e1) {
                    // oh, we may have now a problem
                    e1.printStackTrace();
                }
            }
        } catch (IOException e) {
            try {
                //the directory not exits so we make it
                ftp.makeDirectory(part);
            } catch (IOException e1) {
                log.error(e1.getMessage());

                e1.printStackTrace();
            }
        }

        part = part + "/";

        if (dir.length() > part.length()) {
            //recursive may help us here
            checkDirectory(dir, part);
        }
    }

    public void checkFile(
        File local,
        String remote) {
        //check if the remote file exits
        try {
            FTPFile[] f = ftp.listFiles(remote);

            if ((f != null) && (f.length == 1)) {
                //we will check the length and timestamp of the files
                log.debug("check length local " + local.length() + " remote "
                    + f[0].getSize());

                if (f[0].getSize() == local.length()) {
                    log.debug("check timestamp local " + local.lastModified()
                        + " remote " + f[0].getTimestamp().getTimeInMillis());

                    if (f[0].getTimestamp().getTimeInMillis() == local
                            .lastModified()) {
                        log.debug("need no update -> file="
                            + local.getAbsolutePath());

                        return;
                    }
                }
            }
        } catch (IOException ioe) {
            log.error("checkFile  " + ioe.getMessage());
        }

        rsyncValue++;

        log.debug("upload file=" + local.getAbsolutePath() + " file count="
            + rsyncValue);

        ftpe.setRsyncValue(rsyncValue);

        ftpe.setType(FTPEvent.RSYNC_NEXT);

        fireFTPEvent(ftpe);

        storeFile(local.getAbsolutePath(), remote);

        try {
            //set the timestamp to local modificationtime
            FTPFile[] f = ftp.listFiles(remote);

            if ((f != null) && (f.length == 1)) {
                log.debug("change timestamp for " + remote);

                Calendar cal = Calendar.getInstance();

                cal.setTime(new Date(local.lastModified()));

                f[0].setTimestamp(cal);

                log.debug("timestamp now  "
                    + f[0].getTimestamp().getTimeInMillis());
            }
        } catch (IOException e) {
            log.error("checkFile " + e.getMessage());

            e.printStackTrace();
        }
    }

    private int countFiles(String dir) {
        int count = 0;

        File directory = new File(dir);

        if (directory.isDirectory()) {
            //first count Files
            File[] files = directory.listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    count++;
                } else if (files[i].isDirectory()) {
                    count += countFiles(files[i].getPath());
                }
            }
        }

        return count;
    }
}
