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

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javax.swing.Icon;
import javax.swing.JLabel;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 *
 *
 *
 */
public class ImageLabel extends JLabel {
    BufferedImage img;

    /**
     *
     *
     *
     */
    public ImageLabel() {
        super();
        setSize(200, 200);
    }

    /**
     * @param text
     *
     */
    public ImageLabel(String text) {
        super(text);
    }

    /**
     * @param text
     *
     * @param horizontalAlignment
     *
     */
    public ImageLabel(
        String text,
        int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    /**
     * @param image
     *
     */
    public ImageLabel(Icon image) {
        super(image);
    }

    /**
     * @param image
     *
     * @param horizontalAlignment
     *
     */
    public ImageLabel(
        Icon image,
        int horizontalAlignment) {
        super(image, horizontalAlignment);
    }

    /**
     * @param text
     *
     * @param icon
     *
     * @param horizontalAlignment
     *
     */
    public ImageLabel(
        String text,
        Icon icon,
        int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
    }

    public void paint(Graphics g) {
        if (img != null) {
            g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), this);
        } else {
            g.drawString("No Image", 5, 100);
        }
    }

    public void setImage(String file) {
        img = null;

        try {
            img = ImageIO.read(new File(file));

            if (img != null) {
                this.setSize(img.getWidth(), img.getHeight());
                this.repaint();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
