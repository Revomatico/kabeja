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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.miethxml.toolkit.conf.ConfigManager;
import de.miethxml.toolkit.io.FileModel;
import de.miethxml.toolkit.ui.GradientLabel;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
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
public class ValidationAction implements Action, ErrorHandler, FileModelAction {
    private StringBuffer msg;
    private boolean error = false;
    private boolean warn = false;
    private boolean initialized = false;
    private String errormsg;
    private String warnmsg;
    private JDialog dialog;
    private JTextArea logview;
    private JScrollPane scroll;
    private GradientLabel label;
    private Color red = new Color(255, 152, 152);

    /**
     *
     *
     *
     */
    public ValidationAction() {
        super();
        msg = new StringBuffer();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#doAction(java.lang.String)
     *
     */
    public void doAction(String uri) {
        errormsg = "";
        warnmsg = "";
        msg.delete(0, msg.length());

        File f = new File(uri);
        initialize();
        msg.append("File:" + f.getName() + "\n");

        try {
            if (validateFile(new FileInputStream(f), f.getAbsolutePath())
                    && !error) {
                label.setTextColor(Color.WHITE);
                label.setText("Valid");
                msg.append("This file is wellformed or valid.\n");
            } else {
                label.setTextColor(red);
                label.setText("Invalid");
            }
        } catch (FileNotFoundException e) {
            label.setTextColor(red);
            label.setText("Invalid");
            msg.append(e.getMessage());
        }

        logview.setText(msg.toString());
        dialog.setVisible(true);

        f = null;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#getIcon()
     *
     */
    public Icon getIcon() {
        return new ImageIcon("icons/val.gif");
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.action.Action#isSupported(java.lang.String)
     *
     */
    public boolean isSupported(String extention) {
        //TODO check the extension
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
        return "Validation";
    }

    private boolean validateFile(
        InputStream in,
        String systemId) {
        error = false;
        warn = false;

        XMLReader saxparser = null;

        try {
            saxparser = XMLReaderFactory.createXMLReader(ConfigManager.getInstance()
                                                                      .getProperty("SAXParser"));
            saxparser.setFeature("http://apache.org/xml/features/validation/dynamic",
                true);
            saxparser.setErrorHandler(this);

            InputSource source = new InputSource(in);
            source.setSystemId(systemId);
            saxparser.parse(source);
        } catch (SAXException e) {
            this.msg.append("[Error] " + e.getLocalizedMessage() + "\n");
            error = true;
        } catch (IOException ioe) {
            msg.append("[Error] " + ioe.getLocalizedMessage() + "\n");
            error = true;
        }

        saxparser = null;

        if (!warn && !error) {
            return true;
        }

        return false;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     *
     */
    public void error(SAXParseException arg0) throws SAXException {
        error = true;

        //errormsg.concat(" " + arg0.getLocalizedMessage());
        msg.append("[Error] line:" + arg0.getLineNumber() + "\n"
            + "[Error] message:" + arg0.getLocalizedMessage() + "\n");
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     *
     */
    public void fatalError(SAXParseException arg0) throws SAXException {
        error = true;
        errormsg.concat(" " + arg0.getLocalizedMessage());
        msg.append("[FatalError] line:" + arg0.getLineNumber() + "\n"
            + "[FatalError] message:" + arg0.getLocalizedMessage() + "\n");
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     *
     */
    public void warning(SAXParseException arg0) throws SAXException {
        warn = true;

        //warnmsg= warnmsg.concat(" " + arg0.getLocalizedMessage());
        msg.append("[Warn] line:" + arg0.getLineNumber() + "\n"
            + "[Warn] message:" + arg0.getLocalizedMessage() + "\n");
    }

    private void initialize() {
        if (!initialized) {
            dialog = new JDialog();
            dialog.setTitle("XMLValidation");
            dialog.getContentPane().setLayout(new BorderLayout());
            label = new GradientLabel();
            dialog.getContentPane().add(label, BorderLayout.NORTH);
            logview = new JTextArea(5, 15);
            logview.setEditable(false);
            scroll = new JScrollPane(logview);
            dialog.getContentPane().add(scroll, BorderLayout.CENTER);
            dialog.setSize(new Dimension(400, 300));
            initialized = true;
        }
    }

    public void doAction(FileModel model) {
        errormsg = "";
        warnmsg = "";
        msg.delete(0, msg.length());

        initialize();
        msg.append("File:" + model.getPath() + "\n");

        try {
            if (validateFile(model.getContent().getInputStream(),
                        model.getPath()) && !error) {
                label.setTextColor(Color.WHITE);
                label.setText("Valid");
                msg.append("This file is wellformed or valid.\n");
            } else {
                label.setTextColor(red);
                label.setText("Invalid");
            }
        } catch (Exception e) {
            label.setTextColor(red);
            label.setText("Invalid");
            msg.append(e.getMessage());
        }

        logview.setText(msg.toString());
        dialog.setVisible(true);
    }
}
