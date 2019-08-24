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

import java.awt.BorderLayout;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.io.Utilities;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;

import org.apache.log.Logger;


/**
 *
 * <p>
 *
 * This class wraps the
 *
 * <a href="http://jakarta.apache.org/commons/sandbox/vfs/">commons-vfs </a>
 *
 * to only rsync directories.
 *
 * </p>
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
public class PublisherImpl implements Publisher, Runnable {
    protected Logger log;
    protected String password;
    protected String username;
    private String uri;
    private String protocol;
    protected FileObject root;
    private ArrayList listeners;
    private boolean connected;
    private File localroot;
    private FileSystemManager fsManager;
    private String source;
    private String dest;
    private boolean thread = false;
    private boolean interrupt = false;

    /**
     *
     *
     *
     */
    public PublisherImpl(Logger log) {
        super();
        this.log = log;
        password = "";
        username = "";
        protocol = "";
        uri = "";
        listeners = new ArrayList();
        connected = false;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.Publisher#setURI(java.lang.String)
     *
     */
    public void setURI(String uri) {
        this.uri = uri;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.Publisher#requireAuth()
     *
     */
    public boolean requireAuth() {
        if (username.length() > 0) {
            return true;
        }

        return false;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.Publisher#setUsername(java.lang.String)
     *
     */
    public void setUsername(String user) {
        this.username = user;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.Publisher#setPassword(java.lang.String)
     *
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.Publisher#connect()
     *
     */
    public boolean connect() {
        try {
            //this is needed otherwise the Thread fallback to the
            // SystemClassloader
            ClassLoader cl = this.getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(cl);
            LogFactory.releaseAll();
            fsManager = VFS.getManager();

            String url = protocol;

            if (requireAuth()) {
                url = url.concat(username);

                if (password.length() > 0) {
                    url = url.concat(":" + password);
                } else {
                    //TODO Create a external PasswordRequireClass
                    JPanel panel = new JPanel(new BorderLayout());
                    panel.add(new JLabel(LocaleImpl.getInstance().getString("publish.require.password")
                            + this.username + "@" + this.uri),
                        BorderLayout.CENTER);

                    JPasswordField passwordField = new JPasswordField();
                    panel.add(passwordField, BorderLayout.SOUTH);
                    JOptionPane.showMessageDialog(null, panel, "",
                        JOptionPane.QUESTION_MESSAGE);
                    url = url.concat(":"
                            + new String(passwordField.getPassword()));
                    passwordField = null;
                }

                url = url.concat("@");
            }

            url = url.concat(translateURI(this.uri));

            //this doesnt work, so we patched the commons-vfs
            //SftpClientFactory to work without the known_hosts-file
            //			if (url.startsWith("sftp://")) {
            //
            //				FileSystemOptions fso = new FileSystemOptions();
            //				DelegatingFileSystemOptionsBuilder delegate = new
            // DelegatingFileSystemOptionsBuilder(
            //						fsManager);
            //				try {
            //					delegate.setConfigClass(fso, "sftp", "userinfo",
            //							TrustEveryoneUserInfo.class);
            //
            //				} catch (IllegalAccessException e1) {
            //
            //					e1.printStackTrace();
            //				} catch (InstantiationException e1) {
            //
            //					e1.printStackTrace();
            //				}
            //			}
            this.root = fsManager.resolveFile(url);

            return true;
        } catch (FileSystemException e) {
            e.printStackTrace();

            //log.error("connect failed:" + e.getMessage(), e);
            fireErrorMessage("Could not establish connection" + e.getMessage());
        }

        return false;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.Publisher#disconnect()
     *
     */
    public boolean disconnect() {
        if (this.root != null) {
            try {
                this.interrupt = true;
                this.root.close();
                fireEndPublishing();

                return true;
            } catch (FileSystemException e) {
                e.printStackTrace();

                return false;
            }
        }

        return false;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.Publisher#rsync(java.lang.String, java.lang.String)
     *
     */
    public void rsync(
        String source,
        String dest) {
        this.source = source;
        this.dest = dest;
        this.interrupt = false;
        fireStartPublishing(Utilities.fileCount(source));

        File f = new File(this.source);

        if (f.exists()) {
            log.debug("Try to connect.");

            if (!connected) {
                connected = connect();

                if (!connected) {
                    log.error("no connection");
                    fireEndPublishing();

                    return;
                }
            }

            try {
                if ((this.dest != null) && (this.dest.length() > 0)) {
                    rsyncDirectory(f, root.resolveFile(dest));
                } else {
                    rsyncDirectory(f, root);
                }
            } catch (FileSystemException e) {
                e.printStackTrace();
            }
        } else {
            fireErrorMessage(this.source + " does not exists");
            log.error("Source:" + this.source + " does not exists.");
        }

        fireEndPublishing();
    }

    /**
     * @param protocol
     *
     * The protocol to set.
     *
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.Publisher#addPublishListener(de.miethxml.net.PublishListener)
     *
     */
    public void addPublishListener(PublishListener listener) {
        listeners.add(listener);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.Publisher#removePublishListener(de.miethxml.net.PublishListener)
     *
     */
    public void removePublishListener(PublishListener listener) {
        listeners.remove(listener);
    }

    private void fireStartPublishing(int fileCount) {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            PublishListener listener = (PublishListener) i.next();
            listener.startPublishing(fileCount);
        }
    }

    private void fireEndPublishing() {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            PublishListener listener = (PublishListener) i.next();
            listener.endPublishing();
        }
    }

    private void fireErrorMessage(String msg) {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            PublishListener listener = (PublishListener) i.next();
            listener.errorMessage(msg);
        }
    }

    private void fireFilePublishProcess(
        String name,
        long current,
        long length) {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            PublishListener listener = (PublishListener) i.next();
            listener.filePublishProcess(name, current, length);
        }
    }

    private void firePublishedFile(
        String file,
        long size,
        long time) {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            PublishListener listener = (PublishListener) i.next();
            listener.publishedFile(file, size, time);
        }
    }

    private void rsyncDirectory(
        File file,
        FileObject remote) {
        try {
            if (!remote.exists()) {
                if (file.isDirectory()) {
                    remote.createFolder();
                }

                //handle files here
            }
        } catch (FileSystemException e1) {
            e1.printStackTrace();
        }

        File[] entries = file.listFiles();

        for (int i = 0; (i < entries.length) && !interrupt; i++) {
            if (entries[i].isDirectory()) {
                try {
                    FileObject directory = fsManager.resolveFile(remote,
                            entries[i].getName());

                    if (!directory.exists()) {
                        directory.createFolder();
                    }

                    rsyncDirectory(entries[i], directory);
                } catch (FileSystemException e) {
                    e.printStackTrace();
                }
            } else if (entries[i].isFile()) {
                rsyncFile(entries[i], remote);
            }
        }
    }

    private void rsyncFile(
        File local,
        FileObject remoteDir) {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try {
            FileObject file = remoteDir.resolveFile(local.getName());
            in = new BufferedInputStream(new FileInputStream(local));
            out = new BufferedOutputStream(file.getContent().getOutputStream());

            long length = local.length();
            String name = local.getName();

            if ((out != null) && !interrupt) {
                int off = 0;
                byte[] b = new byte[8192];
                long start = System.currentTimeMillis();
                long bytecount = 0;

                while ((off = in.read(b, 0, b.length)) > -1) {
                    bytecount += off;
                    out.write(b, 0, off);
                    fireFilePublishProcess(name, bytecount, length);
                }

                in.close();
                out.flush();
                out.close();
                in = null;
                out = null;
                firePublishedFile(name, length,
                    start - System.currentTimeMillis());
            }
        } catch (FileSystemException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (Exception e) {
                }
            }

            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (Exception e) {
                }
            }
        }
    }

    private String translateURI(String uri) {
        //first check protocol
        if (this.protocol.startsWith("smb") || this.protocol.startsWith("sftp")
                || this.protocol.startsWith("ftp")
                || this.protocol.startsWith("webdav")) {
            String end = "";
            String host = uri;

            //uri with port
            if (uri.indexOf(":") > -1) {
                end = uri.substring(uri.indexOf(":"));
                host = uri.substring(0, uri.indexOf(":"));
            } else if (uri.indexOf("/") > -1) {
                end = uri.substring(uri.indexOf("/"));
                host = uri.substring(0, uri.indexOf("/"));
            }

            if (host.indexOf(".") >= 0) {
                try {
                    //translate to IP
                    host = InetAddress.getByName(host).getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }

            return host + end;
        }

        //nothing matched try the uri
        return uri;
    }

    public void run() {
    }

    /**
     * @return Returns the thread.
     *
     */
    public boolean isThread() {
        return thread;
    }

    /**
     * @param thread
     *            The thread to set.
     *
     */
    public void setThread(boolean thread) {
        this.thread = thread;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.net.Publisher#setTask(de.miethxml.project.Task)
     */
}
