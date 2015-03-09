/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.esrf.tangoatk.widget.util;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;

/**
 *
 * @author poncet
 */
public class RestrictedFolderJFileChooser extends JFileChooser
{

    private String      initFolder = null;
    private String[]    autorizedFolders = null;
    private String[]    autorizedCanonicalFolders = null;

    public RestrictedFolderJFileChooser(String folder)
    {
        super(".");
        initFolder = folder;

        if (initFolder != null)
        {
            setCurrentDirectory(new File(initFolder));
            addAutorizedFolder(initFolder);
        }
    }

    public void addAutorizedFolder(String  folder)
    {
        if (folder == null) return;
        if (folder.length() <= 0) return;

        if (autorizedFolders == null)
        {
            autorizedFolders = new String[1];
            autorizedFolders[0] = folder;
        }
        else
        {
            int lgth = autorizedFolders.length;
            String[]  afs = new String[lgth+1];
            System.arraycopy(autorizedFolders, 0, afs, 0, lgth);
            afs[lgth] = folder;
            autorizedFolders = afs;
        }

        updateAutorizedCanonicalFolders();
    }

    private void updateAutorizedCanonicalFolders()
    {
        autorizedCanonicalFolders = new String[autorizedFolders.length];
        for (int i=0; i<autorizedFolders.length; i++)
        {
            autorizedCanonicalFolders[i] = getCanonicalFolder(autorizedFolders[i]);
        }
    }

    private String getCanonicalFolder(String folder)
    {
        File fileDir = new File(folder);
        String  folderCanonicalPath = null;
        try
        {
            folderCanonicalPath = fileDir.getCanonicalPath();
        }
        catch (IOException ioex)
        {
            folderCanonicalPath = new String(folder);
        }
        return folderCanonicalPath;
    }

    public File showDialog(Component parent, String approveButtonText, String acceptedFolder)
    {
        int       dialReturn;
        File      selectedFile = null;
        String    selectedFilePath = null;

        setCurrentDirectory(new File(initFolder));
        dialReturn = super.showDialog(parent, approveButtonText);

        if (dialReturn != JFileChooser.APPROVE_OPTION)
        {
            return null;
        }

        selectedFile = this.getSelectedFile();
        try
        {
            selectedFilePath = selectedFile.getCanonicalPath();
        }
        catch (IOException ioex)
        {
            javax.swing.JOptionPane.showMessageDialog(
                    parent, "Failed to get the canonical path of the selected file.\n\n"
                    + ioex + "\n\n\n",
                    approveButtonText+" aborted.\n",
                    javax.swing.JOptionPane.ERROR_MESSAGE);

            return null;
        }

        boolean wellLocated;
        String  msg;

        if (acceptedFolder == null)
        {
            wellLocated = isInAutorizedFolders(selectedFilePath);
            msg = getAutorizedFolderHint();
        }
        else
        {
            wellLocated = isInSpecifiedFolder(selectedFilePath, acceptedFolder);
            msg = "The file should be located inside "+acceptedFolder+" \n ";
        }

        if (wellLocated == false)
        {
            javax.swing.JOptionPane.showMessageDialog(
                    parent, "The selected file is not inside the authorized root folder(s)\n\n"
                    + msg,
                    approveButtonText+" aborted.\n",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return selectedFile;
    }


    private boolean isInSpecifiedFolder(String fPath, String folder)
    {
        File fileDir = new File(folder);
        String  folderCanonicalPath = null;
        try
        {
            folderCanonicalPath = fileDir.getCanonicalPath();
        }
        catch (IOException ioex)
        {
            folderCanonicalPath = new String(folder);
        }

        if (fPath.startsWith(folderCanonicalPath))
            return true;
        else
            return false;
    }


    private boolean isInAutorizedFolders(String fPath)
    {
        if (autorizedCanonicalFolders == null) return true;
        if (autorizedCanonicalFolders.length <= 0) return true;

        for (int i=0; i<autorizedCanonicalFolders.length; i++)
        {
            if (fPath.startsWith(autorizedCanonicalFolders[i]))
            {
                return true;
            }
        }

        return false;
    }


    private String getAutorizedFolderHint()
    {
        if (autorizedFolders == null) return "";
        if (autorizedFolders.length <= 0) return "";

        String  folderHint = "The file should be located inside one the following folders : \n ";

        for (int i = 0; i < autorizedFolders.length; i++)
        {
            folderHint = folderHint + "     " + autorizedFolders[i] + "\n";
        }

        return folderHint;
    }
}
