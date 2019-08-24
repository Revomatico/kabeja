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

import de.miethxml.hawron.gui.context.viewer.ExternalViewer;

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
public class XMLExternalViewerWriter {
    /**
     *
     *
     *
     */
    public XMLExternalViewerWriter() {
        super();
    }

    public void write(
        ArrayList viewers,
        String file) {
        Element root = new Element("viewers");
        Document doc = new Document(root);
        addViewers(root, viewers);

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

    private void addViewers(
        Element root,
        ArrayList viewers) {
        Iterator i = viewers.iterator();

        while (i.hasNext()) {
            ExternalViewer view = (ExternalViewer) i.next();
            Element elem = new Element("viewer");
            Element name = new Element("name");
            name.addContent(new Text(view.getName()));
            elem.addContent(name);

            Element command = new Element("command");
            command.addContent(new Text(view.getCommand()));
            elem.addContent(command);

            Element icon = new Element("icon");
            icon.addContent(new Text(view.getIconURL()));
            elem.addContent(icon);

            Element platform = new Element("platform");
            platform.addContent(new Text(view.getPlatform()));
            elem.addContent(platform);

            Element handles = new Element("handles");
            List h = view.getHandles();
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
