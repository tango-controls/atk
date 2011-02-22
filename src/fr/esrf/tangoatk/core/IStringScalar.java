// File:          IStringScalar.java
// Created:       2002-03-21 13:32:39, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-05-22 16:4:42, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;


public interface IStringScalar extends IScalarAttribute, IString {

    public void addStringScalarListener(IStringScalarListener l);

    public void removeStringScalarListener(IStringScalarListener l);

    public void setValue(String s) throws AttributeSetException;

    public String getStringValue();
    
    public String getStringSetPoint();
    
    public IStringScalarHistory[] getStringScalarHistory();

}
