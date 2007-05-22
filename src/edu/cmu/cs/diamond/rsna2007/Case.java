package edu.cmu.cs.diamond.rsna2007;

import java.awt.image.BufferedImage;

public class Case {
    final private BufferedImage rightCC;
    final private BufferedImage leftCC;
    final private BufferedImage rightML;
    final private BufferedImage leftML;

    final private String name;
    
    public Case(BufferedImage rightCC, BufferedImage leftCC,
            BufferedImage rightML, BufferedImage leftML, String name) {
        this.rightCC = rightCC;
        this.leftCC = leftCC;
        this.rightML = rightML;
        this.leftML = leftML;
        
        this.name = name;
    }
}
