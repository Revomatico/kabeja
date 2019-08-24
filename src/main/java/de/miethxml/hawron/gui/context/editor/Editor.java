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

import javax.swing.Icon;


/**
 *
 * This interface handle an editor.
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
public interface Editor {
    public void init();

    public void open(String file);

    /**
     *
     * This method is called when a file is selected and should give the
     * information back, if
     *
     * the editor can edit this type of file or not.
     *
     * @param file
     *
     * @return true if the file is supported otherwise false
     *
     */
    public boolean isSupported(String file);

    public void save();

    /**
     *
     * Edit a new file in the given directory.
     *
     * @param directory
     *
     */
    public void newFile(String directory);

    public void setVisible(boolean state);

    public Icon getIcon();

    public String getToolTip(String lang);

    public Editor createNewEditor();
}
