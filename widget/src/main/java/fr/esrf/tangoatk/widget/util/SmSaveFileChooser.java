/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.esrf.tangoatk.widget.util;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author poncet
 */
public class SmSaveFileChooser extends JFileChooser
{
    private String                           rootDirectory = null;
    private SmSaveFileAccessoryPanel         saveAccessory = new SmSaveFileAccessoryPanel(400, 250);
    
    public SmSaveFileChooser ()
    {
        super();
        this.setAccessory(saveAccessory);
    }
    
    public SmSaveFileChooser (String dirPath)
    {
        super(dirPath);
        rootDirectory = dirPath;
        this.setAccessory(saveAccessory);
    }
    
    public String getRootDirectory()
    {
        return(rootDirectory);
    }
     
    public void setRootDirectory(String dirPath)
    {
        File dir = new File(dirPath);
        super.setCurrentDirectory(dir);
        rootDirectory = dirPath;
    }
    
    public String getAuthorText()
    {
        if (saveAccessory == null) return null;
        return saveAccessory.getAuthorText();
    }
    
    public void setAuthorText(String auth)
    {
        if (saveAccessory == null) return;
        saveAccessory.setAuthorText(auth);
    }
    
    
    public String getCommentsText()
    {
        if (saveAccessory == null) return null;
        return saveAccessory.getCommentsText();
    }
    
    public void setCommentsText(String com)
    {
        if (saveAccessory == null) return;
        saveAccessory.setCommentsText(com);
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
                
                SmSaveFileChooser saveFc = new SmSaveFileChooser("/users/poncet");
                
                int returnDial = saveFc.showDialog(jf.getRootPane(), "Save");
                if (returnDial == JFileChooser.CANCEL_OPTION)
                    return;
                
                java.io.File selectedFile = saveFc.getSelectedFile();
                if (selectedFile == null) return;

                if (!selectedFile.exists())
                {
                    JOptionPane.showMessageDialog(
                            jf.getRootPane(), "The selected file does not exist.\n\n",
                            "Save file aborted.\n",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // do load action
                System.out.println("File to save : "+selectedFile.getName());
            }
        });
    }
    
}
