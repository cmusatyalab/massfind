package edu.cmu.cs.diamond.massfind2;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ROI {
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

    final private double rawf[] = new double[50];

    final private double edmf[] = new double[38];

    final private double bdmf[] = new double[50];

    final private BufferedImage img;

    public ROI(double data[], double contourX[], double contourY[], double norm[],
            BufferedImage img) {
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
        this.img = img;
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
