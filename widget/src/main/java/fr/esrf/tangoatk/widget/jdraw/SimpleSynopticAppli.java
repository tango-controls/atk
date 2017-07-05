/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
/*
 * SimpleSynopticAppli.java
 *
 * Created on May 25, 2005
 */

package fr.esrf.tangoatk.widget.jdraw;

import fr.esrf.Tango.AttrQuality;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.attribute.SimpleScalarViewer;
import fr.esrf.tangoatk.widget.attribute.Trend;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

import fr.esrf.tangoatk.widget.util.*;
import fr.esrf.tangoatk.widget.util.jdraw.JDFileFilter;
import org.tango.settingsmanager.client.SettingsManagedEvent;
import org.tango.settingsmanager.client.SettingsManagedListener;
import org.tango.settingsmanager.client.SettingsManagerClient;

import javax.swing.*;

/**
 * @author PONCET
 */
public class SimpleSynopticAppli extends javax.swing.JFrame implements SynopticProgressListener {

  private final Splash splash = new Splash();

  private ErrorHistory errorHistory;
  private boolean standAlone = false;
  private boolean fileLoaded = false;
  private String  settingManagerName;
  private SettingsManagerClient sm;
  private SimpleScalarViewer settingFile = null;

  private AttributePolledList numberAndStateScalarAttList; /* used in the global trend */
  private AttributeList attList; /* used for setting manager */
  private JFrame trendFrame;
  private JPanel innerPanel;
  private Trend globalTrend = null;

  /**
   * Creates new form SimpleSynopticAppli
   */
  public SimpleSynopticAppli() {

    fileLoaded = false;
    standAlone = false;
    errorHistory = new ErrorHistory();
    splash.setTitle("SimpleSynopticAppli  ");
    splash.setCopyright("(c) ESRF 2003-2015");
    splash.setMessage("Loading synoptic ...");
    splash.initProgress();
    splash.setMaxProgress(100);
    splash.setVisible(true);

    numberAndStateScalarAttList = new fr.esrf.tangoatk.core.AttributePolledList();
    numberAndStateScalarAttList.setFilter(new IEntityFilter() {
      public boolean keep(IEntity entity) {
        if ((entity instanceof INumberScalar)
            || (entity instanceof IDevStateScalar)) {
          return true;
        }
        return false;
      }
    });

    trendFrame = new JFrame();
    javax.swing.JPanel trendPanel = new javax.swing.JPanel();
    trendPanel.setPreferredSize(new java.awt.Dimension(600, 300));
    trendPanel.setLayout(new BorderLayout());
    globalTrend = new Trend(trendFrame);
    trendPanel.add(globalTrend, BorderLayout.CENTER);
    trendFrame.setContentPane(trendPanel);
    trendFrame.pack();

    initComponents();

  }

  public SimpleSynopticAppli(String jdrawFullFileName) {

    this();
    loadSynoptic(jdrawFullFileName);
    pack();
    setVisible(true);

  }

  public SimpleSynopticAppli(String jdrawFullFileName, boolean stand) {

    this();
    loadSynoptic(jdrawFullFileName);
    standAlone = stand;

  }

  public SimpleSynopticAppli(String jdrawFullFileName, String settingManagerName, boolean stand) {

    this(jdrawFullFileName,stand);
    this.settingManagerName = settingManagerName;

    if( settingManagerName!=null ) {

      // Setting manager file menu
      fileLoadMenuItem = new JMenuItem("Load...");
      fileLoadMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          fileLoadMenuItemActionPerformed(e);
        }
      });


      filePreviewMenuItem = new JMenuItem("Preview...");
      filePreviewMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          filePreviewMenuItemActionPerformed(e);
        }
      });


      fileSaveMenuItem = new JMenuItem("Save...");
      fileSaveMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          fileSaveMenuItemActionPerformed(e);
        }
      });

      fileJMenu.add(new JSeparator(),0);
      fileJMenu.add(fileSaveMenuItem,0);
      fileJMenu.add(filePreviewMenuItem, 0);
      fileJMenu.add(fileLoadMenuItem,0);

      // Setting manager API
      try {

        sm = new SettingsManagerClient(settingManagerName);
        sm.addSettingsAppliedListener(new SettingsManagedListener() {
          @Override
          public void settingsManaged(SettingsManagedEvent event) {
            settingsAppliedPerformed(event);
          }
        });

      } catch (DevFailed ex) {
        ErrorPane.showErrorMessage(this, settingManagerName, ex);
      }

      // Setting panel
      attList = new AttributeList();
      attList.addErrorListener(errorHistory);

      JPanel settingPanel = new JPanel();
      settingPanel.setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();

      gbc.fill = GridBagConstraints.BOTH;
      gbc.insets = new Insets(3,3,3,3);

      JLabel label1 = new JLabel("Configuration file");
      label1.setFont(ATKConstant.labelFont);

      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = 0;
      settingPanel.add(label1,gbc);

      settingFile = new SimpleScalarViewer();
      settingFile.setBackgroundColor(Color.WHITE);

      gbc.gridx = 1;
      gbc.gridy = 0;
      gbc.weightx = 1;
      gbc.ipadx = 100;
      settingPanel.add(settingFile,gbc);

      JButton statusBtn = new JButton("Status");
      statusBtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          fileStatusMenuItemActionPerformed(e);
        }
      });
      gbc.gridx = 2;
      gbc.gridy = 0;
      gbc.ipadx = 0;
      gbc.weightx = 0;
      settingPanel.add(statusBtn,gbc);

      JButton previewBtn = new JButton("Preview");
      previewBtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          filePreviewMenuItemActionPerformed(e);
        }
      });
      gbc.gridx = 3;
      gbc.gridy = 0;
      gbc.ipadx = 0;
      gbc.weightx = 0;
      settingPanel.add(previewBtn,gbc);

      JButton loadBtn = new JButton("Load");
      loadBtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          fileLoadMenuItemActionPerformed(e);
        }
      });
      gbc.gridx = 4;
      gbc.gridy = 0;
      gbc.weightx = 0;
      settingPanel.add(loadBtn,gbc);

      JButton saveBtn = new JButton("Save");
      saveBtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          fileSaveMenuItemActionPerformed(e);
        }
      });
      gbc.gridx = 5;
      gbc.gridy = 0;
      gbc.weightx = 0;
      settingPanel.add(saveBtn,gbc);

      innerPanel.add(settingPanel,BorderLayout.SOUTH);

      try {
        IStringScalar model = (IStringScalar)attList.add(settingManagerName+"/LastAppliedFile");
        settingFile.setModel(model);
        model.refresh();
      } catch (ConnectionException e) {}

      attList.startRefresher();

    }

  }

  private void loadSynoptic(String jdrawFullFileName) {

    try {
      tangoSynopHandler.setProgressListener(this);
      tangoSynopHandler.setSynopticFileName(jdrawFullFileName);
      splash.setMessage("Synoptic file loaded ...");
      tangoSynopHandler.setToolTipMode(TangoSynopticHandler.TOOL_TIP_NAME);
      tangoSynopHandler.setAutoZoom(true);
    } catch (FileNotFoundException fnfEx) {
      splash.setVisible(false);
      javax.swing.JOptionPane.showMessageDialog(
          null, "Cannot find the synoptic file : " + jdrawFullFileName + ".\n"
          + "Check the file name you entered;"
          + " Application will abort ...\n"
          + fnfEx,
          "No such file",
          javax.swing.JOptionPane.ERROR_MESSAGE);
      //System.exit(-1); don't exit if not standalone
      return;
    } catch (IllegalArgumentException illEx) {
      splash.setVisible(false);
      javax.swing.JOptionPane.showMessageDialog(
          null, "Cannot parse the synoptic file : " + jdrawFullFileName + ".\n"
          + "Check if the file is a Jdraw file."
          + " Application will abort ...\n"
          + illEx,
          "Cannot parse the file",
          javax.swing.JOptionPane.ERROR_MESSAGE);
      //System.exit(-1); don't exit if not standalone
      return;
    } catch (MissingResourceException mrEx) {
      splash.setVisible(false);
      javax.swing.JOptionPane.showMessageDialog(
          null, "Cannot parse the synoptic file : " + jdrawFullFileName + ".\n"
          + " Application will abort ...\n"
          + mrEx,
          "Cannot parse the file",
          javax.swing.JOptionPane.ERROR_MESSAGE);
      //System.exit(-1); don't exit if not standalone
      return;
    }
    setTrendAttributeList();
    splash.setVisible(false);

    fileLoaded = true;
    setTitle(jdrawFullFileName);

  }

  private void settingsAppliedPerformed(SettingsManagedEvent event) {
    String fileName = event.getFileName();
    switch (event.getAction()) {
      case SettingsManagerClient.APPLIED:
        //  Display applied results
        if (event.hasFailed()) {
          ErrorPane.showErrorMessage(new JFrame(),
              "Applying file " + fileName, event.getDevFailed());
        } else {
          JOptionPane.showMessageDialog(new JFrame(),
              "Settings loaded from  " + event.getFileName());
        }
        break;
      case SettingsManagerClient.GENERATED:
        //  Display generated results
        if (event.hasFailed()) {
          ErrorPane.showErrorMessage(new JFrame(),
              "Generated file " + fileName, event.getDevFailed());
        } else {
          JOptionPane.showMessageDialog(new JFrame(),
              "Settings saved in  " + event.getFileName());
        }
        break;
    }
  }

  private void setTrendAttributeList() {

    AttributeList attl = tangoSynopHandler.getAttributeList();

    for (int i = 0; i < attl.getSize(); i++) {
      IEntity ie = (IEntity) attl.get(i);
      try {
        numberAndStateScalarAttList.add(ie.getName());
      } catch (ConnectionException ex) {
      }
    }
    globalTrend.setModel(numberAndStateScalarAttList);

  }

  private void initComponents() {

    innerPanel = new JPanel();
    innerPanel.setLayout(new BorderLayout());
    tangoSynopHandler = new fr.esrf.tangoatk.widget.jdraw.TangoSynopticHandler();
    jMenuBar1 = new javax.swing.JMenuBar();
    fileJMenu = new javax.swing.JMenu();
    quitJMenuItem = new javax.swing.JMenuItem();
    viewMenu = new javax.swing.JMenu();
    trendMenuItem = new javax.swing.JMenuItem();
    errHistMenuItem = new javax.swing.JMenuItem();
    diagtMenuItem = new javax.swing.JMenuItem();

    getContentPane().setLayout(new java.awt.GridBagLayout());

    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        exitForm(evt);
      }
    });

    if (errorHistory != null) {
      try {
        tangoSynopHandler.setErrorHistoryWindow(errorHistory);
      } catch (Exception setErrwExcept) {
        System.out.println("Cannot set Error History Window");
      }
    }

    innerPanel.add(tangoSynopHandler, BorderLayout.CENTER);
    setContentPane(innerPanel);

    fileJMenu.setText("File");
    quitJMenuItem.setText("Quit");
    quitJMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        quitJMenuItemActionPerformed(evt);
      }
    });

    fileJMenu.add(quitJMenuItem);

    jMenuBar1.add(fileJMenu);

    viewMenu.setText("View");

    trendMenuItem.setText("Numeric & State Trend ");
    trendMenuItem.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            viewTrendActionPerformed(evt);
          }
        });
    viewMenu.add(trendMenuItem);

    viewMenu.add(errHistMenuItem);

    errHistMenuItem.setText("Error History ...");
    errHistMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        errHistMenuItemActionPerformed(evt);
      }
    });

    viewMenu.add(errHistMenuItem);

    diagtMenuItem.setText("Diagnostic ...");
    diagtMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        fr.esrf.tangoatk.widget.util.ATKDiagnostic.showDiagnostic();
      }
    });

    viewMenu.add(diagtMenuItem);
    jMenuBar1.add(viewMenu);

    setJMenuBar(jMenuBar1);

    pack();

  }

  private void viewTrendActionPerformed(java.awt.event.ActionEvent evt) {
    fr.esrf.tangoatk.widget.util.ATKGraphicsUtils.centerFrame(getRootPane(), trendFrame);
    trendFrame.setVisible(true);
  }

  private void errHistMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
    errorHistory.setVisible(true);
  }

  private void quitJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
    // TODO add your handling code here:
    stopSimpleSynopticAppli();
  }

  private void fileStatusMenuItemActionPerformed(java.awt.event.ActionEvent evt) {

    try {

      String status;

      DeviceAttribute da = sm.getManagerProxy().read_attribute("Status");
      status = da.extractString();

      da = sm.getManagerProxy().read_attribute("AlarmAttributes");
      if(da.getQuality() != AttrQuality.ATTR_INVALID) {
        String[] atts = da.extractStringArray();
        for(int i=0;i<atts.length;i++)
          status += "\n" + atts[0];
      }
      JOptionPane.showMessageDialog(this,status,"Status ["+settingManagerName+"]",JOptionPane.INFORMATION_MESSAGE);

    } catch (DevFailed ex) {
      ErrorPane.showErrorMessage(this, settingManagerName, ex);
    }

  }

  private void fileLoadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {

    try {
      sm.applySettings(this);
    } catch (DevFailed ex) {
      ErrorPane.showErrorMessage(this, settingManagerName, ex);
    }

  }

  private void fileSaveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {

    try {
      sm.generateSettingsFile(this);
    } catch (DevFailed ex) {
      ErrorPane.showErrorMessage(this,settingManagerName,ex);
    }

  }

  private void filePreviewMenuItemActionPerformed(java.awt.event.ActionEvent evt) {

    try {
      sm.viewSettingsFile(this);
    } catch (DevFailed ex) {
      ErrorPane.showErrorMessage(this,settingManagerName,ex);
    }

  }

  /**
   * Exit the Application
   */
  private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
    stopSimpleSynopticAppli();
  }//GEN-LAST:event_exitForm

  public void stopSimpleSynopticAppli() {

    if (standAlone == true)
      System.exit(0);
    else {

      tangoSynopHandler.getAttributeList().stopRefresher();

      if (globalTrend != null)
        globalTrend.clearModel();

      if(settingFile != null)
        settingFile.clearModel();

      this.dispose();
    }

  }

  private static void printUsage() {
    System.out.println("Usage: synopticAppli [-s settingsmanager] [jdwfile]");
    System.exit(0);
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {

    String fullFileName = null;
    SimpleSynopticAppli syApp;
    String settingManagerName = null;

    int argc = 0;
    while( argc<args.length ) {

      if(args[argc].equals("-s")) {
        argc++;
        if(argc<args.length)
          settingManagerName = args[argc];
        else
          printUsage();
        argc++;
      } else {
        if(fullFileName == null)
          fullFileName = args[argc];
        else
          printUsage();
        argc++;
      }

    }

    // If Synoptic file name is not specified, launch a file chooser window to let the user select the file name
    if (fullFileName == null) {

      JFileChooser chooser = new JFileChooser(".");
      chooser.setDialogTitle("[SimpleSynopticAppli] Open a synoptic file");
      JDFileFilter jdwFilter = new JDFileFilter("JDraw synoptic", new String[]{"jdw"});
      chooser.addChoosableFileFilter(jdwFilter);
      int returnVal = chooser.showOpenDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File f = chooser.getSelectedFile();
        fullFileName = f.getAbsolutePath();
      } else {
        System.exit(0);
      }

    }

    syApp = new SimpleSynopticAppli(fullFileName, settingManagerName, true);

    if (!syApp.fileLoaded) // failed to load the synoptic file : constructor failure
      System.exit(-1);

    ATKGraphicsUtils.centerFrameOnScreen(syApp);
    syApp.setVisible(true);

  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JMenuItem trendMenuItem;
  private javax.swing.JMenuItem errHistMenuItem;
  private javax.swing.JMenuItem diagtMenuItem;
  private javax.swing.JMenu fileJMenu;
  private javax.swing.JMenuBar jMenuBar1;
  private javax.swing.JMenuItem quitJMenuItem;
  private javax.swing.JMenuItem fileLoadMenuItem;
  private javax.swing.JMenuItem fileSaveMenuItem;
  private javax.swing.JMenuItem filePreviewMenuItem;
  private fr.esrf.tangoatk.widget.jdraw.TangoSynopticHandler tangoSynopHandler;
  private javax.swing.JMenu viewMenu;

  @Override
  public void progress(double p) {
    splash.progress((int)(p*100.0));
  }
  // End of variables declaration//GEN-END:variables

}
