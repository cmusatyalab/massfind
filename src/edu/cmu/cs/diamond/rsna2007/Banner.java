package edu.cmu.cs.diamond.rsna2007;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.*;

import edu.cmu.cs.diamond.opendiamond.Util;

public class Banner extends JPanel {
    public static class Logo extends JComponent {
        private int oldH;

        private int oldW;

        private BufferedImage scaledImg;

        private int drawPosY;

        private int drawPosX;

        private BufferedImage image;

        private double scale;

        public Logo(BufferedImage image) {
            this.image = image;
            int w = image.getWidth();
//            int h = image.getHeight();
            setPreferredSize(new Dimension(w, 150));
//            setMinimumSize(new Dimension(10, h / 2));
//            setMaximumSize(new Dimension(100, h * 2));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int w = getWidth();
            int h = getHeight();

            if (oldW != w || oldH != h) {
                drawScaledImg();
                oldW = w;
                oldH = h;
            }

            g.drawImage(scaledImg, drawPosX, drawPosY, null);
        }

        private void drawScaledImg() {
            Insets in = getInsets();
            final int cW = getWidth() - in.left - in.right;
            final int cH = getHeight() - in.top - in.bottom;

            final int w = image.getWidth();
            final int h = image.getHeight();

            BufferedImage tmp = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = tmp.createGraphics();
            g2.drawImage(image, 0, 0, null);
            g2.dispose();
            
            scale = Util.getScaleForResize(w, h, cW, cH);

            final int sW = (int) (w * scale);
            final int sH = (int) (h * scale);

            scaledImg = Util.scaleImage(tmp, scale);

            // center in X,Y
            drawPosX = (cW - sW) / 2 + in.left;
            drawPosY = (cH - sH) / 2 + in.top;
        }
    }

    public Banner(File logoDir) {
        super();

        setBackground(null);
        setLayout(new BorderLayout());

        // read logo dir
        File logos[] = logoDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".png");
            }
        });

        if (logos == null || logos.length == 0) {
            JLabel l = new JLabel("Diamond!");
            l.setForeground(Color.WHITE);
            add(l);
        } else {
            Box b = Box.createHorizontalBox();

            // sort logos
            Arrays.sort(logos);
            for (File file : logos) {
                Logo l = null;
                try {
                    l = new Logo(ImageIO.read(file));
                    b.add(l);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                b.add(Box.createHorizontalGlue());
            }
            add(b);
        }
    }
}
