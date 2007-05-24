package edu.cmu.cs.diamond.rsna2007;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class CaseViewer extends JPanel {
    private Case theCase;

    private OneView c1;

    private OneView c2;

    private OneView c3;

    private OneView c4;

    public CaseViewer() {
        super();

        setBackground(null);

        setLayout(new GridLayout(1, 4, 5, 0));

        setMinimumSize(new Dimension(800, 600));
        setPreferredSize(new Dimension(800, 600));
    }

    public void setCase(Case c) {
        theCase = c;

        c1 = new OneView(theCase.getLeftCC());
        c2 = new OneView(theCase.getRightCC());
        c3 = new OneView(theCase.getLeftML());
        c4 = new OneView(theCase.getRightML());

        removeAll();
        add(c1);
        add(c2);
        add(c3);
        add(c4);

        revalidate();
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
