/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.esrf.tangoatk.widget.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;

/**
 *
 * @author poncet
 */
public final class SplashTimer extends Splash
{

    long timeDuration = 2000;
    long timeStep = 200;
    TimerTask progressTask = null;
    Timer progressTimer = null;

    /**
     * Creates and displays an ATK splash panel which will progress during a
     * certain duration
     *
     * @param duration in milliseconds
     * @param step in milliseconds the time step where the splash progress
     * is updated
     */
    public SplashTimer(long duration, long step)
    {
        initComponents(null,null,null);
        if (duration > 0)
        {
            timeDuration = duration;
        }
        if (step > 0)
        {
            timeStep = step;
        }
        if ((timeDuration / timeStep) < 1)
        {
            timeDuration = 2000; // 2 secondes
            timeStep = 200; //200 ms
        }

        super.setMaxProgress((int) (timeDuration / timeStep));

        progressTask = new TimerTask()
        {
            public void run()
            {
                doOneStep();
            }
        };
        progressTimer = new Timer();
    }
    
    private void doOneStep()
    {
        int p = getProgressBar().getValue();
        super.progress(p + 1);
    }
    
    
    @Override
    protected void initComponents(ImageIcon icon, Color textColor, JSmoothProgressBar newBar)
    {

        setBackground(new Color(100, 110, 140));

        splashPanel = new SplashPanel(icon, textColor, newBar);

        setContentPane(splashPanel);
        pack();

        // Center the splash window
        Dimension d = splashPanel.getPreferredSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - d.width) / 2, (screenSize.height - d.height) / 2,
                d.width, d.height);
    }
    

    @Override
    public void setVisible(boolean b)
    {
        if (b)
        {
            super.setMaxProgress((int) (timeDuration / timeStep));
            super.initProgress();

            progressTimer.purge();
            progressTimer.schedule(progressTask, 0, timeStep);
        } else
        {
            progressTask.cancel();
            progressTimer.cancel();
            progressTimer.purge();
        }
        super.setVisible(b);
    }
    
    
    @Override
    public void initProgress()
    {
        // not allowed to use from the application code
    }

    @Override
    public void setMaxProgress(int i)
    {
        // not allowed to use from the application code
    }

    @Override
    public void progress(int i)
    {
        // not allowed to use from the application code
    }

    public static void main(String[] args)
    {
        SplashTimer  splt = new SplashTimer(6000, 500);
	splt.setTitle("SplashTimer Screen");
	splt.setCopyright("(c) ESRF 2014");
	splt.setMessage("This is the free message line");
        splt.setVisible(true);

        for (int i = 0; i <= 6; i++)
        {
            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
            }
        }
        splt.setVisible(false);
        try
        {
            Thread.sleep(3000);
        } catch (InterruptedException e)
        {
        }
        System.exit(0);

    } // end of main ()
}
