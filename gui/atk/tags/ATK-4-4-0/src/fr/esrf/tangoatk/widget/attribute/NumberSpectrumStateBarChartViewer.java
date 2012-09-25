/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.esrf.tangoatk.widget.attribute;

import com.braju.format.Format;
import fr.esrf.tangoatk.core.AttributeList;
import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.DevStateSpectrumEvent;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.IDevStateSpectrum;
import fr.esrf.tangoatk.core.IDevStateSpectrumListener;
import fr.esrf.tangoatk.core.IDevice;
import fr.esrf.tangoatk.core.INumberSpectrum;
import fr.esrf.tangoatk.core.ISpectrumListener;
import fr.esrf.tangoatk.core.IStringSpectrum;
import fr.esrf.tangoatk.core.IStringSpectrumListener;
import fr.esrf.tangoatk.core.NumberScalarEvent;
import fr.esrf.tangoatk.core.NumberSpectrumEvent;
import fr.esrf.tangoatk.core.Property;
import fr.esrf.tangoatk.core.StringSpectrumEvent;
import fr.esrf.tangoatk.widget.util.ATKConstant;
import fr.esrf.tangoatk.widget.util.chart.ColorItem;
import fr.esrf.tangoatk.widget.util.chart.IJLChartListener;
import fr.esrf.tangoatk.widget.util.chart.JLAxis;
import fr.esrf.tangoatk.widget.util.chart.JLChart;
import fr.esrf.tangoatk.widget.util.chart.JLChartEvent;
import fr.esrf.tangoatk.widget.util.chart.JLDataView;
import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import javax.swing.JFrame;

/**
 *
 * @author poncet
 */
public class NumberSpectrumStateBarChartViewer extends JLChart
        implements IJLChartListener,
                   ISpectrumListener, IDevStateSpectrumListener,
                   IStringSpectrumListener,
                   PropertyChangeListener
{

    private INumberSpectrum numberModel = null;
    private IDevStateSpectrum stateModel = null;
    private IStringSpectrum   nameModel = null;
    
    protected JLDataView dvy = null;
    protected JLDataView minAlarmDv = null, maxAlarmDv = null;
    private SimplePropertyFrame pf = null;
    private double A0 = 0.0;
    private double A1 = 1.0;
    private String format = null;
    private String[] currentStates = null;
    protected String[] names = null;

    /* The bean properties */
    private Color            defaultBarChartColor = ATKConstant.getColor4State(IDevice.UNKNOWN);
    private int              barChartFillMethod = JLDataView.METHOD_FILL_FROM_BOTTOM;
    private long             lastForcedUpdateTime = System.currentTimeMillis() - 60000;
    private boolean          drawOnNaN = false;
    private boolean          minAlarmVisible = false;
    private boolean          maxAlarmVisible = false;

    private Color            minAlarmColor = ATKConstant.getColor4State(IDevice.ALARM);
    private Color            maxAlarmColor = ATKConstant.getColor4State(IDevice.ALARM);
    private int              minAlarmStyle = JLDataView.STYLE_SOLID;
    private int              maxAlarmStyle = JLDataView.STYLE_SOLID;

    private Double           minAlarmValue = null;
    private Double           maxAlarmValue = null;

    public NumberSpectrumStateBarChartViewer()
    {
        // Create the graph
        super();

        setBorder(new javax.swing.border.EtchedBorder());
        getY1Axis().setAutoScale(true);
        getXAxis().setAutoScale(true);
        getXAxis().setAnnotation(JLAxis.VALUE_ANNO);

        dvy = new JLDataView();
        dvy.setViewType(JLDataView.TYPE_BAR);
        dvy.setBarWidth(5);
        dvy.setFillStyle(JLDataView.FILL_STYLE_SOLID);
        dvy.setLineWidth(0);
        dvy.setColor(defaultBarChartColor);
        dvy.setFillColor(defaultBarChartColor);
        dvy.setLabelVisible(false);
        getY1Axis().addDataView(dvy);

        minAlarmDv = new JLDataView();
        minAlarmDv.setName("Min Alarm");
        minAlarmDv.setStyle(minAlarmStyle);
        minAlarmDv.setLineWidth(2);
        minAlarmDv.setColor(minAlarmColor);
        minAlarmDv.setLabelVisible(false);
        //getY1Axis().addDataView(minAlarmDv);

        maxAlarmDv = new JLDataView();
        maxAlarmDv.setName("Max Alarm");
        maxAlarmDv.setStyle(maxAlarmStyle);
        maxAlarmDv.setLineWidth(2);
        maxAlarmDv.setColor(maxAlarmColor);
        maxAlarmDv.setLabelVisible(false);
        //getY1Axis().addDataView(maxAlarmDv);

        setJLChartListener(this);
    }

    /*
     * Bean property getters and setters
     */

    public Color getDefaultBarChartColor()
    {
        return(defaultBarChartColor);
    }

    public void setDefaultBarChartColor(Color  col)
    {
        if (defaultBarChartColor != col)
	{
	    defaultBarChartColor = col;
            dvy.setColor(defaultBarChartColor);
            dvy.setFillColor(defaultBarChartColor);
            repaint();
	}
    }

    public int getBarChartFillMethod()
    {
        return (barChartFillMethod);
    }

    public void setBarChartFillMethod(int fillMethod)
    {
        if (barChartFillMethod != fillMethod)
        {
            dvy.setFillMethod(fillMethod);
            barChartFillMethod = dvy.getFillMethod();
        }
    }

    public boolean getDrawOnNaN()
    {
        drawOnNaN = dvy.isDrawOnNaN();
        return (drawOnNaN);
    }

    public void setDrawOnNaN(boolean don)
    {
        dvy.setDrawOnNaN(don);
        drawOnNaN = don;
    }

    public boolean getMinAlarmVisible()
    {
        return (minAlarmVisible);
    }

    public void setMinAlarmVisible(boolean vis)
    {
        if (minAlarmVisible != vis)
        {
            changeMinAlarmVisibility(vis);
        }
        minAlarmVisible = vis;
    }

    public boolean getMaxAlarmVisible()
    {
        return (maxAlarmVisible);
    }

    public void setMaxAlarmVisible(boolean vis)
    {
        if (maxAlarmVisible != vis)
        {
            changeMaxAlarmVisibility(vis);
        }
       maxAlarmVisible = vis;
    }

    public Color getMinAlarmColor()
    {
        return (minAlarmColor);
    }

    public void setMinAlarmColor(Color macol)
    {
        minAlarmDv.setColor(macol);
        minAlarmColor = macol;
    }

    public Color getMaxAlarmColor()
    {
        return (maxAlarmColor);
    }

    public void setMaxAlarmColor(Color macol)
    {
        maxAlarmDv.setColor(macol);
        maxAlarmColor = macol;
    }

    public int getMinAlarmStyle()
    {
        return (minAlarmStyle);
    }

    public void setMinAlarmStyle(int maStyle)
    {
        minAlarmDv.setStyle(maStyle);
        minAlarmStyle = maStyle;
    }

    public int getMaxAlarmStyle()
    {
        return (maxAlarmStyle);
    }

    public void setMaxAlarmStyle(int maStyle)
    {
        maxAlarmDv.setStyle(maStyle);
        maxAlarmStyle = maStyle;
    }

    /**<code>getModel</code> Gets the numberspectrum model.
     * @returns the numberspectrum model.
     */
    public INumberSpectrum getModel()
    {
        return numberModel;
    }

    /**<code>setModel</code> Sets the numberspectrum model.
     * @param ins  the numberspectrum attribute to assign to model.
     */
    public void setModel(INumberSpectrum ins)
    {
        clearNumberModel();
        if (ins == null)
        {
            repaint();
            return;
        }

        numberModel = ins;

        format = numberModel.getFormat();
        dvy.setUserFormat(format);
        dvy.setUnit(ins.getUnit());
        dvy.setName(ins.getName());
        
        numberModel.addSpectrumListener(this);
        numberModel.refresh();

        double minAlarm = numberModel.getMinAlarm();
        if (Double.isNaN(minAlarm))
        {
            minAlarmValue = null;
            changeMinAlarmVisibility(false);
        }
        else
        {
            minAlarmValue = new Double(minAlarm);
            if (minAlarmVisible) changeMinAlarmVisibility(true);
        }
            
        double maxAlarm = numberModel.getMaxAlarm();
        if (Double.isNaN(maxAlarm))
        {
            maxAlarmValue = null;
            changeMaxAlarmVisibility(false);
        }
        else
        {
            maxAlarmValue = new Double(maxAlarm);
            if (maxAlarmVisible) changeMaxAlarmVisibility(true);
        }

        if (pf != null)
        {
            pf.setModel(numberModel);
        }

        numberModel.getProperty("min_alarm").addPresentationListener(this);
        numberModel.getProperty("max_alarm").addPresentationListener(this);

        repaint();
    }

    /**<code>clearNumberModel</code> removes the numberspectrum model.
     */
    public void clearNumberModel()
    {
        if (numberModel != null)
        {
            numberModel.removeSpectrumListener(this);
            if (pf != null)
            {
                pf.setModel(null);
            }
            setMinAlarmVisible(false);
            setMaxAlarmVisible(false);
            numberModel.getProperty("min_alarm").removePresentationListener(this);
            numberModel.getProperty("max_alarm").removePresentationListener(this);
            numberModel = null;
            minAlarmValue = null;
            maxAlarmValue = null;
        }
        setToolTipText(null);
    }


    /**<code>getStatesModel</code> Gets the devState spectrum model.
     * @returns the devState spectrum model.
     */
    public IDevStateSpectrum getStatesModel()
    {
        return stateModel;
    }

    /**<code>setModel</code> Set the devstate spectrum model.
     * @param idss  the devstate spectrum attribute to assign to model.
     */
    public void setModel(IDevStateSpectrum idss)
    {
        clearStateModel();
        if (idss == null)
        {
            repaint();
            return;
        }

        stateModel = idss;
        stateModel.addDevStateSpectrumListener(this);
        repaint();
    }


    /**<code>clearNumberModel</code> removes the numberspectrum model.
     */
    public void clearStateModel()
    {
        if (stateModel != null)
        {
            stateModel.removeDevStateSpectrumListener(this);
            stateModel = null;
            for (int i=0; i<currentStates.length; i++)
            {
                dvy.setBarFillColorAt(i, ATKConstant.getColor4State(IDevice.UNKNOWN));
            }
        }
        currentStates = null;
    }


    /**<code>getNamesModel</code> Gets the string spectrum model.
     * @returns the string spectrum model.
     */
    public IStringSpectrum getNamesModel()
    {
        return nameModel;
    }

    /**<code>setModel</code> Set the string spectrum model.
     * @param iss  the string spectrum attribute to assign to model.
     */
    public void setModel(IStringSpectrum iss)
    {
        clearNameModel();
        if (iss == null) return;

        nameModel = iss;
        nameModel.addListener(this);
    }

    /**<code>clearNameModel</code> removes the string spectrum model.
     */
    public void clearNameModel()
    {
        if (nameModel != null)
        {
            nameModel.removeListener(this);
            nameModel = null;
        }
    }

    void changeMinAlarmVisibility(boolean   vis)
    {
        if (vis == false)
        {
            minAlarmDv.reset();
            getY1Axis().removeDataView(minAlarmDv);
            repaint();
        }
        else
        {
            if (numberModel == null) return;
            if (minAlarmValue == null) return;
            getY1Axis().addDataView(minAlarmDv);
            refreshAlarmDv(minAlarmDv, minAlarmValue.doubleValue());
        }
    }

    void changeMaxAlarmVisibility(boolean   vis)
    {
        if (vis == false)
        {
            maxAlarmDv.reset();
            getY1Axis().removeDataView(maxAlarmDv);
            repaint();
        }
        else
        {
            if (numberModel == null) return;
            if (maxAlarmValue == null) return;
            getY1Axis().addDataView(maxAlarmDv);
            refreshAlarmDv(maxAlarmDv, maxAlarmValue.doubleValue());
        }
    }


    public String[] clickOnChart(JLChartEvent evt)
    {
        String           yValue = null;
        String           xValue = null;
        String[]         ret;
        int              indX = -1;

        if (evt.getDataView() != dvy)
            return null;

        if (numberModel == null)
        {
            ret = new String[2];
            indX = 0;
        }
        else
        {
            ret = new String[3];
            ret[0] = numberModel.getName();
            indX = 1;
            if (format != null)
            {
                Object[] o = {new Double(evt.getTransformedYValue())};
                yValue = Format.sprintf(format, o) + " " + numberModel.getUnit();
            }
            else
            {
                yValue = evt.getTransformedYValue() + " " + numberModel.getUnit();
            }
        }
        
        if (names != null)
        {
            int nameIndex = evt.getDataViewIndex();
            if ( (nameIndex >= 0) && (nameIndex < names.length) )
            {
                xValue = names[nameIndex];
            }
        }
        
        if (xValue != null)
            ret[indX] = "X=" + xValue;
        else
            ret[indX] = "X=" + evt.getTransformedXValue();
        
        if (yValue != null)
            ret[indX+1] = "Y=" + yValue;
        else
            ret[indX+1] = "Y=" + evt.getTransformedYValue();
        return ret;
    }

    /**
     * Sets an affine tranform to the X axis. This allows to transform
     * spectra index displayed on X axis.
     * @param a0
     * @param a1
     */
    public void setXAxisAffineTransform(double a0, double a1)
    {
        A0 = a0;
        A1 = a1;
    }



    public void spectrumChange(NumberSpectrumEvent nse)
    {
        double[] value = nse.getValue();
        int length = value.length;

        synchronized (dvy)
        {
            Vector<ColorItem> barColors = (Vector<ColorItem>) dvy.getBarFillColors().clone();

            dvy.reset();
            for (int i = 0; i < length; i++)
            {
                dvy.add(A0 + A1 * (double) i, value[i], false);
            }
            dvy.updateFilters();
            dvy.setBarFillColors(barColors);

            // Commit change
            repaint();
            refreshTableSingle(dvy);
        }
    }

    public void devStateSpectrumChange(DevStateSpectrumEvent dsse)
    {
        String[] states = dsse.getValue();
        if (states == null) return;

        if (currentStates == null)
        {
            for (int i=0; i<states.length; i++)
            {
                dvy.setBarFillColorAt(i, ATKConstant.getColor4State(states[i]));
            }
            currentStates = dsse.getValue().clone();
            repaint();
            return;
        }

        long  now = System.currentTimeMillis();

        if ((now - lastForcedUpdateTime) > 10000)
        {
            for (int i=0; i<states.length; i++)
            {
                dvy.setBarFillColorAt(i, ATKConstant.getColor4State(states[i]));
                currentStates[i] = states[i];
            }
            repaint();
            lastForcedUpdateTime = now;
            return;
        }

        for (int i=0; i<states.length; i++)
        {
            if (!currentStates[i].equalsIgnoreCase(states[i]))
            {
                dvy.setBarFillColorAt(i, ATKConstant.getColor4State(states[i]));
                currentStates[i] = states[i];
            }
        }
        repaint();
    }

    public void stringSpectrumChange(StringSpectrumEvent evt)
    {
        if (evt.getValue() != null)
        {
            if (evt.getValue().length <= 0) return;
            names = evt.getValue();
        }
    }


    public void refreshAlarmDv (JLDataView alarmDv, double alarmValue)
    {
        double  firstX, lastX;

        if (dvy.getDataLength() < 3) return;

        firstX = dvy.getXValueByIndex(0);
        lastX = dvy.getXValueByIndex(dvy.getDataLength()-1);

        alarmDv.reset();
        alarmDv.add(firstX, alarmValue);
        alarmDv.add(lastX, alarmValue);
        repaint();
    }

    public void stateChange(AttributeStateEvent e)
    {
    }

    public void errorChange(ErrorEvent errEvt)
    {
        if (errEvt.getSource() == numberModel) //source is the number spectrum
        {
            // Clear the graph
            dvy.reset();
            repaint();
            refreshTableSingle(dvy);
            return;
        }

        if (errEvt.getSource() == stateModel) //source is the devstate spectrum
        {
            // Clear the barchart individual state colors
            Vector<ColorItem> barColors = (Vector<ColorItem>) dvy.getBarFillColors();
            barColors.clear();
            currentStates = null;
            repaint();
            return;
        }
    }


    public void propertyChange(PropertyChangeEvent evt)
    {
        Property src = (Property) evt.getSource();

        if (numberModel != null)
        {
            if (src.getName().equalsIgnoreCase("min_alarm"))
            {
                double minAlarm = numberModel.getMinAlarm();
                if (Double.isNaN(minAlarm))
                {
                    minAlarmValue = null;
                    changeMinAlarmVisibility(false);
                }
                else
                {
                    minAlarmValue = new Double(minAlarm);
                    if (minAlarmVisible) changeMinAlarmVisibility(true);
                }
                return;
            }

            if (src.getName().equalsIgnoreCase("max_alarm"))
            {
                double maxAlarm = numberModel.getMaxAlarm();
                if (Double.isNaN(maxAlarm))
                {
                    maxAlarmValue = null;
                    changeMaxAlarmVisibility(false);
                }
                else
                {
                    maxAlarmValue = new Double(maxAlarm);
                    if (maxAlarmVisible) changeMaxAlarmVisibility(true);
                }
            }
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // TODO code application logic here
        try
        {
            AttributeList attl = new AttributeList();

            NumberSpectrumStateBarChartViewer nssbcv = new NumberSpectrumStateBarChartViewer();
            nssbcv.setBarChartFillMethod(JLDataView.METHOD_FILL_FROM_ZERO);
            nssbcv.getXAxis().setGridVisible(true);
            nssbcv.getY1Axis().setGridVisible(true);
            nssbcv.getY1Axis().setName("Neutron Dose Rate");

            nssbcv.setMaxAlarmColor(Color.red);
            nssbcv.setMaxAlarmVisible(true);

            INumberSpectrum      ins = (INumberSpectrum) attl.add("sr/neutron/all/Dose");
            nssbcv.setModel(ins);
            IDevStateSpectrum      idss = (IDevStateSpectrum) attl.add("sr/neutron/all/SubDevicesStates");
            nssbcv.setModel(idss);
            IStringSpectrum      iss = (IStringSpectrum) attl.add("sr/neutron/all/SubDevicesNames");
            nssbcv.setModel(iss);


            nssbcv.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
            nssbcv.setFont(new java.awt.Font("Dialog", 0, 12));
            nssbcv.setPreferredSize(new Dimension(850, 480));
            //attl.setRefreshInterval(5000);
            attl.startRefresher();

            JFrame f = new JFrame();
            f.setContentPane(nssbcv);
            f.pack();
            f.setVisible(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
