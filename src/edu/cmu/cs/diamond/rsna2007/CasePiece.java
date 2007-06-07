package edu.cmu.cs.diamond.rsna2007;

import java.awt.image.BufferedImage;

public class CasePiece {
    final private BufferedImage image;
    final private Truth truth;

    // right now we support a pre-computed list
    // but we want to change this to be generated on the fly
    public Truth getTruth() {
        return truth;
    }

    public CasePiece(BufferedImage image, Truth truth) {
        this.image = image;
        this.truth = truth;
    }
    
    public BufferedImage getImage() {
        return image;
    }
}
