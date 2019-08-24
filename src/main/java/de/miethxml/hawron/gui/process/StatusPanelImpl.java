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

import de.miethxml.hawron.project.Project;
import de.miethxml.hawron.project.ProjectListener;
import de.miethxml.hawron.project.ProjectProcessEvent;
import de.miethxml.hawron.project.Task;

import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.conf.LocaleListener;
import de.miethxml.toolkit.gui.LocaleBorderPanel;
import de.miethxml.toolkit.gui.LocaleButton;
import de.miethxml.toolkit.gui.LocaleLabel;

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
public class StatusPanelImpl implements ProjectListener, ActionListener,
    LocaleListener, StatusPanel {
    private ServiceManager manager;
    private Hashtable allbuttons;
    private Hashtable labels;
    private JLabel task;
    private JLabel step;
    private JLabel time;
    private JLabel current;
    private JProgressBar progress;
    private StringBuffer log;
    int errors;
    private Project project;
    private TimeCounter processcounter;
    private TimeCounter stepcounter;
    private JDialog logview;
    private JTextArea logtext;
    private NumberFormat sizeformat;
    private int count;
    private int max;
    private int fileCount;
    private int file;

    //private ProjectViewImpl pview;
    private Task processTask;

    //private LocaleBorderPanel panel;
    private JPanel panel;
    private JPanel mainview;

    /**
     * @param title
     *
     */
    public StatusPanelImpl() {
        allbuttons = new Hashtable();
        labels = new Hashtable();
        log = new StringBuffer();
        errors = 0;
        stepcounter = new TimeCounter();
        processcounter = new TimeCounter();
        sizeformat = NumberFormat.getInstance();
    }

    public void initialize() {
        mainview = new JPanel();

        FormLayout mainlayout = new FormLayout("3dlu,fill:pref:grow,3dlu",
                "3dlu,p,3dlu");
        mainview.setLayout(mainlayout);

        CellConstraints ccm = new CellConstraints();

        //mainview.add(new LocaleSeparator("view.status.title"), ccm.xy(2, 2));
        panel = new LocaleBorderPanel("view.status.title");

        //panel = new JPanel();
        FormLayout layout = new FormLayout("3dlu,right:pref,3dlu,fill:pref:grow,3dlu",
                "3dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,3dlu");
        panel.setLayout(layout);

        CellConstraints cc = new CellConstraints();
        LocaleLabel label = new LocaleLabel("view.status.label.do");

        //labels.put("view.status.label.do", label);
        panel.add(label, cc.xy(2, 2));
        task = new JLabel("");
        panel.add(task, cc.xy(4, 2));
        label = new LocaleLabel("view.status.label.step");

        //labels.put("view.status.label.step", label);
        panel.add(label, cc.xy(2, 4));
        step = new JLabel("");
        panel.add(step, cc.xy(4, 4));
        label = new LocaleLabel("view.status.label.time");

        //labels.put("view.status.label.time", label);
        panel.add(label, cc.xy(2, 6));
        time = new JLabel("");
        panel.add(time, cc.xy(4, 6));
        label = new LocaleLabel("view.status.label.current");

        //labels.put("view.status.label.current", label);
        panel.add(label, cc.xy(2, 8));
        current = new JLabel("");
        panel.add(current, cc.xy(4, 8));
        label = new LocaleLabel("view.status.label.all");

        //labels.put("view.status.label.all", label);
        panel.add(label, cc.xy(2, 10));
        progress = new JProgressBar();
        panel.add(progress, cc.xy(4, 10));

        LocaleButton[] buttons = new LocaleButton[4];
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
        buttons[3] = new LocaleButton("view.status.button.showresult");
        allbuttons.put("view.status.button.showresult", buttons[3]);
        buttons[3].setEnabled(false);
        buttons[3].setVisible(false);
        buttons[3].setActionCommand("show.result");
        buttons[3].addActionListener(this);

        ButtonBarBuilder bbuilder = new ButtonBarBuilder();

        //bbuilder.addRelatedGap();
        //bbuilder.addGlue();
        bbuilder.addGriddedButtons(buttons);
        panel.add(bbuilder.getPanel(), cc.xy(4, 12));
        mainview.add(panel, ccm.xy(2, 2));

        //setProject(project);
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
        errors++;
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
        stepcounter.end();
        stepcounter.begin(current);
        step.setText(ppe.getPage() + "/" + ppe.getLinks());
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
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.PublishListener#errorMessage(java.lang.String)
     *
     */
    public void errorMessage(String msg) {
    }

    public void startPublishing(int fileCount) {
        System.out.println("start publishing");
        stepcounter.end();
        stepcounter.begin(current);
        max = progress.getMaximum() + fileCount;
        count = progress.getValue();
        progress.setMaximum(max);
        progress.setMinimum(0);
        progress.setValue(count);
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
            logview.setVisible(true);
        } else if (e.getActionCommand().equals("cancel")) {
            this.project.cancelTaskProcessing();
            task.setText(LocaleImpl.getInstance().getString("view.status.msg.do.taskcancel"));
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
        processcounter.end();
        stepcounter.end();
        step.setText("");

        String msg = LocaleImpl.getInstance().getString("view.status.msg.do.taskend")
            + ": " + processTask.getTitle();

        if (errors > 0) {
            msg = msg + ", "
                + LocaleImpl.getInstance().getString("view.status.msg.do.taskerror")
                + ": " + errors;
        }

        task.setText(msg);
        current.setText("");
        ((JButton) allbuttons.get("view.status.button.viewlog")).setEnabled(true);
        ((JButton) allbuttons.get("view.status.button.hidde")).setEnabled(true);
        ((JButton) allbuttons.get("common.button.cancel")).setEnabled(false);

        progress.setValue(0);
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
        errors = 0;
        log.delete(0, log.length());
        ((JButton) allbuttons.get("view.status.button.viewlog")).setEnabled(false);
        ((JButton) allbuttons.get("view.status.button.hidde")).setEnabled(false);
        ((JButton) allbuttons.get("common.button.cancel")).setEnabled(true);

        JButton button = (JButton) allbuttons.get(
                "view.status.button.showresult");
        button.setVisible(false);
        button.setEnabled(false);
        processcounter.begin(time);
        this.task.setText(LocaleImpl.getInstance().getString("view.status.msg.do.taskstart")
            + ": " + task.getTitle());
        step.setText(LocaleImpl.getInstance().getString("view.status.msg.step.initcocoon"));
        stepcounter.begin(current);
    }

    private void initLogView() {
        if (logview == null) {
            logview = new JDialog();
            logview.setTitle("LogView");
            logtext = new JTextArea(25, 50);
            logtext.setEditable(false);
            logview.getContentPane().add(new JScrollPane(logtext),
                BorderLayout.CENTER);

            JPanel p = new JPanel(new FlowLayout());
            JButton button = new JButton("close");
            button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        logview.dispose();
                    }
                });
            p.add(button);
            logview.getContentPane().add(p, BorderLayout.SOUTH);
            logview.pack();
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
     * @see de.miethxml.conf.ProjectListener#publish(java.lang.String, long,
     *
     * long)
     *
     */
    public void publish(
        String file,
        long published,
        long size) {
        step.setText(file + ": " + sizeformat.format(published) + "/"
            + sizeformat.format(size) + "  " + this.file + "/" + fileCount);
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
        stepcounter.end();
        stepcounter.begin(current);
        progress.setMaximum(max);
        progress.setMinimum(0);
        count++;
        progress.setValue(count);
        this.file++;
    }

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
        long max) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
    }

    public void startProcessing() {
        // TODO Auto-generated method stub
    }

    public class TimeCounter implements Runnable {
        long start;
        long current;
        boolean go = false;
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
            while (go) {
                date.setTime((System.currentTimeMillis() - start));
                this.label.setText(dateformat.format(date));

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void begin(JLabel label) {
            this.label = label;
            start = System.currentTimeMillis();
            this.go = true;
            t = new Thread(this);
            t.start();
        }

        public void end() {
            this.go = false;
        }
    }
}
