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
package de.miethxml.toolkit.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import de.miethxml.toolkit.plugins.PluginManager;
import de.miethxml.toolkit.plugins.PluginReceiver;

/**
 * @author simon
 *
 *
 *  
 */
public class FileModelManager implements PluginReceiver{
   
    private ArrayList handlers = new ArrayList();
    
    
    
    public void addFileModelHandler(FileModelHandler handler){
        this.handlers.add(handler);
    }
    
    public void removeFileModelHandler(FileModelHandler handler){
        this.handlers.remove(handler);
    }
    
    
    public FileModel createFileModel(String uri) throws FileModelException{
        
        
        Iterator i = handlers.iterator();
        while(i.hasNext()){
            FileModelHandler handler = (FileModelHandler)i.next();
            if(handler.isSupported(uri)){
                return handler.createFileModel(uri);
            }
        }
        
        
        //no handler found
        throw new FileModelException("No Handler for URI:"+uri+" found.");
        
    }
    
    
	/* (non-Javadoc)
	 * @see de.miethxml.toolkit.plugins.PluginReceiver#addPlugin(java.lang.Object)
	 */
	public void addPlugin(Object obj) {
		FileModelHandler handler = (FileModelHandler)obj;
		//plugins first 
		handlers.add(0,handler);

	}
	/* (non-Javadoc)
	 * @see de.miethxml.toolkit.plugins.PluginReceiver#getInterfaces()
	 */
	public Collection getInterfaces() {
		HashSet set = new HashSet();
		set.add(FileModelHandler.class.getName());
		return set;
	}
	/* (non-Javadoc)
	 * @see de.miethxml.toolkit.plugins.PluginReceiver#removePlugin(java.lang.Object)
	 */
	public void removePlugin(Object obj) {
		handlers.remove(obj);

	}
	
	/**
	 * You can set a PluginManager, this allows to add FileModelHandlers as plugins
	 * @param manager
	 */
	
	
	public void setPluginManager(PluginManager manager){
		manager.addPluginReceiver(this);
	}
}
