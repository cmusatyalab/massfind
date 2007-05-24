package edu.cmu.cs.diamond.rsna2007;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import edu.cmu.cs.diamond.opendiamond.Util;

public class OneView extends JComponent {
    final private BufferedImage img;

    protected BufferedImage scaledImg;

    private int drawPosY;

    private int drawPosX;

    private double scale;

    public OneView(BufferedImage img) {
        super();

        setBackground(null);

        this.img = img;

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                scaledImg = null;
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (scaledImg == null) {
            System.out.println("drawing scaled image");
            drawScaledImg();
            System.out.println("done");
        }

        g.drawImage(scaledImg, drawPosX, drawPosY, null);
    }

    private void drawScaledImg() {
        Insets in = getInsets();
        final int w = getWidth() - in.left - in.right;
        final int h = getHeight() - in.top - in.bottom;

        scale = Util.getScaleForResize(img.getWidth(), img.getHeight(), w, h);

        scaledImg = getGraphicsConfiguration()
                .createCompatibleImage((int) (img.getWidth() * scale),
                        (int) (img.getHeight() * scale));

        Util.scaleImage(img, scale, scaledImg, false);

        // center in X
        drawPosX = (w - scaledImg.getWidth()) / 2;
    }

    public Image getImage() {
        return img;
    }

    public Point getImagePoint(Point p) {
        return new Point((int) ((p.x - drawPosX) / scale),
                (int) ((p.y - drawPosY) / scale));
    }
}
