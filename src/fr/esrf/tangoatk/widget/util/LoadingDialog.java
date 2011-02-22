 

package fr.esrf.tangoatk.widget.util;
 

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;


import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;

 

/**
 * @author HO
 */

public class LoadingDialog extends JFrame{

    

    private static boolean isShowing = false;
    private static LoadingDialog m_dialog = null;
    private static JPanel container = null;


    /**

     * 

     */

    public LoadingDialog(String message)

    {
        super("Loading : " + message);
        container = new JPanel();
        container.setSize(400,0);
        container.setPreferredSize(container.getSize());
        setContentPane(container);
        setSize(container.getSize());
        setResizable(true);
    }

    protected void setShowing(boolean b){
        isShowing = b;
    }
    
    public static synchronized void  showMessageDialog (String message)
    {
       System.out.println("showMessageDialog");
       if (m_dialog==null) {
	       m_dialog = new  LoadingDialog(message);
	   }
	   else{
		   m_dialog.setTitle(message);
	   }
       if(!m_dialog.isShowing())
       {
           ATKGraphicsUtils.centerFrameOnScreen(m_dialog);
           m_dialog.show();
           m_dialog.pack();
           m_dialog.toFront();
       }
    }
    
    public static void hideMessageDialog(){
        System.out.println("hideMessageDialog");
	    if (m_dialog.isShowing()){
		    m_dialog.hide();
		    m_dialog.setShowing(false);
	    } 
    }
}
