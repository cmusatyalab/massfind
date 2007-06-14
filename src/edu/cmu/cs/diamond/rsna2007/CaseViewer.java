package edu.cmu.cs.diamond.rsna2007;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JLayeredPane;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import edu.cmu.cs.diamond.opendiamond.Filter;
import edu.cmu.cs.diamond.opendiamond.FilterCode;
import edu.cmu.cs.diamond.opendiamond.ScopeSource;
import edu.cmu.cs.diamond.opendiamond.Search;
import edu.cmu.cs.diamond.opendiamond.Searchlet;

public class CaseViewer extends JLayeredPane {
    private final static int MAGNIFIER_SIZE = 512;

    private final static int SPACING = 10;

    private Case theCase;

    final private OneView views[] = new OneView[4];

    static final Cursor hiddenCursor = Toolkit.getDefaultToolkit()
            .createCustomCursor(
                    new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
                    new Point(), "Hidden Cursor");

    final protected MagnifierWindow magnifierWindow;

    final private SpringLayout layout = new SpringLayout();

    final protected Cursor defaultCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);

    final private SearchPanel leftSearchResults = new SearchPanel(this);

    final private SearchPanel rightSearchResults = new SearchPanel(this);

    final private MouseListener mouseListener = new MouseAdapter() {
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
                setCursor(defaultCursor);
                magnifierWindow.setVisible(false);
            }
        }

        // preliminary search support
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == 1 && e.getClickCount() == 2) {
                Component c = e.getComponent();

                if (c instanceof OneView) {
                    OneView ov = (OneView) c;
                    Truth t = ov.getTruth();
                    if (t != null) {
                        ROI r = t.getROI();

                        if (r != null) {
                            // start a search
                            startSearch(ov, r);
                            return;
                        }
                    }
                }
            }
        }
    };

    final private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
            if (magnifierWindow.isVisible()) {
                updateMagnifierPosition(e);
            }
        }
    };

    final private File filterdir;

    public CaseViewer(File filterdir) {
        super();

        setBackground(null);

        setCursor(defaultCursor);

        magnifierWindow = new MagnifierWindow(this);
        magnifierWindow.setSize(MAGNIFIER_SIZE, MAGNIFIER_SIZE);

        this.filterdir = filterdir;

        setLayout(layout);

        addMouseListener(mouseListener);

        addMouseMotionListener(mouseMotionListener);
    }

    public void setCase(Case c) {
        theCase = c;

        views[0] = new OneView(theCase.getRightCC(), "RCC", theCase
                .getMaximumHeight());
        views[1] = new OneView(theCase.getLeftCC(), "LCC", theCase
                .getMaximumHeight());
        views[2] = new OneView(theCase.getRightML(), "RML", theCase
                .getMaximumHeight());
        views[3] = new OneView(theCase.getLeftML(), "LML", theCase
                .getMaximumHeight());

        removeAll();
        add(leftSearchResults, new Integer(10));
        add(rightSearchResults, new Integer(10));

        leftSearchResults.setVisible(false);
        rightSearchResults.setVisible(false);

        for (OneView o : views) {
            add(o, JLayeredPane.DEFAULT_LAYER);
            o.addMouseListener(mouseListener);
            o.addMouseMotionListener(mouseMotionListener);
        }

        // add layout constraints

        // left-to-right
        layout.putConstraint(SpringLayout.WEST, views[0], 1, SpringLayout.WEST,
                this);
        layout.putConstraint(SpringLayout.WEST, views[1], SPACING,
                SpringLayout.EAST, views[0]);
        layout.putConstraint(SpringLayout.WEST, views[2], SPACING,
                SpringLayout.EAST, views[1]);
        layout.putConstraint(SpringLayout.WEST, views[3], SPACING,
                SpringLayout.EAST, views[2]);
        layout.putConstraint(SpringLayout.EAST, this, 1, SpringLayout.EAST,
                views[3]);

        // connect bottom
        layout.putConstraint(SpringLayout.SOUTH, views[1], 0,
                SpringLayout.SOUTH, views[0]);
        layout.putConstraint(SpringLayout.SOUTH, views[2], 0,
                SpringLayout.SOUTH, views[1]);
        layout.putConstraint(SpringLayout.SOUTH, views[3], 0,
                SpringLayout.SOUTH, views[2]);
        layout.putConstraint(SpringLayout.SOUTH, this, 1, SpringLayout.SOUTH,
                views[3]);

        // must set NORTH edges last in Java 5, so that the HEIGHT is
        // unconstrained instead of y
        // compare SpringLayout.Constraints in Java 5 to Java 6, where
        // this ordering is not as strange

        // connect top
        for (OneView v : views) {
            layout.putConstraint(SpringLayout.NORTH, v, 1, SpringLayout.NORTH,
                    this);
        }

        // connect up the search things
        layout.putConstraint(SpringLayout.EAST, leftSearchResults, 0,
                SpringLayout.EAST, views[1]);
        layout.putConstraint(SpringLayout.WEST, leftSearchResults, 0,
                SpringLayout.WEST, views[0]);
        layout.putConstraint(SpringLayout.SOUTH, leftSearchResults, 0,
                SpringLayout.SOUTH, views[0]);
        layout.putConstraint(SpringLayout.NORTH, leftSearchResults, 0,
                SpringLayout.NORTH, views[0]);

        layout.putConstraint(SpringLayout.EAST, rightSearchResults, 0,
                SpringLayout.EAST, views[3]);
        layout.putConstraint(SpringLayout.WEST, rightSearchResults, 0,
                SpringLayout.WEST, views[2]);
        layout.putConstraint(SpringLayout.SOUTH, rightSearchResults, 0,
                SpringLayout.SOUTH, views[0]);
        layout.putConstraint(SpringLayout.NORTH, rightSearchResults, 0,
                SpringLayout.NORTH, views[0]);

        revalidate();
        repaint();

        magnifierWindow.repaint();
    }

    protected void updateMagnifierPosition(MouseEvent e) {
        Point p = new Point(e.getX(), e.getY());
        SwingUtilities.convertPointToScreen(p, e.getComponent());
        magnifierWindow.setLocation(p.x - MAGNIFIER_SIZE / 2, p.y
                - MAGNIFIER_SIZE / 2);
        magnifierWindow.repaint();
    }

    public OneView[] getViews() {
        return views;
    }

    public void startSearch(OneView view, ROI r) {
        System.out.println("start search");
        boolean searchPanelOnRight = (view == views[0] || view == views[1]);

        Search search = Search.getSharedInstance();
        // TODO fill in search parameters
        search.setScope(ScopeSource.getPredefinedScopeList().get(0));
        search.setSearchlet(prepareSearchlet(r));

        SearchPanel s;
        if (searchPanelOnRight) {
            s = rightSearchResults;
        } else {
            s = leftSearchResults;
        }

        s.beginSearch(search);
    }

    private Searchlet prepareSearchlet(ROI r) {
        Searchlet s = new Searchlet();

        File f = new File(filterdir, "libfil_euclidian.a");
        // File f = new File(filterdir, "fil_rgb.a");
        try {
            // TODO other types

            double data[] = r.getEuclidianData();
            String args[] = new String[data.length];
            for (int i = 0; i < data.length; i++) {
                args[i] = Double.toString(data[i]);
            }

            FilterCode fc = new FilterCode(new FileInputStream(f));
            Filter ff = new Filter("filter", fc, "f_eval_euclidian",
                    "f_init_euclidian", "f_fini_euclidian", 60,
                    new String[] {}, args, 1);
            // Filter ff = new Filter("filter", fc, "f_eval_img2rgb",
            // "f_init_img2rgb", "f_fini_img2rgb", 60,
            // new String[] {}, new String[] {}, 1);
            s.addFilter(ff);

            s.setApplicationDependencies(new String[] { "filter" });

            System.out.println(s);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return s;
    }

    public void setSelectedResult(MassResult result) {
        System.out.println(result);
    }

    // @Override
    // protected void paintComponent(Graphics g) {
    // super.paintComponent(g);
    // for (int i = 0; i < views.length; i++) {
    // for (int j = 1; j < views.length; j++) {
    // System.out.println(i + "-" + j + ": "
    // + (views[i].getScale() - views[j].getScale()));
    // }
    // }
    // System.out.println();
    // }
}
