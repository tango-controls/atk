// File:          ImageControlAdapter.java
// Created:       2002-06-06 10:29:12, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-16 14:59:8, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.attribute;
import java.awt.image.*;
import javax.swing.*;
import fr.esrf.tangoatk.widget.image.*;
import fr.esrf.tangoatk.widget.properties.PropertyListViewer2;
import fr.esrf.tangoatk.core.IImageListener;

/** <code>ImageControlAdapter</code> serves as an adapter between a
 * <code>fr.esrf.tangoatk.core.INumberImage</code> and a
 * <code>fr.esrf.tangoatk.widget.image.IImageViewer</code>. It will
 * provide the <code>IImageViewer</code> with a panel describing the
 * properties of the attribute this adapter is representing. <p> When
 * an update of the attribute is received, the <code>setRaster(double
 * [][] raster)</code> of the <code>IImageViewer</code> is called
 * along with its repaint.
 * @see fr.esrf.tangoatk.widget.image.IImageViewer
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Version$
 */
public class ImageControlAdapter implements IImageListener {

    fr.esrf.tangoatk.core.INumberImage model;
    IImageViewer viewer;

    class PropertyAdapter extends PropertyListViewer2
	implements IImagePanel {

	public PropertyAdapter(java.util.Map properties) {
	    setModel(properties);
	}

	public void ok() {
	    getRootPane().getParent().setVisible(false);
	}

	public JComponent getComponent() {
	    return this;
	}
	
	public String getName() {
	    return "Properties";
	}
    }

    
    /**
     * <code>setModel</code>
     *
     * @param viewer an <code>IImageViewer</code> value
     * @deprecated please use setImageViewer instead;
     */
    public void setModel(IImageViewer viewer) {
	setImageViewer(viewer);
    }
    
    /**
     * <code>setImageViewer</code>
     *
     * @param viewer an <code>IImageViewer</code> value
     * @deprecated use setViewer instead
     */
    public void setImageViewer(IImageViewer viewer) {
	this.viewer = viewer;
    }

    public void setViewer(IImageViewer viewer) {
	this.viewer = viewer;
    }

    public void imageChange(fr.esrf.tangoatk.core.NumberImageEvent evt) {
	viewer.setRaster(evt.getValue());
	viewer.repaint();
	
    }

    public void stateChange(fr.esrf.tangoatk.core.AttributeStateEvent evt) {

    }

    public void errorChange(fr.esrf.tangoatk.core.ErrorEvent evt) {

    }

    public void setModel(fr.esrf.tangoatk.core.INumberImage image) {
	if (model != null) {
	    model.removeImageListener(this);
	}

	try {
	    viewer.addImagePanel
		(new PropertyAdapter(image.getPropertyMap()));
	} catch (Exception e) {
	    throw new IllegalStateException
		("Please do a setModel(IImageViewer) before you " +
		 "do a setModel(INumberImage)");
	}
	this.model = image;
	model.addImageListener(this);
    }

}
				     
