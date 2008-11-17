/*
 * MassFind 2: A Diamond application for exploration of breast tumors
 *
 * Copyright (c) 2007-2008 Carnegie Mellon University. All rights reserved.
 * Copyright (c) 2007-2008 Intel Corporation. All rights reserved.
 * Additional copyrights may be listed below.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution in the file named LICENSE.
 *
 * Technical and financial contributors are listed in the file named
 * CREDITS.
 */

package edu.cmu.cs.diamond.massfind2;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ROI {
    final static public int NUM_RAW_FEATURES = 50;

    final static public int NUM_UPMC_FEATURES = 38;

    final static public int NUM_BOOSTLDM_FEATURES = 216;

    // average pixel value of whole breast area
    final static public int UPMC_AVG_PIXEL_VALUE = 0;

    // peak value in histogram of breast area
    final static public int UPMC_PEAK_PIXEL_VALUE = 1;

    // pixel value corresponding to maximum value in histogram
    final static public int UPMC_MAX_PIXEL_VALUE = 2;

    // pixel value standard deviation of histogram
    final static public int UPMC_SDEV_PIXEL_VALUE = 3;

    // pixel value skewness of histogram
    final static public int UPMC_SKEW_PIXEL_VALUE = 4;

    // pixel value kurtosis of histogram
    final static public int UPMC_KURTOSIS_PIXEL_VALUE = 5;

    // average local noise (pixel value fluctuation) of breast area
    final static public int UPMC_AVG_NOISE = 6;

    // standard deviation of noise of breast area
    final static public int UPMC_SDEV_NOISE = 7;

    // skewness of noise of breast area
    final static public int UPMC_SKEW_NOISE = 8;

    // type of digitizer
    final static public int UPMC_DIGITIZER_TYPE = 9;

    // ROI features

    // region conspicuity
    final static public int UPMC_REGION_CONSPICUITY = 10;

    // region size
    final static public int UPMC_REGION_SIZE = 11;

    // region constrast
    final static public int UPMC_REGION_CONTRAST = 12;

    // average local noise (pixel value fluctuation)
    final static public int UPMC_AVG_LOCAL_NOISE = 13;

    // standard deviation of local noise
    final static public int UPMC_SDEV_LOCAL_NOISE = 14;

    // skewness of local noise
    final static public int UPMC_SKEW_LOCAL_NOISE = 15;

    // mean radial length/region size
    final static public int UPMC_RADIAL_LENGTH_SIZE = 16;

    // standard deviation of radial lengths
    final static public int UPMC_SDEV_RADIAL_LENGTHS = 17;

    // skewness of radial lengths
    final static public int UPMC_SKEW_RADIAL_LENGTHS = 18;

    // ratio between the maximum and minimal radial length
    final static public int UPMC_MIN_MAX_RADIAL_LENGTH = 19;

    // shape factor ratio (perimeter of boundary/region size)
    final static public int UPMC_SHAPE_FACTOR_RATIO = 20;

    // region circularity
    final static public int UPMC_REGION_CIRCULARITY = 21;

    // standard deviation of pixel values inside growth region
    final static public int UPMC_SDEV_REGION_PIXEL_VALUE = 22;

    // skewness of pixel values inside growth region
    final static public int UPMC_SKEW_REGION_PIXEL_VALUE = 23;

    // kurtosis of pixel values inside growth region
    final static public int UPMC_KURTOSIS_REGION_PIXEL_VALUE = 24;

    // average value of gradient of boundary contour pixels
    final static public int UPMC_AVG_BOUNDARY_GRADIENT = 25;

    // standard deviation of gradient of boundary contour pixels
    final static public int UPMC_SDEV_BOUNDARY_GRADIENT = 26;

    // skewness of gradient of boundary bontour pixels
    final static public int UPMC_SKEW_BOUNDARY_GRADIENT = 27;

    // standard deviation of pixel values in the surrounding background
    final static public int UPMC_SDEV_BACKGROUND = 28;

    // skewness of pixel values in surrounding background
    final static public int UPMC_SKEW_BACKGROUND = 29;

    // kurtosis of pixel values in surrounding background
    final static public int UPMC_KURTOSIS_BACKGROUND = 30;

    // average noise of pixel values in surrounding background
    final static public int UPMC_AVG_NOISE_BACKGROUND = 31;

    // standard deviation of pixel value noise in the surrounding background
    final static public int UPMC_SDEV_NOISE_BACKGROUND = 32;

    // skewness of pixel value noise in the surrounding background
    final static public int UPMC_SKEW_NOISE_BACKGROUND = 33;

    // ratio of number of "minimum" pixels inside growth region
    final static public int UPMC_NUM_MIN_PIXEL_RATIO = 34;

    // average pixel value depth of "minimum" pixels inside growth region
    final static public int UPMC_AVG_MIN_PIXEL_DEPTH = 35;

    // maximum pixel value depth of "minimum" pixels inside growth region
    final static public int UPMC_MAX_MIN_PIXEL_DEPTH = 36;

    // center position shift (distance between gravity center and local
    // minimum)/region size
    final static public int UPMC_CENTER_POSITION_SHIFT = 37;

    // center X coordinate of the region / sub sample rate (4)
    final static public int UPMC_CENTER_X = 38;

    // center Y coordinate of the region / 4
    final static public int UPMC_CENTER_Y = 39;

    // Boundary frame (top - Y/4)
    final static public int UPMC_BOUNDARY_FRAME_TOP = 40;

    // Boundary frame (bottom - Y/4)
    final static public int UPMC_BOUNDARY_FRAME_BOTTOM = 41;

    // Boundary frame (left - X/4)
    final static public int UPMC_BOUNDARY_FRAME_LEFT = 42;

    // Boundary frame (right - X/4)
    final static public int UPMC_BOUNDARY_FRAME_RIGHT = 43;

    // 44 empty
    // Average pixel value of mass region
    final static public int UPMC_AVG_PIXEL_MASS = 45;

    // Average pixel value of surrounding background
    final static public int UPMC_AVG_PIXEL_BACKGROUND = 46;

    // Pixel count of mass region
    final static public int UPMC_MASS_PIXEL_COUNT = 47;

    // 48 empty
    // 49 empty

    final private List<Point2D> contour;

    final private double rawf[] = new double[NUM_RAW_FEATURES];

    final private double edmf[] = new double[NUM_UPMC_FEATURES];

    final private double bdmf[] = new double[NUM_BOOSTLDM_FEATURES];

    private static final String FEATURE_AVG_DAT = "u_vector.dat";

    private static final String TRANSFORM_DAT = "w_matrix.dat";

    static private double u_vector[];

    static private double w_matrix[][];

    final private BufferedImage img;

    public ROI(double data[], double contourX[], double contourY[],
            double norm[], BufferedImage img) throws IOException {
        if (data.length != rawf.length) {
            throw new IllegalArgumentException("length of data given ("
                    + data.length + ") != " + rawf.length);
        }

        if (norm.length != edmf.length) {
            throw new IllegalArgumentException("length of norm given ("
                    + norm.length + ") != " + edmf.length);
        }

        System.arraycopy(data, 0, rawf, 0, rawf.length);
        System.arraycopy(norm, 0, edmf, 0, edmf.length);
        contour = makeContour(contourX, contourY);

        // calculate boosted LDM features
        if (u_vector == null) {
            u_vector = readFeatureAverages();
        }
        if (w_matrix == null) {
            w_matrix = readTransform();
        }
        computeBDMFeatures();

        this.img = img;
    }

    private double[] readFeatureAverages() throws IOException {
        double result[] = new double[NUM_UPMC_FEATURES];

        BufferedReader in = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream(
                        "resources/" + FEATURE_AVG_DAT)));
        for (int i = 0; i < NUM_UPMC_FEATURES; i++) {
            String str = in.readLine();
            StringTokenizer t = new StringTokenizer(str);
            Double val = new Double(t.nextToken());
            result[i] = val.doubleValue();
        }
        in.close();

        System.out.println("feature averages: " + Arrays.toString(result));

        return result;
    }

    private double[][] readTransform() throws IOException {
        // one extra row for thresholds
        double result[][] = new double[NUM_UPMC_FEATURES + 1][NUM_BOOSTLDM_FEATURES];

        BufferedReader in = new BufferedReader(new InputStreamReader(this
                .getClass().getResourceAsStream("resources/" + TRANSFORM_DAT)));
        for (int i = 0; i < NUM_UPMC_FEATURES + 1; i++) {
            String str = in.readLine();
            StringTokenizer t = new StringTokenizer(str);
            for (int j = 0; j < NUM_BOOSTLDM_FEATURES; j++) {
                Double val = new Double(t.nextToken());
                result[i][j] = val.doubleValue();
            }
        }
        in.close();

        System.out.println("transform: " + Arrays.toString(result));

        return result;
    }

    private void computeBDMFeatures() {
        // compute the projection of this ROI on w_matrix
        double x[] = new double[NUM_UPMC_FEATURES + 1];
        System.arraycopy(edmf, 0, x, 0, NUM_UPMC_FEATURES);
        // add a threshold value
        x[NUM_UPMC_FEATURES] = 1.0;

        // subtract out average feature values
        for (int i = 0; i < NUM_UPMC_FEATURES; i++) {
            x[i] -= u_vector[i];
        }

        // BDMF = X * W
        for (int i = 0; i < NUM_BOOSTLDM_FEATURES; i++) {
            for (int j = 0; j < NUM_UPMC_FEATURES + 1; j++) {
                bdmf[i] += x[j] * w_matrix[j][i];
            }

            // now binarize this value
            if (bdmf[i] > 0) {
                bdmf[i] = 1.0;
            } else {
                bdmf[i] = -1.0;
            }
        }
    }

    public ROI(InputStream in, BufferedImage img) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(in));

        String line;
        Pattern p = Pattern.compile("\"([^\"]+)\"=\"([^\"]+)\"");

        double contourX[] = null;
        double contourY[] = null;

        while ((line = r.readLine()) != null) {
            Matcher m = p.matcher(line);
            if (m.matches()) {
                String key = m.group(1);
                String val = m.group(2);

                if (key.startsWith("rawf")) {
                    int index = Integer.parseInt(key.substring(4));
                    rawf[index] = Double.parseDouble(val);
                } else if (key.startsWith("edmf")) {
                    int index = Integer.parseInt(key.substring(4));
                    edmf[index] = Double.parseDouble(val);
                } else if (key.startsWith("bdmf")) {
                    int index = Integer.parseInt(key.substring(4));
                    bdmf[index] = Double.parseDouble(val);
                } else if (key.equals("contourx")) {
                    contourX = readContour(val);
                } else if (key.equals("contoury")) {
                    contourY = readContour(val);
                }
            }
        }

        contour = makeContour(contourX, contourY);
        this.img = img;
    }

    private static List<Point2D> makeContour(double[] contourX,
            double[] contourY) {
        List<Point2D> p = new ArrayList<Point2D>();

        for (int i = 0; i < contourY.length; i++) {
            double x = contourX[i];
            double y = contourY[i];

            p.add(new Point2D.Double(x, y));
        }

        return p;
    }

    private double[] readContour(String val) {
        String ss[] = val.split(";");
        double result[] = new double[ss.length];

        for (int i = 0; i < ss.length; i++) {
            result[i] = Double.parseDouble(ss[i]);
        }

        return result;
    }

    public List<Point2D> getContour() {
        return contour;
    }

    public Point2D getCenter() {
        return new Point2D.Double(rawf[UPMC_CENTER_X] * 4,
                rawf[UPMC_CENTER_Y] * 4);
    }

    public double[] getRawData() {
        return getData(rawf);
    }

    private double[] getData(double data[]) {
        double d[] = new double[data.length];
        System.arraycopy(data, 0, d, 0, d.length);
        return d;
    }

    public double[] getEuclidianData() {
        return getData(edmf);
    }

    public double[] getBoostedData() {
        return getData(bdmf);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        for (int i = 0; i < rawf.length; i++) {
            sb.append(" " + i + ":" + rawf[i]);
        }
        sb.append(" ]");

        sb.append(" " + contour);

        return sb.toString();
    }

    public BufferedImage getImage() {
        return img;
    }
}
