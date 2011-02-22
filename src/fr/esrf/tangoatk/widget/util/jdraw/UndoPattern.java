package fr.esrf.tangoatk.widget.util.jdraw;

import java.util.Vector;
import java.awt.*;

class UndoPattern {

  final static int _JDEllipse        = 1;
  final static int _JDGroup          = 2;
  final static int _JDLabel          = 3;
  final static int _JDLine           = 4;
  final static int _JDPolyline       = 5;
  final static int _JDRectangle      = 6;
  final static int _JDRoundRectangle = 7;
  final static int _JDSpline         = 8;
  final static int _JDImage          = 9;

  // Class
  int JDclass;

  // JDObject property
  double[] summit;
  double   xOrigin;
  double   yOrigin;
  int rgbBackground;
  int rgbForeground;
  int fillStyle;
  int lineWidth;
  int lineStyle;
  boolean isShadowed;
  boolean invertShadow;
  String name;
  int shadowThickness;
  boolean visible;
  boolean selected;
  int minValue;
  int maxValue;
  int initValue;
  boolean userValue;
  int valueChangeMode;
  int valueListenerMode;

  // PolyLine
  int step;
  boolean isClosed;

  // line
  int arrowMode;
  int arrowWidth;

  // Text
  String text;
  String fName;
  int fStyle;
  int fSize;
  int hAlignment;
  int vAlignment;
  int textOrientation;

  // Image
  String fileName;

  //RoundedRect
  int cornerWidth;

  // Ellipse
  int arcType;
  int angleStart;
  int angleExtent;

  // Group children
  Vector gChildren;

  // mapper backup
  JDValueProgram backgroundMapper=null;
  JDValueProgram foregroundMapper=null;
  JDValueProgram visibilityMapper=null;
  JDValueProgram invertShadowMapper=null;
  JDValueProgram hTranslationMapper=null;
  JDValueProgram vTranslationMapper=null;

  // Gradient backup
  float   gradientX1;
  float   gradientX2;
  float   gradientY1;
  float   gradientY2;
  Color   gradientC1;
  Color   gradientC2;
  boolean gradientCyclic;

  // Extensions
  String[] extsV;         //  extensions value
  String[] extsN;         //  extensions name (backup reference only)

  public UndoPattern(int jdClass) {
    JDclass = jdClass;
  }

}
