package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;

public abstract class AAttributeHistory
{
    private String      state;
    private long        timestamp;


    protected AAttributeHistory()
    {
	setState(IAttribute.UNKNOWN);
    }

    protected void setState(String s)
    {
	state = s;
    }

    public String getState()
    {
	return(state);
    }

    protected void setTimestamp(long t)
    {
	timestamp = t;
    }

    public long getTimestamp()
    {
	return (timestamp);
    }
   
   
}
