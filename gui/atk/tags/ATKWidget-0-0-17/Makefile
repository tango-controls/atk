include .make-include
BUILDDIR=/tmp
PROG=runapp
CVSROOT=/segfs/tango/cvsroot
CORE-MAJOR:=0# dont forget to change .core-build when minor and major
CORE-MINOR:=1# versions are changed
CORE-BUILD := $(shell cat .core-build)

ifeq "$(CORE-BUILD)" ""
     CORE-BUILD:=0
endif

WIDGET-MAJOR:=0# dont forget to change .core-build when minor and major
WIDGET-MINOR:=0# versions are changed
WIDGET-BUILD:= $(shell cat .widget-build)

ifeq "$(WIDGET-BUILD)" ""
     WIDGET-BUILD:=0
endif

CORE-VERSION:=$(CORE-MAJOR).$(CORE-MINOR).$(CORE-BUILD)

WIDGET-VERSION:=$(WIDGET-MAJOR).$(WIDGET-MINOR).$(WIDGET-BUILD)

CORE-RELEASE:=ATKCore-$(subst .,-,$(CORE-VERSION))
WIDGET-RELEASE:=ATKWidget-$(subst .,-,$(WIDGET-VERSION))

all: util core widget 

core-release:
	(cvs tag $(CORE-RELEASE) configure                     \
                                 .make-include.in              \
                                 .core-build                   \
                                 Makefile &&                   \
	 (cd src/fr/esrf/tangoatk/ &&                          \
         cvs tag $(CORE-RELEASE) core)      &&                 \
         (cd src/fr/esrf/tangoatk/ &&                          \
         cvs tag $(CORE-RELEASE) util))        

core:
	(cd src/fr/esrf/tangoatk/core/ && $(MAKE) all) &&      \
	 expr $(CORE-BUILD) + 1 > .core-build

widget-release:
	(cvs tag $(WIDGET-RELEASE) .widget-build               \
                                   .make-include.in            \
                                   configure                   \
                                   Makefile &&                 \
	 cd src/fr/esrf/tangoatk/           &&                 \
         cvs tag -R $(WIDGET-RELEASE) widget) 


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
	&& (cd /segfs/tango/lib/java/                                   \
            && ln -sf ATKCore-$(CORE-VERSION).jar ATKCore.jar           \
            && ln -sf ATKWidget-$(WIDGET-VERSION).jar ATKWidget.jar)    \


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

build: core-build widget-build

core-build:
	(cd $(BUILDDIR);                                                    \
         cvs -d $(CVSROOT) co -r  $(CORE-RELEASE) jclient_framework;        \
	(cd jclient_framework/ATK; export JAVA_HOME=$(JAVA_HOME) &&         \
         ./configure && make core  && make core-test && make util           \
         && make core-jar ))

widget-build:
	(cd $(BUILDDIR);	                                            \
         cvs -d $(CVSROOT) co -r  $(WIDGET-RELEASE) jclient_framework;      \
	(cd jclient_framework/ATK && export JAVA_HOME=$(JAVA_HOME) &&       \
        ./configure && make widget && make widget-test &&                   \
        make widget-jar))

ci:
	cvs ci 


