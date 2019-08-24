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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.miethxml.hawron.cocoon.BeanConfiguration;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public class Task {
    public static String CONFIGKEY_CLEANBUILD = "clean-build";
    public static String CONFIGKEY_DIFF_BUILD = "diff-build";
    public static String CONFIGKEY_DESTINATION = "dest";
    public static String CONFIGKEY_TITLE = "title";
    public static String CONFIGKEY_DESCRIPTION = "description";
    public static String CONFIGKEY_PUBLISH = "publish";
    public static String CONFIGKEY_DOCUMENTROOT = "document-root";
    private String ID;
    private String title;
    private String description;
    private String localdest;
    private String docRoot;
    private ArrayList process;
    private ArrayList publish;

    private ProcessModel processmodel;
    private PublishModel publishmodel;
    private boolean cleanBuild = false;
    private boolean followLinks = true;
    private boolean confirmExtensions = false;
    private boolean diffBuild = false;
    private boolean enabled = false;
    
    private BeanConfiguration conf;

    /**
     *
     *
     *
     */
    public Task() {
        super();
        ID = "";
        title = "";
        description = "";
        localdest = "";
        docRoot = "";
        process = new ArrayList();
        publish = new ArrayList();

        processmodel = new ProcessModel();
        publishmodel = new PublishModel();
    }

    /**
     * @return Returns the description.
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *
     * The description to set.
     *
     */
    public void setDescription(String description) {
        this.description = description;
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
    }

    public void addPublishDestination(PublishDestination pd) {
        publish.add(pd);
        publishmodel.fireListEvent();
    }

    public void removePublishDestination(PublishDestination d) {
        publish.remove(d);
        publishmodel.fireListEvent();
    }

    public void replacePublishDestination(
        PublishDestination oldpd,
        PublishDestination newpd) {
        publish.set(publish.indexOf(oldpd), newpd);
        publishmodel.fireListEvent();
    }

    public void addProcessURI(ProcessURI uri) {
        //uri.setTask(this);
        process.add(uri);
        processmodel.fireListEvent();
    }

    public ArrayList getProcessURI() {
        return process;
    }

    public void removeProcessURI(String uri) {
        Iterator i = process.iterator();

        while (i.hasNext()) {
            ProcessURI pu = (ProcessURI) i.next();

            if (pu.getUri().equals(uri)) {
                process.remove(uri);
                processmodel.fireListEvent();

                return;
            }
        }
    }

    public ProcessURI removeProcessURI(int index) {
        ProcessURI uri = (ProcessURI) process.remove(index);
        processmodel.fireListEvent();

        return uri;
    }

    public void replaceProcessURI(
        ProcessURI olduri,
        ProcessURI newuri) {
        process.set(process.indexOf(olduri), newuri);
        processmodel.fireListEvent();
    }

    public void setBuildDir(String d) {
        this.localdest = d;
    }

    public String getBuildDir() {
        return this.localdest;
    }

    public ArrayList getPublishDestinations() {
        return publish;
    }

    public ListModel getProcessListModel() {
        return processmodel;
    }

    public ListModel getPublishListModel() {
        return publishmodel;
    }

    /**
     * @return Returns the docRoot.
     *
     */
    public String getDocRoot() {
        return docRoot;
    }

    /**
     * @param docRoot
     *
     * The docRoot to set.
     *
     */
    public void setDocRoot(String docRoot) {
        this.docRoot = docRoot;
    }
  

    /**
     * @return Returns the cleanBuild.
     *
     */
    public boolean isCleanBuild() {
        return cleanBuild;
    }

    public boolean isFollowLinks() {
        return followLinks;
    }

    public boolean isConfirmExtensions() {
        return confirmExtensions;
    }

    /**
     * @param cleanBuild
     *
     * The cleanBuild to set.
     *
     */
    public void setCleanBuild(boolean cleanBuild) {
        this.cleanBuild = cleanBuild;
    }

    /**
     * @param project
     *
     * The project to set.
     *
     */
//    public void setProject(Project project) {
//        this.project = project;
//    }

    
    
    public Object clone() {
        Task clone = new Task();
        //the setters and getters
        clone.setTitle(getTitle());
        clone.setID(getID());
        clone.setBuildDir(getBuildDir());
        clone.setDescription(getDescription());
        clone.setDocRoot(getDocRoot());
        clone.setBeanConfiguration(conf);
        clone.setCleanBuild(isCleanBuild());
        clone.setDiffBuild(isDiffBuild());
        
        
        //the process-uris
        Iterator i = getProcessURI().iterator();

        while (i.hasNext()) {
            clone.addProcessURI((ProcessURI) i.next());
        }

        i = getPublishDestinations().iterator();

        //the publish destinations
        while (i.hasNext()) {
            PublishDestination pd = new PublishDestination();
            PublishDestination source = (PublishDestination) i.next();
            pd.setTitle(source.getTitle());
            pd.setDestination(source.getDestination());
            pd.setTargetID(source.getTargetID());
            pd.setSource(source.getSource());
            clone.addPublishDestination(pd);
        }

       

        return clone;
    }

    /**
     * @return Returns the diffBuild.
     *
     */
    public boolean isDiffBuild() {
        return diffBuild;
    }

    /**
     * @param diffBuild
     *
     * The diffBuild to set.
     *
     */
    public void setDiffBuild(boolean diffBuild) {
    	  this.diffBuild = diffBuild;
    }

    /**
     * @param confirmExtensions
     *
     * The confirmExtensions to set.
     *
     */
    public void setConfirmExtensions(boolean confirmExtensions) {
        this.confirmExtensions = confirmExtensions;
    }

    /**
     * @param followLinks
     *
     * The followLinks to set.
     *
     */
    public void setFollowLinks(boolean followLinks) {
        this.followLinks = followLinks;
    }

    /**
     * @return Returns the enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     *            The enabled to set.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    
    public void setBeanConfiguration(BeanConfiguration conf){
    	this.conf=conf;
    }
    
    public class ProcessModel implements ListModel {
        ArrayList listeners;

        public ProcessModel() {
            listeners = new ArrayList();
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
         *
         */
        public void addListDataListener(ListDataListener l) {
            listeners.add(l);
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ListModel#getElementAt(int)
         *
         */
        public Object getElementAt(int index) {
            ProcessURI uri = (ProcessURI) process.get(index);

            return uri.getUri();
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ListModel#getSize()
         *
         */
        public int getSize() {
            return process.size();
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
         *
         */
        public void removeListDataListener(ListDataListener l) {
            listeners.remove(l);
        }

        public void fireListEvent() {
            Iterator i = listeners.iterator();

            while (i.hasNext()) {
                ListDataListener l = (ListDataListener) i.next();
                l.contentsChanged(new ListDataEvent(this,
                        ListDataEvent.CONTENTS_CHANGED, 0, process.size()));
            }
        }
    }

    public class PublishModel implements ListModel {
        ArrayList listeners;

        public PublishModel() {
            listeners = new ArrayList();
        }

        public void addListDataListener(ListDataListener l) {
            listeners.add(l);
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ListModel#getElementAt(int)
         *
         */
        public Object getElementAt(int index) {
            PublishDestination pd = (PublishDestination) publish.get(index);

            return pd.getTitle();
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ListModel#getSize()
         *
         */
        public int getSize() {
            return publish.size();
        }

        /*
         *
         * (non-Javadoc)
         *
         * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
         *
         */
        public void removeListDataListener(ListDataListener l) {
            listeners.add(l);
        }

        public void fireListEvent() {
            Iterator i = listeners.iterator();

            while (i.hasNext()) {
                ListDataListener l = (ListDataListener) i.next();
                l.contentsChanged(new ListDataEvent(this,
                        ListDataEvent.CONTENTS_CHANGED, 0, publish.size()));
            }
        }
    }
}
