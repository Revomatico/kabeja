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
package de.miethxml.hawron.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Iterator;

import de.miethxml.hawron.gui.io.ProjectExportFileFilter;
import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.Task;

import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


/**
 *
 * This class will export the project to Cocoon-CLI configuration file,
 *
 * which can used with the Cocoon CLI interface.
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
public class CLIConfigFileWriter extends AbstractFileExport {
    private Project project;

    //private boolean extendedFormat = true;
    private Document doc;
    private boolean relativePaths = false;
    private String location;

    /**
     *
     *
     *
     */
    public CLIConfigFileWriter() {
        super();
        setFileFormat(ProjectExportFileFilter.DEFAULT_CLI_FORMAT);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.project.ProjectExport#export(de.miethxml.project.Project,
     *      java.lang.String)
     *
     */
    public void export(
        Project project,
        String file) {
        Document doc = new Document();

        //get the root Element
        Element root = getCLIRootElement(project);
        root.addContent(new Comment("generated with Hawron"));

        //add the uris from the tasks
        Iterator i = project.getTasks().iterator();

        while (i.hasNext()) {
            Task task = (Task) i.next();
            root.addContent(getUrisElement(task));
        }

        doc.setRootElement(root);

        File f = new File(file);

        try {
            Format format = Format.getPrettyFormat();
            XMLOutputter out = new XMLOutputter(format);
            FileWriter writer = new FileWriter(f);
            out.output(doc, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.project.ProjectExport#getDescription(java.lang.String)
     *
     */
    public String getDescription(String lang) {
        return "This will generate a CocoonCLI file, which you can use with cocoon -cli -x my.xconf."
        + "The Tasks are generate as uris-block for each Task";
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.project.ProjectExport#getFileExtension()
     *
     */
    public String getFileExtension() {
        return "xconf";
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.project.ProjectExport#setRelativePathEnabled(java.lang.String,
     *      boolean)
     *
     */
    public void setRelativePathEnabled(
        String path,
        boolean b) {
        super.setRelativePathEnabled(path, b);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.project.ProjectExport#getShortDescription(java.lang.String)
     *
     */
    public String getShortDescription(String lang) {
        return "Cocoon CLI format";
    }
}
