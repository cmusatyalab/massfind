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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import edu.cmu.cs.diamond.opendiamond.Result;
import edu.cmu.cs.diamond.opendiamond.Util;

public class MassResult {
    final private Result theResult;

    final private BufferedImage thumbnail;

    final private BufferedImage image;

    final private boolean malignant;

    final private int similarity;

    public MassResult(Result r, int size, int border, int pad) {
        theResult = r;

        // is malignant?
        malignant = new String(theResult.getValue("name")).startsWith("TM");

        // similarity
        similarity = Util.extractInt(theResult.getValue("similarity"));

        // read image
        BufferedImage b = null;
        try {
            b = ImageIO.read(new ByteArrayInputStream(r.getData()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // draw thumbnail
        BufferedImage t;
        if (b == null) {
            thumbnail = null;
            image = null;
            return;
        }

        double scale = Util.getScaleForResize(b.getWidth(), b.getHeight(),
                size, size);
        t = Util.scaleImageFast(b, scale);

        image = GraphicsUtilities.toCompatibleImage(b);

        // draw border
        int w = t.getWidth() + (border + pad) * 2;
        int h = t.getHeight() + (border + pad) * 2;
        BufferedImage newThumb;

        newThumb = GraphicsUtilities.createCompatibleTranslucentImage(w, h);
        Graphics2D g = newThumb.createGraphics();
        g.setStroke(new BasicStroke(border));
        if (malignant) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.BLUE);
        }
        g.drawRect(border / 2, border / 2, w - border, h - border);
        g.drawImage(t, border + pad, border + pad, null);
        g.dispose();

        thumbnail = newThumb;
    }

    public BufferedImage getThumbnail() {
        return thumbnail;
    }

    public boolean isMalignant() {
        return malignant;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getSimilarity() {
        return similarity;
    }
}
