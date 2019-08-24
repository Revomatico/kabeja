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
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.toolkit.wizard.WizardConstants;

import org.apache.commons.compress.tar.TarEntry;
import org.apache.commons.compress.tar.TarInputStream;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class ExtractUI {
    protected ResourceBundle resource = WizardConstants.LOCALE;
    private JProgressBar progressBar;
    private JLabel step;
    private JLabel bytes;
    private JLabel time;
    private boolean cancel;
    private String rootDir;
    private TimerUI timer;

    /**
     *
     */
    public ExtractUI() {
        super();

        // TODO Auto-generated constructor stub
    }

    public JComponent getView() {
        FormLayout layout = new FormLayout("9dlu,pref,2dlu,pref:grow,9dlu,",
                "15dlu,p,3dlu,p,3dlu,p,15dlu,p,9dlu");
        PanelBuilder panelbuilder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        step = new JLabel("");
        panelbuilder.add(step, cc.xywh(2, 2, 3, 1));

        panelbuilder.add(new JLabel(resource.getString(
                    "wizard.component.unpack.count")), cc.xy(2, 4));
        bytes = new JLabel("");
        panelbuilder.add(bytes, cc.xy(4, 4));

        panelbuilder.add(new JLabel(resource.getString(
                    "wizard.component.unpack.time")), cc.xy(2, 6));
        time = new JLabel("");
        panelbuilder.add(time, cc.xy(4, 6));

        progressBar = new JProgressBar();
        panelbuilder.add(progressBar, cc.xywh(2, 8, 3, 1));
        timer = new TimerUI();

        return panelbuilder.getPanel();
    }

    public void setText(String text) {
        step.setText(text);
    }

    public void extractArchiv(
        String file,
        String dest) throws Exception {
        cancel = false;

        if (file.endsWith(".zip")) {
            extractZipArchiv(file, dest);
        } else if (file.endsWith(".tar.gz") || file.endsWith(".tgz")) {
            extractTarArchiv(file, dest);
        }
    }

    public void interrupt() {
        cancel = true;
    }

    public String getRootDir() {
        return rootDir;
    }

    private void extractZipArchiv(
        String file,
        String destDir) throws Exception {
        ZipFile ziparchiv = new ZipFile(file);

        //unpackdir=null;
        progressBar.setValue(0);
        progressBar.setMinimum(0);

        int process = 0;
        int totalCount = ziparchiv.size();
        int progress = 0;
        Enumeration e = ziparchiv.entries();
        progressBar.setMaximum(totalCount);
        timer.begin(time, totalCount);

        while (e.hasMoreElements() && !cancel) {
            ZipEntry entry = (ZipEntry) e.nextElement();

            if (entry.isDirectory()) {
                //System.out.println("UnpackName="+entry.getName());
                String name = entry.getName();

                if ((name.substring(0, name.length() - 1)).indexOf("/") < 0) {
                    System.out.println("setting root");

                    if (rootDir == null) {
                        rootDir = destDir + File.separator + entry.getName();
                    } else {
                        //more then one baseDir -> this is a package
                        //whithout rootDir :(
                        rootDir = destDir;
                    }

                    System.out.println("setting root=" + rootDir);
                }

                //check if it exists
                File dir = new File(destDir + File.separator + entry.getName());

                if (!dir.exists()) {
                    dir.mkdirs();
                }
            } else {
                //is file store to disk
                BufferedInputStream in = new BufferedInputStream(ziparchiv
                        .getInputStream(entry));
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destDir
                            + File.separator + entry.getName()));
                int current = 0;

                int length = -1;
                byte[] bytes = new byte[1024];

                while (((length = in.read(bytes, 0, bytes.length)) != -1)
                        && !cancel) {
                    out.write(bytes, 0, length);
                    current += length;
                }

                in.close();
                out.flush();
                out.close();
                in = null;
                out = null;
            }

            process++;
            timer.next();
            this.bytes.setText(process + "/" + totalCount);
            progressBar.setValue(process);
        }
    }

    private void extractTarArchiv(
        String file,
        String destDir) throws Exception {
        GZIPInputStream in = new GZIPInputStream(new FileInputStream(file));
        TarInputStream tararchiv = new TarInputStream(in);

        //init the ui elements
        progressBar.setValue(0);
        progressBar.setMinimum(0);
        progressBar.setIndeterminate(true);

        int process = 0;
        int totalCount = tararchiv.getRecordSize();
        int progress = 0;
        progressBar.setMaximum(totalCount);

        //timer.begin(time,totalCount);
        TarEntry entry = null;

        while (((entry = tararchiv.getNextEntry()) != null) && !cancel) {
            String name = entry.getName();

            if (entry.isDirectory()) {
                if ((name.substring(0, name.length() - 1)).indexOf("/") < 0) {
                    System.out.println("setting root");

                    if (rootDir == null) {
                        rootDir = destDir + File.separator + name;
                    } else {
                        //more then one baseDir -> this is a package
                        //whithout rootDir :(
                        rootDir = destDir;
                    }
                }

                //check if it exists
                File dir = new File(destDir + File.separator + entry.getName());

                if (!dir.exists()) {
                    dir.mkdirs();
                }
            } else {
                //is file store to disk
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destDir
                            + File.separator + entry.getName()));
                int current = 0;

                int length = -1;
                byte[] bytes = new byte[1024];

                while ((
                            (length = tararchiv.read(bytes, 0, bytes.length)) != -1
                        ) && !cancel) {
                    out.write(bytes, 0, length);
                    current += length;
                }

                out.flush();
                out.close();
                out = null;
            }

            process++;

            //timer.next();
            this.bytes.setText("" + process);
            progressBar.setValue(process);
        }

        progressBar.setIndeterminate(false);
    }
}
