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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public class TitledLabel extends JComponent {
    private final static int FONT_SIZE = 38;
    private String text = "";
    private Font font;
    private Color background;
    private Color foreground;
    private Dimension dim;
    private int padding = 20;
    private int fontHeight = 38;
    private int textLength = 0;
    private int margin = 4;

    /**
     *
     */
    public TitledLabel() {
        this("");
    }

    /**
     * @param text
     */
    public TitledLabel(String text) {
        this(text, FONT_SIZE);
    }

    public TitledLabel(
        String text,
        int fontSize) {
        super();
        this.text = text;
        fontHeight = fontSize;
        init();
        generateLayout();
    }

    public void init() {
        font = new Font("SansSerif", Font.BOLD, fontHeight);
        background = new Color(10, 10, 70);
        foreground = new Color(250, 250, 250);
    }

    private void generateLayout() {
        FontMetrics fm = getFontMetrics(font);
        fontHeight = fm.getHeight();
        textLength = fm.stringWidth(text);
        dim = new Dimension((padding * 2) + textLength + (margin * 2),
                (padding * 2) + fontHeight + (margin * 2));
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.Component#getPreferredSize()
     */
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

        g2.setColor(background);
        g2.fillRoundRect(margin, margin, width - 1 - margin,
            height - 1 - margin, 7, 7);
        g2.setColor(foreground);
        g2.setFont(font);
        g2.drawString(text, margin + padding, margin + padding + fontHeight);
    }
}
