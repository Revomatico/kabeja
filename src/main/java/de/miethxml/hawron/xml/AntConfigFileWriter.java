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

import de.miethxml.hawron.cocoon.BeanConfiguration;
import de.miethxml.hawron.gui.io.ProjectExportFileFilter;
import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.Task;

import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


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
public class AntConfigFileWriter extends AbstractFileExport {
    public final static String ELEMENT_ROOT = "project";
    public final static String ATTRIBUTE_ROOT_DEFAULT = "default";
    public final static String ATTRIBUTE_ROOT_BASEDIR = "basedir";
    public final static String ATTRIBUTE_NAME = "name";
    public final static String ATTRIBUTE_VALUE = "value";
    public final static String ATTRIBUTE_DIR = "dir";
    public final static String ATTRIBUTE_DEPEND = "depends";
    public final static String ATTRIBUTE_DESCRIPTION = "description";
    public final static String ATTRIBUTE_INCLUDES = "includes";
    public final static String ATTRIBUTE_CLASSPATHREF = "classpathref";
    public final static String ATTRIBUTE_CLASSNAME = "classname";
    public final static String ELEMENT_PROPERTY = "property";
    public final static String ELEMENT_DESCRIPTION = "description";
    public final static String ELEMENT_TARGET = "target";
    public final static String ELEMENT_TASKDEF = "taskdef";
    public final static String ELEMENT_PATH = "path";
    public final static String ELEMENT_DIRSET = "dirset";
    public final static String ELEMENT_FILESET = "fileset";
    public final static String ELEMENT_DELETE = "delete";
    public final static String TARGET_DEFAULT = "cocoon";
    public final static String TARGET_PROCESSING = "cocoon";
    public final static String TARGET_PUBLISHING = "publish";
    public final static String ID_CONTEXTDIR = "cocoon.context";
    public final static String ID_CLASSPATH = "cocoon.classpath";
    public final static String COCOON_TASK = "cocoon";
    public final static String COCOON_TASK_CLASS = "org.apache.cocoon.CocoonTask";
    private int taskCount = 0;

    /**
     *
     *
     *
     */
    public AntConfigFileWriter() {
        super();
        setFileFormat(ProjectExportFileFilter.ANT_BUILDFILE_FORMAT);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.project.ProjectExport#export(de.miethxml.project.Project,
     *
     * java.lang.String)
     *
     */
    public void export(
        Project project,
        String file) {
        Document doc = new Document();

        //get the root Element
        Element root = getAntRootElement(project);
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
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.project.ProjectExport#getDescription(java.lang.String)
     *
     */
    public String getDescription(String lang) {
        return "This will generate an Ant-buildfile, where each Task is a single Target."
        + " There is one Target \"all\", which build all targets. ";
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.project.ProjectExport#getFileExtension()
     *
     */
    public String getFileExtension() {
        return "xml";
    }

    private Element getAntRootElement(Project project) {
        Element el = new Element(ELEMENT_ROOT);
        el.setAttribute(ATTRIBUTE_NAME, project.getTitle());

        //there is no default target or maybe all together
        //el.setAttribute(ATTRIBUTE_ROOT_DEFAULT, TARGET_DEFAULT);
        el.setAttribute(ATTRIBUTE_ROOT_BASEDIR, ".");
        el.setContent(new Comment("generated with Hawron"));

        Element description = new Element(ELEMENT_DESCRIPTION);
        description.setText(project.getDescription());
        el.addContent(description);
        el.addContent(getContextPropertyElement(project));

        BeanConfiguration config = project.getCocoonBeanConfiguration();
        el.addContent(getClasspathElement(config.getContextDir()));
        el.addContent(getCocoonTaskDef());

        if (project.getTasks().size() > 0) {
            Iterator i = project.getTasks().iterator();
            taskCount = 0;

            while (i.hasNext()) {
                Task task = (Task) i.next();
                taskCount++;
                el.addContent(getProjectTaskElement(project, task));
            }

            el.addContent(getTargetAll(project));
        }

        return el;
    }

    private Element getContextPropertyElement(Project project) {
        Element el = new Element(ELEMENT_PROPERTY);
        BeanConfiguration config = project.getCocoonBeanConfiguration();
        el.setAttribute(ATTRIBUTE_NAME, ID_CONTEXTDIR);
        el.setAttribute(ATTRIBUTE_VALUE, config.getContextDir());

        return el;
    }

    private Element getClasspathElement(String contextDir) {
        Element el = new Element(ELEMENT_PATH);
        el.setAttribute("id", ID_CLASSPATH);

        Element child = new Element(ELEMENT_FILESET);
        child.setAttribute(ATTRIBUTE_DIR, "${" + ID_CONTEXTDIR
            + "}/WEB-INF/lib");
        child.setAttribute(ATTRIBUTE_INCLUDES, "*.jar");
        el.addContent(child);
        child = new Element(ELEMENT_DIRSET);
        child.setAttribute(ATTRIBUTE_DIR,
            "${" + ID_CONTEXTDIR + "}/WEB-INF/classes");
        el.addContent(child);

        return el;
    }

    private Element getCocoonTaskDef() {
        Element el = new Element(ELEMENT_TASKDEF);
        el.setAttribute(ATTRIBUTE_NAME, COCOON_TASK);
        el.setAttribute(ATTRIBUTE_CLASSNAME, COCOON_TASK_CLASS);
        el.setAttribute(ATTRIBUTE_CLASSPATHREF, ID_CLASSPATH);

        return el;
    }

    private Element getProjectTaskElement(
        Project project,
        Task task) {
        Element el = new Element(ELEMENT_TARGET);

        if (task.getID().length() > 0) {
            el.setAttribute(ATTRIBUTE_NAME, task.getID());
        } else {
            el.setAttribute(ATTRIBUTE_NAME, "task" + taskCount);
        }

        el.setAttribute(ATTRIBUTE_DESCRIPTION,
            task.getTitle() + " - " + task.getDescription());

        if (task.isCleanBuild()) {
            //delete the build dir first
            el.addContent(new Comment("cleanup the build directory"));
            el.addContent(getDeleteDirectoryElement(task.getBuildDir()));
        }

        if (task.getProcessURI().size() > 0) {
            el.addContent(new Comment("process the uris"));
            el.addContent(getTaskProcessElement(project, task));
        }

        if (task.getPublishDestinations().size() > 0) {
            el.addContent(new Comment("publish the content"));
            el.addContent(getPublishElement(project, task));
        }

        return el;
    }

    private Element getTaskProcessElement(
        Project project,
        Task task) {
        Element el;
        Element root = new Element(Project.CONFIGKEY_COCOONCONFIG);
        root.setAttribute(ATTRIBUTE_CLASSPATHREF, ID_CLASSPATH);

        //pass all setting from BeanCofiguration
        BeanConfiguration config = project.getCocoonBeanConfiguration();
        Attribute attr = new Attribute(BeanConfiguration.CONFIGKEY_FOLLOWLINKS,
                Boolean.toString(config.isFollowlinks()));
        root.setAttribute(attr);
        attr = new Attribute(BeanConfiguration.CONFIGKEY_CONFIRMEXTENSIONS,
                Boolean.toString(config.isConfirmextenions()));
        root.setAttribute(attr);
        attr = new Attribute(BeanConfiguration.CONFIGKEY_VERBOSE,
                Boolean.toString(config.isVerbose()));
        root.setAttribute(attr);
        attr = new Attribute(BeanConfiguration.CONFIGKEY_PRECOMPILEONLY,
                Boolean.toString(config.isPrecompileonly()));
        root.setAttribute(attr);

        if (config.getConfigFile().length() > 0) {
            root.setAttribute(BeanConfiguration.CONFIGKEY_CONFIGFILE,
                config.getConfigFile());
        }

        if (config.getContextDir().length() > 0) {
            root.setAttribute(BeanConfiguration.CONFIGKEY_CONTEXTDIR,
                config.getContextDir());
        }

        if (config.getWorkDir().length() > 0) {
            root.setAttribute(BeanConfiguration.CONFIGKEY_WORKDIR,
                config.getWorkDir());
        }

        if (config.getDestDir().length() > 0) {
            root.setAttribute(BeanConfiguration.CONFIGKEY_DESTDIR,
                config.getDestDir());
        }

        if (config.isBrokenLinks()) {
            root.addContent(getBrokenLinksElement(config));
        }

        if (config.getIncludePatterns().size() > 0) {
            Iterator i = config.getIncludePatterns().iterator();

            while (i.hasNext()) {
                String pattern = (String) i.next();
                el = new Element(BeanConfiguration.CONFIGKEY_INCLUDEPATTERNS);
                el.setAttribute("pattern", pattern);
                root.addContent(el);
            }
        }

        if (config.getExcludePatterns().size() > 0) {
            Iterator i = config.getExcludePatterns().iterator();

            while (i.hasNext()) {
                String pattern = (String) i.next();
                el = new Element(BeanConfiguration.CONFIGKEY_EXCLUDEPATTERNS);
                el.setAttribute("pattern", pattern);
                root.addContent(el);
            }
        }

        if ((config.getChecksumURI().length() > 0) && task.isDiffBuild()) {
            root.setAttribute(BeanConfiguration.CONFIGKEY_CHECKSUMURI,
                config.getChecksumURI());
        }

        if (config.getLoadClasses().size() > 0) {
            root.setAttribute(BeanConfiguration.CONFIGKEY_LOADCLASS,
                (String) config.getLoadClasses().get(0));
        }

        root.addContent(getLoggingElement(config));

        if (config.getUserAgent().length() > 0) {
            root.setAttribute(BeanConfiguration.CONFIGKEY_USERAGENT,
                config.getUserAgent());
        }

        if (config.getAccept().length() > 0) {
            root.setAttribute(BeanConfiguration.CONFIGKEY_ACCEPT,
                config.getAccept());
        }

        if (config.getDefaultFilename().length() > 0) {
            root.setAttribute(BeanConfiguration.CONFIGKEY_DEFAULTFILENAME,
                config.getDefaultFilename());
        }

        //change later
        if (config.getUriFile().length() > 0) {
            root.setAttribute(BeanConfiguration.CONFIGKEY_URI_FILE,
                config.getUriFile());
        }

        root.addContent(getUrisElement(task));

        return root;
    }

    private Element getDeleteDirectoryElement(String directory) {
        Element el = new Element(ELEMENT_DELETE);
        el.setAttribute(ATTRIBUTE_DIR, directory);

        return el;
    }

    private Element getPublishElement(
        Project project,
        Task task) {
        Element el = new Element("foo");

        return el;
    }

    private Element getTargetAll(Project project) {
        Element el = new Element(ELEMENT_TARGET);
        el.setAttribute(ATTRIBUTE_NAME, "all");
        el.setAttribute(ATTRIBUTE_DESCRIPTION, "build all targets");

        Iterator i = project.getTasks().iterator();
        String depends = "";
        int count = 1;

        while (i.hasNext()) {
            Task task = (Task) i.next();

            if (task.getID().length() > 0) {
                depends = depends + ", " + task.getID();
            } else {
                depends = depends + ", task" + count;
            }

            count++;
        }

        depends = depends.substring(1, depends.length());
        el.setAttribute(ATTRIBUTE_DEPEND, depends);

        return el;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.project.ProjectExport#getShortDescription(java.lang.String)
     *
     */
    public String getShortDescription(String lang) {
        return "Ant buildfile-format";
    }
}
