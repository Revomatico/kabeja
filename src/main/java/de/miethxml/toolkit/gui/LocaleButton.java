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

import javax.swing.Icon;
import javax.swing.JButton;

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
public class LocaleButton extends JButton implements LocaleListener {
    private String key;
    private boolean imagebutton = false;
    public String TOOLTIPKEY = ".tooltip";
    private boolean showText = false;

    /**
     *
     * @param key -
     *            The key to query a DisplayName from the LocaleImpl.
     *
     */
    public LocaleButton(String key) {
        super(LocaleImpl.getInstance().getString(key));

        this.key = key;

        LocaleImpl.getInstance().addLocaleListener(this);

        setText(LocaleImpl.getInstance().getString(key));

        if (LocaleImpl.getInstance().getString(key + TOOLTIPKEY).equals(LocaleImpl.NOLABEL)) {
            setToolTipText(LocaleImpl.getInstance().getString(key));
        } else {
            setToolTipText(LocaleImpl.getInstance().getString(key + TOOLTIPKEY));
        }
    }

    /**
     *
     * @param key -
     *            The key to query a DisplayName from the LocaleImpl.
     *
     * @param icon
     *
     * @param showText
     *            true shows the icon and text, false show the icon and set text
     *            as tooltip
     *
     */
    public LocaleButton(
        String key,
        Icon icon,
        boolean showText) {
        super("", icon);

        this.key = key;

        if (showText) {
            setText(LocaleImpl.getInstance().getString(key));

            this.showText = true;
        }

        if (LocaleImpl.getInstance().getString(key + TOOLTIPKEY).equals(LocaleImpl.NOLABEL)) {
            setToolTipText(LocaleImpl.getInstance().getString(key));
        } else {
            setToolTipText(LocaleImpl.getInstance().getString(key + TOOLTIPKEY));
        }

        LocaleImpl.getInstance().addLocaleListener(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.conf.LocaleListener#langChanged()
     *
     */
    public void langChanged() {
        setText(LocaleImpl.getInstance().getString(key));

        if (LocaleImpl.getInstance().getString(key + TOOLTIPKEY).equals(LocaleImpl.NOLABEL)) {
            setToolTipText(LocaleImpl.getInstance().getString(key));
        } else {
            setToolTipText(LocaleImpl.getInstance().getString(key + TOOLTIPKEY));
        }

        if (showText) {
            setText(LocaleImpl.getInstance().getString(key));
        }
    }

    public void enableText(boolean state) {
        this.showText = state;
    }
}
