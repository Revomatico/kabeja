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
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.CubicCurve2D;

import javax.swing.border.Border;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 */
public class SplineBorder implements Border {
    private Insets i = new Insets(0, 40, 0, 0);

    /**
     *
     */
    public SplineBorder() {
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
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        CubicCurve2D.Float spline = new CubicCurve2D.Float(x, y + height,
                x + (i.left / 2) + 2, y + height, x + (i.left / 2), y,
                x + i.left, y - 1);
        g2.setColor(Color.WHITE);
        g2.draw(spline);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
     */
    public Insets getBorderInsets(Component c) {
        // TODO Auto-generated method stub
        return i;
    }
}
