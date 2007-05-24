package edu.cmu.cs.diamond.rsna2007;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class CaseViewer extends JPanel {
    private final static int MAGNIFIER_SIZE = 256;

    private Case theCase;

    private OneView c1;

    private OneView c2;

    private OneView c3;

    private OneView c4;

    protected int magX;

    protected int magY;

    protected boolean magnifying;

    public CaseViewer() {
        super();

        setBackground(null);

        setLayout(new GridLayout(1, 4, 5, 0));

        setMinimumSize(new Dimension(800, 600));
        setPreferredSize(new Dimension(800, 600));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 2) {
                    magnifying = true;
                    magX = e.getX();
                    magY = e.getY();
                    repaintMagnifier(magX, magY);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 2) {
                    magnifying = false;
                    repaintMagnifier(e.getX(), e.getY());
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (magnifying) {
                    repaintMagnifier(e.getX(), e.getY());
                    magX = e.getX();
                    magY = e.getY();
                    repaintMagnifier(magX, magY);
                }
            }
        });
    }

    protected void repaintMagnifier(int x, int y) {
        repaint(x - MAGNIFIER_SIZE / 2, y - MAGNIFIER_SIZE / 2, MAGNIFIER_SIZE,
                MAGNIFIER_SIZE);
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

        // magnify
        if (magnifying) {
            Component c = SwingUtilities
                    .getDeepestComponentAt(this, magX, magY);
            if (c instanceof OneView) {
                OneView o = (OneView) c;
                Point p = SwingUtilities.convertPoint(this, magX, magY, c);
                g.drawImage(o.getMagnifiedImage(p.x, p.y, MAGNIFIER_SIZE),
                        magX - MAGNIFIER_SIZE / 2, magY - MAGNIFIER_SIZE / 2,
                        null);
            }
        }
    }
}
