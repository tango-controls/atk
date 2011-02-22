// File:          StringVoidCommand.java

package fr.esrf.tangoatk.core.command;

public class StringVoidCommand extends ScalarVoidCommand 
{

    public StringVoidCommand()
    {
        super();
    }

    public String getVersion()
    {
	return "$Id$";
    }

}


