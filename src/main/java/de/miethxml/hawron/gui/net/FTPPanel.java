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
package de.miethxml.hawron.gui.net;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import de.miethxml.hawron.gui.context.FileTableCellRenderer;
import de.miethxml.hawron.io.FTPsite;
import de.miethxml.hawron.net.FTPEvent;
import de.miethxml.hawron.net.FTPListener;
import de.miethxml.hawron.net.FTPWrapper;

import de.miethxml.toolkit.gui.BorderPanel;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 * @deprecated
 */
public class FTPPanel extends JPanel implements ActionListener, FTPListener {
    FTPsite site;
    JSplitPane split;
    JTextField user;
    JPasswordField password;
    JTextField ftpsite;
    JButton connect;
    JLabel status;
    JLabel info;
    JLabel rate;
    JLabel time;
    JProgressBar pbar;
    FTPWrapper ftp;
    FTPTableModel ftptable;
    JTable table;

    /**
     *
     *
     *
     */
    public FTPPanel() {
        super();
        this.site = new FTPsite();
        init();
    }

    public FTPPanel(FTPsite site) {
        super();
        this.site = site;
        init();
    }

    /**
     * @param arg0
     *
     */
    public FTPPanel(boolean arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     *
     */
    public FTPPanel(LayoutManager arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     *
     * @param arg1
     *
     */
    public FTPPanel(
        LayoutManager arg0,
        boolean arg1) {
        super(arg0, arg1);
    }

    public void init() {
        //new Layout
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);

        //placeholder
        JLabel placeholder = new JLabel("   ");
        addComponent(placeholder, gbl, 0, 0, 1, 1);

        //FTP-Client Panel
        BorderPanel bp = new BorderPanel("FTP-Client");

        //FTP-Toolbar
        JButton button = new JButton("Home");
        bp.addComponent(button, 0, 13, 1, 1);
        button.addActionListener(this);
        button.setActionCommand("ftp.go.home");
        button = new JButton("Up");
        bp.addComponent(button, 1, 13, 1, 1);
        button.addActionListener(this);
        button.setActionCommand("ftp.go.up");
        button = new JButton("Reload");
        bp.addComponent(button, 2, 13, 1, 1);
        button.addActionListener(this);
        button.setActionCommand("ftp.reload");
        button = new JButton("Delete");
        bp.addComponent(button, 3, 13, 1, 1);
        button.addActionListener(this);
        button.setActionCommand("ftp.remove");
        button = new JButton("Make Dir");
        bp.addComponent(button, 4, 13, 1, 1);
        button.addActionListener(this);
        button.setActionCommand("ftp.mkdir");
        button = new JButton("Upload");
        bp.addComponent(button, 5, 13, 1, 1);
        button.addActionListener(this);
        button.setActionCommand("ftp.upload");
        button = new JButton("Download");
        bp.addComponent(button, 6, 13, 1, 1);
        button.addActionListener(this);
        button.setActionCommand("ftp.download");
        button = new JButton("RSYNC");
        bp.addComponent(button, 7, 13, 1, 1);
        button.addActionListener(this);
        button.setActionCommand("ftp.rsync");
        button = new JButton("Abort");
        bp.addComponent(button, 8, 13, 1, 1);
        button.addActionListener(this);
        button.setActionCommand("ftp.abort");
        ftptable = new FTPTableModel();
        table = new JTable(ftptable);

        //new layout
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        table.setPreferredScrollableViewportSize(new Dimension(500, 500));

        TableColumnModel tcm = table.getColumnModel();
        TableColumn column = tcm.getColumn(0);
        column.setCellRenderer(new FileTableCellRenderer());
        column.setPreferredWidth(16);
        column.setMaxWidth(16);
        column.setMinWidth(16);
        table.setRowHeight(16);
        table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int row = table.getSelectedRow();
                        ftptable.setFTPDir(ftp.changeDir(row));
                    }
                }
            });
        table.revalidate();

        JScrollPane sp = new JScrollPane(table);
        bp.addComponent(sp, 0, 0, 9, 12);
        addComponent(bp, gbl, 1, 1, 8, 13);

        //placeholder
        JLabel label = new JLabel("   ");
        addComponent(label, gbl, 9, 1, 1, 1);

        //build Login Panel
        bp = new BorderPanel("Login");
        label = new JLabel("User");
        bp.addComponent(label, 0, 0, 1, 1);
        user = new JTextField(15);
        user.setText(site.getUser());
        bp.addComponent(user, 1, 0, 2, 1);
        label = new JLabel("Password");
        bp.addComponent(label, 0, 1, 1, 1);
        password = new JPasswordField(15);

        //password.setText(PasswordManager.getInstance().decrypt(site.getPassword()));
        bp.addComponent(password, 1, 1, 2, 1);
        label = new JLabel("Site");
        bp.addComponent(label, 0, 2, 1, 1);
        ftpsite = new JTextField(15);
        ftpsite.setText(site.getUri());
        bp.addComponent(ftpsite, 1, 2, 2, 1);
        connect = new JButton("connect");
        connect.addActionListener(this);
        connect.setActionCommand("ftp.connect");
        bp.addComponent(connect, 1, 3, 1, 1);
        addComponent(bp, gbl, 10, 1, 3, 5);

        //placeholder rightmargin
        placeholder = new JLabel("   ");
        addComponent(placeholder, gbl, 13, 14, 1, 1);
        placeholder = new JLabel("   ");
        addComponent(placeholder, gbl, 10, 6, 1, 1);
        placeholder = new JLabel("   ");
        addComponent(placeholder, gbl, 10, 7, 1, 1);
        placeholder = new JLabel("   ");
        addComponent(placeholder, gbl, 10, 8, 1, 1);

        //		the statuspanel
        bp = new BorderPanel("Status");
        status = new JLabel("                       ");
        bp.addComponent(status, 0, 0, 3, 1);
        info = new JLabel("                           ");
        bp.addComponent(info, 0, 1, 3, 1);
        rate = new JLabel("                            ");
        bp.addComponent(rate, 0, 2, 3, 1);
        time = new JLabel("                               ");
        bp.addComponent(time, 0, 3, 3, 1);
        pbar = new JProgressBar();
        pbar.setVisible(false);
        bp.addComponent(pbar, 0, 4, 3, 1);
        addComponent(bp, gbl, 10, 9, 3, 5);

        //panel.add(pbar);
        //panel2.add(BorderLayout.CENTER, panel);
        //split.setLeftComponent(panel2);
        //RightSide with FTPView
        //panel = new JPanel(new BorderLayout());
        //JPanel p = new JPanel(new FlowLayout());
        //		panel.add(BorderLayout.NORTH, p);
        //split.setRightComponent(panel);
        //split.validate();
        //add(split);
        validate();
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *
     */
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getActionCommand().equals("ftp.connect")) {
            if (ftp == null) {
                ftp = new FTPWrapper();
                ftp.addFTPListener(this);
            }

            ftp.setUser(user.getText());
            ftp.setPassword(new String(password.getPassword()));
            ftp.setFTPSite(ftpsite.getText());
            ftptable.setFTPDir(ftp.connect());
        }

        if (arg0.getActionCommand().equals("ftp.go.up")) {
            ftptable.setFTPDir(ftp.goUp());
        }

        if (arg0.getActionCommand().equals("ftp.go.home")) {
            ftptable.setFTPDir(ftp.goHome());
        }

        if (arg0.getActionCommand().equals("ftp.reload")) {
            ftptable.setFTPDir(ftp.reload());
        }

        if (arg0.getActionCommand().equals("ftp.mkdir")) {
            String newdir = JOptionPane.showInputDialog("Please input the dir");
            ftp.mkDir(newdir);
            ftptable.setFTPDir(ftp.reload());
        }

        if (arg0.getActionCommand().equals("ftp.remove")) {
            int index = table.getSelectedRow();
            ftp.deleteEntry(index);
            ftptable.setFTPDir(ftp.reload());
        }

        if (arg0.getActionCommand().equals("ftp.upload")) {
            JFileChooser chooser = new JFileChooser();

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                ftp.upload(f.getPath(), f.getName());
                status.setText("Upload " + f.getName());
            }
        }

        if (arg0.getActionCommand().equals("ftp.download")) {
            int index = table.getSelectedRow();
            String filename = ftptable.getSelectedName(index);
            status.setText("Download " + filename);
            System.out.println("download=" + filename);

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(filename));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                ftp.download(f.getPath(), index);
            }
        }

        if (arg0.getActionCommand().equals("ftp.abort")) {
            ftp.abort();
        }

        if (arg0.getActionCommand().equals("ftp.disconnect")) {
            ftp.disconnect();
        }

        if (arg0.getActionCommand().equals("ftp.rsync")) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();

                if (f.exists() && f.isDirectory()) {
                    ftp.synchronize(f.getAbsolutePath());
                }
            }
        }
    }

    /*
     *
     * (non-Javadoc)
     *
     * @see de.miethxml.net.FTPListener#ftpStatePerformed(de.miethxml.net.FTPEvent)
     *
     */
    public void ftpStatePerformed(FTPEvent e) {
        double r = 0.001;

        switch (e.getType()) {
        case FTPEvent.CONNECT:
            connect.setText("disconnect");
            connect.setActionCommand("ftp.disconnect");

            break;

        case FTPEvent.DISCONNECT:
            connect.setText("connect");
            connect.setActionCommand("ftp.connect");

            break;

        case FTPEvent.DOWNLOAD_START:
            pbar.setMinimum(0);
            pbar.setMaximum((int) e.getSize());
            pbar.setValue(0);
            pbar.setVisible(true);

            break;

        case FTPEvent.DOWNLOADING:

            String msg = Long.toString(e.getCount());
            msg = msg + "/";
            msg = msg + Long.toString(e.getSize()) + " Bytes";
            info.setText(msg);
            r = (1000 * e.getCount()) / (e.getTime() * 1024);
            msg = Double.toString(r) + "  Kb/sec";
            rate.setText(msg);
            msg = Long.toString(e.getTime() / 1000) + " sec from "
                + Double.toString((e.getSize() / 1024) / r) + " sec";
            time.setText(msg);
            pbar.setValue((int) e.getCount());

            break;

        case FTPEvent.DOWNLOAD_END:
            pbar.setVisible(false);

        case FTPEvent.UPLOAD_START:
            pbar.setMinimum(0);
            pbar.setMaximum((int) e.getSize());
            pbar.setValue(0);
            pbar.setVisible(true);
            status.setText("Upload " + e.getName());

            break;

        case FTPEvent.UPLOADING:
            msg = Long.toString(e.getCount());
            msg = msg + "/";
            msg = msg + Long.toString(e.getSize()) + " Bytes";
            info.setText(msg);

            if (e.getTime() > 0) {
                r = (1000 * e.getCount()) / (e.getTime() * 1024);
                msg = Double.toString(r) + "  Kb/sec";
            } else {
                msg = " ... Kb/sec";
            }

            rate.setText(msg);

            if (e.getSize() > 0) {
                msg = Long.toString(e.getTime() / 1000) + " sec from "
                    + Double.toString((e.getSize() / 1024) / r) + " sec";
            } else {
                msg = "0 sec";
            }

            time.setText(msg);
            pbar.setValue((int) e.getCount());

            break;

        case FTPEvent.UPLOAD_END:
            pbar.setVisible(false);
        }
    }

    private void addComponent(
        Component c,
        GridBagLayout gbl,
        int x,
        int y,
        int width,
        int height) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbl.setConstraints(c, gbc);
        add(c);
    }
}
