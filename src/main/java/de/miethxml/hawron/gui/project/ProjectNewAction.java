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

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import de.miethxml.hawron.gui.MainFrame;
import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.ProjectComponent;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;

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
public class ProjectNewAction extends AbstractAction implements LocaleListener,
		ProjectComponent {
	private MainFrame frame;

	private ProjectView projectView;

	private Project project;

	private ProjectViewComponentManager projectViewManager;

	/**
	 * 
	 * 
	 * 
	 */
	public ProjectNewAction() {
		super(LocaleImpl.getInstance().getString("menu.file.new"),
				new ImageIcon("icons/new.gif"));
		putValue(SHORT_DESCRIPTION, LocaleImpl.getInstance().getString(
				"menu.file.new"));
		putValue(SMALL_ICON, new ImageIcon("icons/new.gif"));
		LocaleImpl.getInstance().addLocaleListener(this);
		putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke
				.getKeyStroke("control N"));
	}

	/**
	 * @param name
	 * 
	 */
	public ProjectNewAction(String name) {
		super(name);
	}

	/**
	 * @param name
	 * 
	 * @param icon
	 * 
	 */
	public ProjectNewAction(String name, Icon icon) {
		super(name, icon);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		// this blocks the GUI-repainting
		if (project.getContextPath().length() > 0) {
			if (JOptionPane.showConfirmDialog(null, LocaleImpl.getInstance()
					.getString("action.project.new.discard")) != JOptionPane.OK_OPTION) {
				return;
			}
		}
		Thread t = new Thread(new Runnable() {

			public void run() {
				project.recycle();
			}
		});
		t.start();
	}

	public void langChanged() {
		putValue(SHORT_DESCRIPTION, LocaleImpl.getInstance().getString(
				"menu.file.new"));
		putValue(NAME, LocaleImpl.getInstance().getString("menu.file.new"));
	}

	public void setProject(Project project) {
		this.project = project;
	}
}
