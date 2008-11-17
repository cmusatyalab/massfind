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

import java.awt.image.BufferedImage;

public class CasePiece {
    final private BufferedImage image;

    final private Truth truth;

    final private String imageFilename;

    // right now we support a pre-computed list
    // but we want to change this to be generated on the fly
    public Truth getTruth() {
        return truth;
    }

    public CasePiece(BufferedImage image, Truth truth, String imageFilename) {
        this.image = image;
        this.truth = truth;
        this.imageFilename = imageFilename;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getImageFilename() {
        return imageFilename;
    }
}
