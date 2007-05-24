package edu.cmu.cs.diamond.rsna2007;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

public class MagnifierWindow extends JWindow {
    protected class Magnifier extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // get the component to draw
            Point p = SwingUtilities.convertPoint(this, getWidth() / 2,
                    getHeight() / 2, viewer);
            Component c = SwingUtilities
                    .getDeepestComponentAt(viewer, p.x, p.y);

            if (c instanceof OneView) {
                OneView ov = (OneView) c;
                Point p2 = SwingUtilities.convertPoint(viewer, p, ov);

                Image img = ov.getImage();
                Point imgP = ov.getImagePoint(p2);

                int half = getWidth() / 2;

                int sx1 = imgP.x - half;
                int sx2 = imgP.x + half;
                int sy1 = imgP.y - half;
                int sy2 = imgP.y + half;
                g.drawImage(img, 0, 0, getWidth(), getHeight(), sx1, sy1, sx2,
                        sy2, null);
            }
        }
    }

    final protected CaseViewer viewer;

    public MagnifierWindow(CaseViewer viewer) {
        super();
        setBackground(Color.BLACK);
        getContentPane().setBackground(Color.BLACK);
        this.viewer = viewer;

        add(new Magnifier());
    }
}
