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

import de.miethxml.hawron.cocoon.GeneratorWrapper;
import de.miethxml.hawron.cocoon.MatcherWrapper;
import de.miethxml.hawron.cocoon.SitemapWrapper;
import de.miethxml.hawron.cocoon.TransformerWrapper;

import de.miethxml.toolkit.conf.ConfigManager;

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
 * To change this generated comment go to Window>Preferences>Java>Code
 *
 * Generation>Code and Comments
 *
 */
public class SAXSitemapBuilder implements ContentHandler {
    SitemapWrapper sitemap;
    StringBuffer buf;
    String defaultTransformer;
    String defaultGenerator;
    String defaultSerializer;
    String defaultmatcher;
    final int MODE_MATCH = 0;
    int mode;
    MatcherWrapper match;

    public SAXSitemapBuilder() {
        buf = new StringBuffer();
        defaultTransformer = "";
        defaultGenerator = "";
        defaultSerializer = "";
        defaultmatcher = "";
        mode = -1;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     *
     */
    public void characters(
        char[] arg0,
        int arg1,
        int arg2) throws SAXException {
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
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *
     * java.lang.String, java.lang.String)
     *
     */
    public void endElement(
        String arg0,
        String arg1,
        String arg2) throws SAXException {
        if (arg2.equals("map:match") && (mode == MODE_MATCH)) {
            sitemap.addMatcher(match);
            mode = -1;
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     *
     */
    public void endPrefixMapping(String arg0) throws SAXException {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     *
     */
    public void ignorableWhitespace(
        char[] arg0,
        int arg1,
        int arg2) throws SAXException {
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
        String arg0,
        String arg1) throws SAXException {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     *
     */
    public void setDocumentLocator(Locator arg0) {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     *
     */
    public void skippedEntity(String arg0) throws SAXException {
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
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     *
     */
    public void startElement(
        String arg0,
        String arg1,
        String arg2,
        Attributes arg3) throws SAXException {
        if (arg2.equals("map:match")) {
            //a matcher Object
            mode = MODE_MATCH;
            match = new MatcherWrapper();

            String pattern = arg3.getValue(arg3.getIndex("pattern"));
            match.setMatch(pattern);
        }

        if (mode == MODE_MATCH) {
            if (arg2.equals("map:generate")) {
                GeneratorWrapper gen = new GeneratorWrapper();
                String type = arg3.getValue(arg3.getIndex("type"));

                if (type == null) {
                    type = defaultGenerator;
                }

                gen.setType(type);

                String src = arg3.getValue(arg3.getIndex("src"));

                if (src == null) {
                    src = "";
                }

                gen.setSource(src);
                match.setGenerator(gen);
            } else if (arg2.equals("map:transform")) {
                TransformerWrapper transform = new TransformerWrapper();
                String type = arg3.getValue(arg3.getIndex("type"));

                if (type == null) {
                    type = defaultTransformer;
                }

                transform.setType(type);

                String src = arg3.getValue(arg3.getIndex("src"));

                if (src == null) {
                    src = "";
                }

                transform.setSource(src);
                match.addTransformer(transform);
            } else if (arg2.equals("map:serialize")) {
                String type = arg3.getValue(arg3.getIndex("type"));

                if (type == null) {
                    type = defaultSerializer;
                }

                match.setSerializer(type);
            }
        }

        if (arg2.equals("map:generators")) {
            String def = arg3.getValue(arg3.getIndex("default"));
            defaultGenerator = def;
        } else if (arg2.equals("map:transformers")) {
            String def = arg3.getValue(arg3.getIndex("default"));
            defaultTransformer = def;
        } else if (arg2.equals("map:serializers")) {
            String def = arg3.getValue(arg3.getIndex("default"));
            defaultSerializer = def;
        } else if (arg2.equals("map:matchers")) {
            String def = arg3.getValue(arg3.getIndex("default"));
            defaultTransformer = def;
        } else if (arg2.equals("map:readers")) {
            String def = arg3.getValue(arg3.getIndex("default"));
            defaultTransformer = def;
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
        String arg0,
        String arg1) throws SAXException {
    }

    public SitemapWrapper parseURI(String uri) {
        sitemap = new SitemapWrapper();

        try {
            XMLReader saxparser = XMLReaderFactory.createXMLReader(ConfigManager.getInstance()
                                                                                .getProperty("SAXParser"));
            saxparser.setContentHandler(this);
            saxparser.parse(uri);
        } catch (SAXException e) {
            System.err.println(e.getMessage());
        } catch (IOException ioe) {
        }

        return sitemap;
    }

    public SitemapWrapper parseURI(
        String uri,
        SitemapWrapper sitemap) {
        this.sitemap = sitemap;

        try {
            XMLReader saxparser = XMLReaderFactory.createXMLReader(ConfigManager.getInstance()
                                                                                .getProperty("SAXParser"));

            saxparser.setContentHandler(this);
            saxparser.parse(uri);
        } catch (SAXException e) {
            System.err.println(e.getMessage());
        } catch (IOException ioe) {
        }

        return sitemap;
    }
}
