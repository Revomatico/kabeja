/*
   Copyright 2016 Vincent Privat

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
package org.kabeja.ui.viewer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.junit.Test;

/**
 * Unit tests of {@link SVGViewer} cmass.
 */
public class SVGViewerTest {

    private CountDownLatch lock;

    /**
     * Unit test of {@link SVGViewer#load and SVGViewer#saveToJPEG} methods.
     * @throws InterruptedException if the current thread is interrupted while waiting
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testLoadAndSaveToJPEG() throws InterruptedException, IOException {
        File[] samples = new File("samples/dxf").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase(Locale.ENGLISH).endsWith(".dxf");
            }
        });
        assertTrue(samples.length > 0);

        for (File sample : samples) {
            SVGViewer svg = new SVGViewer();
            svg.initialize();

            System.out.println("Loading file " + sample.getName());
            svg.load(sample);

            lock = new CountDownLatch(1);

            svg.getSvgCanvas().addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
                @Override
                public void gvtRenderingFailed(GVTTreeRendererEvent e) {
                    fail("Rendering failed");
                }

                @Override
                public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
                    System.out.println("Rendering completed");
                    lock.countDown();
                }
            });
            System.out.println("Waiting for rendering (5s max) ...");

            lock.await(5, TimeUnit.SECONDS);

            ByteArrayOutputStream out = new ByteArrayOutputStream(500);
            svg.saveToJPEG(out);
            out.flush();
            byte[] jpg = out.toByteArray();
            assertTrue(jpg.length > 0);

            BufferedImage img = ImageIO.read(new ByteArrayInputStream(jpg));
            assertNotNull(img);
            assertTrue(img.getWidth() > 0);
            assertTrue(img.getHeight() > 0);

            System.out.println("Image size: " + img.getWidth() + "x" + img.getHeight());
        }
    }
}
