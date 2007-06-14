package edu.cmu.cs.diamond.rsna2007;

import java.awt.Component;

import javax.swing.*;
import javax.swing.border.Border;

import edu.cmu.cs.diamond.opendiamond.Util;

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
        ThumbnailedResult r = (ThumbnailedResult) value;
        int similarity = Util.extractInt(r.getValue("similarity"));
        String text = Integer.toString(similarity);

        c.setText(text);

        Icon icon = new ImageIcon(r.getThumbnail());

        c.setIcon(icon);

        c.setBorder(border);

        c.setOpaque(isSelected);

        c.setToolTipText("Similarity: " + text);

        return c;
    }
}
