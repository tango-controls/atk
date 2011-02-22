// File:          RoiControlPanel.java
// Created:       2002-06-06 11:08:19, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-03 15:1:26, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.image;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
public class RoiControlPanel extends JPanel implements IImageManipulatorPanel {

    int sx = 0, sy = 0, ex = 0, ey = 0;

    int i = 0;
    IImageViewer model;
    JPanel top;
    Graphics graphics;

    public void setModel(IImageViewer viewer) {
	setImageViewer(viewer);
    }

    public void setImageViewer(IImageViewer viewer) {
	viewer.addImageManipulator(this);
	model = viewer;
	top = model.getTopLayer();

    }
    
    public String getName() {
	return "Roi Control";
    }

    public JComponent getComponent() {
	return this;
    }

    public BufferedImage filter(BufferedImage i) {
	graphics = i.getGraphics();
	graphics.setColor(Color.red);
	graphics.drawRect(sx, sy, ex - sx, ey - sy);
	return i;
    }

    public void ok() {
	getRootPane().getParent().setVisible(false);
    }
    
    public void roiChanged(int startx, int endx, int starty, int endy) {

	
	sx = startx;
	ex = endx;
	sy = starty;
	ey = endy;

	if (++i > 20 ) {
	    i = 0;
	    top.repaint();
	}

    }
}
