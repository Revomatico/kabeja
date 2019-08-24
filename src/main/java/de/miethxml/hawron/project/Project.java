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
package de.miethxml.hawron.project;

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.miethxml.hawron.cocoon.BeanConfiguration;
import de.miethxml.hawron.cocoon.CocoonBeanController;
import de.miethxml.hawron.cocoon.CocoonBeanFactory;
import de.miethxml.hawron.net.PublishListener;
import de.miethxml.hawron.net.PublishTarget;
import de.miethxml.hawron.net.Publisher;
import de.miethxml.hawron.net.PublisherImpl;
import de.miethxml.hawron.project.helper.TaskModel;
import de.miethxml.hawron.xml.XMLProjectReader;
import de.miethxml.toolkit.io.Utilities;

import org.apache.log.Logger;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public class Project implements ProcessListener, PublishListener {
    //the config keys, maybe move this later to a config-class
    public static String CONFIGKEY_PROJECTROOT = "project";
    public static String CONFIGKEY_TITLE = "title";
    public static String CONFIGKEY_COCOONCONFIG = "cocoon";
    public static String CONFIGKEY_DESCRIPTION = "description";
    public static String CONFIGKEY_PUBLISHING = "publishing";
    public static String CONFIGKEY_PUBLISHTARGET = "publish-target";
    public static String CONFIGKEY_TASK = "task";
    public static String CONFIGKEY_TASKTITLE = "title";
    public static String CONFIGKEY_TASKDOCROOT = "docroot";
    public static String CONFIGKEY_TASKDESCRIPTION = "description";
    public static String CONFIGKEY_TASKDESTDIR = "dest-dir";
    public static String CONFIGKEY_PUBISHTARGET_TITLE = "title";
    public static String CONFIGKEY_PUBISHTARGET_USERNAME = "username";
    public static String CONFIGKEY_PUBISHTARGET_PASSWORD = "password";
    public static String CONFIGKEY_PUBISHTARGET_URI = "uri";
    public static String CONFIGKEY_PUBISHTARGET_PROTOCOL = "protocol";
    private static String DEFAULT_CONFIG_DIRECTORY = ".hawron";
    private Logger log;
    private String title;
    private String ID;
    private String contextDir;
    private String description;
    private String filename;
    private ArrayList targets;
    private ArrayList tasks;
    private ArrayList listeners;
    private ArrayList configListeners;
    private TargetsListModel targetlistmodel;
    boolean thread;
    private int processtask;
    private ProjectProcessEvent processEvent;
    private ProjectPublishingEvent publishEvent;
    private Task activeTask;
    private Thread t;
    private Publisher publisher;
    private boolean canceled = false;
    private BeanConfiguration cocoonBeanConfiguration;
    private int parsedFileFormat = -1;
    private boolean processing;
    private boolean publishing;

    //file handling
    private boolean saved = true;
    private boolean relativePaths = false;
    private String configLocation;

    //new BeeanController
    private CocoonBeanController cocoonController;

    //new models
    private TaskModel taskModel;

    /**
     *
     *
     *
     */
    public Project() {
        super();
        title = "";
        ID = "";
        contextDir = "";
        description = "";
        filename = "";

        targets = new ArrayList();
        tasks = new ArrayList();
        listeners = new ArrayList();
        configListeners = new ArrayList();
        thread = false;
        processtask = -1;

        targetlistmodel = new TargetsListModel();

        cocoonBeanConfiguration = new BeanConfiguration();
        configLocation = "";
        taskModel = new TaskModel(this);
    }

    /**
     * @return
     */
    public String getContextPath() {
        return cocoonBeanConfiguration.getContextDir();
    }

    public int getPublishTargetCount() {
        return targets.size();
    }

    public PublishTarget getPublishTarget(int index) {
        if ((index >= 0) && (index < targets.size())) {
            return (PublishTarget) targets.get(index);
        }

        return null;
    }

    public PublishTarget getPublishTarget(String id) {
        Iterator i = targets.iterator();

        while (i.hasNext()) {
            PublishTarget p = (PublishTarget) i.next();

            if (p.getID().equals(id)) {
                return p;
            }
        }

        return null;
    }

    public void addPublishTarget(PublishTarget target) {
        targets.add(target);
        targetlistmodel.firePublishTargetUpdate();
    }

    public void replacePublishTarget(
        PublishTarget old,
        PublishTarget newtarget) {
        targets.set(targets.indexOf(old), newtarget);
        targetlistmodel.firePublishTargetUpdate();
        saved = false;
        fireProjectConfigChangedEvent();
    }

    public void removePublishTarget(PublishTarget target) {
        targets.remove(target);
        targetlistmodel.firePublishTargetUpdate();
        saved = false;
        fireProjectConfigChangedEvent();
    }

    public void removePublishTarget(int index) {
        targets.remove(index);
        targetlistmodel.firePublishTargetUpdate();
        saved = false;
        fireProjectConfigChangedEvent();
    }

    /**
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *
     */
    public void setDescription(String description) {
        this.description = description;
        saved = false;
        fireProjectConfigChangedEvent();
    }

    public void addTask(Task task) {
        task.setBeanConfiguration(cocoonBeanConfiguration);
        tasks.add(task);
        taskModel.fireTableUpdate();
        saved = false;
        fireProjectConfigChangedEvent();
    }

    public ArrayList getTasks() {
        return tasks;
    }

    public void removeTask(int index) {
        if ((index >= 0) && (index < tasks.size())) {
            tasks.remove(index);
            taskModel.fireTableUpdate();
            saved = false;
            fireProjectConfigChangedEvent();
        }
    }

    public void removeTask(String id) {
        Iterator i = tasks.iterator();

        while (i.hasNext()) {
            Task t = (Task) i.next();

            if (t.getID().equals(id)) {
                tasks.remove(t);
                taskModel.fireTableUpdate();
                saved = false;
                fireProjectConfigChangedEvent();

                return;
            }
        }
    }

    public void replaceTask(
        Task oldTask,
        Task newTask) {
        tasks.set(tasks.indexOf(oldTask), newTask);
        taskModel.fireTableUpdate();
        saved = false;
        fireProjectConfigChangedEvent();
    }

    /**
     * @return Returns the title.
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *
     * The title to set.
     *
     */
    public void setTitle(String title) {
        this.title = title;
        saved = false;
        fireProjectConfigChangedEvent();
    }

    public void processTask(int index) {
        Task task = (Task) tasks.get(index);
        processTask(task);
    }

    public void processTask(Task task) {
        fireTaskStartEvent(task);

        if (task.getProcessURI().size() > 0) {
            
        	if(task.isCleanBuild()){
        		Utilities.deleteFile(task.getBuildDir());
        	}
        	
        	
        	//let now cocoon do the jobs
            log.debug("initialize the CocoonBean");
            
            
            
            //create or recycle the controller
            initCocoonBeanController();

            if (cocoonController != null) {
                log.debug("cocoon process Task: " + task.getTitle());
                processing = true;
                cocoonController.processTask(task);
            } else {
                log.error("no Cocoon instance");
            }
        }

        //publish now
        processing = false;

        if (task.getPublishDestinations().size() > 0) {
            publishing = true;
            publisher = new PublisherImpl(log.getChildLogger("publisher"));
            publisher.addPublishListener(this);

            Iterator dests = task.getPublishDestinations().iterator();
            log.debug("publishing dest=" + task.getPublishDestinations().size());

            while (dests.hasNext() && !canceled) {
                PublishDestination dest = (PublishDestination) dests.next();
                log.debug("dest=" + dest.getTitle());
                log.debug("dest-destination=" + dest.getDestination());

                PublishTarget target = getPublishTarget(dest.getTargetID());
                target.setTask(task);
                target.publish(publisher, dest);
            }

            publishing = false;
            publisher = null;
        }

        fireTaskEndEvent();
    }

    public void processTasks() {
        thread = false;

        fireStartProcessing();
        canceled = false;

        Iterator i = tasks.iterator();

        while (i.hasNext() && !canceled) {
            Task task = (Task) i.next();

            if (task.isEnabled()) {
                processTask(task);
            }
        }

        fireEndProcessing();
    }

    public void addProjectListener(ProjectListener l) {
        listeners.add(l);
    }

    public void removeProjectListener(ProjectListener l) {
        listeners.remove(l);
    }

    private void fireTaskStartEvent(Task task) {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            ProjectListener l = (ProjectListener) i.next();
            l.taskStart(task);
        }
    }

    private void fireTaskEndEvent() {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            ProjectListener l = (ProjectListener) i.next();
            l.taskEnd();
        }
    }

    private void fireTaskProcessEvent(ProjectProcessEvent ppe) {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            ProjectListener l = (ProjectListener) i.next();
            l.processed(ppe);
        }
    }

    private void fireFilePublishProcess(
        String file,
        long done,
        long size) {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            ProjectListener l = (ProjectListener) i.next();
            l.filePublishProcess(file, done, size);
        }
    }

    private void firePublishedFile(
        String file,
        long size,
        long time) {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            ProjectListener l = (ProjectListener) i.next();
            l.publishedFile(file, size, time);
        }
    }

    private void fireStartPublishing(int count) {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            ProjectListener l = (ProjectListener) i.next();
            l.startPublishing(count);
        }
    }

    private void fireStartProcessing() {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            ProjectListener l = (ProjectListener) i.next();
            l.startProcessing();
        }
    }

    private void fireEndProcessing() {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            ProjectListener l = (ProjectListener) i.next();
            l.endProcessing();
        }
    }

    /**
     *
     *
     */
    public ArrayList getPublishTargets() {
        return targets;
    }

    /**
     * @param ftpsite
     *
     * The ftpsite to set.
     *
     */
    public void setPublishTargets(ArrayList targets) {
        this.targets = targets;
        saved = false;
        fireProjectConfigChangedEvent();
    }

    public ComboBoxModel getTargetsListModel() {
        return targetlistmodel;
    }

    /**
     * @return Returns the filename.
     *
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename
     *
     * The filename to set.
     *
     */
    public void setFilename(String filename) {
        this.filename = filename;

        File f = new File(filename);

        if (f.exists() && f.isFile()) {
            cocoonBeanConfiguration.setConfigLocation(f.getParent());
        }

        fireProjectConfigChangedEvent();
    }

    /**
     * @return Returns the activeTask.
     *
     */
    public Task getActiveTask() {
        return activeTask;
    }

    /**
     * @param activeTask
     *
     * The activeTask to set.
     *
     */
    public void setActiveTask(Task activeTask) {
        this.activeTask = activeTask;
    }

    public synchronized void recycle() {
        title = "";
        contextDir = "";
        description = "";
        filename = "";
        targets.clear();
        tasks.clear();

        processtask = -1;
        destroyCocoonBean();

        configLocation = "";
        cocoonBeanConfiguration = new BeanConfiguration();
        fireProjectConfigChangedEvent();
    }

    public void cancelTaskProcessing() {
        if (processing) {
            log.debug("Stopping Cocoon");
            cocoonController.interrupt();
            canceled = true;
        } else if (publishing) {
            log.debug("Stopping Publishing");
            publisher.disconnect();
            canceled = true;
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.conf.ProcessListener#complete()
     *
     */
    public void complete() {
        if (canceled) {
            fireTaskEndEvent();
            canceled = false;
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.conf.ProcessListener#error(java.lang.String,
     *
     * java.lang.String)
     *
     */
    public void error(
        String uri,
        String message) {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            ProjectListener l = (ProjectListener) i.next();
            l.error(uri, message);
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.conf.ProcessListener#processed(de.miethxml.conf.ProjectProcessEvent)
     *
     */
    public void processed(ProjectProcessEvent ppe) {
        fireTaskProcessEvent(ppe);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.conf.ProcessListener#skipped(java.lang.String,
     *
     * java.lang.String)
     *
     */
    public void skipped(
        String uri,
        String message) {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.conf.ProcessListener#warn(java.lang.String,
     *
     * java.lang.String)
     *
     */
    public void warn(
        String uri,
        String message) {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.PublishListener#endPublishing()
     *
     */
    public void endPublishing() {
        //fireTaskEndEvent();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.PublishListener#errorMessage(java.lang.String)
     *
     */
    public void errorMessage(String msg) {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.PublishListener#filePublishProcess(java.lang.String,
     *
     * long, long)
     *
     */
    public void filePublishProcess(
        String name,
        long current,
        long max) {
        fireFilePublishProcess(name, current, max);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.PublishListener#publishedFile(java.lang.String,
     *
     * long, long)
     *
     */
    public void publishedFile(
        String file,
        long size,
        long time) {
        firePublishedFile(file, size, time);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.PublishListener#startPublishing(int)
     *
     */
    public void startPublishing(int fileCount) {
        fireStartPublishing(fileCount);
    }

    public void addProjectConfigListener(ProjectConfigListener l) {
        this.configListeners.add(l);
    }

    public void removeProjectConfigListener(ProjectConfigListener l) {
        this.configListeners.remove(l);
    }

    private void fireProjectConfigChangedEvent() {
        //the models
        taskModel.fireListUpdate();
        taskModel.fireTableUpdate();
        targetlistmodel.firePublishTargetUpdate();

        //this avoid the java.util.ConcurrentModificationException
        ArrayList backup = (ArrayList) configListeners.clone();
        Iterator i = backup.iterator();

        while (i.hasNext()) {
            ProjectConfigListener l = (ProjectConfigListener) i.next();
            l.configChanged(this);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    /**
     * @return Returns the iD.
     *
     */
    public String getID() {
        return ID;
    }

    /**
     * @param id
     *
     * The iD to set.
     *
     */
    public void setID(String id) {
        ID = id;
    }

    public void setSaved(boolean state) {
        saved = state;
        fireProjectConfigChangedEvent();
    }

    public void load(String filename) {
        recycle();

        //this avoid the fireConfigEvent at every step
        //TODO change this later
        ArrayList clone = (ArrayList) configListeners.clone();
        configListeners.clear();

        //setFilename(filename);
        //reader.setProject(this);
        XMLProjectReader reader = new XMLProjectReader();
        reader.parseURI(filename, this);

        configListeners = clone;

        //loaded and not changed yet
        setSaved(true);
    }

    /**
     * @return Returns the cocoonBeanConfiguration.
     *
     */
    public BeanConfiguration getCocoonBeanConfiguration() {
        return cocoonBeanConfiguration;
    }

    /**
     * @param cocoonBeanConfiguration
     *
     * The cocoonBeanConfiguration to set.
     *
     */
    public void setCocoonBeanConfiguration(
        BeanConfiguration cocoonBeanConfiguration) {
        this.cocoonBeanConfiguration = cocoonBeanConfiguration;
        fireProjectConfigChangedEvent();
    }

    /**
     * @return Returns the parsedFileFormat.
     *
     */
    public int getParsedFileFormat() {
        return parsedFileFormat;
    }

    /**
     * @param parsedFileFormat
     *
     * The parsedFileFormat to set.
     *
     */
    public void setParsedFileFormat(int parsedFileFormat) {
        this.parsedFileFormat = parsedFileFormat;
    }

    private void initCocoonBeanController() {
        if (null == cocoonController) {
        	
        
       
        	
            String libPath = "";
            File f = new File(getContextPath());

            if (f.exists() && f.isDirectory()) {
                libPath = getContextPath();
            } else {
                //set the project-file directory as context Dir
                if (getFilename().length() > 0) {
                    f = new File(getFilename());

                    if (f.exists() && f.isFile()) {
                        libPath = f.getParentFile().getAbsolutePath();
                    }
                } else {
                    //handle error here
                    log.error("no Cocoon lib path");
                }
            }

            cocoonController = CocoonBeanFactory.createCocoonBeanController(libPath);
            cocoonController.initialize(cocoonBeanConfiguration);

            for (int i = 0; i < listeners.size(); i++) {
                cocoonController.addProcessListener((ProcessListener) listeners
                    .get(i));
            }
        } else {
            cocoonController.recycle();
        }
    }

    /**
     * @return Returns the configLocation.
     *
     */
    public String getConfigLocation() {
        if ((configLocation == null) || (configLocation.length() == 0)) {
            if (cocoonBeanConfiguration.getContextDir().length() > 0) {
                configLocation = cocoonBeanConfiguration.getContextDir()
                    + File.separator + Project.DEFAULT_CONFIG_DIRECTORY;

                //create the config directory
                File configDir = new File(configLocation);

                if (!configDir.exists()) {
                    configDir.mkdir();
                }
            }
        }

        return configLocation;
    }

    /**
     * @param configLocation
     *
     * The configLocation to set.
     *
     */
    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public TaskModel getTaskModel() {
        return taskModel;
    }

    public void destroyCocoonBean() {
        if (cocoonController != null) {
            cocoonController.interrupt();
            cocoonController.dispose();

            cocoonController = null;
            System.gc();
        }
    }

    public void setLogger(Logger log) {
        this.log = log;
    }

    public class TargetsListModel implements ComboBoxModel {
        ArrayList listener;
        Object selection;

        public TargetsListModel() {
            listener = new ArrayList();
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
         *
         */
        public void addListDataListener(ListDataListener l) {
            listener.add(l);
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ListModel#getElementAt(int)
         *
         */
        public Object getElementAt(int index) {
            PublishTarget target = getPublishTarget(index);

            return target.getTitle();
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ListModel#getSize()
         *
         */
        public int getSize() {
            return getPublishTargetCount();
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
         *
         */
        public void removeListDataListener(ListDataListener l) {
            listener.remove(l);
        }

        public void firePublishTargetUpdate() {
            Iterator i = listener.iterator();

            while (i.hasNext()) {
                ListDataListener l = (ListDataListener) i.next();
                l.contentsChanged(new ListDataEvent(this,
                        ListDataEvent.CONTENTS_CHANGED, 0, targets.size()));
            }
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ComboBoxModel#getSelectedItem()
         *
         */
        public Object getSelectedItem() {
            return selection;
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
         *
         */
        public void setSelectedItem(Object anItem) {
            selection = anItem;
        }
    }
}
