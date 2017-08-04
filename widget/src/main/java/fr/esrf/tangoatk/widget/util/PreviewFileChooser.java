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
public class PreviewFileChooser extends JFileChooser
{
    private String                    rootDirectory = null;
    private boolean                   contentVisible = false;
    private FileContentViewer         fcv = new FileContentViewer(500, 250);
    
    public PreviewFileChooser ()
    {
        super();
    }
    
    public PreviewFileChooser (String dirPath)
    {
        super(dirPath);
        rootDirectory = dirPath;
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
   
    public boolean isContentVisible()
    {
        return contentVisible;
    }
    
    public void setContentVisible(boolean cv)
    {
        if (cv == isContentVisible())
            return;
        
        if (isContentVisible())
        {
            this.removePropertyChangeListener(fcv);
            this.setAccessory(null);
        }
        else
        {
            this.addPropertyChangeListener(fcv);
            this.setAccessory(fcv);
        }
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
                
                PreviewFileChooser pjfc = new PreviewFileChooser("/users/poncet");
                pjfc.setContentVisible(true);
                
                int returnDial = pjfc.showDialog(jf.getRootPane(), "Load");
                if (returnDial == JFileChooser.CANCEL_OPTION)
                    return;
                
                java.io.File selectedFile = pjfc.getSelectedFile();
                if (selectedFile == null) return;

                if (!selectedFile.exists())
                {
                    JOptionPane.showMessageDialog(
                            jf.getRootPane(), "The selected file does not exist.\n\n",
                            "Load file aborted.\n",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // do load action
                System.out.println("File to load : "+selectedFile.getName());
            }
        });
    }
}
