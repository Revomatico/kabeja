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
package de.miethxml.hawron.gui.project;

import java.awt.event.ActionEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.gui.io.ExportFileFilter;
import de.miethxml.hawron.gui.io.ProjectExportFileFilter;
import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.ProjectComponent;
import de.miethxml.hawron.project.ProjectConfigListener;
import de.miethxml.hawron.project.ProjectExport;
import de.miethxml.hawron.xml.AntConfigFileWriter;
import de.miethxml.hawron.xml.CLIConfigFileWriter;
import de.miethxml.hawron.xml.HawronConfigFileWriter;

import de.miethxml.toolkit.conf.ConfigManager;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;
import de.miethxml.toolkit.gui.LocaleLabel;
import de.miethxml.toolkit.gui.LocaleSeparator;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;


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
public class ProjectSaveAsAction extends AbstractAction
    implements LocaleListener, ProjectConfigListener, Serviceable,
        Initializable, ProjectComponent {
    private Project project;
    private ExportFileFilter cli;
    private ExportFileFilter extended;
    private ExportFileFilter ant;
    private JFileChooser fc;
    private JCheckBox relativePath;
    private FileFilter last;
    private ServiceManager manager;
    private JTextArea description;

    public ProjectSaveAsAction() {
        super(LocaleImpl.getInstance().getString("menu.file.save_as"),
            new ImageIcon("icons/save_as.gif"));
        putValue(SHORT_DESCRIPTION,
            LocaleImpl.getInstance().getString("menu.file.save_as"));
        LocaleImpl.getInstance().addLocaleListener(this);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
        fc.setFileFilter(last);
        relativePath.setSelected(false);

        int returnVal = fc.showSaveDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            ProjectExportFileFilter filter = (ProjectExportFileFilter) fc
                .getFileFilter();
            ProjectExport exporter = filter.getExporter();

            //	project.save(fc.getSelectedFile().getPath(), filter.getFormat(),
            // relativePath.isSelected());
            File directory = new File(project.getFilename());

            if ((directory != null) && directory.exists()) {
                directory = directory.getParentFile();

                //the directory not the file
                exporter.setRelativePathEnabled(directory.getAbsolutePath(),
                    relativePath.isSelected());
                ConfigManager.getInstance().setProperty("project.last.directory",
                    directory.getAbsolutePath());
            } else {
                exporter.setRelativePathEnabled("", false);
            }

            exporter.export(project, fc.getSelectedFile().getAbsolutePath());
            last = filter;
            project.setFilename(fc.getSelectedFile().getAbsolutePath());
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.conf.LocaleListener#langChanged()
     *
     */
    public void langChanged() {
        putValue(SHORT_DESCRIPTION,
            LocaleImpl.getInstance().getString("menu.file.save_as"));
        putValue(NAME, LocaleImpl.getInstance().getString("menu.file.save_as"));
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.project.ProjectConfigListener#configChanged(de.miethxml.project.Project)
     *
     */
    public void configChanged(Project project) {
        this.project = project;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#init()
     *
     */
    public void initialize() {
        project.addProjectConfigListener(this);

        //create a JFileChooser
        fc = new JFileChooser();

        ProjectExportFileFilter filter = new ProjectExportFileFilter(new HawronConfigFileWriter());
        fc.addChoosableFileFilter(filter);
        last = filter;
        filter = new ProjectExportFileFilter(new CLIConfigFileWriter());
        fc.addChoosableFileFilter(filter);
        filter = new ProjectExportFileFilter(new AntConfigFileWriter());
        fc.addChoosableFileFilter(filter);
        fc.setAcceptAllFileFilterUsed(false);

        //add the relativePath checkbox
        fc.setAccessory(getAccessory());
        fc.validate();

        PropertyChangeListener listener = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(JFileChooser.FILE_FILTER_CHANGED_PROPERTY)) {
                        ProjectExportFileFilter filter = (ProjectExportFileFilter) evt
                            .getNewValue();

                        if (filter != null) {
                            ProjectExport exporter = filter.getExporter();
                            description.setText(exporter.getDescription(""));
                        }
                    }
                }
            };

        fc.addPropertyChangeListener(listener);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#setComponentManager(de.miethxml.toolkit.component.ComponentManager)
     *
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    private JPanel getAccessory() {
        FormLayout layout = new FormLayout("3dlu,p,2dlu,fill:p:grow,3dlu",
                "3dlu,p,2dlu,fill:p:grow,9dlu,p,2dlu,p,3dlu");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();
        builder.add(new LocaleSeparator("dialog.file.export.description"),
            cc.xywh(2, 2, 3, 1));
        description = new JTextArea(8, 15);
        description.setOpaque(false);
        description.setEditable(false);
        description.setWrapStyleWord(true);
        description.setLineWrap(true);
        builder.add(description, cc.xywh(2, 4, 3, 1));
        builder.add(new LocaleSeparator("dialog.file.export.option"),
            cc.xywh(2, 6, 3, 1));
        relativePath = new JCheckBox();
        builder.add(relativePath, cc.xy(2, 8));
        builder.add(new LocaleLabel("dialog.file.export.label.relativepath"),
            cc.xy(4, 8));

        return builder.getPanel();
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
