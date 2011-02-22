package fr.esrf.tangoatk.widget.attribute;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import fr.esrf.tangoatk.core.AttributePolledList;
import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.INumberSpectrum;
import fr.esrf.tangoatk.core.ISpectrumListener;
import fr.esrf.tangoatk.core.NumberSpectrumEvent;
import fr.esrf.tangoatk.widget.attribute.NumberSpectrumViewer;
import fr.esrf.tangoatk.widget.util.chart.DataList;
import fr.esrf.tangoatk.widget.util.chart.JLDataView;

/**
 * A component using a NumberSpectrumViewer, and which allows to remember the
 * Values of the JLDataView of the NumberSpectrumViewer.
 * 
 * @author GIRARDOT
 */
public class NumberSpectrumRecorder extends JPanel implements ISpectrumListener, ActionListener {

    //Default Color
    protected static final Color[] defaultColor   = {
            Color.red, Color.blue, Color.magenta, Color.orange,
            Color.cyan, Color.green, Color.pink, Color.yellow, Color.black
    };
    protected int                  index          = 0;
    protected NumberSpectrumViewer viewer         = null;
    protected JLDataView           dvy            = null;
    protected INumberSpectrum      model          = null;
    protected Vector               snapshots      = null;
    protected JButton              snapshotButton = null;
    protected JButton              clearButton    = null;
    protected JPanel               buttonPanel    = null;

    public NumberSpectrumRecorder () {
        super();
        initComponents();
        dvy = viewer.getY1Axis().getDataView( 0 );
        dvy.setMarkerColor( defaultColor[index] );
        dvy.setColor( defaultColor[index] );
        snapshots = new Vector();
        initLayout();
    }

    public void spectrumChange (NumberSpectrumEvent event) {
        synchronized(dvy) {
            viewer.spectrumChange(event);
        }
    }

    public void makeSnaphot() {
        synchronized(dvy) {
            index++;
            JLDataView snapshot = new JLDataView();
            snapshot.setMarkerColor( defaultColor[index%defaultColor.length] );
            snapshot.setColor( defaultColor[index%defaultColor.length] );
            snapshot.setMarkerSize( dvy.getMarkerSize() );
            snapshot.setMarker( dvy.getMarker() );
            snapshot.setLineWidth( dvy.getLineWidth() );
            snapshot.setStyle( dvy.getStyle() );
            snapshot.setViewType( dvy.getViewType() );
            snapshot.setUnit( dvy.getUnit() );
            snapshot.setUserFormat( dvy.getUserFormat() );
            snapshot.setBarWidth( dvy.getBarWidth() );
            snapshot.setA0( dvy.getA0() );
            snapshot.setA1( dvy.getA1() );
            snapshot.setA2( dvy.getA2() );

            DataList data = dvy.getData();
            while (data != null) {
                snapshot.add( data.x, data.y );
                data = data.next;
            }

            snapshots.add( snapshot );
            snapshot.setName( dvy.getName() + " - Record " + snapshots.size() );
            viewer.getY1Axis().addDataView( snapshot );
        }
        viewer.repaint();
    }

    public synchronized void clearSnapshots() {
        for (int i = 0; i < snapshots.size(); i++) {
            viewer.getY1Axis().removeDataView( (JLDataView )snapshots.get(i) );
        }
        snapshots.clear();
        index = 0;
        viewer.repaint();
    }

    public void stateChange (AttributeStateEvent event) {
        synchronized(dvy) {
            viewer.stateChange(event);
        }
    }

    public void errorChange (ErrorEvent event) {
        synchronized(dvy) {
            viewer.errorChange(event);
        }
    }

    /**
     * Returns the INumberSpectrum this componen is listening to
     * 
     * @return The INumberSpectrum this componen is listening to
     */
    public INumberSpectrum getModel() {
        return model;
    }

    /**
     * Sets the INumberSpectrum this component has to listen to
     * 
     * @param v
     *            The INumberSpectrum this component has to listen to
     */
    public void setModel (INumberSpectrum v) {
        if ( model != null ) {
            model.removeSpectrumListener( this );
        }
        viewer.setModel( v );
        if (v != null) {
            v.removeSpectrumListener( viewer );
            v.addSpectrumListener( this );
        }
    }

    /**
     * Does anything necessary for this component not to listen to any
     * INumberSpectrum any more
     */
    public void clearModel() {
        setModel(null);
    }

    /**
     * Returns the NumberSpectrumViewer used by this Component.<br>
     * Even if you have access to it, DO NOT USE the setModel(INumberSpectrum)
     * of the NumberSpectrumViewer returned by this method.<br>
     * Use the setModel(INumberSpectrum) of this component ONLY
     * 
     * @return The NumberSpectrumViewer used by this Component.
     */
    public NumberSpectrumViewer getViewer () {
        return viewer;
    }

    public void actionPerformed (ActionEvent e) {
        if (e.getSource() == snapshotButton) {
            makeSnaphot();
        }
        else if (e.getSource() == clearButton) {
            clearSnapshots();
        }
    }

    protected void initComponents() {
        viewer = new NumberSpectrumViewer();
        snapshotButton = new JButton("Record Values");
        snapshotButton.setMargin( new Insets(0,0,0,0) );
        snapshotButton.addActionListener( this );
        clearButton = new JButton("Clear Records");
        clearButton.setMargin( new Insets(0,0,0,0) );
        clearButton.addActionListener( this );
        viewer.setOpaque( false );
        buttonPanel = new JPanel();
        buttonPanel.setOpaque( false );
        setBackground( viewer.getChartBackground() );
    }

    protected void initLayout() {

        GridBagConstraints snapBConstraint = new GridBagConstraints();
        snapBConstraint.fill = GridBagConstraints.NONE;
        snapBConstraint.gridx = 0;
        snapBConstraint.gridy = 0;
        snapBConstraint.weightx = 0;
        snapBConstraint.weighty = 0;

        GridBagConstraints emptyConstraint = new GridBagConstraints();
        emptyConstraint.fill = GridBagConstraints.HORIZONTAL;
        emptyConstraint.gridx = 1;
        emptyConstraint.gridy = 0;
        emptyConstraint.weightx = 1;
        emptyConstraint.weighty = 0;

        GridBagConstraints clearBConstraint = new GridBagConstraints();
        clearBConstraint.fill = GridBagConstraints.NONE;
        clearBConstraint.gridx = 2;
        clearBConstraint.gridy = 0;
        clearBConstraint.weightx = 0;
        clearBConstraint.weighty = 0;

        buttonPanel.setLayout( new GridBagLayout() );
        buttonPanel.add( snapshotButton, snapBConstraint );
        buttonPanel.add( Box.createGlue(), emptyConstraint );
        buttonPanel.add( clearButton, clearBConstraint );

        GridBagConstraints viewerConstraint = new GridBagConstraints();
        viewerConstraint.fill = GridBagConstraints.BOTH;
        viewerConstraint.gridx = 0;
        viewerConstraint.gridy = 0;
        viewerConstraint.weightx = 1;
        viewerConstraint.weighty = 1;
        
        GridBagConstraints panelConstraint = new GridBagConstraints();
        panelConstraint.fill = GridBagConstraints.HORIZONTAL;
        panelConstraint.gridx = 0;
        panelConstraint.gridy = 1;
        panelConstraint.weightx = 1;
        panelConstraint.weighty = 0;

        this.setLayout( new GridBagLayout() );
        this.add( viewer, viewerConstraint );
        this.add( buttonPanel, panelConstraint );
        this.repaint();
    }

    public static void main (String[] args) {
        try {
            AttributePolledList attributeList = new AttributePolledList();
            NumberSpectrumRecorder recorder = new NumberSpectrumRecorder();
            //String spectrumName = "jlp/test/1/att_spectrum";
            //String spectrumName = "fp/test/1/short_spectrum_ro";
            String spectrumName = "tango/tangotest/1/short_spectrum_ro";
	    	    
            if (args.length > 0) {
                spectrumName = args[0];
            }
            INumberSpectrum spectrum = (INumberSpectrum) attributeList
                    .add( spectrumName );
            recorder.setModel( spectrum );
            JFrame f = new JFrame("NumberSpectrumRecorder");
            f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            attributeList.startRefresher();
            f.setContentPane( recorder );
            f.setSize( 640, 480 );
            f.setVisible( true );
            f.repaint();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
