package edu.cmu.cs.diamond.rsna2007;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class SearchPanel extends JPanel {

    final private JList list;

    public SearchPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());

        setBorder(BorderFactory.createTitledBorder(null, "Search Results",
                TitledBorder.CENTER, TitledBorder.TOP, null, Color.WHITE));

        String data[] = new String[100000];
        for (int i = 0; i < data.length; i++) {
            data[i] = Integer.toString(i);
        }
        
        SearchListCellRenderer cr = new SearchListCellRenderer();
        list = new JList(data);
        list.setCellRenderer(cr);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(-1);
        
        list.setOpaque(false);
        list.setBackground(null);
        list.setForeground(Color.WHITE);

        JScrollPane jsp = new JScrollPane(list,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setOpaque(false);
        jsp.setBackground(null);

        JViewport v = jsp.getViewport();
        v.setOpaque(false);
        v.setBackground(null);

        add(jsp);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 0, 0, 0.8f));

        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}
