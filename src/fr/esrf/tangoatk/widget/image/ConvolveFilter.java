// File:          EdgeDetector.java
// Created:       2002-06-12 14:43:49, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-05 16:5:35, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.image;
import java.awt.image.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import fr.esrf.tangoatk.widget.util.IApplicable;
public class ConvolveFilter extends JPanel
    implements IImageManipulator, IImagePanel, IApplicable {
    IImageViewer viewer;
    boolean customFilter = false;
    int i = 0;
    final float ninth = 1f/9f;

    float[] edge =    { 0f, -1f, 0f,
		       -1f, 4f,  -1f,
			0f, -1f, 0f};

    float[] sharpen =    { 0f, -1f, 0f,
			 -1f, 5f,  -1f,
			 0f, -1f, 0f};

    float [] blur = { ninth, ninth, ninth,
		      ninth, ninth, ninth,
		      ninth, ninth, ninth };
    Kernel kernel;
    ConvolveOp cop;
    ButtonGroup group = new ButtonGroup();
    JRadioButton edgeButton = new JRadioButton("Edge");
    JRadioButton blurButton = new JRadioButton("Blur");
    JRadioButton sharpButton = new JRadioButton("Sharpen");
    JRadioButton resetButton = new JRadioButton("No filter");
    float [] myFilter = { 0f, -1f, 0f,
			  -1f, 4f, -1f,
			  0f, -1f, 0f};

    float [] currentFilter = myFilter;
    
    JRadioButton setButton = new JRadioButton("Custom");
    
    boolean newFilter = false;
    boolean filter = false;
    MyTableModel tableModel = new MyTableModel();
    JTable table = new JTable(tableModel);

    class MyTableModel extends javax.swing.table.AbstractTableModel {

	public int getRowCount() {
	    return 3;
	}

	public void filterChanged() {
	    table.setBackground(java.awt.Color.white);
	    fireTableStructureChanged();
	}
	
	public int getColumnCount() {
	    return 3;
	}

	public Object getValueAt(int row, int column) {
	    return new Float(currentFilter[row * getColumnCount() + column]);
	}

	public void setValueAt(Object val, int row, int column) {
	    currentFilter[row * getColumnCount() + column] =
		((Float)val).floatValue();
	}

	public Class getColumnClass(int i) {
	    return Float.class;
	}

	public boolean isCellEditable(int row, int column) {
	    return setButton.isSelected();
	}
    }

    public ConvolveFilter() {
	group.add(edgeButton);
	group.add(blurButton);
	group.add(sharpButton);
	group.add(setButton);
	group.add(resetButton);
	resetButton.setSelected(true);
	resetAction();
	GridBagConstraints constraints = new GridBagConstraints();
	kernel = new Kernel(3, 3, sharpen);
	cop = new ConvolveOp(kernel);
	setLayout(new GridBagLayout());
	constraints.gridx = 2;
	constraints.gridy = 1;
	constraints.anchor = GridBagConstraints.EAST;
	constraints.fill = GridBagConstraints.BOTH;
	add(edgeButton, constraints);

	constraints.gridy = 2;
	add(blurButton, constraints);

	constraints.gridy = 3;
	add(sharpButton, constraints);

	constraints.gridy = 4;

	add(setButton, constraints);

	constraints.gridx = 2;
	constraints.gridy = 5;

	
	add(resetButton, constraints);
	constraints = new GridBagConstraints();
	constraints.gridy = 1;
	constraints.gridx = 1;
 	add(table, constraints);

	edgeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    edgeAction();
		}
	    });
	setButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setAction();
		}
	    });
	blurButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    blurAction();
		}
	    });
	resetButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    resetAction();
		}
	    });
	sharpButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    sharpAction();
		}
	    });
	
    }

    protected void edgeAction() {
	currentFilter = edge;
	tableModel.filterChanged();
	newFilter = filter = true;
    }
 
    protected void setAction() {

	currentFilter = myFilter;
	tableModel.filterChanged();	

	newFilter = filter = true;
    }

    protected void blurAction() {
	currentFilter = blur;
	tableModel.filterChanged();
	newFilter = filter = true;
    }

    protected void resetAction() {
	table.setBackground(getBackground());
	filter = false;
    }

    protected void sharpAction() {
	currentFilter = sharpen;
	tableModel.filterChanged();
	newFilter = filter = true;
    }


    
    public String getName() {
	return "ConvolveOps";
    }

    public JComponent getComponent() {
	return this;
    }
    
    public BufferedImage filter(BufferedImage image) {
	if (filter) {
	    System.out.println("filtering...");
	    newFilter = false;
	    return cop.filter(image, null);	    
	}

	return image;

    }

    public void setModel(IImageViewer viewer) {
	setImageViewer(viewer);
    }

    public void setImageViewer(IImageViewer viewer) {
	this.viewer = viewer;
    }

    public void roiChanged(int startx, int endx, int starty, int endy) {

    }
    public void ok() {
	apply();
	cancel();
    }

    public void cancel() {
	getRootPane().getParent().setVisible(false);
    }

    public void apply() {
	kernel = new Kernel(3, 3, currentFilter);
	cop = new ConvolveOp(kernel);
	
	viewer.repaint();
    }
	
    public static void main (String[] args) {
	JFrame f = new JFrame();
	f.getContentPane().add(new ConvolveFilter());
	f.pack();
	f.setVisible(true);
    } // end of main ()
    
}
