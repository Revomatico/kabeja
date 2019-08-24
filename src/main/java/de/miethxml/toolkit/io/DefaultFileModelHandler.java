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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author simon
 *
 *
 *  
 */
public class DefaultFileModelHandler implements FileModelHandler {

    /* (non-Javadoc)
     * @see de.miethxml.toolkit.io.FileModelHandler#isSupported(java.lang.String)
     */
    public boolean isSupported(String uri) {
        //TODO replace this
    
//        if(uri.trim().length()==0){
//            return true;
//        }
//        
//        
//        
//        try {
//            if(!uri.startsWith("file://")){
//                uri = "file://"+uri;
//            }
//            
//            File f = new File(new URI(uri));
//            return true;
//        } catch (Exception e) {
//             return false;
//        }
        
        return true;
       
    }

    /* (non-Javadoc)
     * @see de.miethxml.toolkit.io.FileModelHandler#createFileModel(java.lang.String)
     */
    public FileModel createFileModel(String uri) throws FileModelException {
      return new DefaultFileModel(new File(uri),null);
    }

	/* (non-Javadoc)
	 * @see de.miethxml.toolkit.io.FileModelHandler#authenticationRequired(java.lang.String)
	 */
	public boolean authenticationRequired(String uri) {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see de.miethxml.toolkit.io.FileModelHandler#createFileModel(java.lang.String, java.lang.String, char[])
	 */
	public FileModel createFileModel(String uri, String name, char[] password) {
		// TODO Auto-generated method stub
		return null;
	}
}
