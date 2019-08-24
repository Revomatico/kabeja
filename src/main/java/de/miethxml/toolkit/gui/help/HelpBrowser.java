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
package de.miethxml.toolkit.gui.help;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;

import java.net.URL;

import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import de.miethxml.toolkit.cache.Cacheable;
import de.miethxml.toolkit.conf.ConfigManager;
import de.miethxml.toolkit.conf.LocaleImpl;
import de.miethxml.toolkit.gui.LocaleButton;


//import org.apache.log4j.*;
public class HelpBrowser implements ActionListener, HyperlinkListener,
    Cacheable {
    JFrame frame;
    JEditorPane pane;

    //JToolBar toolbar;
    Stack back;

    //JToolBar toolbar;
    Stack forward;

    //    URL url;
    String url;

    //    URL url;
    String last;
    String home;

    //static Category cat = Category.getInstance("HelpBrowser");
    public HelpBrowser() {
        //cat.debug("new Instance");
        frame = new JFrame("Help");

        frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });

        frame.getContentPane().setLayout(new BorderLayout());

        frame.getContentPane().add("North", createJToolBar());

        pane = new JEditorPane();

        //
        init();

        String fs = System.getProperty("file.separator");

        String initPage = "file:"
            + ConfigManager.getInstance().getProperty("help.path") + fs
            + ConfigManager.getInstance().getProperty("lang") + fs
            + "index.html";

        // cat.debug("help start:"+initPage);
        setHomePage(initPage);

        goHome();

        pane.setEditable(false);

        pane.addHyperlinkListener(this);

        JScrollPane scroll = new JScrollPane(pane);

        frame.getContentPane().add("Center", scroll);

        frame.pack();

        frame.setSize(500, 400);

        frame.setLocation(0, 20);

        //frame.setVisible(true);
        last = "";

        url = "";
    }

    public HelpBrowser(JDesktopPane parent) {
        JInternalFrame iframe = new JInternalFrame(LocaleImpl.getInstance()
                                                             .getString("HelpBrowser.Title"),
                true, true, true, true);

        iframe.getContentPane().setLayout(new BorderLayout());

        iframe.getContentPane().add("North", createJToolBar());

        pane = new JEditorPane();

        init();

        String fs = System.getProperty("file.separator");

        String initPage = "file:"
            + ConfigManager.getInstance().getProperty("help.path") + fs
            + ConfigManager.getInstance().getProperty("lang") + fs
            + "index.html";

        setHomePage(initPage);

        goHome();

        pane.setEditable(false);

        pane.addHyperlinkListener(this);

        JScrollPane scroll = new JScrollPane(pane);

        iframe.getContentPane().add("Center", scroll);

        iframe.pack();

        iframe.setSize(500, 400);

        iframe.setLocation(0, 20);

        iframe.setVisible(true);

        last = "";

        url = "";

        parent.add(iframe);

        iframe.toFront();
    }

    public static void main(String[] args) {
        HelpBrowser browser = new HelpBrowser();

        browser.setHomePage(args[0]);

        browser.goHome();
    }

    private JToolBar createJToolBar() {
        JToolBar toolbar = new JToolBar();

        //set BackButton
        ImageIcon backicon = new ImageIcon("icons/back.gif");

        LocaleButton backbutton = new LocaleButton("common.button.back",
                backicon, false);

        backbutton.addActionListener(this);

        backbutton.setActionCommand("goback");

        toolbar.add(backbutton);

        //set HomeButton
        ImageIcon homeicon = new ImageIcon("icons/home.gif");

        LocaleButton homebutton = new LocaleButton("common.button.home",
                homeicon, false);

        homebutton.addActionListener(this);

        homebutton.setActionCommand("gohome");

        toolbar.add(homebutton);

        //set ForwardButton
        ImageIcon forwardicon = new ImageIcon("icons/forward.gif");

        LocaleButton forwardbutton = new LocaleButton("common.button.forward",
                forwardicon, false);

        forwardbutton.addActionListener(this);

        forwardbutton.setActionCommand("goforward");

        toolbar.add(forwardbutton);

        return toolbar;
    }

    public void setHomePage(String homepage) {
        //cat.debug("set HomePage to:"+homepage);
        this.home = homepage;
    }

    public void goHome() {
        if (home != null) {
            back.push(url);

            last = url;

            setPage(home);
        }
    }

    //ActionListener
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("goback")) {
            if (!last.equals(url)) {
                if (!back.empty()) {
                    last = url;

                    url = (String) back.pop();

                    if (url != null) {
                        setPage(url);

                        forward.push(last);
                    }
                }
            }
        }

        if (e.getActionCommand().equals("goforward")) {
            if (!last.equals(url)) {
                if (!forward.empty()) {
                    last = url;

                    url = (String) forward.pop();

                    if (url != null) {
                        setPage(url);

                        back.push(last);
                    }
                }
            }
        }

        if (e.getActionCommand().equals("gohome")) {
            goHome();
        }
    }

    //HyperlinkListener
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            back.push(url);

            last = url;

            url = ((URL) e.getURL()).toExternalForm();

            setPage(url);
        }
    }

    public void setPage(String go) {
        if ((go != null) && (go.length() > 0)) {
            try {
                url = go;

                pane.setPage(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void exit() {
        //Resourcen freigeben
        frame.dispose();

        //Programm beenden
        System.exit(0);
    }

    private void init() {
        back = new Stack();

        forward = new Stack();

        //	if((new File("help/index.html")).exists()){
        //    url="file:help/index.html";
        //    setHomePage(url);
        //    goHome();
        //}else{
        url = "";

        //}
    }

    public void show() {
        frame.show();
    }

    public void dispose() {
        frame.dispose();
    }

    public void destroy() {
        frame = null;

        pane = null;
    }
} // HelpBrowser
