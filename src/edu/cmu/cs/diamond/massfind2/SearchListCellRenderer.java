package edu.cmu.cs.diamond.massfind2;

import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class SearchListCellRenderer extends DefaultListCellRenderer {
    final private static Border border = BorderFactory.createEmptyBorder(10,
            10, 10, 10);

    public SearchListCellRenderer() {
        super();
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.BOTTOM);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        DefaultListCellRenderer c = (DefaultListCellRenderer) super
                .getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
        MassResult r = (MassResult) value;

        int similarity = r.getSimilarity();

        String text = Integer.toString(similarity);

        c.setText(text);

        BufferedImage thumb = r.getThumbnail();

        c.setIcon(new ImageIcon(thumb));

        c.setBorder(border);

        c.setOpaque(isSelected);

        c.setToolTipText((r.isMalignant() ? "Malignant" : "Benign")
                + ", Similarity: " + text);

        return c;
    }
}
