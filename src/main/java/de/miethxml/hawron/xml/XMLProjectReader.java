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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Iterator;
import java.util.List;

import de.miethxml.hawron.cocoon.BeanConfiguration;
import de.miethxml.hawron.gui.io.ProjectExportFileFilter;
import de.miethxml.hawron.net.PublishTarget;
import de.miethxml.hawron.project.ProcessURI;
import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.PublishDestination;
import de.miethxml.hawron.project.Task;

import de.miethxml.toolkit.conf.LocaleImpl;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import org.jdom.input.SAXBuilder;


/**
 * Parse the CLI and the Hawron fileformat.
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
public class XMLProjectReader {
    private Project project;
    private boolean extendedFormat = false;
    private int taskCount;

    /**
     *
     *
     *
     */
    public XMLProjectReader() {
        super();
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project parseURI(String uri) {
        this.project = new Project();
        parse(uri);

        return this.project;
    }

    public Project parseURI(
        String uri,
        Project project) {
        this.project = project;
        parse(uri);

        return this.project;
    }

    private void parse(String uri) {
        //get the document
        Document doc = null;
        SAXBuilder builder = new SAXBuilder();

        try {
            doc = builder.build(uri);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (doc != null) {
            //set config location
            BeanConfiguration config = project.getCocoonBeanConfiguration();
            File f = new File(uri);
            config.setConfigLocation(f.getParent());

            Element root = doc.getRootElement();
            project.setFilename(uri);

            if (root.getName().equals(Project.CONFIGKEY_PROJECTROOT)) {
                this.extendedFormat = true;
                parseProjectDocument(root);
                project.setParsedFileFormat(ProjectExportFileFilter.HAWRON_FORMAT);
            } else if (root.getName().equals(Project.CONFIGKEY_COCOONCONFIG)) {
                this.extendedFormat = false;
                this.taskCount = 1;
                project.setParsedFileFormat(ProjectExportFileFilter.DEFAULT_CLI_FORMAT);
                parseCLIConfDocument(root);
            } else {
                //got nothing or add here ANT-buildfileparsing from
                //cocoon-anttask here
            }
        }
    }

    private void parseProjectDocument(Element root) {
        Attribute attr = root.getAttribute("id");

        if (attr != null) {
            project.setID(attr.getValue());
        }

        List<Element> children = root.getChildren();
        Iterator i = children.iterator();

        while (i.hasNext()) {
            Element el = (Element) i.next();

            if (el.getName().equals(Project.CONFIGKEY_TITLE)) {
                project.setTitle(el.getText());
            } else if (el.getName().equals(Project.CONFIGKEY_DESCRIPTION)) {
                project.setDescription(el.getText());
            } else if (el.getName().equals(Project.CONFIGKEY_PUBLISHING)) {
                parsePublishingElements(el);
            } else if (el.getName().equals(Project.CONFIGKEY_COCOONCONFIG)) {
                parseCLIConfDocument(el);
            }
        }
    }

    private void parseCLIConfDocument(Element root) {
        BeanConfiguration config = project.getCocoonBeanConfiguration();

        // get the default settings
        List attributes = root.getAttributes();
        Iterator i = attributes.iterator();

        while (i.hasNext()) {
            Attribute attr = (Attribute) i.next();

            if (attr.getName().equals(BeanConfiguration.CONFIGKEY_CONFIRMEXTENSIONS)) {
                config.setConfirmextenions(Boolean.valueOf(attr.getValue())
                                                  .booleanValue());
            } else if (attr.getName().equals(BeanConfiguration.CONFIGKEY_FOLLOWLINKS)) {
                config.setFollowlinks(Boolean.valueOf(attr.getValue())
                                             .booleanValue());
            } else if (attr.getName().equals(BeanConfiguration.CONFIGKEY_VERBOSE)) {
                config.setVerbose(Boolean.valueOf(attr.getValue()).booleanValue());
            } else if (attr.getName().equals(BeanConfiguration.CONFIGKEY_PRECOMPILEONLY)) {
                config.setPrecompileonly(Boolean.valueOf(attr.getValue())
                                                .booleanValue());
            }
        }

        // get the config-elements
        List elements = root.getChildren();
        i = elements.iterator();

        while (i.hasNext()) {
            Element el = (Element) i.next();

            if (el.getName().equals(BeanConfiguration.CONFIGKEY_CONTEXTDIR)) {
                config.setContextDir(el.getText());
            } else if (el.getName().equals(BeanConfiguration.CONFIGKEY_CONFIGFILE)) {
                config.setConfigFile(el.getText());
            } else if (el.getName().equals(BeanConfiguration.CONFIGKEY_WORKDIR)) {
                config.setWorkDir(el.getText());
            } else if (el.getName().equals(BeanConfiguration.CONFIGKEY_DESTDIR)) {
                config.setDestDir(el.getText());
            } else if (el.getName().equals(BeanConfiguration.CONFIGKEY_USERAGENT)) {
                config.setUserAgent(el.getText());
            } else if (el.getName().equals(BeanConfiguration.CONFIGKEY_DEFAULTFILENAME)) {
                config.setDefaultFilename(el.getText());
            } else if (el.getName().equals(BeanConfiguration.CONFIGKEY_ACCEPT)) {
                config.setAccept(el.getText());
            } else if (el.getName().equals(BeanConfiguration.CONFIGKEY_CHECKSUMURI)) {
                config.setChecksumURI(el.getText());
            } else if (el.getName().equals(BeanConfiguration.CONFIGKEY_LOADCLASS)) {
                config.addLoadClass(el.getText());
            } else if (el.getName().equals(BeanConfiguration.CONFIGKEY_LOGGING)) {
                //configure the logging
                Iterator logging = el.getAttributes().iterator();

                while (logging.hasNext()) {
                    Attribute option = (Attribute) logging.next();

                    if (option.getName().equals(BeanConfiguration.CONFIGKEY_LOGKIT)) {
                        config.setLogKit(option.getValue());
                    } else if (option.getName().equals(BeanConfiguration.CONFIGKEY_LOGGER)) {
                        config.setLogger(option.getValue());
                    } else if (option.getName().equals(BeanConfiguration.CONFIGKEY_LOGLEVEL)) {
                        config.setLogLevel(option.getValue());
                    }
                }
            } else if (el.getName().equals(BeanConfiguration.CONFIGKEY_INCLUDEPATTERNS)) {
                Attribute pattern = el.getAttribute("pattern");

                if (pattern != null) {
                    config.addIncludePattern(pattern.getValue());
                }
            } else if (el.getName().equals(BeanConfiguration.CONFIGKEY_EXCLUDEPATTERNS)) {
                Attribute pattern = el.getAttribute("pattern");

                if (pattern != null) {
                    config.addExcludePattern(pattern.getValue());
                }
            } else if (el.getName().equals(BeanConfiguration.CONFIGKEY_URI)
                    && !extendedFormat) {
                createTaskFromURI(el);
            } else if (el.getName().equals(BeanConfiguration.CONFIGKEY_URIS)
                    && !extendedFormat) {
                createTaskFromURIList(el);
            } else if (el.getName().equals(Project.CONFIGKEY_TASK)
                    && extendedFormat) {
                parseTaskElement(el);
            } else if (el.getName().equals(BeanConfiguration.CONFIGKEY_BROKEN_LINKS)) {
                parseBrokenLinksElement(el);
            } else if (el.getName().equals(BeanConfiguration.CONFIGKEY_URI_FILE)) {
                //config.setUriFile(el.getText());
                parseURIFileElement(el);
            }
        }
    }

    private void parsePublishingElements(Element publishing) {
        Iterator i = publishing.getChildren().iterator();

        while (i.hasNext()) {
            Element el = (Element) i.next();

            if (el.getName().equals(Project.CONFIGKEY_PUBLISHTARGET)) {
                parsePublishTargetElement(el);
            }
        }
    }

    private void parsePublishTargetElement(Element el) {
        PublishTarget target = new PublishTarget();
        target.setID(el.getAttributeValue("id"));

        Iterator i = el.getChildren().iterator();

        while (i.hasNext()) {
            Element child = (Element) i.next();

            if (child.getName().equals(Project.CONFIGKEY_PUBISHTARGET_TITLE)) {
                target.setTitle(child.getText());
            } else if (child.getName().equals(Project.CONFIGKEY_PUBISHTARGET_USERNAME)) {
                target.setUsername(child.getText());
            } else if (child.getName().equals(Project.CONFIGKEY_PUBISHTARGET_PASSWORD)) {
                target.setPassword(child.getText());
            } else if (child.getName().equals(Project.CONFIGKEY_PUBISHTARGET_PROTOCOL)) {
                target.setProtocol(child.getText());
            } else if (child.getName().equals(Project.CONFIGKEY_PUBISHTARGET_URI)) {
                target.setURI(child.getText());
            }
        }

        project.addPublishTarget(target);
    }

    private void parsePublishElement(
        Task task,
        Element el) {
        PublishDestination pd = new PublishDestination();
        pd.setTargetID(el.getAttributeValue(
                PublishDestination.CONFIGKEY_PUBLISHTARGETID));

        Iterator i = el.getChildren().iterator();

        while (i.hasNext()) {
            Element child = (Element) i.next();

            if (child.getName().equals(PublishDestination.CONFIGKEY_PUBLISHTITLE)) {
                pd.setTitle(child.getText());
            } else if (child.getName().equals(PublishDestination.CONFIGKEY_PUBLISHDEST)) {
                pd.setDestination(child.getText());
            } else if (child.getName().equals(PublishDestination.CONFIGKEY_PUBLISHSOURCE)) {
                pd.setSource(child.getText());
            }
        }

        task.addPublishDestination(pd);
    }

    private void parseTaskElement(Element taskElement) {
        Task task = new Task();
       
        Iterator i = taskElement.getAttributes().iterator();

        while (i.hasNext()) {
            Attribute attr = (Attribute) i.next();

            if (attr.getName().equals("id")) {
                task.setID(attr.getValue());
            } else if (attr.getName().equals(Task.CONFIGKEY_CLEANBUILD)) {
                task.setCleanBuild(Boolean.valueOf(attr.getValue())
                                          .booleanValue());
            } else if (attr.getName().equals(Task.CONFIGKEY_DIFF_BUILD)) {
                task.setDiffBuild(Boolean.valueOf(attr.getValue()).booleanValue());
            } else if (attr.getName().equals(Task.CONFIGKEY_DESTINATION)) {
                task.setBuildDir(attr.getValue());
            }
        }

        i = taskElement.getChildren().iterator();

        while (i.hasNext()) {
            Element el = (Element) i.next();

            if (el.getName().equals(Task.CONFIGKEY_DESCRIPTION)) {
                task.setDescription(el.getText());
            } else if (el.getName().equals(Task.CONFIGKEY_TITLE)) {
                task.setTitle(el.getText());
            } else if (el.getName().equals(Task.CONFIGKEY_DOCUMENTROOT)) {
                task.setDocRoot(el.getText());
            } else if (el.getName().equals(BeanConfiguration.CONFIGKEY_URIS)) {
                parseURIListElement(task, el);
            } else if (el.getName().equals(PublishDestination.CONFIGKEY_PUBLISHROOT)) {
                parsePublishElement(task, el);
            }
        }

        project.addTask(task);
    }

    private void createTaskFromURI(Element uri) {
        Task task = new Task();
       
        task.setTitle(LocaleImpl.getInstance().getString("project.task.default.name")
            + " " + taskCount++);

        //there is only one processURI for the task
        ProcessURI processUri = new ProcessURI();
        Iterator i = uri.getAttributes().iterator();

        while (i.hasNext()) {
            Attribute attr = (Attribute) i.next();

            if (attr.getName().equals(ProcessURI.CONFIGKEY_DEST)) {
                task.setBuildDir(attr.getValue());
                processUri.setDest(attr.getValue());
            } else if (attr.getName().equals(ProcessURI.CONFIGKEY_SRCPREFIX)) {
                processUri.setSrcPrefix(attr.getValue());
            } else if (attr.getName().equals(ProcessURI.CONFIGKEY_SRC)) {
                processUri.setUri(attr.getValue());
            } else if (attr.getName().equals(ProcessURI.CONFIGKEY_TYPE)) {
                processUri.setType(attr.getValue());
            }
        }

        task.addProcessURI(processUri);
        project.addTask(task);
    }

    private void createTaskFromURIList(Element uris) {
        Task task = new Task();
       
        if (uris.getAttribute("name") != null) {
            task.setTitle(uris.getAttributeValue("name"));
        } else {
            task.setTitle(LocaleImpl.getInstance().getString("project.task.default.name")
                + " " + taskCount++);
        }

        parseURIListElement(task, uris);
        project.addTask(task);
    }

    private void parseURIListElement(
        Task task,
        Element el) {
        int subtask = 1;

        //process the attributes
        Iterator i = el.getAttributes().iterator();
        String srcPrefix = null;

        //default
        String type = "append";

        while (i.hasNext()) {
            Attribute attr = (Attribute) i.next();

            if (attr.getName().equals(BeanConfiguration.CONFIGKEY_FOLLOWLINKS)) {
                task.setFollowLinks(Boolean.valueOf(attr.getValue())
                                           .booleanValue());
            } else if (attr.getName().equals(BeanConfiguration.CONFIGKEY_CONFIRMEXTENSIONS)) {
                task.setConfirmExtensions(Boolean.valueOf(attr.getValue())
                                                 .booleanValue());
            } else if (attr.getName().equals(ProcessURI.CONFIGKEY_DEST)) {
                task.setBuildDir(attr.getValue());
            } else if (attr.getName().equals(ProcessURI.CONFIGKEY_TYPE)) {
                type = attr.getValue();
            } else if (attr.getName().equals(ProcessURI.CONFIGKEY_SRCPREFIX)) {
                srcPrefix = attr.getValue();
            }
        }

        //add the uri elements
        i = el.getChildren().iterator();

        while (i.hasNext()) {
            Element uri = (Element) i.next();

            if (uri.getName().equals(BeanConfiguration.CONFIGKEY_URI)) {
                ProcessURI puri = new ProcessURI();
                puri.setDest(task.getBuildDir());

                if (srcPrefix != null) {
                    puri.setSrcPrefix(srcPrefix);
                }

                puri.setType(type);
                parseURIElement(puri, uri);

                if (puri.getDest().equals(task.getBuildDir())
                        || (task.getBuildDir().length() == 0)) {
                    if (task.getBuildDir().length() == 0) {
                        task.setBuildDir(puri.getDest());
                    }

                    task.addProcessURI(puri);
                } else {
                    //we handle only one destination in a Task, so
                    //create a new Task here with the different
                    //destination
                    Task t = new Task();
                  
                    t.setTitle(task.getTitle() + "-" + (subtask++));
                    t.setFollowLinks(task.isFollowLinks());
                    t.setConfirmExtensions(t.isConfirmExtensions());
                    t.setBuildDir(puri.getDest());
                    t.addProcessURI(puri);
                    project.addTask(t);
                }
            }
        }
    }

    private void parseURIElement(
        ProcessURI uri,
        Element el) {
        Iterator i = el.getAttributes().iterator();

        while (i.hasNext()) {
            Attribute attr = (Attribute) i.next();

            if (attr.getName().equals(ProcessURI.CONFIGKEY_DEST)) {
                uri.setDest(attr.getValue());
            } else if (attr.getName().equals(ProcessURI.CONFIGKEY_SRCPREFIX)) {
                uri.setSrcPrefix(attr.getValue());
            } else if (attr.getName().equals(ProcessURI.CONFIGKEY_SRC)) {
                uri.setUri(attr.getValue());
            } else if (attr.getName().equals(ProcessURI.CONFIGKEY_TYPE)) {
                uri.setType(attr.getValue());
            }
        }
    }

    private void parseURIFileElement(Element el) {
        BeanConfiguration config = project.getCocoonBeanConfiguration();

        //we create a task for this element
        Task t = new Task();
       
        t.setFollowLinks(config.isFollowlinks());
        t.setConfirmExtensions(config.isConfirmextenions());
        t.setBuildDir(config.getDestDir());
        t.setTitle(LocaleImpl.getInstance().getString("project.task.default.name")
            + " " + taskCount++);

        //resolve the uri-file
        File f = new File(el.getText());

        if (!f.exists()) {
            //resolve relative to the config-file
            File cf = new File(project.getFilename());
            f = new File(cf.getParent() + File.separator + el.getText());
        }

        if (f.exists() && f.isFile()) {
            try {
                BufferedReader in = new BufferedReader(new FileReader(f));
                String src;

                while ((src = in.readLine()) != null) {
                    ProcessURI uri = new ProcessURI();
                    uri.setUri(src);
                    uri.setDest(config.getDestDir());
                    t.addProcessURI(uri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        project.addTask(t);
    }

    private void parseBrokenLinksElement(Element el) {
        BeanConfiguration config = project.getCocoonBeanConfiguration();
        config.setBrokenLinks(true);

        Iterator i = el.getAttributes().iterator();

        while (i.hasNext()) {
            Attribute attr = (Attribute) i.next();

            if (attr.getName().equals(BeanConfiguration.CONFIGKEY_BROKEN_LINKS_REPORT_TYPE)) {
                config.setBrokenLinkReportType(attr.getValue());
            } else if (attr.getName().equals(BeanConfiguration.CONFIGKEY_BROKEN_LINKS_REPORT_FILE)) {
                config.setBrokenLinkReportFile(attr.getValue());
            } else if (attr.getName().equals(BeanConfiguration.CONFIGKEY_BROKEN_LINKS_GENERATE)) {
                config.setBrokeLinksReport(Boolean.valueOf(attr.getValue())
                                                  .booleanValue());
            } else if (attr.getName().equals(BeanConfiguration.CONFIGKEY_BROKEN_LINKS_EXTENSION)) {
                config.setBrokenLinkReportExtension(attr.getValue());
            }
        }
    }
}
