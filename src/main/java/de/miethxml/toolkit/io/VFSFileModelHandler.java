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

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;


/**
 * @author simon
 *
 *
 *
 */
public class VFSFileModelHandler implements FileModelHandler{
    
    
    private String[] protocols = new String[]{"sftp:","ftp:","smb","zip:","jar:","gz:","bz2:","http:","file:","webdav:"};
    

    public FileModel createFileModel(String uri) throws FileModelException {
        try {
            FileObject f = VFS.getManager().resolveFile(uri);
            VFSFileModel model = new VFSFileModel(f, null);
            return model;
        } catch (FileSystemException e) {
            throw new FileModelException(e);
        }
    }
    public boolean isSupported(String uri) {
       for(int i=0;i<protocols.length;i++){
           if(uri.startsWith(protocols[i])){
               return true;
           }
           
       }
        
        return false;
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
