package fr.esrf.tangoatk.core.util;

/*
 * INonAttrSpectrumListener.java
 *
 * Created on 12 septembre 2003, 11:00
 */


import java.util.EventListener;

/**
 *
 * @author  OUNSY
 */
public interface INonAttrSpectrumListener extends EventListener {
    public void spectrumChange(NonAttrNumberSpectrumEvent e);    
}
