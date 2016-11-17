include ../../../../../.make-include

ALL = $(TARGET)/AppLauncher.class           \
      $(TARGET)/IATKRunnable.class          \
      $(TARGET)/BeanPropertyGenerator.class \
      $(TARGET)/BeanPropertyVerifier.class \
      $(TARGET)/JarVerifier.class \
      $(TARGET)/AtkTimer.class \

all: $(ALL)
	$(J)

run:
	$(JAVA) fr.esrf.tangoatk.util.AppLauncher

verify-jar: all
	$(JAVA) fr.esrf.tangoatk.util.JarVerifier /tmp/widget/jar

genrun:
	@$(JAVA) fr.esrf.tangoatk.util.BeanPropertyGenerator test.info test.java


verrun:
	@$(JAVA) fr.esrf.tangoatk.util.BeanPropertyVerifier fr.esrf.TangoATK.Widget.Attribute.StringScalarViewer fr.esrf.TangoATK.Widget.Attribute.StringScalarViewerBeanInfo

test:
