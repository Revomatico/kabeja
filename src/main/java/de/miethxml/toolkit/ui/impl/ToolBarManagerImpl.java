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
package de.miethxml.toolkit.ui.impl;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import com.jgoodies.plaf.HeaderStyle;
import com.jgoodies.plaf.Options;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;
import de.miethxml.toolkit.ui.ToolBarManager;

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
public class ToolBarManagerImpl implements Serviceable, Initializable,
    ToolBarManager,LocaleListener{
    private ServiceManager manager;
    private JToolBar toolbar = new JToolBar();
    private boolean glue = false;
    private ArrayList buttons=new ArrayList();
    

    /**
     *
     *
     *
     */
    public ToolBarManagerImpl() {
        super();
    }

    public JComponent getToolBar() {
        return toolbar;
    }

    public void addAction(Action action) {
        addAction(action, LAST);
    }

    public void addAction(
        Action action,
        int index) {
        JButton button = new JButton(action);
        button.setText("");
        button.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        button.setOpaque(false);
        addComponent(button, index);
        buttons.add(button);
    }

    public void addComponent(Component comp) {
        toolbar.add(comp);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#init()
     *
     */
    public void initialize() {
        toolbar.setRollover(true);
        toolbar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
        LocaleImpl.getInstance().addLocaleListener(this);
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

    public void addComponent(
        Component comp,
        int index) {
        if ((index == BEFORE_LAST) && (toolbar.getComponentCount() > 0)) {
            if (glue) {
                toolbar.add(comp, toolbar.getComponentCount() - 2);
            } else {
                toolbar.add(comp, toolbar.getComponentCount() - 1);
            }
        } else if (index == LAST) {
            if (!glue) {
                toolbar.add(Box.createHorizontalGlue());
                glue = true;
            }

            toolbar.add(comp, -1);
        } else {
            toolbar.add(comp);
        }
    }
	/* (non-Javadoc)
	 * @see de.miethxml.toolkit.conf.LocaleListener#langChanged()
	 */
	public void langChanged() {
		//update the buttons remove the text
		Iterator i = buttons.iterator();
		while(i.hasNext()){
			((JButton)i.next()).setText("");
		}

	}
}
