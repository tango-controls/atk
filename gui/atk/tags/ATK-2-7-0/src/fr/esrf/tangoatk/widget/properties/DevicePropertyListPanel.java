/*	Synchrotron Soleil 
 *  
 *   File          :  DevicePropertiesPanel.java
 *  
 *   Project       :  ATKWidgetSoleil
 *  
 *   Description   :  
 *  
 *   Author        :  SOLEIL
 *  
 *   Original      :  9 sept. 2005 
 *  
 *   Revision:  					Author:  
 *   Date: 							State:  
 *  
 *   Log: DevicePropertiesPanel.java,v 
 *
 */
package fr.esrf.tangoatk.widget.properties;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import fr.esrf.tangoatk.core.Device;
import fr.esrf.tangoatk.core.DeviceFactory;
import fr.esrf.tangoatk.core.DeviceProperty;

class DevicePropertyListTableCellEditor extends AbstractCellEditor implements TableCellEditor {
    
    JTextArea c;
    
    public DevicePropertyListTableCellEditor() {
        c = null;
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {

        if (value instanceof String) {
             switch (column) {
            case 0:
                JTextField textField = new JTextField();
                textField = new JTextField();
                textField.setText((String)value);
                c = new JTextArea();
                c.setEditable(false);
                c.setText(textField.getText());
                textField.setEditable(false);
                return textField;

            case 1:
                c = new JTextArea();
                c.setText((String)value);
                c.setEditable(true);
                c.setEnabled(true);
                c.setLineWrap(false);
                c.setWrapStyleWord(false);
                c.setAutoscrolls(true);
                int height = (int) Math.rint(c.getPreferredSize().getHeight());
                if (table.getRowHeight() < height) {
                    table.setRowHeight(height);
                }
                return new JScrollPane(c);

            default:
                c = new JTextArea();
                c.setText("");
                c.setEditable(false);
                return null;
            }
        }
        else {
            JTextField textField = new JTextField();
            textField = new JTextField();
            textField.setText(value.toString());
            c = new JTextArea();
            c.setEditable(false);
            c.setText(textField.getText());
            return textField;
        }
     }

    public Object getCellEditorValue() {
        return ((JTextArea)c).getText();
    }
}
class DevicePropertyListTableCellRenderer implements TableCellRenderer {
    
    public DevicePropertyListTableCellRenderer() {
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = null;
        
        if (value instanceof String) {
            switch (column) {
                case 0 :
                    JTextField textField = new JTextField();
                    textField = new JTextField();
                    textField.setText((String)value);
                    textField.setEditable(false);
                    c = textField;
                    break;

                case 1 :
                    JTextArea textArea = new JTextArea();
                    textArea.setText((String)value);
                    textArea.setEditable(true);
                    textArea.setEnabled(true);
                    textArea.setLineWrap(false);
                    textArea.setWrapStyleWord(false);
                    int height = (int)Math.rint(textArea.getPreferredSize().getHeight());
                    if (table.getRowHeight() < height) {
                        table.setRowHeight(height);
                    }
                    c = textArea;
                    break;

                default :
                    //we have a bug
            }
        }
        else {
            JTextField textField = new JTextField();
            textField = new JTextField();
            textField.setText(value.toString());
            c = textField;
        }
        return c;
    }
}
class DevicePropertyListTableModel extends DefaultTableModel {
    private DeviceProperty [] rows;
    private String [] columnsNames;
    
    private static final int NO_SORT = 0;
    private static final int SORT_UP = 1;
    private static final int SORT_DOWN = 2;
    
    private int idSort = NO_SORT;
    
    
    /**
     * @param rowIndex
     * @return 8 juil. 2005
     */
    public DeviceProperty getContextAtRow ( int rowIndex ) {
        if ( rows != null ) {
            return rows [ rowIndex ];
        }
        else {
            return null;
        }
    }
    
    /**
     * Constructor
     */
    public DevicePropertyListTableModel (String title1, String title2) {
        columnsNames = new String [ this.getColumnCount () ];
        columnsNames [ 0 ] = title1;
        columnsNames [ 1 ] = title2;
        rows = new DeviceProperty[0];
    }
    
    /**
     * Applies modifications
     */
    public void apply() {
        for (int i = 0; i<rows.length; i++) {
            rows[i].store();
        }
    }
    
    /**
     *  8 juil. 2005
     */
    public void reset () {
        int firstRemoved = 0;
        int lastRemoved = rows.length - 1;
        
        this.rows = null;
        
        this.fireTableRowsDeleted ( firstRemoved , lastRemoved );
    }
    
    /**
     * @param indexesToRemove 13 juil. 2005
     */
    public void removeRows(int[] indexesToRemove) {
        int numberOfLinesToRemove = indexesToRemove.length;
        DeviceProperty[] newRows = new DeviceProperty[rows.length - numberOfLinesToRemove];
        
        Vector idsToRemoveList = new Vector(numberOfLinesToRemove); 
        for ( int i = 0 ; i < numberOfLinesToRemove ; i ++ ) {
            int idOfLineToRemove = indexesToRemove[i];
            idsToRemoveList.add (new Integer(idOfLineToRemove));
        }
        
        int j = 0;
        for (int i = 0; i < rows.length; i ++) {
            Integer idOfCurrentLine = new Integer(i);
            if (!idsToRemoveList.contains(idOfCurrentLine)) {
                newRows[j] = rows[i];
                j++;
            }
        }
        rows = newRows;
        for ( int i = 0 ; i < numberOfLinesToRemove ; i ++ ) {
            this.fireTableRowsDeleted(indexesToRemove[i] , indexesToRemove[i]);
        }
    }
    
    /**
     * @param _rows 8 juil. 2005
     */
    public void setRows(DeviceProperty[] _rows) {
        rows = _rows;
        fireTableRowsInserted (0 , _rows.length-1);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount()  {
        return 2; 
    }
 
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        if ( rows == null ) {
            return 0;
        }
        return rows.length;
    }
 
    
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt (int rowIndex, int columnIndex)  {
        Object value = null;

        switch (columnIndex) {
            case 0:
                value = rows[rowIndex].getName(); 
            break;

            case 1:
                value = rows[rowIndex].getStringValue();
            break;

            default:
                //we have a bug
        }
        return value;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#setValueAt(Object, int, int)
     */
    public void setValueAt(Object aValue, int row, int column) {
         switch (column) {
            case 0:
                return; 

            case 1:
                rows[row].setValue((String)aValue);
            break;

            default:
                return;
        }
     }

    
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName (int columnIndex) {
        return columnsNames[columnIndex];
    }
    
    public void setColumnName(int columnIndex, String columnName) {
        if (columnIndex < getColumnCount()) {
            columnsNames[columnIndex] = columnName;
        }
    }
 
}

public class DevicePropertyListPanel extends JFrame implements Serializable {

    protected DevicePropertyListTableModel tableModel;
    protected DevicePropertyListTableCellRenderer tableCellRenderer;
    protected DevicePropertyListTableCellEditor tableCellEditor;
    protected Device device = null;
    protected String dismissText = "Close";
    protected String applyChangeText = "Modify";
    protected String refreshText = "Refresh";
    protected String titleText = "Properties";
    protected String propertiesNameColumnText = "Name";
    protected String propertiesValueColumnText = "Values";
    protected boolean propertyListEditable = true;
    protected boolean askConfirmation = false;
    protected String titleAskConfirmation = "Confirmation";
    protected String textAskConfirmation = "Do you wish to modify the properties ?";
    protected JScrollPane textView = null;
    protected JTable theTable;
    protected JButton okButton;
    protected JButton applyButton;
    protected JButton refreshButton;
    protected JPanel innerPanel;
    protected String colName[] = { propertiesNameColumnText,
                                   propertiesValueColumnText };
    protected DeviceProperty[] data = null;
    protected Device m_device = null;

    /**
     * Constructor
     * 
     * @throws java.awt.HeadlessException
     */
    public DevicePropertyListPanel() throws HeadlessException {
        super();

        data = new DeviceProperty[0];
        getContentPane().setLayout(new BorderLayout());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                hide();
                dispose();
            }
        });

        innerPanel = new JPanel();
        innerPanel.setLayout(new FlowLayout());

        okButton = new JButton(dismissText);
        okButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                hide();
                dispose();
            }
        });

        applyButton = new JButton(applyChangeText);
        applyButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                // Apply resources
                applyChange();
            }
        });
        refreshButton = new JButton(refreshText);
        refreshButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                // Refresh Table
                refresh();
            }
        });

        tableModel = new DevicePropertyListTableModel(propertiesNameColumnText,propertiesValueColumnText);
        theTable = new JTable(tableModel);
        tableCellRenderer = new DevicePropertyListTableCellRenderer();
        theTable.setDefaultRenderer(Object.class, tableCellRenderer);
        tableCellEditor = new DevicePropertyListTableCellEditor();
        theTable.setDefaultEditor(Object.class, tableCellEditor);
        textView = new JScrollPane(theTable);
        getContentPane().add(textView, BorderLayout.CENTER);

        innerPanel.add(applyButton);
        innerPanel.add(okButton);
        innerPanel.add(refreshButton);
        setBounds(0, 0, 600, 400);

        setTitle(titleText);
        getContentPane().add(innerPanel, BorderLayout.SOUTH);
    }

    /**
     * Constructs the widget, associates a Device, and sets the title of the
     * columns of the table
     * 
     * @throws java.awt.HeadlessException
     */
    public DevicePropertyListPanel(Device aDevice,
            String apropertiesNameColumnText,
            String apropertiesValueColumnText)
            throws HeadlessException {

        super();
        data = new DeviceProperty[0];
        setPropertiesNameColumnText(apropertiesNameColumnText);
        setPropertiesValueColumnText(apropertiesValueColumnText);

        getContentPane().setLayout(new BorderLayout());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                hide();
                dispose();
            }
        });

        innerPanel = new JPanel();
        innerPanel.setLayout(new FlowLayout());

        okButton = new JButton(dismissText);
        okButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                hide();
                dispose();
            }
        });

        applyButton = new JButton(applyChangeText);
        applyButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                // Apply resources
                applyChange();
            }
        });

        refreshButton = new JButton(refreshText);
        refreshButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                // Refresh Table
                refresh();
            }
        });
        
        tableModel = new DevicePropertyListTableModel(propertiesNameColumnText,propertiesValueColumnText);
        theTable = new JTable(tableModel);
        tableCellRenderer = new DevicePropertyListTableCellRenderer();
        theTable.setDefaultRenderer(Object.class, tableCellRenderer);
        tableCellEditor = new DevicePropertyListTableCellEditor();
        theTable.setDefaultEditor(Object.class, tableCellEditor);
        textView = new JScrollPane(theTable);
        getContentPane().add(textView, BorderLayout.CENTER);

        innerPanel.add(applyButton);
        innerPanel.add(okButton);
        innerPanel.add(refreshButton);
        setBounds(0, 0, 600, 400);

        getContentPane().add(innerPanel, BorderLayout.SOUTH);
        setDevice(aDevice);
        try {
            initTable();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        setTitle(titleText);
    }

    protected void clearTable() {
        int[] temp = new int[data.length];
        for (int i=0; i<temp.length; i++) {
            temp[i] = i;
        }
        tableModel.removeRows(temp);
    }
    
    protected void initTable() {
        tableModel.setRows(data);
        theTable.setEnabled(propertyListEditable);
    }

    protected void refresh() {
        theTable.editingCanceled(null);
        clearTable();
        data = null;
        device.refreshPropertyMap();
        Map propertyMap = device.getPropertyMap();
        Set keySet = propertyMap.keySet();
        if (keySet != null) {
            data = new DeviceProperty[keySet.size()];
            Iterator it = keySet.iterator();
            int i = 0;
            while (it.hasNext()) {
                data[i] = (DeviceProperty)propertyMap.get(it.next());
                i++;
            }
        }
        else {
            data = new DeviceProperty[0];
        }
        if (data.length > 0) {
            applyButton.setEnabled(true);
        }
        else {
            applyButton.setEnabled(false);
        }
        initTable();
    }

    /**
     *  Apply resource change
     */
    public void applyChange() {
        if (theTable.isEditing()) {
            tableModel.setValueAt(tableCellEditor.getCellEditorValue(),
                    theTable.getEditingRow(), theTable.getEditingColumn());
        }

        if (data == null || data.length == 0) {
            return;
        }

        int ok = JOptionPane.YES_OPTION;
        if (askConfirmation)
            ok = JOptionPane.showConfirmDialog(this, textAskConfirmation,
                    titleAskConfirmation, JOptionPane.YES_NO_OPTION);

        if (ok == JOptionPane.YES_OPTION) {
            tableModel.apply();
        }

    }

    /**
     * @return The Device of which you view the properties
     */
    public Device getDevice() {
        return device;
    }

    /**
     * Sets the device of which you want to view the properties
     * @param aDevice the device
     */
    public void setDevice(Device aDevice) {
        clearTable();
        data = null;
        device = aDevice;
        if (device != null) {
            refreshButton.setEnabled(true);
            Map propertyMap = device.getPropertyMap();
            Set keySet = propertyMap.keySet();
            if (keySet!=null) {
                data = new DeviceProperty[keySet.size()];
                Iterator it = keySet.iterator();
                int i = 0;
                while (it.hasNext()) {
                    data[i] = (DeviceProperty)propertyMap.get(it.next());
                    i++;
                }
            }
            else {
                data = new DeviceProperty[0];
            }
        }
        else {
            data = new DeviceProperty[0];
            refreshButton.setEnabled(false);
        }
        if (data.length > 0) {
            applyButton.setEnabled(true);
        }
        else {
            applyButton.setEnabled(false);
        }
        initTable();
    }

    /**
     * usefull method when you need to do modifications
     * of the table like background color control, etc...
     * @return the table of this widget
     */
    public JTable getTable() {
        return theTable;
    }

    /**
     * @return the text of the "apply" button
     */
    public String getApplyChangeText() {
        return applyChangeText;
    }
    
    /**
     * sets the text of the "apply" button
     * @param applyChangeText the text to set
     */
    public void setApplyChangeText(String applyChangeText) {
        this.applyChangeText = applyChangeText;
        applyButton.setText(applyChangeText);
    }

    /**
     * @return the text of the "close" button
     */
    public String getDismissText() {
        return dismissText;
    }

    /**
     * sets the text of the "close" button
     * @param dismissText the text to set
     */
    public void setDismissText(String dismissText) {
        this.dismissText = dismissText;
        okButton.setText(dismissText);
    }

    /**
     * @return the title of the Frame
     */
    public String getTitleText() {
        return titleText;
    }

    /**
     * sets the Frame title
     * @param titleText the title
     */
    public void setTitleText(String titleText) {
        this.titleText = titleText;
        setTitle(titleText);
    }

    /**
     * @return the title of the column "names"
     */
    public String getPropertiesNameColumnText() {
        return propertiesNameColumnText;
    }

    /**
     * sets the title of the column "names"
     * @param propertiesNameColumnText the title
     */
    public void setPropertiesNameColumnText(String propertiesNameColumnText) {
        this.propertiesNameColumnText = propertiesNameColumnText;
        colName[0] = propertiesNameColumnText;
    }

    /**
     * @return the title of the column "values"
     */
    public String getPropertiesValueColumnText() {
        return propertiesValueColumnText;
    }

    /**
     * sets the title of the column "values"
     * @param propertiesNameColumnText the title
     */
    public void setPropertiesValueColumnText(String propertiesValueColumnText) {
        this.propertiesValueColumnText = propertiesValueColumnText;
        colName[1] = propertiesValueColumnText;
    }

    /**
     * @return a boolean to know wheather the table is editable or not
     */
    public boolean isPropertyListEditable() {
        return propertyListEditable;
    }

    /**
     * sets wheather the table is editable or not
     * @param propertyListEditable table is editable or not
     */
    public void setPropertyListEditable(boolean propertyListEditable) {
        this.propertyListEditable = propertyListEditable;
        applyButton.setEnabled(propertyListEditable);
        if (theTable != null)
            theTable.setEnabled(propertyListEditable);
    }

    /**
     * @return The title to ask confirmation for modifications
     */
    public String getTitleAskConfirmation() {
        return titleAskConfirmation;
    }

    /**
     * sets the title to ask confirmation for modifications
     * @param titleAskConfirmation the title
     */
    public void setTitleAskConfirmation(String titleAskConfirmation) {
        if (titleAskConfirmation.equals(""))
            return;
        this.titleAskConfirmation = titleAskConfirmation;
    }

    /**
     * @return a boolean that tells wheather you have to confirm
     * the modifications or not on click on "apply" button
     */
    public boolean isAskConfirmation() {
        return askConfirmation;
    }

    /**
     * sets wheather you have to confirm
     * the modifications or not on click on "apply" button
     * @param askConfirmation the corresponding boolean
     */
    public void setAskConfirmation(boolean askConfirmation) {
        this.askConfirmation = askConfirmation;
    }

    /**
     * @return The message to ask confirmation for modifications
     */
    public String getTextAskConfirmation() {
        return textAskConfirmation;
    }

    /**
     * sets the message to ask confirmation for modifications
     * @param titleAskConfirmation the message
     */
    public void setTextAskConfirmation(String textAskConfirmation) {
        if (textAskConfirmation.equals(""))
            return;
        this.textAskConfirmation = textAskConfirmation;
    }
    
    /**
     * Main class, so you can test this widget.
     * Give the name of your device as parameter
     */
    public static void main(String[] args) {
        try {
            if (args.length != 0 && args.length != 1) {
                System.out.println("wrong arguments");
                System.exit(2);
            }
            Device d;
            System.out.println("accessing device...");
            if (args.length > 0) {
                d = DeviceFactory.getInstance().getDevice(args[0]);
                System.out.println("device accessed");
            }
            else {
                d = DeviceFactory.getInstance().getDevice("tango/tangotest/1");
                System.out.println("device accessed");
            }
            System.out.println("loading widget...");
            DevicePropertyListPanel dplp = new DevicePropertyListPanel();
            dplp.setPropertyListEditable(true);
            dplp.setDevice(d);
            dplp.show();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}