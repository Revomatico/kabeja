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
package de.miethxml.hawron.gui.context.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import de.miethxml.toolkit.cache.Cacheable;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.gui.LocaleButton;


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
public class PlainTextEditor implements Cacheable, CacheableEditor {
    private JFrame frame;
    private String file;
    private JTextArea editor;
    private ArrayList listeners;
    private boolean closing = false;
    boolean isNew;
    boolean changed = false;
    private String[] unsupported = new String[] {
            ".gif", ".jpeg", ".jpg", ".png", ".doc"
        };
    private String[] xmlextension = new String[] {
            ".xml", ".svg", ".xslt", ".xsl", ".xmap", ".xconf", ".fo", ".xsp"
        };
    private boolean destroy = false;

    /**
     * @throws java.awt.HeadlessException
     *
     */
    public PlainTextEditor() {
        super();
        listeners = new ArrayList();
    }

    /**
     * @param gc
     *
     */
    public void init() {
        frame = new JFrame();
        frame.setTitle("PlainTextEditor");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(getToolbar(), BorderLayout.NORTH);
        frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    //capture the second close-event and ignore
                    if (!destroy && !closing) {
                        setVisible(false);
                    }
                }
            });
        editor = new JTextArea();
        editor.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                    changed = true;
                }

                public void keyTyped(KeyEvent e) {
                    changed = true;
                }

                public void keyReleased(KeyEvent e) {
                    changed = true;
                }
            });

        JScrollPane sp = new JScrollPane(editor);
        frame.getContentPane().add(sp, BorderLayout.CENTER);
        frame.setSize(new Dimension(640, 480));

        //pack();
    }

    public void open(String file) {
        this.file = file;
        this.changed = false;
        editor.setText("");

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line = null;

            while ((line = in.readLine()) != null) {
                editor.append(line + "\n");
            }

            in.close();
            in = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
        }

        frame.validate();
        frame.setTitle(file);
        isNew = false;
        frame.setSize(new Dimension(640, 480));
    }

    public void save() {
        if (isNew) {
            JFileChooser fc = new JFileChooser(this.file);
            int returnVal = fc.showSaveDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                this.file = fc.getSelectedFile().getAbsolutePath();
                frame.setTitle(file);
            } else {
                this.file = null;
            }
        }

        if (file != null) {
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(
                            new FileWriter(file)));
                out.write(editor.getText());
                out.flush();
                out.close();
                changed = false;
                out = null;
                isNew = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void destroy() {
        destroy = true;
        editor = null;

        //this.dispose();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#getIcon()
     *
     */
    public Icon getIcon() {
        ImageIcon icon = new ImageIcon("icons/text_edit.gif");

        return icon;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#getSetup()
     *
     */
    public JComponent getSetup() {
        return null;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#getTooltip(java.lang.String)
     *
     */
    public String getToolTip(String lang) {
        return "PlainTextEditor";
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#hasSetup()
     *
     */
    public boolean hasSetup() {
        return false;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#isSetup()
     *
     */
    public boolean isSetup() {
        return true;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#isSupported(java.lang.String)
     *
     */
    public boolean isSupported(String extension) {
        for (int i = 0; i < unsupported.length; i++) {
            if (extension.endsWith(unsupported[i])) {
                return false;
            }
        }

        return true;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#newFile(java.lang.String)
     *
     */
    public void newFile(String file) {
        isNew = true;
        this.changed = false;
        this.file = file;
        frame.setTitle("New File");
        editor.setText("");
        frame.setSize(new Dimension(640, 480));
    }

    private boolean isXMLDocument(String file) {
        for (int i = 0; i < xmlextension.length; i++) {
            if (file.toLowerCase().endsWith(xmlextension[i])) {
                return true;
            }
        }

        return false;
    }

    private JToolBar getToolbar() {
        JToolBar toolbar = new JToolBar();
        LocaleButton button = new LocaleButton("comon.button.save",
                new ImageIcon("icons/save.gif"), false);
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    save();
                }
            });
        toolbar.add(button);
        button = new LocaleButton("common.button.copy",
                new ImageIcon("icons/copy_edit.gif"), false);
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editor.copy();
                }
            });
        toolbar.add(button);
        button = new LocaleButton("common.button.paste",
                new ImageIcon("icons/paste_edit.gif"), false);
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editor.paste();
                }
            });
        toolbar.add(button);
        button = new LocaleButton("common.button.cut",
                new ImageIcon("icons/cut_edit.gif"), false);
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editor.cut();
                }
            });
        toolbar.add(button);

        return toolbar;
    }

    private boolean checkChanges() {
        if (changed) {
            int option = JOptionPane.showConfirmDialog(null,
                    LocaleImpl.getInstance().getString("editor.plaintexteditord.dialog.savechanges"));

            if (option == JOptionPane.OK_OPTION) {
                save();

                return true;
            }

            if (option == JOptionPane.CANCEL_OPTION) {
                return false;
            }

            changed = false;
        }

        return true;
    }

    public void setVisible(boolean state) {
        if (!state) {
            closing = true;
        } else {
            closing = false;
        }

        if (!state && checkChanges()) {
            frame.setVisible(state);

            if (!destroy) {
                fireCloseEvent();
            }
        } else {
            frame.setVisible(state);
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#addEditorCloseListener(de.miethxml.gui.editor.EditorCloseListener)
     *
     */
    public void addEditorCloseListener(EditorCloseListener listener) {
        listeners.add(listener);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#removeEditorCloseListener(de.miethxml.gui.editor.EditorCloseListener)
     *
     */
    public void removeEditorCloseListener(EditorCloseListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private void fireCloseEvent() {
        synchronized (listeners) {
            Iterator i = listeners.iterator();

            while (i.hasNext()) {
                EditorCloseListener l = (EditorCloseListener) i.next();
                l.close(this);
            }
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.editor.Editor#createNewEditor()
     *
     */
    public Editor createNewEditor() {
        PlainTextEditor edit = new PlainTextEditor();
        edit.init();

        return edit;
    }

    public void dispose() {
        destroy();
    }
}
