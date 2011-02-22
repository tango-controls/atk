// File:          AttributeViewerFactory.java
// Created:       2002-05-17 15:54:17, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-17 15:46:50, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.attribute;
import javax.swing.*;
import java.awt.*;
import com.klg.jclass.chart.beans.*;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.properties.*;
import fr.esrf.tangoatk.widget.image.*;
public class AttributeViewerFactory {

    private static AttributeViewerFactory instance;


    private AttributeViewerFactory() {

    }

    public static AttributeViewerFactory getInstance() {
	if (instance == null) {
	    instance = new AttributeViewerFactory();
	}
	return instance;
    }

    public JComponent getViewer4Attribute(INumberScalar attribute,
					  boolean editable,
					  boolean propertyListEditable,
					  boolean borderVisible) {

	NumberScalarViewer viewer = new NumberScalarViewer(attribute);
	return configure(viewer, editable, propertyListEditable,
			 borderVisible);
    }

    public JComponent getViewer4Attribute(INumberScalar attribute) {
	NumberScalarViewer viewer = new NumberScalarViewer(attribute);
	viewer.setInfoVisible(false);
	return viewer;
    }

    public JComponent getViewer4Attribute(IStringScalar attribute,
					  boolean editable,
					  boolean propertyListEditable,
					  boolean borderVisible) {
	StringScalarViewer viewer = new StringScalarViewer(attribute);

	return configure(viewer, editable, propertyListEditable,
			 borderVisible);
    }

    public JComponent getViewer4Attribute(IStringScalar attribute) {
	StringScalarViewer viewer = new StringScalarViewer(attribute);
	viewer.setInfoVisible(false);
	return viewer;
    }
    
    protected AScalarViewer configure(AScalarViewer viewer,
				      boolean editable,
				      boolean propertyListEditable,
				      boolean borderVisible) {
	viewer.setValueEditable(editable);
	viewer.setPropertyListEditable(propertyListEditable);
	if (!borderVisible) {
	    viewer.setBorder(null);
	}
	return viewer;
    }


    public JComponent getViewer4Attribute(INumberSpectrum attribute,
					  boolean propertyListEditable) {
	JPanel panel = new JPanel();
	panel.setLayout(new GridBagLayout());
	SimpleChart simpleChart = new SimpleChart();
	NumberSpectrumJChartAdapter adapter =
	    new NumberSpectrumJChartAdapter();
	adapter.setChart(simpleChart);
	adapter.setModel(attribute);
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.fill = GridBagConstraints.BOTH;
	panel.add(simpleChart, gbc);
	panel.add(getInfoButton(attribute, propertyListEditable));
	return panel;
    }

    public JComponent getViewer4Attribute(INumberImage attribute,
					  boolean propertyListEditable) {
	ImageViewer siv = new ImageViewer();
	ImageControlAdapter ica = new ImageControlAdapter();
	ica.setModel(siv);
	ica.setModel(attribute);
	siv.setRasterControl(new RasterControl());
	JPanel panel = new JPanel();
	panel.add(siv);
	panel.add(getInfoButton(attribute, propertyListEditable));
	return panel;
    }

    public JButton getInfoButton(IAttribute attribute, boolean editable) {
	JButton info = new JButton("...");
	final boolean e = editable;
	final IAttribute attr = attribute;
        info.addMouseListener(new java.awt.event.MouseAdapter() {
		PropertyFrame f;
		public void mouseClicked(java.awt.event.MouseEvent evt) {
		    if (f == null) {
			f = new PropertyFrame();
			f.setSize(300, 400);
			f.setEditable(e);
			f.setModel(attr);
			f.pack();
		    }
		    f.show();
		    
		}
        });
	return info;
    }
    
    public JComponent getViewer4Attribute(IAttribute attribute,
					  boolean editable,
					  boolean propertyListEditable,
					  boolean borderVisible) {
	if (attribute instanceof IStringScalar) {
	    return getViewer4Attribute((IStringScalar)attribute, editable,
				       propertyListEditable,
				       borderVisible);
	}
	if (attribute instanceof INumberScalar) {
	    return getViewer4Attribute((INumberScalar)attribute, editable,
				       propertyListEditable,
				       borderVisible);
	}
	if (attribute instanceof INumberSpectrum) {
	    return getViewer4Attribute((INumberSpectrum)attribute,
				       propertyListEditable);
	}
	if (attribute instanceof INumberImage) {
	    return getViewer4Attribute((INumberImage)attribute,
				       propertyListEditable);
	}
	
	return null;
    }

    public JComponent getViewer4Attribute(IAttribute attribute) {
	return getViewer4Attribute(attribute, true, true, false);
    }
}

