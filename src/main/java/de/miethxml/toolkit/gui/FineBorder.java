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

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;

import javax.swing.border.Border;


public class FineBorder implements Border {
    private Insets i;

    //GradientColors
    private Color startColor;
    private Color endColor;
    final int steps = 55;
    private Color[] gradient;
    private String label;
    private String icon;
    private Image iconImg;
    private int iconHeight = 16;
    private int iconWidth = 16;
    private double plainsize = 0.4;
    private boolean withicon;
    private Color textColor = Color.WHITE;
    private Color darkGray = new Color(100, 100, 100);
    private Color lightGray = new Color(160, 160, 160);

    public FineBorder(String title) {
        i = new Insets(25, 1, 3, 3);

        startColor = new Color(91, 110, 179);

        endColor = new Color(212, 212, 185);

        gradient = new Color[steps];

        buildGradient();

        this.label = title;

        withicon = false;
    }

    public FineBorder(
        String title,
        String icon,
        Component c) {
        this(title);

        this.icon = icon;

        withicon = true;

        MediaTracker mt = new MediaTracker(c);

        iconImg = Toolkit.getDefaultToolkit().getImage(icon);

        mt.addImage(iconImg, 0);

        try {
            mt.waitForID(0);

            c.repaint();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Insets getBorderInsets(Component c) {
        return i;
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(
        Component c,
        Graphics g,
        int x,
        int y,
        int width,
        int height) {
        FontMetrics fm = g.getFontMetrics();

        g.setColor(startColor);

        int size = (int) (plainsize * (width - 2));
        g.fillRect(x + 1, y, size, y + i.top);
        paintGradient(g, x + 1 + size, y, (width - 2) - size, i.top - 1);

        //the right border
        g.setColor(darkGray);
        g.drawLine((x + width) - 3, y, (x + width) - 3, y + height);
        g.setColor(lightGray);
        g.drawLine(((x + width) - 2), y + 1, ((x + width) - 2), (y + height)
            - 1);
        g.drawLine(((x + width) - 1), y + 2, ((x + width) - 1), (y + height)
            - 2);

        //the bottom border
        g.setColor(darkGray);
        g.drawLine(x, (y + height) - 3, (x + width) - 3, ((y + height) - 3));
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
        g.drawLine(x, y, (x + width) - 5, y);
        g.drawLine(x, (y + i.top) - 1, (x + width) - i.right, (y + i.top) - 1);

        //white edge on gradient
        g.setColor(Color.white);
        g.drawLine(x + 1, y + 1, (x + width) - i.right - 1, y + 1);
        g.drawLine(x + 1, y + 1, x + 1, (y + i.top) - 1);

        //imageicon
        if (withicon) {
            g.drawImage(iconImg, x + 5, y + ((i.top - 1 - iconHeight) / 2),
                iconWidth, iconHeight, null);
            g.setColor(textColor);
            g.drawString(label, x + 10 + iconWidth,
                y + (i.top / 2) + (fm.getHeight() / 2));
        } else {
            g.setColor(textColor);
            g.drawString(label, x + 10,
                (y + (i.top / 2) + (fm.getHeight() / 2)) - fm.getDescent());
        }
    }

    private void paintGradient(
        Graphics g,
        int x,
        int y,
        int width,
        int height) {
        double diffx = width / steps;

        int i = 0;

        int length = (int) diffx;

        while ((((i + 1) * length) < width) && (i < steps)) {
            g.setColor(gradient[i]);

            g.fillRect(x + (i * length), y, length, height);

            i++;
        }

        //last snippet
        if (i < steps) {
            g.setColor(gradient[i]);
        } else {
            g.setColor(endColor);
        }

        g.fillRect(x + (i * length), y, width - (i * length), height);
    }

    private void buildGradient() {
        double diffred = endColor.getRed() - startColor.getRed();

        double diffgreen = endColor.getGreen() - startColor.getGreen();

        double diffblue = endColor.getBlue() - startColor.getBlue();

        gradient[0] = startColor;

        gradient[steps - 1] = endColor;

        for (int i = 1; i < (steps - 1); i++) {
            gradient[i] = new Color(startColor.getRed()
                    + (int) ((diffred / (steps - 2)) * i),
                    startColor.getGreen()
                    + (int) ((diffgreen / (steps - 2)) * i),
                    startColor.getBlue() + (int) (
                        (diffblue / (steps - 2)) * i
                    ));
        }
    }

    /**
     *
     * @return Returns the endColor.
     *
     */
    public Color getEndColor() {
        return endColor;
    }

    /**
     * The endColor of the gradient (right side)
     *
     * @param endColor
     *
     *
     */
    public void setEndColor(Color endColor) {
        this.endColor = endColor;
        buildGradient();
    }

    /**
     *
     * @return Returns the startColor.
     *
     */
    public Color getStartColor() {
        return startColor;
    }

    /**
     * The startColor of the gradient (left side).
     *
     * @param startColor
     *
     *
     */
    public void setStartColor(Color startColor) {
        this.startColor = startColor;
        buildGradient();
    }

    public void setTitle(String title) {
        this.label = title;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }
}
