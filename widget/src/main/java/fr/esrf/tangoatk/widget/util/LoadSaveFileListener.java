package fr.esrf.tangoatk.widget.util;

import fr.esrf.tangoatk.widget.util.SettingsManagerProxy;

import java.io.File;

/**
 * Interface used to execute code during loading or saving setting file
 */
public interface LoadSaveFileListener {

  // Called when the user has pressed the load button, but before reading the file
  public void beforeLoad(SettingsManagerProxy src,File f);
  // Called when the user has pressed the load button, after the file has been read
  public void afterLoad(SettingsManagerProxy src,File f);
  // Called when the user has pressed the save button, but before writing the file
  public void beforeSave(SettingsManagerProxy src,File f);
  // Called when the user has pressed the save button, after the file has been saved
  public void afterSave(SettingsManagerProxy src,File f);

}
