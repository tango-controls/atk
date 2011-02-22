include .make-include

PROG=runapp

CORE-MAJOR=0
CORE-MINOR=1
CORE-BUILD := $(shell cat .core-build)

ifeq "$(CORE-BUILD)" ""
     CORE-BUILD=0
endif

WIDGET-MAJOR=0
WIDGET-MINOR=0
WIDGET-BUILD := $(shell cat .widget-build)

ifeq "$(WIDGET-BUILD)" ""
     WIDGET-BUILD=0
endif

CORE-VERSION=$(CORE-MAJOR).$(CORE-MINOR).$(CORE-BUILD)

WIDGET-VERSION=$(WIDGET-MAJOR).$(WIDGET-MINOR).$(WIDGET-BUILD)

all: util core widget 


core:
	(cd src/fr/esrf/tangoatk/core/ && $(MAKE) all) &&    \
	 expr $(CORE-BUILD) + 1 > .core-build

widget:
	(cd src/fr/esrf/tangoatk/widget/ && $(MAKE) all) &&   \
        expr $(WIDGET-BUILD) + 1 > .widget-build

util:
	(cd src/fr/esrf/tangoatk/util/ && $(MAKE) all)

test: core-test widget-test

core-test:
	(cd src/fr/esrf/tangoatk/core/t && $(MAKE) test)

widget-test:



clean:
	(cd src/fr/esrf/tangoatk/core/ && $(MAKE) clean); \
	(cd src/fr/esrf/tangoatk/widget/ && $(MAKE) clean)


install: 
	install -g dserver lib/ATKCore-$(CORE-VERSION).jar              \
                           lib/ATKWidget-$(WIDGET-VERSION).jar          \
                           lib/jscrollpane.jar                          \
                           /segfs/tango/lib/java/                       \
        && ln -sf /segfs/tango/lib/java/ATKCore-$(CORE-VERSION).jar     \
                  /segfs/tango/lib/java/ATKCore.jar                     \
        && ln -sf /segfs/tango/lib/java/ATKWidget-$(WIDGET-VERSION).jar \
                  /segfs/tango/lib/java/ATKWidget.jar


install-doc: 
	cp -va  doc/* /segfs/tango/doc/www/tango/tango_doc/atk_doc/ 


# 
checkin:
	cvs ci

jar: core-jar  widget-jar



core-jar: core-manifest
	@(cd lib &&                             \
         if [ -e $(COREMANIFEST) ]; then       \
            $(JAR) cmf $(COREMANIFEST) ATKCore-$(CORE-VERSION).jar fr/esrf/tangoatk/core fr/esrf/tangoatk/util; \
         else                                           \
            $(JAR) cf ATKCore-$(CORE-VERSION).jar fr/esrf/tangoatk/core fr/esrf/tangoatk/util;                   \
         fi)
	$(call verifyjar,/tmp/core/jar,ATKCore-$(CORE-VERSION))

widget-jar: widget-manifest
	(cd lib && $(JAR) cmf $(WIDGETMANIFEST) ATKWidget-$(WIDGET-VERSION).jar fr/esrf/tangoatk/widget); \
	$(call verifyjar,/tmp/widget/jar,ATKWidget-$(WIDGET-VERSION))

verify-widget-jar: widget-jar



desktop:
	@(cd src/com/tomtessier/scrollabledesktop && $(MAKE) all)

desktop-jar: desktop
	@(cd lib &&                             \
	$(JAR) cf  jscrollpane.jar com/tomtessier/scrollabledesktop;\
        )

tag:
	cvs tag -c `cat .version` src 

jdoc:
	(cd src && $(MAKE) jdoc)

etags:
	find src  -name "*.java" | xargs etags

manifest: core-manifest widget-manifest

core-manifest:
	echo "Manifest-Version: 1.0" > $(COREMANIFEST) ;  \
	echo "" >> $(COREMANIFEST) ;                       \
	echo "" >> $(COREMANIFEST) ;                       \
	(cd src/fr/esrf/tangoatk/core/ && $(MAKE) manifest.mf) ; \

widget-manifest:
	echo "Manifest-Version: 1.0" > $(WIDGETMANIFEST) ;    \
	echo "" >> $(WIDGETMANIFEST) ;                        \
	echo "" >> $(WIDGETMANIFEST) ;                        \
	(cd src/fr/esrf/tangoatk/widget/ && $(MAKE) manifest); \




beanbox:
	$(JAVA) sun.beanbox.BeanBoxFrame

build:
	(cd /tmp; cvs -d /segfs/tango/cvsroot co jclient_framework;  \
         (cd jclient_framework/ATK; export JAVA_HOME=$(JAVA_HOME) && \
           configure && make all && make test && make jar &&         \
           rm -fr jclient_framework))

ci:
	cvs ci 


