




include ../../../../../../.make-include


ALL = $(TARGET)/JLooxAdapter.class \
      $(TARGET)/SimpleJLooxViewer.class \


all: jloox beaninfo icons

jloox: $(ALL)
	$(J)

adapter: all
	$(JAVA) fr.esrf.tangoatk.widget.loox.JLooxAdapter