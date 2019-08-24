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
public class SmallShadowBorder implements Border {
    private Insets i = new Insets(1, 1, 3, 3);
    private Color darkGray = new Color(100, 100, 100);
    private Color lightGray = new Color(160, 160, 160);

    /**
     *
     */
    public SmallShadowBorder() {
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
        //the right border
        g.setColor(darkGray);
        g.drawLine((x + width) - 3, y - 1, (x + width) - 3, y + height);
        g.setColor(lightGray);
        g.drawLine(((x + width) - 2), y + 1, ((x + width) - 2), (y + height)
            - 1);
        g.drawLine(((x + width) - 1), y + 2, ((x + width) - 1), (y + height)
            - 2);

        //the bottom border
        g.setColor(darkGray);
        g.drawLine(x, (y + height) - 3, (x + width) - 2, (y + height) - 3);
        g.setColor(lightGray);
        g.drawLine(x + 1, ((y + height) - 2), (x + width) - 2,
            ((y + height) - 2));
        g.drawLine(x + 2, ((y + height) - 1), (x + width) - 3,
            ((y + height) - 1));

        //the left border
        g.setColor(Color.gray);
        g.drawLine(x, y, x, (y + height) - 3);

        //the top border
        g.setColor(Color.gray);
        g.drawLine(x, y, (x + width) - 2, y);

        //		g.setColor(lightGray);
        //		g.drawLine(x+1, y +1 , x + width-2, y +1);
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
