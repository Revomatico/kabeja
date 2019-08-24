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
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import de.miethxml.toolkit.ui.event.PopupActionListener;

/**
 * @author simon
 *
 *
 *
 */
public class PanelFactory {
  /**
   * Create a titled Panel with a icon on the left side and a PopupButton on the right side.
   * 
   * @param view
   * @param title
   * @param icon
   * @param menu
   *          the JPopupMenu, which should displayed
   * @return
   */
  public static JPanel createTitledPanel(JComponent view, String title, String icon, JPopupMenu menu) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(new SmallShadowBorder());

    ButtonGradientTitledLabel bpanel = new ButtonGradientTitledLabel(title);

    if (icon != null) {
      bpanel.addComponent(new JLabel(new ImageIcon(icon)), SwingConstants.LEFT);
    }

    // the popup-button
    JButton button = new JButton(new ImageIcon("icons/prefs01.gif"));
    button.addActionListener(new PopupActionListener(menu));
    button.setUI(new BasicButtonUI());
    bpanel.addComponent(button);

    panel.add(bpanel, BorderLayout.NORTH);

    panel.add(view, BorderLayout.CENTER);

    return panel;
  }

  public static JPanel createTitledPanel(JComponent view, String title) {
    return createTitledPanel(view, title, null);
  }

  public static JPanel createTitledPanel(JComponent view, String title, ImageIcon icon) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(new SmallShadowBorder());

    ButtonGradientTitledLabel bpanel = new ButtonGradientTitledLabel(title);

    if (icon != null) {
      bpanel.addComponent(new JLabel(icon), SwingConstants.LEFT);
    } else {
      // set a higher padding
      bpanel.setPadding(10);
    }

    panel.add(bpanel, BorderLayout.NORTH);
    panel.add(view, BorderLayout.CENTER);

    return panel;
  }

  public static JSplitPane createOneTouchSplitPane() {
    return createOneTouchSplitPane(JSplitPane.HORIZONTAL_SPLIT);
  }

  public static JSplitPane createOneTouchSplitPane(int orientation) {
    JSplitPane pane = new JSplitPane(orientation);
    pane.setOpaque(false);

    // create a divider without decoration
    pane.setOneTouchExpandable(true);

    pane.setUI(new SimpleSplitPaneUI());

    pane.setDividerSize(11);

    pane.setBorder(BorderFactory.createEmptyBorder());

    return pane;
  }

  /**
   * Create a JSplitPane and remove any decoration (divider).
   *
   * @return
   */

  public static JSplitPane createDefaultSplitPane() {
    return createDefaultSplitPane(JSplitPane.HORIZONTAL_SPLIT);
  }

  /**
   * Create a JSplitPane and remove any decoration (divider).
   * 
   * @param orientation
   * @return
   */
  public static JSplitPane createDefaultSplitPane(int orientation) {
    JSplitPane pane = new JSplitPane(orientation);
    pane.setOpaque(false);

    BasicSplitPaneUI ui = new BasicSplitPaneUI();

    pane.setUI(ui);
    ui.getDivider().setBorder(null);
    pane.setBorder(BorderFactory.createEmptyBorder());

    return pane;
  }
}
