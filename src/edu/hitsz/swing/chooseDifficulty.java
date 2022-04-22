package edu.hitsz.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class chooseDifficulty {
    private JPanel MainPanel;
    private JPanel topPanel;
    private JPanel buttomPanel;
    private JComboBox chooseBox;
    private JButton simpleModel;
    private JButton commonModel;
    private JButton difficultModel;

    public chooseDifficulty() {
        simpleModel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        commonModel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        difficultModel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        chooseBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public void choose(JFrame frame ) {
//        JFrame currentFrame = new JFrame("chooseDifficulty");
        frame.setContentPane(new chooseDifficulty().MainPanel);
        frame.setPreferredSize(new Dimension(512,768));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
