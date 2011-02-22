
package fr.esrf.tangoatk.widget.util;

/**
 * A Base class for atk formatting.
 */
public class ATKFormat {

  /** Returns a String representating the given number.
   * @param number A number
   */
  public String format(Number number) {
    return number.toString();
  }

  /** Returns a String representating the given string.
   * @param s A String
   */
  public String format(String s) {
    return s;
  }

  /** Returns a String representating the given object.
   * @param obj An Object
   */
  public String format(Object obj) {
    return obj.toString();
  }

  /**
   * Construct an ATKFormat object.
   */
  public ATKFormat() {
  }
}
