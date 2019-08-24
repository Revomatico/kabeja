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
package de.miethxml.hawron.gui.context.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.jdom.Document;
import org.jdom.JDOMException;

import org.jdom.input.SAXBuilder;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.miethxml.toolkit.io.FileModel;


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
public class XMLFormatAction implements Action,FileModelAction {
    /**
     *
     *
     *
     */
    public XMLFormatAction() {
        super();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#doAction(java.lang.String)
     *
     */
    public void doAction(String uri) {
        File f = new File(uri);

        if (f.exists()) {
            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            Format format = null;
            XMLOutputter out = null;
            Document doc = null;

            try {
                doc = builder.build(f);
                format = Format.getPrettyFormat();
                out = new XMLOutputter(format);

                FileWriter writer = new FileWriter(f);
                out.output(doc, writer);
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //maybe help for the GC
            builder = null;
            format = null;
            out = null;
            doc = null;
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#getIcon()
     *
     */
    public Icon getIcon() {
        return new ImageIcon("icons/indent.gif");
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#isSupported(java.lang.String)
     *
     */
    public boolean isSupported(String extention) {
        //TODO check the extention
        return true;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#isHandleDirectory()
     *
     */
    public boolean isHandleDirectory() {
        return false;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#isHandleFile()
     *
     */
    public boolean isHandleFile() {
        return true;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#getTooltip(java.lang.String)
     *
     */
    public String getToolTip(String lang) {
        return "XML Format";
    }
    
    public void doAction(FileModel model) {
        SAXBuilder builder = new SAXBuilder();
        Format format = null;
        XMLOutputter out = null;
        Document doc = null;
        BufferedInputStream in=null;
        BufferedOutputStream writer=null;
        try {
             in = new BufferedInputStream(model.getContent().getInputStream());
            doc = builder.build(in);
            format = Format.getPrettyFormat();
            out = new XMLOutputter(format);
            in.close();
             writer = new BufferedOutputStream(model.getContent().getOutputStream());
            out.output(doc,writer );
            writer.flush();
            writer.close();
            in = null;
        } catch (Exception e) {
            
            e.printStackTrace();
            try {
                in.close();
                writer.close();
            } catch (IOException e1) {
                
                e1.printStackTrace();
            }
           
            JOptionPane.showMessageDialog(null,e.getLocalizedMessage(),"Error",JOptionPane.WARNING_MESSAGE);
        }

        //maybe help for the GC
        builder = null;
        format = null;
        out = null;
        doc = null;
        in=null;
        writer=null;

    }
}
