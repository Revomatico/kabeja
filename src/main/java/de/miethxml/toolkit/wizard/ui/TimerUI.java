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

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;

import javax.swing.JLabel;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class TimerUI implements Runnable {
    private long start;
    private long current;
    private boolean go = false;
    private JLabel label;
    private Thread t;
    private SimpleDateFormat dateformat;
    private Date date;
    private int count;
    private int maxCount;

    public TimerUI() {
        dateformat = new SimpleDateFormat("HH:mm:ss");

        TimeZone tz = TimeZone.getTimeZone("GMT");
        dateformat.setTimeZone(tz);
        date = new Date();
    }

    public void run() {
        while (go) {
            long time = (System.currentTimeMillis() - start);
            long left = ((maxCount - count) * time) / count;
            date.setTime(left);
            this.label.setText(dateformat.format(date));

            //calculate the left time
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void begin(
        JLabel label,
        int maxCount) {
        this.label = label;
        start = System.currentTimeMillis();
        this.go = true;

        //
        this.maxCount = maxCount + 1;
        this.count = 1;
        t = new Thread(this);
        t.start();
    }

    public void end() {
        this.go = false;
    }

    public void next() {
        count++;
    }

    public void setValue(int count) {
        this.count = count;
    }
}
