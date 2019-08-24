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
package de.miethxml.toolkit.wizard;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 */
public class WizardFactory implements ContentHandler {
    public static String ELEMENT_ROOT = "wizard";
    public static String ELEMENT_CONFIGURATION = "configuration";
    public static String ELEMENT_COMPONENT = "component";
    public static String ELEMENT_PARAMETER = "parameter";
    public static String ELEMENT_VALUE = "value";
    public static String ELEMENT_LIST = "list";
    public static String ELEMENT_ENTRY = "entry";
    public static String ATTRIBUTE_CLASS = "class";
    public static String DEFAULT_CONFIGFILE = "resources/wizard.xconf";
    private WizardManager wizard = new WizardManager();
    private WizardConfiguration conf = new WizardConfiguration();
    private WizardComponent component;
    private WizardConfiguration parent;
    private Hashtable parameters;
    private List list;
    private String value;
    private StringBuffer buffer = new StringBuffer();
    private int CONFIGURATION_MODE = 0;
    private int COMPONENT_MODE = 1;
    private int mode = -1;

    /**
     *
     */
    public WizardFactory() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        wizard.initialize();
        wizard.setWizardConfiguration(conf);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(
        char[] ch,
        int start,
        int length) throws SAXException {
        buffer.append(ch, start, length);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(
        char[] ch,
        int start,
        int length) throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     */
    public void skippedEntity(String name) throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    public void setDocumentLocator(Locator locator) {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String,
     *      java.lang.String)
     */
    public void processingInstruction(
        String target,
        String data) throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
     *      java.lang.String)
     */
    public void startPrefixMapping(
        String prefix,
        String uri) throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void endElement(
        String namespaceURI,
        String localName,
        String qName) throws SAXException {
        if (qName.equals(ELEMENT_ENTRY) && (mode == CONFIGURATION_MODE)) {
            System.out.println("add ENTRY:" + buffer.toString());
            list.add(buffer.toString());
        } else if (qName.equals(ELEMENT_VALUE) && (mode == CONFIGURATION_MODE)) {
            System.out.println("setValue:" + value);

            if (list != null) {
                System.out.println("List value:" + list);
                conf.setValue(value, list);
            } else {
                System.out.println("textvalue");
                conf.setValue(value, buffer.toString());
            }
        } else if (mode == CONFIGURATION_MODE) {
            //switch back to parent
            if (parent != null) {
                conf = parent;
            }
        } else if (qName.equals(ELEMENT_CONFIGURATION)) {
            mode = -1;
        } else if (qName.equals(ELEMENT_COMPONENT)) {
            mode = -1;
            System.out.println("setPARAMETER");
            component.setup(parameters);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(
        String namespaceURI,
        String localName,
        String qName,
        Attributes atts) throws SAXException {
        if (qName.equals(ELEMENT_COMPONENT)) {
            String clazzName = atts.getValue(ATTRIBUTE_CLASS);
            mode = COMPONENT_MODE;
            parameters = new Hashtable();
            System.out.println("componentCLASS:" + clazzName);

            try {
                Class clazz = this.getClass().getClassLoader().loadClass(clazzName);

                component = (WizardComponent) clazz.newInstance();
                wizard.addWizardComponent(component);
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (qName.equals(ELEMENT_CONFIGURATION)) {
            mode = CONFIGURATION_MODE;
            processConfigurationAttributes(conf, atts);
        } else if (qName.equals(ELEMENT_PARAMETER) && (mode == COMPONENT_MODE)) {
            System.out.println("PARAMETER for:" + component.getTitle());
            System.out.println("NAME:" + atts.getValue("name") + " VALUE="
                + atts.getValue("value"));
            parameters.put(atts.getValue("name"), atts.getValue("value"));
        } else if (qName.equals(ELEMENT_VALUE) && (mode == CONFIGURATION_MODE)) {
            value = atts.getValue("name");
            buffer.delete(0, buffer.length());
        } else if (qName.equals(ELEMENT_LIST) && (mode == CONFIGURATION_MODE)) {
            System.out.println("new List");
            list = new ArrayList();
        } else if (qName.equals(ELEMENT_ENTRY) && (mode == CONFIGURATION_MODE)) {
            System.out.println("new Entry");
            buffer.delete(0, buffer.length());
        }

        //		else if(qName.equals(ELEMENT_CONFIGURATION) && mode == CONFIGURATION_MODE){
        //			WizardConfiguration config = new WizardConfiguration();
        //			config.setName(qName);
        //			processConfigurationAttributes(config,atts);
        //			
        //			conf.addChild(config);
        //			parent = conf;
        //			conf = config;
        //			
        //			
        //		}
    }

    private void processConfigurationAttributes(
        WizardConfiguration config,
        Attributes atts) {
        int count = atts.getLength();

        for (int i = 0; i < count; i++) {
            config.setValue(atts.getQName(i), atts.getValue(i));
        }
    }

    public WizardManager getWizardManager() {
        return wizard;
    }

    /**
     * Create a InstallManager from url.
     *
     * <b>Sample File </b>
     *
     * <pre>
     *
     *  &lt;wizard&gt;
     *    &lt;configuration&gt;
     *      &lt;base-dir src=&quot;...&quot;/&gt;
     *      &lt;distribution-url src=&quot;http://....zip&quot;/&gt;
     *      &lt;archiv src=&quot;/cdrom/mypackage.zip&quot;/&gt;
     *    &lt;/configuration&gt;
     *    &lt;components&gt;
     *         &lt;component class=&quot;my.implementation&quot;/&gt;
     *         &lt;component class=&quot;my.implementation&quot;/&gt;
     *           ...
     *  &lt;/wizard&gt;
     *
     * </pre>
     *
     * @param url
     * @return
     */
    public static WizardManager createFromFile(String url) {
        WizardFactory factory = new WizardFactory();

        try {
            XMLReader saxparser = SAXParserFactory.newInstance().newSAXParser()
                                                  .getXMLReader();
            saxparser.setContentHandler(factory);
            saxparser.parse(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return factory.getWizardManager();
    }

    public static WizardManager createDefaultInstaller() {
        String file = WizardFactory.class.getClassLoader()
                                         .getResource(DEFAULT_CONFIGFILE)
                                         .toExternalForm();

        return createFromFile(file);
    }
}
