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
package de.miethxml.toolkit.wizard;

import java.awt.BorderLayout;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.PropertyResourceBundle;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 */
public class Main {
    private String[] args;

    /**
     *
     */
    public Main(String[] args) {
        super();
        this.args = args;
    }

    public static void main(String[] args) {
        System.setSecurityManager(null);

        Main main = new Main(args);

        main.init();
    }

    public void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        WizardManager manager = null;

        if (args.length == 2) {
            try {
                WizardConstants.LOCALE = new PropertyResourceBundle(new FileInputStream(
                            args[1]));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        if (args.length == 0) {
            manager = WizardFactory.createDefaultInstaller();
        } else {
            manager = WizardFactory.createFromFile(args[0]);
        }

        //		WizardConfiguration conf = manager.getWizardConfiguration();
        //testing
        //		ArrayList list = new ArrayList();
        //		list.add("/home/simon/nfs/cdr/cocoon-2.1.2-src.tar.gz");
        //		list.add("/home/simon/nfs/cdr/cocoon-2.1-src.tar.gz");
        //		conf.setValue(UnpackComponent.FILES_LIST_KEY,list);
        JFrame frame = new JFrame("Wizard");
        frame.getContentPane().setLayout(new BorderLayout());

        JComponent view = manager.getView();
        frame.getContentPane().add(view, BorderLayout.CENTER);
        frame.setSize(view.getPreferredSize());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        manager.start();
    }
}
