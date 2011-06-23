
BEANINFO= StringScalarViewerBeanInfo.java            \
          SimpleScalarViewerBeanInfo.java            \
          IconNumberScalarViewerBeanInfo.java        \
          IconNumberSpectrumViewerBeanInfo.java      \
          NumberScalarListViewerBeanInfo.java        \
          ScalarListViewerBeanInfo.java              \
          NumberScalarStateViewerBeanInfo.java       \
          SimpleStringSpectrumViewerBeanInfo.java    \
          NumberSpectrumViewerBeanInfo.java    \
          NumberScalarProgressBarBeanInfo.java    \
          ScalarListSetterBeanInfo.java              \
          StateViewerBeanInfo.java              \
          BooleanScalarCheckBoxViewerBeanInfo.java	\
          StatusViewerBeanInfo.java              \
          NumberScalarWheelEditorBeanInfo.java        \
          NumberScalarViewerBeanInfo.java        \
	  BooleanScalarComboEditorBeanInfo.java \
	  TabbedPaneDevStateScalarViewerBeanInfo.java \
	  StringImageTableViewerBeanInfo.java \
	  MultiNumberSpectrumViewerBeanInfo.java \
	  NumberScalarSetPanelBeanInfo.java \
	  StringScalarSetPanelBeanInfo.java \
	  EnumScalarSetPanelBeanInfo.java \
	  BooleanScalarSetPanelBeanInfo.java \
	  ScalarAttributeSetPanelBeanInfo.java \
	  MultiScalarTableViewerBeanInfo.java \
	  NumberSpectrumItemTrendBeanInfo.java \
	  DevStateSpectrumViewerBeanInfo.java \
          SimpleStateViewerBeanInfo.java	\
	  NumberSpectrumStateBarChartViewerBeanInfo.java    \


include ../../../../../../.make-include


ALL =	$(TARGET)/IAttributeViewer.class            \
	$(TARGET)/SimpleScalarViewer.class          \
	$(TARGET)/AScalarViewer.class               \
	$(TARGET)/ScalarListViewer.class            \
	$(TARGET)/ImageControlAdapter.class         \
	$(TARGET)/NumberImageJTableAdapter.class    \
	$(TARGET)/IconNumberScalarViewer.class      \
	$(TARGET)/IconNumberSpectrumViewer.class    \
	$(TARGET)/StringScalarViewer.class          \
	$(TARGET)/Trend.class                       \
	$(TARGET)/TrendSelectionNode.class          \
	$(TARGET)/TrendRenderer.class               \
	$(TARGET)/NumberScalarWheelEditor.class     \
	$(TARGET)/NumberSpectrumViewer.class        \
	$(TARGET)/NumberSpectrumTableEditor.class   \
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
	$(TARGET)/NumberScalarProgressBar.class  \
	$(TARGET)/NumberScalarComboEditor.class  \
	$(TARGET)/StringScalarComboEditor.class  \
	$(TARGET)/ScalarListSetter.class            \
	$(TARGET)/StateViewer.class            \
	$(TARGET)/BooleanScalarCheckBoxViewer.class \
	$(TARGET)/StatusViewer.class            \
	$(TARGET)/AttributeMultiChart.class            \
	$(TARGET)/AttributeQualityLightViewer.class            \
	$(TARGET)/NumberScalarCheckBoxViewer.class \
	$(TARGET)/NumberScalarSpinner.class \
	$(TARGET)/ScalarInput.class \
	$(TARGET)/SignalScalarLightViewer.class \
	$(TARGET)/DualNumberScalarChart.class \
	$(TARGET)/StringScalarNumberComboEditor.class \
	$(TARGET)/NumberImageTable.class \
	$(TARGET)/NumberScalarViewer.class \
	$(TARGET)/NumberSpectrumTrendViewer.class        \
	$(TARGET)/RawImageViewer.class        \
	$(TARGET)/BooleanScalarComboEditor.class \
	$(TARGET)/BooleanTrend.class                       \
	$(TARGET)/BooleanTrendSelectionNode.class          \
	$(TARGET)/BooleanTrendRenderer.class               \
	$(TARGET)/EnumScalarComboEditor.class  \
	$(TARGET)/SimpleEnumScalarViewer.class  \
	$(TARGET)/NumberSpectrumRecorder.class \
	$(TARGET)/TabbedPaneDevStateScalarViewer.class \
	$(TARGET)/StringImageTableViewer.class \
	$(TARGET)/MultiNumberSpectrumViewer.class \
	$(TARGET)/NumberScalarSetPanel.class \
	$(TARGET)/StringScalarSetPanel.class \
	$(TARGET)/EnumScalarSetPanel.class \
	$(TARGET)/BooleanScalarSetPanel.class \
	$(TARGET)/ScalarAttributeSetPanel.class \
	$(TARGET)/MultiScalarTableViewer.class \
	$(TARGET)/NumberSpectrumItemTrend.class        \
	$(TARGET)/SignalScalarCheckBoxViewer.class \
	$(TARGET)/SignalScalarComboEditor.class \
	$(TARGET)/NumberImageJViewer.class           \
	$(TARGET)/DevStateSpectrumViewer.class       \
	$(TARGET)/SimpleStateViewer.class           \
	$(TARGET)/NumberSpectrumTrend3DViewer.class \
	$(TARGET)/NumberSpectrumStateBarChartViewer.class \
   	$(TARGET)/DigitalNumberScalarViewer.class

#	$(TARGET)/AttributeBarChart.class

all: attributes beaninfo icons

attributes: $(ALL)
	$(J)

#	$(TARGET)/GraphScalarViewer.class \

manifest: manifest-file



run: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.IconNumberSpectrumViewer

ssv: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.SimpleScalarViewer

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
nspect: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberSpectrumViewer	
nspb: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberScalarProgressBar	
ncbe: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberScalarComboEditor	
scbe: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.StringScalarComboEditor	
sls: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.ScalarListSetter	
stv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.StateViewer	
bscbv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.BooleanScalarCheckBoxViewer	
stsv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.StatusViewer	
abc: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.AttributeBarChart	
amc: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.AttributeMultiChart fp/test/1/double_scalar fp/test/1/short_scalar_ro
aqlv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.AttributeQualityLightViewer jlp/test/1/att_un
nscbv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberScalarCheckBoxViewer jlp/test/1/att_six
nsspin: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberScalarSpinner jlp/test/1/att_un
si: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.ScalarInput
sigslv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.SignalScalarLightViewer jlp/test/1/att_six
dnsc: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.DualNumberScalarChart jlp/test/1/att_un jlp/test/1/att_deux
nsv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberScalarViewer
bscombo: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.BooleanScalarComboEditor	
btrend: all
	$(JAVA) fr.esrf.tangoatk.widget.attribute.BooleanTrend
esce: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.EnumScalarComboEditor	
sesv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.SimpleEnumScalarViewer	
nsr:
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberSpectrumRecorder
tpsv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.TabbedPaneDevStateScalarViewer	
sitv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.StringImageTableViewer	
mnsv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.MultiNumberSpectrumViewer	
mstv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.MultiScalarTableViewer	
nstv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberSpectrumTrendViewer	
nsit: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.NumberSpectrumItemTrend	
dssv: attributes
	$(JAVA) fr.esrf.tangoatk.widget.attribute.DevStateSpectrumViewer	
