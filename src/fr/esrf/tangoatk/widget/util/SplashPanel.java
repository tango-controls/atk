/*
 * SplashPanel.java
 *
 * Created on February 28, 2002, 10:37 AM
 */

package fr.esrf.tangoatk.widget.util;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
    
/**
 *
 * @author  root
 */

public class SplashPanel extends JComponent {

    // Splah panel constructor
    
    public SplashPanel(Dimension size) {
     
	setLayout( null );
	setOpaque(false);
	setSize( size );
	setPreferredSize( size );
	
        title = new JSmoothLabel();
        title.setFont(new java.awt.Font("Dialog", 1, 18));
        title.setForeground(new java.awt.Color(204, 204, 204));
        title.setText("");
	title.setHorizontalAlignment( JSmoothLabel.LEFT_ALIGNMENT );
	title.setValueOffsets( 0 , -5 );
	title.setOpaque( false );
        add(title);
	
        message = new JSmoothLabel();
	message.setFont(new java.awt.Font("Dialog", 0, 14));
        message.setForeground(new java.awt.Color(204, 204, 204));
        message.setText("Initializing...");
	message.setHorizontalAlignment( JSmoothLabel.LEFT_ALIGNMENT );
	message.setValueOffsets( 0 , -3 );
	message.setOpaque( false );
        add(message);
	
        copyright = new JSmoothLabel();
        copyright.setFont(new java.awt.Font("Dialog", 0, 10));
        copyright.setForeground(new java.awt.Color(204, 204, 204));
        copyright.setText("(c) ESRF 2002");
	copyright.setHorizontalAlignment( JSmoothLabel.LEFT_ALIGNMENT );
	copyright.setOpaque( false );
        add(copyright);

        progress = new JSmoothProgressBar();
        progress.setStringPainted(true);
        add(progress);
	
	placeComponents( size );

	/*
	addComponentListener( new ComponentListener() {
          public void componentHidden(ComponentEvent e) {}
          public void componentMoved(ComponentEvent e) {}
          public void componentResized(ComponentEvent e) {
            placeComponents();
          }
          public void componentShown(ComponentEvent e) {
            placeComponents();
          }
        });
	*/
    }

    // Place components
         
    private void placeComponents(Dimension d) {
    
       int w = d.width;
       int h = d.height;
       
       title.setBounds(      5 , h-95, w-10 , 23 );
       message.setBounds(    5 , h-75 , w-10 , 18 );
       progress.setBounds(   5 , h-55 , w-10 , 20 );
       copyright.setBounds(  5 , h-35 , w-10 , 15 );

    }
        
    // Paint the components using double buffer    
    /*    
    public void paint(Graphics g) {
    
      Dimension d=getSize();
      int w = d.width;
      int h = d.height;
      
      BufferedImage bi=new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR );
      Graphics big = bi.getGraphics();
      super.paint( big );
      g.drawImage(bi,0,0,this);
      
    }
    */

    // Panel components
           
    public JSmoothLabel title;
    public JSmoothLabel message;
    public JSmoothLabel copyright;
    public JSmoothProgressBar progress;
     
}
