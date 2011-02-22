
BEANINFO= NumberScalarViewerBeanInfo.java            \
          StringScalarViewerBeanInfo.java            \
          JChartAdapterBeanInfo.java                 \
          NumberSpectrumJChartAdapterBeanInfo.java   \
          IconNumberScalarViewerBeanInfo.java        \
          IconNumberSpectrumViewerBeanInfo.java      \

#           AttributeStateViewerBeanInfo.java \
#           JChartAttributeViewerBeanInfo.java \

#           StringImageViewerBeanInfo.java \

#      GraphScalarViewerBeanInfo.java \


include ../../../../../../.make-include


ALL =	$(TARGET)/IAttributeViewer.class            \
	$(TARGET)/SimpleScalarViewer.class          \
	$(TARGET)/AScalarViewer.class               \
	$(TARGET)/ScalarListViewer.class            \
	$(TARGET)/ImageControlAdapter.class         \
	$(TARGET)/AttributeStateViewer.class        \
	$(TARGET)/PropertyFrame.class               \
	$(TARGET)/ChartDataEventAdapter.class       \
	$(TARGET)/NumberSpectrumJChartAdapter.class \
	$(TARGET)/NumberImageJChartAdapter.class    \
	$(TARGET)/NumberImageJTableAdapter.class    \
	$(TARGET)/JChartAdapter.class               \
	$(TARGET)/IconNumberScalarViewer.class      \
	$(TARGET)/IconNumberSpectrumViewer.class    \
	$(TARGET)/JChartAttributeViewer.class       \
	$(TARGET)/StringScalarViewer.class          \
	$(TARGET)/NumberScalarViewer.class          \
	$(TARGET)/AttributeList.class               \
	$(TARGET)/TrendOptions.class                \
	$(TARGET)/Trend.class                       \
	$(TARGET)/AttributeViewerFactory.class      \


all: attributes beaninfo icons

attributes: $(ALL)
	$(J)

#	$(TARGET)/GraphScalarViewer.class \

manifest: manifest-file


ci:
	@cvs ci Makefile `echo $(ALL) \
                     | sed 's|$(TARGET)/||g' |  \
                     sed 's|\.class|.java|g'`


run: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.IconNumberSpectrumViewer

snv: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.SimpleScalarViewer
nsv: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberScalarViewer

jca: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.JChartAdapter
nia: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberImageJChartAdapter
nsa: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberSpectrumJChartAdapter
nst: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberImageJTableAdapter

siv: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.SimpleImageViewer
trend: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.Trend
slv: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.ScalarListViewer	