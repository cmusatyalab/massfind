package edu.cmu.cs.diamond.rsna2007;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import edu.cmu.cs.diamond.opendiamond.Util;

public class OneView extends JPanel {
    final private CasePiece casePiece;

    protected BufferedImage scaledImg;

    private int drawPosY;

    private int drawPosX;

    private double scale;

    final private int unscaledHeight;

    private int oldW;

    private int oldH;

    final private String viewName;

    public OneView(CasePiece casePiece, String viewName, int unscaledHeight) {
        super();

        setBackground(null);

        this.casePiece = casePiece;

        this.viewName = viewName;

        this.unscaledHeight = unscaledHeight;

        int w = casePiece.getImage().getWidth();
        int h = unscaledHeight;
        setPreferredSize(new Dimension(w, h));
        setMinimumSize(new Dimension(w / 2, h / 2));
        setMaximumSize(new Dimension(w * 2, h * 2));
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
        // g.setColor(Color.WHITE);
        // g.drawString(Double.toString(scale),
        // g.getFontMetrics().getMaxAscent() + 10, 20);

        Truth t = casePiece.getTruth();
        if (t != null) {
            ROI r = t.getROI();
            if (r != null) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.RED);

                g2.translate(drawPosX, drawPosY);

                for (Point2D p : r.getContour()) {
                    g2.fill(new Rectangle2D.Double(p.getX() * scale, p.getY()
                            * scale, 1, 1));
                }
            }
        }
    }

    private void drawScaledImg() {
        Insets in = getInsets();
        final int cW = getWidth() - in.left - in.right;
        final int cH = getHeight() - in.top - in.bottom;

        final int w = casePiece.getImage().getWidth();
        final int h = unscaledHeight;

        scale = Util.getScaleForResize(w, h, cW, cH);

        final int sW = (int) (w * scale);

        scaledImg = Util.scaleImageFast(casePiece.getImage(), scale);

        // center in X
        drawPosX = (cW - sW) / 2 + in.left;
        drawPosY = in.top;
    }

    public BufferedImage getImage() {
        return casePiece.getImage();
    }

    public Point getImagePoint(Point p) {
        return new Point((int) ((p.x - drawPosX) / scale),
                (int) ((p.y - drawPosY) / scale));
    }

    public String getViewName() {
        return viewName;
    }

    public double getScale() {
        return scale;
    }
}
