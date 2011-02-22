package fr.esrf.tangoatk.core.util;

import fr.esrf.tangoatk.core.AttributeList;
import fr.esrf.tangoatk.core.AttributePolledList;
import fr.esrf.tangoatk.core.ConnectionException;
import fr.esrf.tangoatk.core.INumberSpectrum;
import fr.esrf.tangoatk.core.IRefresherListener;
import fr.esrf.tangoatk.core.util.NonAttrNumberSpectrum;

public class TimeAndValueDualSpectrum extends NonAttrNumberSpectrum implements IRefresherListener
{

    protected AttributeList list;
    protected INumberSpectrum timeModel;
    protected INumberSpectrum yModel;

    public TimeAndValueDualSpectrum ()
    {
        super();
        list = new AttributePolledList();
        list.addRefresherListener(this);
    }

    public void refreshStep ()
    {
        double[] xvalue = new double[0];
        double[] yvalue = new double[0];
        if (timeModel != null && yModel != null)
        {
            try
            {
                xvalue = timeModel.getSpectrumValue();
            }
            catch(Exception e)
            {
                xvalue = new double[0];
            }
            try
            {
                yvalue = yModel.getSpectrumValue();
            }
            catch(Exception e)
            {
                yvalue = new double[0];
            }
            this.setXYValue(xvalue, yvalue);
        }
    }

    public AttributeList getList ()
    {
        return list;
    }

    public void setList (AttributeList list)
    {
        if (this.list != null && timeModel != null)
        {
            this.list.remove(timeModel.getName());
        }
        if (this.list != null && yModel != null)
        {
            this.list.remove(yModel.getName());
        }
        this.list = list;
        if (list != null && timeModel != null)
        {
            list.add(timeModel);
        }
        if (list != null && timeModel != null)
        {
            list.add(yModel);
        }
        list.addRefresherListener(this);
    }

    public INumberSpectrum getTimeModel ()
    {
        return timeModel;
    }

    public void setTimeModel (INumberSpectrum timeModel)
    {
        if (list != null && this.timeModel != null)
        {
            list.remove(this.timeModel.getName());
        }
        this.timeModel = timeModel;
        if (list != null && this.timeModel != null)
        {
            list.add(this.timeModel);
        }
    }

    public INumberSpectrum getYModel ()
    {
        return yModel;
    }

    public void setYModel (INumberSpectrum model)
    {
        if (list != null && yModel != null)
        {
            list.remove(yModel.getName());
        }
        yModel = model;
        if (list != null && yModel != null)
        {
            list.add(yModel);
        }
    }

    /**
     * Use case example
     * @param args
     * @throws ConnectionException 
     */
    public static void main (String[] args) throws ConnectionException
    {
    /* This code cannot compile because it makes reference to classes inside
    fr.esrf.tangoatk.widget. subpackge. During the build of ATK, the
    core is compiled before widget. So the build will fail if in core
    we make reference to widget which is not yet compiled.
    Generally speaking, avoid to have "main" in the core classes. The main
    is normally made inside widget classes to test and not the other way!
    
        AttributePolledList attrList = new AttributePolledList();
        fr.esrf.tangoatk.widget.attribute.NonAttrNumberSpectrumViewer viewer = new fr.esrf.tangoatk.widget.attribute.NonAttrNumberSpectrumViewer();
        viewer.getXAxis().setLabelFormat(fr.esrf.tangoatk.widget.util.chart.JLAxis.TIME_FORMAT);
        TimeAndValueDualSpectrum tester = new TimeAndValueDualSpectrum();
        if (args.length < 2)
        {
            System.out.println("Need the names of 2 NumberSpectrum as argument");
            return;
        }
        INumberSpectrum x = (INumberSpectrum)attrList.add(args[0]);
        INumberSpectrum y = (INumberSpectrum)attrList.add(args[1]);
        tester.setTimeModel(x);
        tester.setYModel(y);
        tester.setList(attrList);
        tester.setXName(args[0]);
        tester.setYName(args[1]);
        attrList.startRefresher();
        viewer.addModel(tester);
        javax.swing.JFrame f = new javax.swing.JFrame();
        f.setSize(800,600);
        f.getContentPane().add(viewer);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
	*/
    }
}
