package edu.cmu.cs.diamond.massfind2;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class LayoutTest extends JFrame {
    public static class SizePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int w = getWidth();
            FontMetrics fm = g.getFontMetrics();
            String s = Integer.toString(w);
            g.drawString(s, 5, fm.getMaxAscent());
        }
    }

    public LayoutTest(boolean spring) {
        super("Test");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationByPlatform(true);

        if (spring) {
            doSpring();
        } else {
            doBox();
        }

        pack();
        setVisible(true);
    }

    private void doBox() {
        Box h = Box.createHorizontalBox();
        
        SizePanel j1 = makeSizePanel1();
        h.add(j1);

        h.add(Box.createHorizontalStrut(10));
        
        SizePanel j2 = makeSizePanel2();
        h.add(j2);

        add(h);
    }

    private SizePanel makeSizePanel2() {
        SizePanel p = new SizePanel();
        p.setBackground(Color.getHSBColor(0.3f, 0.3f, 0.7f));
        p.setMinimumSize(new Dimension(150, 50));
        p.setPreferredSize(new Dimension(300, 50));
        p.setMaximumSize(new Dimension(600, 50));
        return p;
    }

    private SizePanel makeSizePanel1() {
        SizePanel p = new SizePanel();
        p.setBackground(Color.getHSBColor(0, 0.3f, 0.7f));
        p.setMinimumSize(new Dimension(100, 50));
        p.setPreferredSize(new Dimension(200, 50));
        p.setMaximumSize(new Dimension(400, 50));
        return p;
    }

    private void doSpring() {
        Container cp = getContentPane();

        SpringLayout layout = new SpringLayout();

        setLayout(layout);

        SizePanel j1 = makeSizePanel1();
        add(j1);

        SizePanel j2 = makeSizePanel2();
        add(j2);

        layout.putConstraint(SpringLayout.WEST, j1, 0, SpringLayout.WEST, cp);
        layout.putConstraint(SpringLayout.WEST, j2, 10, SpringLayout.EAST, j1);
        layout.putConstraint(SpringLayout.EAST, cp, 0, SpringLayout.EAST, j2);

        layout.putConstraint(SpringLayout.NORTH, j1, 0, SpringLayout.NORTH, cp);
        layout.putConstraint(SpringLayout.SOUTH, cp, 0, SpringLayout.SOUTH, j2);
    }

    public static void main(String[] args) {
        new LayoutTest(true);
        new LayoutTest(false);
    }
}
