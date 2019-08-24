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
package de.miethxml.hawron.gui.process;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.miethxml.hawron.gui.project.TabbedProjectView;
import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.ProjectListener;
import de.miethxml.hawron.project.ProjectProcessEvent;
import de.miethxml.hawron.project.Task;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;
import de.miethxml.toolkit.gui.LocaleButton;
import de.miethxml.toolkit.gui.LocaleLabel;
import de.miethxml.toolkit.ui.TopLineBorder;

import org.apache.avalon.framework.service.ServiceManager;


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
public class ProjectProcessMonitorPanel implements ProjectListener,
    ActionListener, LocaleListener, StatusPanel {
    private ServiceManager manager;
    private Hashtable allbuttons;
    private Hashtable labels;
    private JLabel taskMsg;
    private JLabel stepMsg;
    private JLabel totalTimeCounter;
    private JLabel currentTimeCounter;
    private JProgressBar progress;
    private StringBuffer log;
    private int errorCount;
    private Project project;
    private TimeCounter processCounter;
    private TimeCounter stepCounter;
    private JDialog logView;
    private JTextArea logtext;
    private NumberFormat sizeformat;
    private int count;
    private int maxCount;
    private int fileCount;
    private int file;
    private long totalTaskTransferedBytes;
    private Task processTask;
    private JPanel panel;
    private JPanel mainview;

    /**
     * @param title
     *
     */
    public ProjectProcessMonitorPanel() {
        allbuttons = new Hashtable();
        labels = new Hashtable();
        log = new StringBuffer();
        errorCount = 0;
        stepCounter = new TimeCounter();
        processCounter = new TimeCounter();
        sizeformat = NumberFormat.getInstance();
    }

    public void initialize() {
        mainview = new JPanel();

        //TODO remove this mainpanel
        FormLayout mainlayout = new FormLayout("0dlu,fill:pref:grow,0dlu",
                "3dlu,p,0dlu");
        mainview.setLayout(mainlayout);

        CellConstraints ccm = new CellConstraints();

        panel = new JPanel();

        panel.setBorder(new TopLineBorder(Color.GRAY));

        FormLayout layout = new FormLayout("15dlu,right:pref,3dlu,fill:pref:grow,15dlu",
                "3dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,9dlu");
        panel.setLayout(layout);

        CellConstraints cc = new CellConstraints();
        LocaleLabel label = new LocaleLabel("view.status.label.do");

        panel.add(label, cc.xy(2, 2));
        taskMsg = new JLabel("");
        panel.add(taskMsg, cc.xy(4, 2));
        label = new LocaleLabel("view.status.label.step");

        panel.add(label, cc.xy(2, 4));
        stepMsg = new JLabel("");
        panel.add(stepMsg, cc.xy(4, 4));
        label = new LocaleLabel("view.status.label.time");

        //labels.put("view.status.label.time", label);
        panel.add(label, cc.xy(2, 6));
        totalTimeCounter = new JLabel("");
        panel.add(totalTimeCounter, cc.xy(4, 6));
        label = new LocaleLabel("view.status.label.current");

        panel.add(label, cc.xy(2, 8));
        currentTimeCounter = new JLabel("");
        panel.add(currentTimeCounter, cc.xy(4, 8));
        label = new LocaleLabel("view.status.label.all");

        panel.add(label, cc.xy(2, 10));
        progress = new JProgressBar();
        panel.add(progress, cc.xy(4, 10));

        LocaleButton[] buttons = new LocaleButton[3];
        buttons[0] = new LocaleButton("common.button.cancel");
        allbuttons.put("common.button.cancel", buttons[0]);
        buttons[0].setEnabled(false);
        buttons[0].setActionCommand("cancel");
        buttons[0].addActionListener(this);
        buttons[1] = new LocaleButton("view.status.button.viewlog");
        allbuttons.put("view.status.button.viewlog", buttons[1]);
        buttons[1].setEnabled(false);
        buttons[1].setActionCommand("show.log");
        buttons[1].addActionListener(this);
        buttons[2] = new LocaleButton("view.status.button.hidde");
        allbuttons.put("view.status.button.hidde", buttons[2]);
        buttons[2].setEnabled(false);
        buttons[2].setActionCommand("hidde");
        buttons[2].addActionListener(this);

        ButtonBarBuilder bbuilder = new ButtonBarBuilder();

        //bbuilder.addRelatedGap();
        //bbuilder.addGlue();
        bbuilder.addGriddedButtons(buttons);
        panel.add(bbuilder.getPanel(), cc.xy(4, 12));
        mainview.add(panel, ccm.xy(2, 2));
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.conf.ProcessListener#error(java.lang.String,
     *
     * java.lang.String)
     *
     */
    public void error(
        String uri,
        String message) {
        log.append("[Error] " + uri + " " + message + "\n");
        errorCount++;
        stepCounter.end();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.conf.ProcessListener#skipped(java.lang.String,
     *
     * java.lang.String)
     *
     */
    public void skipped(
        String uri,
        String message) {
        log.append("[Skipped] " + uri + " " + message + "\n");
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.conf.ProcessListener#taskProcessing(de.miethxml.conf.ProjectProcessEvent)
     *
     */
    public void processed(ProjectProcessEvent ppe) {
        stepCounter.end();
        stepCounter.begin(currentTimeCounter);
        stepMsg.setText(ppe.getPage() + "/" + ppe.getLinks());
        progress.setMinimum(0);
        progress.setMaximum(ppe.getLinks());
        progress.setValue(ppe.getPage());
        log.append("[Generated] " + ppe.getUri() + "\n");
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.conf.ProcessListener#warn(java.lang.String,
     *
     * java.lang.String)
     *
     */
    public void warn(
        String uri,
        String message) {
        log.append("[Warn] " + uri + " " + message);
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.PublishListener#endPublishing()
     *
     */
    public void endPublishing() {
        stepCounter.end();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.PublishListener#errorMessage(java.lang.String)
     *
     */
    public void errorMessage(String msg) {
        stepMsg.setText(msg);
        stepCounter.end();
    }

    public void startPublishing(int fileCount) {
        stepCounter.end();
        stepCounter.begin(currentTimeCounter);

        //max = progress.getMaximum() + fileCount;
        //count = progress.getValue();
        progress.setMaximum(fileCount);
        progress.setMinimum(0);
        progress.setValue(0);
        this.fileCount = fileCount;
        this.file = 1;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("hidde")) {
            this.setVisible(false);
        } else if (e.getActionCommand().equals("show.log")) {
            initLogView();
            logView.setVisible(true);
        } else if (e.getActionCommand().equals("cancel")) {
            this.project.cancelTaskProcessing();
            taskMsg.setText(LocaleImpl.getInstance().getString("view.status.msg.do.taskcancel"));
            ((JButton) allbuttons.get("common.button.cancel")).setEnabled(false);
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.conf.TranslationListener#langChanged()
     *
     */
    public void langChanged() {
        LocaleImpl lang = LocaleImpl.getInstance();
    }

    public void setProject(Project project) {
        //unregister on old project
        if (this.project != null) {
            this.project.removeProjectListener(this);
        }

        this.project = project;

        if (project != null) {
            this.project.addProjectListener(this);
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.conf.ProjectListener#taskEnd(java.lang.String)
     *
     */
    public void taskEnd() {
        processCounter.end();
        stepCounter.end();
        stepMsg.setText("");

        String msg = LocaleImpl.getInstance().getString("view.status.msg.do.taskend")
            + ": " + processTask.getTitle();

        if (errorCount > 0) {
            msg = msg + ", "
                + LocaleImpl.getInstance().getString("view.status.msg.do.taskerror")
                + ": " + errorCount;
        }

        taskMsg.setText(msg);
        currentTimeCounter.setText("");
        ((JButton) allbuttons.get("view.status.button.viewlog")).setEnabled(true);
        ((JButton) allbuttons.get("view.status.button.hidde")).setEnabled(true);
        ((JButton) allbuttons.get("common.button.cancel")).setEnabled(false);

        progress.setValue(0);
        log.append("[End] end processing task:" + processTask.getTitle() + "\n");

        if (totalTaskTransferedBytes > 0) {
            log.append("[End] total bytes transfered:"
                + sizeformat.format(totalTaskTransferedBytes) + "\n");
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.conf.ProjectListener#taskStart(java.lang.String)
     *
     */
    public void taskStart(Task task) {
        this.processTask = task;
        errorCount = 0;
        totalTaskTransferedBytes = 0;
        log.append("[Begin] processing now Task:" + task.getTitle() + "\n");
        ((JButton) allbuttons.get("view.status.button.viewlog")).setEnabled(false);
        ((JButton) allbuttons.get("view.status.button.hidde")).setEnabled(false);
        ((JButton) allbuttons.get("common.button.cancel")).setEnabled(true);
        processCounter.begin(totalTimeCounter);
        this.taskMsg.setText(LocaleImpl.getInstance().getString("view.status.msg.do.taskstart")
            + ": " + task.getTitle());
        stepMsg.setText(LocaleImpl.getInstance().getString("view.status.msg.step.initcocoon"));
        stepCounter.begin(currentTimeCounter);
    }

    private void initLogView() {
        if (logView == null) {
            logView = new JDialog();
            logView.setTitle("LogView");
            logtext = new JTextArea(25, 50);
            logtext.setEditable(false);
            logView.getContentPane().add(new JScrollPane(logtext),
                BorderLayout.CENTER);

            JPanel p = new JPanel(new FlowLayout());
            JButton button = new JButton("close");
            button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        logView.dispose();
                    }
                });
            p.add(button);
            logView.getContentPane().add(p, BorderLayout.SOUTH);
            logView.pack();
        }

        logtext.setText(log.toString());
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.conf.ProcessListener#complete()
     *
     */
    public void complete() {
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.PublishListener#publishedFile(java.lang.String,
     *
     * long, long)
     *
     */
    public void publishedFile(
        String file,
        long size,
        long time) {
        stepCounter.end();
        stepCounter.begin(currentTimeCounter);

        //progress.setMaximum(max);
        //progress.setMinimum(0);
        count++;
        progress.setValue(this.file);
        this.file++;
        log.append("[Published] " + file + " bytes:" + sizeformat.format(size)
            + "\n");
        totalTaskTransferedBytes += size;
    }

    public void addResultActionListener(ActionListener l) {
    }

    public void setProjectView(TabbedProjectView pv) {
        //this.pview = pv;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.toolkit.component.Component#setComponentManager(de.miethxml.toolkit.component.ComponentManager)
     *
     */
    public void service(ServiceManager manager) {
        this.manager = manager;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.net.PublishListener#filePublishProcess(java.lang.String,
     *      long, long)
     */
    public void filePublishProcess(
        String name,
        long current,
        long length) {
        stepMsg.setText(name + ": " + sizeformat.format(current) + "/"
            + sizeformat.format(length) + "  " + this.file + "/" + fileCount);
    }

    public JPanel getView() {
        //return panel;
        return mainview;
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.gui.project.StatusPanel#setVisible(boolean)
     *
     */
    public void setVisible(boolean b) {
        panel.setVisible(b);
    }

    public void endProcessing() {
        stepCounter.interrupt();
        processCounter.interrupt();
    }

    public void startProcessing() {
        log.delete(0, log.length());
        stepCounter.begin(currentTimeCounter);

        Thread t = new Thread(stepCounter);
        t.start();

        processCounter.begin(totalTimeCounter);
        t = new Thread(processCounter);
        t.start();
    }

    public class TimeCounter implements Runnable {
        long start;
        long current;
        boolean interrupted = false;
        boolean silent = false;
        JLabel label;
        Thread t;
        SimpleDateFormat dateformat;
        Date date;

        public TimeCounter() {
            dateformat = new SimpleDateFormat("HH:mm:ss");

            TimeZone tz = TimeZone.getTimeZone("GMT");
            dateformat.setTimeZone(tz);
            date = new Date();
        }

        public void run() {
            while (!interrupted) {
                if (!silent) {
                    date.setTime((System.currentTimeMillis() - start));
                    this.label.setText(dateformat.format(date));
                }

                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void begin(JLabel label) {
            this.label = label;
            this.start = System.currentTimeMillis();
            this.interrupted = false;
            this.silent = false;
        }

        public long end() {
            this.silent = true;

            return (System.currentTimeMillis() - start);
        }

        public void interrupt() {
            this.silent = true;
            this.interrupted = true;
        }
    }
}
