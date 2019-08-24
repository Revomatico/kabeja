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

import de.miethxml.hawron.project.ProjectExport;


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
public class ProjectExportFileFilter extends FileFilter {
    public static int DEFAULT_CLI_FORMAT = 0;
    public static int HAWRON_FORMAT = 1;
    public static int ANT_BUILDFILE_FORMAT = 2;
    private ProjectExport exporter;
    private int fileFormat = HAWRON_FORMAT;

    /**
     *
     *
     *
     */
    public ProjectExportFileFilter(ProjectExport exporter) {
        this.exporter = exporter;
        setFileFormat(exporter.getFileFormat());
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
        return exporter.getShortDescription("");
    }

    /**
     * @return Returns the exporter.
     *
     */
    public ProjectExport getExporter() {
        return exporter;
    }

    /**
     * @param exporter
     *            The exporter to set.
     *
     */
    public void setExporter(ProjectExport exporter) {
        this.exporter = exporter;
    }

    /**
     * @return Returns the fileFormat.
     */
    public int getFileFormat() {
        return fileFormat;
    }

    /**
     * @param fileFormat
     *            The fileFormat to set.
     */
    public void setFileFormat(int fileFormat) {
        this.fileFormat = fileFormat;
    }
}
