BEANINFO= JSmoothLabelBeanInfo.java            \

include ../../../../../../.make-include

ALL = $(TARGET)/UIManagerHelper.class       \
      $(TARGET)/ButtonBar.class             \
      $(TARGET)/ATKMenuBar.class            \
      $(TARGET)/Splash.class                \
      $(TARGET)/SplashPanel.class           \
      $(TARGET)/IControlee.class            \
      $(TARGET)/IApplicable.class           \
      $(TARGET)/IHelpful.class              \
      $(TARGET)/DeviceFinder.class          \
      $(TARGET)/ATKFormat.class             \
      $(TARGET)/ErrorPopup.class       \
      $(TARGET)/ATKFrame.class              \
      $(TARGET)/ATKField.class              \
      $(TARGET)/ATKStringField.class        \
      $(TARGET)/ATKNumberField.class        \
      $(TARGET)/AppenderWindow.class        \
      $(TARGET)/ErrorHistory.class          \
      $(TARGET)/ErrorTree.class             \
      $(TARGET)/ErrorAdapter.class          \
      $(TARGET)/ErrorPanel.class            \
      $(TARGET)/EntityJTableAdapter.class   \
      $(TARGET)/DropTable.class             \
      $(TARGET)/JAutoScrolledText.class     \
      $(TARGET)/JSmoothLabel.class          \
      $(TARGET)/JSmoothProgressBar.class    \
      $(TARGET)/WheelSwitchEvent.class      \
      $(TARGET)/IWheelSwitchListener.class  \
      $(TARGET)/WheelSwitch.class           \
      $(TARGET)/JArrowButton.class          \
      $(TARGET)/HelpWindow.class            \
      $(TARGET)/Gradient.class              \
      $(TARGET)/JGradientViewer.class       \
      $(TARGET)/JGradientEditor.class       \
      $(TARGET)/JTableRow.class             \
      $(TARGET)/JImage.class                \
      $(TARGET)/ThreeStateSwitch.class      \
      $(TARGET)/JSmoothDelayDialog.class    \
      $(TARGET)/ATKGraphicsUtils.class

all: itlk uchart utils icons beaninfo

utils: $(ALL)
	$(J)

run: all
	$(JAVA) fr.esrf.tangoatk.widget.util.ErrorHistory2

nf: all
	$(JAVA) fr.esrf.tangoatk.widget.util.ATKNumberField

mb: all
	$(JAVA) fr.esrf.tangoatk.widget.util.ATKMenuBar

error: all
	$(JAVA) fr.esrf.tangoatk.widget.util.ErrorHistory

splash: all
	$(JAVA) fr.esrf.tangoatk.widget.util.Splash

eta:	all
	$(JAVA) fr.esrf.tangoatk.widget.util.EntityJTableAdapter

ep:	all
	$(JAVA) fr.esrf.tangoatk.widget.util.ErrorPopup

df: 	all
	$(JAVA) fr.esrf.tangoatk.widget.util.DeviceFinder

uchart:
	(cd chart && $(MAKE) all)

itlk:
	(cd interlock && $(MAKE) all)

jsmoothdelay:all
	$(JAVA) fr.esrf.tangoatk.widget.util.JSmoothDelayDialog

