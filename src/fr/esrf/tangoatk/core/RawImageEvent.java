// File:          RawImageEvent.java
// Created:       2005-02-04 09:31:10, poncet
// By:            <pons@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;


public class RawImageEvent extends ATKEvent {

  byte[][] value;
  long timeStamp;

  public RawImageEvent(IRawImage source, byte[][] value, long timeStamp) {
    super(source, timeStamp);
    setValue(value);
  }

  public byte[][] getValue() {
    return value;
  }

  public void setValue(byte[][] value) {
    this.value = value;
  }

  public void setSource(IRawImage source) {
    this.source = source;
  }

  public String getVersion() {
    return "$Id$";
  }
}
