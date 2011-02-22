/*
 * CommandMenuViewer.java
 *
 * Created on March 20, 2002, 4:13 PM
 */

package fr.esrf.tangoatk.widget.command;

import javax.swing.*;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.command.*;

/*
 * @author  pons
 */
 
public class CommandMenuViewer extends JPanel {

    // Local variable
    
    JFrame 		argFrame=null;
    AnyCommandViewer 	acv=null;
    JMenuBar            commands=null;
    JMenu		commands_menu=null;
    JMenuItem[]		commands_items=null;
    
    private fr.esrf.tangoatk.core.CommandList commandList;

    // CommandMenuViewer 
    public CommandMenuViewer() {
    
        commands      = new JMenuBar();
	commands.setBorder( null );
	
	commands_menu = new JMenu();
	commands_menu.setText("...");
	
	commands.add(commands_menu);
        
	updateMenuItem();
        
        setLayout(new java.awt.GridLayout(1,1));
	setBorder(BorderFactory.createRaisedBevelBorder());
        add(commands);

    }
    
    // Set the text of the button
    public void setMenuTitle(String s) {
	commands_menu.setText(s);       
    }
    
    // Set the model of this Command list
    public void setModel(fr.esrf.tangoatk.core.CommandList list) {
        
	this.commandList = list;	
	commands_items = new JMenuItem[list.size()];	
	for(int i=0;i<list.size();i++) {
          commands_items[i] = new JMenuItem();	  
	  commands_items[i].addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commandsActionPerformed(evt);
            }
          });
	  commands_menu.add(commands_items[i]); 
	}
	
	updateMenuItem();
	
    }
    
    // Return the model
    public fr.esrf.tangoatk.core.CommandList getModel() {
        return commandList;
    }
    
    // Update command menu item properties
    public void updateMenuItem() {
      
      if( commandList != null ) {
        for(int i=0;i<commandList.size();i++) {
	  commands_items[i].setText( commandList.get(i).toString() );
	  commands_items[i].setFont( getFont() );
	  commands_items[i].setBackground( getBackground() );
	  commands_items[i].setForeground( getForeground() );	  	
	}
      }
      
      if( commands != null ) {
        commands_menu.setFont( getFont() );
        commands_menu.setBackground( getBackground() );
        commands_menu.setForeground( getForeground() );
      
        commands.setFont( getFont() );
        commands.setBackground( getBackground() );
        commands.setForeground( getForeground() );
      }
      
    }

    private void commandsActionPerformed(java.awt.event.ActionEvent evt) {
    
        // Add your handling code here:
	JMenuItem menu = (JMenuItem)evt.getSource();
        int i=0;
	boolean found=false;
	
	while( i<commandList.size() && !found ) {
	  found = ( menu == commands_items[i] );
	  if(!found) i++;
	}
	
	if( !found ) return;
	
	ICommand command = (ICommand)commandList.get(i);

	if (command instanceof InvalidCommand) {
	    JOptionPane.showMessageDialog(this, command.getName() + " is not supported. It probably takes an array as input.", "Error", 1);
	    return;
	}
	
	if (command instanceof VoidVoidCommand) {
	    command.execute();
	    return;
	}
	
	if( acv==null ) {
	  acv      =new AnyCommandViewer();
	  argFrame =new JFrame();
	  argFrame.getContentPane().add(acv);
	}

	acv.initialize(command);
	acv.setDeviceButtonVisible(deviceButtonVisible);
	acv.setDescriptionVisible(descriptionVisible);
	acv.setInfoButtonVisible(infoButtonVisible);

	acv.setBorder(null);
	acv.setInputVisible(true);
	
	if (!command.takesInput()) {
	    command.execute();
	} 
	
	argFrame.setTitle(command.getName());
	argFrame.pack();
	argFrame.show();
	
    }
    
    boolean borderVisble;
    
    /**
     * Get the value of borderVisble.
     * @return value of borderVisble.
     */
    public boolean isBorderVisible() {
	return borderVisble;
    }
    
    /**
     * Set the value of borderVisble.
     * @param v  Value to assign to borderVisble.
     */
    public void setBorderVisible(boolean  v) {
	this.borderVisble = v;
    }
    

    boolean infoButtonVisible;
    
    /**
     * Get the value of infoButtonVisible.
     * @return value of infoButtonVisible.
     */
    public boolean isInfoButtonVisible() {
	return infoButtonVisible;
    }
    
    /**
     * Set the value of infoButtonVisible.
     * @param v  Value to assign to infoButtonVisible.
     */
    public void setInfoButtonVisible(boolean  v) {
	this.infoButtonVisible = v;
    }
    
    boolean cancelButtonVisible;
    
    /**
     * Get the value of cancelButtonVisible.
     * @return value of cancelButtonVisible.
     */
    public boolean isCancelButtonVisible() {
	return cancelButtonVisible;
    }
    
    /**
     * Set the value of cancelButtonVisble.
     * @param v  Value to assign to cancelButtonVisble.
     */
    public void setCancelButtonVisible(boolean  v) {
	this.cancelButtonVisible = v;
    }
    
    boolean deviceButtonVisible;
    
    /**
     * Get the value of deviceButtonVisible.
     * @return value of deviceButtonVisible.
     */
    public boolean isDeviceButtonVisible() {
	return deviceButtonVisible;
    }
    
    /**
     * Set the value of deviceButtonVisible.
     * @param v  Value to assign to deviceButtonVisible.
     */
    public void setDeviceButtonVisible(boolean  v) {
	this.deviceButtonVisible = v;
    }

    boolean descriptionVisible;
    
    /**
     * Get the value of descriptionVisible.
     * @return value of descriptionVisible.
     */
    public boolean isDescriptionVisible() {
	return descriptionVisible;
    }
    
    /**
     * Set the value of descriptionVisible.
     * @param v  Value to assign to descriptionVisible.
     */
    public void setDescriptionVisible(boolean  v) {
	this.descriptionVisible = v;
    }

        
    public void setForeground(java.awt.Color color) {
	super.setForeground(color);
	updateMenuItem();
    }

    public void setBackground(java.awt.Color color) {
	super.setBackground(color);
	updateMenuItem();
    }

    public void setFont(java.awt.Font font) {
        super.setFont(font);
	updateMenuItem();
    }
    
    public static void main (String[] args) throws Exception {
    
	fr.esrf.tangoatk.core.CommandList clist =
	    new fr.esrf.tangoatk.core.CommandList();
	CommandMenuViewer cmv = new CommandMenuViewer();
	clist.add("eas/test-api/1/*");
	cmv.setModel(clist);
	JFrame f = new JFrame();
	f.getContentPane().add(cmv);
	f.pack();
	f.show();
	
    } // end of main ()
    
}
