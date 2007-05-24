package edu.cmu.cs.diamond.rsna2007;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

public class MagnifierWindow extends JWindow {
    protected class Magnifier extends JComponent {
        final private Font font = Font.decode(null);

        final static private int BORDER_SIZE = 4;

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

                // draw label
                String label = ov.getViewName();
                g.setFont(font);
                FontMetrics fm = g.getFontMetrics();
                int sw = SwingUtilities.computeStringWidth(fm, label);
                int sh = fm.getHeight();
                int sx = half - sw / 2;
                int sy = getHeight() - sh - BORDER_SIZE;
                g.setColor(Color.BLACK);
                g.fillRect(sx - 2, sy, sw + 4, sh);
                g.setColor(Color.WHITE);
                g.drawString(label, sx, sy + sh - fm.getDescent()
                        - fm.getLeading());
            }

            // draw border
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.GRAY);
            g2.setStroke(new BasicStroke(BORDER_SIZE));
            g2.drawRect(BORDER_SIZE / 2, BORDER_SIZE / 2, getWidth()
                    - BORDER_SIZE, getHeight() - BORDER_SIZE);
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
