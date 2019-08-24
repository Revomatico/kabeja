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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;


public class MemoryUsage {
    private MemoryUsageComponent view = new MemoryUsageComponent();
    private int count = 0;

    public static void main(String[] args) {
        MemoryUsage m = new MemoryUsage();
        m.init();
        m.start();
    }

    public void init() {
        JFrame frame = new JFrame("MemoryTest");
        frame.getContentPane().add(view, BorderLayout.CENTER);
        view.setPreferredSize(new Dimension(100, 30));
        view.setTotalSize(200);
        frame.pack();
        frame.setVisible(true);
    }

    public void start() {
        Thread t = new Thread(new Runnable() {
                    public void run() {
                        while (count < 201) {
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                            }

                            view.setCurrentSize(count++);
                        }
                    }
                });
        t.start();
    }
}
