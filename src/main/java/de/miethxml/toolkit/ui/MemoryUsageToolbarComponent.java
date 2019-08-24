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
package de.miethxml.toolkit.ui;

import java.awt.Dimension;
import java.awt.Font;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;

import de.miethxml.toolkit.component.AbstractServiceable;


/**
 * @author simon
 *
 *
 *
 */
public class MemoryUsageToolbarComponent extends AbstractServiceable
    implements Startable, Initializable {
    private boolean interrupted = false;
    private MemoryUsageComponent view;

    public void initialize() throws Exception {
        view = new MemoryUsageComponent();
        view.setFont(new Font("Serif", Font.PLAIN, 9));
        view.setPreferredSize(new Dimension(60, 24));
        view.setMaximumSize(new Dimension(60, 24));

        
        //add to Toolbar
        ToolBarManager toolbar = (ToolBarManager) manager.lookup(ToolBarManager.ROLE);

        toolbar.addComponent(view, ToolBarManager.BEFORE_LAST);
    }

    public void start() throws Exception {
        Thread t = new Thread(new Runnable() {
                    public void run() {
                        while (!interrupted) {
                            long total = Runtime.getRuntime().maxMemory();

                            if (total == Long.MAX_VALUE) {
                                total = Runtime.getRuntime().totalMemory();
                            }

                            view.setTotalSize(total);
                            view.setCurrentSize(total
                                - Runtime.getRuntime().freeMemory());

                            try {
                                Thread.sleep(2000);
                            } catch (Exception e) {
                            }
                        }
                    }
                });
        t.start();
    }

    public void stop() throws Exception {
        interrupted = true;
    }
}
