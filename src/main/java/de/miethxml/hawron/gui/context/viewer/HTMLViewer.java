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
package de.miethxml.hawron.gui.context.viewer;

import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 *
 *
 *
 */
public class HTMLViewer implements Viewer {
    private String[] supported = new String[] { ".htm", ".html", ".xhtml", ".xml" };
    private String command = "";
    boolean foundBrowser = false;

    /**
     *
     *
     *
     */
    public HTMLViewer() {
        super();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.Viewer#init()
     *
     */
    public void init() {
        determineBrowser();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.Viewer#setup()
     *
     */
    public boolean setup() {
        return false;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.Viewer#getSetup()
     *
     */
    public JComponent getSetup() {
        return null;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.Viewer#setContextPath(java.lang.String)
     *
     */
    public void setContextPath(String path) {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.Viewer#setVisible(boolean)
     *
     */
    public void setVisible(boolean state) {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.Viewer#load(java.lang.String)
     *
     */
    public void open(String file) {
        try {
            Runtime.getRuntime().exec(command + " " + file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.Viewer#isSupported(java.lang.String)
     *
     */
    public boolean isSupported(String file) {
        for (int i = 0; i < supported.length; i++) {
            if (file.endsWith(supported[i]) && foundBrowser) {
                return true;
            }
        }

        return false;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.Viewer#getIcon()
     *
     */
    public Icon getIcon() {
        ImageIcon icon = new ImageIcon("icons/browser.gif");

        return icon;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.Viewer#getTooltip(java.lang.String)
     *
     */
    public String getToolTip(String lang) {
        return "HtmlViewer";
    }

    private void determineBrowser() {
        String os = System.getProperty("os.name");

        if (os.indexOf("indows") > -1) {
            command = "rundll32 url.dll,FileProtocolHandler ";
            foundBrowser = true;
        } else {
            String[] browsers = {
                    "/usr/bin/epiphany", "/usr/bin/mozilla", "/usr/bin/galeon",
                    "/usr/kde/3.1/bin/konqueror", "/usr/kde/3/bin/konqueror",
                    "/usr/bin/netscape"
                };

            for (int i = 0; i < browsers.length; i++) {
                File f = new File(browsers[i]);

                if (f.exists() && f.isFile()) {
                    command = browsers[i];
                    foundBrowser = true;

                    return;
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.Viewer#addEditorCloseListener(de.miethxml.gui.viewer.ViewerCloseListener)
     *
     */
    public void addViewerCloseListener(ViewerCloseListener listener) {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.Viewer#createNewViewer()
     *
     */
    public Viewer createNewViewer() {
        HTMLViewer viewer = new HTMLViewer();
        viewer.init();

        return viewer;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.Viewer#removeEditorCloseListener(de.miethxml.gui.viewer.ViewerCloseListener)
     *
     */
    public void removeViewerCloseListener(ViewerCloseListener listener) {
    }
}
