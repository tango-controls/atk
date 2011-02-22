// File:          ArrayCommandHelper.java
// Created:       2002-01-22 13:02:18, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 18:18:25, assum>
//
// $Id$
//
// Description:
package fr.esrf.tangoatk.core.command;

import fr.esrf.TangoApi.DeviceData;
import fr.esrf.Tango.DevState;

import java.util.*;

class ArrayCommandHelper extends ACommandHelper {

  ArrayCommandHelper(ACommand command) {
    super(command);
  }

  protected DeviceData setInput(List l) {
    int i, size = l.size();

    switch (getInType()) {
      case Tango_DEVVAR_SHORTARRAY:
        {
          short[] tmp = new short[size];
          for (i = 0; i < size; i++) {
            tmp[i] = Short.parseShort((String) l.get(i));
          } // end of for ()

          data.insert(tmp);
        }
        break;
      case Tango_DEVVAR_FLOATARRAY:
        {
          float[] tmp = new float[size];
          for (i = 0; i < size; i++) {
            tmp[i] = Float.parseFloat((String) l.get(i));
          } // end of for ()

          data.insert(tmp);
        }
        break;
      case Tango_DEVVAR_DOUBLEARRAY:
        {
          double[] tmp = new double[size];
          for (i = 0; i < size; i++) {
            tmp[i] = Double.parseDouble((String) l.get(i));
          } // end of for ()

          data.insert(tmp);
        }

        break;
      case Tango_DEVVAR_USHORTARRAY:
        {
          int[] tmp = new int[size];
          for (i = 0; i < size; i++) {
            tmp[i] = Integer.parseInt((String) l.get(i));
          } // end of for ()

          data.insert_us(tmp);
        }

        break;
      case Tango_DEVVAR_ULONGARRAY:
        {
          long[] tmp = new long[size];
          for (i = 0; i < size; i++) {
            tmp[i] = Long.parseLong((String) l.get(i));
          } // end of for ()

          data.insert_ul(tmp);
        }

        break;
      case Tango_DEVVAR_LONGARRAY:
        {
          int[] tmp = new int[size];
          for (i = 0; i < size; i++) {
            tmp[i] = Integer.parseInt((String) l.get(i));
          } // end of for ()

          data.insert(tmp);
        }
        break;
      case Tango_DEVVAR_STRINGARRAY:
        {
          String[] tmp = new String[size];
          for (i = 0; i < size; i++) {
            tmp[i] = (String) l.get(i);
          } // end of for ()

          data.insert(tmp);
        }
        break;
    }
    return data;
  }

  protected List<String> extractOutput(DeviceData d) {
    List<String> val = new Vector<String> ();
    int i;
    switch (getOutType()) {
      case Tango_DEVVAR_SHORTARRAY:
        {
          short[] tmp = d.extractShortArray();
          for (i = 0; i < tmp.length; i++) {
            val.add(Short.toString(tmp[i]));
          } // end of for ()
        }
        break;
      case Tango_DEVVAR_FLOATARRAY:
        {
          float[] tmp = d.extractFloatArray();
          for (i = 0; i < tmp.length; i++) {
            val.add(Float.toString(tmp[i]));
          } // end of for ()
        }
        break;
      case Tango_DEVVAR_DOUBLEARRAY:
        {
          double[] tmp = d.extractDoubleArray();
          for (i = 0; i < tmp.length; i++) {
            val.add(Double.toString(tmp[i]));
          }
        }
        break;
      case Tango_DEVVAR_LONGARRAY:
        {
          long[] tmp = d.extractULongArray();
          for (i = 0; i < tmp.length; i++) {
            val.add(Long.toString(tmp[i]));
          }
        }
        break;
      case Tango_DEVVAR_USHORTARRAY:
        {
          int[] tmp = d.extractUShortArray();
          for (i = 0; i < tmp.length; i++) {
            val.add(Integer.toString(tmp[i]));
          } // end of for ()
        }
        break;
      case Tango_DEVVAR_ULONGARRAY:
        {
          long[] tmp = d.extractULongArray();
          for (i = 0; i < tmp.length; i++) {
            val.add(Long.toString(tmp[i]));
          }
        }
        break;
      case Tango_DEVVAR_STRINGARRAY:
        {
          String[] tmp = d.extractStringArray();
          for (i = 0; i < tmp.length; i++) {
            val.add(tmp[i]);
          }
        }
        break;
    }

    return val;
  }

  public String getVersion() {
    return "$Id$";
  }

  private void readObject(java.io.ObjectInputStream in)
    throws java.io.IOException, ClassNotFoundException {
    in.defaultReadObject();
    try {
      serializeInit();
    } catch (Exception e) {
      throw new java.io.IOException(e.getMessage());
    }
  }


}
