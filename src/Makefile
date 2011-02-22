all: core widget

core:
	(cd fr/esrf/TangoATK/Core/ && $(MAKE) all)

widget:
	(cd fr/esrf/TangoATK/Widget/ && $(MAKE) all)

jdoc:
	(cd fr/esrf/TangoATK/Core/ && $(MAKE) jdoc)

test:
	(cd fr/esrf/TangoATK/Core/t && $(MAKE) test)

clean:
	(cd fr/esrf/TangoATK/Core/ && $(MAKE) clean); \
	(cd fr/esrf/TangoATK/Widget/ && $(MAKE) clean)

manifest:
	(cd fr/esrf/TangoATK/Core/ && $(MAKE) manifest) && \
	(cd fr/esrf/TangoATK/Widget/ && $(MAKE) manifest) 