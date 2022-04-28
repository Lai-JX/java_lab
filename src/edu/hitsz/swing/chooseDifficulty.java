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
    private JLabel soundLabel;

    public static int getMode() {
        return mode;
    }

    private static int mode = 0; // 1表示简单模式，2表示普通模式，3表示困难模式

    public static boolean isSoundOpen() {
        return soundOpen;
    }

    private static boolean soundOpen = true;

    public chooseDifficulty() {
        simpleModel.addActionListener(new ActionListener (){
            @Override
            public void actionPerformed(ActionEvent e){
                chooseDifficulty.mode = 1;
                System.out.println("mode=1(简单模式)");
                System.out.println("音效开启："+chooseDifficulty.soundOpen);
            }
        });
        commonModel.addActionListener(new ActionListener (){
            @Override
            public void actionPerformed(ActionEvent e){
                chooseDifficulty.mode = 2;
                System.out.println("mode=2(普通模式)");
                System.out.println("音效开启："+chooseDifficulty.soundOpen);
            }
        });
        difficultModel.addActionListener(new ActionListener (){
            @Override
            public void actionPerformed(ActionEvent e){
                chooseDifficulty.mode = 3;
                System.out.println("mode=3(困难模式)");
                System.out.println("音效开启："+chooseDifficulty.soundOpen);
            }
        });
        chooseBox.addActionListener(new ActionListener (){
            @Override
            public void actionPerformed(ActionEvent e){
                JComboBox cb = (JComboBox)e.getSource();
                String selected = (String) cb.getSelectedItem();
                if(selected == "关"){
                    chooseDifficulty.soundOpen = false;
                }else{
                    chooseDifficulty.soundOpen = true;
                }
            }
        });
    }

    public JPanel getMainPanel() {
        return MainPanel;
    }

}
//class simpleAction implements ActionListener {
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        chooseDifficulty.mode = 1;
//        System.out.println("mode=1(简单)");
//        System.out.println("音效开启："+chooseDifficulty.soundOpen);
//    }
//}
//class commonAction implements ActionListener {
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        chooseDifficulty.mode = 2;
//        System.out.println("mode=2(普通)");
//        System.out.println("音效开启："+chooseDifficulty.soundOpen);
//    }
//}
//class difficultAction implements ActionListener {
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        chooseDifficulty.mode = 3;
//        System.out.println("mode=3(困难)");
//        System.out.println("音效开启："+chooseDifficulty.soundOpen);
//    }
//}
//class soundAction implements ActionListener {
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        JComboBox cb = (JComboBox)e.getSource();
//        String selected = (String) cb.getSelectedItem();
//        if(selected == "关"){
//            chooseDifficulty.soundOpen = false;
//        }else{
//            chooseDifficulty.soundOpen = true;
//        }
//
//    }
//}