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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 */
public class SmallTriangleBorder implements Border {
    private Insets i = new Insets(0, 0, 7, 0);
    private int[] px = new int[3];
    private int[] py = new int[3];

    /**
     *
     */
    public SmallTriangleBorder() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.border.Border#isBorderOpaque()
     */
    public boolean isBorderOpaque() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.border.Border#paintBorder(java.awt.Component,
     *      java.awt.Graphics, int, int, int, int)
     */
    public void paintBorder(
        Component c,
        Graphics g,
        int x,
        int y,
        int width,
        int height) {
        px[0] = x;
        py[0] = (y + height) - 5;

        px[1] = x + 3;
        py[1] = y + height;

        px[2] = x + 6;
        py[2] = py[0];
        g.setColor(Color.BLACK);
        g.fillPolygon(px, py, 3);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
     */
    public Insets getBorderInsets(Component c) {
        return i;
    }
}
