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
package de.miethxml.hawron.gui.io;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;


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
public class ExportFileFilter extends FileFilter implements LocaleListener {
    public static int DEFAULT_CLI_FORMAT = 0;
    public static int EXTENDED_CLI_FORMAT = 1;
    public static int ANT_BUILDFILE_FORMAT = 2;
    private String localeKey;
    private int format;
    private String description;

    /**
     *
     *
     *
     */
    public ExportFileFilter() {
        super();
        description = "";
    }

    public ExportFileFilter(String localeKey) {
        this();
        this.localeKey = localeKey;
        LocaleImpl.getInstance().addLocaleListener(this);
        this.description = LocaleImpl.getInstance().getString(localeKey);
    }

    public ExportFileFilter(
        String localeKey,
        int format) {
        this(localeKey);
        this.format = format;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     *
     */
    public boolean accept(File f) {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.filechooser.FileFilter#getDescription()
     *
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.conf.LocaleListener#langChanged()
     *
     */
    public void langChanged() {
        description = LocaleImpl.getInstance().getString(localeKey);
    }

    /**
     * @return Returns the format.
     *
     */
    public int getFormat() {
        return format;
    }

    /**
     * @param format
     *            The format to set.
     *
     */
    public void setFormat(int format) {
        this.format = format;
    }
}
