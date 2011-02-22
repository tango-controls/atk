package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;

public class NumberScalarHistory extends AAttributeHistory implements INumberScalarHistory
{
    private  double     attval;
    
    
    protected void setValue(double  val)
    {
       attval = val;
    }

    
    public double getValue()
    {
       return(attval);
    }

    
    public String toString()
    {
        String    str= new String();
		
	str = str.concat("{");
	str = str.concat(String.valueOf(this.getTimestamp()));
	str = str.concat(",");
	str = str.concat(this.getState());
	str = str.concat(",");
	str = str.concat(String.valueOf(this.getValue()));
	str = str.concat("}");
        return str;
    }
}
