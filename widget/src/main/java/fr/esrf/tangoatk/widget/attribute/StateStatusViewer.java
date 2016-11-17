/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.IDevStateScalar;
import fr.esrf.tangoatk.core.IDevStateScalarListener;
import fr.esrf.tangoatk.core.IDevice;
import fr.esrf.tangoatk.core.IStringScalar;
import fr.esrf.tangoatk.core.IStringScalarListener;
import fr.esrf.tangoatk.widget.util.ATKConstant;
import fr.esrf.tangoatk.widget.util.JAutoScrolledText;

/**
 *
 * @author poncet
 * 
 * A Text field which display a status and whose background color is linked to the device state
 */
public class StateStatusViewer extends JAutoScrolledText implements IDevStateScalarListener, IStringScalarListener
{

    private IDevStateScalar stateModel = null;
    private IStringScalar statusModel = null;
    private String currentState = null;
    private String currentStatus = null;

    /**
     * Creates a new instance of StateStatusViewer
     */
    public StateStatusViewer()
    {
        currentState = new String(IDevice.UNKNOWN);
        currentStatus = new String(IDevice.UNKNOWN);
        setBackground(ATKConstant.getColor4State(currentState));
        setOpaque(true);
        setText(currentStatus);
        setFont(new java.awt.Font("Dialog", 0, 14));
    }

    /**
    * <code>setStateModel</code> sets one of the models of this viewer (stateModel).
     * @param stateAtt a <code>DevStateScalar</code>
     */
    public void setStateModel(IDevStateScalar stateAtt)
    {
        if (stateModel != null)
        {
            stateModel.removeDevStateScalarListener(this);
            stateModel = null;
            setToolTipText(null);
        }

        if (stateAtt == null)
            return;

        stateModel = stateAtt;
        stateAtt.addDevStateScalarListener(this);

        setToolTipText(stateAtt.getName());
        setCurrentState(stateAtt.getDeviceValue());
    }

    /**
     * <code>getStateModel</code> gets the state model of this viewer.
     *
     * @return a <code>DevStateScalar</code> value
     */
    public IDevStateScalar getStateModel()
    {
        return stateModel;
    }

    /**
    * <code>setStatusModel</code> sets one of the models of this viewer (statusModel).
     * @param stateAtt a <code>IStringScalar</code>
     */
    public void setStatusModel(IStringScalar statusAtt)
    {
        if (statusModel != null)
        {
            statusModel.removeStringScalarListener(this);
            statusModel = null;
        }

        if (statusAtt == null)
            return;

        statusModel = statusAtt;
        statusModel.addStringScalarListener(this);
        currentStatus = new String(statusModel.getStringDeviceValue());
        setText(currentStatus);
    }

    /**
     * <code>getStatusModel</code> gets the status model of this StateStatusViewer.
     *
     * @return a <code>IStringScalar</code> value
     */
    public IStringScalar getStatusModel()
    {
        return statusModel;
    }

    public void devStateScalarChange(fr.esrf.tangoatk.core.DevStateScalarEvent devStateScalarEvent)
    {
        setCurrentState(devStateScalarEvent.getValue());
    }

    public void errorChange(fr.esrf.tangoatk.core.ErrorEvent errorEvent)
    {
        if (errorEvent.getSource() == stateModel)
        {
            setCurrentState(IDevice.UNKNOWN);
            return;
        }
        if (!currentStatus.equals(IDevice.UNKNOWN))
        {
            currentStatus = new String(IDevice.UNKNOWN);
            setText(currentStatus);
        }
    }

    public void stateChange(fr.esrf.tangoatk.core.AttributeStateEvent attributeStateEvent)
    {// quality factor change for status string attribute : do nothing
    }

    public void stringScalarChange(fr.esrf.tangoatk.core.StringScalarEvent stringScalarEvent)
    {
        String val;

        val = stringScalarEvent.getValue();
        if (!val.equals(currentStatus))
        {
            currentStatus = new String(val);
            setText(currentStatus);
        }
    }

    private void setCurrentState(String stateStr)
    {
        if (!currentState.equals(stateStr))
        {
            currentState = new String(stateStr);
            setBackground(ATKConstant.getColor4State(currentState));
        }
    }

}

