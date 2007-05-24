package edu.cmu.cs.diamond.rsna2007;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.JPanel;

import edu.cmu.cs.diamond.opendiamond.Util;

public class CaseViewer extends JPanel {
    private final static int MAGNIFIER_SIZE = 512;

    private final static int SPACING = 10;

    private Case theCase;

    final private OneView views[] = new OneView[4];

    final protected Cursor hiddenCursor = getToolkit().createCustomCursor(
            new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(),
            "null");

    final private Box hBox = Box.createHorizontalBox();

    final protected MagnifierWindow magnifierWindow;

    public CaseViewer() {
        super();

        setBackground(null);

        magnifierWindow = new MagnifierWindow(this);
        magnifierWindow.setSize(MAGNIFIER_SIZE, MAGNIFIER_SIZE);

        setMinimumSize(new Dimension(800, 600));
        setPreferredSize(new Dimension(800, 600));

        setLayout(new BorderLayout());

        add(hBox);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 2) {
                    updateMagnifierPosition(e);
                    setCursor(hiddenCursor);
                    magnifierWindow.setVisible(true);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 2) {
                    setCursor(null);
                    magnifierWindow.setVisible(false);
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (magnifierWindow.isVisible()) {
                    updateMagnifierPosition(e);
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                updateScale();
            }
        });
    }

    protected void updateScale() {
        int width = 0;
        int height = 0;

        for (OneView view : views) {
            BufferedImage img = view.getImage();
            width += img.getWidth() + SPACING;
            height = Math.max(img.getHeight(), height);
        }
        width -= SPACING;

        Insets in = getInsets();
        double scale = Util.getScaleForResize(width, height, getWidth()
                - in.left - in.right, getHeight() - in.top - in.bottom);
        
        for (OneView view : views) {
            view.setScale(scale);
        }
        
        revalidate();
    }

    public void setCase(Case c) {
        theCase = c;

        views[0] = new OneView(theCase.getRightCC(), "RCC");
        views[1] = new OneView(theCase.getLeftCC(), "LCC");
        views[2] = new OneView(theCase.getRightML(), "RML");
        views[3] = new OneView(theCase.getLeftML(), "LML");

        hBox.removeAll();
        hBox.add(views[0]);
        hBox.add(Box.createHorizontalStrut(SPACING));
        hBox.add(views[1]);
        hBox.add(Box.createHorizontalStrut(SPACING));
        hBox.add(views[2]);
        hBox.add(Box.createHorizontalStrut(SPACING));
        hBox.add(views[3]);

        updateScale();

        magnifierWindow.repaint();
    }

    protected void updateMagnifierPosition(MouseEvent e) {
        magnifierWindow.setLocation(e.getX() - MAGNIFIER_SIZE / 2, e.getY()
                - MAGNIFIER_SIZE / 2);
        magnifierWindow.repaint();
    }
}
