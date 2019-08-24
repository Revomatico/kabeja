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
package de.miethxml.toolkit.wizard.ui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.net.URL;
import java.net.URLConnection;

import java.text.NumberFormat;

import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.toolkit.wizard.WizardConstants;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 */
public class DownloadUI {
    protected ResourceBundle resource = WizardConstants.LOCALE;
    private JProgressBar progressBar;
    private JLabel step;
    private JLabel transfered;
    private JLabel time;
    private JLabel rate;
    private boolean cancel;
    private TimerUI timer;
    private NumberFormat sizeformat = NumberFormat.getInstance();

    /**
     *
     */
    public DownloadUI() {
        super();
    }

    public JComponent getView() {
        FormLayout layout = new FormLayout("9dlu,pref,2dlu,fill:pref:grow,9dlu,",
                "15dlu,p,3dlu,p,3dlu,p,3dlu,p,15dlu,p,9dlu");
        PanelBuilder panelbuilder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        step = new JLabel("");
        panelbuilder.add(step, cc.xywh(2, 2, 3, 1));

        panelbuilder.addLabel(resource.getString(
                "wizard.component.downloader.time"), cc.xy(2, 4));
        time = new JLabel("");
        panelbuilder.add(time, cc.xy(4, 4));

        panelbuilder.addLabel(resource.getString(
                "wizard.component.downloader.size"), cc.xy(2, 6));
        transfered = new JLabel("");
        panelbuilder.add(transfered, cc.xy(4, 6));

        panelbuilder.addLabel(resource.getString(
                "wizard.component.downloader.speed"), cc.xy(2, 8));
        rate = new JLabel("");
        panelbuilder.add(rate, cc.xy(4, 8));

        progressBar = new JProgressBar();
        panelbuilder.add(progressBar, cc.xywh(2, 10, 3, 1));

        timer = new TimerUI();

        return panelbuilder.getPanel();
    }

    public void setText(String text) {
        step.setText(text);
    }

    public void download(
        String url,
        String dest) throws Exception {
        boolean complete = true;
        String filename = null;
        int filelength;
        cancel = false;

        //firt download the file
        URL source = new URL(url);
        filename = source.getFile();

        if (filename.lastIndexOf("/") > -1) {
            filename = filename.substring(filename.lastIndexOf("/") + 1);
        }

        URLConnection connection = source.openConnection();

        //some init values
        filelength = connection.getContentLength();

        String contentLength = sizeformat.format(filelength / 1024) + "KB";
        timer.begin(time, filelength);

        long startTime = System.currentTimeMillis();

        //set the gui
        step.setText(step.getText() + " " + filename);
        progressBar.setMinimum(0);
        progressBar.setMaximum(filelength);

        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try {
            in = new BufferedInputStream(connection.getInputStream());
            out = new BufferedOutputStream(new FileOutputStream(dest
                        + File.separator + filename));

            int current = 0;
            int process = 0;
            int length = -1;
            byte[] bytes = new byte[1024];

            while (((length = in.read(bytes, 0, bytes.length)) != -1)
                    && !cancel) {
                out.write(bytes, 0, length);
                current += length;

                //the ui reporting
                progressBar.setValue(current);

                //transfered bytes
                transfered.setText(sizeformat.format(current / 1024) + "KB of "
                    + contentLength);

                //the left time
                timer.setValue(current);

                //rate
                double speed = (
                        current / (System.currentTimeMillis() - startTime + 1)
                    ) * 0.9765625;
                rate.setText(sizeformat.format(speed) + " KB/s");
            }

            in.close();
            out.flush();
            out.close();
            timer.end();

            //set all null
            in = null;
            out = null;
            complete = true;
        } catch (Exception e) {
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (Exception e) {
                }
            }

            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (Exception e) {
                }
            }
        }

        return;
    }

    public void interrupt() {
        cancel = true;
        timer.end();
    }
}
