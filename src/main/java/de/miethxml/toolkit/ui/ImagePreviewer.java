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
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import javax.imageio.ImageIO;

import javax.swing.JComponent;


/**
 * @author simon
 *
 *
 *
 */
public class ImagePreviewer extends JComponent {
    private BufferedImage img;
    private int margin = 5;
    private Font font = new Font("SansSerif", Font.PLAIN, 9);
    private int fontHeight;
    private Dimension dim;
    private double ih;
    private double iw;
    private double ratio;
    private double x;
    private double y;
    private double w;
    private double h;
    private double text_x;
    private double text_y;
    private String text;

    public void paintComponent(Graphics g) {
        if (img != null) {
            // g.setColor(Color.WHITE);
            // g.fillRect(0,0,(int)dim.getWidth(),(int)dim.getHeight());
            g.drawImage(img, (int) x, (int) y, (int) w, (int) h, this);
            g.setColor(Color.BLACK);
            g.drawRect((int) x - 1, (int) y - 1, (int) w + 1, (int) h + 1);

            g.setFont(font);
            g.drawString(text, (int) text_x, (int) text_y);
        }
    }

    public void setImage(String uri) {
        img = null;

        if ((uri != null) && (uri.length() > 0)) {
            try {
                if (uri.startsWith("http:") || uri.startsWith("ftp:")) {
                    img = ImageIO.read(new URL(uri));
                } else {
                    //use own cache here or is ImageIO-cache ok?
                    img = ImageIO.read(new File(uri));
                }

                if (img != null) {
                    scaleImage();
                    this.repaint();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Dimension getPreferredSize() {
        return dim;
    }

    private void scaleImage() {
        //calculate
        h = img.getHeight();
        w = img.getWidth();

        //check the ratio
        double r = w / h;

        if (r > ratio) {
            //scale over the width
            double f = iw / w;
            h = h * f;
            w = iw;
            y = margin + ((ih - h) / 2);
            x = margin;
        } else {
            //scale over the height
            double f = ih / h;
            w = w * f;
            x = (dim.getWidth() / 2) - (w / 2);
            h = ih;
            y = margin;
        }

        //the text
        text = img.getWidth() + "x" + img.getHeight();

        FontMetrics fm = getFontMetrics(font);
        fontHeight = fm.getHeight();

        int length = fm.stringWidth(text);
        text_x = (dim.getWidth() - length) / 2;
        text_y = dim.getHeight() - 2;
    }

    public void setFixedSize(
        int arg0,
        int arg1) {
        this.dim = new Dimension(arg0, arg1);

        super.setSize(arg0, arg1);

        //calculate
        FontMetrics fm = getFontMetrics(font);
        fontHeight = fm.getHeight();
        ih = dim.getHeight() - margin - 4 - fontHeight;
        iw = dim.getWidth() - (2 * margin);
        ratio = iw / ih;

        if (img != null) {
            scaleImage();
        }
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }
}
