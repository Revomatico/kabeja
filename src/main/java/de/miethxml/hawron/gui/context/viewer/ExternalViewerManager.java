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
package de.miethxml.hawron.gui.context.viewer;

import java.io.File;

import java.util.ArrayList;

import de.miethxml.hawron.ApplicationConstants;
import de.miethxml.hawron.xml.SAXExternalViewerBuilder;
import de.miethxml.hawron.xml.XMLExternalViewerWriter;


/**
 * This class handle the external viewer. First the viewer.xml will parsed and
 * generate then the Viewer.
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
public class ExternalViewerManager {
    public static String CONFIGFILE = "conf"+File.separator+"viewers.xml";
    private ArrayList viewers= new ArrayList();
    private String userConfig;

    /**
     *
     *
     *
     */
    public ExternalViewerManager() {
        super();
       
      
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#init()
     *
     */
    public void initialize() {
        File f = new File(ApplicationConstants.USER_CONFIG_HOME +File.separator+ CONFIGFILE);
        if (f.exists()) {
          
            load(f);
           

        }else{
            load(new File(CONFIGFILE));
        }
       
     
    }

    public int getViewerCount() {
        return viewers.size();
    }

    public void addViewer(ExternalViewer viewer) {
        viewers.add(viewer);
        save();
    }

    public ExternalViewer getViewer(int index) {
        return (ExternalViewer) viewers.get(index);
    }

    public void removeViewer(int index) {
        if ((index >= 0) && (index < viewers.size())) {
            viewers.remove(index);
        }

        save();
    }

    private void load(File file) {

        if (file.exists() && file.isFile()) {
            SAXExternalViewerBuilder parser = new SAXExternalViewerBuilder();
            viewers = parser.build(file.getAbsolutePath());
        }
    }

    public void save() {
        XMLExternalViewerWriter writer = new XMLExternalViewerWriter();
        String config;
        File f = new File(ApplicationConstants.USER_CONFIG_HOME);
        if(f.exists()){
            config = f.getAbsolutePath()+File.separator+CONFIGFILE;
            File dir = new File(config);
            if(!dir.getParentFile().exists()){
                dir.getParentFile().mkdir();
            }
        }else{
            config = CONFIGFILE;
        }
  
        writer.write(viewers, config);
    }
}
