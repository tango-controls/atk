package fr.esrf.tangoatk.core;

import java.util.*;
import fr.esrf.tangoatk.core.attribute.AttributeFactory;

public class AttributePolledList extends AttributeList
{

    public AttributePolledList()
    {
	polledList = true;
    }

    public String getVersion()
    {
	return "$Id$";
    }

}
