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
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.miethxml.toolkit.ui.GradientLabel;
import de.miethxml.toolkit.ui.TopLineBorder;
import de.miethxml.toolkit.wizard.event.VetoException;
import de.miethxml.toolkit.wizard.event.WizardListener;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class WizardManager {
    public static String ACTION_NEXT = "next";
    public static String ACTION_PREVIOUS = "previous";
    public static String ACTION_CANCEL = "cancel";
    public static String ACTION_SKIP = "skip";
    public static String ACTION_QUIT = "quit";

    static {
        if (WizardConstants.LOCALE == null) {
            WizardConstants.LOCALE = PropertyResourceBundle.getBundle(
                    "resources.labels");
        }
    }

    private ArrayList components = new ArrayList();
    private WizardConfiguration conf = new WizardConfiguration();
    private WizardComponent currentComponent;
    private int currentStep = -1;

    //the view
    private JPanel panel;
    private JButton nextButton;
    private JButton previousButton;
    private JButton skipButton;
    private GradientLabel titleLabel;
    private CardLayout cardLayout;
    private JPanel componentPanel;

    //the controller - MVC
    private Controller controller;
    private Thread worker;
    private boolean stopped = false;
    private ArrayList listener = new ArrayList();
    private ResourceBundle resource;
    private double buttonPanelHeight;

    /**
     *
     */
    public WizardManager() {
        super();

        resource = WizardConstants.LOCALE;
    }

    public void addWizardComponent(WizardComponent comp) {
        comp.setWizardConfiguration(conf);

        JComponent view = comp.getInstallUIComponent();
        componentPanel.add(view, "" + components.size());
        components.add(comp);

        Dimension dim = view.getPreferredSize();
        double width = dim.getWidth();
        double height = titleLabel.getPreferredSize().getHeight()
            + dim.getHeight() + buttonPanelHeight;
        System.out.println("from comp:" + dim);

        if (panel.getPreferredSize().getWidth() > width) {
            width = panel.getPreferredSize().getWidth();
        }

        if (panel.getPreferredSize().getHeight() > height) {
            height = panel.getPreferredSize().getHeight();
        }

        panel.setPreferredSize(new Dimension((int) width, (int) height));
        System.out.println("set View:" + panel.getPreferredSize());
    }

    public JComponent getView() {
        return panel;
    }

    public void initialize() {
        panel = new JPanel(new BorderLayout());
        controller = new Controller();

        //the title
        titleLabel = new GradientLabel("");
        panel.add(titleLabel, BorderLayout.NORTH);

        //the ComponentView
        cardLayout = new CardLayout();
        componentPanel = new JPanel(cardLayout);
        panel.add(componentPanel, BorderLayout.CENTER);

        //the buttonPanel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new TopLineBorder());
        previousButton = new JButton(resource.getString("common.previous"));
        previousButton.setActionCommand(ACTION_PREVIOUS);
        previousButton.addActionListener(controller);
        previousButton.setEnabled(false);
        buttonPanel.add(previousButton);
        nextButton = new JButton(resource.getString("common.next"));
        nextButton.setActionCommand(ACTION_NEXT);
        nextButton.addActionListener(controller);
        buttonPanel.add(nextButton);
        skipButton = new JButton(resource.getString("common.skip"));
        skipButton.setActionCommand(ACTION_SKIP);
        skipButton.addActionListener(controller);
        buttonPanel.add(skipButton);
        buttonPanelHeight = buttonPanel.getPreferredSize().getHeight();
        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void start() {
        nextInstallComponent();
    }

    private void processNextInstallComponent() {
        stopped = false;
        worker = new Thread(new Runnable() {
                    public void run() {
                        try {
                            currentComponent.startWizardProcess();

                            if (!stopped) {
                                nextButton.setText(resource.getString(
                                        "common.next"));
                                nextButton.setActionCommand(ACTION_NEXT);
                                nextInstallComponent();
                            }
                        } catch (WizardException e) {
                            JOptionPane.showMessageDialog(null,
                                resource.getString("installer.error.message")
                                + e.getLocalizedMessage());
                        }
                    }
                });
        worker.start();
        nextButton.setText(resource.getString("common.cancel"));
        nextButton.setActionCommand(ACTION_CANCEL);
    }

    private void stopProcessing() {
        try {
            currentComponent.stopWizardProcess();
        } catch (WizardException e) {
            //not the best but only possible for ANT
            worker.stop();
        }

        stopped = true;
        nextButton.setText(resource.getString("common.next"));
        nextButton.setActionCommand(ACTION_NEXT);
    }

    private void nextInstallComponent() {
        currentStep++;

        if (currentStep < components.size()) {
            skipButton.setText(resource.getString("common.skip"));
            skipButton.setActionCommand(ACTION_SKIP);

            currentComponent = (WizardComponent) components.get(currentStep);
            titleLabel.setText(currentComponent.getTitle());
            cardLayout.show(componentPanel, "" + currentStep);

            if (currentStep > 0) {
                previousButton.setEnabled(true);
            }

            if (currentComponent.isModifiable()) {
                nextButton.setEnabled(true);
            } else {
                nextButton.setEnabled(false);
                processNextInstallComponent();
            }
        } else {
            fireEndInstallationEvent();
            skipButton.setText(resource.getString("common.quit"));
            skipButton.setActionCommand(ACTION_QUIT);
            nextButton.setEnabled(false);

            //one step back;
            currentStep--;
        }
    }

    private void previousInstallComponent() {
        stopProcessing();

        if (currentStep > 0) {
            if (currentStep == (components.size() - 1)) {
                //switch back the quit-button
                skipButton.setText(resource.getString("common.skip"));
                skipButton.setActionCommand(ACTION_SKIP);
            }

            currentStep--;
            currentComponent = (WizardComponent) components.get(currentStep);
            titleLabel.setText(currentComponent.getTitle());
            cardLayout.show(componentPanel, "" + currentStep);
            nextButton.setText(resource.getString("common.next"));
            nextButton.setActionCommand(ACTION_NEXT);
            nextButton.setEnabled(true);
        }
    }

    public WizardConfiguration getWizardConfiguration() {
        return conf;
    }

    public void setWizardConfiguration(WizardConfiguration conf) {
        this.conf = conf;

        if (components.size() > 0) {
            Iterator i = components.iterator();

            while (i.hasNext()) {
                WizardComponent comp = (WizardComponent) i.next();
                comp.setWizardConfiguration(conf);
            }
        }
    }

    public void addInstallerListener(WizardListener l) {
        listener.add(l);
    }

    public void removeInstallerListener(WizardListener l) {
        listener.remove(l);
    }

    private void fireStartInstallationEvent() {
        Iterator i = listener.iterator();

        while (i.hasNext()) {
            WizardListener l = (WizardListener) i.next();
            l.startInstallation();
        }
    }

    private void fireEndInstallationEvent() {
        Iterator i = listener.iterator();

        while (i.hasNext()) {
            WizardListener l = (WizardListener) i.next();
            l.endInstallation();
        }
    }

    private void fireQuitInstallationEvent() {
        Iterator i = listener.iterator();

        while (i.hasNext()) {
            WizardListener l = (WizardListener) i.next();

            try {
                l.quitInstallation();
            } catch (VetoException e) {
                //a Veto do nothing
                return;
            }
        }

        //no Veto -> exit
        System.exit(0);
    }

    public void showInstallComponent(int index) {
        currentStep = index - 1;
        nextInstallComponent();
    }

    private class Controller implements ActionListener {
        /*
         * (non-Javadoc)
         *
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();

            if (cmd.equals(ACTION_NEXT)) {
                processNextInstallComponent();
            } else if (cmd.equals(ACTION_CANCEL)) {
                stopProcessing();
            } else if (cmd.equals(ACTION_PREVIOUS)) {
                previousInstallComponent();
            } else if (cmd.equals(ACTION_SKIP)) {
                stopProcessing();

                nextInstallComponent();
            } else if (cmd.equals(ACTION_QUIT)) {
                fireQuitInstallationEvent();
            }
        }
    }
}
