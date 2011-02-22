
# BEANINFO =  ATKNumberFieldBeanInfo.java \

include ../../../../../../.make-include

ALL = $(TARGET)/UIManagerHelper.class       \
      $(TARGET)/ButtonBar.class             \
      $(TARGET)/ATKMenuBar.class            \
      $(TARGET)/Splash.class                \
      $(TARGET)/IControlee.class            \
      $(TARGET)/IApplicable.class           \
      $(TARGET)/IHelpful.class              \
      $(TARGET)/DeviceFinder.class          \
      $(TARGET)/ATKFormat.class             \
      $(TARGET)/SplashPanel.class           \
      $(TARGET)/LuserErrorPopup.class       \
      $(TARGET)/ATKFrame.class              \
      $(TARGET)/ATKField.class              \
      $(TARGET)/ATKStringField.class        \
      $(TARGET)/ATKNumberField.class        \
      $(TARGET)/AppenderWindow.class        \
      $(TARGET)/ErrorHistory.class          \
      $(TARGET)/ErrorTree.class             \
      $(TARGET)/ErrorAdapter.class          \
      $(TARGET)/ErrorPanel.class            \
      $(TARGET)/BasicLogWindow.class        \
      $(TARGET)/EntityJTableAdapter.class   \
      $(TARGET)/DropTable.class             \

all: utils icons

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

lep:	all
	$(JAVA) fr.esrf.tangoatk.widget.util.LuserErrorPopup

df: 	all
	$(JAVA) fr.esrf.tangoatk.widget.util.DeviceFinder

ci:
	@cvs ci Makefile `echo $(ALL) \
                     | sed 's|$(TARGET)/||g' |  \
                     sed 's|\.class|.java|g'`

