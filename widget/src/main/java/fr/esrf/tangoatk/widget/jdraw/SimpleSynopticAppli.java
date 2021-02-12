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

import fr.esrf.tangoatk.core.AttributeList;
import fr.esrf.tangoatk.core.AttributePolledList;
import fr.esrf.tangoatk.core.ConnectionException;
import fr.esrf.tangoatk.core.IDevStateScalar;
import fr.esrf.tangoatk.core.IEntity;
import fr.esrf.tangoatk.core.IEntityFilter;
import fr.esrf.tangoatk.core.INumberScalar;
import fr.esrf.tangoatk.widget.attribute.Trend;
import java.io.*;
import java.util.*;
import fr.esrf.tangoatk.widget.util.ErrorHistory;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import fr.esrf.tangoatk.widget.util.SettingsManagerProxy;
import fr.esrf.tangoatk.widget.util.Splash;
import fr.esrf.tangoatk.widget.util.jdraw.JDFileFilter;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * @author PONCET
 */
public class SimpleSynopticAppli extends javax.swing.JFrame implements SynopticProgressListener
{
    private final Splash             splash = new Splash();

    private ErrorHistory             errorHistory;
    private boolean                  standAlone = false;
    private boolean                  fileLoaded = false;
    private String                   settingManagerName = null;
    private SettingsManagerProxy     smProxy = null;

    private AttributePolledList      numberAndStateScalarAttList; /* used in the global trend */
    
    private JFrame                   trendFrame;
    private JPanel                   mainPanel;
    private Trend                    globalTrend = null;
    
    /**
     * Creates new form SimpleSynopticAppli
     */
    public SimpleSynopticAppli()
    {
        fileLoaded = false;
        standAlone = false;
        errorHistory = new ErrorHistory();
        splash.setTitle("SimpleSynopticAppli  ");
        splash.setCopyright("(c) ESRF 2003-2017");
        splash.setMessage("Loading synoptic ...");
        splash.initProgress();
        splash.setMaxProgress(100);
        splash.setVisible(true);

        numberAndStateScalarAttList = new fr.esrf.tangoatk.core.AttributePolledList();
        numberAndStateScalarAttList.setFilter(new IEntityFilter()
        {
            public boolean keep(IEntity entity)
            {
                if (    (entity instanceof INumberScalar)
                     || (entity instanceof IDevStateScalar))
                {
                    return true;
                }
                return false;
            }
        });

        trendFrame = new JFrame();
        javax.swing.JPanel trendPanel = new javax.swing.JPanel();
        trendPanel.setLayout(new BorderLayout());
        trendPanel.setPreferredSize(new java.awt.Dimension(600, 300));
        globalTrend = new Trend(trendFrame);
        trendPanel.add(globalTrend, BorderLayout.CENTER);
        trendFrame.setContentPane(trendPanel);
        trendFrame.pack();

        initComponents();
    }

    public SimpleSynopticAppli(String jdrawFullFileName)
    {
        this();
        loadSynoptic(jdrawFullFileName);
        pack();
        setVisible(true);
    }

    public SimpleSynopticAppli(String jdrawFullFileName, boolean stand)
    {
        this();
        loadSynoptic(jdrawFullFileName);
        standAlone = stand;
    }

    public SimpleSynopticAppli(String jdrawFullFileName, String settingManagerName, boolean stand)
    {
        this(jdrawFullFileName, stand);
        this.settingManagerName = settingManagerName;
        addSettingsManagerSupport();
    }
    
    private void addSettingsManagerSupport()
    {
        if (settingManagerName == null) return;
        
        smProxy = new SettingsManagerProxy(settingManagerName);
        smProxy.setErrorHistoryWindow(errorHistory); 
        String username = System.getProperty("user.name");

        String author =  settingManagerName.substring(settingManagerName.lastIndexOf("/")+1)
                       + " Synoptic ("
                       + username + ")";
//        System.out.println(author);
        smProxy.setFileAuthor(author);
        
        // Build Setting manager file menu
        fileLoadMenuItem = new JMenuItem("Load...");
        fileLoadMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                smProxy.loadSettingsFile();
            }
        });
        
        filePreviewMenuItem = new JMenuItem("Preview...");
        filePreviewMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                smProxy.previewSettingsFile();
            }
        });
        
        fileSaveMenuItem = new JMenuItem("Save...");
        fileSaveMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                smProxy.saveSettingsFile();
            }
        });
        
        // add menu items at the top
        fileJMenu.add(new JSeparator(), 0);
        fileJMenu.add(fileSaveMenuItem, 0);
        fileJMenu.add(filePreviewMenuItem, 0);
        fileJMenu.add(fileLoadMenuItem, 0);
        
        // Add Setting manager panel provided by the settingManagerProxy
        if (smProxy.getSettingsPanel() != null)
            mainPanel.add(smProxy.getSettingsPanel(), java.awt.BorderLayout.SOUTH);        
    }

    private void loadSynoptic(String jdrawFullFileName)
    {
        try
        {
            tangoSynopHandler.setProgressListener(this);
            tangoSynopHandler.setSynopticFileName(jdrawFullFileName);
            splash.setMessage("Synoptic file loaded ...");
            tangoSynopHandler.setToolTipMode(TangoSynopticHandler.TOOL_TIP_NAME);
            if(tangoSynopHandler.isAutoZoomAsked())
              tangoSynopHandler.setAutoZoom(true);
        }
        catch (FileNotFoundException fnfEx)
        {
            splash.setVisible(false);
            javax.swing.JOptionPane.showMessageDialog(
                    null, "Cannot find the synoptic file : " + jdrawFullFileName + ".\n"
                    + "Check the file name you entered;"
                    + " Application will abort ...\n"
                    + fnfEx,
                    "No such file",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            //System.exit(-1); don't exit if not standalone
            splash.setVisible(false);
            return;
        }
        catch (IllegalArgumentException illEx)
        {
            splash.setVisible(false);
            javax.swing.JOptionPane.showMessageDialog(
                    null, "Cannot parse the synoptic file : " + jdrawFullFileName + ".\n"
                    + "Check if the file is a Jdraw file."
                    + " Application will abort ...\n"
                    + illEx,
                    "Cannot parse the file",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            //System.exit(-1); don't exit if not standalone
            splash.setVisible(false);
            return;
        }
        catch (MissingResourceException mrEx)
        {
            splash.setVisible(false);
            javax.swing.JOptionPane.showMessageDialog(
                    null, "Cannot parse the synoptic file : " + jdrawFullFileName + ".\n"
                    + " Application will abort ...\n"
                    + mrEx,
                    "Cannot parse the file",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            //System.exit(-1); don't exit if not standalone
            splash.setVisible(false);
            return;
        }
        setTrendAttributeList();
        splash.setVisible(false);

        fileLoaded = true;
        setTitle(jdrawFullFileName);

    }

    private void setTrendAttributeList()
    {
        AttributeList attl = tangoSynopHandler.getAttributeList();

        for (int i = 0; i < attl.getSize(); i++)
        {
            IEntity ie = (IEntity) attl.get(i);
            try
            {
                numberAndStateScalarAttList.add(ie.getName());
            }
            catch (ConnectionException ex) {}
        }
        globalTrend.setModel(numberAndStateScalarAttList);
    }

    private void initComponents()
    {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        tangoSynopHandler = new fr.esrf.tangoatk.widget.jdraw.TangoSynopticHandler();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileJMenu = new javax.swing.JMenu();
        quitJMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        trendMenuItem = new javax.swing.JMenuItem();
        errHistMenuItem = new javax.swing.JMenuItem();
        diagtMenuItem = new javax.swing.JMenuItem();

        addWindowListener(new java.awt.event.WindowAdapter()
                                {
                                    public void windowClosing(java.awt.event.WindowEvent evt)
                                    {
                                        exitForm(evt);
                                    }
                                });

        if (errorHistory != null)
        {
            try
            {
                tangoSynopHandler.setErrorHistoryWindow(errorHistory);
            }
            catch (Exception setErrwExcept)
            {
                System.out.println("Cannot set Error History Window");
            }
        }

        mainPanel.add(tangoSynopHandler, BorderLayout.CENTER);
        setContentPane(mainPanel);

        fileJMenu.setText("File");
        quitJMenuItem.setText("Quit");
        quitJMenuItem.addActionListener(new java.awt.event.ActionListener()
                        {
                            public void actionPerformed(java.awt.event.ActionEvent evt)
                            {
                                quitJMenuItemActionPerformed(evt);
                            }
                        });

        fileJMenu.add(quitJMenuItem);

        jMenuBar1.add(fileJMenu);

        viewMenu.setText("View");

        trendMenuItem.setText("Numeric & State Trend ");
        trendMenuItem.addActionListener( new java.awt.event.ActionListener()
                        {
                            public void actionPerformed(java.awt.event.ActionEvent evt)
                            {
                                viewTrendActionPerformed(evt);
                            }
                        });
        viewMenu.add(trendMenuItem);

        viewMenu.add(errHistMenuItem);

        errHistMenuItem.setText("Error History ...");
        errHistMenuItem.addActionListener(new java.awt.event.ActionListener()
                        {
                            public void actionPerformed(java.awt.event.ActionEvent evt)
                            {
                                errHistMenuItemActionPerformed(evt);
                            }
                        });

        viewMenu.add(errHistMenuItem);

        diagtMenuItem.setText("Diagnostic ...");
        diagtMenuItem.addActionListener(new java.awt.event.ActionListener()
                        {
                            public void actionPerformed(java.awt.event.ActionEvent evt)
                            {
                                fr.esrf.tangoatk.widget.util.ATKDiagnostic.showDiagnostic();
                            }
                        });

        viewMenu.add(diagtMenuItem);
        jMenuBar1.add(viewMenu);

        setJMenuBar(jMenuBar1);

        pack();

    }

    private void viewTrendActionPerformed(java.awt.event.ActionEvent evt)
    {
        fr.esrf.tangoatk.widget.util.ATKGraphicsUtils.centerFrame(getRootPane(), trendFrame);
        trendFrame.setVisible(true);
    }

    private void errHistMenuItemActionPerformed(java.awt.event.ActionEvent evt)
    {
        errorHistory.setVisible(true);
    }

    private void quitJMenuItemActionPerformed(java.awt.event.ActionEvent evt)
    {
        // TODO add your handling code here:
        stopSimpleSynopticAppli();
    }


  /**
   * Exit the Application
   */
    private void exitForm(java.awt.event.WindowEvent evt)
    {//GEN-FIRST:event_exitForm
        stopSimpleSynopticAppli();
    }//GEN-LAST:event_exitForm
    

    public void stopSimpleSynopticAppli()
    {
        if (standAlone == true)
            System.exit(0);
        else
        {
            tangoSynopHandler.getAttributeList().stopRefresher();

            if (globalTrend != null)
                globalTrend.clearModel();
            if (smProxy != null)
                smProxy.clearModels();

            this.dispose();
        }

    }
    
    @Override
    public void progress(double p)
    {
        splash.progress((int) (p * 100.0));
    }

    private static void printUsage()
    {
        System.out.println("Usage: synopticAppli [-s settingsmanager] [jdwfile]");
        System.exit(0);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {

        String fullFileName = null;
        SimpleSynopticAppli syApp;
        String settingManagerName = null;
        
        int argc = 0;
        while (argc < args.length)
        {
            if (args[argc].equals("-s"))
            {
                argc++;
                if (argc < args.length)
                    settingManagerName = args[argc];
                else
                    printUsage();
                argc++;
            }
            else
            {
                if (fullFileName == null)
                    fullFileName = args[argc];
                else
                    printUsage();
                argc++;
            }
        }

        // If Synoptic file name is not specified, launch a file chooser window to let the user select the file name
        if (fullFileName == null)
        {
//            fullFileName = "/segfs/tango/tmp/poncet/syquad.jdw";
//            settingManagerName = "sys/settings/sy-quad";
//            
            JFileChooser chooser = new JFileChooser(".");
            chooser.setDialogTitle("[SimpleSynopticAppli] Open a synoptic file");
            JDFileFilter jdwFilter = new JDFileFilter("JDraw synoptic", new String[]{"jdw"});
            chooser.addChoosableFileFilter(jdwFilter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File f = chooser.getSelectedFile();
                fullFileName = f.getAbsolutePath();
            }
            else
            {
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

    // End of variables declaration//GEN-END:variables

}
