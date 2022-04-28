package edu.hitsz.application;

import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.aircraft.BossEnemyFactory;
import edu.hitsz.aircraft.EliteEnemyFactory;
import edu.hitsz.aircraft.MobEnemyFactory;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BombProp;
import edu.hitsz.swing.chooseDifficulty;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class CommonModelGame extends AbstractGame{


    public CommonModelGame(){

        super();
        // 敌机产生周期
        enemyCycleDuration = 500;
        // 不产生道具的概率
        noPropProbability = 0.25;
        // 子弹道具持续时间
        bulletPropTime = 5000;
        // 产生精英敌机的概率
        eliteEnemyProbability = 0.3;
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
        System.out.println("普通模式：");
        System.out.println("\t产生boss敌机的阈值:300\t最大敌机数:7\tboss敌机血量:200" +
                "\n\t精英敌机初始速度:13\t" + "精英敌机血量:60\t普通敌机初始速度:10\t普通敌机血量:30" +
                "\n\t击落boss敌机得分:40\t击落精英敌机得分:10\t击落普通敌机得分:5" +
                "\n\t除boss机外提升难度的时间间隔:5s" +
                "\n\t精英敌机概率初始值为:0.3\t敌机产生周期初始值为:500ms\t不产生道具的概率初始值为:0.25\t子弹道具持续时间初始值为5s");

        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            time += timeInterval;
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


//                System.out.println(time);

                // 每隔5秒增加难度
                if(time % 5000 == 0){
                    eliteEnemyProbability += 0.01;
                    System.out.print("提升难度！精英敌机概率:"+Double.parseDouble(String.format("%.2f",eliteEnemyProbability)));
                    enemyCycleDuration -= 10;
                    System.out.print("!\t敌机产生周期:"+enemyCycleDuration+"ms");
                    enemySpeedyImproveRate += 0.01;
                    System.out.print("!\t新增敌机速度提升倍率:"+Double.parseDouble(String.format("%.2f",enemySpeedyImproveRate)));
                    noPropProbability += 0.01;
                    System.out.print("!\t击落精英敌机或boss敌机不产生道具的概率:"+Double.parseDouble(String.format("%.2f",noPropProbability)));
                    bulletPropTime -= 100;
                    System.out.println("!\t子弹道具持续时间:"+bulletPropTime+"ms");
                }


                // 飞机射出子弹
                shootAction();
            }
            // 每隔enemyCycleDuration产生敌机
            if(enemy_timeCountAndNewCycleJudge()){
                // 产生敌机
                // 参数:精英敌机出现的概论eliteEnemyProbability，产生boss机的阈值
                creatEnemyAircraft(300,7,200,
                        60,13,30,10);
            }

            // 子弹移动
            bulletsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 道具移动
            propMoveAction();

            // 子弹道具失效
            bullutPropWorkTime();

            // 撞击检测
            crashCheckAction(5,10,40);

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
            g.drawImage(ImageManager.BACKGROUND_IMAGE2, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
            g.drawImage(ImageManager.BACKGROUND_IMAGE2, 0, this.backGroundTop, null);

    }
}
