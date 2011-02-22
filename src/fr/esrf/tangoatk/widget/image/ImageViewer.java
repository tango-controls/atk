// File:          ImageViewer.java
// Created:       2002-06-05 15:37:33, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-05 16:6:7, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.image;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import fr.esrf.tangoatk.core.*;
import java.awt.geom.AffineTransform;
import java.beans.*;
public class ImageViewer extends JPanel implements IImageViewer {


    IImageManipulator [] listenerList;
    double [][] raster;

    BufferedImage image;
    JDialog controlFrame;
    int startx, starty, endx, endy;
    JPanel canvas;
    JFrame frame;
    boolean rasterChanged = true;
    
    public ImageViewer() {

	listenerList = new IImageManipulator[0];
	controlFrame = new JDialog();
	controller = new ImageController();
	controller.setModel(this);
	controlFrame.getContentPane().add(controller);
	setRasterConverter(new BWRaster());

// 	canvas = new JPanel() {
// 		public void paintComponent(Graphics g) {
// 		    internalPaint(g);
// 		}
// 	    };

// 	canvas.setOpaque(false);
// 	add(canvas);
	addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
		    if (e.getButton() == MouseEvent.BUTTON1) {
			startRoi(e.getX(), e.getY());			
		    }
		}

		public void mouseClicked(MouseEvent e) {
		    int i = e.getButton();
		    switch (i) {
		    case MouseEvent.BUTTON1:
			break;
			
		    case MouseEvent.BUTTON2 :
			setControllerVisible(true);
			break;
			    
		    case MouseEvent.BUTTON3 :
			setControllerVisible(true);
			break;
			    
		    default:
			break;
		    } // end of switch ()
		    
		}
	    });

	addMouseMotionListener(new MouseMotionAdapter() {
		public void mouseDragged(MouseEvent e) {
		    dragRoi(e.getX(), e.getY());
		}
	    });
    }

    ImageController controller;


    IRasterConverter rasterConverter;
    

    public void setRasterConverter(IRasterConverter v) {
	this.rasterConverter = v;
	rasterConverter.setImageViewer(this);
    }

    public IRasterConverter getRasterConverter() {
	return rasterConverter;
    }
	

    public void setSize(Dimension size) {
	setPreferredSize(size);
	setMaximumSize(size);
    }

    public Dimension getSize() {
	return getPreferredSize();
    }

    public void repaint() {
	super.repaint();
    }
    
    protected void startRoi(int x, int y) {
	startx = x;
	starty = y;
	endx = x;
	endy = y;
    }

    protected void dragRoi(int x, int y) {
	for (int i = 0; i < listenerList.length; i++) {
	    listenerList[i].roiChanged(startx, x, starty, y);
	}
    }


    /**
     * Get the value of controller.
     * @return value of controller.
     */
    public ImageController getController() {
	return controller;
    }
    
    /**
     * Set the value of controller.
     * @param v  Value to assign to controller.
     */
    public void setController(ImageController  v) {

    }
    
    /**
     * Get the value of image.
     * @return value of image.
     */
    public BufferedImage getImage() {
	return image;
    }
    
    /**
     * Set the value of image.
     * @param v  Value to assign to image.
     */
    public void setImage(BufferedImage  v) {
	this.image = v;
    }
    
    /**
     * Get the value of controllerVisible.
     * @return value of controllerVisible.
     */
    public boolean isControllerVisible() {
	return controlFrame.isVisible();
    }

    /**
     * Set the value of controllerVisible.
     * @param v  Value to assign to controllerVisible.
     */
    public void setControllerVisible(boolean  v) {
	controlFrame.pack();
	controlFrame.setVisible(v);
    }
    
    /**
     * Get the value of raster.
     * @return value of raster.
     */
    public double [][] getRaster() {
	return raster;
    }
    
    /**
     * Set the value of raster. This method in turn calls the
     * IRasterControl.rasterChanged(double [][])
     * @param v  Value to assign to raster.
     */
    public void setRaster(double [][]  v) {
	this.raster = v;
	rasterChanged = true;
	image = rasterConverter.convertRaster(raster);

	setSize(new Dimension(image.getWidth(), image.getHeight()));
	repaint();
    }

    public void paintComponent(Graphics g) {
	super.paintComponent(g);
	internalPaint(g);
    }
    

    /**
     * <code>addImageListener</code>
     *
     * @param l an <code>IImageManipulator</code> value
     * @deprecated use addImageManipulator instead
     */
    public void addImageListener(IImageManipulator l) {

    }

    public void addImagePanel(IImagePanel panel) {
	getController().addToPanel(panel);	
    }
	
    public void addImageManipulator(IImageManipulator l) {
	int i = listenerList.length;
	IImageManipulator [] tmp = new IImageManipulator[i + 1];
	System.arraycopy(listenerList, 0, tmp, 0, i);
	tmp[i] = l;
	listenerList = tmp;
	l.setImageViewer(this);

	if (l instanceof IImagePanel) {
	    addImagePanel((IImagePanel)l);
	    l.setModel(this);
	}
    }

    /**
     * <code>removeImageListener</code>
     *
     * @param l an <code>IImageManipulator</code> value
     * @deprecated use addImageManipulator instead
     */
    public void removeImageListener(IImageManipulator l) {

    }

    public void removeImageManipulator(IImageManipulator l) {
	boolean found = false;
	int length = listenerList.length;
	int i = 0;
	while (i < length) {
	    if (listenerList[i] == l) {
		found = true;
		break;
	    }
	    i++;
	}

	if (!found) return;
	listenerList[i] =
	    listenerList[length - 1];
	IImageManipulator[] tmp = new IImageManipulator[length -1];
	System.arraycopy(listenerList, 0, tmp, 0, length - 1);
	listenerList = tmp;

    }

    void internalPaint(Graphics g) {
	if (image == null) return;
	BufferedImage tmpImg = image;

	for (int i = 0; i < listenerList.length; i++) {
	    tmpImg = listenerList[i].filter(tmpImg);
	}

	g.drawImage(tmpImg, 0, 0, this);
	rasterChanged = false;
    }

    public boolean isRasterChanged() {
	return rasterChanged;
    }
    
    public JPanel getTopLayer() {
	return this;
    }
    
    public static void main (String[] args) {
	ImageViewer iv = new ImageViewer();
	JFrame f = new JFrame();
	f.getContentPane().add(iv);
	f.pack();
	f.show();

    } // end of main ()
    
}
