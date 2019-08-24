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

/**
 * @author simon
 *
 *
 *  
 */
public interface FileModelHandler {

    
    /**
     * 
     * @param uri
     * @return true if the uri is supported
     */
    
    public boolean isSupported(String uri);
    
    /**
     * Create a FileModel from the given uri.
     * @param uri
     * @return
     */
    
    public FileModel createFileModel(String uri)throws FileModelException;
    
    /**
     * You can check if the given uri needs authentication
     * @param uri
     * @return true - if the uri needs authentication
     */
    
    
    public boolean authenticationRequired(String uri);
    
    /**
     * Create a FileModel from the given uri with username and password.
     * 
     * @param uri
     * @param name
     * @param password
     * @return
     */
    
    public FileModel createFileModel(String uri,String name,char[] password);
}
