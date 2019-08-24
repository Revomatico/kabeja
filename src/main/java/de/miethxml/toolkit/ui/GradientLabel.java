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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 */
public class GradientLabel extends JComponent {
    private String text = "";
    private int margin = 15;
    private int paddingLeft = 30;
    private int fontHeight = 28;
    private int textLength = 0;
    private int descent = 0;
    private Font font;
    private GradientPaint gradient;
    private Dimension dim;

    //	private Color startColor= Color.LIGHT_GRAY;
    //	private Color endColor = Color.WHITE;
    //	private Color textColor = Color.BLACK;
    private Color startColor = new Color(66, 66, 66);
    private Color endColor = new Color(230, 230, 230);
    private Color textColor = Color.WHITE;
    private Color blue = new Color(91, 110, 179);

    public GradientLabel(String text) {
        super();
        font = new Font("SansSerif", Font.BOLD, fontHeight);
        setText(text);
    }

    public GradientLabel() {
        this("");
    }

    public void setText(String text) {
        this.text = text;

        layoutText();
        repaint();
    }

    public Dimension getPreferredSize() {
        return dim;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int gy = height / 2;

        //		gradient = new GradientPaint(0, gy, blue,(int) (width*0.3),
        //				gy, Color.WHITE);
        gradient = new GradientPaint(0, gy, startColor, (int) (width * 0.8),
                gy, endColor);
        g2.setPaint(gradient);
        g2.fillRect(0, 0, width, height);

        g2.setFont(font);

        g2.setColor(textColor);

        g2.drawString(text, margin, ((gy + (fontHeight / 2)) - descent));

        int y = (fontHeight + (margin * 2)) - 1;

        //		g2.drawRect(0, 0, width-1, height-1);
        g2.setColor(Color.GRAY);
        g2.drawLine(0, height - 1, width - 1, height - 1);
    }

    public Color getEndColor() {
        return endColor;
    }

    public void setEndColor(Color endColor) {
        this.endColor = endColor;
    }

    public int getFontHeight() {
        return fontHeight;
    }

    public void setFontHeight(int fontHeight) {
        this.fontHeight = fontHeight;
        font = new Font("SansSerif", Font.BOLD, fontHeight);
        layoutText();
    }

    public Color getStartColor() {
        return startColor;
    }

    public void setStartColor(Color startColor) {
        this.startColor = startColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    private void layoutText() {
        FontMetrics fm = getFontMetrics(font);
        fontHeight = fm.getHeight();
        textLength = fm.stringWidth(text);
        descent = fm.getDescent();
        dim = new Dimension(paddingLeft + textLength + margin,
                fontHeight + (margin * 2));
    }

    public void setMargin(int margin) {
        this.margin = margin;
        layoutText();
    }

    public void setLeftPadding(int padding) {
        this.paddingLeft = padding;
    }
}
