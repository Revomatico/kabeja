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
package de.miethxml.hawron;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.log.Hierarchy;
import org.apache.log.format.PatternFormatter;
import org.apache.log.output.io.FileTarget;
import org.xml.sax.SAXException;

import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.ProjectComponent;
import de.miethxml.hawron.project.ProjectConfigListener;
import de.miethxml.toolkit.application.ApplicationShutdown;
import de.miethxml.toolkit.application.CLIParser;
import de.miethxml.toolkit.component.PluginComponent;
import de.miethxml.toolkit.conf.ConfigManager;
import de.miethxml.toolkit.container.DefaultApplicationContainer;
import de.miethxml.toolkit.gui.SplashScreen;
import de.miethxml.toolkit.plugins.PluginManager;
import de.miethxml.toolkit.plugins.PluginManagerImpl;
import de.miethxml.toolkit.plugins.PluginReceiver;
import de.miethxml.toolkit.setup.ApplicationSetup;

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
public class ApplicationContainer extends DefaultApplicationContainer implements
        ProjectConfigListener {
    //some state flags
    private boolean projectSet = false;

    private Project project;

    /**
     * 
     * 
     *  
     */
    public ApplicationContainer() {
        super();
    }

    public void initializeApplication() {

        //check for local settings
        File local = new File(ApplicationConstants.USER_CONFIG_HOME);
        if (!local.exists()
                && System.getProperty("os.name").toLowerCase()
                        .indexOf("indows") == -1) {
            //TODO change this to a ProjectResolver, which create such
            // directories
            File conf = new File(local.getAbsolutePath() + File.separator
                    + "conf");
            conf.mkdirs();

        }

        //load local settings
        if (local.exists()) {
            
            ConfigManager.getInstance().setConfigFile(
                    local.getAbsolutePath() + File.separator
                            + ConfigManager.DEFAULT_CONFIGNAME);
        }
        
      

       
        try {
            h = new Hierarchy();
            h
                    .setDefaultLogTarget(new FileTarget(
                            new File("debug.log"),
                            false,
                            new PatternFormatter(
                                    "%{time:HH:mm:ss.SSS dd.MM.y} [%{priority}]  %{category}: %{context} %{message}\n")));
            log = h.getLoggerFor(this.getClass().getName());
        } catch (IOException ioe) {
        }

       
        //get Configuration
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

        try {
            File configFile = null;

            if (local.exists()) {
                //try user-config
                configFile = new File(local.getAbsolutePath() + File.separator
                        + ApplicationConstants.XCONFIG_FILE);

            }
            if (configFile == null || !configFile.exists()) {
                configFile = new File(ApplicationConstants.XCONFIG_FILE);
            }

            config = builder.buildFromFile(configFile.getAbsolutePath());
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        splash.nextSubStep();

        loadComponent();

        ApplicationBuilder appBuilder = new ApplicationBuilder();
        appBuilder.setApplicationContainer(this);
        manager.put(ApplicationBuilder.ROLE, appBuilder);
        components.add(appBuilder);

        //the PluginManager
        PluginManagerImpl pm = new PluginManagerImpl();
        pm.setLogger(h.getLoggerFor(PluginManager.ROLE));
        pluginManager = pm;

        //we have to listen to all Plugins, we handle the
        //Avalon-Lifecycle for this Plugins
        pluginInterfaces = new HashSet();
        pluginInterfaces.add(PluginReceiver.RECEIVE_ALL_PLUGINS);
        manager.put(PluginManager.ROLE, pluginManager);
        pluginManager.addPluginReceiver(this);

        //the ApplicationShutdown
        manager.put(ApplicationShutdown.ROLE, this);

        //search for Plugins

        //check for user-plugins
        if (local.exists()) {
            pluginManager.findPlugins(local.getAbsolutePath() + File.separator
                    + PluginManager.DEFAULT_PLUGIN_DIRECTORY);
        }
        //check for global plugins
        pluginManager.findPlugins(PluginManager.DEFAULT_PLUGIN_DIRECTORY);

        splash.next("Initialize ...");
        splash.startSubSteps(components.size() + 6);

        //add the ApplicationSetup
        manager.put(ApplicationSetup.ROLE, appSetup);

        //create the project
        project = new Project();
        project.setLogger(h.getLoggerFor(project.getClass().getName()));
        project.addProjectConfigListener(this);

        //the Avalon LifeCycle
        enableLoggingComponents();
        splash.nextSubStep();

        //hawron's LifeCycle
        setProject();
        splash.nextSubStep();

        //the Avalon LifeCycle
        contextualizeComponents();
        splash.nextSubStep();

        serviceComponents();
        splash.nextSubStep();

        configureComponents();
        splash.nextSubStep();

        parameterizeComponents();
        splash.nextSubStep();

        initializeComponents();
        guiConfigure();
        splash.endSubSteps();
    }

    protected void processPluginObject(Object plugin) {
        if (projectSet) {
            if (plugin instanceof ProjectComponent) {
                ((ProjectComponent) plugin).setProject(project);
            }
        }

        super.processPluginObject(plugin);
    }

    /**
     * @param launchParameters
     *            The launchParameters to set.
     */
    public void setLaunchParameters(Hashtable launchParameters) {
        this.launchParameters = launchParameters;
    }

    private void setProject() {
        synchronized (components) {
            Iterator i = components.iterator();

            while (i.hasNext()) {
                Object object = i.next();

                try {
                    if (log.isDebugEnabled()) {
                        log.debug("try setProject: "
                                + object.getClass().getName());
                    }

                    if (object instanceof ProjectComponent) {
                        ProjectComponent comp = (ProjectComponent) object;
                        comp.setProject(project);
                    }
                } catch (Exception ex) {
                    log.error("setProject: " + object.getClass().getName()
                            + " " + ex.getMessage());
                }
            }
        }

        projectSet = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.miethxml.toolkit.plugins.PluginReceiver#addPlugin(java.lang.Object)
     */
    public void addPlugin(Object plugin) {
        if (plugin instanceof PluginComponent) {
            //we add this now to the ServiceManager
            PluginComponent component = (PluginComponent) plugin;
            manager.put(component.getRole(), plugin);

            //at the moment only one component for a role
            //is allowed
            pluginRoles.add(component.getRole());
        }

        processPluginObject(plugin);
        components.add(plugin);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.miethxml.toolkit.plugins.PluginReceiver#getInterfaces()
     */
    public Collection getInterfaces() {
        return pluginInterfaces;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.miethxml.toolkit.plugins.PluginReceiver#removePlugin(java.lang.Object)
     */
    public void removePlugin(Object obj) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.miethxml.toolkit.application.ApplicationShutdown#shutdownApplication()
     */
    public void shutdownApplication() {

    	
    	
        stopComponents();

        storeConfiguration();

        disposeComponents();

        project.destroyCocoonBean();
        
        if (log.isDebugEnabled()) {
            log.debug("Shutdown Application");
        }

        System.exit(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.miethxml.toolkit.application.Launcher#init(java.util.Hashtable)
     */
    public void init(Hashtable param) {
    
        splash = new SplashScreen("icons/splash.png", "Start ;-)");
        splash.setMaximum(3);
        splash.setVisible(true);

        splash.next("Loading ...");

        //logging stdout/stderr to a file
        if (Boolean.valueOf(ConfigManager.getInstance().getProperty("jre.logging","false")).booleanValue()) {
            try {
                PrintStream ps = new PrintStream(new BufferedOutputStream(
                        new FileOutputStream("jre.log", false), 128));
                System.setErr(ps);
                System.setOut(ps);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        System.setProperty("saxparser", ConfigManager.getInstance()
                .getProperty("SAXParser"));

        //make cli args available for components
        setLaunchParameters(param);
      
        initializeApplication();

        splash.next("Launching ...");

        //lauchApplication();
        startComponents();

        splash.dispose();
        splash = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.miethxml.toolkit.application.Launcher#setProperty(java.lang.String,
     *      java.lang.String)
     */
    public void setProperty(String key, String value) {
    }

    public static void main(String[] args) {
        ApplicationContainer container = new ApplicationContainer();
        container.init(CLIParser.parseParameters(args));
    }

    public void configChanged(Project project) {
        this.project = project;
        setProject();
    }
}