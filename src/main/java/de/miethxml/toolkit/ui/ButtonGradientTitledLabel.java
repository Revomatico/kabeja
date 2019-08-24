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
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.SwingConstants;


/**
 * @author simon
 *
 *
 *
 */
public class ButtonGradientTitledLabel extends JComponent {
    private ArrayList rightSide = new ArrayList();
    private ArrayList leftSide = new ArrayList();
    private String text;
    private int margin = 3;
    private int iconSize = 16;
    private int padding = 3;
    private GradientPaint gradient;
    private Color startColor = new Color(66, 66, 66);
    private Color endColor = new Color(230, 230, 230);
    private Color textColor = Color.WHITE;

    public ButtonGradientTitledLabel(String title) {
        super();
        this.text = title;
    }

    public ButtonGradientTitledLabel() {
        this("");
    }

    /**
     * Adds a component on the default side (RIGHT)
     * @param component to add
     */
    public void addComponent(JComponent comp) {
        addComponent(comp, SwingConstants.RIGHT);
    }

    /**
     * Adds the component to left or right side (SwingConstans.LEFT/RIGHT)
     * @param component to add
     * @param orientation
     */
    public void addComponent(
        JComponent comp,
        int orientation) {
        comp.setBorder(BorderFactory.createEmptyBorder());
        comp.setOpaque(false);
        comp.setFocusable(false);

        switch (orientation) {
        case SwingConstants.RIGHT:
            rightSide.add(comp);

            break;

        case SwingConstants.LEFT:
            leftSide.add(comp);

            break;

        default:
            rightSide.add(comp);
        }

        add(comp);
    }

    public Dimension getPreferredSize() {
        int h = 0;
        int fontHeight = getFontMetrics(getFont()).getHeight();

        //the height
        if (fontHeight > iconSize) {
            h = (2 * margin) + fontHeight;
        } else {
            h = (2 * margin) + iconSize;
        }

        //the width
        int w = ((leftSide.size() + rightSide.size()) * padding)
            + (2 * padding) + getFontMetrics(getFont()).stringWidth(text);

        Dimension dim = new Dimension(w, h);

        return dim;
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        //		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        //				RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        int gy = height / 2;
        gradient = new GradientPaint((float) (width * 0.45), gy, startColor,
                (float) (width), gy, endColor);

        g2.setPaint(gradient);
        g2.fillRect(0, 0, width, height);

        g2.setColor(Color.WHITE);
        g2.drawLine(0, 0, width, 0);
        g2.drawLine(0, 0, 0, height - 1);
        g2.setColor(Color.gray);
        g2.drawLine(0, height - 1, width - 1, height - 1);
        g2.setFont(getFont());
        g2.setColor(textColor);

        int x = padding;
        int y = (height / 2) - (iconSize / 2);

        //set the bounds for the left  buttons
        Iterator i = leftSide.iterator();

        while (i.hasNext()) {
            JComponent comp = (JComponent) i.next();
            comp.setBounds(x, y, iconSize, iconSize);

            x += (iconSize + padding);
        }

        //paint the text 
        FontMetrics fm = g2.getFontMetrics();
        y = ((height / 2) + (fm.getHeight() / 2)) - fm.getDescent();
        g2.drawString(text, x, y);

        //set the bounds for the right buttons 
        //we start from the right side
        x = width - padding - iconSize;
        y = (height / 2) - (iconSize / 2);
        i = rightSide.iterator();

        while (i.hasNext()) {
            JComponent comp = (JComponent) i.next();
            comp.setBounds(x, y, iconSize, iconSize);

            x = x - (iconSize + padding);
        }
    }

    public int getIconSize() {
        return iconSize;
    }

    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }
}
