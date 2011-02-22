package fr.esrf.tangoatk.widget.attribute;


/**
 * @author OUNSY
 *
 */

/* Modified by F. Poncet on August 3rd 2005 */
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import fr.esrf.tangoatk.core.AttributeList;
import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.ConnectionException;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.INumberScalar;
import fr.esrf.tangoatk.core.INumberScalarListener;
import fr.esrf.tangoatk.core.NumberScalarEvent;
import fr.esrf.tangoatk.widget.util.ATKConstant;
import fr.esrf.tangoatk.widget.util.chart.*;


public class AttributeBarChart extends JLChart implements INumberScalarListener {

    private String        header = "Barchart";
    private String        xaxisName = "X";
    private String        unit = "unknown";
    private double        alarmLevel = 0.0;
    private double        faultLevel = 0.0;
    JLDataView            alarm;
    JLDataView            fault;
    private String        configFile = "";
    private String        configFile2;
    private String        lastConfig = "";
    private int           refreshingPeriod = 0;
    
    
    
    private  Map             dataViewHash = null;
    private  AttributeList   model=null;


	public AttributeBarChart()
	{
	    super();
	    dataViewHash = new HashMap();
	    initComponents();
	    addUserAction("Load configuration");
	    addUserAction("Save configuration");
	    addUserAction("Set Refresh Interval...");
        }
	
	private void initComponents(){

	    // Initialise chart properties
	    setHeaderFont(new Font("Times", Font.BOLD, 18));
	    setHeader("BarChart");
	    setLabelVisible(false);
	    setSize(640,480);
	    setXAxisOnBottom(false);

	    // Initialise axis properties
	    getY1Axis().setAutoScale(true);
	    getY1Axis().setGridVisible(true);
	    getY1Axis().setSubGridVisible(false);
	    getXAxis().setAutoScale(false);
	    getXAxis().setMinimum(0.0);
	    getXAxis().setMaximum(0);
	    getXAxis().setAnnotation(JLAxis.VALUE_ANNO);
	    getXAxis().setGridVisible(false);
	    getXAxis().setSubGridVisible(false);

	    // Alarm and Fault level line
	    alarm=new JLDataView();
	    alarm.setName("Alarm level");
	    alarm.setColor(ATKConstant.getColor4State("ALARM"));
	    alarm.setLineWidth(2);
	    getY1Axis().addDataView(alarm);
	    fault=new JLDataView();
	    fault.setName("Fault level");
	    fault.setColor(ATKConstant.getColor4State("FAULT"));
	    fault.setLineWidth(2);
	    getY1Axis().addDataView(fault);
	    setPaintAxisFirst(false);
	}
	
    // To allow a diffenrent filling color for each bar,
    // we use 1 dataview per bar
  	public void setModel(AttributeList  attl)
	{
	    int                          nbAtts, idx;
	    boolean                      containsNumberScalar;
	    Object                       elem;
	    int                          nbNs;
  	    int                          bar_width = 10;
	    
	    if (model != null)
	    {
	       clearModel();
	       model = null;
	    }
	    
	    if (attl == null)
	       return;

	    nbAtts = attl.getSize();

	    if (nbAtts <= 0)
	       return;
	       

	    containsNumberScalar = false;

	    for (idx=0; idx < nbAtts; idx++)
	    {
	       elem = attl.getElementAt(idx);
	       if (elem instanceof INumberScalar)
	       {
		  containsNumberScalar = true;
		  break;
	       }
	    }

	    if (containsNumberScalar == false)
	      return;

	    model = attl;
	    refreshingPeriod = model.getRefreshInterval();
            nbNs = 0;
	    for (idx=0; idx < nbAtts; idx++)
	    {
	       elem = attl.getElementAt(idx);
	       if (elem instanceof INumberScalar)
	       {
		  INumberScalar ins = (INumberScalar) elem;
  		  if ( !dataViewHash.containsKey(ins) ) // add only once each NumberScalar
		  {
      		     JLDataView dvy_new = new JLDataView();
    		     dvy_new.setUnit( ins.getUnit() );
    		     dvy_new.setName( ins.getName() );
    		     dvy_new.setColor(Color.black);
    		     dvy_new.setLineWidth(1);
    		     dvy_new.setBarWidth(bar_width);
    		     dvy_new.setFillStyle(JLDataView.FILL_STYLE_SOLID);
    		     dvy_new.setViewType(JLDataView.TYPE_BAR);
    		     getY1Axis().addDataView( dvy_new );
    		     ins.addNumberScalarListener(this);
		     
	             java.util.List  list = new Vector();
		     Integer     XaxisValue = new Integer(nbNs);
		     list.add(0, dvy_new);
		     list.add(1, XaxisValue);
		     dataViewHash.put(ins, list);
		     nbNs++;
		  }
	       }
	    }
	    
	    getXAxis().setMaximum(nbNs+1);
  	    getXAxis().setTickSpacing(nbNs);
//  	    System.out.println("bar_width :" + bar_width);
  	}
	

	public void clearModel()
	{
	    int                          nbAtts, idx;
	    Object                       elem;
	    
	    if (model == null)
	       return;

	    nbAtts = model.getSize();

	    if (nbAtts <= 0)
	       return;

	    for (idx=0; idx < nbAtts; idx++)
	    {
	       elem = model.getElementAt(idx);
	       if (elem instanceof INumberScalar)
	       {
		  INumberScalar ins = (INumberScalar) elem;
  		  if ( dataViewHash.containsKey(ins) )
		  {
    		     ins.removeNumberScalarListener(this);
		  }
	       }
	    }
	    
	    // Remove all dataviews and numberScalars from HMap and from JLChart
	    getY1Axis().clearDataView();
	    dataViewHash.clear();
	    
	    // Add the alarm and fault dataViews back to the JlChart
	    initComponents();       
	}
	
	
  	
  	public void setWidth(int bar_width)
	{
	    int                          nbAtts, idx;
	    Object                       elem;
	    
	    if (model == null)
	       return;
	    
	    nbAtts = model.getSize();

	    if (nbAtts <= 0)
	       return;

	    for (idx=0; idx < nbAtts; idx++)
	    {
	       elem = model.getElementAt(idx);
	       if (elem instanceof INumberScalar)
	       {
		  INumberScalar ins = (INumberScalar) elem;
  		  if ( dataViewHash.containsKey(ins) ) 
		  {
      		     java.util.List      dvyAndIndex = (java.util.List) dataViewHash.get(ins);
		     if (dvyAndIndex == null)
        	         continue;

		     int nbObjs = dvyAndIndex.size();
		     
		     if (nbObjs < 2)
		        continue;
	             
		     Object   obj = dvyAndIndex.get(0);
		     
		     if (obj == null)
		        continue;
			
		     if ( !(obj instanceof JLDataView) )
		        continue;
			
		     JLDataView  dvy = (JLDataView) obj;
    		     dvy.setBarWidth(bar_width);
 		  }
	       }
	    }
	    
  	}
  	
/*  	public int getWidth(){
  	    return bar_width;
  	}*/



    /* (non-Javadoc)
     * @see fr.esrf.tangoatk.core.INumberScalarListener#numberScalarChange(fr.esrf.tangoatk.core.NumberScalarEvent)
     */
    public void numberScalarChange(NumberScalarEvent numberScalarEvent)
    {
    	INumberScalar source = (INumberScalar)numberScalarEvent.getSource();

  	if ( dataViewHash.containsKey(source) ) 
	{
      	    java.util.List      dvyAndIndex = (java.util.List) dataViewHash.get(source);
	    if (dvyAndIndex == null)
        	return;

	    int nbObjs = dvyAndIndex.size();

	    if (nbObjs < 2)
	       return;

	    Object   obj = dvyAndIndex.get(0);

	    if (obj == null)
	       return;

	    if ( !(obj instanceof JLDataView) )
	       return;
	       
	    JLDataView    dvy = (JLDataView) obj;

	    obj = dvyAndIndex.get(1);

	    if (obj == null)
	       return;

	    if ( !(obj instanceof Integer) )
	       return;
			
      	   int  dvyIndex = ((Integer) obj).intValue();

           double yvalue = numberScalarEvent.getValue();
           dvy.reset();
           dvy.add( (double)(dvyIndex+1) , yvalue  );            
           if( yvalue>faultLevel )     dvy.setFillColor(Color.red);
           else if (yvalue>alarmLevel) dvy.setFillColor(Color.orange);
           else               dvy.setFillColor(Color.green);
           // Commit change
           repaint();
 	}
    }


    /* (non-Javadoc)
     * @see fr.esrf.tangoatk.core.IAttributeStateListener#stateChange(fr.esrf.tangoatk.core.AttributeStateEvent)
     */
    public void stateChange(AttributeStateEvent arg0) {
    	// TODO Auto-generated method stub
    	
    }


    /* (non-Javadoc)
     * @see fr.esrf.tangoatk.core.IErrorListener#errorChange(fr.esrf.tangoatk.core.ErrorEvent)
     */
    public void errorChange(ErrorEvent arg0) {
    	// TODO Auto-generated method stub
    	
    }
    
	/**
	 * @return Returns the alarm_level.
	 */
	public double getAlarmLevel() {
		return alarmLevel;
	}
	/**
	 * @param alarm_level The alarm_level to set.
	 */
	public void setAlarmLevel(double alarm_level) {
		this.alarmLevel = alarm_level;
	    alarm.add(-1.0,alarm_level);
	    alarm.add(alarm_level,alarm_level);
//	    alarm.add(current_model_number,alarm_level);
	}
	/**
	 * @return Returns the fault_level.
	 */
	public double getFaultLevel() {
		return faultLevel;
	}
	/**
	 * @param fault_level The fault_level to set.
	 */
	public void setFaultLevel(double fault_level) {
		this.faultLevel = fault_level;
	    fault.add(-1.0,fault_level);
	    fault.add(fault_level,fault_level);
//	    fault.add(current_model_number,fault_level);
	}
	/**
	 * @return Returns the header.
	 */
	public String getChartHeader() {
		return header;
	}
	/**
	 * @param header The header to set.
	 */
	public void setChartHeader(String header) {
		this.header = header;
	    setHeader(header);
	}
	/**
	 * @return Returns the unit.
	 */
	public String getUnit() {
		return unit;
	}
	/**
	 * @param unit The unit to set.
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	    getY1Axis().setName(unit);
	    alarm.setUnit(unit);
	    fault.setUnit(unit);
	}
	/**
	 * @return Returns the xaxis_name.
	 */
	public String getXaxisName() {
		return xaxisName;
	}
	/**
	 * @param xaxis_name The xaxis_name to set.
	 */
	public void setXaxisName(String xaxis_name) {
		this.xaxisName = xaxis_name;
	    getXAxis().setName(xaxis_name);
	}

	public String getSettings(){
	    
	    int    nbAtts, nbNs;
	    Object   elem;
	    String s = "";
	    s = s + this.getConfiguration();
	    if(model != null){
	        s = s + "refresh_time:" + this.refreshingPeriod + "\n";
	    }
	    s = s + this.getXAxis().getConfiguration("x");
	    s = s + this.getY1Axis().getConfiguration("y1");
	    s = s + this.getY2Axis().getConfiguration("y2");
	    
	    nbAtts = model.getSize();
            nbNs = 0;
	    for (int idx=0; idx < nbAtts; idx++)
	    {
	       elem = model.getElementAt(idx);
	       if (elem instanceof INumberScalar)
	       {
		  INumberScalar ins = (INumberScalar) elem;
  		  if ( dataViewHash.containsKey(ins) ) 
		     nbNs++;
	       }
	    }

	    s = s + "dv_number:" + nbNs + "\n";

	    nbNs = 0;
	    
	    for (int idx=0; idx < nbAtts; idx++)
	    {
	       elem = model.getElementAt(idx);
	       if (elem instanceof INumberScalar)
	       {
		  INumberScalar ins = (INumberScalar) elem;
  		  if ( dataViewHash.containsKey(ins) ) 
		  {
      		     java.util.List      dvyAndIndex = (java.util.List) dataViewHash.get(ins);
		     if (dvyAndIndex == null)
        	         continue;

		     int nbObjs = dvyAndIndex.size();
		     
		     if (nbObjs < 2)
		        continue;
	             
		     Object   obj = dvyAndIndex.get(0);
		     
		     if (obj == null)
		        continue;
			
		     if ( !(obj instanceof JLDataView) )
		        continue;

		     obj = dvyAndIndex.get(1);

		     if (obj == null)
			continue;

		     if ( !(obj instanceof Integer) )
			continue;

      		     int  dvyIndex = ((Integer) obj).intValue();
			
		     JLDataView  dvy = (JLDataView) obj;
 	             s = s + "dv" + dvyIndex + "_name:'" + ins + "'\n";
	             s = s + dvy.getConfiguration("dv" + dvyIndex);
		  }
	       }
	    }
	    return s;
	}

	public void saveSetting(String s){
        try{
            FileWriter filewriter = new FileWriter(s);
            String s1 = getSettings();
            filewriter.write(s1, 0, s1.length());
            filewriter.close();
            lastConfig = s;
        }
        catch(Exception exception){
            JOptionPane.showMessageDialog(this.getParent(), "Failed to write " + s, "Error", 0);
            exception.printStackTrace();
        }
    }

    public String loadSetting(String s){
        CfFileReader cffilereader = new CfFileReader();
        if(!cffilereader.readFile(s)) {
            return "Failed to read " + s;
        }
        else{
            lastConfig = s;
            return applySettings(cffilereader);
        }
    }

    public int getRefreshingPeriod()
    {
        if (model == null)
	   return 0;
	   
	refreshingPeriod = model.getRefreshInterval();
	return refreshingPeriod;
    }

    public void setRefreshingPeriod(int refreshingPeriod)
    {
        if (model == null)
	   return;
        this.refreshingPeriod = refreshingPeriod;
        model.setRefreshInterval(refreshingPeriod);
    }

    private String applySettings(CfFileReader cffilereader){
        String s = "";
        String attributeListTmp ="";
        Vector vector = cffilereader.getParam("dv_number");
        int k;
        if(vector != null) {
            try {
                k = Integer.parseInt(vector.get(0).toString());
            }
            catch(NumberFormatException numberformatexception){
                s = s + "dv_number: invalid number\n";
                return s;
            }
            for(int i = 0; i < k; i++) {
                vector = cffilereader.getParam("dv" + i + "_name");
                if(vector == null) {
                    s = s + "Unable to find dv" + i + "_name param\n";
                    return s;
                }
                try{
                    attributeListTmp = attributeListTmp + vector.get(0).toString()+ ",";
                }
                catch(Exception exception) {
                    s = s + exception.getMessage() + "\n";
                    return s;
                }
            }
            //System.out.println("k="+k);
            if(k > 0){
                vector = cffilereader.getParam("refresh_time");
                if(vector != null){
                    setRefreshingPeriod(OFormat.getInt(vector.get(0).toString()));
                }
                //System.out.println("attributeListTmp="+attributeListTmp);
                attributeListTmp = attributeListTmp.substring(0,attributeListTmp.lastIndexOf(","));
                setAttributeListAsString(attributeListTmp);
            }
        }
        else{
            k = 0;
        }
        this.applyConfiguration(cffilereader);
        this.getXAxis().applyConfiguration("x", cffilereader);
        this.getY1Axis().applyConfiguration("y1", cffilereader);
        this.getY2Axis().applyConfiguration("y2", cffilereader);
        vector = cffilereader.getParam("dv" + 0 + "_barwidth");
        if(vector == null) {
            s = s + "Unable to find dv" + 0 + "_barwidth param";
            return s;
        }
        setWidth(Integer.parseInt(vector.get(0).toString().trim()));
        vector = cffilereader.getParam("xtitle");
        if(vector == null) {
            s = s + "Unable to find xtitle param";
            return s;
        }
        setXaxisName(vector.get(0).toString().replaceAll("'","").trim());
        vector = cffilereader.getParam("graph_title");
        if(vector == null) {
            s = s + "Unable to find graph_title param";
            return s;
        }
        setChartHeader(vector.get(0).toString().replaceAll("'","").trim());
        vector = cffilereader.getParam("y1title");
        if(vector == null) {
            s = s + "Unable to find y1title param";
            return s;
        }
        setUnit(vector.get(0).toString().replaceAll("'","").trim());
        return s;
    }
    
    private void setAttributeListAsString(String attributeListAsString)
    {
        StringTokenizer st = new StringTokenizer(attributeListAsString, ",");
        String[] attrlist = new String[st.countTokens()];
        int count = 0;


        while (st.hasMoreTokens())
	{
            attrlist[count++]=st.nextToken().trim();
        }
        if (attrlist!=null)
	{
            AttributeList tempattributeList = new AttributeList();
            for (int i=0;i<attrlist.length;i++)
	    {
                try
		{
                    tempattributeList.add(attrlist[i]);
                }
                catch (ConnectionException e) {
                    e.printStackTrace();
                    //System.exit(1); OH!!!!!!!!!!!!!!!
                }
            }
            setModel(tempattributeList);
            tempattributeList.startRefresher();
        }
    }



    public void actionPerformed(ActionEvent evt){
        if (evt.getActionCommand().trim().equalsIgnoreCase("Load configuration")) {
            loadPerformed();
        }
        else if (evt.getActionCommand().trim().equalsIgnoreCase("Save configuration")) {
            savePerformed();
        }
        else if (evt.getActionCommand().trim().equalsIgnoreCase("Set Refresh Interval..."))
	{
            int  ref_period = -1;
	    ref_period = getRefreshingPeriod();
            String refp_str = JOptionPane.showInputDialog(this,
	             "Enter refresh interval (ms)",(Object) new Integer(ref_period));
	    
	    if (refp_str != null)
	    {
	       if (refp_str.length() > 0 )
	       {
		   try
		   {
        	       int period_int = Integer.parseInt(refp_str);
		       setRefreshingPeriod(period_int);
		   }
		   catch ( NumberFormatException e )
		   {
        	       JOptionPane.showMessageDialog(this,"Invalid number !","Error",JOptionPane.ERROR_MESSAGE);
		       return;
		   }
	       }
	    }
	    //refreshDialog = new RefreshDialog(this);
        }
        else {
            super.actionPerformed(evt);
        }
    }

    private void loadPerformed() {
        if (model != null)
	   model.stopRefresher();
        
	clearModel();
	
	getY1Axis().clearDataView();
	
        double fault = faultLevel;
        double alarm = alarmLevel;
        initComponents();
        setFaultLevel(fault);
        setAlarmLevel(alarm);
        boolean flag = false;
        JFileChooser jfilechooser = new JFileChooser();
        jfilechooser.addChoosableFileFilter(new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String extension = getExtension(f);
                if (extension != null && extension.equals("txt"))
                    return true;
                return false;
            }

            public String getDescription() {
                return "text files ";
            }
        });
        int i = jfilechooser.showOpenDialog(this);
        if (i == 0) {
            File file = jfilechooser.getSelectedFile();
            if (file != null && !flag) {
                String s = loadSetting(file.getAbsolutePath());
                if (s.length() > 0)
                    JOptionPane.showMessageDialog(this.getParent(), s,
                            "Errors reading " + file.getName(), 0);
            }
        }
        repaint();
    }

    private void savePerformed() {
        int i = 0;
        JFileChooser jfilechooser = new JFileChooser(".");
        jfilechooser.addChoosableFileFilter(new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String extension = getExtension(f);
                if (extension != null && extension.equals("txt"))
                    return true;
                return false;
            }

            public String getDescription() {
                return "text files ";
            }
        });
        jfilechooser.setSelectedFile(new File(lastConfig));
        int j = jfilechooser.showSaveDialog(this.getParent());
        if(j == 0) {
            File file = jfilechooser.getSelectedFile();
    		if (getExtension(file) == null) {
    		    file = new File(file.getAbsolutePath() + ".txt");
    		}
            if(file != null) {
                if(file.exists())
                    i = JOptionPane.showConfirmDialog(this.getParent(), "Do you want to overwrite " + file.getName() + " ?", "Confirm overwrite", 0);
                if(i == 0)
                    saveSetting(file.getAbsolutePath());
            }
        }
    }
    

    /**
     * <code>getExtension</code> returns the extension of a given file,
     * that is the part after the last `.' in the filename.
     *
     * @param f a <code>File</code> value
     * @return a <code>String</code> value
     */
    public String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 &&  i < s.length() - 1) {
		    ext = s.substring(i+1).toLowerCase();
		}
		return ext;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        AttributeBarChart f = new AttributeBarChart();
        AttributeList attributeList = new AttributeList();
        try {
               attributeList.add("jlp/test/1/att_un");
               attributeList.add("jlp/test/1/att_deux");
               attributeList.add("jlp/test/1/att_trois");
               f.setModel(attributeList);
        }
        catch (ConnectionException e) {
            e.printStackTrace();
            System.exit(1);//RG comment : I added this code to avoid freeze
        }
        f.setWidth(120);
        f.setXaxisName("Pressures");
        f.setChartHeader("LT1");
        f.setUnit("mbar");
        f.setFaultLevel(200.0);
        f.setAlarmLevel(50.0);
        attributeList.startRefresher();
        frame.getContentPane().add(f, BorderLayout.CENTER);
        frame.setSize(640,480);
        frame.setTitle("Bar Chart Example 1");
        frame.addWindowListener(
            new WindowAdapter() {
	            public void windowClosing(WindowEvent e) {
	                System.exit(0);
	            }
            }
        );
        frame.setVisible(true);
    }
}
