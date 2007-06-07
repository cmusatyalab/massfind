package edu.cmu.cs.diamond.rsna2007;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class SearchPanel extends JPanel {

    public SearchPanel() {
        setOpaque(false);

        setBorder(BorderFactory.createTitledBorder(null, "Search Results",
                TitledBorder.CENTER, TitledBorder.TOP, null, Color.WHITE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 0, 0, 0.8f));

        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}
