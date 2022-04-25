package edu.hitsz.application;

import edu.hitsz.swing.chooseDifficulty;
import edu.hitsz.swing.scoreList;

import javax.swing.*;
import java.awt.*;

public class FrameThread {
    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

    private JFrame frame;
    private boolean chooseFinish = false;
    private Game game;

    public FrameThread(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.frame = new JFrame("Aircraft War");
        this.frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.frame.setResizable(false);
        //设置窗口的大小和位置,居中放置
        this.frame.setBounds(((int) screenSize.getWidth() - WINDOW_WIDTH) / 2, 0,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.game = new Game();
    }

    // 启动游戏页面
    public void gameWork(){
        boolean actionflag = false;// game.action未开启
        while(true) {
            synchronized (this) {
                if (game.isGameOverFlag()) {
                    notifyAll();
                }
                //如果还未选择模式或游戏结束，则等待
                if (!chooseFinish || (chooseFinish && game.isGameOverFlag())) {
                    try {
                        wait();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!game.isGameOverFlag() && !actionflag) {
                    actionflag = true;
                    frame.setContentPane(game);
                    frame.setVisible(true);
                    game.action();
                }
            }
        }
    }

    // 选择模式页面
    public void chooseWork(){
        chooseDifficulty c = new chooseDifficulty();
        frame.setContentPane(c.getMainPanel());
        frame.setVisible(true);

        while(true){
            synchronized (this){
                // 如果已经选择模式，则等待
                if(chooseFinish){
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(chooseDifficulty.getMode() != 0){
                chooseFinish = true;
                synchronized (this){
                    notify();
                }
            }
        }
    }

    // 得分榜页面
    public void showScoreWork(){
        boolean showFlag = false;   // 标志得分榜页面是否已经显示
        while(true){
            synchronized (this){
                if(!game.isGameOverFlag() || !chooseFinish  ){ // 游戏未结束或还没选择模式，等待
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(!showFlag){  // 还没显示过得分榜就显示
                    scoreList c = new scoreList();
                    frame.setContentPane(c.getMainPanel());
                    frame.setVisible(true);
                    showFlag = true;
                }

            }
            synchronized (this){
                notify();
            }
        }
    }

}
