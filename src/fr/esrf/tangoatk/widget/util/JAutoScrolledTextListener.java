package fr.esrf.tangoatk.widget.util;

import java.util.EventListener;

/** An interface to handle dimension change of the AutoScrolled text. */
public interface JAutoScrolledTextListener extends EventListener {

  /** Trigger when the text exceed component bounds */
  public void textExceedBounds(JAutoScrolledText source);

}
