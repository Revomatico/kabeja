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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import java.util.Hashtable;

import de.miethxml.toolkit.application.Launcher;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.gui.SplashScreen;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 * @deprecated
 */
public class LauncherImpl implements Launcher, Runnable {
    private SplashScreen splash;
    private Hashtable params;

    /**
     *
     *
     *
     */
    public LauncherImpl() {
        super();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.application.Launcher#init()
     *
     */
    public void init(Hashtable param) {
        this.params = param;
        splash = new SplashScreen("icons/splash.jpg", "Start ;-)");
        splash.setMaximum(3);
        splash.setVisible(true);

        Thread t = new Thread(this);
        t.start();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.application.Launcher#setProperty(java.lang.String,
     *
     * java.lang.String)
     *
     */
    public void setProperty(
        String key,
        String value) {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     *
     */
    public void run() {
        splash.next("Loading ...");

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

        ApplicationContainer appContainer = new ApplicationContainer();

        //make cli args available for components
        appContainer.setLaunchParameters(params);
  
        LocaleImpl.getInstance();

        //GUICache.getInstance();
        splash.next("Initialize ...");

        appContainer.initializeApplication();

        //GUICache cache = GUICache.getInstance();
        splash.next("Launching ...");

        appContainer.lauchApplication();

        splash.dispose();
    }
}
