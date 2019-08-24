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

import java.io.File;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 */
public class ApplicationConstants {
    public final static String APPLICATION_NAME = "Hawron";
    public final static String APPLICATION_VERSION = "0.3";
    public final static String APPLICATION_CLASSLOADER = "application-classloader";
    public final static String APPLICATION_HOME = "application-home";
    public final static String COMPONENT_CLASSLOADER = "component-classloader";
    public static String USER_CONFIG_HOME=System.getProperty("user.home")+File.separator+".hawron";
    public static String XCONFIG_FILE = "conf"+File.separator+"hawron.xconf";

}
