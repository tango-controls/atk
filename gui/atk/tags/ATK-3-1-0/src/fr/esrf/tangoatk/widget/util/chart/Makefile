include ../../../../../../../.make-include

ALL = $(TARGET)/DataList.class            \
      $(TARGET)/AxisPanel.class           \
      $(TARGET)/JLAxis.class              \
      $(TARGET)/JLChart.class             \
      $(TARGET)/AdvancedJLChart.class             \
      $(TARGET)/JLChartOption.class       \
      $(TARGET)/JLDataView.class          \
      $(TARGET)/JLDataViewOption.class    \
      $(TARGET)/SearchInfo.class          \
      $(TARGET)/CfFileReader.class        \
      $(TARGET)/OFormat.class             \
      $(TARGET)/IJLChartListener.class    \
      $(TARGET)/JLChartEvent.class        \
      $(TARGET)/IJLChartActionListener.class \
      $(TARGET)/JLTable.class \
      $(TARGET)/GraphicsUtils.class \
      $(TARGET)/JLChartActionEvent.class
      
all: chart mathchart

jdoc: icons

chart: $(ALL)
	$(J)
	
mathchart: $(ALL)
	(cd math && $(MAKE) all)
	

