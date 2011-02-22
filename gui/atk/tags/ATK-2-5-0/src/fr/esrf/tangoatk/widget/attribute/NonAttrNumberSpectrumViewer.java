package fr.esrf.tangoatk.widget.attribute;

/*
 * NonAttrNumberSpectrumViewer.java
 *
 * Created on 12 septembre 2003, 14:34
 */

import java.awt.*;
import java.util.*;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.chart.*;

/**
 *
 * @author  OUNSY
 */
public class NonAttrNumberSpectrumViewer extends JLChart implements INonAttrSpectrumListener {

    private int current_model_number = 0;
    private int maximum_model_number = 9;
    INonAttrNumberSpectrum[] models = new INonAttrNumberSpectrum[maximum_model_number];
    JLDataView[]      dvy = new JLDataView[maximum_model_number];
    JLDataView	    dvx;

    public static final Color[] defaultColor = {
	Color.red,
	Color.blue,
	Color.cyan,
	Color.green,
	Color.magenta,
	Color.orange,
	Color.pink,
	Color.yellow,
	Color.black };

    /** Creates a new instance of NonAttrNumberSpectrumViewer */
    public NonAttrNumberSpectrumViewer() {
    
      // Create the graph
      super();
      
      setBorder(new javax.swing.border.EtchedBorder());
      setBackground(new java.awt.Color(180, 180, 180));
      getY1Axis().setAutoScale(true);
      getY2Axis().setAutoScale(true);
      getXAxis().setAutoScale(true);  
      
      dvx = new JLDataView();
      getXAxis().addDataView( dvx );

    }

    public  void reset() {
	for (int i=0 ; i < current_model_number ; i++)
	{
	    models[i].removeNonAttrSpectrumListener(this);
	    getY1Axis().removeDataView( dvy[i] );
	}
	current_model_number = 0;
    }

    private int findModelIndex(INonAttrNumberSpectrum  v) {
	int model_index = -1 ;
	while ( ++model_index < current_model_number && models[model_index] != v );
	if (model_index == current_model_number) model_index = -1;
	return model_index;
    }

    public void spectrumChange(NonAttrNumberSpectrumEvent numberSpectrumEvent) {
	INonAttrNumberSpectrum source = (INonAttrNumberSpectrum)numberSpectrumEvent.getSource();
	int model_index = findModelIndex(source) ;
	if (model_index != -1) {
            double [] xvalue = numberSpectrumEvent.getXValue();
            double [] yvalue = numberSpectrumEvent.getYValue();
            int length = xvalue.length;
            //System.out.println("xvalue length :" +length);
            dvx.reset();
            for (int i = 0; i < length; i++) {
            dvx.add( (double)i , xvalue[i] );
            }
            length = yvalue.length;
            //System.out.println("yvalue length :" + length);
            JLDataView dvy_i = dvy[model_index];
            dvy_i.reset();
            for (int i = 0; i < length; i++) {
            dvy_i.add( (double)i , yvalue[i]  );
            }
        
            // Commit change
            repaint();
        }
    }    

		
    /**<code>setModel</code> Set the value of model.
     * @param v  Value to assign to model.
     */
    public void addModel(INonAttrNumberSpectrum  v) {

	if( v!=null && (current_model_number < maximum_model_number)
		    && (findModelIndex(v) == -1 )
	  )
	{

	  models[current_model_number] = v ;
	  JLDataView dvy_new = new JLDataView();
	  dvy_new.setUnit( v.getYUnit() );
	  dvy_new.setName( v.getYName() );
	  dvy_new.setColor( defaultColor[current_model_number] );
	  dvy_new.setMarkerColor( defaultColor[current_model_number] );
	  getY1Axis().addDataView( dvy_new );

	  dvy[current_model_number] = dvy_new ;

	  current_model_number++;

	  v.addNonAttrSpectrumListener(this);
	}
    }
    
    /**
     * <code>getSettings()</code> Return graph configuration as string
     * return error string or an empty string when succesfull
     */
    public String setSettings(String cfg) {
    
       CfFileReader f = new CfFileReader();
       Vector	    p;
	      
       if( !f.parseText(cfg) ) {
	 return "NumberSpectrumViewer.setSettings: Failed to parse given config";
       }
       
       // General settings
       p = f.getParam( "graph_title" );
       if( p!=null ) setHeader(OFormat.getName(p.get(0).toString()));
       p = f.getParam( "label_visible" );
       if( p!=null ) setLabelVisible(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "graph_background" );
       if( p!=null ) setBackground(OFormat.getColor(p));
       p = f.getParam( "title_font" );
       if( p!=null ) setHeaderFont(OFormat.getFont(p));

       // xAxis
       JLAxis a = getXAxis();
       p = f.getParam( "xgrid" );
       if( p!=null ) a.setGridVisible(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "xsubgrid" );
       if( p!=null ) a.setSubGridVisible(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "xgrid_style" );
       if( p!=null ) a.setGridStyle(OFormat.getInt(p.get(0).toString()));
       p = f.getParam( "xmin" );
       if( p!=null ) a.setMinimum(OFormat.getDouble(p.get(0).toString()));
       p = f.getParam( "xmax" );
       if( p!=null ) a.setMaximum(OFormat.getDouble(p.get(0).toString()));
       p = f.getParam( "xautoscale" );
       if( p!=null ) a.setAutoScale(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "xcale" );
       if( p!=null ) a.setScale(OFormat.getInt(p.get(0).toString()));
       p = f.getParam( "xformat" );
       if( p!=null ) a.setLabelFormat(OFormat.getInt(p.get(0).toString()));
       p = f.getParam( "xtitle" );
       if( p!=null ) a.setName(OFormat.getName(p.get(0).toString()));
       p = f.getParam( "xcolor" );
       if( p!=null ) a.setAxisColor(OFormat.getColor(p));
       p = f.getParam( "xlabel_font" );
       if( p!=null ) a.setFont(OFormat.getFont(p));

       // y1Axis
       a = getY1Axis();
       p = f.getParam( "y1grid" );
       if( p!=null ) a.setGridVisible(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "y1subgrid" );
       if( p!=null ) a.setSubGridVisible(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "y1grid_style" );
       if( p!=null ) a.setGridStyle(OFormat.getInt(p.get(0).toString()));
       p = f.getParam( "y1min" );
       if( p!=null ) a.setMinimum(OFormat.getDouble(p.get(0).toString()));
       p = f.getParam( "y1max" );
       if( p!=null ) a.setMaximum(OFormat.getDouble(p.get(0).toString()));
       p = f.getParam( "y1autoscale" );
       if( p!=null ) a.setAutoScale(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "y1cale" );
       if( p!=null ) a.setScale(OFormat.getInt(p.get(0).toString()));
       p = f.getParam( "y1format" );
       if( p!=null ) a.setLabelFormat(OFormat.getInt(p.get(0).toString()));
       p = f.getParam( "y1title" );
       if( p!=null ) a.setName(OFormat.getName(p.get(0).toString()));
       p = f.getParam( "y1color" );
       if( p!=null ) a.setAxisColor(OFormat.getColor(p));
       p = f.getParam( "y1label_font" );
       if( p!=null ) a.setFont(OFormat.getFont(p));
       
       return "";    
    }
    
    public String getSettings() {
    
       String to_write="";
		
       // General settings
       to_write += "graph_title:\'" + getHeader() + "\'\n";
       to_write += "label_visible:" + isLabelVisible() + "\n";
       to_write += "graph_background:" + OFormat.color(getBackground()) + "\n";
       to_write += "title_font:" + OFormat.font(getHeaderFont()) + "\n";
      
	// xAxis
	to_write += "xgrid:" + getXAxis().isGridVisible() + "\n";
	to_write += "xsubgrid:" + getXAxis().isSubGridVisible() + "\n";
	to_write += "xgrid_style:" + getXAxis().getGridStyle() + "\n";
	to_write += "xmin:" + getXAxis().getMinimum() + "\n";
	to_write += "xmax:" + getXAxis().getMaximum() + "\n";
	to_write += "xautoscale:" + getXAxis().isAutoScale() + "\n";
	to_write += "xcale:" + getXAxis().getScale() + "\n";
	to_write += "xformat:" + getXAxis().getLabelFormat() + "\n";
	to_write += "xtitle:\'" + getXAxis().getName() + "\'\n";
	to_write += "xcolor:" + OFormat.color(getXAxis().getAxisColor()) + "\n";
	to_write += "xlabel_font:" + OFormat.font(getXAxis().getFont()) + "\n";

	// y1Axis
	to_write += "y1grid:" + getY1Axis().isGridVisible() + "\n";
	to_write += "y1subgrid:" + getY1Axis().isSubGridVisible() + "\n";
	to_write += "y1grid_style:" + getY1Axis().getGridStyle() + "\n";
	to_write += "y1min:" + getY1Axis().getMinimum() + "\n";
	to_write += "y1max:" + getY1Axis().getMaximum() + "\n";
	to_write += "y1autoscale:" + getY1Axis().isAutoScale() + "\n";
	to_write += "y1cale:" + getY1Axis().getScale() + "\n";
	to_write += "y1format:" + getY1Axis().getLabelFormat() + "\n";
	to_write += "y1title:\'" + getY1Axis().getName() + "\'\n";
	to_write += "y1color:" + OFormat.color(getY1Axis().getAxisColor()) + "\n";
	to_write += "y1label_font:" + OFormat.font(getY1Axis().getFont()) + "\n";

	return to_write;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
