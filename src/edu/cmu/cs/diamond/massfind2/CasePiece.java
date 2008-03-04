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
