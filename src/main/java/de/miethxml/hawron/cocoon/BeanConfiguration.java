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
package de.miethxml.hawron.cocoon;

import java.io.File;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public class BeanConfiguration {
    //configuration keys
    public static String CONFIGKEY_LOGGING = "logging";
    public static String CONFIGKEY_LOGGER = "logger";
    public static String CONFIGKEY_LOGKIT = "log-kit";
    public static String CONFIGKEY_LOGLEVEL = "level";
    public static String CONFIGKEY_LOADCLASS = "load-class";
    public static String CONFIGKEY_CONFIRMEXTENSIONS = "confirm-extensions";
    public static String CONFIGKEY_FOLLOWLINKS = "follow-links";
    public static String CONFIGKEY_WORKDIR = "work-dir";
    public static String CONFIGKEY_CONFIGFILE = "config-file";
    public static String CONFIGKEY_CONTEXTDIR = "context-dir";
    public static String CONFIGKEY_CHECKSUMURI = "checksums-uri";
    public static String CONFIGKEY_ACCEPT = "accept";
    public static String CONFIGKEY_USERAGENT = "user-agent";
    public static String CONFIGKEY_INCLUDEPATTERNS = "include";
    public static String CONFIGKEY_EXCLUDEPATTERNS = "exclude";
    public static String CONFIGKEY_DESTDIR = "dest-dir";
    public static String CONFIGKEY_PRECOMPILEONLY = "precompile-only";
    public static String CONFIGKEY_URITYPE = "URITYPE";
    public static String CONFIGKEY_VERBOSE = "verbose";
    public static String CONFIGKEY_URIS = "uris";
    public static String CONFIGKEY_URISNAME = "name";
    public static String CONFIGKEY_URI = "uri";
    public static String CONFIGKEY_DEFAULTFILENAME = "default-filename";
    public static String CONFIGKEY_BROKEN_LINKS = "broken-links";
    public static String CONFIGKEY_BROKEN_LINKS_REPORT_TYPE = "type";
    public static String CONFIGKEY_BROKEN_LINKS_REPORT_FILE = "file";
    public static String CONFIGKEY_BROKEN_LINKS_GENERATE = "generate";
    public static String CONFIGKEY_BROKEN_LINKS_EXTENSION = "extension";
    public static String CONFIGKEY_URI_FILE = "uri-file";
    private boolean verbose = true;
    private boolean followlinks = true;
    private boolean precompileonly = false;
    private boolean confirmextenions = false;
    private String contextDir = "";
    private String configFile = "WEB-INF/cocoon.xconf";
    private String workDir = "work";
    private String destDir = "dest";
    private String checksumURI = "uri-checksum.log";
    private String uriFile = "";
    private boolean brokenLinksReport = false;
    private boolean brokenLinks = false;
    private String brokenLinkReportType = "";
    private String brokenLinkReportFile = "";
    private String brokenLinkReportExtension = "";
    private List loadClasses = new ArrayList();

    //a shipped version
    private String logKit = "";
    private String logger = "cli";
    private String logLevel = "ERROR";
    private String userAgent = "";
    private String accept = "";
    private String defaultFilename = "";
    private List includes = new ArrayList();
    private List excludes = new ArrayList();
    private String configLocation = "";
    private boolean contextDirResolved = false;
    private boolean logkitResolved = false;
    private boolean configFileResolved = false;

    public BeanConfiguration() {
    }

    /**
     *
     * @return Returns the accept.
     *
     */
    public String getAccept() {
        return accept;
    }

    /**
     *
     * @param accept
     *
     * The accept to set.
     *
     */
    public void setAccept(String accept) {
        this.accept = accept;
    }

    /**
     *
     * @return Returns the checksumURI.
     *
     */
    public String getChecksumURI() {
        return checksumURI;
    }

    /**
     *
     * @param checksumURI
     *
     * The checksumURI to set.
     *
     */
    public void setChecksumURI(String checksumURI) {
        File f = new File(checksumURI);

        if (!f.isAbsolute()) {
            f = new File(configLocation + File.separator + checksumURI);
        }

        this.checksumURI = f.getAbsolutePath();
    }

    /**
     *
     * @return Returns the configFile.
     *
     */
    public String getConfigFile() {
        return configFile;
    }

    /**
     *
     * @param configFile
     *
     * The configFile to set.
     *
     */
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    /**
     *
     * @return Returns the contextDir.
     *
     */
    public String getContextDir() {
        return contextDir;
    }

    /**
     *
     * @param contextDir
     *
     * The contextDir to set.
     *
     */
    public void setContextDir(String contextDir) {
        File f = new File(contextDir);

        if (!f.isAbsolute()) {
            f = new File(configLocation + File.separator + contextDir);
        }

        this.contextDir = f.getAbsolutePath();
    }

    /**
     *
     * @return Returns the destDir.
     *
     */
    public String getDestDir() {
        return destDir;
    }

    /**
     *
     * @param destDir
     *
     * The destDir to set.
     *
     */
    public void setDestDir(String destDir) {
        File f = new File(destDir);

        if (!f.isAbsolute()) {
            f = new File(configLocation + File.separator + destDir);
        }

        this.destDir = f.getAbsolutePath();
    }

    /**
     *
     * @return Returns the followlinks.
     *
     */
    public boolean isFollowlinks() {
        return followlinks;
    }

    /**
     *
     * @param followlinks
     *
     * The followlinks to set.
     *
     */
    public void setFollowlinks(boolean followlinks) {
        this.followlinks = followlinks;
    }

    /**
     *
     * @return Returns the logger.
     *
     */
    public String getLogger() {
        return logger;
    }

    /**
     *
     * @param logger
     *
     * The logger to set.
     *
     */
    public void setLogger(String logger) {
        this.logger = logger;
    }

    /**
     *
     * @return Returns the logKit.
     *
     */
    public String getLogKit() {
        return logKit;
    }

    /**
     *
     * @param logKit
     *
     * The logKit to set.
     *
     */
    public void setLogKit(String logKit) {
        File f = new File(logKit);

        if (!f.isAbsolute()) {
            f = new File(configLocation + File.separator + logKit);
        }

        this.logKit = f.getAbsolutePath();
    }

    /**
     *
     * @return Returns the logLevel.
     *
     */
    public String getLogLevel() {
        return logLevel;
    }

    /**
     *
     * @param logLevel
     *
     * The logLevel to set.
     *
     */
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    /**
     *
     * @return Returns the precompileonly.
     *
     */
    public boolean isPrecompileonly() {
        return precompileonly;
    }

    /**
     *
     * @param precompileonly
     *
     * The precompileonly to set.
     *
     */
    public void setPrecompileonly(boolean precompileonly) {
        this.precompileonly = precompileonly;
    }

    /**
     *
     * @return Returns the userAgent.
     *
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     *
     * @param userAgent
     *
     * The userAgent to set.
     *
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void addLoadClass(String classname) {
        loadClasses.add(classname);
    }

    public List getLoadClasses() {
        return loadClasses;
    }

    public void addIncludePattern(String pattern) {
        includes.add(pattern);
    }

    public void removeIncludePattern(String pattern) {
        includes.remove(pattern);
    }

    public List getIncludePatterns() {
        return includes;
    }

    public void addExcludePattern(String pattern) {
        excludes.add(pattern);
    }

    public void removeExludePattern(String pattern) {
        excludes.remove(pattern);
    }

    public List getExcludePatterns() {
        return excludes;
    }

    public void setWorkDir(String workDir) {
        File f = new File(workDir);

        if (!f.isAbsolute()) {
            f = new File(configLocation + File.separator + workDir);
        }

        this.workDir = f.getAbsolutePath();
    }

    public String getWorkDir() {
        return workDir;
    }

 
    /**
     *
     * @return Returns the confirmextentions.
     *
     */
    public boolean isConfirmextenions() {
        return confirmextenions;
    }

    /**
     *
     * @param confirmextentions
     *
     * The confirmextentions to set.
     *
     */
    public void setConfirmextenions(boolean confirmextenions) {
        this.confirmextenions = confirmextenions;
    }

    /**
     *
     * @return Returns the verbose.
     *
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     *
     * @param verbose
     *
     * The verbose to set.
     *
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     *
     * @return Returns the defaultFilename.
     *
     */
    public String getDefaultFilename() {
        return defaultFilename;
    }

    /**
     *
     * @param defaultFilename
     *
     * The defaultFilename to set.
     *
     */
    public void setDefaultFilename(String defaultFilename) {
        this.defaultFilename = defaultFilename;
    }

    /**
     *
     * @return Returns the configLocation.
     *
     */
    public String getConfigLocation() {
        return configLocation;
    }

    /**
     *
     * @param configLocation
     *
     * The configLocation to set.
     *
     */
    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    /**
     *
     * @return Returns the brokeLinksReport.
     *
     */
    public boolean isBrokenLinkReporting() {
        return brokenLinksReport;
    }

    /**
     *
     * @param brokeLinksReport
     *            The brokeLinksReport to set.
     *
     */
    public void setBrokeLinksReport(boolean brokeLinksReport) {
        this.brokenLinksReport = brokeLinksReport;
    }

    /**
     *
     * @return Returns the brokenLinkReportExtension.
     *
     */
    public String getBrokenLinkReportExtension() {
        return brokenLinkReportExtension;
    }

    /**
     *
     * @param brokenLinkReportExtension
     *            The brokenLinkReportExtension to set.
     *
     */
    public void setBrokenLinkReportExtension(String brokenLinkReportExtension) {
        this.brokenLinkReportExtension = brokenLinkReportExtension;
    }

    /**
     *
     * @return Returns the brokenLinkReportFile.
     *
     */
    public String getBrokenLinkReportFile() {
        return brokenLinkReportFile;
    }

    /**
     *
     * @param brokenLinkReportFile
     *            The brokenLinkReportFile to set.
     *
     */
    public void setBrokenLinkReportFile(String brokenLinkReportFile) {
        this.brokenLinkReportFile = brokenLinkReportFile;
    }

    /**
     *
     * @return Returns the brokenLinkReportType.
     *
     */
    public String getBrokenLinkReportType() {
        return brokenLinkReportType;
    }

    /**
     *
     * @param brokenLinkReportType
     *            The brokenLinkReportType to set.
     *
     */
    public void setBrokenLinkReportType(String brokenLinkReportType) {
        this.brokenLinkReportType = brokenLinkReportType;
    }

    /**
     *
     * @return Returns the uriFile.
     *
     */
    public String getUriFile() {
        return uriFile;
    }

    /**
     *
     * @param uriFile
     *            The uriFile to set.
     *
     */
    public void setUriFile(String uriFile) {
        this.uriFile = uriFile;
    }

    /**
     *
     * @return Returns the brokenLinks.
     *
     */
    public boolean isBrokenLinks() {
        return brokenLinks;
    }

    /**
     *
     * @param brokenLinks
     *            The brokenLinks to set.
     *
     */
    public void setBrokenLinks(boolean brokenLinks) {
        this.brokenLinks = brokenLinks;
    }

    /**
     *
     * @param excludes
     *            The excludes to set.
     *
     */
    public void setExcludePatterns(List excludes) {
        this.excludes = excludes;
    }

    /**
     *
     * @param includes
     *            The includes to set.
     *
     */
    public void setIncludePatterns(List includes) {
        this.includes = includes;
    }

    /**
     *
     * @param loadClasses
     *            The loadClasses to set.
     *
     */
    public void setLoadClasses(List loadClasses) {
        this.loadClasses = loadClasses;
    }
}
