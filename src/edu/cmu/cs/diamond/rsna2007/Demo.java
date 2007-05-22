package edu.cmu.cs.diamond.rsna2007;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;

public class Demo extends JFrame {
    final private CaseViewer caseViewer = new CaseViewer();
    
    public static void main(String[] args) {
        Demo m;
        try {
            m = new Demo(new File(args[0]));
            m.setLocationByPlatform(true);
            m.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            m.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    final private List<Case> cases = new ArrayList<Case>();

    public Demo(File file) throws IOException {
        super("Diamond RSNA 2007");
        
        readIndex(file);

        setupWindow();
        
        // load initial case
        
        pack();
    }

    
    
    private void setupWindow() {
        add(caseViewer);
        
        Box h = Box.createHorizontalBox();

        JButton p = new JButton("Previous Case");
        JButton n = new JButton("Next Case");
        
        h.add(Box.createHorizontalGlue());
        h.add(p);
        h.add(Box.createHorizontalGlue());
        h.add(n);
        h.add(Box.createHorizontalGlue());
        
        add(h, BorderLayout.SOUTH);
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
            
            if (name != null && lcc != null && lml != null && rcc != null && rml != null) {
                System.out.println("Adding case " + name);
                cases .add(new Case(rcc, lcc, rml, lml, name));
                name = null;
                lcc = lml = rcc = rml = null;
            }
        }
    }
}
