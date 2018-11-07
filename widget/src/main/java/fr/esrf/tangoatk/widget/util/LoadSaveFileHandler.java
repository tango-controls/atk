/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.esrf.tangoatk.widget.util;

import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.ICommand;
import fr.esrf.tangoatk.core.IResultListener;
import fr.esrf.tangoatk.core.IStringScalar;
import fr.esrf.tangoatk.core.ResultEvent;
import fr.esrf.tangoatk.core.command.ArrayVoidCommand;
import fr.esrf.tangoatk.core.command.StringVoidCommand;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author poncet
 */
public class LoadSaveFileHandler implements IResultListener
{
    private PreviewFileChooser            pfc = null;
    private JFileChooser                  saveFileChooser = null;
    private FilePreviewWindow             previewer = null;
    
    private String                        loadButtonText = "Load ...";
    private String                        saveButtonText = "Save ...";
    private String                        previewButtonText = "Preview ...";
    
    private String                        canonicalRootPath = null;
    
    private String                        fileAuthor = null;  // to be written in the file
    private String                        fileComments = null;  // to be written in the file
    
    private IStringScalar                 rootFolderModel = null;
    private ICommand                      saveFileCmd = null; 
    private ICommand                      loadFileCmd = null; 
    private ICommand                      readFileContentCmd = null;

    private boolean                       relativeFileName = true;
    
    private String                        fileContent = null;

    private LoadSaveFileListener          listener = null;
    private SettingsManagerProxy          parent = null;

    public LoadSaveFileHandler ()
    {
        pfc = new PreviewFileChooser();
        pfc.setContentVisible(true);
        saveFileChooser = pfc;
        previewer = new FilePreviewWindow();
    }
       
    public LoadSaveFileHandler (boolean relFileName)
    {
        this();
        relativeFileName = relFileName;
    }
    
    public LoadSaveFileHandler (String rootDirPath)
    {
        pfc = new PreviewFileChooser(rootDirPath);
        pfc.setContentVisible(true);
        saveFileChooser = pfc;
        // Get conninical path of the root path
        File sDir = new File(pfc.getRootDirectory());
        try
        {
            canonicalRootPath = sDir.getCanonicalPath();
        }
        catch (IOException ioex)
        {          
        }
    }
    
    public LoadSaveFileHandler (String rootDirPath, boolean relFileName)
    {
        this(rootDirPath);
        relativeFileName = relFileName;
    }

    public void setParent(SettingsManagerProxy parent) {
      this.parent = parent;
    }
    
    public void clearModels()
    {
        if (readFileContentCmd != null)
            readFileContentCmd.removeResultListener(this);
        
        rootFolderModel = null;
        saveFileCmd = null;
        loadFileCmd = null;
        readFileContentCmd = null;
    }

    public void setLoadSaveListener(LoadSaveFileListener l) {
      listener = l;
    }

    public void setSaveJFileChooser(JFileChooser jfc)
    {
        if (jfc == null) return;
        saveFileChooser = jfc;
    }
    
    public void setLoadButtonText(String loadLabel)
    {
        loadButtonText = loadLabel;
    }
     
    public void setSaveButtonText(String saveLabel)
    {
        saveButtonText = saveLabel;
    }
   
    public String getFileAuthor()
    {
        return (fileAuthor);
    }
    
    
    public void setFileAuthor(String fa)
    {
        if (fa == null) return;
        if (fa.length() < 1) return;
        fileAuthor = fa;
    }
    
    public String getFileComments()
    {
        return (fileComments);
    }
    
    
    public void setFileComments(String fc)
    {
        if (fc == null) return;
        if (fc.length() < 1) return;
        fileComments = fc;
    }
    
     
    private void setRootDirectory(String dirPath)
    {
        if (rootFolderModel != null) return; // in this case only root folder attribute can change the root directory
        
        pfc.setRootDirectory(dirPath);
        canonicalRootPath = null;
        File sDir = new File(pfc.getRootDirectory());
        try
        {
            canonicalRootPath = sDir.getCanonicalPath();
        }
        catch (IOException ioex)
        {          
        }
    }
    
    //Lazily, do not register as an IStringScalarListener and do not listen to changes on this attribute
    //Will get the value of the attribute on an Synchron Read at the setRootFolderModel method invoke
    public void setRootFolderModel(IStringScalar  folderNameAtt)
    {
        if (folderNameAtt == null) return;
        rootFolderModel = null;
        
        folderNameAtt.refresh();
        String rootFolderName = folderNameAtt.getStringValue();
        
        setRootDirectory(rootFolderName);        
        rootFolderModel = folderNameAtt;
    }
    
    public IStringScalar getRootFolderModel()
    {
        return rootFolderModel;
    }
    
    
    public void setLoadCmdModel(ICommand  loadCmd)
    {
        if (loadCmd == null) return;
        if ( !(loadCmd instanceof StringVoidCommand) ) return;
        
        loadFileCmd = loadCmd;
    }
    
    
    public void setSaveCmdModel(ICommand  saveCmd)
    {
        if (saveCmd == null) return;
        if (    !(saveCmd instanceof StringVoidCommand) 
             && !(saveCmd instanceof ArrayVoidCommand)  ) return;
        
        saveFileCmd = saveCmd;
    }
    
    
    public void setReadFileContentCmd(ICommand  getFileContentCmd)
    {
        if (getFileContentCmd == null) return;
        if ( !(getFileContentCmd instanceof fr.esrf.tangoatk.core.command.ScalarScalarCommand) ) return;
        
        readFileContentCmd = getFileContentCmd;
        readFileContentCmd.addResultListener(this);
    }


    private String getRelativeFileName(File file)
    {
        String selectedFilePath = null;
        int rootPathLength = 0, filePathLength = 0, increment = 1;

        if (canonicalRootPath == null || canonicalRootPath.length() <= 0)
        {
            if (pfc.getRootDirectory() == null)
                javax.swing.JOptionPane.showMessageDialog(
                        pfc.getRootPane(), "Root folder is not set.\n",
                        "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            return null;
        }

        try
        {
            selectedFilePath = file.getCanonicalPath();
        }
        catch (IOException ioex)
        {
            javax.swing.JOptionPane.showMessageDialog(
                    pfc.getRootPane(), "Failed to get the canonical path of the selected file.\n"
                    + ioex.getMessage(),
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return null;
        }
        

        if (!selectedFilePath.startsWith(canonicalRootPath))
        {
            javax.swing.JOptionPane.showMessageDialog(
                    pfc.getRootPane(), "The selected file is not inside the authorized root directory.\n\n"
                    + "The file should be located in  " + pfc.getRootDirectory() + "  directory tree.\n\n",
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        rootPathLength = canonicalRootPath.length();

        if (canonicalRootPath.endsWith(File.separator))
            increment = 0;
        else
            increment = 1;
        
        filePathLength = selectedFilePath.length();


        if ((rootPathLength + increment) >= filePathLength)
        {
            javax.swing.JOptionPane.showMessageDialog(
                    pfc.getRootPane(), "Invalid file name :" + selectedFilePath + ".\n",
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return selectedFilePath.substring(rootPathLength + increment);
    }

    private void loadFile(File file)
    {
        String filePath = null;
        if (relativeFileName)
            filePath = getRelativeFileName(file);
        else
            filePath = file.getAbsolutePath();
        if (filePath == null) return;
        if (loadFileCmd == null) return;
        
        
        ArrayList<String>  listArg = new ArrayList<String>();
        listArg.add(filePath);
//        System.out.println("Will execute "+loadFileCmd.getName());
        loadFileCmd.execute(listArg);
    }
    
    // Input argument of the command is an array of Strings and follows the input argument
    // conventions made by the Tango Setting Manager device server which are
    // One of the strings in the array should be File: followed by the relative file path
    // The additionnal strings can be given for the header of the file, such as Author and comments;
    private void settingManagerSaveFile (String relativeFilePath)
    {
        ArrayList<String>  listArg = new ArrayList<String>();
        
        int i = 0;
        listArg.add(i, "FILE:"+relativeFilePath);
        i++;
        if (fileAuthor != null)
        {
            listArg.add(i, "AUTHOR: "+fileAuthor);
            i++;
        }
        if (fileComments != null)
        {
            listArg.add(i, "COMMENTS: "+fileComments);
            i++;
        }
        
        
//        System.out.println("Will execute "+saveFileCmd.getName());
        saveFileCmd.execute(listArg);
    }
    
    
    private void saveFile(File  file)
    {
        String filePath = null;
        if (relativeFileName)
            filePath = getRelativeFileName(file);
        else
            filePath = file.getAbsolutePath();
        if (filePath == null) return;
        if (saveFileCmd == null) return;

        if (saveFileChooser instanceof SmSaveFileChooser)
        {
            SmSaveFileChooser ssfc = (SmSaveFileChooser) saveFileChooser;
            setFileAuthor(ssfc.getAuthorText());
            setFileComments(ssfc.getCommentsText());
        }
        
        if (saveFileCmd instanceof ArrayVoidCommand)
            settingManagerSaveFile(filePath);
        else
        {
            ArrayList<String>  listArg = new ArrayList<String>();
            listArg.add(filePath);
//            System.out.println("Will execute "+saveFileCmd.getName());
            saveFileCmd.execute(listArg);
        }
    }
    
    
    private void readFile(File  file)
    {
        if (readFileContentCmd == null) return;
        
        String filePath = null;
        if (relativeFileName)
            filePath = getRelativeFileName(file);
        else
            filePath = file.getAbsolutePath();
        if (filePath == null) return;
        
        fileContent = null;
        previewer.setText(fileContent);

        ArrayList<String>  listArg = new ArrayList<String>();
        listArg.add(filePath);
        
//        System.out.println("Will execute "+readFileContentCmd.getName());
        readFileContentCmd.execute(listArg);
        
        ATKGraphicsUtils.centerFrameOnScreen(previewer);
        previewer.setVisible(true);
    }
    
    
  /**
   * Load a file
   */
    public void loadFile()
    {
        if (loadFileCmd == null) return;

        File   selectedFile = null;
        
        int    returnDial = pfc.showDialog(pfc.getRootPane(), loadButtonText);

        if (returnDial == JFileChooser.CANCEL_OPTION)
            return;
        
        selectedFile = pfc.getSelectedFile();
        if (selectedFile == null) return;

        if (!selectedFile.exists())
        {
            JOptionPane.showMessageDialog(
                    pfc.getRootPane(), "The selected file does not exist.\n\n",
                    loadButtonText + " aborted.\n",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(listener!=null) listener.beforeLoad(parent,selectedFile);
        loadFile(selectedFile);
        if(listener!=null) listener.afterLoad(parent,selectedFile);

    }
    
    
    
  /**
   * Save a file
   */
    public void saveFile()
    {
        if (saveFileCmd == null) return;

        File   selectedFile = null;

        if (saveFileChooser instanceof SmSaveFileChooser)
        {
            SmSaveFileChooser ssfc = (SmSaveFileChooser) saveFileChooser;
            ssfc.setAuthorText(fileAuthor);
            ssfc.setCommentsText(fileComments);
        }
        
        int    returnDial = saveFileChooser.showDialog(saveFileChooser.getRootPane(), saveButtonText);

        if (returnDial == JFileChooser.CANCEL_OPTION)
            return;
        
        selectedFile = saveFileChooser.getSelectedFile();
        if (selectedFile == null) return;

        if (selectedFile.exists())
        {
            returnDial = JOptionPane.showConfirmDialog(
                    saveFileChooser.getRootPane(), "Do you want to overwrite the file?\n\n",
                    "File already exists", JOptionPane.YES_NO_OPTION);
            if (returnDial != JOptionPane.YES_OPTION)
            {
                return;
            }
        }

        if(listener!=null) listener.beforeSave(parent,selectedFile);
        saveFile(selectedFile);
        if(listener!=null) listener.afterSave(parent,selectedFile);

    }
    
    
    
  /**
   * Preview a file
   */
    public void previewFile()
    {
        if (readFileContentCmd == null) return;

        File   selectedFile = null;
        
        int    returnDial = pfc.showDialog(pfc.getRootPane(), previewButtonText);

        if (returnDial == JFileChooser.CANCEL_OPTION)
            return;
        
        selectedFile = pfc.getSelectedFile();
        if (selectedFile == null) return;

        if (!selectedFile.exists())
        {
            JOptionPane.showMessageDialog(
                    pfc.getRootPane(), "The selected file does not exist.\n\n",
                    previewButtonText + " aborted.\n",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        readFile(selectedFile);
    }

    public void resultChange(ResultEvent resultEvent)
    {
        if (readFileContentCmd == null) return;
        if (resultEvent.getSource() != readFileContentCmd) return;

        java.util.List      outArg = resultEvent.getResult();
        if (outArg == null) return;
        if (outArg.isEmpty()) return;
        
        Object  outElem = outArg.get(0);
        if (!(outElem instanceof String))
            return;
        
        fileContent = (String) outElem;
        previewer.setText(fileContent);
        if (previewer.isVisible())
            previewer.repaint();
    }

    public void errorChange(ErrorEvent evt)
    {
        if (readFileContentCmd == null) return;
        if (evt.getSource() != readFileContentCmd) return;
        
        fileContent = null;
        previewer.setText(fileContent);
        if (previewer.isVisible())
            previewer.repaint();
    }
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                JFrame jf = new JFrame();
                jf.setVisible(true);
                
                IStringScalar  rootPathAtt = null;
                ICommand       loadCmd = null;
                ICommand       saveCmd = null;
                ICommand       fileContentCmd = null;
                ErrorHistory   errh = new ErrorHistory();
                
                fr.esrf.tangoatk.core.AttributeList  attl = new fr.esrf.tangoatk.core.AttributeList();
                fr.esrf.tangoatk.core.CommandList    cmdl = new fr.esrf.tangoatk.core.CommandList();
                attl.addErrorListener(errh);
                cmdl.addErrorListener(errh);
                cmdl.addErrorListener(ErrorPopup.getInstance());

                try
                {
                    fr.esrf.tangoatk.core.IEntity ie = attl.add("//acudebian7:10000/sys/settings/syco"+"/SettingsPath");
                    if (ie instanceof IStringScalar)
                    {
                        rootPathAtt = (IStringScalar) ie;
                    }
                }
                catch (Exception ex) {}
                
                try
                {

                    fr.esrf.tangoatk.core.IEntity ie = cmdl.add("//acudebian7:10000/sys/settings/syco"+"/ApplySettings");
                    if (ie instanceof ICommand)
                        loadCmd = (ICommand) ie;

                    ie = cmdl.add("//acudebian7:10000/sys/settings/syco"+"/GenerateSettingsFile");
                    if (ie instanceof ICommand)
                        saveCmd = (ICommand) ie;

                    ie = cmdl.add("//acudebian7:10000/sys/settings/syco"+"/GetSettingsFileContent");
                    if (ie instanceof ICommand)
                        fileContentCmd = (ICommand) ie;
               }
                catch (Exception ex) {}
                
                
                LoadSaveFileHandler loadSaveHandler = new LoadSaveFileHandler();
                loadSaveHandler.setFileAuthor("SYCO");
                loadSaveHandler.setFileComments("Testing ATK util widget LoadSaveFileHandler");
                loadSaveHandler.setLoadButtonText("Load Settings");
                loadSaveHandler.setSaveButtonText("Save Settings");
                
                loadSaveHandler.setRootFolderModel(rootPathAtt);
                loadSaveHandler.setLoadCmdModel(loadCmd);
                loadSaveHandler.setSaveCmdModel(saveCmd);
                loadSaveHandler.setReadFileContentCmd(fileContentCmd);
                
                String  rootPath = null;
                if (loadSaveHandler.getRootFolderModel() != null)
                {
                    rootPath = loadSaveHandler.getRootFolderModel().getStringValue();
                }
                SmSaveFileChooser ssfc = new SmSaveFileChooser(rootPath);
                loadSaveHandler.setSaveJFileChooser(ssfc);

                
                loadSaveHandler.loadFile();
                loadSaveHandler.saveFile();
                loadSaveHandler.previewFile();
            }
        });
    }
}
