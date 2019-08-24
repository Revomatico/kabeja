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

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.plaf.basic.BasicTabbedPaneUI;


/**
 * @author simon
 *
 *
 *
 */
public class ExtendedTabbedPaneUI extends BasicTabbedPaneUI {
    private int margin = 3;

    protected int calculateTabWidth(
        int arg0,
        int arg1,
        FontMetrics arg2) {
        return super.calculateTabWidth(arg0, arg1, arg2) + (margin * 2);
    }

    protected void paintTab(
        Graphics g,
        int arg1,
        Rectangle[] arg2,
        int arg3,
        Rectangle arg4,
        Rectangle arg5) {
        super.paintTab(g, arg1, arg2, arg3, arg4, arg5);

        Rectangle bounds = arg2[arg3];
        g.setColor(Color.DARK_GRAY);
        g.drawRoundRect(bounds.x, bounds.y, bounds.width - 1,
            bounds.height - 1, 3, 3);
    }

    protected void paintTabBackground(
        Graphics g,
        int arg1,
        int arg2,
        int x,
        int y,
        int w,
        int h,
        boolean selected) {
        if (selected) {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x, y, w, h);
        }
    }
}
