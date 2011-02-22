/*
 * SimpleImageViewer.java
 *
 * Created on May 7, 2002, 10:23 AM
 */

package fr.esrf.tangoatk.widget.attribute;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import fr.esrf.tangoatk.core.*;
import java.awt.geom.AffineTransform;
/**
 *
 * @author  root
 */
public class SimpleImageViewer extends JPanel
    implements fr.esrf.tangoatk.core.IImageListener,
	       IImageViewer, ImageObserver {
    INumberImage model;
    JPanel canvas;
    Image image;
    int threshold;
    int bucketSize = 8;
    int zoomLevel = 0;
    JFrame controlFrame;
    ImageController controller;
    double [][] raster;
    int startx = -1, starty = -1 , endx = -1 , endy = -1;
    int height, width;
    int mode = TRUE_COLOR;
    int roiStartX, roiStartY, roiEndX, roiEndY;
    boolean eightColors = false;
    AffineTransform scale = AffineTransform.getScaleInstance(1, 1);
    IImageControl [] listenerList;
    /** Creates new form SimpleImageViewer */

    public SimpleImageViewer() {
	canvas = new JPanel() {
		public void paintComponent(Graphics g) {
		    internalPaint(g);
		}
	    };

	controlFrame = new JFrame();
	listenerList = new IImageControl[0];
	
	canvas.addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
		    if (e.getButton() == MouseEvent.BUTTON1) {
			startRect(e.getX(), e.getY());			
		    }
		}

		public void mouseReleased(MouseEvent e) {
		    if (e.getButton() == MouseEvent.BUTTON1) {
			endRect(e.getX(), e.getY());			
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

	canvas.addMouseMotionListener(new MouseMotionAdapter() {
		public void mouseDragged(MouseEvent e) {
		    dragRect(e.getX(), e.getY());
		}
	    });



	controller = new ImageController();
	controller.setModel(this);
	controlFrame.setContentPane(controller);
	add(canvas);
    }


    public void addImageListener(IImageControl l) {
	int i = listenerList.length;
	IImageControl [] tmp = new IImageControl[i + 1];
	System.arraycopy(listenerList, 0, tmp, 0, i);
	tmp[i] = l;
	listenerList = tmp;
    }

    public void removeImageListener(IImageControl l) {
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
	IImageControl[] tmp = new IImageControl[length -1];
	System.arraycopy(listenerList, 0, tmp, 0, length - 1);
	listenerList = tmp;

    }

    public void setControllerVisible(boolean visible) {
	controlFrame.pack();
	controlFrame.setVisible(visible);
    }

    public ImageController getController() {
	return controller;
    }
    
    public boolean isControllerVisible() {
	return controlFrame.isVisible();
    }
	
    protected void internalPaint(Graphics g) {
	if (image != null) {
	    ((Graphics2D)g).drawImage(image, scale, this);
	}
	if (endy != -1 && endx != -1) {
	    g.setXORMode(Color.white);
	    g.drawRect(startx, starty, endx - startx, endy - starty);
	    
	}

	
	for (int i = 0; i < listenerList.length; i++) {
	    listenerList[i].draw(g);
	}
    }

    protected void startRect(int x, int y) {
	startx = x;
	starty = y;
	endx = x;
	endy = y;
    }

    protected void dragRect(int x, int y) {
	endx = x ; 
	endy = y ;
	repaint();
    }

    protected void endRect(int x, int y) {
 	roiEndX   = endx;
 	roiStartX = startx + roiStartX;
 	roiEndY   = endy;
 	roiStartY = starty + roiStartY;

	startx = starty = endx = endy = -1;
	
	for (int i = 0; i < listenerList.length; i++) {
	    listenerList[i].roiChanged(roiStartX,
								roiEndX,
								roiStartY,
								roiEndY);
	    
	}
    }

    public void setSize(java.awt.Dimension size) {
	canvas.setPreferredSize(size);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */


    
    public void setModel(INumberImage image) {
	if (model != null) {
	    model.removeImageListener(this);
	}

	model = image;
	setImage(new BufferedImage(model.getMaxXDimension(),
				       model.getMaxYDimension(),
				       BufferedImage.TYPE_INT_RGB));
	height = model.getMaxXDimension();
	width  = model.getMaxYDimension();
	
	canvas.setPreferredSize(new Dimension(model.getMaxXDimension(),
					    model.getMaxYDimension()));
	
	model.addImageListener(this);
	roiStartX = roiStartY = 0;
	roiEndX = model.getMaxXDimension();
	roiEndY = model.getMaxYDimension();
    }

    public Image getImage() {
	return image;
    }

    public void setImage(Image image) {
	this.image = image;
	drawRaster();
    }

    public int getXStart() {
	return roiStartX;
    }

    public int getXEnd() {
	return roiEndX;
    }

    public int getYStart() {
	return roiStartY;
    }

    public int getYEnd() {
	return roiEndY;
    }
    
    public IAttribute getModel() {
	return (IAttribute)model;
    }

    public boolean isValueEditable() {
	return false;
    }
    
    public void imageChange(NumberImageEvent evt) {
	raster = evt.getValue();

	drawRaster();

    }

    synchronized protected void drawRaster() {
	Color color;

	try {
	    for (int y = 0; y < raster.length; y++) {
		for (int x = 0; x < raster[y].length; x++) {
		    int val = (int)raster[y][x];

		    switch (mode) {
			case EIGHT_COLOR:

			    do {
				if (val < bucketSize * 1) {
				    color = Color.black;
				    break;
				}

				if (val < bucketSize * 2) {
				    color = Color.blue;
				    break;
				}
				if (val < bucketSize * 3) {
				    color = Color.cyan;
				    break;
				}
				if (val < bucketSize * 4) {
				    color = Color.green;
				    break;
				}
				if (val < bucketSize * 5) {
				    color = Color.magenta;
				    break;
				}
				if (val < bucketSize * 6) {
				    color = Color.pink;
				    break;
				}
				if (val < bucketSize * 7) {
				    color = Color.red;
				    break;
				}

				color = Color.white;

			    } while (false);
			    val = color.getRGB();
			    break;
			    
		    case  BINARIZE:
			val = val < threshold ? 0 : 255;
			break;

		    } // end of switch ()
		    
		    ((BufferedImage)image).setRGB(x, y, val);
		
	    } // end of for ()
	} // end of for ()
	} catch (Exception e) {
	    System.out.println("Setting outside limit " +
			       (roiStartY) + " " + (roiStartX));
	} // end of try-catch
		

	repaint();
    }


    public void setThreshold(int threshold) {
	this.threshold = threshold;
	if (raster != null) {
	    drawRaster();
	}
    }

    public int getThreshold() {
	return threshold;
    }
    


    public void setBucketSize(int i) {
	this.bucketSize = i;
	if (raster != null) {
	    drawRaster();
	}
    }

    public int getBucketSize() {
	return bucketSize;
    }
    

    public void setMode(int i) {
	mode = i;
	if (raster != null) {
	    drawRaster();	    
	}
	

    }

    public void setZoomLevel(int i) {
	zoomLevel = i;
	double j = (zoomLevel/100D) + 1;
	scale = AffineTransform.getScaleInstance(j, j);

	if (raster != null) {
	    drawRaster();
	}
    }

    public int getZoomLevel() {
	return zoomLevel;
    }
    
    public int getMode() {
	return mode;
    }

    public void errorChange(ErrorEvent evt) {

    }

    public void stateChange(AttributeStateEvent evt) {

    }


    public static void main (String[] args) throws Exception {
	JFrame f = new JFrame();
	SimpleImageViewer siv = new SimpleImageViewer();
	ImageController ic = siv.getController();
	fr.esrf.tangoatk.core.AttributeList list =
	    new fr.esrf.tangoatk.core.AttributeList();
	
	INumberImage image = (INumberImage)list.add("fe/imacq/2/Image");

	class MyController extends JPanel implements IImageControlPanel {
	    IImageViewer viewer;
	    BufferedImage image = new BufferedImage(256,
						    200,
						    BufferedImage.TYPE_INT_RGB);
	    public MyController() {
		int k = 0;
		for (int i = 0; i < 256; i++) {
		    for (int j = 0; j < 200; j++) {
			image.setRGB(i, j, i);
		    } // end of for ()
		} // end of for ()
	    }
	    
	
	    public void setModel(IImageViewer viewer) {
		this.viewer = viewer;
		viewer.setSize(new java.awt.Dimension(256, 200));
	    }

	    public JComponent getComponent() {
		return this;
	    }

	    public String getName() {
		return "My Controller";
	    }

	    public void draw(Graphics g) {
		g.drawImage(image, 0, 0, this);
	    }

	    public void roiChanged(int startx, int endx,
				   int starty, int endy) {
	    }
	    
		
	}

	//	siv.setModel(image);
	ic.addToPanel(new MyController());
	
	list.setRefreshInterval(3000);
	list.startRefresher();

	f.getContentPane().add(siv);
	f.pack();f.show();
	
    } // end of main ()
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
