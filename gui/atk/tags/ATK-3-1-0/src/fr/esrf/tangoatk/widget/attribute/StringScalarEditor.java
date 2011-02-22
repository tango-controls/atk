/*
 * StringScalarEditor.java
 *
 * Created on July 29, 2003, 11:00 AM
 */

/**
 *
 * @author  poncet
 */
package fr.esrf.tangoatk.widget.attribute;


import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JTextField;

import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.IStringScalar;
import fr.esrf.tangoatk.core.IStringScalarListener;
import fr.esrf.tangoatk.core.StringScalarEvent;

public class StringScalarEditor extends JTextField
             implements IStringScalarListener
{
    private IStringScalar    model;
    private String           lastSet;
    
    /** Creates new form StringScalarEditor */
    public StringScalarEditor()
    {
       model = null;
       lastSet = null;
       this.addActionListener
		 (
		    new java.awt.event.ActionListener()
		    {
		       public void actionPerformed(java.awt.event.ActionEvent evt)
		       {
			  textInsertActionPerformed(evt);
		       }
		    }
		 );
       setMargin( new Insets(0,0,0,0) ); // text will have the maximum available space
    }

    public void setModel(IStringScalar is)
    {
       // Remove old registered listener
       if (model != null)
	   model.removeStringScalarListener(this);	  
	  
       if (is == null)
       {
	  model = null;
	  return;
       }
       
       
       if( !is.isWritable() )
	  throw new IllegalArgumentException("StringScalarEditor: Only accept writeable attribute.");

       model = is;

       // Register new listener
       model.addStringScalarListener(this);	
       model.refresh();
       
       String textFieldString = model.getString();
       
       if (textFieldString == null )
	    textFieldString = "NULL";
	
       setText(textFieldString);
       lastSet = textFieldString;
    }
    
    
    public IStringScalar getModel()
    {
       return model;
    }
    

  
    // Listen on "setpoint" change
    // this is not clean yet as there is no setpointChangeListener
    // Listen on valueChange and readSetpoint
    public void stringScalarChange(StringScalarEvent e)
    {
       String    set = null;
       
       long now = System.currentTimeMillis();

       //avoiding NullPointerException
       if (model != null)
       {
           if(hasFocus())
        	   set = model.getStringDeviceSetPoint();
           else
        	   set = model.getStringSetPoint();
       }

       if ( set != null )
       {
          // Dont update if the set point has not changed
          if ( !set.equals(lastSet) )
	  {
	      this.setText(set);
	      lastSet = set;
	  }
       }
       else //set == null
       {
	  this.setText("NULL");
	  lastSet = "NULL";
       }
    }
    
  
    
    public void errorChange(ErrorEvent e)
    {
      setText( "Read Error" );
      lastSet = "Read Error";
    }

    public void stateChange(AttributeStateEvent e)
    {
    }


  
    private void textInsertActionPerformed(java.awt.event.ActionEvent evt)
    {
        lastSet = this.getText();
        //avoiding NullPointerException
        if (model != null)
        {
            model.setString(lastSet);
        }
    }





 
    
    public static void main(String[] args)
    {
       fr.esrf.tangoatk.core.AttributeList  attList = new fr.esrf.tangoatk.core.AttributeList();
       StringScalarEditor          sse = new StringScalarEditor();
       IStringScalar               att;
       JFrame                      mainFrame;
       
       // Connect to a "writable" string scalar attribute
       try
       {
          att = (IStringScalar) attList.add("dev/test/10/String_attr_w");
	  sse.setModel(att);
       }
       catch (Exception ex)
       {
          System.out.println("caught exception : "+ ex.getMessage());
	  System.exit(-1);
       }
       
       attList.startRefresher();
       
       mainFrame = new JFrame();
       
       mainFrame.getContentPane().add(sse);
       mainFrame.pack();

       mainFrame.setVisible(true);

    } // end of main ()

}

