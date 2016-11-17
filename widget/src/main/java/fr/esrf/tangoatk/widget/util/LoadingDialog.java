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
