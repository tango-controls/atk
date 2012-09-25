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

import com.sun.opengl.util.BufferUtil;

import javax.media.opengl.GL;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.nio.ByteBuffer;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.event.ActionListener;
import java.awt.*;

/**
 * Mathematic util functions
 */
public class Utils {

  private static Class theClass=null;
  private static Insets bMargin = new Insets(3,3,3,3);

  final static Color  fColor = new Color(99, 97, 156);
  final static Insets zInset = new Insets(0,0,0,0);
  final static Font   labelFont = new Font("Dialog", Font.PLAIN, 12);
  final static Font   labelbFont = new Font("Dialog", Font.BOLD, 12);

  static private void init() {
    if( theClass==null ) {
      String className = "fr.esrf.tangoatk.widget.util.jgl3dchart.Utils";
      try {
        theClass = Class.forName(className);
      } catch (Exception e) {
        System.out.println("Utils.init() Class not found: " + className);
      }
    }
  }

  // Make cross product between 2 vectors
  static VERTEX3D Cross(VERTEX3D v1,VERTEX3D v2) {

    VERTEX3D result = new VERTEX3D();
    result.x = (v1.y)*(v2.z) - (v1.z)*(v2.y);
    result.y = (v1.z)*(v2.x) - (v1.x)*(v2.z);
    result.z = (v1.x)*(v2.y) - (v1.y)*(v2.x);
    return result;

  }

  // Make a texture from a texture image
  static ByteBuffer convertImageToTexture(BufferedImage img, boolean storeAlphaChannel) {

    int[] packedPixels = new int[img.getWidth() * img.getHeight()];
    PixelGrabber pixelgrabber = new PixelGrabber(img, 0, 0, img.getWidth(), img.getHeight(), packedPixels, 0, img.getWidth());

    try {
      pixelgrabber.grabPixels();
    } catch (InterruptedException e) {
      throw new RuntimeException();
    }

    int bytesPerPixel = storeAlphaChannel ? 4 : 3;

    ByteBuffer unpackedPixels = BufferUtil.newByteBuffer(packedPixels.length * bytesPerPixel);

    for (int row = img.getHeight() - 1; row >= 0; row--) {
      for (int col = 0; col < img.getWidth(); col++) {

        int packedPixel = packedPixels[row * img.getWidth() + col];
        unpackedPixels.put((byte) ((packedPixel >> 16) & 0xFF));
        unpackedPixels.put((byte) ((packedPixel >> 8) & 0xFF));
        unpackedPixels.put((byte) ((packedPixel >> 0) & 0xFF));

        if (storeAlphaChannel) {
          unpackedPixels.put((byte) ((packedPixel >> 24) & 0xFF));
        }

      }
    }

    unpackedPixels.flip();
    return unpackedPixels;

  }

  // Round angle in the [-PI,PI] range
  static double RoundAngle(double a) {

    double r=a;
    while(r<-Math.PI) r+=2.0*Math.PI;
    while(r> Math.PI) r-=2.0*Math.PI;
    return r;

  }

  // Convert angle into degres
  static double ToDeg(double radians) {
    return (radians/Math.PI)*180.0;
  }

  // project vertex
  static public void project(GL gl,double x,double y,double z,double[] _2dcoord) {

    int    viewport[]   = new int[4];
    double mvmatrix[]   = new double[16];
    double projmatrix[] = new double[16];
    double wcoord[]     = new double[4];

    gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
    gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvmatrix, 0);
    gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projmatrix, 0);

    JGL3DView.glu.gluProject(x, y, z,
              mvmatrix, 0,
              projmatrix, 0,
              viewport, 0,
              wcoord, 0);

    _2dcoord[0] = wcoord[0];
    _2dcoord[1] = (double)viewport[3] - wcoord[1] - 1.0;

  }

  // Unproject vertex
  static public void unproject(GL gl,double x,double y,double z,double[] _3dcoord) {

    int    viewport[]   = new int[4];
    double mvmatrix[]   = new double[16];
    double projmatrix[] = new double[16];
    double wcoord[]     = new double[4];

    gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
    gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvmatrix, 0);
    gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projmatrix, 0);
    double realy = viewport[3] - (int) y - 1;
    JGL3DView.glu.gluUnProject(x, realy, z,
              mvmatrix, 0,
              projmatrix, 0,
              viewport, 0,
              wcoord, 0);

    _3dcoord[0] = wcoord[0];
    _3dcoord[1] = wcoord[1];
    _3dcoord[2] = wcoord[2];

  }

  static public void project3D(GL gl,double x,double y,double z,double[] _3dcoord) {

    double mvmatrix[]   = new double[16];
    gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvmatrix, 0);

    _3dcoord[0] = x*mvmatrix[0] + y*mvmatrix[4] + z*mvmatrix[8]  + mvmatrix[12];
    _3dcoord[1] = x*mvmatrix[1] + y*mvmatrix[5] + z*mvmatrix[9]  + mvmatrix[13];
    _3dcoord[2] = x*mvmatrix[2] + y*mvmatrix[6] + z*mvmatrix[10] + mvmatrix[14];

  }

  static public JButton createIconButton(String name,boolean hasDisa,String tipText, ActionListener l) {
    init();
    if( theClass!=null ) {
      JButton nB = new JButton(new ImageIcon(theClass.getResource("/fr/esrf/tangoatk/widget/util/jgl3dchart/gif/" + name + ".gif")));
      nB.setPressedIcon(new ImageIcon(theClass.getResource("/fr/esrf/tangoatk/widget/util/jgl3dchart/gif/" + name + "_push.gif")));
      if (hasDisa)
        nB.setDisabledIcon(new ImageIcon(theClass.getResource("/fr/esrf/tangoatk/widget/util/jgl3dchart/gif/" + name + "_disa.gif")));
      nB.setToolTipText(tipText);
      nB.setMargin(bMargin);
      nB.setBorder(null);
      nB.addActionListener(l);
      return nB;
    } else {
      return new JButton(name);
    }
  }

  public static Border createTitleBorder(String name) {
    return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
            name, TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION,
            labelbFont, fColor);
  }

}
