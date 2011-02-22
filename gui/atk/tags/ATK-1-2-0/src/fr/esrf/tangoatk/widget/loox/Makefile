




include ../../../../../../.make-include


ALL = $(TARGET)/JLooxAdapter.class \
      $(TARGET)/SimpleJLooxAdapter.class \
      $(TARGET)/SimpleJLooxViewer.class


all: jloox beaninfo icons

jloox: $(ALL)
	echo $(CLASSPATH)
	$(J)

sadapter: all
	echo $(CLASSPATH)
	$(JAVA) fr.esrf.tangoatk.widget.loox.SimpleJLooxAdapter

sjv: all
	echo $(CLASSPATH)
	$(JAVA) fr.esrf.tangoatk.widget.loox.SimpleJLooxViewer
