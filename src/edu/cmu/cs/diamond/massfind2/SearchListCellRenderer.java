/*
 * MassFind 2: A Diamond application for exploration of breast tumors
 *
 * Copyright (c) 2007-2008 Carnegie Mellon University. All rights reserved.
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
