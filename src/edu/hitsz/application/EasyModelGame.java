package edu.hitsz.application;

import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BombProp;
import edu.hitsz.swing.chooseDifficulty;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class EasyModelGame extends AbstractGame{
    public EasyModelGame(){
        super();
        noPropProbability = 0.1;
        bulletPropTime = 8000;
        eliteEnemyProbability = 0.3;
        timeInterval = 30;
    }

    /**
     * 游戏启动入口，执行游戏逻辑
     * 需要随游戏模式改变
     * 精英敌机出现的概论eliteEnemyProbability，产生boss机的阈值
     */
    @Override
    public void action() {
        if(chooseDifficulty.isSoundOpen()){
            bgm = new MusicThread("src/videos/bgm.wav");
            bgm.start();
        }
        System.out.println("简单模式：");
        System.out.println("\t不产生boss敌机\t最大敌机数:5" +
                "\n\t子弹伤害:30\t精英敌机速度:10\t" + "精英敌机血量:30\t普通敌机速度:10\t普通敌机血量:30" +
                "\n\t击落精英敌机得分:20\t击落普通敌机得分:10" +
                "\n\t不随时间提升难度"+
                "\n\t精英敌机概率为:0.3\t不产生道具的概率为:0.1\t子弹道具持续时间初始值为8s");

        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            time += timeInterval;
            System.out.println(time);
            // bgm和boss_bgm线程是否失效，失效则重新添加，以实现循环播放
            if(chooseDifficulty.isSoundOpen() && !bgm.isAlive()){
                bgm = new MusicThread("src/videos/bgm.wav");
                bgm.start();
            }
            if(chooseDifficulty.isSoundOpen() && BossEnemy.bossNum==1 && !boss_bgm.isAlive()){
                boss_bgm = new MusicThread("src/videos/boss_bgm.wav");
                boss_bgm.start();
            }

            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {

                // 飞机射出子弹
                shootAction();
            }
            // 每隔enemyCycleDuration产生敌机
            if(enemy_timeCountAndNewCycleJudge()) {
                // 产生敌机
                // 参数:精英敌机出现的概论eliteEnemyProbability，产生boss机的阈值
                creatEnemyAircraft(600, 5, 0, 30,
                        10, 30, 10);
            }

            // 子弹移动
            bulletsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 子弹道具失效
            bullutPropWorkTime();

            // 道具移动
            propMoveAction();

            // 撞击检测
            crashCheckAction(10,20,40);

            // 后处理
            postProcessAction();

            //每个时刻重绘界面
            repaint();




            // 游戏结束检查
            if (heroAircraft.getHp() <= 0) {
                // 游戏结束音乐
                if(chooseDifficulty.isSoundOpen()){
                    new MusicThread("src/videos/game_over.wav").start();
                    bgm.setStop(true);
                    if(BossEnemy.bossNum == 1){
                        boss_bgm.setStop(true);
                    }
                }

                // 游戏结束
                gameOverFlag = true;
                executorService.shutdown();

                System.out.println("Game Over!");
                synchronized (FrameThread.class){// 释放线程game，回到main
                    notifyAll();
                }
            }

        };

        /**
         * 以固定延迟时间进行执行
         * 本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
         */
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);
    }
    @Override
    public void drawBackground(Graphics g){
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop, null);

    }
}
