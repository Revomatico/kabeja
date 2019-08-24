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
package de.miethxml.hawron.cocoon.impl;

import java.io.File;
import java.io.OutputStream;
import java.io.PipedOutputStream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import de.miethxml.hawron.cocoon.BeanConfiguration;
import de.miethxml.hawron.cocoon.CocoonBeanController;
import de.miethxml.hawron.project.ProcessListener;
import de.miethxml.hawron.project.ProcessURI;
import de.miethxml.hawron.project.ProjectProcessEvent;
import de.miethxml.hawron.project.Task;

import org.apache.cocoon.bean.BeanListener;
import org.apache.cocoon.bean.CocoonBean;
import org.apache.cocoon.bean.Target;
import org.apache.cocoon.bean.helpers.OutputStreamListener;



/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public class CocoonBeanControllerImpl implements CocoonBeanController,
    BeanListener {
    private ReuseableCocoonBean cocoonBean;
    private BeanConfiguration config;
    private ArrayList listeners = new ArrayList();
    private ProjectProcessEvent processEvent;
    private BeanListener brokenLinkListener;

    //support methods of used CocoonBean
    private HashSet methods = new HashSet();
    private boolean old = false;
    private CocoonBean oldBean;

    public CocoonBeanControllerImpl() {
        checkCocoonBean();

        processEvent = new ProjectProcessEvent(this);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.CocoonBeanController#initialize(de.miethxml.cocoon.BeanConfiguration)
     *
     */
    public void initialize(BeanConfiguration config) {
        this.config = config;

        
        //TODO this can be removed, we have always the new Bean 
        //and we know all supported methods so we don't need the 
        //test here 
        if (old) {
            //initialize now the CocoonBean 2.1.2 and lower
            if (methods.contains("setVerbose")) {
                oldBean.setVerbose(config.isVerbose());
            }

            if (methods.contains("setFollowLinks")) {
                oldBean.setFollowLinks(config.isFollowlinks());
            }

            if (methods.contains("setConfirmExtensions")) {
                oldBean.setConfirmExtensions(config.isConfirmextenions());
            }

            if (methods.contains("setLogKit")) {
                if (config.getLogKit().length() > 0) {
                    oldBean.setLogKit(config.getLogKit());
                } else {
                    //fallback
                    oldBean.setLogKit(config.getContextDir() + File.separator
                        + "WEB-INF" + File.separator + "logkit.xconf");
                }
            }

            if (methods.contains("setLogLevel")) {
                oldBean.setLogLevel(config.getLogLevel());
            }

            if (methods.contains("setLogger")) {
                oldBean.setLogger(config.getLogger());
            }

            if (methods.contains("setContextDir")) {
                oldBean.setContextDir(config.getContextDir());
            }

            if (methods.contains("addLoadedClasses")
                    && (config.getLoadClasses().size() > 0)) {
                oldBean.addLoadedClasses(config.getLoadClasses());
            }

            if (methods.contains("setWorkDir")) {
                oldBean.setWorkDir(config.getWorkDir());
            }

            if (methods.contains("setConfigFile")
                    && (config.getConfigFile().length() > 0)) {
                oldBean.setConfigFile(config.getConfigFile());
            }

            if (methods.contains("setAgentOptions")
                    && (config.getUserAgent().length() > 0)) {
                oldBean.setAgentOptions(config.getUserAgent());
            }

            if (methods.contains("setAcceptOptions")
                    && (config.getAccept().length() > 0)) {
                oldBean.setAcceptOptions(config.getAccept());
            }

            if (methods.contains("addExcludePattern")) {
                List patterns = config.getExcludePatterns();
                Iterator i = patterns.iterator();

                while (i.hasNext()) {
                    oldBean.addExcludePattern((String) i.next());
                }
            }

            if (methods.contains("addIncludePattern")) {
                List patterns = config.getIncludePatterns();
                Iterator i = patterns.iterator();

                while (i.hasNext()) {
                    oldBean.addIncludePattern((String) i.next());
                }
            }

            if (methods.contains("setPrecompileOnly")) {
                oldBean.setPrecompileOnly(config.isPrecompileonly());
            }

            if (methods.contains("setDefaultFilename")
                    && (config.getDefaultFilename().length() > 0)) {
                oldBean.setDefaultFilename(config.getDefaultFilename());
            }

            //BrokenLinks here
            if (methods.contains("addListener") && config.isBrokenLinks()
                    && methods.contains("setBrokenLinkGenerate")) {
                //dummy
                OutputStream out = new PipedOutputStream();
                OutputStreamListener listener = new OutputStreamListener(out);
                brokenLinkListener = listener;
                oldBean.addListener(listener);
                oldBean.setBrokenLinkGenerate(config.isBrokenLinkReporting());

                if (methods.contains("setBrokenLinkExtension")) {
                    oldBean.setBrokenLinkExtension(config
                        .getBrokenLinkReportExtension());
                }

                if (config.getBrokenLinkReportFile().length() > 0) {
                    listener.setReportFile(config.getBrokenLinkReportFile());
                }

                if (config.getBrokenLinkReportType().length() > 0) {
                    listener.setReportType(config.getBrokenLinkReportType());
                }
            } else {
                brokenLinkListener = null;
            }
        } else {
            //initialize our CocoonBean
            if (methods.contains("setVerbose")) {
                cocoonBean.setVerbose(config.isVerbose());
            }

            if (methods.contains("setFollowLinks")) {
                cocoonBean.setFollowLinks(config.isFollowlinks());
            }

            if (methods.contains("setConfirmExtensions")) {
                cocoonBean.setConfirmExtensions(config.isConfirmextenions());
            }

            if (methods.contains("setLogKit")) {
                if (config.getLogKit().length() > 0) {
                    cocoonBean.setLogKit(config.getLogKit());
                } else {
                    //fallback
                    cocoonBean.setLogKit(config.getContextDir() + File.separator
                        + "WEB-INF" + File.separator + "logkit.xconf");
                }
            }

            if (methods.contains("setLogLevel")) {
                cocoonBean.setLogLevel(config.getLogLevel());
            }

            if (methods.contains("setLogger")) {
                cocoonBean.setLogger(config.getLogger());
            }

            if (methods.contains("setContextDir")) {
                cocoonBean.setContextDir(config.getContextDir());
            }

            if (methods.contains("setChecksumUri")
                    && (config.getChecksumURI().length() > 0)) {
                cocoonBean.setChecksumURI(config.getChecksumURI());
            }

            if (methods.contains("addLoadedClasses")
                    && (config.getLoadClasses().size() > 0)) {
                cocoonBean.addLoadedClasses(config.getLoadClasses());
            }

            if (methods.contains("setWorkDir")) {
                cocoonBean.setWorkDir(config.getWorkDir());
            }

            if (methods.contains("setConfigFile")
                    && (config.getConfigFile().length() > 0)) {
                cocoonBean.setConfigFile(config.getConfigFile());
            }

            if (methods.contains("setAgentOptions")
                    && (config.getUserAgent().length() > 0)) {
                cocoonBean.setAgentOptions(config.getUserAgent());
            }

            if (methods.contains("setAcceptOptions")
                    && (config.getAccept().length() > 0)) {
                cocoonBean.setAcceptOptions(config.getAccept());
            }

            if (methods.contains("addExcludePattern")) {
                List patterns = config.getExcludePatterns();
                Iterator i = patterns.iterator();

                while (i.hasNext()) {
                    cocoonBean.addExcludePattern((String) i.next());
                }
            }

            if (methods.contains("addIncludePattern")) {
                List patterns = config.getIncludePatterns();
                Iterator i = patterns.iterator();

                while (i.hasNext()) {
                    cocoonBean.addIncludePattern((String) i.next());
                }
            }

            if (methods.contains("setPrecompileOnly")) {
                cocoonBean.setPrecompileOnly(config.isPrecompileonly());
            }

            if (methods.contains("setDefaultFilename")
                    && (config.getDefaultFilename().length() > 0)) {
                cocoonBean.setDefaultFilename(config.getDefaultFilename());
            }

            //BrokenLinks here
            if (methods.contains("addListener") && config.isBrokenLinks()
                    && methods.contains("setBrokenLinkGenerate")) {
                //dummy
                OutputStream out = new PipedOutputStream();
                OutputStreamListener listener = new OutputStreamListener(out);
                brokenLinkListener = listener;
                cocoonBean.addListener(listener);
                cocoonBean.setBrokenLinkGenerate(config.isBrokenLinkReporting());

                if (methods.contains("setBrokenLinkExtension")) {
                    cocoonBean.setBrokenLinkExtension(config
                        .getBrokenLinkReportExtension());
                }

                if (config.getBrokenLinkReportFile().length() > 0) {
                    listener.setReportFile(config.getBrokenLinkReportFile());
                }

                if (config.getBrokenLinkReportType().length() > 0) {
                    listener.setReportType(config.getBrokenLinkReportType());
                }
            } else {
                brokenLinkListener = null;
            }
            
        
            
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.CocoonBeanController#processTask(Task task)
     *
     */
    public synchronized void processTask(Task task) {
        Thread.currentThread().setContextClassLoader(this.getClass()
                                                         .getClassLoader());

        if (old) {
            //cocoon <= 2.1.2
            Iterator i = task.getProcessURI().iterator();

            while (i.hasNext()) {
                ProcessURI uri = (ProcessURI) i.next();

                Target target = null;

                if (uri.getSrcPrefix().length() > 0) {
                    oldBean.addTarget(uri.getType(), uri.getSrcPrefix(),
                        uri.getUri(), task.getBuildDir(), task.isFollowLinks(),
                        task.isConfirmExtensions(), config.getLogger());
                } else {
                    oldBean.addTarget(uri.getType(), uri.getUri(),
                        task.getBuildDir());
                }
            }

            try {
                //try to cleanup
                System.gc();
                oldBean.process();
            } catch (Exception e) {
                e.printStackTrace();
                fireErrorEvent("", "FATAL: " + e.getMessage());
                fireCompleteEvent();
            }
        } else {
            //our Bean
            if (methods.contains("setChecksumURI")) {
                if (task.isDiffBuild()) {
                	   cocoonBean.setChecksumURI(config.getChecksumURI());
                   
                } else {
                	cocoonBean.setChecksumURI(null);
                } 
            }

            try {
                //try to cleanup
                System.gc();

                if (methods.contains("processCrawler")) {
                    cocoonBean.process(task, config.getLogger());
                }

                //this is a workaround and must changed in CocoonBean
                if (brokenLinkListener != null) {
                    brokenLinkListener.complete();
                    brokenLinkListener = null;
                }
            } catch (Exception e) {
                //			log.error("build:" + e.getMessage());
                e.printStackTrace();
                fireErrorEvent("", "FATAL: " + e.getMessage());
                fireCompleteEvent();

                //return;
            } catch (Error le) {
                //			log.error("build:" + le.getMessage());
                fireErrorEvent("", "FATAL: " + le.getMessage());
                fireCompleteEvent();
                le.printStackTrace();

                //return;
            }
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.CocoonBeanController#recycle()
     *
     */
    public void recycle() {
    	   Thread.currentThread().setContextClassLoader(this.getClass()
                .getClassLoader());
        if (old) {
        	
            oldBean.dispose();
            oldBean = new CocoonBean();

            if (methods.contains("addListener")) {
                cocoonBean.addListener(this);
            }

            initialize(this.config);
        } else {
      
            System.gc();
        }

        //remove ??
     
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.CocoonBeanController#interrupt()
     *
     */
    public void interrupt() {
        if (this.methods.contains("interruptProcessing") && !old) {
            //only works with patched CocoonBean
            cocoonBean.interruptProcessing();
        } else {
            //you can only dispose cocoon
            oldBean.dispose();
            oldBean = null;
        }

        System.gc();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.apache.cocoon.bean.BeanListener#brokenLinkFound(java.lang.String,
     *
     * java.lang.String, java.lang.String, java.lang.Throwable)
     *
     */
    public void brokenLinkFound(
        String uri,
        String parentURI,
        String message,
        Throwable t) {
        //log.error("brokenLink " + arg0 + " " + arg1 + " " + arg2);
        fireErrorEvent(uri, parentURI + " " + message);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.apache.cocoon.bean.BeanListener#complete()
     *
     */
    public void complete() {
        fireCompleteEvent();

        if (brokenLinkListener != null) {
            brokenLinkListener.complete();
        }

        //log.debug("CocoonBean complete");
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.apache.cocoon.bean.BeanListener#messageGenerated(java.lang.String)
     *
     */
    public void messageGenerated(String msg) {
        //log.debug("Message: " + msg);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.apache.cocoon.bean.BeanListener#pageGenerated(java.lang.String,
     *
     * java.lang.String, int, int, int, int, int, long)
     *
     */
    public void pageGenerated(
        String sourceURI,
        String destinationURI,
        int pageSize,
        int linksInPage,
        int newLinksinPage,
        int pagesRemaining,
        int pagesComplete,
        long timeTaken) {
        processEvent.setUri(sourceURI);
        processEvent.setLinks(pagesRemaining + pagesComplete);
        processEvent.setPage(pagesComplete);
        fireTaskProcessEvent(processEvent);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.apache.cocoon.bean.BeanListener#pageSkipped(java.lang.String,
     *
     * java.lang.String)
     *
     */
    public void pageSkipped(
        String uri,
        String message) {
        //log.debug("page skipped " + uri + " " + message);
        fireSkippedEvent(uri, message);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.apache.cocoon.bean.BeanListener#warningGenerated(java.lang.String,
     *
     * java.lang.String)
     *
     */
    public void warningGenerated(
        String uri,
        String warning) {
        //log.warn("CocoonBean warn " + uri + " " + warning);
        fireWarnEvent(uri, warning);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.CocoonBeanController#addProcessListener(de.miethxml.project.ProcessListener)
     *
     */
    public void addProcessListener(ProcessListener l) {
        listeners.add(l);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.CocoonBeanController#removeProcessListener(de.miethxml.project.ProcessListener)
     *
     */
    public void removeProcessListener(ProcessListener l) {
        listeners.remove(l);
    }

    private void checkCocoonBean() {
        try {
            Class clazz = this.getClass().getClassLoader().loadClass("org.apache.cocoon.bean.helpers.Crawler");
        } catch (ClassNotFoundException e) {
            System.out.println("old Cocoon");
            old = true;
        }

        java.lang.reflect.Method[] m = null;

        if (old) {
            oldBean = new CocoonBean();
            m = oldBean.getClass().getMethods();
        } else {
            cocoonBean = new ReuseableCocoonBean();
            cocoonBean.addListener(this);
            m = cocoonBean.getClass().getMethods();
        }

        //remove this we have now old and own bean
        for (int i = 0; i < m.length; i++) {
            if (m[i].getName().equals("process")
                    && (m[i].getParameterTypes().length > 0)) {
                methods.add("processCrawler");
            } else {
                methods.add(m[i].getName());
            }
        }
    }

    private void fireCompleteEvent() {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            ProcessListener l = (ProcessListener) i.next();
            l.complete();
        }
    }

    private void fireTaskProcessEvent(ProjectProcessEvent ppe) {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            ProcessListener l = (ProcessListener) i.next();
            l.processed(ppe);
        }
    }

    private void fireErrorEvent(
        String uri,
        String msg) {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            ProcessListener l = (ProcessListener) i.next();
            l.error(uri, msg);
        }
    }

    private void fireWarnEvent(
        String uri,
        String msg) {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            ProcessListener l = (ProcessListener) i.next();
            l.warn(uri, msg);
        }
    }

    private void fireSkippedEvent(
        String uri,
        String msg) {
        Iterator i = listeners.iterator();

        while (i.hasNext()) {
            ProcessListener l = (ProcessListener) i.next();
            l.skipped(uri, msg);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.CocoonBeanController#getCocoonBean()
     */
    public Object getCocoonBean() {
        return cocoonBean;
    }

    public void processUri(
        String uri,
        OutputStream out) {
        if (cocoonBean == null) {
            recycle();
        }

        try {
            cocoonBean.processURI(uri, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
    	//this is need for complete dispose (for jcl-jakarta-commons-logging )
    	ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
    	
    	
    	   Thread.currentThread().setContextClassLoader(this.getClass()
                .getClassLoader());
    
        if (old && (oldBean != null)) {
            oldBean.dispose();
        } else if (cocoonBean != null) {
            listeners.clear();
            cocoonBean.dispose();
            cocoonBean = null;
        }
        
        Thread.currentThread().setContextClassLoader(oldCL);
    }
    
    
  
}
