package edu.cmu.cs.diamond.rsna2007;

import java.awt.*;

import javax.swing.*;

public class MagnifierWindow extends JWindow {
    private static final int MAGNIFIER_SIZE = 512;

    protected class Magnifier extends JComponent {
        final private Font font = Font.decode(null);

        final static private int BORDER_SIZE = 4;

        public Magnifier() {
            Dimension d = new Dimension(MAGNIFIER_SIZE, MAGNIFIER_SIZE);
            setMinimumSize(d);
            setPreferredSize(d);
            setMaximumSize(d);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            final int w = getWidth();
            final int h = getHeight();

            // figure out rectangle
            Point cP = SwingUtilities.convertPoint(this, w / 2, h / 2, viewer);
            Rectangle rect = SwingUtilities.convertRectangle(this,
                    new Rectangle(w, h), viewer);

            // get all components
            int half = w / 2;

            for (OneView ov : viewer.getViews()) {
                if (ov.getBounds().intersects(rect)) {
                    Point p2 = SwingUtilities.convertPoint(viewer, cP, ov);

                    Image img = ov.getImage();
                    Point imgP = ov.getImagePoint(p2);

                    int sx = imgP.x - half;
                    int sy = imgP.y - half;
                    g.drawImage(img, -sx, -sy, null);
                }
            }

            // draw label
            Component c = SwingUtilities.getDeepestComponentAt(viewer, cP.x,
                    cP.y);
            if (c instanceof OneView) {
                OneView centerView = (OneView) c;

                String label = centerView.getViewName();
                g.setFont(font);
                FontMetrics fm = g.getFontMetrics();
                int sw = SwingUtilities.computeStringWidth(fm, label);
                int sh = fm.getHeight();
                int sx = half - sw / 2;
                int sy = h - sh - BORDER_SIZE;
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
            g2.drawRect(BORDER_SIZE / 2, BORDER_SIZE / 2, w - BORDER_SIZE, h
                    - BORDER_SIZE);

            // draw center
            if (false) {
                g2.setStroke(new BasicStroke());
                g.setColor(Color.RED);
                g.drawArc(half - 2, half - 2, 4, 4, 0, 360);
            }
        }
    }

    final protected CaseViewer viewer;

    final private Box box;

    final private Magnifier mag;

    public MagnifierWindow(CaseViewer viewer) {
        super();
        setBackground(Color.BLACK);
        getContentPane().setBackground(null);
        setCursor(CaseViewer.hiddenCursor);
        this.viewer = viewer;

        box = Box.createHorizontalBox();

        mag = new Magnifier();

        add(box);

        // packs the window
        setExtraResult(null, false);
    }

    public void setExtraResult(MassResult result, boolean putOnRight) {
        box.removeAll();

        JLabel l = null;
        if (result != null) {
            l = new JLabel(new ImageIcon(result.getImage()));
        }

        if (!putOnRight && l != null) {
            box.add(l);
        }
        box.add(mag);
        if (putOnRight && l != null) {
            box.add(l);
        }

        pack();
    }

    public void setMagnifyPoint(int x, int y) {
        setLocation(x - mag.getX() - MAGNIFIER_SIZE / 2, y - mag.getY()
                - MAGNIFIER_SIZE / 2);
    }
}
