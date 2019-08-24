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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;


/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 * To change this generated comment go to Window>Preferences>Java>Code
 *
 * Generation>Code and Comments
 *
 */
public class ImagePreviewer extends JLabel implements PropertyChangeListener,
    Runnable {
    String imgSrc;
    Image img;
    int height;
    int width;
    int x;
    int y;
    MediaTracker mt;
    Thread t;

    /**
     *
     *
     *
     */
    public ImagePreviewer() {
        super();

        imgSrc = "Select an image";

        height = 128;

        width = 128;

        x = 30;

        y = 30;

        setSize(new Dimension(width + (x * 2), height + (y * 2)));

        setBounds(0, 0, width + (x * 2), height + (y * 2));

        setPreferredSize(new Dimension(width + (x * 2), height + (y * 2)));

        validate();

        mt = new MediaTracker(this);
    }

    public ImagePreviewer(
        int width,
        int height) {
        super();

        imgSrc = "";

        this.height = height;

        this.width = width;

        x = 30;

        y = 30;

        setSize(new Dimension(width + (x * 2), height + (y * 2)));

        setBounds(0, 0, width + (x * 2), height + (y * 2));

        setPreferredSize(new Dimension(width + (x * 2), height + (y * 2)));

        validate();

        mt = new MediaTracker(this);
    }

    public void setValue(Object value) {
        this.imgSrc = (String) value;

        if (imgSrc.length() > 0) {
            img = Toolkit.getDefaultToolkit().getImage(imgSrc);

            mt.addImage(img, 0);

            t = new Thread(this);

            t.start();

            //t.destroy();
            t = null;
        }
    }

    public void run() {
        try {
            mt.waitForID(0);

            repaint();
        } catch (InterruptedException e) {
            System.out.println("Exception Thread:" + e.getMessage());

            return;
        }
    }

    public void paint(Graphics g) {
        //System.out.println("im painting");
        if (img != null) {
            g.drawImage(img, x, y, width, height, null);

            //String size = "w:" + img.getWidth(null) + " x h:" +
            // img.getHeight(null);
            //g.drawString(size, 0, height - 4);
        }

        g.drawString("Preview", width / 2, 15);

        g.drawRect(x - 1, y - 1, width + 1, height + 1);
    }

    /*
     *
     * (non-Javadoc)
     *
     *
     *
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     *
     */
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            File f = (File) e.getNewValue();

            if ((f != null) && f.isFile()
                    && (
                        f.getName().toLowerCase().endsWith(".jpg")
                        || f.getName().toLowerCase().endsWith(".jpeg")
                        || f.getName().toLowerCase().endsWith(".gif")
                        || f.getName().toLowerCase().endsWith(".png")
                    )) {
                setValue(f.getPath());
            }
        }
    }
}
