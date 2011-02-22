// File:          EdgeDetector.java
// Created:       2002-06-12 14:43:49, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-13 14:5:43, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.image;
import java.awt.image.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ConvolveFilter extends JPanel implements IImageManipulatorPanel {
    IImageViewer viewer;
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
    JButton edgeButton = new JButton("Edge");
    JButton blurButton = new JButton("Blur");
    JButton sharpButton = new JButton("Sharpen");
    JButton resetButton = new JButton("Reset");
    float [][] myFilter = { {0f, -1f, 0f},
			    {-1f, 4f, -1f},
			    {0f, -1f, 0f}};

    JButton setButton = new JButton("Set");
    boolean newFilter = false;
    boolean filter = false;
    JTable table = new JTable(new myTableModel());

    class myTableModel extends javax.swing.table.AbstractTableModel {
	public int getRowCount() {
	    return 3;
	}

	public int getColumnCount() {
	    return 3;
	}

	public Object getValueAt(int row, int column) {
	    return new Float(myFilter[row][column]);
	}

	public void setValueAt(Object val, int row, int column) {
	    myFilter[row][column] = ((Float)val).floatValue();
	}

	public Class getColumnClass(int i) {
	    return Float.class;
	}

	public boolean isCellEditable(int row, int column) {
	    return true;
	}
    }

    public ConvolveFilter() {
	GridBagConstraints constraints = new GridBagConstraints();
	kernel = new Kernel(3, 3, sharpen);
	cop = new ConvolveOp(kernel);
	setLayout(new GridBagLayout());
	constraints.gridx = 0;
	constraints.gridy = 0;
	add(edgeButton, constraints);
	edgeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    edgeAction();
		}
	    });
	constraints.gridx = 1;
	add(blurButton, constraints);
	blurButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    blurAction();
		}
	    });
	constraints.gridx = 2;
	add(sharpButton, constraints);
	sharpButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    sharpAction();
		}
	    });
	constraints.gridy = 1;
	constraints.gridx = 0;
	add(table, constraints);
	constraints.gridy = 2;
	constraints.gridx = 0;

	add(setButton, constraints);
	setButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setAction();
		}
	    });
	constraints.gridx = 0;
	constraints.gridy = 3;
	add(resetButton, constraints);
	resetButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    resetAction();
		}
	    });
		
    }

    protected void edgeAction() {
	kernel = new Kernel(3, 3, edge);
	cop = new ConvolveOp(kernel);
	newFilter = filter = true;
	viewer.repaint();
    }
 
    protected void setAction() {
	float [] tmp = new float[9];
	int k = 0;
	for (int i = 0; i < 3; i++) {
	    for (int j = 0; j < 3; j++) {
		tmp[k++] = myFilter[i][j];
	    } // end of for ()
	    
	    
	} // end of for ()
	
	kernel = new Kernel(3, 3, tmp);
	cop = new ConvolveOp(kernel);
	newFilter = filter = true;
	viewer.repaint();
    }

    protected void blurAction() {
	kernel = new Kernel(3, 3, blur);
	cop = new ConvolveOp(kernel);
	newFilter = filter = true;
	viewer.repaint();
    }

    protected void resetAction() {
	filter = false;
	viewer.repaint();
    }

    protected void sharpAction() {
	kernel = new Kernel(3, 3, sharpen);
	cop = new ConvolveOp(kernel);
	newFilter = filter = true;
	viewer.repaint();
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

    public static void main (String[] args) {
	JFrame f = new JFrame();
	f.getContentPane().add(new ConvolveFilter());
	f.pack();
	f.show();
    } // end of main ()
    
}
