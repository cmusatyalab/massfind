package edu.cmu.cs.diamond.rsna2007;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import edu.cmu.cs.diamond.opendiamond.Result;
import edu.cmu.cs.diamond.opendiamond.Util;

public class ThumbnailedResult extends Result {
    final private Result theResult;

    final private BufferedImage thumbnail;

    public ThumbnailedResult(Result r, int size) {
        theResult = r;

        BufferedImage b = null;
        try {
            b = ImageIO.read(new ByteArrayInputStream(r.getData()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (b == null) {
            thumbnail = null;
        } else if (b.getWidth() < size && b.getHeight() < size) {
            double scale = Util.getScaleForResize(b.getWidth(), b.getHeight(),
                    size, size);
            thumbnail = Util.scaleImage(b, scale);
        } else {
            thumbnail = GraphicsUtilities.createThumbnail(b, size);
        }
    }

    public BufferedImage getThumbnail() {
        return thumbnail;
    }

    @Override
    public byte[] getData() {
        return theResult.getData();
    }

    @Override
    public List<String> getKeys() {
        return theResult.getKeys();
    }

    @Override
    public String getObjectName() {
        return theResult.getObjectName();
    }

    @Override
    public String getServerName() {
        return theResult.getServerName();
    }

    @Override
    public byte[] getValue(String key) {
        return theResult.getValue(key);
    }
}
