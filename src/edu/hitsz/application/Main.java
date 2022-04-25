package edu.hitsz.application;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {

    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Hello Aircraft War");
        FrameThread frameThread = new FrameThread();
        // 创建选择模式页面的线程
        Runnable choose =() ->{
//            while(true){
                frameThread.chooseWork();
//            }
        };
        new Thread(choose,"Menu").start();

        // 创建展示得分榜页面的线程
        Runnable showScore =() ->{
            frameThread.showScoreWork();

        };
        Thread showScoreThread = new Thread(showScore,"showScore");
        showScoreThread.start();

        // 游戏进行
        frameThread.gameWork();
    }
}
