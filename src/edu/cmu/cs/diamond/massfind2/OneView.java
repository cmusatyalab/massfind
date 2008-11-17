/*
 * MassFind 2: A Diamond application for exploration of breast tumors
 *
 * Copyright (c) 2007-2008 Carnegie Mellon University. All rights reserved.
 * Additional copyrights may be listed below.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution in the file named LICENSE.
 *
 * Technical and financial contributors are listed in the file named
 * CREDITS.
 */

package edu.cmu.cs.diamond.massfind2;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import edu.cmu.cs.diamond.opendiamond.Util;

public class OneView extends JPanel {
    private CasePiece casePiece;

    protected BufferedImage scaledImg;

    private int drawPosY;

    private int drawPosX;

    private double scale;

    private int unscaledHeight;

    private int oldW;

    private int oldH;

    private String viewName;

    private ROI roi;

    public OneView() {
        super();

        setBackground(null);
    }

    public void setView(CasePiece casePiece, String viewName, int unscaledHeight) {
        this.casePiece = casePiece;

        this.viewName = viewName;

        this.unscaledHeight = unscaledHeight;

        int w = casePiece.getImage().getWidth();
        int h = unscaledHeight;
        setPreferredSize(new Dimension(w, h));
        setMinimumSize(new Dimension(w / 2, h / 2));
        setMaximumSize(new Dimension(w * 2, h * 2));

        // force redraw of scaled image
        oldW = oldH = 0;

        String toolTipText = null;
        Truth t = casePiece.getTruth();
        if (t != null) {
            if (t.getBiopsy() == Truth.Biopsy.BIOPSY_MALIGNANT) {
                toolTipText = "<html><table><tr><th>Age</th><td>" + t.getAge()
                        + "</tr><tr><th>Shape</th><td>" + t.getShape()
                        + "</td></tr><tr><th>Margin</th><td>" + t.getMargin()
                        + "</td></tr><tr><th>BIRAD</th><td>" + t.getBirad()
                        + "</td></tr><tr><th>Density</th><td>" + t.getDensity()
                        + "</td></tr><tr><th>Subtlety</th><td>"
                        + t.getSubtlety() + "</td></tr></table></html>";
            }

            roi = t.getROI();
        } else {
            roi = null;
        }
        setToolTipText(toolTipText);
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

        if (roi != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.RED);

            g2.translate(drawPosX, drawPosY);

            for (Point2D p : roi.getContour()) {
                g2.fill(new Rectangle2D.Double(p.getX() * scale, p.getY()
                        * scale, 1, 1));
            }

            // Point2D c = roi.getCenter();
            //
            // BufferedImage roiImg = roi.getImage();
            // double rw = roiImg.getWidth();
            // double rh = roiImg.getHeight();
            // double x = c.getX() - rw / 2;
            // double y = c.getY() - rh / 2;
            //
            // g2.draw(new Rectangle2D.Double(scale * x, scale * y, scale * rh,
            // scale * rh));
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

    public ROI getROI() {
        return roi;
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

    public String getImageFilename() {
        return casePiece.getImageFilename();
    }

    public void setROI(ROI roi) {
        this.roi = roi;
        repaint();
    }
}
