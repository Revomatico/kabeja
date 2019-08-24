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
import de.miethxml.hawron.net.PublishTarget;
import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.PublishDestination;
import de.miethxml.hawron.project.Task;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


/**
 *
 * This class export the Project to the default fileformat,which is independed
 * from
 *
 * Cocoon and will not work with Cocoon or ANT.
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
public class HawronConfigFileWriter extends AbstractFileExport {
    /**
     *
     *
     *
     */
    public HawronConfigFileWriter() {
        super();
        setFileFormat(ProjectExportFileFilter.HAWRON_FORMAT);
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
        Element root = getProjectElement(project);
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
        return "The default format";
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.project.ProjectExport#getFileExtension()
     *
     */
    public String getFileExtension() {
        return "hconf";
    }

    private Element getProjectElement(Project project) {
        Element root = new Element(Project.CONFIGKEY_PROJECTROOT);
        root.setAttribute("id", project.getID());

        Element el = new Element(Project.CONFIGKEY_TITLE);
        el.setText(project.getTitle());
        root.addContent(el);
        el = new Element(Project.CONFIGKEY_DESCRIPTION);
        el.setText(project.getDescription());
        root.addContent(el);

        //add the publishtargets
        el = new Element(Project.CONFIGKEY_PUBLISHING);

        Iterator i = project.getPublishTargets().iterator();

        while (i.hasNext()) {
            PublishTarget target = (PublishTarget) i.next();
            el.addContent(getPublishTargetElement(target));
        }

        root.addContent(el);

        //add the tasks
        Element processing = getCLIRootElement(project);
        i = project.getTasks().iterator();

        while (i.hasNext()) {
            Task t = (Task) i.next();
            processing.addContent(getTaskElement(t));
        }

        root.addContent(processing);

        return root;
    }

    private Element getPublishTargetElement(PublishTarget target) {
        Element site = new Element(Project.CONFIGKEY_PUBLISHTARGET);
        site.setAttribute("id", target.getID());

        Element child = new Element(Project.CONFIGKEY_PUBISHTARGET_TITLE);
        child.addContent(new Text(target.getTitle()));
        site.addContent(child);
        child = new Element(Project.CONFIGKEY_PUBISHTARGET_USERNAME);
        child.addContent(new Text(target.getUsername()));
        site.addContent(child);
        child = new Element(Project.CONFIGKEY_PUBISHTARGET_PASSWORD);
        child.addContent(new Text(target.getPassword()));
        site.addContent(child);
        child = new Element(Project.CONFIGKEY_PUBISHTARGET_URI);
        child.addContent(new Text(target.getURI()));
        site.addContent(child);
        child = new Element(Project.CONFIGKEY_PUBISHTARGET_PROTOCOL);
        child.addContent(new Text(target.getProtocol()));
        site.addContent(child);

        return site;
    }

    private Element getTaskElement(Task task) {
        Element el = new Element(Project.CONFIGKEY_TASK);
        el.setAttribute("id", task.getID());
        el.setAttribute(Task.CONFIGKEY_DESTINATION, task.getBuildDir());
        el.setAttribute(Task.CONFIGKEY_CLEANBUILD,
            Boolean.toString(task.isCleanBuild()));
        el.setAttribute(Task.CONFIGKEY_DIFF_BUILD,
            Boolean.toString(task.isDiffBuild()));

        Element child = new Element(Task.CONFIGKEY_TITLE);
        child.addContent(task.getTitle());
        el.addContent(child);
        child = new Element(Task.CONFIGKEY_DESCRIPTION);
        child.addContent(task.getDescription());
        el.addContent(child);
        child = new Element(Task.CONFIGKEY_DOCUMENTROOT);
        child.addContent(task.getDocRoot());
        el.addContent(child);

        //add the process URIs
        el.addContent(getUrisElement(task));

        //add publish
        //addPublishDestinations(el, task.getPublishDestinations());
        Iterator i = task.getPublishDestinations().iterator();

        while (i.hasNext()) {
            PublishDestination pd = (PublishDestination) i.next();
            el.addContent(getPublishDestinationElement(pd));
        }

        return el;
    }

    private Element getPublishDestinationElement(PublishDestination pd) {
        Element child = new Element(PublishDestination.CONFIGKEY_PUBLISHROOT);
        child.setAttribute(PublishDestination.CONFIGKEY_PUBLISHTARGETID,
            pd.getTargetID());

        Element el = new Element(PublishDestination.CONFIGKEY_PUBLISHDEST);
        el.setText(pd.getDestination());
        child.addContent(el);
        el = new Element(PublishDestination.CONFIGKEY_PUBLISHTITLE);
        el.setText(pd.getTitle());
        child.addContent(el);
        el = new Element(PublishDestination.CONFIGKEY_PUBLISHSOURCE);
        el.setText(pd.getSource());
        child.addContent(el);

        return child;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.project.ProjectExport#getShortDescription(java.lang.String)
     *
     */
    public String getShortDescription(String lang) {
        return "Hawron-format";
    }
}
