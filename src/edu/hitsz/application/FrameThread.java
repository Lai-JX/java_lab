package edu.hitsz.application;

import edu.hitsz.swing.chooseDifficulty;
import edu.hitsz.swing.scoreList;

import javax.swing.*;
import java.awt.*;

public class FrameThread {
    private static final int WINDOW_WIDTH = 512;
    private static final int WINDOW_HEIGHT = 768;

    private JFrame frame;
    private boolean chooseFinish = false;
    private AbstractGame game;  // 游戏线程
    private chooseDifficulty c; // 选择模式
    private boolean showFlag = false;   // 标志得分榜页面是否已经显示
    private boolean actionflag = false;// game.action未开启
    private boolean chooseFlag = false; // 未选择模式


    public FrameThread(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.frame = new JFrame("Aircraft War");
        this.frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.frame.setResizable(false);
        //设置窗口的大小和位置,居中放置
        this.frame.setBounds(((int) screenSize.getWidth() - WINDOW_WIDTH) / 2, 0,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game = new AbstractGame() {
            @Override
            void drawBackground(Graphics g) {

            }
            @Override
            public void action()
            {}        };
    }

    // 启动游戏页面
    public void gameWork(){
        while(true) {
            synchronized (this) {

                if (game.isGameOverFlag() && chooseFinish) {
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

                if(!actionflag && chooseFinish){
                    if(chooseDifficulty.getMode()==1){
                        System.out.println(actionflag+" "+chooseFinish);
                        game = new EasyModelGame();
                    }else if(chooseDifficulty.getMode()==2){
                        game = new CommonModelGame();
                    }else if(chooseDifficulty.getMode()==3){
                        game = new DifficultModelGame();
                    }
                }
                if (!game.isGameOverFlag() && !actionflag) {
                    System.out.println("action");
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
        while(true){
//            System.out.println(chooseDifficulty.getMode()+" "+chooseFinish);
            if(chooseDifficulty.getMode()==0){
                chooseFinish = false;
//                chooseFlag = false;
                showFlag = false;
                actionflag = false;

            }
            if(!chooseFlag && !chooseFinish){
                c = new chooseDifficulty();
                frame.setContentPane(c.getMainPanel());
                frame.setVisible(true);
                chooseFlag = true;
            }
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
                    notifyAll();
                }
            }
        }
    }

    // 得分榜页面
    public void showScoreWork(){

        while(true){
            synchronized (this){

                if(!game.isGameOverFlag() || !chooseFinish  ){ // 游戏未结束或还没选择模式，等待
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(!showFlag && game.isGameOverFlag() && chooseFinish) {  // 还没显示过得分榜就显示
                    scoreList c = new scoreList();
                    frame.setContentPane(c.getMainPanel());
                    frame.setVisible(true);
                    showFlag = true;
                    chooseFlag = false;
                }


                notifyAll();
            }
        }
    }

}
