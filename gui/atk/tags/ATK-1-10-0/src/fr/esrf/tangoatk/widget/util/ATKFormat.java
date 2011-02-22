// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) space 
// Source File Name:   ATKFormat.java

package fr.esrf.tangoatk.widget.util;


public class ATKFormat {

  public String format(Number number) {
    return number.toString();
  }

  public String format(String s) {
    return s;
  }

  public String format(Object obj) {
    return obj.toString();
  }

  public ATKFormat() {
  }
}
