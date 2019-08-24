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
package de.miethxml.toolkit.repository.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;
import de.miethxml.toolkit.io.FileModel;
import de.miethxml.toolkit.repository.Reloadable;
import de.miethxml.toolkit.repository.RepositoryModelImpl;
import de.miethxml.toolkit.repository.RepositorySelectionListener;

/**
 * @author simon
 * 
 * 
 *  
 */
public class ArchivAction extends AbstractAction implements LocaleListener,
        RepositorySelectionListener {

    private RepositoryModelImpl model;

    private FileModel file;

    private String[] zipextension = new String[] { ".zip", ".sxw", ".sxc",
            ".sxi", ".sxd", ".sxg" };

    private String[] extensions = new String[] { ".zip", ".jar", ".bz2", ".gz",
            ".sxw", ".sxc", ".sxi", ".sxd", ".sxg" };

    private boolean archivShowing = false;

    private FileModel oldBase;
    
    private String archivURL;

    public ArchivAction(RepositoryModelImpl model) {
        super(LocaleImpl.getInstance().getString(
                "view.context.popupmenu.open.archiv"));
        putValue(SHORT_DESCRIPTION, LocaleImpl.getInstance().getString(
                "view.context.popupmenu.open.archiv"));
        LocaleImpl.getInstance().addLocaleListener(this);
        this.model = model;
        model.addRepositorySelectionListener(this);
    }

    public void actionPerformed(ActionEvent e) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                String baseURL = "";

                if (!archivShowing) {
                  
                 
                    //get the old
                    FileModel base = model.getBase();
                    baseURL = getArchivURL(file.getPath());
                    //this will fire unselect 
                    model.setBase(baseURL);
                    //set the old values now
                    archivURL = model.getBase().getPath();
                    archivShowing = true;
                    setLabelBack();
                    oldBase = base;
                    
                } else {
                    archivShowing = false;
                    if (oldBase != null) {
                        baseURL = oldBase.getPath();
                    }
                    oldBase = null;
                    setLabelOpenArchive();
                    model.setBase(baseURL);
                  
                }
            
                    
                   

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

        });
    }

    public void langChanged() {
        putValue(SHORT_DESCRIPTION, LocaleImpl.getInstance().getString(
                "view.context.popupmenu.open.archiv"));
        putValue(NAME, LocaleImpl.getInstance().getString(
                "view.context.popupmenu.open.archiv"));
    }

    public void directorySelected(Reloadable model, FileModel directory) {
      
            setEnabled(false);
       
    }

    public void fileSelected(Reloadable model, FileModel f) {

        this.file = f;
        Thread t = new Thread(new Runnable() {

            public void run() {

                String name = file.getName();
                for (int i = 0; i < extensions.length; i++) {
                    if (name.endsWith(extensions[i])) {
                        setEnabled(true);
                        return;
                    }
                }
              
                setEnabled(false);
              
            }
        });
        t.start();

    }

    public void unselect(){
     
        if(!model.getBase().getPath().equals(archivURL)){
         
            //the base has changed
            setLabelOpenArchive();
            oldBase = null;
            archivShowing=false;
             
        }
        setEnabled(false);
    }

    private String getArchivURL(String url) {

        if (url.endsWith(".jar")) {
            return "jar:" + url;
        } else if (url.endsWith(".gz")) {
            return "gz:" + file.getPath();
        } else if (url.endsWith(".bz2")) {
            return "bz2:" + file.getPath();
        } else {
            for (int i = 0; i < zipextension.length; i++) {
                if (url.endsWith(zipextension[i])) {
                    return "zip:" + file.getPath();

                }
            }
        }
        return "";
    }

    private void setLabelOpenArchive() {
        putValue(SHORT_DESCRIPTION, LocaleImpl.getInstance().getString(
                "view.context.popupmenu.open.archiv"));
        putValue(NAME, LocaleImpl.getInstance().getString(
                "view.context.popupmenu.open.archiv"));
   
    }

    private void setLabelBack() {
       
        putValue(SHORT_DESCRIPTION, LocaleImpl.getInstance().getString(
                "view.context.popupmenu.open.archiv.back"));
        putValue(NAME, LocaleImpl.getInstance().getString(
                "view.context.popupmenu.open.archiv.back"));
        setEnabled(true);
        
    }
    
    
    public void setEnabled(boolean b){
      
        if(b){
          
            super.setEnabled(b);
        }else if(!archivShowing){
            super.setEnabled(b);
        }
    }

}