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

import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import de.miethxml.toolkit.wizard.WizardException;
import de.miethxml.toolkit.wizard.ui.ExtractUI;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 */
public class UnpackComponent extends AbstractWizardComponent {
    public static String FILES_LIST_KEY = "file.list";
    public static String DESTINATION_KEY = "unpack.destination";
    private ExtractUI unpacker = new ExtractUI();

    public String getTitle() {
        return resource.getString("wizard.component.unpack.title");
    }

    public JComponent getInstallUIComponent() {
        return unpacker.getView();
    }

    public void startWizardProcess() throws WizardException {
        List list = (List) conf.getValue(FILES_LIST_KEY);
        Iterator i = list.iterator();

        while (i.hasNext()) {
            File source = new File((String) i.next());
            String dest = null;

            if (conf.hasValue(DESTINATION_KEY)) {
                dest = (String) conf.getValue(DESTINATION_KEY);

                File d = new File(dest);

                if (!d.exists()) {
                    d.mkdirs();
                }

                System.out.println("DESTINATION:" + d.getAbsolutePath());
            } else {
                dest = source.getParentFile().getAbsolutePath();
            }

            try {
                unpacker.extractArchiv(source.getAbsolutePath(), dest);
            } catch (Exception e) {
                throw new WizardException(e);
            }
        }
    }

    public void stopWizardProcess() {
        unpacker.interrupt();
    }

    public boolean isModifiable() {
        return false;
    }
}
