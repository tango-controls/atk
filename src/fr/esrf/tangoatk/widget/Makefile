
include ../../../../../.make-include

ALL   = $(TARGET)/Splash.class \
	$(TARGET)/ATKFrame.class \
	$(TARGET)/ATKNumberField.class \
	$(TARGET)/ATKDevice.class \
	$(TARGET)/SimpleCommandViewer.class \
	$(TARGET)/AttributeList.class \
	$(TARGET)/CommandList.class \
	$(TARGET)/TangoPanel.class \
	$(TARGET)/AppenderWindow.class \
	$(TARGET)/ErrorHistory.class \
	$(TARGET)/BasicLogWindow.class \
	$(TARGET)/SimpleSpectrumGraphViewer.class \
	$(TARGET)/VoidVoidCommandViewer.class \




all: widget-util \
     properties-components \
     device-components     \
     image-components    \
     attribute-components  \
     command-components    \
     loox-components     \
     image-components   \

widgets: $(ALL)
	$(J)

#	$(TARGET)/GraphScalarViewer.class \f
#	$(TARGET)/VisadTest.class \
#	$(TARGET)/VisadTestSine.class \
# 	$(TARGET)/ATKExecutable.class \
# 	$(TARGET)/ATKAttributeCreator.class \
# 	$(TARGET)/ATKSetter.class \
# 	$(TARGET)/ATKYesOrNo.class \
# 	$(TARGET)/ATKMain.class \
# 	$(TARGET)/AttributeViewer.class \
# 	$(TARGET)/AttributeDisplayUnit.class \
# 	$(TARGET)/AttributeStandardUnit.class \
# 	$(TARGET)/AttributeUnit.class \
# 	$(TARGET)/AttributeLabel.class \
# 	$(TARGET)/AttributeName.class \
# 	$(TARGET)/AttributeInfo.class \
# 	$(TARGET)/AttributeEvent.class \
# 	$(TARGET)/SimpleStringSpectrumGraphViewer.class \
# 	$(TARGET)/SimpleNumberImageGraphViewer.class \
# 	$(TARGET)/test.class \



image-components:
	(cd image && $(MAKE) all)

image-clean:
	(cd image && $(MAKE) class-clean) ; 

widget-util:
	(cd util && $(MAKE) all)

widget-util-clean:
	(cd util && $(MAKE) class-clean)

attribute-components:
	(cd attribute && $(MAKE) all)

command-components:
	(cd command && $(MAKE) all)

device-components:
	(cd device && $(MAKE) all)

loox-components:
	(cd loox && $(MAKE) all)

properties-components:
	(cd properties && $(MAKE) all)

image-components:
	(cd image && $(MAKE) all)

image-clean:
	(cd image && $(MAKE) class-clean);

device-clean:
	(cd device && $(MAKE) class-clean) ;

attribute-clean:
	(cd attribute && $(MAKE) class-clean) ; 
properties-clean:
	(cd properties && $(MAKE) class-clean) ; 

command-clean:
	(cd command && $(MAKE) class-clean) ; 

clean:  class-clean        \
	attribute-clean    \
	properties-clean   \
	widget-util-clean  \
	device-clean       \
        image-clean       \

run:
	$(JAVA) fr.esrf.tangoatk.widget.NumberScalarViewer

trun:
	$(JAVA) fr.esrf.TangoATK.Widget.test

vrun:
	$(JAVA) fr.esrf.TangoATK.Widget.VisadTest

properties-manifest:
	(cd properties && $(MAKE) manifest.mf)

attributes-manifest:
	(cd attribute && $(MAKE) manifest.mf)

device-manifest:
	(cd device && $(MAKE) manifest.mf)

command-manifest:
	(cd command && $(MAKE) manifest.mf)

manifest: properties-manifest \
          attributes-manifest \
          device-manifest     \
          command-manifest    
	mv manifest.mf manifest.mf.tmp; \
           cat manifest.mf.tmp          \
            command/manifest.mf         \
            attribute/manifest.mf       \
            device/manifest.mf          \
            properties/manifest.mf  > manifest.mf ; \
	    rm manifest.mf.tmp

