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
package de.miethxml.toolkit.plugins.xml;

import java.io.IOException;

import java.util.ArrayList;

import de.miethxml.toolkit.conf.ConfigManager;
import de.miethxml.toolkit.plugins.PluginConfig;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


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
public class SAXPluginBuilder implements ContentHandler {
    StringBuffer buf;
    int DESCRIPTION_MODE = 0;
    int INSTANCECLASS_MODE = 1;
    int NAME_MODE = 2;
    int INTERFACE_MODE = 3;
    ArrayList classpath;
    ArrayList resources;
    PluginConfig plugin;

    /**
     *
     *
     *
     */
    public SAXPluginBuilder() {
        super();
        buf = new StringBuffer();
        classpath = new ArrayList();
        resources = new ArrayList();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     *
     */
    public void characters(
        char[] ch,
        int start,
        int length) throws SAXException {
        buf.append(ch, start, length);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endDocument()
     *
     */
    public void endDocument() throws SAXException {
        //cast list to String[]
        if (classpath.size() > 0) {
            String[] cpath = new String[classpath.size()];

            for (int i = 0; i < classpath.size(); i++) {
                cpath[i] = (String) classpath.get(i);
            }

            plugin.setClasspath(cpath);
        }

        if (resources.size() > 0) {
            String[] res = new String[resources.size()];

            for (int i = 0; i < resources.size(); i++) {
                res[i] = (String) resources.get(i);
            }

            plugin.setResources(res);
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *
     * java.lang.String, java.lang.String)
     *
     */
    public void endElement(
        String namespaceURI,
        String localName,
        String qName) throws SAXException {
        if (localName.equals("interface")) {
            plugin.setInterfaceName(buf.toString());
        } else if (localName.equals("name")) {
            plugin.setName(buf.toString());
        } else if (localName.equals("description")) {
            plugin.setDescription(buf.toString());
        } else if (localName.equals("instanceclass")) {
            plugin.setInstanceClass(buf.toString());
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     *
     */
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     *
     */
    public void ignorableWhitespace(
        char[] ch,
        int start,
        int length) throws SAXException {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String,
     *
     * java.lang.String)
     *
     */
    public void processingInstruction(
        String target,
        String data) throws SAXException {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     *
     */
    public void setDocumentLocator(Locator locator) {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     *
     */
    public void skippedEntity(String name) throws SAXException {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startDocument()
     *
     */
    public void startDocument() throws SAXException {
        plugin = new PluginConfig();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     *
     */
    public void startElement(
        String namespaceURI,
        String localName,
        String qName,
        Attributes atts) throws SAXException {
        buf.delete(0, buf.length());

        if (localName.equals("classpath")) {
            String src = atts.getValue(atts.getIndex("src"));
            classpath.add(src);
        } else if (localName.equals("resource")) {
            String src = atts.getValue(atts.getIndex("src"));
            resources.add(src);
        } else if (localName.equals("resources")) {
            classpath.clear();
            resources.clear();
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
     *
     * java.lang.String)
     *
     */
    public void startPrefixMapping(
        String prefix,
        String uri) throws SAXException {
    }

    public PluginConfig parseURI(String uri) {
        try {
            XMLReader saxparser = XMLReaderFactory.createXMLReader(ConfigManager.getInstance()
                                                                                .getProperty("SAXParser"));
            saxparser.setContentHandler(this);
            saxparser.parse(uri);
        } catch (SAXException e) {
            System.err.println(e.getMessage());
        } catch (IOException ioe) {
        }

        return plugin;
    }
}
