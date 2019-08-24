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
package de.miethxml.hawron.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.miethxml.hawron.gui.context.editor.ExternalEditor;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


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
public class XMLExternalEditorWriter {
    /**
     *
     *
     *
     */
    public XMLExternalEditorWriter() {
        super();
    }

    public void write(
        ArrayList editors,
        String file) {
        Element root = new Element("editors");
        Document doc = new Document(root);
        addEditors(root, editors);

        File f = new File(file);

        try {
            Format format = Format.getPrettyFormat();
            XMLOutputter out = new XMLOutputter(format);
            FileWriter writer = new FileWriter(f);
            out.output(doc, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addEditors(
        Element root,
        ArrayList editors) {
        Iterator i = editors.iterator();

        while (i.hasNext()) {
            ExternalEditor edit = (ExternalEditor) i.next();
            Element elem = new Element("editor");
            Element name = new Element("name");
            name.addContent(new Text(edit.getName()));
            elem.addContent(name);

            Element command = new Element("command");
            command.addContent(new Text(edit.getCommand()));
            elem.addContent(command);

            Element icon = new Element("icon");
            icon.addContent(new Text(edit.getIconURL()));
            elem.addContent(icon);

            Element platform = new Element("platform");
            platform.addContent(new Text(edit.getPlatform()));
            elem.addContent(platform);

            Element handles = new Element("handles");
            List h = edit.getHandles();
            Iterator x = h.iterator();

            while (x.hasNext()) {
                String handle = (String) x.next();
                Element ha = new Element("handle");
                ha.addContent(new Text(handle));
                handles.addContent(ha);
            }

            elem.addContent(handles);
            root.addContent(elem);
        }
    }
}
