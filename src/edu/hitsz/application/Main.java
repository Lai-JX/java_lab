package edu.hitsz.application;

import edu.hitsz.swing.chooseDifficulty;

import javax.swing.*;
import java.awt.*;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {

    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Hello Aircraft War");




        // 获得屏幕的分辨率，初始化 Frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("Aircraft War");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(false);
        //设置窗口的大小和位置,居中放置
        frame.setBounds(((int) screenSize.getWidth() - WINDOW_WIDTH) / 2, 0,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//        chooseModel(frame);
        // 主程序等待用户选择模式和音效
//        Thread.currentThread().wait();



        Game game = new Game();
        frame.add(game);
        frame.setVisible(true);
        game.action();
    }

//    public static void chooseModel(JFrame frame){
//        Runnable choose =() ->{
//          new chooseDifficulty().choose(frame);
//        };
//        new Thread(choose).start();
//    }
}
