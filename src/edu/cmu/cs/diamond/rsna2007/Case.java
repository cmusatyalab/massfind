package edu.cmu.cs.diamond.rsna2007;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Case {
    final private BufferedImage rightCC;

    final private BufferedImage leftCC;

    final private BufferedImage rightML;

    final private BufferedImage leftML;
    
    final private List<ROI> rois = new ArrayList<ROI>();

    final private String name;

    public Case(BufferedImage rightCC, BufferedImage leftCC,
            BufferedImage rightML, BufferedImage leftML, String name) {
        this.rightCC = rightCC;
        this.leftCC = leftCC;
        this.rightML = rightML;
        this.leftML = leftML;

        this.name = name;
    }

    public BufferedImage getLeftCC() {
        return leftCC;
    }

    public BufferedImage getLeftML() {
        return leftML;
    }

    public String getName() {
        return name;
    }

    public BufferedImage getRightCC() {
        return rightCC;
    }

    public BufferedImage getRightML() {
        return rightML;
    }

    public int getMaximumHeight() {
        return Math.max(rightCC.getHeight(), Math.max(leftCC.getHeight(), Math
                .max(rightML.getHeight(), leftML.getHeight())));
    }
    
    // right now we support a pre-computed list
    // but we want to change this to be generated on the fly
    public ROI[] getROIs() {
        return rois.toArray(new ROI[0]);
    }
}
