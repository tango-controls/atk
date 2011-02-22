package fr.esrf.tangoatk.core.attribute;


public abstract class ANumber extends AAttribute {

	private NumberAttributeHelper numberHelper = null; 
	
	public double getMinValue() {
		return getPropertyStorage().getNumberProperty("min_value");
	}

	public double getMaxValue() {
		return getPropertyStorage().getNumberProperty("max_value");
	}

	public double getMinAlarm() {
		return getPropertyStorage().getNumberProperty("min_alarm");
	}

	public double getMaxAlarm() {
		return getPropertyStorage().getNumberProperty("max_alarm");
	}

	public double getMinWarning() {
		return getPropertyStorage().getNumberProperty("min_warning");
	}

	public double getMaxWarning() {
		return getPropertyStorage().getNumberProperty("max_warning");
	}

	public double getDeltaT() {
		return getPropertyStorage().getNumberProperty("delta_t");
	}

	public double getDeltaVal() {
		return getPropertyStorage().getNumberProperty("delta_val");
	}

	public void setMinValue(double d) {
		numberHelper.setMinValue(d);
	}

	public void setMaxValue(double d) {
		numberHelper.setMaxValue(d);
	}

	public void setMinAlarm(double d) {
		numberHelper.setMinAlarm(d);
	}

	public void setMaxAlarm(double d) {
		numberHelper.setMaxAlarm(d);
	}

	public void setMinWarning(double d) {
		numberHelper.setMinWarning(d);
	}

	public void setMaxWarning(double d) {
		numberHelper.setMaxWarning(d);
	}

	public void setDeltaT(double d) {
		numberHelper.setDeltaT(d);
	}

	public void setDeltaVal(double d) {
		numberHelper.setDeltaVal(d);
	}

	public void setMinValue(double d, boolean writable) {
		numberHelper.setMinValue(d, writable);
	}

	public void setMaxValue(double d, boolean writable) {
		numberHelper.setMaxValue(d, writable);
	}

	public void setMinAlarm(double d, boolean writable) {
		numberHelper.setMinAlarm(d, writable);
	}

	public void setMaxAlarm(double d, boolean writable) {
		numberHelper.setMaxAlarm(d, writable);
	}

	public void setMinWarning(double d, boolean writable) {
		numberHelper.setMinWarning(d, writable);
	}

	public void setMaxWarning(double d, boolean writable) {
		numberHelper.setMaxWarning(d, writable);
	}

	public void setDeltaT(double d, boolean writable) {
		numberHelper.setDeltaT(d, writable);
	}

	public void setDeltaVal(double d, boolean writable) {
		numberHelper.setDeltaVal(d, writable);
	}

	public NumberAttributeHelper getNumberHelper() {
		return numberHelper;
	}

	public void setNumberHelper(NumberAttributeHelper numberHelper) {
		this.numberHelper = numberHelper;
	}

}
