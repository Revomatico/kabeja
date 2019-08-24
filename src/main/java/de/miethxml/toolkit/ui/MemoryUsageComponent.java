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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;


public class MemoryUsageComponent extends JComponent {
    private long totalSize;
    private long currentSize;
    private double warningSize = 1.0;
    private int margin = 1;
    private AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
            0.40f);
    private StringBuffer text = new StringBuffer();

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int w = getWidth() - (2 * margin) - 2;

        int h = getHeight() - (2 * margin) - 1;
        g.setColor(getBackground());
        g.fillRect(0,0,getWidth(),getHeight());
        double p = (double) currentSize / (double) totalSize;

        int x = (int) (p * w);

        //draw the border
        //draw String
        text.delete(0, text.length());
        text.append((int) (currentSize / 1048576));
        text.append('/');
        text.append((int) (totalSize / 1048576));
        text.append("MB");

        FontMetrics fm = g2.getFontMetrics();
        String t = text.toString();
        
        int l = fm.stringWidth(t);
        g2.setColor(Color.BLACK);
        g2.drawString(t, (int) ((getWidth() - l) / 2),
            (int) (((getHeight() + fm.getHeight()) / 2) - fm.getDescent()));

        g2.setComposite(ac);

        if (p >= warningSize) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.GRAY);
        }

        g2.fillRect(margin, margin, x, h);

        g2.setColor(Color.BLACK);
        g2.drawRect(margin, margin, w, h);
    }

    public void setCurrentSize(long size) {
        this.currentSize = size;
        setToolTipText("Memory:" + ((size * 100) / totalSize) + "%");
        repaint();
    }

    public void setTotalSize(long size) {
        this.totalSize = size;
    }
}
