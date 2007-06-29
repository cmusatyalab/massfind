package edu.cmu.cs.diamond.rsna2007;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.cmu.cs.diamond.opendiamond.Filter;
import edu.cmu.cs.diamond.opendiamond.FilterCode;
import edu.cmu.cs.diamond.opendiamond.Scope;
import edu.cmu.cs.diamond.opendiamond.Search;
import edu.cmu.cs.diamond.opendiamond.Searchlet;

public class CaseViewer extends JLayeredPane {
    private final static int SPACING = 10;

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

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == 1) {
                if (e.getClickCount() == 1) {
                    // region
                    Component c = e.getComponent();

                    if (c instanceof OneView) {
                        OneView ov = (OneView) c;
                        Point ip = ov.getImagePoint(e.getPoint());

                        ROI roi = getContour(ip, ov.getImageFilename());
                        System.out.println(roi == null ? "no roi" : roi
                                .getCenter());
                        ov.setROI(roi);
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

    private boolean searchPanelOnRight;

    private int magY;

    private int magX;

    final private Scope scope;

    final protected JSlider thresholdSlider = new JSlider(0, 100, 60);

    protected SearchType searchType;

    final private String regionFinderExe;

    final private JCheckBoxMenuItem visSizeCheckbox = new JCheckBoxMenuItem(
            "ROI Size");

    final private JCheckBoxMenuItem visCircularityCheckbox = new JCheckBoxMenuItem(
            "ROI Circularity");

    final private JCheckBoxMenuItem visShapeFactorCheckbox = new JCheckBoxMenuItem(
            "Shape Factor");

    final private JSpinner visSizeMin = new JSpinner(new SpinnerNumberModel(
            1.0, 0.0, 1.0, 0.1));

    final private JSpinner visSizeMax = new JSpinner(new SpinnerNumberModel(
            1.0, 1.0, 10.0, 0.1));

    final private JSpinner visCircularityMin = new JSpinner(
            new SpinnerNumberModel(1.0, 0.0, 1.0, 0.1));

    final private JSpinner visCircularityMax = new JSpinner(
            new SpinnerNumberModel(1.0, 1.0, 10.0, 0.1));

    final private JSpinner visShapeFactorMin = new JSpinner(
            new SpinnerNumberModel(1.0, 0.0, 1.0, 0.1));

    final private JSpinner visShapeFactorMax = new JSpinner(
            new SpinnerNumberModel(1.0, 1.0, 10.0, 0.1));

    public CaseViewer(File filterdir, File regionFinderExe, Scope scope)
            throws IOException {
        super();

        this.scope = scope;

        this.regionFinderExe = regionFinderExe.getCanonicalPath();

        setBackground(null);

        setCursor(defaultCursor);

        magnifierWindow = new MagnifierWindow(this);

        this.filterdir = filterdir;

        setLayout(layout);

        addMouseListener(mouseListener);

        addMouseMotionListener(mouseMotionListener);

        for (int i = 0; i < views.length; i++) {
            OneView o = new OneView();
            o.addMouseListener(mouseListener);
            o.addMouseMotionListener(mouseMotionListener);
            JPopupMenu popup = makePopup(o);
            o.setComponentPopupMenu(popup);
            views[i] = o;
        }
    }

    protected ROI getContour(Point ip, String imageFilename) {
        File f = new File(imageFilename);
        ProcessBuilder pb = new ProcessBuilder(regionFinderExe, f.getName(),
                "-cx", Integer.toString(ip.x), "-cy", Integer.toString((ip.y)));
        pb.directory(f.getParentFile());
        try {
            Process p = pb.start();
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return interpretContourResult(new File(imageFilename.substring(0,
                    imageFilename.lastIndexOf(".img"))
                    + ".txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ROI interpretContourResult(File file) {
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(file));
            String line;

            line = r.readLine();
            if (line == null) {
                return null;
            }

            if (line.startsWith(" ***")) {
                // failed
                return null;
            }

            // success

            // read data
            double data[] = new double[50];
            int i = 0;
            do {
                StringTokenizer st = new StringTokenizer(line);
                while (st.hasMoreTokens()) {
                    try {
                        data[i] = Double.parseDouble(st.nextToken());
                    } catch (NumberFormatException e) {
                        data[i] = Double.NaN;
                    }
                    i++;
                }
                line = r.readLine();
                if (line == null) {
                    return null;
                }
            } while (i < 50);

            // read contour
            int numContour = Integer.parseInt(line.trim());
            double cx[] = new double[numContour];
            double cy[] = new double[numContour];

            for (i = 0; i < numContour; i++) {
                // this will fail with NPE on early EOF
                line = r.readLine();
                if (line == null) {
                    return null;
                }
                StringTokenizer st = new StringTokenizer(line);
                cx[i] = Double.parseDouble(st.nextToken());
                cy[i] = Double.parseDouble(st.nextToken());
            }

            return new ROI(data, cx, cy, null);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JPopupMenu makePopup(final OneView ov) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem m;

        m = new JMenuItem("Search");
        m.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doSearch(ov);
            }
        });
        popup.add(m);

        popup.addSeparator();

        ButtonGroup searchGroup = new ButtonGroup();
        JRadioButtonMenuItem mr;
        JMenu subpop = new JMenu("Distance Metric");
        mr = createTypeMenu("Euclidian", SearchType.SEARCH_TYPE_EUCLIDIAN,
                searchGroup);
        mr.setSelected(true);
        subpop.add(mr);

        mr = createTypeMenu("Boosted Learned",
                SearchType.SEARCH_TYPE_BOOSTED_LEARNED, searchGroup);
        subpop.add(mr);

        mr = createTypeMenu("Query Adaptive Learned",
                SearchType.SEARCH_TYPE_QUERY_ADAPTIVE_LEARNED, searchGroup);
        subpop.add(mr);

        popup.add(subpop);

        subpop = new JMenu("Search Threshold");
        thresholdSlider.setMajorTickSpacing(20);
        thresholdSlider.setMinorTickSpacing(5);
        thresholdSlider.setPaintTicks(true);
        thresholdSlider.setPaintLabels(true);
        subpop.add(thresholdSlider);
        popup.add(subpop);

        subpop = new JMenu("Visual Constraints");
        subpop.add(visSizeCheckbox);
        subpop.add(visSizeMin);
        subpop.add(visSizeMax);
        subpop.addSeparator();
        subpop.add(visCircularityCheckbox);
        subpop.add(visCircularityMin);
        subpop.add(visCircularityMax);
        subpop.addSeparator();
        subpop.add(visShapeFactorCheckbox);
        subpop.add(visShapeFactorMin);
        subpop.add(visShapeFactorMax);

        popup.add(subpop);

        popup.addSeparator();

        m = new JMenuItem("Exit");
        m.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        popup.add(m);
        return popup;
    }

    public void doSearch(OneView ov) {
        // search
        ROI r = ov.getROI();

        if (r != null) {
            // start a search
            startSearch(ov, r);
            return;
        }
    }

    private JRadioButtonMenuItem createTypeMenu(String text,
            final SearchType type, ButtonGroup searchGroup) {
        final JRadioButtonMenuItem mr;
        mr = new JRadioButtonMenuItem(text);
        mr.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (mr.isSelected()) {
                    searchType = type;
                }
            }
        });
        searchGroup.add(mr);
        return mr;
    }

    public void setCase(Case theCase) {
        views[0].setView(theCase.getRightCC(), "RCC", theCase
                .getMaximumHeight());
        views[1]
                .setView(theCase.getLeftCC(), "LCC", theCase.getMaximumHeight());
        views[2].setView(theCase.getRightML(), "RML", theCase
                .getMaximumHeight());
        views[3]
                .setView(theCase.getLeftML(), "LML", theCase.getMaximumHeight());

        removeAll();
        add(leftSearchResults, Integer.valueOf(10));
        add(rightSearchResults, Integer.valueOf(10));

        leftSearchResults.setVisible(false);
        rightSearchResults.setVisible(false);

        for (OneView o : views) {
            add(o, JLayeredPane.DEFAULT_LAYER);
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
        updateMagnifierPosition();
    }

    protected void updateMagnifierPosition() {
        magnifierWindow.setMagnifyPoint(magX, magY);
        magnifierWindow.repaint();
    }

    protected void updateMagnifierPosition(MouseEvent e) {
        updateMagnifierPosition(e.getX(), e.getY(), e.getComponent());
    }

    protected void updateMagnifierPosition(int x, int y, Component c) {
        Point p = new Point(x, y);
        SwingUtilities.convertPointToScreen(p, c);
        magX = p.x;
        magY = p.y;
        updateMagnifierPosition();
    }

    public OneView[] getViews() {
        return views;
    }

    public void startSearch(OneView view, ROI r) {
        System.out.println("start search");
        searchPanelOnRight = (view == views[0] || view == views[1]);

        Search search = Search.getSharedInstance();
        // TODO fill in search parameters
        search.setScope(scope);
        search.setSearchlet(prepareSearchlet(r));

        SearchPanel s;
        if (searchPanelOnRight) {
            s = rightSearchResults;
        } else {
            s = leftSearchResults;
        }

        s.beginSearch(search);
    }

    public enum SearchType {
        SEARCH_TYPE_EUCLIDIAN, SEARCH_TYPE_BOOSTED_LEARNED, SEARCH_TYPE_QUERY_ADAPTIVE_LEARNED
    }

    private Searchlet prepareSearchlet(ROI r) {
        Searchlet s = new Searchlet();

        try {
            String filename;
            double data[];
            String functionName;

            switch (searchType) {
            case SEARCH_TYPE_EUCLIDIAN:
                filename = "libfil_euclidian.a";
                data = r.getEuclidianData();
                functionName = "euclidian";
                break;
            case SEARCH_TYPE_BOOSTED_LEARNED:
                filename = "libfil_boostldm.a";
                data = r.getBoostedData();
                functionName = "boostldm";
                break;
            case SEARCH_TYPE_QUERY_ADAPTIVE_LEARNED:
                filename = "libfil_qaldm.a";
                data = r.getEuclidianData();
                functionName = "qaldm";
                break;
            default:
                filename = null;
                data = null;
                functionName = null;
            }

            File f = new File(filterdir, filename);
            String args[] = new String[data.length];
            for (int i = 0; i < data.length; i++) {
                args[i] = Double.toString(data[i]);
            }

            FilterCode fc = new FilterCode(new FileInputStream(f));
            Filter ff = new Filter("filter", fc, "f_eval_" + functionName,
                    "f_init_" + functionName, "f_fini_" + functionName,
                    thresholdSlider.getValue(), new String[] {}, args, 1);
            s.addFilter(ff);

            if (visSizeCheckbox.isSelected()
                    || visCircularityCheckbox.isSelected()
                    || visShapeFactorCheckbox.isSelected()) {
                // visual
                double visArgs[] = new double[6];
                for (int i = 0; i < visArgs.length; i++) {
                    visArgs[i] = -1;
                }

                if (visSizeCheckbox.isSelected()) {
                    visArgs[0] = (Double) visSizeMin.getValue();
                    visArgs[1] = (Double) visSizeMax.getValue();
                }
                if (visCircularityCheckbox.isSelected()) {
                    visArgs[2] = (Double) visCircularityMin.getValue();
                    visArgs[3] = (Double) visCircularityMax.getValue();
                }
                if (visShapeFactorCheckbox.isSelected()) {
                    visArgs[4] = (Double) visShapeFactorMin.getValue();
                    visArgs[5] = (Double) visShapeFactorMax.getValue();
                }

                double rawData[] = r.getRawData();
                String visArgsStr[] = new String[rawData.length
                        + visArgs.length];
                for (int i = 0; i < rawData.length; i++) {
                    visArgsStr[i] = Double.toString(rawData[i]);
                }
                for (int i = 0; i < visArgs.length; i++) {
                    visArgsStr[i + rawData.length] = Double
                            .toString(visArgs[i]);
                }

                f = new File(filterdir, "libfil_visual.a");
                fc = new FilterCode(new FileInputStream(f));
                ff = new Filter("visual", fc, "f_eval_visual", "f_init_visual",
                        "f_fini_visual", 1, new String[0], visArgsStr, 1);
                s.addFilter(ff);
            }

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
        magnifierWindow.setExtraResult(result, searchPanelOnRight);
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
