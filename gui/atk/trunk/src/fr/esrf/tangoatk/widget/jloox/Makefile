BEANINFO =	SynopticFileViewerBeanInfo.java 

include ../../../../../../.make-include


ALL = $(TARGET)/TangoSynopticHandler.class \
      $(TARGET)/SynopticFileViewer.class \
      $(TARGET)/DeviceSynopticViewer.class \
      $(TARGET)/DeviceSynopticFrame.class


all: jloox beaninfo icons

jloox: $(ALL)
	echo $(CLASSPATH)
	$(J)

synoptic: all
	echo $(CLASSPATH)
	$(JAVA) fr.esrf.tangoatk.widget.jloox.TangoSynopticHandler
	
manifest: manifest-file

sfv: all
	echo $(CLASSPATH)
	$(JAVA) fr.esrf.tangoatk.widget.jloox.SynopticFileViewer

dsv: all
	echo $(CLASSPATH)
	$(JAVA) fr.esrf.tangoatk.widget.jloox.DeviceSynopticViewer

dsf: all
	echo $(CLASSPATH)
	$(JAVA) fr.esrf.tangoatk.widget.jloox.DeviceSynopticFrame
