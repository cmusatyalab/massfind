package edu.cmu.cs.diamond.rsna2007;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import edu.cmu.cs.diamond.opendiamond.Util;

public class OneView extends JComponent {
    final private BufferedImage img;

    protected BufferedImage scaledImg;

    private int drawPosY;

    private int drawPosX;

    private double scale;

    final private String viewName;

    public OneView(BufferedImage img, String viewName) {
        super();

        setBackground(null);

        this.img = img;

        this.viewName = viewName;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (scaledImg == null) {
            // System.out.println("drawing scaled image");
            drawScaledImg();
            // System.out.println("done");
        }

        g.drawImage(scaledImg, drawPosX, drawPosY, null);
    }

    public void setScale(double scale) {
        if (scale <= 0) {
            throw new IllegalArgumentException("Scale (" + scale
                    + ") cannot be <= 0");
        }

        this.scale = scale;

        scaledImg = null;

        Dimension d = new Dimension((int) (scale * img.getWidth()),
                (int) (scale * img.getHeight()));
        setPreferredSize(d);
        revalidate();
    }

    private void drawScaledImg() {
        Insets in = getInsets();
        final int w = getWidth() - in.left - in.right;

        final int ww = (int) (img.getWidth() * scale);
        final int hh = (int) (img.getHeight() * scale);

        scaledImg = getGraphicsConfiguration().createCompatibleImage(ww, hh);

        Util.scaleImage(img, scaledImg, false);

        // center in X
        drawPosX = (w - scaledImg.getWidth()) / 2;
    }

    public BufferedImage getImage() {
        return img;
    }

    public Point getImagePoint(Point p) {
        return new Point((int) ((p.x - drawPosX) / scale),
                (int) ((p.y - drawPosY) / scale));
    }

    public String getViewName() {
        return viewName;
    }
}
