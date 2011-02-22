 

package fr.esrf.tangoatk.widget.util;
 

import javax.swing.JFrame;
import javax.swing.JPanel;

 

/**
 * @author HO
 */

public class LoadingDialog extends JFrame{

    

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
    
    public static synchronized void  showMessageDialog (String message)
    {
       if (m_dialog==null) {
	       m_dialog = new  LoadingDialog(message);
	   }
	   else{
		   m_dialog.setTitle(message);
	   }
       if(!m_dialog.isShowing())
       {
           ATKGraphicsUtils.centerFrameOnScreen(m_dialog);
           m_dialog.pack();
           m_dialog.setVisible(true);
           m_dialog.toFront();
       }
    }
    
    public static void hideMessageDialog(){
	    if (m_dialog.isShowing()){
		    m_dialog.setVisible(false);
	    } 
    }
}
