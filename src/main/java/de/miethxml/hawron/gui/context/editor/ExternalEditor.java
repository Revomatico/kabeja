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
package de.miethxml.hawron.gui.context.editor;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.miethxml.hawron.io.FileMatcher;

import de.miethxml.toolkit.component.Configurable;


/**
 *
 * This class is a wrapper for external editors. The Editor is launched during
 *
 * a System.exec() call.
 *
 * @author Simon Mieth
 *
 *
 *
 *
 *
 *
 *
 */
public class ExternalEditor implements Editor, Configurable {
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
    private FileMatcher matcher;

    /**
     *
     *
     *
     */
    public ExternalEditor() {
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
        matcher = new FileMatcher();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#setContextPath(java.lang.String)
     *
     */
    public void setContextPath(String path) {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#isSetup()
     *
     */
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
     * @see de.miethxml.gui.editor.Editor#getSetup()
     *
     */
    public JComponent getSetup() {
        return null;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#setup()
     *
     */
    public void setup() {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#init()
     *
     */
    public void init() {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#load(java.lang.String)
     *
     */
    public void open(String file) {
        this.file = file;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#isSupported(java.lang.String)
     *
     */
    public boolean isSupported(String extension) {
        return matcher.matches(extension);
    } /*
    *
    * (non-Javadoc)
    *
    * @see de.miethxml.gui.editor.Editor#save()
    *
    */
    public void save() {
    } /*
    *
    * (non-Javadoc)
    *
    * @see de.miethxml.gui.editor.Editor#dispose()
    *
    */
    public void dispose() {
    } /*
    *
    * (non-Javadoc)
    *
    * @see de.miethxml.toolkit.cache.Cacheable#destroy()
    *
    */
    public void destroy() {
    } /*
    *
    * (non-Javadoc)
    *
    * @see de.miethxml.gui.editor.Editor#newFile(java.lang.String)
    *
    */
    public void newFile(String file) {
        this.file = file;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#setVisible(boolean)
     *
     */
    public void setVisible(boolean state) {
        execute = start + file + end;

        try {
            Runtime.getRuntime().exec(execute);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } /*
    *
    * (non-Javadoc)
    *
    * @see de.miethxml.gui.editor.Editor#getIcon()
    *
    */
    public Icon getIcon() {
        return icon;
    } /*
    *
    * (non-Javadoc)
    *
    * @see de.miethxml.gui.editor.Editor#getTooltip(java.lang.String)
    *
    */
    public String getToolTip(String lang) {
        return name;
    }

    /**
     * @return Returns the command.
     *
     */
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
        if (platform.equals(ExternalEditor.PLATFORM_ALL)) {
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

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#addEditorCloseListener(de.miethxml.gui.editor.EditorCloseListener)
     *
     */
    public void addEditorCloseListener(EditorCloseListener listener) {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#removeEditorCloseListener(de.miethxml.gui.editor.EditorCloseListener)
     *
     */
    public void removeEditorCloseListener(EditorCloseListener listener) {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#createNewEditor()
     *
     */
    public Editor createNewEditor() {
        ExternalEditor edit = new ExternalEditor();
        edit.setCommand(getCommand());
        edit.setIcon(getIcon());
        edit.setHandles(getHandles());
        edit.setName(getName());

        return edit;
    }
}
