// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) space 
// Source File Name:   JLooxAdapter.java

package fr.esrf.tangoatk.widget.loox;

import com.loox.jloox.*;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.attribute.AttributeFactory;
import fr.esrf.tangoatk.core.command.CommandFactory;
import fr.esrf.tangoatk.widget.device.StateViewer;
import java.io.PrintStream;
import java.util.*;
import javax.swing.JFrame;


public class JLooxAdapter
    implements IStateListener, INumberScalarListener, IStringScalarListener {

    public AttributeList getAttributeList() {
	return attributeList;
    }

    public void setAttributeList(AttributeList attributelist) {
	attributeList = attributelist;
    }

    public LxGraph getGraph() {
	return graph;
    }

    public void setGraph(LxGraph lxgraph) {
	graph = lxgraph;
	lxComponents = new HashMap();
	parseComponents(graph.getComponents());
    }

    protected void parseComponents(LxComponent alxcomponent[]) {
	for (int i = 0; i < alxcomponent.length; i++) {
	    LxComponent component = alxcomponent[i];
	    String s = component.getName();

	    if (isAttribute(s))
		addAttribute(component, s);

	    if (isDevice(s))
		addDevice(component, s);

	    if (isGroup(component))
		parseComponents(((LxAbstractGroup)component).getComponents());
	}

    }

    boolean isGroup(LxComponent lxcomponent) {
	return lxcomponent instanceof LxAbstractGroup;
    }

    boolean isAttribute(String s) {
	return attributeFactory.isAttribute(s);
    }

    boolean isDevice(String s) {
	return deviceFactory.isDevice(s);
    }

    void addAttribute(LxComponent lxcomponent, String s) {
	try {
	    IAttribute iattribute = (IAttribute)getAttributeList().add(s);
	    stashComponent(s, lxcomponent);
	    if (iattribute instanceof INumberScalar) {
		addNumberScalar(lxcomponent, (INumberScalar)iattribute);
		return;
	    }
	    if (iattribute instanceof IStringScalar) {
		addStringScalar(lxcomponent, (IStringScalar)iattribute);
		return;
	    }
	} catch (ConnectionException connectionexception) {
	    System.out.println("Couldn't load attribute " + s + " " +
			       connectionexception);
	}
    }

    void addNumberScalar(LxComponent lxcomponent, INumberScalar inumberscalar) {
	System.out.println("adding " + inumberscalar + " to " + lxcomponent);
	inumberscalar.addNumberScalarListener(this);

	if (lxcomponent instanceof LxAbstractValueDyno)
	    connectDyno((LxAbstractValueDyno)lxcomponent, inumberscalar);
    }

    void connectDyno(LxAbstractValueDyno dyno, INumberScalar inumberscalar) {

	if (!inumberscalar.isWritable() || !dyno.isSensitive()) return;

	inumberscalar.removeNumberScalarListener(this);
	dyno.setMaximum(inumberscalar.getMaxValue());
	dyno.setMinimum(inumberscalar.getMinValue());

	System.out.println("dyno.getMaximum() " + dyno.getMaximum());
	System.out.println("dyno.getMinimum() " + dyno.getMinimum());
	System.out.println("Setting value to " +
			   inumberscalar.getNumberScalarValue() + " for " +
			   inumberscalar);
    
	dyno.setValue(inumberscalar.getNumberScalarValue());
	dyno.addVariableListener(new LxVariableListener() {
	    
		public void valueChanged(LxVariableEvent event) {
		    LxAbstractValueDyno d =
			(LxAbstractValueDyno)event.getSource();
		    setValue((INumberScalar)getAttributeList().get
			     (d.getName()), d.getValue());
		}
	    });
    }


    void setValue(INumberScalar inumberscalar, double d) {
	System.out.println("Setting " + inumberscalar + " value to " + d);
	double d1 = inumberscalar.getStandardUnit();

	if (Double.isNaN(d1)) d1 = 1.0D;
	inumberscalar.setValue(d * d1);
    }

    void addStringScalar(LxComponent lxcomponent, IStringScalar istringscalar) {
	istringscalar.addStringScalarListener(this);
    }

    void addDevice(LxComponent lxcomponent, String s) {
	try {
	    addListener(deviceFactory.getDevice(s));
	    mouseify(lxcomponent, s);
	    stashComponent(s, lxcomponent);
	} catch (ConnectionException connectionexception) {
	    System.out.println("Couldn't load device " + s + " " +
			       connectionexception);
	}
    }
    protected void mouseify(LxComponent component, String name) {
	final String n = name;
        component.addMouseListener(new LxMouseAdapter() {
		public void mouseDoubleClicked(LxMouseEvent e) {
		    new Thread() {
			public void run() {
			    System.out.println("Starting atkpanel for " + n);
// 			    try {
// 				Class clazz = Class.forName("apps.atkpanel.MainPanel");
// 				apps.atkpanel.MainPanel panel =
// 				    (apps.atkpanel.MainPanel)clazz.newInstance();
// 				panel.init(n);
// 			    } catch (Exception e) {
// 				e.printStackTrace();
// 			    } // end of try-catch
			}
		    }.run();
		}
	    });
    }
    
    void addListener(Device device) {
	System.out.println("connecting to " + device);
	device.addStateListener(this);
    }

    void stashComponent(String s, LxComponent lxcomponent) {
	List list = (List)lxComponents.get(s);
	if (list == null)
	    list = new Vector();
	list.add(lxcomponent);
	lxComponents.put(s, list);
    }

    Iterator getComponent4Name(String s) {
	return ((List)lxComponents.get(s)).iterator();
    }

    public void stringScalarChange(StringScalarEvent event) {
	Iterator iterator = getComponent4Event(event);
	while (iterator.hasNext()) {
	    LxComponent component = (LxComponent)iterator.next();
	    if (component instanceof LxAbstractText)
		((LxAbstractText)component).setText(event.getValue());
	}
    }

    Iterator getComponent4Event(ATKEvent atkevent) {
	IEntity ientity = (IEntity)atkevent.getSource();
	return getComponent4Name(ientity.getName());
    }

    public void stateChange(StateEvent event) {
	Device device = (Device)event.getSource();
	String s = device.getName();
	LxComponent component = (LxComponent)lxComponents.get(s);
	if (component instanceof LxElement)
	    ((LxElement)component).setPaint(StateViewer.getColor4State(event.getState()));
    }

    public void stateChange(AttributeStateEvent attributestateevent) {
    }

    public void errorChange(ErrorEvent errorevent) {
    }

    public void numberScalarChange(NumberScalarEvent event) {
	Iterator iterator = getComponent4Event(event);
	double d = ((INumberScalar)event.getSource()).getStandardUnit();
	if (Double.isNaN(d))  d = 1.0D;
	double d1 = event.getValue() * d;
	while (iterator.hasNext()) {
	    LxComponent lxcomponent = (LxComponent)iterator.next();
	    if (lxcomponent instanceof LxAbstractValueDyno) {
		System.out.println(event.getSource() + " setting value to " + d1);
		((LxAbstractValueDyno)lxcomponent).setValue(Math.abs(d1));
	    }
	    if (lxcomponent instanceof LxDigit)
		((LxDigit)lxcomponent).setValue(d1 * 100);
	}
    }

    public static void main(String args[]) {
	LxGraph lxgraph = new LxGraph();
	LxView lxview = new LxView();
	JLooxAdapter jlooxadapter = new JLooxAdapter();
	AttributeList attributelist = new AttributeList();
	jlooxadapter.setAttributeList(attributelist);
	lxview.setGraph(lxgraph);
	lxgraph.read("slider.jlx");
	jlooxadapter.setGraph(lxgraph);
	attributelist.startRefresher();
	lxview.fitToGraph(20, 20);
	JFrame jframe = new JFrame();
	jframe.setContentPane(lxview);
	jframe.pack();
	jframe.show();
    }

    public JLooxAdapter() {
	attributeFactory = AttributeFactory.getInstance();
	deviceFactory = DeviceFactory.getInstance();
    }

    LxGraph graph;
    Map lxComponents;
    AttributeFactory attributeFactory;
    DeviceFactory deviceFactory;
    AttributeList attributeList;



}
