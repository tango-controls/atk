
BEANINFO= NumberScalarViewerBeanInfo.java            \
          StringScalarViewerBeanInfo.java            \
          SimpleScalarViewerBeanInfo.java            \
          IconNumberScalarViewerBeanInfo.java        \
          IconNumberSpectrumViewerBeanInfo.java      \
          NumberScalarListViewerBeanInfo.java        \
          ScalarListViewerBeanInfo.java              \
          NumberScalarStateViewerBeanInfo.java       \
          SimpleStringSpectrumViewerBeanInfo.java    \
          NumberSpectrumPeakViewerBeanInfo.java    \
          NumberScalarProgressBarBeanInfo.java    \

#           AttributeStateViewerBeanInfo.java \


include ../../../../../../.make-include


ALL =	$(TARGET)/IAttributeViewer.class            \
	$(TARGET)/SimpleScalarViewer.class          \
	$(TARGET)/AScalarViewer.class               \
	$(TARGET)/ScalarListViewer.class            \
	$(TARGET)/ImageControlAdapter.class         \
	$(TARGET)/AttributeStateViewer.class        \
	$(TARGET)/PropertyFrame.class               \
	$(TARGET)/ChartDataEventAdapter.class       \
	$(TARGET)/NumberImageJTableAdapter.class    \
	$(TARGET)/IconNumberScalarViewer.class      \
	$(TARGET)/IconNumberSpectrumViewer.class    \
	$(TARGET)/StringScalarViewer.class          \
	$(TARGET)/NumberScalarViewer.class          \
	$(TARGET)/AttributeList.class               \
	$(TARGET)/Trend.class                       \
	$(TARGET)/TrendSelectionNode.class          \
	$(TARGET)/TrendRenderer.class               \
	$(TARGET)/AttributeViewerFactory.class      \
	$(TARGET)/NumberScalarWheelEditor.class     \
	$(TARGET)/NumberSpectrumViewer.class        \
	$(TARGET)/DualSpectrumImageViewer.class     \
	$(TARGET)/NumberScalarListViewer.class      \
	$(TARGET)/StringScalarEditor.class          \
	$(TARGET)/SimplePropertyFrame.class         \
	$(TARGET)/NumberScalarStateViewer.class     \
	$(TARGET)/NumberImageViewer.class           \
	$(TARGET)/IRoiListener.class                \
	$(TARGET)/RoiEvent.class                    \
	$(TARGET)/SimpleStringSpectrumViewer.class  \
	$(TARGET)/NonAttrNumberSpectrumViewer.class  \
	$(TARGET)/NumberSpectrumPeakViewer.class  \
	$(TARGET)/NumberScalarProgressBar.class  \
	$(TARGET)/NumberScalarComboEditor.class  \


all: attributes beaninfo icons

attributes: $(ALL)
	$(J)

#	$(TARGET)/GraphScalarViewer.class \

manifest: manifest-file



run: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.IconNumberSpectrumViewer

ssv: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.SimpleScalarViewer
nsv: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberScalarViewer
snsv: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.SimpleNumberScalarViewer

nst: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberImageJTableAdapter

trend: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.Trend
nslv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberScalarListViewer	
slv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.ScalarListViewer	
wse: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberScalarWheelEditor	
sse: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.StringScalarEditor	
pfv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.SimplePropertyFrame
nssv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberScalarStateViewer	
niv: attributes
	(cd ../util && $(MAKE) all)
	(cd ../image && $(MAKE) all)
	(cd ../attribute)
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberImageViewer
sssv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.SimpleStringSpectrumViewer	
nspv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberSpectrumPeakViewer	
nspb: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberScalarProgressBar	
cbe: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberScalarComboEditor	
