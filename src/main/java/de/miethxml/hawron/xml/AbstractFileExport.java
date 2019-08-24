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

import java.util.ArrayList;
import java.util.Iterator;

import de.miethxml.hawron.cocoon.BeanConfiguration;
import de.miethxml.hawron.gui.io.ProjectExportFileFilter;
import de.miethxml.hawron.project.ProcessURI;
import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.ProjectExport;
import de.miethxml.hawron.project.Task;

import org.jdom.Attribute;
import org.jdom.Element;


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
public abstract class AbstractFileExport implements ProjectExport {
    protected boolean relativePaths = false;
    protected String location = "";
    protected int fileFormat = ProjectExportFileFilter.HAWRON_FORMAT;

    /**
     *
     *
     *
     */
    public AbstractFileExport() {
        super();
    }

    public Element getCLIRootElement(Project project) {
        Element root = new Element(Project.CONFIGKEY_COCOONCONFIG);

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

        Element el;

        if (config.getConfigFile().length() > 0) {
            el = new Element(BeanConfiguration.CONFIGKEY_CONFIGFILE);
            el.setText(config.getConfigFile());
            root.addContent(el);
        }

        if (config.getContextDir().length() > 0) {
            el = new Element(BeanConfiguration.CONFIGKEY_CONTEXTDIR);

            if (relativePaths) {
                el.setText(getRelativePath(config.getContextDir()));
            } else {
                el.setText(config.getContextDir());
            }

            root.addContent(el);
        }

        if (config.getWorkDir().length() > 0) {
            el = new Element(BeanConfiguration.CONFIGKEY_WORKDIR);

            if (relativePaths) {
                el.setText(getRelativePath(config.getWorkDir()));
            } else {
                el.setText(config.getWorkDir());
            }

            root.addContent(el);
        }

        if (config.getDestDir().length() > 0) {
            el = new Element(BeanConfiguration.CONFIGKEY_DESTDIR);

            if (relativePaths) {
                el.setText(getRelativePath(config.getDestDir()));
            } else {
                el.setText(config.getDestDir());
            }

            root.addContent(el);
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

        if (config.getChecksumURI().length() > 0) {
            el = new Element(BeanConfiguration.CONFIGKEY_CHECKSUMURI);
            el.setText(config.getChecksumURI());
            root.addContent(el);
        }

        if (config.getLoadClasses().size() > 0) {
            Iterator i = config.getLoadClasses().iterator();

            while (i.hasNext()) {
                String pattern = (String) i.next();
                el = new Element(BeanConfiguration.CONFIGKEY_LOADCLASS);
                el.setText(pattern);
                root.addContent(el);
            }
        }

        root.addContent(getLoggingElement(config));

        if (config.getUserAgent().length() > 0) {
            el = new Element(BeanConfiguration.CONFIGKEY_USERAGENT);
            el.setText(config.getUserAgent());
            root.addContent(el);
        }

        if (config.getAccept().length() > 0) {
            el = new Element(BeanConfiguration.CONFIGKEY_ACCEPT);
            el.setText(config.getAccept());
            root.addContent(el);
        }

        if (config.getDefaultFilename().length() > 0) {
            el = new Element(BeanConfiguration.CONFIGKEY_DEFAULTFILENAME);
            el.setText(config.getDefaultFilename());
            root.addContent(el);
        }

        //change later
        if (config.getUriFile().length() > 0) {
            el = new Element(BeanConfiguration.CONFIGKEY_URI_FILE);
            el.setText(config.getUriFile());
            root.addContent(el);
        }

        return root;
    }

    public Element getUrisElement(Task task) {
        ArrayList uris = task.getProcessURI();
        Element el = new Element(BeanConfiguration.CONFIGKEY_URIS);
        el.setAttribute(BeanConfiguration.CONFIGKEY_CONFIRMEXTENSIONS,
            Boolean.toString(task.isConfirmExtensions()));
        el.setAttribute(BeanConfiguration.CONFIGKEY_FOLLOWLINKS,
            Boolean.toString(task.isFollowLinks()));
        el.setAttribute(BeanConfiguration.CONFIGKEY_URISNAME, task.getTitle());
        el.setAttribute(ProcessURI.CONFIGKEY_DEST, task.getBuildDir());

        Iterator i = uris.iterator();

        while (i.hasNext()) {
            ProcessURI uri = (ProcessURI) i.next();
            Element child = new Element(BeanConfiguration.CONFIGKEY_URI);
            child.setAttribute(ProcessURI.CONFIGKEY_SRC, uri.getUri());
            child.setAttribute(ProcessURI.CONFIGKEY_TYPE, uri.getType());

            if (uri.getSrcPrefix().length() > 0) {
                child.setAttribute(ProcessURI.CONFIGKEY_SRCPREFIX,
                    uri.getSrcPrefix());
            }

            el.addContent(child);
        }

        return el;
    }

    protected String getRelativePath(String path) {
        if (path.startsWith(location)) {
            return path.substring(location.length() + 1);
        }

        return path;
    }

    public Element getBrokenLinksElement(BeanConfiguration config) {
        Element el = new Element(BeanConfiguration.CONFIGKEY_BROKEN_LINKS);

        if (config.getBrokenLinkReportExtension().length() > 0) {
            el.setAttribute(BeanConfiguration.CONFIGKEY_BROKEN_LINKS_EXTENSION,
                config.getBrokenLinkReportExtension());
        }

        if (config.getBrokenLinkReportFile().length() > 0) {
            el.setAttribute(BeanConfiguration.CONFIGKEY_BROKEN_LINKS_REPORT_FILE,
                config.getBrokenLinkReportFile());
        }

        if (config.getBrokenLinkReportType().length() > 0) {
            el.setAttribute(BeanConfiguration.CONFIGKEY_BROKEN_LINKS_REPORT_TYPE,
                config.getBrokenLinkReportType());
        }

        el.setAttribute(BeanConfiguration.CONFIGKEY_BROKEN_LINKS_GENERATE,
            Boolean.toString(config.isBrokenLinkReporting()));

        return el;
    }

    public Element getLoggingElement(BeanConfiguration config) {
        Element el = new Element(BeanConfiguration.CONFIGKEY_LOGGING);

        if (config.getLogger().length() > 0) {
            el.setAttribute(BeanConfiguration.CONFIGKEY_LOGGER,
                config.getLogger());
        }

        if (config.getLogKit().length() > 0) {
            if (relativePaths) {
                el.setAttribute(BeanConfiguration.CONFIGKEY_LOGKIT,
                    getRelativePath(config.getLogKit()));
            } else {
                el.setAttribute(BeanConfiguration.CONFIGKEY_LOGKIT,
                    config.getLogKit());
            }
        }

        if (config.getLogLevel().length() > 0) {
            el.setAttribute(BeanConfiguration.CONFIGKEY_LOGLEVEL,
                config.getLogLevel());
        }

        return el;
    }

    public void setRelativePathEnabled(
        String path,
        boolean b) {
        this.relativePaths = b;
        this.location = path;
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
