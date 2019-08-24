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
package de.miethxml.toolkit.container;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import de.miethxml.toolkit.application.ApplicationShutdown;
import de.miethxml.toolkit.application.CLIParser;
import de.miethxml.toolkit.application.Launcher;
import de.miethxml.toolkit.component.GuiConfigurable;
import de.miethxml.toolkit.component.PluginComponent;
import de.miethxml.toolkit.component.StoreConfigurable;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.StoreableConfigurationImpl;
import de.miethxml.toolkit.gui.SplashScreen;
import de.miethxml.toolkit.plugins.PluginManager;
import de.miethxml.toolkit.plugins.PluginManagerImpl;
import de.miethxml.toolkit.plugins.PluginReceiver;
import de.miethxml.toolkit.setup.ApplicationSetup;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.framework.service.ServiceException;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;
import org.apache.log.format.PatternFormatter;
import org.apache.log.output.io.FileTarget;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;


/**
 * @author simon
 *
 */
public class DefaultApplicationContainer implements PluginReceiver,
    ApplicationShutdown, Launcher {
    public static String XCONF_FILE = "conf/application.xconf";
    protected Logger log;
    protected DefaultServiceManager manager = new DefaultServiceManager();
    protected PluginManager pluginManager;
    protected ApplicationSetup appSetup = new ApplicationSetup();
    protected Configuration config;
    protected LogKitLogger appLogger;
    protected Hashtable launchParameters;
    protected ArrayList components = new ArrayList();
    protected HashSet pluginRoles = new HashSet();
    protected Hashtable componentobjects = new Hashtable();
    protected Hashtable roles = new Hashtable();
    protected DefaultContext context;
    protected StoreableConfigurationImpl appConfig;
    protected Parameters params;
    protected Collection pluginInterfaces;

    //some state flags
    protected boolean logEnabled = false;
    protected boolean contextEnabled = false;
    protected boolean serviceEnabled = false;
    protected boolean configureEnabled = false;
    protected boolean parameterEnabled = false;
    protected boolean initializeEnabed = false;
    protected boolean startEnabled = false;
    protected boolean guiconfigured = false;
    protected SplashScreen splash;
    protected Hierarchy h;
    private boolean stoptEnabled;

    /**
     *
     */
    public DefaultApplicationContainer() {
        super();
    }

    public void initializeApplication() {
        try {
         
            h = new Hierarchy();
            h.setDefaultLogTarget(new FileTarget(new File("debug.log"), false,
                    new PatternFormatter("%{time:HH:mm:ss.SSS dd.MM.y} [%{priority}]  %{category}: %{context} %{message}\n")));
            log = h.getLoggerFor(this.getClass().getName());
        } catch (IOException ioe) {
        }

        LocaleImpl.getInstance();

        //get Configuration
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

        try {
            config = builder.buildFromFile(XCONF_FILE);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        splash.nextSubStep();

        loadComponent();

        //ApplicationBuilder appBuilder = new ApplicationBuilder();
        //appBuilder.setApplicationContainer(this);
        //manager.put(ApplicationBuilder.ROLE, appBuilder);
        //components.add(appBuilder);
        //appConfig = new StoreableConfigurationImpl();
        //config = appConfig.getConfiguration();
        //manager.put(StoreableConfiguration.ROLE, appConfig);
        //put all Components to the manager and initialize all
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
        //handle here more the one directory
        //TODO support $HOME/.hawron/plugins for UNIX-based systems
        pluginManager.findPlugins(PluginManager.DEFAULT_PLUGIN_DIRECTORY);

        splash.next("Initialize ...");
        splash.startSubSteps(components.size() + 6);

        //add the ApplicationSetup
        manager.put(ApplicationSetup.ROLE, appSetup);

        //the Avalon LifeCycle
        enableLoggingComponents();
        splash.nextSubStep();

        //the application dependend lifecycle
        handleApplicationLifecycle();

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

        //toolkit lifecycle
        guiConfigure();
        splash.endSubSteps();
    }

    public void lauchApplication() {
        startComponents();
    }

    protected void loadComponent() {
        try {
            Configuration[] configs = config.getChild("role-list").getChildren();

            for (int i = 0; i < configs.length; i++) {
                if (configs[i].getName().equals("role")) {
                    roles.put(configs[i].getAttribute("default-class"),
                        configs[i].getAttribute("name"));
                }
            }

            configs = config.getChild("components").getChildren();

            for (int i = 0; i < configs.length; i++) {
                if (configs[i].getName().equals("component")) {
                    String instanceClass = configs[i].getAttribute("class");

                    Class clazz = this.getClass().getClassLoader().loadClass(instanceClass);
                    Object object = clazz.newInstance();

                    if (log.isDebugEnabled()) {
                        log.debug("load component:" + instanceClass);
                    }

                    //TODO remove this list with components and
                    //use only the map classname -> object
                    components.add(object);
                    componentobjects.put(instanceClass, object);

                    //check for ROLE
                    if (roles.containsKey(instanceClass)) {
                        String role = (String) roles.get(instanceClass);

                        //put to service manager
                        manager.put(role, object);

                        if (log.isDebugEnabled()) {
                            log.debug("add to service manager:" + instanceClass
                                + " for role:" + role);
                        }

                        roles.remove(instanceClass);
                    }
                }
            }

            //check if we have to instance some role-components
            if (roles.size() > 0) {
                Enumeration e = roles.keys();

                while (e.hasMoreElements()) {
                    String classname = (String) e.nextElement();
                    Class clazz = this.getClass().getClassLoader().loadClass(classname);
                    Object object = clazz.newInstance();
                    String role = (String) roles.get(classname);

                    //put to service manager
                    manager.put(role, object);

                    if (log.isDebugEnabled()) {
                        log.debug("add to servicemanager:" + classname
                            + " with role:" + role);
                    }

                    components.add(object);
                }
            }
        } catch (Exception e1) {
            log.error(e1.getMessage());
        }
    }

    protected void initializeComponents() {
        //splash.startSubSteps(components.size());
        synchronized (components) {
            Iterator i = components.iterator();

            while (i.hasNext()) {
                Object object = i.next();

                try {
                    if (log.isDebugEnabled()) {
                        log.debug("try to initialize: "
                            + object.getClass().getName());
                    }

                    splash.nextSubStep();
                    ContainerUtil.initialize(object);
                } catch (Exception e) {
                    log.error("initialize: " + object.getClass().getName()
                        + " " + e.getMessage(), e);
                }
            }
        }

        initializeEnabed = true;

        //splash.endSubSteps();
    }

    public void disposeComponents() {
        synchronized (components) {
            Iterator i = components.iterator();

            while (i.hasNext()) {
                Object object = i.next();

                try {
                    if (log.isDebugEnabled()) {
                        log.debug("try to dispose: "
                            + object.getClass().getName());
                    }

                    ContainerUtil.dispose(object);
                } catch (Exception e) {
                    log.error("dispose: " + object.getClass().getName() + " "
                        + e.getMessage());
                }
            }
        }

        Hierarchy.getDefaultHierarchy().getRootLogger().unsetLogTargets();

        //appConfig.store();
    }

    protected void serviceComponents() {
        synchronized (components) {
            Iterator i = components.iterator();

            while (i.hasNext()) {
                Object object = i.next();

                try {
                    log.debug("try to service " + object.getClass().getName());
                    ContainerUtil.service(object, manager);
                } catch (Exception e) {
                    log.error("service: " + object.getClass().getName() + " "
                        + e.getMessage());
                }
            }
        }

        serviceEnabled = true;
    }

    public void init() {
    }

    protected void startComponents() {
        synchronized (components) {
            Iterator i = components.iterator();

            while (i.hasNext()) {
                Object object = i.next();

                try {
                    log.debug("try to start " + object.getClass().getName());
                    ContainerUtil.start(object);
                } catch (Exception e) {
                    log.error("start: " + object.getClass().getName() + " "
                        + e.getMessage());
                }
            }
        }

        startEnabled = true;
    }

    protected void stopComponents() {
        synchronized (components) {
            Iterator i = components.iterator();

            while (i.hasNext()) {
                Object object = i.next();

                try {
                    log.debug("try to stop " + object.getClass().getName());
                    ContainerUtil.stop(object);
                } catch (Exception e) {
                    log.error("stop: " + object.getClass().getName() + " "
                        + e.getMessage());
                }
            }
        }

        stoptEnabled = true;
    }

    protected void processPluginObject(Object plugin) {
        if (logEnabled) {
            ContainerUtil.enableLogging(plugin, appLogger);
        } else {
            return;
        }

        if (contextEnabled) {
            try {
                ContainerUtil.contextualize(plugin, context);
            } catch (ContextException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }

        if (serviceEnabled) {
            try {
                ContainerUtil.service(plugin, manager);
            } catch (ServiceException e) {
                log.error("plugin serviceable exception: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            return;
        }

        if (configureEnabled) {
            try {
                ContainerUtil.configure(plugin, config);
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }

        if (parameterEnabled) {
            try {
                ContainerUtil.parameterize(plugin, params);
            } catch (ParameterException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }

        if (initializeEnabed) {
            try {
                ContainerUtil.initialize(plugin);
            } catch (Exception e) {
                log.error("plugin initialize exception: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            return;
        }

        if (startEnabled) {
            try {
                ContainerUtil.start(plugin);
            } catch (Exception e) {
                log.error("plugin startable exception: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            return;
        }

        if (guiconfigured) {
            if (plugin instanceof GuiConfigurable) {
                appSetup.addGuiConfigurable("setup.components",
                    (GuiConfigurable) plugin);
            }
        }
    }

    protected void guiConfigure() {
        synchronized (components) {
            Iterator i = components.iterator();

            while (i.hasNext()) {
                Object object = i.next();

                if (object instanceof GuiConfigurable) {
                    appSetup.addGuiConfigurable("setup",
                        (GuiConfigurable) object);
                }
            }
        }

        guiconfigured = true;
    }

    protected void enableLoggingComponents() {
        synchronized (components) {
            Iterator i = components.iterator();

            while (i.hasNext()) {
                Object object = i.next();

                try {
                    if (log.isDebugEnabled()) {
                        log.debug("try to enableLogger: "
                            + object.getClass().getName());
                    }

                    ContainerUtil.enableLogging(object,
                        new LogKitLogger(h.getLoggerFor(
                                object.getClass().getName())));
                } catch (Exception e) {
                    log.error("enableLogger: " + object.getClass().getName()
                        + " " + e.getMessage());
                }
            }
        }

        logEnabled = true;
    }

    /**
     * @param launchParameters
     *            The launchParameters to set.
     */
    public void setLaunchParameters(Hashtable launchParameters) {
        this.launchParameters = launchParameters;
    }

    protected void contextualizeComponents() {
        context = new DefaultContext();

        //		context.put(ApplicationConstants.APPLICATION_CLASSLOADER, this
        //				.getClass().getClassLoader());
        String home = new File("").getAbsolutePath();
        log.debug("app-home=" + home);

        synchronized (components) {
            Iterator i = components.iterator();

            while (i.hasNext()) {
                Object object = i.next();

                try {
                    if (log.isDebugEnabled()) {
                        log.debug("try contextualize: "
                            + object.getClass().getName());
                    }

                    ContainerUtil.contextualize(object, context);
                } catch (Exception ex) {
                    log.error("contextualize: " + object.getClass().getName()
                        + " " + ex.getMessage());
                }
            }
        }
    }

    protected void parameterizeComponents() {
        params = new Parameters();

        Enumeration e = launchParameters.keys();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            System.out.println("key=" + key + " value="
                + launchParameters.get(key));
            params.setParameter(key, (String) launchParameters.get(key));
        }

        synchronized (components) {
            Iterator i = components.iterator();

            while (i.hasNext()) {
                Object object = i.next();

                try {
                    if (log.isDebugEnabled()) {
                        log.debug("try parameterize: "
                            + object.getClass().getName());
                    }

                    ContainerUtil.parameterize(object, params);
                } catch (Exception ex) {
                    log.error("parameterize: " + object.getClass().getName()
                        + " " + ex.getMessage());
                }
            }
        }

        parameterEnabled = true;
    }

    protected void configureComponents() {
        Configuration[] configs = config.getChild("components").getChildren();

        for (int i = 0; i < configs.length; i++) {
            String clazz = null;

            if (configs[i].getName().equals("component")) {
                try {
                    clazz = configs[i].getAttribute("class");

                    if (componentobjects.containsKey(clazz)) {
                        ContainerUtil.configure(componentobjects.get(clazz),
                            configs[i]);
                        if (log.isDebugEnabled()) {
                            log.debug("try configure: "
                                + clazz);
                        }
                    }
                } catch (ConfigurationException e) {
                    log.error("configure:" + clazz + " " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        configureEnabled = true;
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
        splash = new SplashScreen("icons/splash.jpg", "Start ;-)");
        splash.setMaximum(3);
        splash.setVisible(true);

        splash.next("Loading ...");

        //logging stdout to a file
        if (System.getProperty("os.name").indexOf("indows") > -1) {
            try {
                PrintStream ps = new PrintStream(new BufferedOutputStream(
                            new FileOutputStream("jre.log", false), 128));
                System.setErr(ps);
                System.setOut(ps);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

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
    public void setProperty(
        String key,
        String value) {
    }

    public static void main(String[] args) {
        DefaultApplicationContainer container = new DefaultApplicationContainer();
        container.init(CLIParser.parseParameters(args));
    }

    protected void storeConfiguration() {
        //TODO get this ready and deprecate the ConfigManager soon
        
        //create a mutable configuration
//        DefaultConfiguration mutableConfig = new DefaultConfiguration(
//                "configuration");
//        mutableConfig.addAll(config);
//
//        Configuration[] configs = mutableConfig.getChild("components")
//                                               .getChildren();
//
//        for (int i = 0; i < configs.length; i++) {
//            String clazz = null;
//
//            if (configs[i].getName().equals("component")) {
//                try {
//                    clazz = configs[i].getAttribute("class");
//
//                    if (componentobjects.containsKey(clazz)) {
//                        if (componentobjects.get(clazz) instanceof StoreConfigurable) {
//                            Configuration conf = ((StoreConfigurable) componentobjects
//                                .get(clazz)).getConfiguration();
//
//                            if (conf != null) {
//                                //change the config now
//                            }
//                        }
//                    }
//                } catch (ConfigurationException e) {
//                    log.error("storeConfigure:" + clazz + " " + e.getMessage(),
//                        e);
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        Document doc = new DocumentImpl();
//        Element element = ConfigurationUtil.toElement(mutableConfig);
//        doc.appendChild(doc.importNode(element, true));
//
//        try {
//            XMLSerializer serializer = new XMLSerializer(new FileOutputStream(
//                        "conf/foo.xconf"), new OutputFormat(doc, "utf-8", true));
//
//            serializer.serialize(doc);
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
    }

    protected void handleApplicationLifecycle() {
        //handle your lifecycle here
    }
}
