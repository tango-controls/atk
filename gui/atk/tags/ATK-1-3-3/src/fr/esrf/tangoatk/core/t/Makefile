
include ../../../../../../.make-include

ALL = 	$(TARGET)/CoreTestCase.class \
	$(TARGET)/CoreTestRunner.class \
	$(TARGET)/DeviceFactoryTest.class \
	$(TARGET)/DeviceTest.class \
	$(TARGET)/CommandFactoryTest.class \
	$(TARGET)/AttributeFactoryTest.class \
	$(TARGET)/CommandListTest.class \
	$(TARGET)/ImageAttributeTest.class \
	$(TARGET)/NumberImageTest.class \
	$(TARGET)/NumberScalarTest.class \
	$(TARGET)/SpectrumAttributeTest.class \
	$(TARGET)/NumberSpectrumTest.class \
	$(TARGET)/ScalarAttributeTest.class \
	$(TARGET)/AttributeListTest.class \
	$(TARGET)/BasicErrorHandlerTest.class \
	$(TARGET)/StateCommandTest.class \
	$(TARGET)/CoreTest.class \


all: $(ALL)
	$(J)

clean:
	rm $(TARGET)/*.class 2>/dev/null 

run:
	$(JAVA) fr.esrf.tangoatk.core.t.CoreTest

test: all run

rundevicefactory: all
	$(JAVA) DeviceFactoryTest
runattributelist:
	$(JAVA) fr.esrf.tangoatk.core.t.AttributeListTest

runbasicerrorhandler:
	$(JAVA) BasicErrorHandlerTest

runcommandlist: $(TARGET)/CommandListTest.class
	$(JAVA) fr.esrf.tangoatk.core.t.CommandListTest

runnumberimage: all
	$(JAVA) NumberImageTest

runnumberspectrum: all
	$(JAVA) fr.esrf.tangoatk.core.t.NumberSpectrumTest

runnumberscalar: all
	$(JAVA) fr.esrf.tangoatk.core.t.NumberScalarTest

runscalar: all
	$(JAVA) ScalarAttributeTest

runstate:
	$(JAVA) StateCommandTest

rundevice:
	$(JAVA) fr.esrf.tangoatk.core.t.DeviceTest	