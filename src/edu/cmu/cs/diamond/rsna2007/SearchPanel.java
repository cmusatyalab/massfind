package edu.cmu.cs.diamond.rsna2007;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.cmu.cs.diamond.opendiamond.Search;

public class SearchPanel extends JPanel {

    final private JList list;

    protected Search theSearch;

    public SearchPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());

        setBorder(BorderFactory.createTitledBorder(null, "Search Results",
                TitledBorder.CENTER, TitledBorder.TOP, null, Color.WHITE));

        SearchListCellRenderer cr = new SearchListCellRenderer();
        list = new JList();
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

        JButton closeButton = new JButton("Close Search");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        add(closeButton, BorderLayout.SOUTH);
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (!aFlag && theSearch != null) {
            theSearch.stop();
        }
    }

    void beginSearch(Search s) {
        if (theSearch != null) {
            theSearch.stop();
        }

        theSearch = s;

        list.setModel(new SearchModel(theSearch, 12));

        theSearch.start();

        setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 0, 0, 0.8f));

        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}
