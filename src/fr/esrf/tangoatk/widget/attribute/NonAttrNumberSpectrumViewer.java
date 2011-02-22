
package fr.esrf.tangoatk.widget.attribute;

/*
 * NonAttrNumberSpectrumViewer.java Created on 12 septembre 2003, 14:34
 */
import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileWriter;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import fr.esrf.tangoatk.core.AttributePolledList;
import fr.esrf.tangoatk.core.IAttribute;
import fr.esrf.tangoatk.core.IRefresherListener;
import fr.esrf.tangoatk.core.util.AttrDualSpectrum;
import fr.esrf.tangoatk.core.util.INonAttrNumberSpectrum;
import fr.esrf.tangoatk.core.util.INonAttrSpectrumListener;
import fr.esrf.tangoatk.core.util.NonAttrNumberSpectrumEvent;
import fr.esrf.tangoatk.widget.util.chart.CfFileReader;
import fr.esrf.tangoatk.widget.util.chart.IJLChartActionListener;
import fr.esrf.tangoatk.widget.util.chart.JLAxis;
import fr.esrf.tangoatk.widget.util.chart.JLChart;
import fr.esrf.tangoatk.widget.util.chart.JLChartActionEvent;
import fr.esrf.tangoatk.widget.util.chart.JLDataView;
import fr.esrf.tangoatk.widget.util.chart.OFormat;
import fr.esrf.tangoatk.widget.util.chart.SearchInfo;

/**
 * @author OUNSY
 */
public class NonAttrNumberSpectrumViewer extends JLChart implements
        INonAttrSpectrumListener, IJLChartActionListener
{
    protected int                      current_model_number = 0;
    protected int                      maximum_model_number = 9;
    protected INonAttrNumberSpectrum[] models               = new INonAttrNumberSpectrum[maximum_model_number];
    protected JLDataView[]             dvy                  = new JLDataView[maximum_model_number];
    protected JLDataView               dvx;
    protected String                   lastConfig           = "";
    public static final Color[]        defaultColor         = { 
        Color.red, Color.blue, Color.cyan, Color.green, Color.magenta,
        Color.orange, Color.pink, Color.yellow, Color.black
    };

    /** Creates a new instance of NonAttrNumberSpectrumViewer */
    public NonAttrNumberSpectrumViewer ()
    {
        // Create the graph
        super();
        setBorder( new javax.swing.border.EtchedBorder() );
        setBackground( new java.awt.Color( 180, 180, 180 ) );
        getY1Axis().setAutoScale( true );
        getY2Axis().setAutoScale( true );
        getXAxis().setAutoScale( true );
        getXAxis().setAnnotation( JLAxis.VALUE_ANNO );
        getXAxis().setLabelFormat( JLAxis.AUTO_FORMAT );
        dvx = new JLDataView();
        getXAxis().addDataView( dvx );
        addUserAction("Save Settings");
        addUserAction("Load Settings");
        addJLChartActionListener(this);
    }

    public void reset ()
    {
        for (int i = 0; i < current_model_number; i++)
        {
            models[i].removeNonAttrSpectrumListener( this );
            getY1Axis().removeDataView( dvy[i] );
        }
        current_model_number = 0;
    }

    protected String[] buildPanelString (SearchInfo si)
    {
        String[] str = new String[4];
        str[0] = si.dataView.getExtendedName() + " " + si.axis.getAxeName();
        str[1] = "Index= " + new Double( si.value.x ).intValue();
        str[2] = "X= " + si.xdataView.formatValue( si.xdataView.getTransformedValue( si.xvalue.y ) );
        str[3] = "Y= " + si.dataView.formatValue( si.dataView.getTransformedValue( si.value.y ) ) + " " + si.dataView.getUnit();
        return str;
    }

    protected int findModelIndex (INonAttrNumberSpectrum v)
    {
        int model_index = -1;
        while (++model_index < current_model_number && models[model_index] != v)
            ;
        if ( model_index == current_model_number ) model_index = -1;
        return model_index;
    }

    public void spectrumChange (NonAttrNumberSpectrumEvent numberSpectrumEvent)
    {
        INonAttrNumberSpectrum source = (INonAttrNumberSpectrum) numberSpectrumEvent
                .getSource();
        int model_index = findModelIndex( source );
        if ( model_index != -1 )
        {
            double[] xvalue = numberSpectrumEvent.getXValue();
            double[] yvalue = numberSpectrumEvent.getYValue();
            int length = xvalue.length;
            dvx.reset();
            for (int i = 0; i < length; i++)
            {
                dvx.add( (double) i, xvalue[i] );
            }
            length = yvalue.length;
            JLDataView dvy_i = dvy[model_index];
            dvy_i.reset();
            for (int i = 0; i < length; i++)
            {
                dvy_i.add( (double) i, yvalue[i] );
            }
            // Commit change
            repaint();
        }
    }

    /**
     * <code>setModel</code> Set the value of model.
     * 
     * @param v
     *            Value to assign to model.
     */
    public void addModel (INonAttrNumberSpectrum v)
    {
        if ( v != null && ( current_model_number < maximum_model_number )
                && ( findModelIndex( v ) == -1 ) )
        {
            models[current_model_number] = v;
            if ( current_model_number == 0 )
            {
                dvx.setName( v.getXName() );
            }
            JLDataView dvy_new = new JLDataView();
            dvy_new.setUnit( v.getYUnit() );
            dvy_new.setName( v.getYName() );
            dvy_new.setColor( defaultColor[current_model_number] );
            dvy_new.setMarkerColor( defaultColor[current_model_number] );
            getY1Axis().addDataView( dvy_new );
            dvy[current_model_number] = dvy_new;
            current_model_number++;
            v.addNonAttrSpectrumListener( this );
        }
    }

    /**
     * <code>setSettings()</code> Applies graph configuration given as string return
     * error string or an empty string when succesfull
     */
    public String setSettings (String cfg)
    {
        CfFileReader f = new CfFileReader();
        Vector p;
        if ( !f.parseText( cfg ) )
        {
            return "NumberSpectrumViewer.setSettings: Failed to parse given config";
        }
        // General settings
        p = f.getParam( "graph_title" );
        if ( p != null ) setHeader( OFormat.getName( p.get( 0 ).toString() ) );
        p = f.getParam( "label_visible" );
        if ( p != null ) setLabelVisible( OFormat.getBoolean( p.get( 0 ).toString() ) );
        p = f.getParam( "graph_background" );
        if ( p != null ) setBackground( OFormat.getColor( p ) );
        p = f.getParam( "title_font" );
        if ( p != null ) setHeaderFont( OFormat.getFont( p ) );
        // xAxis
        JLAxis a = getXAxis();
        p = f.getParam( "xgrid" );
        if ( p != null ) a.setGridVisible( OFormat.getBoolean( p.get( 0 ).toString() ) );
        p = f.getParam( "xsubgrid" );
        if ( p != null ) a.setSubGridVisible( OFormat.getBoolean( p.get( 0 ).toString() ) );
        p = f.getParam( "xgrid_style" );
        if ( p != null ) a.setGridStyle( OFormat.getInt( p.get( 0 ).toString() ) );
        p = f.getParam( "xmin" );
        if ( p != null ) a.setMinimum( OFormat.getDouble( p.get( 0 ).toString() ) );
        p = f.getParam( "xmax" );
        if ( p != null ) a.setMaximum( OFormat.getDouble( p.get( 0 ).toString() ) );
        p = f.getParam( "xautoscale" );
        if ( p != null ) a.setAutoScale( OFormat.getBoolean( p.get( 0 )
                .toString() ) );
        p = f.getParam( "xcale" );
        if ( p != null ) a.setScale( OFormat.getInt( p.get( 0 ).toString() ) );
        p = f.getParam( "xformat" );
        if ( p != null ) a.setLabelFormat( OFormat.getInt( p.get( 0 ).toString() ) );
        p = f.getParam( "xtitle" );
        if ( p != null ) a.setName( OFormat.getName( p.get( 0 ).toString() ) );
        p = f.getParam( "xcolor" );
        if ( p != null ) a.setAxisColor( OFormat.getColor( p ) );
        p = f.getParam( "xlabel_font" );
        if ( p != null ) a.setFont( OFormat.getFont( p ) );
        // y1Axis
        a = getY1Axis();
        p = f.getParam( "y1grid" );
        if ( p != null ) a.setGridVisible( OFormat.getBoolean( p.get( 0 ).toString() ) );
        p = f.getParam( "y1subgrid" );
        if ( p != null ) a.setSubGridVisible( OFormat.getBoolean( p.get( 0 ).toString() ) );
        p = f.getParam( "y1grid_style" );
        if ( p != null ) a.setGridStyle( OFormat.getInt( p.get( 0 ).toString() ) );
        p = f.getParam( "y1min" );
        if ( p != null ) a.setMinimum( OFormat
                .getDouble( p.get( 0 ).toString() ) );
        p = f.getParam( "y1max" );
        if ( p != null ) a.setMaximum( OFormat
                .getDouble( p.get( 0 ).toString() ) );
        p = f.getParam( "y1autoscale" );
        if ( p != null ) a.setAutoScale( OFormat.getBoolean( p.get( 0 ).toString() ) );
        p = f.getParam( "y1cale" );
        if ( p != null ) a.setScale( OFormat.getInt( p.get( 0 ).toString() ) );
        p = f.getParam( "y1format" );
        if ( p != null ) a.setLabelFormat( OFormat.getInt( p.get( 0 ).toString() ) );
        p = f.getParam( "y1title" );
        if ( p != null ) a.setName( OFormat.getName( p.get( 0 ).toString() ) );
        p = f.getParam( "y1color" );
        if ( p != null ) a.setAxisColor( OFormat.getColor( p ) );
        p = f.getParam( "y1label_font" );
        if ( p != null ) a.setFont( OFormat.getFont( p ) );
        return "";
    }

    /**
     * <code>getSettings()</code> Return graph configuration as string
     */
    public String getSettings ()
    {
        String to_write = "";
        // General settings
        to_write += "graph_title:\'" + getHeader() + "\'\n";
        to_write += "label_visible:" + isLabelVisible() + "\n";
        to_write += "graph_background:" + OFormat.color( getBackground() ) + "\n";
        to_write += "title_font:" + OFormat.font( getHeaderFont() ) + "\n";
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
        to_write += "xcolor:" + OFormat.color( getXAxis().getAxisColor() ) + "\n";
        to_write += "xlabel_font:" + OFormat.font( getXAxis().getFont() ) + "\n";
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
        to_write += "y1color:" + OFormat.color( getY1Axis().getAxisColor() ) + "\n";
        to_write += "y1label_font:" + OFormat.font( getY1Axis().getFont() ) + "\n";
        // y2Axis
        to_write += "y2grid:" + getY2Axis().isGridVisible() + "\n";
        to_write += "y2subgrid:" + getY2Axis().isSubGridVisible() + "\n";
        to_write += "y2grid_style:" + getY2Axis().getGridStyle() + "\n";
        to_write += "y2min:" + getY2Axis().getMinimum() + "\n";
        to_write += "y2max:" + getY2Axis().getMaximum() + "\n";
        to_write += "y2autoscale:" + getY2Axis().isAutoScale() + "\n";
        to_write += "y2cale:" + getY2Axis().getScale() + "\n";
        to_write += "y2format:" + getY2Axis().getLabelFormat() + "\n";
        to_write += "y2title:\'" + getY2Axis().getName() + "\'\n";
        to_write += "y2color:" + OFormat.color( getY2Axis().getAxisColor() ) + "\n";
        to_write += "y2label_font:" + OFormat.font( getY2Axis().getFont() ) + "\n";
 
        Vector views = new Vector();
        if (getXAxis().isXY()) views.addAll(getXAxis().getViews());
        views.addAll(getY1Axis().getViews());
        views.addAll(getY2Axis().getViews());
        to_write += "dv_number:" + views.size() + "\n";
        for (int i = 0; i < views.size(); i++) {
          JLDataView data = (JLDataView) views.get(i);
          to_write += "dv" + i + "_name:\'" + data.getName() + "\'\n";
          to_write += data.getConfiguration("dv" + i);
          data = null;
        }
        views.clear();
        views = null;
        return to_write;
    }

    /**
     * Apply a configuration.
     * 
     * @param txt
     *            Configuration text.
     * @return An error string or An empty string when succes
     * @see #getSettings
     */
    public String setSetting (String txt)
    {
        CfFileReader f = new CfFileReader();
        // Read and browse the file
        if ( !f.parseText( txt ) )
        {
            return "NonAttrNumberSpectrumViewer.setSettings: Failed to parse given text";
        }
        return applySettings( f );
    }

    /**
     * Save settings.
     * 
     * @param filename
     *            file to be saved.
     */
    public void saveSetting (String filename)
    {
        try
        {
            FileWriter f = new FileWriter( filename );
            String s = getSettings();
            f.write( s, 0, s.length() );
            f.close();
            lastConfig = filename;
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog( null, "Failed to write "
                    + filename, "Error", JOptionPane.ERROR_MESSAGE );
        }
    }

    /**
     *  Load graph settings.
     * @param filename file to be read
     * @return An error string or An empty string when succes
     */
    public String loadSetting(String filename) {

      CfFileReader f = new CfFileReader();

      // Read and browse the file
      if (!f.readFile(filename)) {
        return "Failed to read " + filename;
      }
      lastConfig = filename;

      return applySettings(f);
    }

    protected String applySettings (CfFileReader f)
    {
        String errBuff = "";
        Vector p;
        int i, nbDv;
        setMaxDisplayDuration( Double.POSITIVE_INFINITY );
        // Reset display duration (to avoid history reading side FX)
        setDisplayDuration( Double.POSITIVE_INFINITY );
        // Get all dataviews
        p = f.getParam( "dv_number" );
        Vector views = new Vector();
        if (getXAxis().isXY()) views.addAll(getXAxis().getViews());
        views.addAll(getY1Axis().getViews());
        views.addAll(getY2Axis().getViews());
        if ( p != null )
        {
            try
            {
                nbDv = Integer.parseInt( p.get( 0 ).toString() );
            }
            catch (NumberFormatException e)
            {
                errBuff += "dv_number: invalid number\n";
                return errBuff;
            }
            // Build attribute list
            for (i = 0; i < nbDv; i++)
            {
                p = f.getParam( "dv" + i + "_name" );
                if ( p == null )
                {
                    errBuff += ( "Unable to find dv" + i + "_name param\n" );
                    return errBuff;
                }
                String name = p.get(0).toString();
                for (int j = 0; j < views.size(); j++)
                {
                    JLDataView data = (JLDataView)views.get(j);
                    if (name.equals(data.getName()))
                    {
                        data.applyConfiguration("dv" + i, f);
                        break;
                    }
                    data = null;
                }
                name = null;
            }
        }
        else
        {
            nbDv = 0;
        }
        views.clear();
        views = null;
        // Now we can set up the graph
        // General settings
        applyConfiguration( f );
        // Axis
        getXAxis().applyConfiguration( "x", f );
        getY1Axis().applyConfiguration( "y1", f );
        getY2Axis().applyConfiguration( "y2", f );
        return errBuff;
    }

    protected void loadButtonActionPerformed ()
    {
        int ok = JOptionPane.YES_OPTION;
        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter( new FileFilter() {
            public boolean accept (File f)
            {
                if ( f.isDirectory() )
                {
                    return true;
                }
                String extension = getExtension( f );
                if ( extension != null && extension.equals( "txt" ) ) return true;
                return false;
            }

            public String getDescription ()
            {
                return "text files ";
            }
        } );
        if ( lastConfig.length() > 0 ) chooser.setSelectedFile( new File( lastConfig ) );
        int returnVal = chooser.showOpenDialog( null );
        if ( returnVal == JFileChooser.APPROVE_OPTION )
        {
            File f = chooser.getSelectedFile();
            if ( f != null )
            {
                if ( ok == JOptionPane.YES_OPTION )
                {
                    String err = loadSetting( f.getAbsolutePath() );
                    if ( err.length() > 0 )
                    {
                        JOptionPane.showMessageDialog( null, err,
                                "Errors reading " + f.getName(),
                                JOptionPane.ERROR_MESSAGE );
                    }
                    repaint();
                }
            }
        }
    }

    /**
     * <code>getExtension</code> returns the extension of a given file, that
     * is the part after the last `.' in the filename.
     * 
     * @param f
     *            a <code>File</code> value
     * @return a <code>String</code> value
     */
    protected String getExtension (File f)
    {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf( '.' );
        if ( i > 0 && i < s.length() - 1 )
        {
            ext = s.substring( i + 1 ).toLowerCase();
        }
        return ext;
    }

    public void actionPerformed(JLChartActionEvent evt)
    {
        if (evt.getName().equals("Load Settings"))
        {
            loadButtonActionPerformed();
        }
        else if (evt.getName().equals("Save Settings"))
        {
            saveButtonActionPerformed();
        }
    }

    protected void saveButtonActionPerformed ()
    {
        int ok = JOptionPane.YES_OPTION;
        JFileChooser chooser = new JFileChooser( "." );
        chooser.addChoosableFileFilter( new FileFilter() {
            public boolean accept (File f)
            {
                if ( f.isDirectory() )
                {
                    return true;
                }
                String extension = getExtension( f );
                if ( extension != null && extension.equals( "txt" ) ) return true;
                return false;
            }

            public String getDescription ()
            {
                return "text files ";
            }
        } );
        if ( lastConfig.length() > 0 ) chooser.setSelectedFile( new File(
                lastConfig ) );
        int returnVal = chooser.showSaveDialog( null );
        if ( returnVal == JFileChooser.APPROVE_OPTION )
        {
            File f = chooser.getSelectedFile();
            if ( f != null )
            {
                if ( getExtension( f ) == null )
                {
                    f = new File( f.getAbsolutePath() + ".txt" );
                }
                if ( f.exists() ) ok = JOptionPane.showConfirmDialog( null,
                        "Do you want to overwrite " + f.getName() + " ?",
                        "Confirm overwrite", JOptionPane.YES_NO_OPTION );
                if ( ok == JOptionPane.YES_OPTION )
                {
                    saveSetting( f.getAbsolutePath() );
                }
            }
        }
    }

    public boolean getActionState (JLChartActionEvent evt)
    {
        return false;
    }

    public static void main (String[] args)
    {
        JFrame jframe = new JFrame();
        final NonAttrNumberSpectrumViewer viewer = new NonAttrNumberSpectrumViewer();
        try
        {
            AttributePolledList attributelist = new AttributePolledList();
            String xname = "tango/tangotest/1/double_spectrum_ro";
            String yname = "tango/tangotest/1/float_spectrum_ro";
            if (args.length == 2)
            {
                xname = args[0];
                yname = args[1];
            }
            IAttribute xattr = (IAttribute) attributelist.add( xname );
            IAttribute yattr = (IAttribute) attributelist.add( yname );
            final AttrDualSpectrum dual = new AttrDualSpectrum( 
                    xattr.getDevice(),
                    xattr.getNameSansDevice(),
                    yattr.getDevice(),
                    yattr.getNameSansDevice()
            );
            dual.setYUnit( "UNITE" );
            dual.setYName( "NOM" );
            dual.setRefreshInterval( 1000 );
            viewer.addModel( dual );
            jframe.getContentPane().setLayout( new GridLayout( 1, 1 ) );
            jframe.getContentPane().add( viewer );
            jframe.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            jframe.setTitle( "DualSpectrumViewer:" );
            jframe.setSize( 640, 480 );
            jframe.setVisible( true );
            attributelist.addRefresherListener( new IRefresherListener() {
                public void refreshStep ()
                {
                    dual.refresh();
                    viewer.repaint();
                }
            } );
            attributelist.startRefresher();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
