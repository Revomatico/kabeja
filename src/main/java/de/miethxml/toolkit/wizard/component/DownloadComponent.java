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
package de.miethxml.toolkit.wizard.component;

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import de.miethxml.toolkit.wizard.WizardException;
import de.miethxml.toolkit.wizard.ui.DownloadUI;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class DownloadComponent extends AbstractWizardComponent {
    private JProgressBar progressBar;
    private JLabel step;
    private JLabel bytes;
    private JLabel time;
    private boolean cancel;
    private DownloadUI downloader = new DownloadUI();
    public String URL_LIST_KEY = "url.list";
    public String DESTINATION_DIRECTORY = "dest";
    public String DESTINATION_LIST_KEY = "file.list";

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.installer.InstallComponent#getInstallTitle()
     */
    public String getTitle() {
        return resource.getString("wizard.component.downloader.title");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.installer.InstallComponent#getInstallUIComponent()
     */
    public JComponent getInstallUIComponent() {
        return downloader.getView();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.installer.InstallComponent#startInstallProcess()
     */
    public void startWizardProcess() throws WizardException {
        List list = (List) conf.getValue(URL_LIST_KEY);
        int count = 1;
        Iterator i = list.iterator();
        List files = new ArrayList();
        String dest = (String) conf.getValue(DESTINATION_DIRECTORY);

        while (i.hasNext()) {
            String url = (String) i.next();

            try {
                downloader.setText(resource.getString(
                        "wizard.component.downloader.step") + " " + count + "/"
                    + list.size());
                downloader.download(url, dest);

                String file = dest + File.separator
                    + url.substring(url.lastIndexOf('/'));
                files.add(file);
            } catch (Exception e) {
                throw new WizardException(e);
            }
        }

        conf.setValue(DESTINATION_LIST_KEY, files);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.installer.InstallComponent#stopInstallProcess()
     */
    public void stopWizardProcess() throws WizardException {
        downloader.interrupt();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.cocoon.installer.InstallComponent#isConfigurable()
     */
    public boolean isModifiable() {
        // TODO Auto-generated method stub
        return false;
    }
}
