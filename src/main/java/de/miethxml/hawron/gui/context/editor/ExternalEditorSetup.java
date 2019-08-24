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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.toolkit.component.GuiConfigurable;
import de.miethxml.toolkit.gui.ImagePreviewer;
import de.miethxml.toolkit.gui.LocaleButton;
import de.miethxml.toolkit.gui.StringListView;
import de.miethxml.toolkit.gui.StringListViewImpl;

import org.apache.avalon.framework.activity.Initializable;


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
public class ExternalEditorSetup implements GuiConfigurable, Initializable,
    ActionListener {
    private static String ACTION_SETTINGS_OK = "settings.ok";
    private static String ACTION_SETTINGS_CANCEL = "settings.cancel";
    public static String DEFAUL_ICON = "icons/text_edit.gif";
    private JList handles;
    private JList editors;
    private JTextField name;
    private JTextField icon;
    private JTextField command;
    private JTextField handle;
    private DefaultListModel handlesdata;
    private JPanel setup;
    private ExternalEditorManager em;
    private DefaultListModel editordata;
    private StringListView suffices;
    private JFileChooser commandChooser;
    private JDialog dialog;
    private boolean isNew = false;
    private int editviewer = -1;

    /**
     *
     *
     *
     */
    public ExternalEditorSetup() {
        super();
        handlesdata = new DefaultListModel();
        editordata = new DefaultListModel();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.GuiConfigurable#getLabel()
     *
     */
    public String getLabel() {
        return "External Editor Setup";
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.GuiConfigurable#getSetupComponent()
     *
     */
    public JComponent getSetupComponent() {
        if (setup == null) {
            setup = createSetupPanel();
        }

        return setup;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.GuiConfigurable#setup()
     *
     */
    public void setup() {
        em.save();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#init()
     *
     */
    public void initialize() {
        setEditors();
    }

    private JPanel createSetupPanel() {
        FormLayout panellayout = new FormLayout("3dlu,pref,3dlu,pref:grow,3dlu,pref,3dlu,pref:grow,3dlu",
                "3dlu,p,3dlu,p,2dlu,p,2dlu,p,fill:3dlu:grow,3dlu");
        PanelBuilder builder = new PanelBuilder(panellayout);
        CellConstraints cc = new CellConstraints();
        builder.addSeparator("All Editors", cc.xywh(2, 2, 5, 1));
        editors = new JList(editordata);
        editors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane sp = new JScrollPane(editors);
        builder.add(sp, cc.xywh(4, 4, 1, 6));

        JButton button = new JButton("add");
        button.setActionCommand("add.editor");
        button.addActionListener(this);
        builder.add(button, cc.xy(6, 4));
        button = new JButton("edit");
        button.setActionCommand("edit.editor");
        button.addActionListener(this);
        builder.add(button, cc.xy(6, 6));
        button = new JButton("remove");
        button.setActionCommand("remove.editor");
        button.addActionListener(this);
        builder.add(button, cc.xy(6, 8));

        createDialog();

        return builder.getPanel();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("remove.editor")) {
            em.removeEditor(editors.getSelectedIndex());
            editordata.removeElementAt(editors.getSelectedIndex());
        } else if (e.getActionCommand().equals("edit.editor")) {
            isNew = false;
            setEditor(editors.getSelectedIndex());
            dialog.setVisible(true);
        } else if (e.getActionCommand().equals("add.editor")) {
            isNew = true;
            setBlankFields();
            dialog.setVisible(true);
        } else if (e.getActionCommand().equals("choose.icon")) {
            JFileChooser fs = new JFileChooser("icons/");
            ImagePreviewer ip = new ImagePreviewer(36, 36);
            fs.setAccessory(ip);
            fs.addPropertyChangeListener(ip);

            if (fs.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                icon.setText(fs.getSelectedFile().getPath());
            }
        } else if (e.getActionCommand().equals("choose.command")) {
            commandChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int value = commandChooser.showDialog(null, "Choose Program");

            if (value == JFileChooser.APPROVE_OPTION) {
                command.setText(commandChooser.getSelectedFile()
                                              .getAbsolutePath() + "  %s");
            }
        } else if (e.getActionCommand().equals(ACTION_SETTINGS_CANCEL)) {
            dialog.setVisible(false);
        } else if (e.getActionCommand().equals(ACTION_SETTINGS_OK)) {
            if (!isNew) {
                em.removeEditor(editviewer);
            }

            addNewEditor();

            dialog.setVisible(false);
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.GuiConfigurable#isSetup()
     *
     */
    public boolean isSetup() {
        return false;
    }

    private void setEditors() {
        for (int i = 0; i < em.getEditorCount(); i++) {
            editordata.addElement(em.getEditor(i).getName());
        }
    }

    private void addNewEditor() {
        ExternalEditor edit = new ExternalEditor();
        edit.init();
        edit.setName(name.getText());
        edit.setCommand(command.getText());
        edit.setIcon(icon.getText());

        edit.setHandles(suffices.getStringList());

        em.addEditor(edit);
        editordata.addElement(name.getText());
        setBlankFields();
    }

    private void setBlankFields() {
        name.setText("");
        command.setText("");
        icon.setText(DEFAUL_ICON);

        //handlesdata.removeAllElements();
        ArrayList list = new ArrayList();
        list.add("*");
        suffices.setStringList(list);
    }

    private void setEditor(int index) {
        setBlankFields();

        ExternalEditor edit = em.getEditor(index);
        name.setText(edit.getName());
        command.setText(edit.getCommand());
        icon.setText(edit.getIconURL());

        suffices.setStringList(edit.getHandles());
    }

    public void setExternalEditorManager(ExternalEditorManager manager) {
        this.em = manager;
    }

    private void createDialog() {
        FormLayout panellayout = new FormLayout("3dlu,pref,3dlu,pref:grow,3dlu,pref,3dlu,pref:grow,3dlu",
                "3dlu,p,3dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,20dlu:grow,3dlu");

        PanelBuilder builder = new PanelBuilder(panellayout);
        CellConstraints cc = new CellConstraints();

        dialog = new JDialog();
        dialog.setTitle("Editor Settings");

        builder.addLabel("Name", cc.xy(2, 4));
        name = new JTextField(20);
        builder.add(name, cc.xy(4, 4));

        builder.addLabel("Command ", cc.xy(2, 6));
        command = new JTextField(20);
        builder.add(command, cc.xy(4, 6));

        JButton button = new JButton("...");
        button.setActionCommand("choose.command");
        button.addActionListener(this);
        builder.add(button, cc.xy(6, 6));
        commandChooser = new JFileChooser();
        builder.addLabel("%s for file ", cc.xy(8, 6));
        builder.addLabel("Icon", cc.xy(2, 8));

        icon = new JTextField(20);
        builder.add(icon, cc.xy(4, 8));
        button = new JButton("...");
        button.setActionCommand("choose.icon");
        button.addActionListener(this);
        builder.add(button, cc.xy(6, 8));

        builder.addSeparator("Extensions", cc.xywh(2, 10, 7, 1));

        suffices = new StringListViewImpl();
        suffices.init();
        builder.add(suffices.getView(), cc.xywh(2, 12, 5, 2));

        dialog.getContentPane().add(builder.getPanel(), BorderLayout.CENTER);

        //button panel
        JPanel buttonpanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        button = new LocaleButton("common.button.cancel");
        button.addActionListener(this);
        button.setActionCommand(ACTION_SETTINGS_CANCEL);
        buttonpanel.add(button);

        button = new LocaleButton("common.button.ok");
        button.addActionListener(this);
        button.setActionCommand(ACTION_SETTINGS_OK);
        buttonpanel.add(button);

        dialog.getContentPane().add(buttonpanel, BorderLayout.SOUTH);

        dialog.pack();
    }
}
