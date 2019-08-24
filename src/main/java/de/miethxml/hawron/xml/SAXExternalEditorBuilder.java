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

import java.io.IOException;

import java.util.ArrayList;

import de.miethxml.hawron.gui.context.editor.ExternalEditor;

import de.miethxml.toolkit.conf.ConfigManager;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 *
 * This class read a xml description of external editors from File.
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
public class SAXExternalEditorBuilder implements ContentHandler {
    private ArrayList editors;
    private ExternalEditor editor;
    private StringBuffer buf;
    private ArrayList handles;

    /**
     *
     *
     *
     */
    public SAXExternalEditorBuilder() {
        super();
        buf = new StringBuffer();
        handles = new ArrayList();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endDocument()
     *
     */
    public void endDocument() throws SAXException {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startDocument()
     *
     */
    public void startDocument() throws SAXException {
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
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     *
     */
    public void endPrefixMapping(String prefix) throws SAXException {
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
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     *
     */
    public void setDocumentLocator(Locator locator) {
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
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
     *
     * java.lang.String)
     *
     */
    public void startPrefixMapping(
        String prefix,
        String uri) throws SAXException {
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
        if (localName.equals("handle")) {
            handles.add(buf.toString());
        } else if (localName.equals("editor")) {
            editor.setHandles(handles);
            editors.add(editor);
        } else if (localName.equals("name")) {
            editor.setName(buf.toString());
        } else if (localName.equals("command")) {
            editor.setCommand(buf.toString());
        } else if (localName.equals("platform")) {
            editor.setPlatform(buf.toString());
        } else if (localName.equals("icon")) {
            editor.setIcon(buf.toString());
        }
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
        if (localName.equals("editor")) {
            editor = new ExternalEditor();
            handles.clear();
        } else {
            buf.delete(0, buf.length());
        }
    }

    public ArrayList build(String uri) {
        editors = new ArrayList();

        try {
            //SAXParser saxparser
            // =SAXParserFactory.newInstance().newSAXParser();//ConfigManager.getInstance().getProperty("SAXParser"));
            XMLReader saxparser = XMLReaderFactory.createXMLReader(ConfigManager.getInstance()
                                                                                .getProperty("SAXParser"));

            saxparser.setContentHandler(this);
            saxparser.parse(uri);
        } catch (SAXException e) {
            System.err.println(e.getMessage() + e.getLocalizedMessage());
            e.getStackTrace();
        } catch (IOException ioe) {
        }

        return editors;
    }
}
