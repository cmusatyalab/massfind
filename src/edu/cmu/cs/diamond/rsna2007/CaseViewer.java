package edu.cmu.cs.diamond.rsna2007;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class CaseViewer extends JPanel {
    private Case theCase;

    public CaseViewer() {
        super();
        
        setMinimumSize(new Dimension(800, 600));
        setPreferredSize(new Dimension(800, 600));
    }

    public void setCase(Case c) {
        theCase = c;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (theCase == null) {
            return;
        }
        
        g.drawString(theCase.getName(), 100, 100);
    }
}
