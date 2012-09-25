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
 
/*
 * TableCommandInput.java
 *
 * Created on July 17, 2002, 1:41 PM
 */

package fr.esrf.tangoatk.widget.command;
import fr.esrf.tangoatk.core.ICommand;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author  root
 */
public class TableCommandInput extends javax.swing.JPanel implements IInput {

    /** Creates new form TableCommandInput */
    public TableCommandInput()
    {
        initComponents();
        javax.swing.table.DefaultTableCellRenderer cellRend = new javax.swing.table.DefaultTableCellRenderer();
        cellRend.setHorizontalAlignment(SwingConstants.CENTER);
        jTable1.getColumnModel().getColumn(CommandTableInputAdapter.INDEX_COL).setMaxWidth(20);
        jTable1.getColumnModel().getColumn(CommandTableInputAdapter.INDEX_COL).setCellRenderer(cellRend);
    }

    public TableCommandInput(ICommand command) {
        this();
        setModel(command);
    }

    private void initComponents()
    {
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        executeButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;

        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jTable1.setModel(commandTableInputAdapter);
        jScrollPane1.setViewportView(jTable1);
	
	
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints1);
        
        executeButton.setText("execute");
        executeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeButtonActionPerformed(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        add(executeButton, gridBagConstraints1);
	setMinimumSize(new Dimension(200, 17));
	setPreferredSize(new Dimension(200, 130));
        
    }

    private void executeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeButtonActionPerformed
        firePropertyChange("execute", null, null);
    }

    

	
    public void setModel(ICommand model)
    {
       this.model = model;
       commandTableInputAdapter.setModel(model);
    }  



    public ICommand getModel()
    {
        return model;
    }    
    
   
    
    public void setInputEnabled(boolean b)
    {
    }



    public boolean isInputEnabled()
    {
       return true;
    }



    public java.util.List<Object> getInput()
    {
        java.util.List<Object>     nums, strs;
	java.util.Vector<Object>   in;
	
	nums = commandTableInputAdapter.getNums();
	strs = commandTableInputAdapter.getStrs();
		  
	in = new java.util.Vector<Object> (2);
	
	in.add(0, nums);
	in.add(1, strs);
	
	return in;
    }



    public void setInput(java.util.List l)
    {
        return;
    }

       
          
    public static void main(String arg[])
    {
        javax.swing.JFrame f = new javax.swing.JFrame();
        TableCommandInput input = new TableCommandInput();
        f.setContentPane(input);
        f.pack();
	f.setVisible(true);
    }
    
    private CommandTableInputAdapter commandTableInputAdapter = new CommandTableInputAdapter();
    private ICommand model;


    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton executeButton;

}
