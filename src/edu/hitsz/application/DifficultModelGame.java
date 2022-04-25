package edu.hitsz.application;

import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BombProp;
import edu.hitsz.swing.chooseDifficulty;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class DifficultModelGame extends AbstractGame{
    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private int timeInterval = 40;

    public DifficultModelGame(){
        super();
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


        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            time += timeInterval;


            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                // bgm和boss_bgm线程是否失效，失效则重新添加，以实现循环播放
                if(chooseDifficulty.isSoundOpen() && !bgm.isAlive()){
                    bgm = new MusicThread("src/videos/bgm.wav");
                    bgm.start();
                }
                if(chooseDifficulty.isSoundOpen() && BossEnemy.bossNum==1 && !boss_bgm.isAlive()){
                    boss_bgm = new MusicThread("src/videos/boss_bgm.wav");
                    boss_bgm.start();
                }

                System.out.println(time);

                // 产生敌机
                // 参数:精英敌机出现的概论eliteEnemyProbability，产生boss机的阈值
                creatEnemyAircraft(0.8,8,400);

                // 飞机射出子弹
                shootAction();
            }

            // 子弹移动
            bulletsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 道具移动
            propMoveAction();

            // 为炸弹道具增加观察者（子弹和非boss敌机）
            for(AbstractProp prop : props){
                if(prop instanceof BombProp){
                    addEnemyBulletSucscribe((BombProp) prop);
                }
            }

            // 撞击检测
            crashCheckAction(10,20,40, 3000);

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
        g.drawImage(ImageManager.BACKGROUND_IMAGE5, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(ImageManager.BACKGROUND_IMAGE5, 0, this.backGroundTop, null);

    }
}
