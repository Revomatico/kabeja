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

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.miethxml.hawron.io.FileMatcher;

import de.miethxml.toolkit.component.Configurable;


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
public class ExternalViewer implements Viewer, Configurable {
    public static String PLATFORM_ALL = "all";
    private String command;
    private String name;
    private List handles;
    private String platform;
    private Icon icon;
    private String open;
    private String iconurl;
    private String file;
    private String execute;
    private String start;
    private String end;
    private FileMatcher matcher = new FileMatcher();

    /**
     *
     *
     *
     */
    public ExternalViewer() {
        super();
        command = "";
        name = "";
        open = "";
        platform = "";
        iconurl = "";
        handles = new ArrayList();
        platform = PLATFORM_ALL;
        execute = "";
        file = "";
        start = "";
        end = "";
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.Viewer#init()
     *
     */
    public void init() {
    }

    public void setup() {
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

    public boolean isSetup() {
        if (isSupportedPlatform()) {
            return true;
        }

        return false;
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
        execute = start + file + end;

        try {
            Runtime.getRuntime().exec(execute);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.Viewer#load(java.lang.String)
     *
     */
    public void open(String file) {
        this.file = file;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.Viewer#isSupported(java.lang.String)
     *
     */
    public boolean isSupported(String f) {
        return matcher.matches(f);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.viewer.Viewer#getIcon()
     *
     */
    public Icon getIcon() {
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
        return name;
    }

    public String getCommand() {
        return command;
    }

    /**
     *
     * Set the execute command for the external editor program.
     *
     * @param command
     *
     * The execute command to set.
     *
     */
    public void setCommand(String command) {
        this.command = command;

        if (command.indexOf("%s") > 0) {
            start = command.substring(0, command.indexOf("%s"));
            end = command.substring(command.indexOf("%s") + 2);
        } else {
            start = command + " ";
            end = "";
        }
    }

    /**
     * @return Returns the handles.
     *
     */
    public List getHandles() {
        return handles;
    }

    /**
     * @param handles
     *
     * The handles to set.
     *
     */
    public void setHandles(List handles) {
        this.handles = handles;
        matcher.setPattern(this.handles);
    }

    /**
     * @return Returns the name.
     *
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *
     * The name to set.
     *
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param icon
     *
     * The icon to set.
     *
     */
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public void setIcon(String file) {
        this.iconurl = file;
        this.icon = new ImageIcon(file);
    }

    public String getIconURL() {
        return iconurl;
    }

    /**
     * @return Returns the platform.
     *
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * @param platform
     *
     * The platform to set.
     *
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public boolean isSupportedPlatform() {
        if (platform.equals(ExternalViewer.PLATFORM_ALL)) {
            return true;
        } else {
            String os = System.getProperty("os.name");

            if (os.indexOf(platform) != -1) {
                return true;
            }
        }

        return false;
    }

    public void addHandle(String handle) {
        handles.add(handle);
    }

    private void prepareCommand() {
        //search the %s and replace with file
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
        ExternalViewer viewer = new ExternalViewer();
        viewer.setCommand(getCommand());
        viewer.setIcon(getIcon());
        viewer.setHandles(getHandles());
        viewer.setName(getName());

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
