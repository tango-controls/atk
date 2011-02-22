package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;

public class StringScalarHistory extends AAttributeHistory implements IStringScalarHistory
{
    private  java.lang.String     attval;
    
    
    protected void setValue(java.lang.String  val)
    {
       attval = val;
    }
    
    public java.lang.String getValue()
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
	str = str.concat(this.getValue());
	str = str.concat("}");

        return str;
    }


}
