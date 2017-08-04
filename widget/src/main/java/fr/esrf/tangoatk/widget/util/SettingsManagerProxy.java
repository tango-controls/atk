/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.esrf.tangoatk.widget.util;

import fr.esrf.Tango.DevFailed;
import fr.esrf.tangoatk.core.AttributeList;
import fr.esrf.tangoatk.core.CommandList;
import fr.esrf.tangoatk.core.ConnectionException;
import fr.esrf.tangoatk.core.Device;
import fr.esrf.tangoatk.core.DeviceFactory;
import fr.esrf.tangoatk.core.IAttribute;
import fr.esrf.tangoatk.core.ICommand;
import fr.esrf.tangoatk.core.IEntity;
import fr.esrf.tangoatk.core.IStringScalar;
import fr.esrf.tangoatk.core.command.ArrayVoidCommand;
import fr.esrf.tangoatk.core.command.ScalarScalarCommand;
import fr.esrf.tangoatk.core.command.StringVoidCommand;
import fr.esrf.tangoatk.widget.attribute.SimpleScalarViewer;
import fr.esrf.tangoatk.widget.attribute.StatusViewer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author poncet
 */
public class SettingsManagerProxy
{
    public static final String            ROOT_PATH_ATT_NAME = "SettingsPath";
    public static final String            SETTINGS_FILE_ATT_NAME = "LastAppliedFile";
    public static final String            LOAD_CMD_NAME = "ApplySettings";
    public static final String            SAVE_CMD_NAME = "GenerateSettingsFile";
    public static final String            READ_CONTENT_CMD_NAME = "GetSettingsFileContent";
    
    public static final String            STATUS_BUTTON = "Status";
    public static final String            LOAD_BUTTON = "Load";
    public static final String            SAVE_BUTTON = "Save";
    public static final String            PREVIEW_BUTTON = "Preview";
    
    
    private String                 devName = null;
    private Device                 settingsManagerDevice = null;
    
    private AttributeList          attl = new AttributeList();
    private CommandList            cmdl = new CommandList();
    
    private IStringScalar          rootPathAtt = null;
    private IStringScalar          settingsFileAtt = null;
    private IStringScalar          statusAtt = null;
    
    private ICommand               loadCmd = null;
    private ICommand               saveCmd = null;
    private ICommand               readContentCmd = null;
    private ErrorHistory           errh = new ErrorHistory();
    
    private LoadSaveFileHandler    lsfh = null;
    private JPanel                 settingsPanel = null;
    private SimpleScalarViewer     settingsFileSsv = null;
    private JFrame                 statusJFrame = null;
    private StatusViewer           statusv = null;
    
    private JButton                statusJButton = null;
    private JButton                loadJButton = null;
    private JButton                saveJButton = null;
    private JButton                previewJButton = null;
//    private FilePreviewWindow      previewer = null;
    
    public SettingsManagerProxy(String smDeviceName)
    {
        attl.addErrorListener(errh);
        cmdl.addErrorListener(errh);
        cmdl.addErrorListener(ErrorPopup.getInstance());
        
        if (smDeviceName != null)
        {
            try
            {
                settingsManagerDevice = DeviceFactory.getInstance().getDevice(smDeviceName);
                devName = smDeviceName;
            }
            catch (ConnectionException ex) {}
        }
        if (devName == null)
        {
            javax.swing.JOptionPane.showMessageDialog(
                        null, "Cannot connect to the device.\n"
                                + "Check the setting manager device name you entered.",
                        "Connection to device failed",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        
        String className = null;
        try
        {
            className = settingsManagerDevice.get_class_name();
        }
        catch (DevFailed ex) {}
        
        if (className == null)
        {
            javax.swing.JOptionPane.showMessageDialog(
                        null, "Failed to get the class name of the device.\n"
                                + "Check the setting manager device name you entered.",
                        "Get class name failed",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        
        if (!className.equalsIgnoreCase("SettingsManager"))
        {
            javax.swing.JOptionPane.showMessageDialog(
                        null, "The device is not a SettingsManager.\n"
                                + "Check the setting manager device name you entered.",
                        "Bad class name",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            className = null;
        }
        
        if (className != null)
        {
            IEntity ie;
            String  entityName=null;
            
            try
            {
                entityName = devName+"/Status";
                ie = attl.add(entityName);
                if (ie instanceof IStringScalar)
                    statusAtt = (IStringScalar) ie;
                
                entityName = devName+"/"+ ROOT_PATH_ATT_NAME;
                ie = attl.add(entityName);
                if (ie instanceof IStringScalar)
                    rootPathAtt = (IStringScalar) ie;
                
                entityName = devName+"/"+ SETTINGS_FILE_ATT_NAME;
                ie = attl.add(entityName);
                if (ie instanceof IStringScalar)
                    settingsFileAtt = (IStringScalar) ie;
            }
            catch (ConnectionException ex)
            {
                javax.swing.JOptionPane.showMessageDialog(
                            null, "Cannot connect to the SettingsManager device.\n"
                                    + "Failed to connect to the attribute : "+entityName,
                            "Connection failed",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
            }
                
            try
            {
                entityName = devName+"/"+ LOAD_CMD_NAME;
                ie = cmdl.add(entityName);
                if (ie instanceof StringVoidCommand)
                    loadCmd = (ICommand) ie;

                entityName = devName+"/"+ SAVE_CMD_NAME;
                ie = cmdl.add(entityName);
                if (ie instanceof ArrayVoidCommand)
                    saveCmd = (ICommand) ie;

                entityName = devName+"/"+ READ_CONTENT_CMD_NAME;
                ie = cmdl.add(entityName);
                if (ie instanceof ScalarScalarCommand)
                    readContentCmd = (ICommand) ie;
            }
            catch (Exception ex) {}
        }
        attl.startRefresher();
        createLoadSaveFileHandler();
        createSettingsPanel();
    }
    
    
    private void createLoadSaveFileHandler()
    {
        lsfh = new LoadSaveFileHandler();
        lsfh.setFileAuthor("ATK SettingsManagerProxy class");
        lsfh.setLoadButtonText("Load Settings");
        lsfh.setSaveButtonText("Save Settings");
        
        lsfh.setRootFolderModel(rootPathAtt);
        lsfh.setLoadCmdModel(loadCmd);
        lsfh.setSaveCmdModel(saveCmd);
        lsfh.setReadFileContentCmd(readContentCmd);
        
        String  rootPath = null;
        if (lsfh.getRootFolderModel() != null)
        {
            rootPath = lsfh.getRootFolderModel().getStringValue();
        }
        SmSaveFileChooser ssfc = new SmSaveFileChooser(rootPath);
        lsfh.setSaveJFileChooser(ssfc);
    }
    
    
    private void createSettingsPanel()
    {
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(3, 3, 3, 3);

        JLabel fileLabel = new JLabel("Configuration file");
//        fileLabel.setFont(ATKConstant.labelFont);
        fileLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        settingsPanel.add(fileLabel, gbc);

        settingsFileSsv = new SimpleScalarViewer();
        settingsFileSsv.setBackgroundColor(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.ipadx = 100;
        settingsPanel.add(settingsFileSsv, gbc);
        if(settingsFileAtt != null)
        {
            settingsFileSsv.setHasToolTip(true);
            settingsFileSsv.setModel(settingsFileAtt);
        }

        statusJButton = new JButton("Status");
        statusJButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        fileStatusButtonPerformed(e);
                    }
                });
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.ipadx = 0;
        gbc.weightx = 0;
        settingsPanel.add(statusJButton, gbc);

        previewJButton = new JButton("Preview");
        previewJButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        filePreviewButtonActionPerformed(e);
                    }
                });
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.ipadx = 0;
        gbc.weightx = 0;
        settingsPanel.add(previewJButton, gbc);

        loadJButton = new JButton("Load");
        loadJButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        fileLoadButtonActionPerformed(e);
                    }
                });
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0;
        settingsPanel.add(loadJButton, gbc);

        saveJButton = new JButton("Save");
        saveJButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        fileSaveButtonActionPerformed(e);
                    }
                });
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.weightx = 0;
        settingsPanel.add(saveJButton, gbc);
        
        statusJFrame = new JFrame();
        statusJFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        statusJFrame.getContentPane().setLayout(new GridBagLayout());
        statusv = new StatusViewer();
        gbc = new GridBagConstraints();        
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        statusJFrame.getContentPane().add(statusv, gbc);
        statusJFrame.setPreferredSize(new Dimension(700, 400));
        if (statusAtt != null)
            statusv.setModel(statusAtt);
        statusJFrame.pack();
    }

    private void fileStatusButtonPerformed(ActionEvent   evt)
    {
        if (statusJFrame == null) return;
        ATKGraphicsUtils.centerFrame(settingsPanel, statusJFrame);
        statusJFrame.setVisible(true);
    }

    private void filePreviewButtonActionPerformed(ActionEvent   evt)
    {
        previewSettingsFile();
    }

    private void fileLoadButtonActionPerformed(ActionEvent   evt)
    {
        loadSettingsFile();
    }

    private void fileSaveButtonActionPerformed(ActionEvent   evt)
    {
        saveSettingsFile();
    }
    
    public JPanel getSettingsPanel()
    {
        return settingsPanel;
    }
    
    public ErrorHistory getErrorHistoryWindow()
    {
        return errh;
    }
    
    public void setErrorHistoryWindow(ErrorHistory errorWindow)
    {
        if (errorWindow == null) return;
        if (errorWindow == errh) return;
        if (errh != null)
        { // remove errh from the list of the erroListeners of all entities.
            Object obj = null;
            for (int i=0; i<attl.size(); i++)
            {
                obj = attl.get(i);
                if (obj instanceof IAttribute)
                {
                    IAttribute ia = (IAttribute) obj;
                    ia.removeErrorListener(errh);
                }
            }
            for (int i=0; i<cmdl.size(); i++)
            {
                obj = cmdl.get(i);
                if (obj instanceof ICommand)
                {
                    ICommand ic = (ICommand) obj;
                    ic.removeErrorListener(errh);
                }
            }
            errh = null;
        }
        
        errh = errorWindow;
        AttributeList  oldAttl = attl;
        CommandList    oldCmdl = cmdl;
        
        attl = new AttributeList();
        cmdl = new CommandList();
        attl.addErrorListener(errh);
        cmdl.addErrorListener(errh);
        
        for (int i=0; i<oldAttl.size(); i++)
        {
            IAttribute ia = (IAttribute) oldAttl.get(i);
            attl.add(ia);
        }
        
        for (int i=0; i<oldCmdl.size(); i++)
        {
            ICommand ic = (ICommand) oldCmdl.get(i);
            cmdl.add(ic);
        }
        
        oldAttl.stopRefresher();
        oldAttl.clear();
        oldCmdl.clear();
        
        attl.startRefresher();
    }
    
    public void clearModels()
    {
        if (statusv != null)
            statusv.clearModel();
        if (settingsFileSsv != null)
            settingsFileSsv.clearModel();
        if (lsfh != null)
            lsfh.clearModels();

        attl.stopRefresher();
        attl.clear();
        cmdl.clear();
    }
    
    public void settingsPanelHideChild(String  childName)
    {
        if (childName.equalsIgnoreCase(STATUS_BUTTON))
        {
            if (statusJButton != null)
                statusJButton.setVisible(false);
            return;
        }
        if (childName.equalsIgnoreCase(LOAD_BUTTON))
        {
            if (loadJButton != null)
                loadJButton.setVisible(false);
            return;
        }
        if (childName.equalsIgnoreCase(SAVE_BUTTON))
        {
            if (saveJButton != null)
                saveJButton.setVisible(false);
            return;
        }
        if (childName.equalsIgnoreCase(PREVIEW_BUTTON))
        {
            if (previewJButton != null)
                previewJButton.setVisible(false);
        }
    }
    
    public void settingsPanelShowChild(String  buttonName)
    {
        if (buttonName.equalsIgnoreCase(STATUS_BUTTON))
        {
            if (statusJButton != null)
                statusJButton.setVisible(true);
            return;
        }
        if (buttonName.equalsIgnoreCase(LOAD_BUTTON))
        {
            if (loadJButton != null)
                loadJButton.setVisible(true);
            return;
        }
        if (buttonName.equalsIgnoreCase(SAVE_BUTTON))
        {
            if (saveJButton != null)
                saveJButton.setVisible(true);
            return;
        }
        if (buttonName.equalsIgnoreCase(PREVIEW_BUTTON))
        {
            if (previewJButton != null)
                previewJButton.setVisible(true);
        }
    }
    
    
    public void setLoadButtonText(String loadLabel)
    {
        if (lsfh == null) return;
        lsfh.setLoadButtonText(loadLabel);
    }
     
    public void setSaveButtonText(String saveLabel)
    {
        if (lsfh == null) return;
        lsfh.setSaveButtonText(saveLabel);
    }
   
    public String getFileAuthor()
    {
        if (lsfh == null) return null;
        return (lsfh.getFileAuthor());
    }
    
    
    public void setFileAuthor(String fa)
    {
        if (lsfh == null) return;
        lsfh.setFileAuthor(fa);
    }
    
    public String getFileComments()
    {
        if (lsfh == null) return null;
        return (lsfh.getFileComments());
    }
    
    
    public void setFileComments(String fc)
    {
        if (lsfh == null) return;
        lsfh.setFileComments(fc);
    }

  /**
   * Load a settings file
   */
    public void loadSettingsFile()
    {
        if (lsfh == null) return;
        lsfh.loadFile();
    }

  /**
   * Save (Generate) a settings file
   */
    public void saveSettingsFile()
    {
        if (lsfh == null) return;
        lsfh.saveFile();
    }

  /**
   * Preview (content of) a settings file
   */
    public void previewSettingsFile()
    {
        if (lsfh == null) return;
        lsfh.previewFile();
    }
    
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                // Create the SettingsManagerProxy and errorWindow
                final ErrorHistory  errorWindow = new ErrorHistory();
                
                final SettingsManagerProxy  smp = new SettingsManagerProxy("//acudebian7:10000/sys/settings/syco");
                smp.setErrorHistoryWindow(errorWindow);
                smp.setFileAuthor("Test");
                smp.setFileComments("Encore test");
                smp.setLoadButtonText("Load it");
                smp.setSaveButtonText("Save it");
                // use the calls to smp.loadSettingsFile(), smp.saveSettingsFile(), smp.previewSettingsFile()
                // to implement the JMenuItem Action Listeners inside your application

                
//                smp.settingsPanelHideChild(SettingsManagerProxy.STATUS_BUTTON);
//                smp.settingsPanelHideChild(SettingsManagerProxy.LOAD_BUTTON);
//                smp.settingsPanelHideChild(SettingsManagerProxy.SAVE_BUTTON);
//                smp.settingsPanelHideChild(SettingsManagerProxy.PREVIEW_BUTTON);
                
                
                
                // Create window to test. Note the actionPerformed associated to each menu item
                JFrame jf = new JFrame();
                jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                
                jf.getContentPane().setLayout(new java.awt.BorderLayout());
                jf.getContentPane().add(new JPanel(), java.awt.BorderLayout.CENTER);
                // Use the call to smp.getSettingsPanel() to add the settingManager panel to your window
                if (smp.getSettingsPanel() != null)
                    jf.getContentPane().add(smp.getSettingsPanel(), java.awt.BorderLayout.SOUTH);
                
                javax.swing.JMenuBar    jMenuBar1 = new javax.swing.JMenuBar();
                javax.swing.JMenu       fileJMenu = new javax.swing.JMenu();
                
                // Setting manager file menu
                fileJMenu.setText("File");
                jMenuBar1.add(fileJMenu);
                javax.swing.JMenuItem fileLoadMenuItem = new javax.swing.JMenuItem("Load...");
                fileLoadMenuItem.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        smp.loadSettingsFile();
                    }
                });
                fileJMenu.add(fileLoadMenuItem);

                javax.swing.JMenuItem filePreviewMenuItem = new javax.swing.JMenuItem("Preview...");
                filePreviewMenuItem.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        smp.previewSettingsFile();
                    }
                });
                fileJMenu.add(filePreviewMenuItem);
                
                javax.swing.JMenuItem fileSaveMenuItem = new javax.swing.JMenuItem("Save...");
                fileSaveMenuItem.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        smp.saveSettingsFile();
                    }
                });
                fileJMenu.add(fileSaveMenuItem);
                
                javax.swing.JMenuItem quitJMenuItem = new javax.swing.JMenuItem("Quit");
                quitJMenuItem.addActionListener(new java.awt.event.ActionListener()
                {
                    public void actionPerformed(java.awt.event.ActionEvent evt)
                    {
                        System.exit(0);
                    }
                });
                fileJMenu.add(new javax.swing.JSeparator());
                fileJMenu.add(quitJMenuItem);
                
                // View menu for erros and AtkDiagnostic
                javax.swing.JMenu       viewMenu = new javax.swing.JMenu();
                viewMenu.setText("View");
                jMenuBar1.add(viewMenu);
            
                javax.swing.JMenuItem errorHistoryMenuItem = new javax.swing.JMenuItem("Error History...");
                errorHistoryMenuItem.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        ATKGraphicsUtils.centerFrameOnScreen(errorWindow);
                        errorWindow.setVisible(true);
                    }
                });
                viewMenu.add(errorHistoryMenuItem);
            
                javax.swing.JMenuItem atkDiagMenuItem = new javax.swing.JMenuItem("ATK Diagnostic...");
                atkDiagMenuItem.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        ATKDiagnostic.showDiagnostic();
                    }
                });
                viewMenu.add(atkDiagMenuItem);
                
                jf.setJMenuBar(jMenuBar1);
                jf.pack();
                jf.setVisible(true);
                
            }
        });
    }
}

