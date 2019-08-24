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
package de.miethxml.toolkit.gui;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;


/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 *
 *
 *
 */
public class LocaleBorderPanel extends BorderPanel implements LocaleListener {
    private String key;

    public LocaleBorderPanel(String key) {
        super(LocaleImpl.getInstance().getString(key));

        this.key = key;

        LocaleImpl.getInstance().addLocaleListener(this);
    }

    public LocaleBorderPanel(
        String key,
        String icon) {
        super(LocaleImpl.getInstance().getString(key), icon);

        this.key = key;

        LocaleImpl.getInstance().addLocaleListener(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.conf.LocaleListener#langChanged()
     *
     */
    public void langChanged() {
        border.setTitle(LocaleImpl.getInstance().getString(key));
    }
}
