package edu.cmu.cs.diamond.rsna2007;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Demo extends JFrame {
    final private CaseViewer caseViewer = new CaseViewer();

    final private List<Case> cases = new ArrayList<Case>();

    final private JButton prevButton = new JButton("Previous Case");

    final private JButton nextButton = new JButton("Next Case");

    protected int currentCase = 0;

    final private JLabel caseLabel = new JLabel();

    public static void main(String[] args) {
        Demo m;

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

        try {
            m = new Demo(new File(args[0]));
            m.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            m.setUndecorated(true);
            m.setResizable(false);

            DisplayMode dm = gd.getDisplayMode();
            m.setBounds(0, 0, dm.getWidth(), dm.getHeight());
            m.setVisible(true);
            m.toFront();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Demo(File file) throws IOException {
        super("Diamond RSNA 2007");
        
        setMinimumSize(new Dimension(640, 480));

        setBackground(Color.BLACK);
        
        getContentPane().setBackground(Color.BLACK);

        readIndex(file);

        setupWindow();

        updateButtonAndCaseState();
    }

    private void setupWindow() {
        add(caseViewer);

        Box h = Box.createHorizontalBox();

        prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentCase--;

                updateButtonAndCaseState();
            }
        });

        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentCase++;

                updateButtonAndCaseState();
            }
        });

        caseLabel.setForeground(Color.WHITE);
        
        h.add(Box.createHorizontalGlue());
        h.add(prevButton);
        h.add(Box.createHorizontalGlue());
        h.add(caseLabel);
        h.add(Box.createHorizontalGlue());
        h.add(nextButton);
        h.add(Box.createHorizontalGlue());

        add(h, BorderLayout.SOUTH);
    }

    protected void updateButtonAndCaseState() {
        prevButton.setEnabled(currentCase != 0);
        nextButton.setEnabled(currentCase != cases.size() - 1);

        Case c = cases.get(currentCase);
        caseLabel.setText(c.getName());
        caseViewer.setCase(c);

        validate();
    }

    private void readIndex(File file) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        File dir = file.getParentFile();
        String line;

        System.out.println("Reading index file: " + file);

        BufferedImage rml = null, lml = null, rcc = null, lcc = null;
        String name = null;

        Pattern p = Pattern.compile("(.*)(LCC|LML|RCC|RML)(.*)\\.jpg");

        while ((line = reader.readLine()) != null) {
            Matcher m = p.matcher(line);
            if (m.matches()) {
                if (name == null) {
                    name = m.group(1) + "xxx" + m.group(3);
                }

                String view = m.group(2).intern();
                if (view == "LCC") {
                    lcc = ImageIO.read(new File(dir, line));
                } else if (view == "LML") {
                    lml = ImageIO.read(new File(dir, line));
                } else if (view == "RCC") {
                    rcc = ImageIO.read(new File(dir, line));
                } else if (view == "RML") {
                    rml = ImageIO.read(new File(dir, line));
                }
            }

            if (name != null && lcc != null && lml != null && rcc != null
                    && rml != null) {
                System.out.println("Adding case " + name);
                cases.add(new Case(rcc, lcc, rml, lml, name));
                name = null;
                lcc = lml = rcc = rml = null;
            }
        }
    }
}
