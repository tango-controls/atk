/*
 *  Copyright (C) :     2002,2003,2004,2005,2006,2007,2008,2009
 *                      European Synchrotron Radiation Facility
 *                      BP 220, Grenoble 38043
 *                      FRANCE
 *
 *  This file is part of Tango.
 *
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.esrf.tangoatk.widget.util.jgl3dchart;

import javax.media.opengl.GLJPanel;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.event.*;

class JGL3DView extends GLJPanel implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener {

  final static GLU glu = new GLU();

  final static int ZOOM_ZY = 1;
  final static int ZOOM_ZX = 2;
  final static int ZOOM_YX = 3;

  // Camera stuff
  private double angleOx=0.5;
  private double angleOy=0.785f;
  private double camDist;
  private boolean isDraggingRot;
  private boolean isDraggingZoom;
  private int mX;
  private int mY;
  private int mX2;
  private int mY2;
  private boolean autoScaleCameraRequest;

  // Y colormap
  private int[]             gColormap;

  // Axis
  private JGL3DAxis xAxis;
  private JGL3DAxis yAxis;
  private JGL3DAxis zAxis;

  private double colMin=0;     // Data min (for colormap)
  private double colMax=100;   // Data max (for colormap)
  private double Scmin=0.0;    // Data min (for colormap scaling)
  private double Scmax=100.0;  // Data min (for colormap scaling)

  private boolean yDataAutoScale;  // Autoscale colormap

  // Bounding box
  private double x0;
  private double y0;
  private double x1;
  private double y1;
  private double zNear=-1e100;
  private double zFar=1e100;

  //Data
  private double[][] data;
  private boolean updateDataRequest;
  private int dataList;

  // Private data
  private JGL3DChart parent;
  private int lastWidth=0;
  private int lastHeight=0;
  private int zoomAllowedMode =0;
  private boolean zoomRequest;
  private int zoomMode=0;

  /**
   * Construct a JOGL view
   * @param parent parent chart
   */
  JGL3DView(JGL3DChart parent) {

    this.parent = parent;

    addGLEventListener(this);
    addMouseListener(this);
    addMouseMotionListener(this);
    addMouseWheelListener(this);
    setPreferredSize(new Dimension(640,480));

    xAxis = new JGL3DAxis(this,JGL3DAxis.XAXIS);
    yAxis = new JGL3DAxis(this,JGL3DAxis.YAXIS);
    zAxis = new JGL3DAxis(this,JGL3DAxis.ZAXIS);
    xAxis.setName("Xaxis");
    yAxis.setName("Yaxis");
    zAxis.setName("Zaxis");
    xAxis.setLabelColor(Color.RED);
    yAxis.setLabelColor(new Color(0,128,0));
    zAxis.setLabelColor(Color.BLUE);

    updateAxisPosition();
    autoScaleCameraRequest();
    updateDataRequest = false;
    zoomRequest = false;
    dataList = 0;
    yDataAutoScale = true;

  }

  /**
   * Sets the colormap (65536 color)
   * @param colormap Colormap of 64K colors
   */
  void setColorMap(int[] colormap) {
    gColormap = colormap;
  }

  /**
   * Auto scale the chart to fit the screen
   */
  void autoScaleCameraRequest() {

    autoScaleCameraRequest = true;

  }

  // Axis management

  JGL3DAxis getXAxis() {
    return xAxis;
  }

  JGL3DAxis getYAxis() {
    return yAxis;
  }

  JGL3DAxis getZAxis() {
    return zAxis;
  }

  private void transformBoxSide(GL gl,double x,double y,double z) {

    double[] pos = new double[3];
    Utils.project3D(gl,x,y,z,pos);
    if(pos[0]<x0)    x0 = pos[0];
    if(pos[0]>x1)    x1 = pos[0];
    if(pos[1]<y0)    y0 = pos[1];
    if(pos[1]>y1)    y1 = pos[1];

  }

  private void computeBoundingBoxSide(GL gl) {

    x0=1e100;
    y0=1e100;
    x1=-1e100;
    y1=-1e100;

    double xMin = xAxis.getMin();
    double xMax = xAxis.getMax();
    double yMin = yAxis.getMin();
    double yMax = yAxis.getMax();
    double zMin = zAxis.getMin();
    double zMax = zAxis.getMax();

    transformBoxSide(gl,xMax,yMax,zMax);
    transformBoxSide(gl,xMin,yMax,zMax);
    transformBoxSide(gl,xMin,yMin,zMax);
    transformBoxSide(gl,xMax,yMin,zMax);
    transformBoxSide(gl,xMax,yMax,zMin);
    transformBoxSide(gl,xMin,yMax,zMin);
    transformBoxSide(gl,xMin,yMin,zMin);
    transformBoxSide(gl,xMax,yMin,zMin);

  }

  private void transformBoxDepth(GL gl,double x,double y,double z) {

    double[] pos = new double[3];
    Utils.project3D(gl,x,y,z,pos);
    if(pos[2]<zNear) zNear = pos[2];
    if(pos[2]>zFar)  zFar = pos[2];

  }

  private void computeBoundingBoxDepth(GL gl) {

    zNear = 1e100;
    zFar  = -1e100;

    double xMin = xAxis.getMin();
    double xMax = xAxis.getMax();
    double yMin = yAxis.getMin();
    double yMax = yAxis.getMax();
    double zMin = zAxis.getMin();
    double zMax = zAxis.getMax();

    transformBoxDepth(gl,xMax,yMax,zMax);
    transformBoxDepth(gl,xMin,yMax,zMax);
    transformBoxDepth(gl,xMin,yMin,zMax);
    transformBoxDepth(gl,xMax,yMin,zMax);
    transformBoxDepth(gl,xMax,yMax,zMin);
    transformBoxDepth(gl,xMin,yMax,zMin);
    transformBoxDepth(gl,xMin,yMin,zMin);
    transformBoxDepth(gl,xMax,yMin,zMin);

  }

  private void computeOrthographicProj(GL gl) {

    updateModelMatrix(gl,camDist);
    computeBoundingBoxDepth(gl);

    Dimension d = getSize();
    gl.glMatrixMode(GL.GL_PROJECTION);
    gl.glLoadIdentity();
    gl.glOrtho(d.width/2,-d.width/2,-d.height/2,d.height/2,zNear*1.1,zFar*1.1);

  }

  private void autoScaleCamera(GL gl) {

    updateModelMatrix(gl,1.0);
    computeBoundingBoxSide(gl);

    // Scale camDist
    Dimension d = getSize();
    double camDistx = (double)d.width*0.8  / (x1-x0);
    double camDisty = (double)d.height*0.8 / (y1-y0);
    if( camDistx>camDisty ) camDist = camDisty;
    else                    camDist = camDistx;

    updateAxisPosition();
    autoScaleCameraRequest = false;
  }

  private void updateAxisPosition() {

    double xMin = xAxis.getMin();
    double xMax = xAxis.getMax();
    double yMin = yAxis.getMin();
    double yMax = yAxis.getMax();
    double zMin = zAxis.getMin();
    double zMax = zAxis.getMax();

     // X Axis
     if(angleOy<Math.PI/2)
       xAxis.setPosition(new VERTEX3D(xMin,yMin,zMin),
                         new VERTEX3D(xMax,yMin,zMin));
     else
       xAxis.setPosition(new VERTEX3D(xMin,yMin,zMax),
                         new VERTEX3D(xMax,yMin,zMax));

    if( angleOx>=0.5 ) {
      if(angleOy<Math.PI/2)
        xAxis.setNormal(new VERTEX3D(0.0,0.0,-10.0/camDist));
      else
        xAxis.setNormal(new VERTEX3D(0.0,0.0,10.0/camDist));
    } else {
      xAxis.setNormal(new VERTEX3D(0.0,-10.0/camDist,0.0));
    }


     // Y Axis
     if(angleOy<Math.PI/2)
       yAxis.setPosition(new VERTEX3D(xMax,yMin,zMin),
                         new VERTEX3D(xMax,yMax,zMin));
     else
       yAxis.setPosition(new VERTEX3D(xMax,yMin,zMax),
                         new VERTEX3D(xMax,yMax,zMax));


    if(angleOy<Math.PI/2) {
      if( angleOy<Math.PI/4 )
        yAxis.setNormal(new VERTEX3D(10.0/camDist,0.0,0.0));
      else
        yAxis.setNormal(new VERTEX3D(0.0,0.0,-10.0/camDist));
    } else {
      if( angleOy>3.0*Math.PI/4 )
        yAxis.setNormal(new VERTEX3D(10.0/camDist,0.0,0.0));
      else
        yAxis.setNormal(new VERTEX3D(0.0,0.0,10.0/camDist));
    }

    // Z Axis
    zAxis.setPosition(new VERTEX3D(xMin,yMin,zMin),
                      new VERTEX3D(xMin,yMin,zMax));

    if( angleOx>=0.5 )
      zAxis.setNormal(new VERTEX3D(-10.0/camDist,0.0,0.0));
    else
      zAxis.setNormal(new VERTEX3D(0.0,-10.0/camDist,0.0));

  }

  // --------------------------------------------------------------------------
  // Data
  // --------------------------------------------------------------------------

  /**
   * Sets the data (The value is mapped to the y axis)
   * The fisrt coordinates data[x][] is mapped to the x axis
   * The second coordinates data[][y] is mapped the the y axis
   * All y line must have the same lenght
   * @param data Data to be displayed
   */
  void setData(double[][] data) {

    this.data = data;
    computeScale();

  }

  void computeScale() {

    if(data==null) {
      xAxis.setMin(xAxis.getMinimum());
      xAxis.setMax(xAxis.getMaximum());
      yAxis.setMin(yAxis.getMinimum());
      yAxis.setMax(yAxis.getMaximum());
      zAxis.setMin(zAxis.getMinimum());
      zAxis.setMax(zAxis.getMaximum());
      repaint();
      return;
    }
    if (data.length > 2 && data[0].length > 2) {

      // Get gain, offset
      double xGain = xAxis.getGainTransform();
      double xOff  = xAxis.getOffsetTransform();
      double yGain = yAxis.getGainTransform();
      double yOff  = yAxis.getOffsetTransform();
      double zGain = zAxis.getGainTransform();
      double zOff  = zAxis.getOffsetTransform();

      // Compute min , max and autoscaling
      boolean yRangeOK = false;
      Scmin = Double.MAX_VALUE;
      Scmax = -Double.MAX_VALUE;
      if(!yDataAutoScale) {
        Scmax = colMax;
        Scmin = colMin;
      }

      if (yDataAutoScale) {
        for (int x = 0; x < data.length - 1; x++) {
          for (int z = 0; z < data[x].length - 1; z++) {
            double v = yGain*data[x][z]+yOff;
            if (!Double.isNaN(v)) {
              if (v < Scmin) Scmin = v;
              if (v > Scmax) Scmax = v;
              yRangeOK = true;
            }
          }
        }
      }

      if( yRangeOK ) {
        if ((Scmax - Scmin) < 1e-100) {
          Scmax += 0.999;
          Scmin -= 0.999;
        }
      } else {
        // Only Nan or invalid data
        Scmin = colMin;
        Scmax = colMax;
      }

      switch(zoomMode) {

        case ZOOM_ZY:
          if (xAxis.isAutoScale()) {
            if( xGain<0.0 ) {
              xAxis.setMin(xGain*(double)data.length+xOff);
              xAxis.setMax(xOff);
            } else {
              xAxis.setMin(xOff);
              xAxis.setMax(xGain*(double)data.length+xOff);
            }
          } else {
            xAxis.setMin(xAxis.getMinimum());
            xAxis.setMax(xAxis.getMaximum());
          }
          break;

        case ZOOM_ZX:
          if (yAxis.isAutoScale()) {
            yAxis.setMin(Scmin);
            yAxis.setMax(Scmax);
          } else {
            yAxis.setMin(yAxis.getMinimum());
            yAxis.setMax(yAxis.getMaximum());
          }
          break;

        case ZOOM_YX:
          if (zAxis.isAutoScale()) {
            if(zGain<0.0) {
              zAxis.setMin(zGain*(double)data[0].length+zOff);
              zAxis.setMax(zOff);
            } else {
              zAxis.setMin(zOff);
              zAxis.setMax(zGain*(double)data[0].length+zOff);
            }
          } else {
            zAxis.setMin(zAxis.getMinimum());
            zAxis.setMax(zAxis.getMaximum());
          }
          break;

        default:

          if (xAxis.isAutoScale()) {
            if( xGain<0.0 ) {
              xAxis.setMin(xGain*(double)data.length+xOff);
              xAxis.setMax(xOff);
            } else {
              xAxis.setMin(xOff);
              xAxis.setMax(xGain*(double)data.length+xOff);
            }
          } else {
            xAxis.setMin(xAxis.getMinimum());
            xAxis.setMax(xAxis.getMaximum());
          }

          if (yAxis.isAutoScale()) {
            yAxis.setMin(Scmin);
            yAxis.setMax(Scmax);
          } else {
            yAxis.setMin(yAxis.getMinimum());
            yAxis.setMax(yAxis.getMaximum());
          }

          if (zAxis.isAutoScale()) {
            if(zGain<0.0) {
              zAxis.setMin(zGain*(double)data[0].length+zOff);
              zAxis.setMax(zOff);
            } else {
              zAxis.setMin(zOff);
              zAxis.setMax(zGain*(double)data[0].length+zOff);
            }
          } else {
            zAxis.setMin(zAxis.getMinimum());
            zAxis.setMax(zAxis.getMaximum());
          }

      }

      updateAxisPosition();

      // Update gradient viewer
      parent.gradientViewer.getAxis().setMinimum(Scmin);
      parent.gradientViewer.getAxis().setMaximum(Scmax);
      parent.revalidate();
      parent.repaint();

    }

    updateDataRequest = true;

  }

  void zoomBack() {

    if( zoomMode!=0 ) {
      zoomMode = 0;
      computeScale();
      repaint();
    }

  }

  private void getColor(double data,double min,double max,float[] rgb) {

    double c = ((data - min) / (max - min)) * 65536.0;
    if (c < 0.0) c = 0.0;
    if (c > 65535.0) c = 65535.0;
    rgb[0] = (float)( (gColormap[(int)c] & 0xFF0000)>>16 )/256.0f;
    rgb[1] = (float)( (gColormap[(int)c] & 0x00FF00)>>8  )/256.0f;
    rgb[2] = (float)( (gColormap[(int)c] & 0x0000FF)     )/256.0f;

  }

  private void buildDataList(GL gl) {

    if(data==null)
      return;

    if(dataList!=0) gl.glDeleteLists(dataList,1);
    dataList=0;

    if (data.length > 2 && data[0].length > 2) {

      // Get gain, offset
      double xGain = xAxis.getGainTransform();
      double xOff  = xAxis.getOffsetTransform();
      double yGain = yAxis.getGainTransform();
      double yOff  = yAxis.getOffsetTransform();
      double zGain = zAxis.getGainTransform();
      double zOff  = zAxis.getOffsetTransform();

      dataList = gl.glGenLists(1);
      gl.glNewList(dataList, GL.GL_COMPILE);

      gl.glBegin(GL.GL_TRIANGLES);
      float rgb[] = new float[3];
      for (int x = 0; x < data.length - 1; x++) {
        for (int z = 0; z < data[x].length - 1; z++) {

          if (xGain*(double)x+xOff >= xAxis.getMin() && xGain*(x+1.0)+xOff <= xAxis.getMax()
           && zGain*(double)z+zOff >= zAxis.getMin() && zGain*(z+1.0)+zOff <= zAxis.getMax() ) {

            if (!Double.isNaN(data[x][z]) && !Double.isNaN(data[x+1][z]) &&
                !Double.isNaN(data[x][z+1]) && !Double.isNaN(data[x+1][z+1]) )
            {

              double x00 = xGain*(double)x+xOff;
              double y00 = yGain*data[x][z]+yOff;
              double z00 = zGain*(double)z+zOff;

              double x01 = x00;
              double y01 = yGain*data[x][z+1]+yOff;
              double z01 = zGain*(z+1.0)+zOff;

              double x10 = xGain*(x+1.0)+xOff;
              double y10 = yGain*data[x+1][z]+yOff;
              double z10 = z00;

              double x11 = x10;
              double y11 = yGain*data[x+1][z+1]+yOff;
              double z11 = z01;


              getColor(y00, Scmin, Scmax, rgb);
              gl.glColor3f(rgb[0], rgb[1], rgb[2]);
              gl.glVertex3d(x00, y00, z00);
              getColor(y01, Scmin, Scmax, rgb);
              gl.glColor3f(rgb[0], rgb[1], rgb[2]);
              gl.glVertex3d(x01,y01, z01);
              getColor(y10, Scmin, Scmax, rgb);
              gl.glColor3f(rgb[0], rgb[1], rgb[2]);
              gl.glVertex3d(x10, y10, z10);

              getColor(y01, Scmin, Scmax, rgb);
              gl.glColor3f(rgb[0], rgb[1], rgb[2]);
              gl.glVertex3d(x01, y01, z01);
              getColor(y11, Scmin, Scmax, rgb);
              gl.glColor3f(rgb[0], rgb[1], rgb[2]);
              gl.glVertex3d(x11, y11, z11);
              getColor(y10, Scmin, Scmax, rgb);
              gl.glColor3f(rgb[0], rgb[1], rgb[2]);
              gl.glVertex3d(x10, y10, z10);
            }
          }

        }
      }
      gl.glEnd();
      gl.glEndList();

    }

  }

  private void manageZoom(GL gl) {

    double[] _coords  = new double[3];
    double[] _coords2 = new double[3];

    switch(zoomAllowedMode) {

      case ZOOM_ZY:

        Utils.unproject(gl,mX,mY,0.0,_coords);
        Utils.unproject(gl,mX2,mY2,0.0,_coords2);

        if( _coords[1]>_coords2[1] ) {
          yAxis.setMax(_coords[1]);
          yAxis.setMin(_coords2[1]);
        } else {
          yAxis.setMax(_coords2[1]);
          yAxis.setMin(_coords[1]);
        }

        if( _coords[2]>_coords2[2] ) {
          zAxis.setMax(_coords[2]);
          zAxis.setMin(_coords2[2]);
        } else {
          zAxis.setMax(_coords2[2]);
          zAxis.setMin(_coords[2]);
        }
        updateAxisPosition();
        buildDataList(gl);
        repaint();
        zoomMode = zoomAllowedMode;
        break;

      case ZOOM_ZX:

        Utils.unproject(gl,mX,mY,0.0,_coords);
        Utils.unproject(gl,mX2,mY2,0.0,_coords2);

        if( _coords[0]>_coords2[0] ) {
          xAxis.setMax(_coords[0]);
          xAxis.setMin(_coords2[0]);
        } else {
          xAxis.setMax(_coords2[0]);
          xAxis.setMin(_coords[0]);
        }

        if( _coords[2]>_coords2[2] ) {
          zAxis.setMax(_coords[2]);
          zAxis.setMin(_coords2[2]);
        } else {
          zAxis.setMax(_coords2[2]);
          zAxis.setMin(_coords[2]);
        }
        updateAxisPosition();
        buildDataList(gl);
        repaint();
        zoomMode = zoomAllowedMode;
        break;

      case ZOOM_YX:

        Utils.unproject(gl,mX,mY,0.0,_coords);
        Utils.unproject(gl,mX2,mY2,0.0,_coords2);

        if( _coords[0]>_coords2[0] ) {
          xAxis.setMax(_coords[0]);
          xAxis.setMin(_coords2[0]);
        } else {
          xAxis.setMax(_coords2[0]);
          xAxis.setMin(_coords[0]);
        }

        if( _coords[1]>_coords2[1] ) {
          yAxis.setMax(_coords[1]);
          yAxis.setMin(_coords2[1]);
        } else {
          yAxis.setMax(_coords2[1]);
          yAxis.setMin(_coords[1]);
        }
        updateAxisPosition();
        buildDataList(gl);
        repaint();
        zoomMode = zoomAllowedMode;
        break;

    }

  }

  // --------------------------------------------------------------------------
  // JOGL stuff
  // --------------------------------------------------------------------------

  public void init(GLAutoDrawable glDrawable) {

    GL gl = glDrawable.getGL();
    gl.glClearColor(0.9f, 0.9f, 0.9f, 0.0f);
    gl.glClearDepth(0.0f);
    gl.glDepthFunc(GL.GL_GEQUAL);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDisable(GL.GL_CULL_FACE);

    // Light
    gl.glDisable( GL.GL_LIGHTING );
    gl.glDisable(GL.GL_TEXTURE_2D);

    /*
    private int[] tex1 = new int[1];
    gl.glGenTextures(1,tex1,0);
    gl.glBindTexture(GL.GL_TEXTURE_2D,tex1[0]);
    ByteBuffer buff = Utils.convertImageToTexture(yAxisImage,true);
    gl.glTexImage2D(
          GL.GL_TEXTURE_2D,    // Type
          0,                   // No Mipmap
          GL.GL_RGBA,          // Format RGBA
          256,                 // Width
          256,                 // Height
          0,                   // Border
          GL.GL_RGBA,          // Format RGBA
          GL.GL_UNSIGNED_BYTE, // 8 Bit/pixel
          buff                 // Data
        );
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
    */

  }

  public void display(GLAutoDrawable glDrawable) {

    GL gl = glDrawable.getGL();

    double xMin = xAxis.getMin();
    double xMax = xAxis.getMax();
    double yMin = yAxis.getMin();
    double yMax = yAxis.getMax();
    double zMin = zAxis.getMin();
    double zMax = zAxis.getMax();

    if(autoScaleCameraRequest)
      autoScaleCamera(gl);
    computeOrthographicProj(gl);
    updateModelMatrix(gl,camDist);

    gl.glClear(GL.GL_COLOR_BUFFER_BIT);
    gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
    gl.glDisable(GL.GL_DEPTH_TEST);

    gl.glBegin(GL.GL_QUADS);

    //Z,Y plane
    gl.glColor3f(0.8f,0.8f,0.8f);
    gl.glVertex3d(xMax , yMin , zMin);
    gl.glVertex3d(xMax, yMin , zMax);
    gl.glVertex3d(xMax, yMax, zMax);
    gl.glVertex3d(xMax , yMax, zMin);

    //Z,X plane
    gl.glColor3f(0.91f,0.91f,0.91f);
    gl.glVertex3d(xMin, yMin , zMin);
    gl.glVertex3d(xMin, yMin , zMax);
    gl.glVertex3d(xMax, yMin, zMax);
    gl.glVertex3d(xMax, yMin, zMin);

    //Y,X plane
    gl.glColor3f(0.85f,0.85f,0.85f);
    if(angleOy<Math.PI/2) {
      gl.glVertex3d(xMin, yMin , zMax);
      gl.glVertex3d(xMin, yMax , zMax);
      gl.glVertex3d(xMax, yMax, zMax);
      gl.glVertex3d(xMax, yMin, zMax);
    } else {
      gl.glVertex3d(xMin, yMin , zMin);
      gl.glVertex3d(xMin, yMax , zMin);
      gl.glVertex3d(xMax, yMax, zMin);
      gl.glVertex3d(xMax, yMin, zMin);
    }

    gl.glEnd();

    // Axis
    xAxis.measureAxis(gl);
    xAxis.paintAxis(gl);
    yAxis.measureAxis(gl);
    yAxis.paintAxis(gl);
    zAxis.measureAxis(gl);
    zAxis.paintAxis(gl);

    // Paint grid
    short pattern = 0x0F0F;
    gl.glLineStipple(1,pattern);
    gl.glEnable(GL.GL_LINE_STIPPLE);
    gl.glColor3f(0.5f,0.5f,0.5f);
    gl.glBegin(GL.GL_LINES);

    //Z,Y plane
    for(int i=0;i<yAxis.labelInfo.size();i++) {
      LabelInfo li = (LabelInfo)yAxis.labelInfo.get(i);
      gl.glVertex3d(xMax, li.p1.y, zMin);
      gl.glVertex3d(xMax, li.p1.y, zMax);
    }
    for(int i=0;i<zAxis.labelInfo.size();i++) {
      LabelInfo li = (LabelInfo)zAxis.labelInfo.get(i);
      gl.glVertex3d(xMax, yMin, li.p1.z);
      gl.glVertex3d(xMax, yMax, li.p1.z);
    }

    //Z,X plane
    for(int i=0;i<xAxis.labelInfo.size();i++) {
      LabelInfo li = (LabelInfo)xAxis.labelInfo.get(i);
      gl.glVertex3d(li.p1.x, yMin, zMin);
      gl.glVertex3d(li.p1.x, yMin, zMax);
    }
    for(int i=0;i<zAxis.labelInfo.size();i++) {
      LabelInfo li = (LabelInfo)zAxis.labelInfo.get(i);
      gl.glVertex3d(xMin, yMin, li.p1.z);
      gl.glVertex3d(xMax, yMin, li.p1.z);
    }

    //Y,X plane
    if (angleOy < Math.PI / 2) {

      for (int i = 0; i < yAxis.labelInfo.size(); i++) {
        LabelInfo li = (LabelInfo) yAxis.labelInfo.get(i);
        gl.glVertex3d(xMin, li.p1.y, zMax);
        gl.glVertex3d(xMax, li.p1.y, zMax);
      }
      for (int i = 0; i < xAxis.labelInfo.size(); i++) {
        LabelInfo li = (LabelInfo) xAxis.labelInfo.get(i);
        gl.glVertex3d(li.p1.x, yMin, zMax);
        gl.glVertex3d(li.p1.x, yMax, zMax);
      }

    } else {

      for (int i = 0; i < yAxis.labelInfo.size(); i++) {
        LabelInfo li = (LabelInfo) yAxis.labelInfo.get(i);
        gl.glVertex3d(xMin, li.p1.y, zMin);
        gl.glVertex3d(xMax, li.p1.y, zMin);
      }
      for (int i = 0; i < xAxis.labelInfo.size(); i++) {
        LabelInfo li = (LabelInfo) xAxis.labelInfo.get(i);
        gl.glVertex3d(li.p1.x, yMin, zMin);
        gl.glVertex3d(li.p1.x, yMax, zMin);
      }

    }

    gl.glEnd();

    if (updateDataRequest) {
      buildDataList(gl);
      updateDataRequest = false;
    }

    if(dataList>0) {
      gl.glEnable(GL.GL_DEPTH_TEST);
      gl.glCallList(dataList);
    }

    if( zoomRequest ) {
      manageZoom(gl);
      autoScaleCameraRequest = true;
      zoomRequest = false;
    }
  }

  public void reshape(GLAutoDrawable glDrawable, int x, int y, int width, int height) {

    if( lastWidth!=width || lastHeight!=height ) {
      GL gl = glDrawable.getGL();
      computeOrthographicProj(gl);
      updateDataRequest = true;
      lastWidth=width;
      lastHeight=height;
    }

  }

  public void displayChanged(GLAutoDrawable glDrawable,boolean modeChanged, boolean deviceChanged) {
  }

  // --------------------------------------------------------------------------
  // Mouse listener
  // --------------------------------------------------------------------------

  public void mouseWheelMoved(MouseWheelEvent e) {
    if (e.getWheelRotation() < 0) {
      camDist *= 0.95;
    } else {
      camDist *= 1.05;
    }
    updateAxisPosition();
    display();
  }

  public void mouseClicked(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON1) {
      if (e.isControlDown())
        isDraggingZoom = zoomAllowedMode != 0;
      else
        isDraggingRot = true;
      mX = e.getX();
      mY = e.getY();
    }
  }
  public void mouseReleased(MouseEvent e) {
    isDraggingRot=false;
    if(isDraggingZoom) {
      zoomRequest = true;
      isDraggingZoom=false;
      repaint();
    }
  }
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mouseDragged(MouseEvent e) {
    if( isDraggingRot ) {
      double dx = (double)(mX-e.getX())*0.02;
      double dy = (double)(mY-e.getY())*0.02;
      mX = e.getX();
      mY = e.getY();
      angleOx -= dy;
      angleOy += dx;

      if(angleOx<0.0) angleOx=0.0;
      if(angleOx>Math.PI/2.0) angleOx=Math.PI/2.0;
      if(angleOy<0.0) angleOy=0.0;
      if(angleOy>Math.PI) angleOy=Math.PI;
      updateAxisPosition();
      display();
      zoomAllowedMode =0;
    }
    if( isDraggingZoom ) {
      mX2 = e.getX();
      mY2 = e.getY();
      repaint();
    }
  }
  public void mouseMoved(MouseEvent e) {}

  public void resetRotation() {
    angleOx=0.5;
    angleOy=0.785f;
    updateAxisPosition();
    display();
    zoomAllowedMode =0;
  }

  void rotateYX() {
    angleOx=0.0;
    angleOy=Math.PI;
    updateAxisPosition();
    display();
    zoomAllowedMode = ZOOM_YX;
  }

  void rotateYZ() {
    angleOx=0.0;
    angleOy=Math.PI/2.0;
    updateAxisPosition();
    display();
    zoomAllowedMode = ZOOM_ZY;
  }

  void rotateXZ() {
    angleOx=Math.PI/2.0;
    angleOy=Math.PI/2.0;
    updateAxisPosition();
    display();
    zoomAllowedMode = ZOOM_ZX;
  }

  void rotate(double ox,double oy) {
    angleOx=ox;
    angleOy=oy;
    if(angleOx<0.0) angleOx=0.0;
    if(angleOx>Math.PI/2.0) angleOx=Math.PI/2.0;
    if(angleOy<0.0) angleOy=0.0;
    if(angleOy>Math.PI) angleOy=Math.PI;
    updateAxisPosition();
    display();
    zoomAllowedMode =0;
  }

  private Rectangle buildZoomRect() {

    Rectangle r = new Rectangle();

    if (mX < mX2) {
      if (mY < mY2) {
        r.setRect(mX, mY, mX2 - mX, mY2 - mY);
      } else {
        r.setRect(mX, mY2, mX2 - mX, mY - mY2);
      }
    } else {
      if (mY < mY2) {
        r.setRect(mX2, mY, mX - mX2, mY2 - mY);
      } else {
        r.setRect(mX2, mY2, mX - mX2, mY - mY2);
      }
    }

    return r;
  }

  // --------------------------------------------------------------------------
  // 2D painting
  // --------------------------------------------------------------------------

  public void paint(Graphics g) {

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
      RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    FontRenderContext frc = g2.getFontRenderContext();

    super.paint(g);

    if (xAxis.isVisible() && xAxis.isDrawAble()) {
      for (int i = 0; i < xAxis.labelInfo.size(); i++) {
        LabelInfo l = (LabelInfo) xAxis.labelInfo.get(i);
        l.measureLabel(frc);
        l.computePosition();
        l.paint(g);
      }
      xAxis.nameInfo.measureLabel(frc);
      xAxis.nameInfo.computePosition();
      xAxis.nameInfo.paint(g);
    }

    if (yAxis.isVisible() && yAxis.isDrawAble()) {
      for (int i = 0; i < yAxis.labelInfo.size(); i++) {
        LabelInfo l = (LabelInfo) yAxis.labelInfo.get(i);
        l.measureLabel(frc);
        l.computePosition();
        l.paint(g);
      }
      yAxis.nameInfo.measureLabel(frc);
      yAxis.nameInfo.computePosition();
      yAxis.nameInfo.paint(g);
    }

    if (zAxis.isVisible() && zAxis.isDrawAble()) {
      for (int i = 0; i < zAxis.labelInfo.size(); i++) {
        LabelInfo l = (LabelInfo) zAxis.labelInfo.get(i);
        l.measureLabel(frc);
        l.computePosition();
        l.paint(g);
      }
      zAxis.nameInfo.measureLabel(frc);
      zAxis.nameInfo.computePosition();
      zAxis.nameInfo.paint(g);
    }

    if( isDraggingZoom) {
      g.setColor(Color.WHITE);
      Rectangle zRect = buildZoomRect();
      g.drawRect(zRect.x, zRect.y, zRect.width, zRect.height);
    }

    //Dimension d = getSize();
    //g.drawString("Ox   " + Double.toString(angleOx),5,d.height-40);
    //g.drawString("Oy   " + Double.toString(angleOy),5,d.height-20);

  }

  // Update lookat matrix (rotation)
  private void updateModelMatrix(GL gl,double scale) {

    // Model matrix --------------------------------------

    // Scale angle in -PI,PI
    angleOx = Utils.RoundAngle(angleOx);
    angleOy = Utils.RoundAngle(angleOy);

    /*
    VERTEX3D camDir  = new VERTEX3D();
    VERTEX3D camUp   = new VERTEX3D();
    VERTEX3D camLeft = new VERTEX3D();

    // Convert polar coordinates
    camDir.x = -Math.cos(angleOx) * Math.sin(angleOy);
    camDir.y =  Math.sin(angleOx);
    camDir.z = -Math.cos(angleOx) * Math.cos(angleOy);

    camLeft.x = -Math.cos(angleOy);
    camLeft.y = 0.0;
    camLeft.z = Math.sin(angleOy);

    camUp = Utils.Cross(camDir, camLeft);

    VERTEX3D center = new VERTEX3D();
    center.x = (xMax+xMin)/2.0;
    center.y = 0.0;
    center.z = (zMax+zMin)/2.0;

    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glLoadIdentity();
    glu.gluLookAt((camDir.x * camDist) + center.x,
                  (camDir.y * camDist) + center.y,
                  (camDir.z * camDist) + center.z,
                  center.x, center.y, center.z,
                  camUp.x, camUp.y, camUp.z);
    */

    VERTEX3D center = new VERTEX3D();
    center.x = (xAxis.getMax()+xAxis.getMin())/2.0;
    center.y = (yAxis.getMax()+yAxis.getMin())/2.0;
    center.z = (zAxis.getMax()+zAxis.getMin())/2.0;

    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glLoadIdentity();
    gl.glScaled(scale,scale,scale);
    gl.glRotated(-Utils.ToDeg(angleOx),1.0,0.0,0.0);
    gl.glRotated(-Utils.ToDeg(angleOy),0.0,1.0,0.0);
    gl.glTranslated(-center.x,-center.y,-center.z);

  }

}
