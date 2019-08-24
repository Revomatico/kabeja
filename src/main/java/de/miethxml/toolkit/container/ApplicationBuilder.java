/*
   Copyright 2004 Simon Mieth

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
package de.miethxml.toolkit.container;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.service.ServiceException;

import de.miethxml.hawron.gui.ApplicationFrame;
import de.miethxml.toolkit.component.AbstractServiceable;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public class ApplicationBuilder extends AbstractServiceable implements Startable {
    public final static String ROLE = ApplicationBuilder.class.getName();
    private DefaultApplicationContainer appContainer;

    /**
     *
     */
    public ApplicationBuilder() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.AbstractServiceable#initialize()
     */
    public void initialize() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.activity.Startable#start()
     */
    public void start() throws Exception {
        ApplicationFrame frame = null;

        try {
            frame = (ApplicationFrame) manager.lookup(ApplicationFrame.ROLE);

            //TODO maybe there is a better place to show the frame
            //show application
            frame.setVisible(true);
        } catch (ServiceException e2) {
            e2.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.activity.Startable#stop()
     */
    public void stop() throws Exception {
    }

    public void setApplicationContainer(
        DefaultApplicationContainer appContainer) {
        this.appContainer = appContainer;
    }

    public void shutdownApplication() {
        appContainer.disposeComponents();
    }
}
