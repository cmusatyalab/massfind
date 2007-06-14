package edu.cmu.cs.diamond.rsna2007;

import java.awt.Component;

import javax.swing.*;
import javax.swing.border.Border;

public class SearchListCellRenderer extends DefaultListCellRenderer {
    final private static Icon icon = new ImageIcon("/home/adam/adam-zz.png");

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
        c.setIcon(icon);

        c.setBorder(border);

        c.setOpaque(isSelected);
        
        c.setToolTipText(value.toString());
        
        return c;
    }
}
