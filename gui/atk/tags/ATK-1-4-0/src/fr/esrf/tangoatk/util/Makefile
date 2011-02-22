include ../../../../../.make-include

ALL = $(TARGET)/AppLauncher.class           \
      $(TARGET)/IATKRunnable.class          \
      $(TARGET)/BeanPropertyGenerator.class \
      $(TARGET)/BeanPropertyVerifier.class \
      $(TARGET)/JarVerifier.class \

all: $(ALL)
	$(J)

run:
	$(JAVA) fr.esrf.TangoATK.Util.AppLauncher

verify-jar: all
	$(JAVA) fr.esrf.tangoatk.util.JarVerifier /tmp/widget/jar

genrun:
	@$(JAVA) fr.esrf.TangoATK.Util.BeanPropertyGenerator test.info test.java


verrun:
	@$(JAVA) fr.esrf.TangoATK.Util.BeanPropertyVerifier fr.esrf.TangoATK.Widget.Attribute.StringScalarViewer fr.esrf.TangoATK.Widget.Attribute.StringScalarViewerBeanInfo

test:
