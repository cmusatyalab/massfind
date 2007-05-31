package edu.cmu.cs.diamond.rsna2007;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Banner extends JPanel {
    public Banner() {
        super();
        
        setBackground(null);
        
        JLabel l = new JLabel("Diamond!");
        l.setForeground(Color.WHITE);
        add(l);
    }
}
