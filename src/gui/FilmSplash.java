package gui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JWindow;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class FilmSplash extends JWindow
{
    private Image image;
    private ImageIcon imageIcon;

    public FilmSplash()
    {
        String fileName = System.getProperty("user.dir")
                + File.separator + "src"
                + File.separator + "data" + File.separator + "kinosaal.jpg";
        this.image = Toolkit.getDefaultToolkit().getImage(fileName);
        this.imageIcon = new ImageIcon(image);

        try
        {
            setSize(721, 461);
            setLocationRelativeTo(null);
            show();
            Applause applause = new Applause();
            applause.start();
            Thread.sleep(5000);
            dispose();
        }
        catch (Exception exception)
        {
        }
    }

    public static void main(String[] args)
    {
        FilmSplash filmSplash = new FilmSplash();
        FilmDataBaseGUI.colorItems(new FilmDataBaseGUI());
    }

    public void paint(Graphics g)
    {
        g.drawImage(image, 0, 0, this);
    }

    class Applause extends Thread
    {

        @Override
        public void run()
        {
            clap();
        }

        public void clap()
        {
            InputStream in = null;
            AudioStream as = null;
            try
            {
                String fileName = System.getProperty("user.dir")
                        + File.separator + "src"
                        + File.separator + "data" + File.separator + "applaus.wav";

                in = new FileInputStream(fileName);
                as = new AudioStream(in);
                AudioPlayer.player.start(as);
            }
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(null, ex.toString());
            }
        }
    }
        private void designComponents() throws Exception
    {
        Color nimbuscolor = new Color(106,206,146);
        UIManager.put("nimbusBase", nimbuscolor);
        UIManager.put("nimbusBlueGrey", nimbuscolor);
        UIManager.put("control", nimbuscolor);
        UIManager.put("defaultFont", new Font("Arial", Font.PLAIN, 13));

        SwingUtilities.updateComponentTreeUI(this);
    }
}
