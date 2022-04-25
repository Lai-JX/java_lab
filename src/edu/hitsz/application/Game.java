package edu.hitsz.application;

import edu.hitsz.Dao.DaoInterface;
import edu.hitsz.Dao.Record;
import edu.hitsz.Dao.RecordDao;
import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.prop.*;
import edu.hitsz.strategy.DirectShoot;
import edu.hitsz.swing.chooseDifficulty;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * 游戏主面板，游戏启动
 *
 * @author hitsz
 */
public class Game extends JPanel {

    private int backGroundTop = 0;

    /**
     * Scheduled 线程池，用于任务调度
     */
    private final ScheduledExecutorService executorService;

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private int timeInterval = 40;

    private final HeroAircraft heroAircraft;

    private final List<AbstractEnemyAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<AbstractProp> props;
    private PropFactory propFactory;
    private EnemyFactory enemyFactory;
//    private RecordDao recordDao;
    private MusicThread boss_bgm;
    private MusicThread bgm;

    private int enemyMaxNumber = 5;
    private int enemyNumber = 0;

    private boolean gameOverFlag = false;
    public static int score = 0;
    private int time = 0;
    private int counter = 0;
    private int BulletPropStart = 0;
    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    private int cycleDuration = 600;
    private int cycleTime = 0;


    public Game() {
        heroAircraft = HeroAircraft.getInstance();
        // 建立数据访问对象，并从scoreRecord.txt文件中读取之前的数据
//        recordDao = new RecordDao();

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        ThreadFactory gameThread = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("game thread");
                return t;
            }
        };

        //Scheduled 线程池，用于定时任务调度
        executorService = new ScheduledThreadPoolExecutor(1,gameThread);

        //启动英雄机鼠标监听
        new HeroController(this, heroAircraft);

    }

    public boolean isGameOverFlag() {
        return gameOverFlag;
    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
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


                // 新敌机产生 随机产生一架普通敌机或精英敌机
                if (enemyAircrafts.size() < enemyMaxNumber) {
                    enemyNumber++;
                    if(Math.random()< 0.5) {
                        enemyFactory = new MobEnemyFactory();
                        enemyAircrafts.add(enemyFactory.createEnemy(
                                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())) * 1,
                                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2) * 1,
                                0,
                                10,
                                30
                        ));
                    } else {
                        enemyFactory = new EliteEnemyFactory();
                        enemyAircrafts.add(enemyFactory.createEnemy(
                                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.ELITE_ENEMY_IMAGE.getWidth())) * 1,
                                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2) * 1,
                                (int)(Math.random() * 11 -5),
                                10,
                                60
                        ));
                    }
                    if(BossEnemy.bossNum == 0 && counter >= 600){   // 每600分产生一架boss敌机
                        counter = 0;
                        enemyFactory = new BossEnemyFactory();
                        enemyAircrafts.add(enemyFactory.createEnemy(
                                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.BOSS_ENEMY_IMAGE.getWidth()/4)) * 1,
                                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05 + ImageManager.BOSS_ENEMY_IMAGE.getHeight()) * 1,
                                1,
                                0,
                                600
                        ));
                        if(chooseDifficulty.isSoundOpen()){
                            boss_bgm = new MusicThread("src/videos/bgm_boss.wav");
                            boss_bgm.start();
                        }

                    }

                }
                // 飞机射出子弹
                shootAction();
            }

            // 子弹移动
            bulletsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 道具移动
            propMoveAction();

            // 撞击检测
            crashCheckAction();

            // 后处理
            postProcessAction();

            //每个时刻重绘界面
            repaint();



            // 游戏结束检查
            if (heroAircraft.getHp() <= 0) {
                System.out.println("xx");
                if(chooseDifficulty.isSoundOpen()){
                    new MusicThread("src/videos/game_over.wav").start();
                    bgm.setStop(true);
                    if(BossEnemy.bossNum == 1){
                        boss_bgm.setStop(true);
                    }
                }




//                // 记录数据
//                recordDao.add(new Record("",this.score));
////                Record r = recordDao.findFirst();
//                // 写入scoreRedord.txt
//                try {
//                    recordDao.writeToFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

//                System.out.println("********************************************************");
//                System.out.println("                        得分排行榜                        ");
//                System.out.println("********************************************************");
//                // 根据得分进行排序并打印
//                recordDao.sortAndPrintf();

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
//        if(this.gameOverFlag){
//            System.out.println("shundowm"+Thread.currentThread().getName());
//            executorService.shutdown();
//            synchronized (FrameThread.class){
//                System.out.println("shundowm"+Thread.currentThread().getName());
//                notifyAll();
//                System.out.println("shundowm"+Thread.currentThread().getName());
//            }
//        }
    }

    //***********************
    //      Action 各部分
    //***********************

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration && cycleTime - timeInterval < cycleTime) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void shootAction() {
        // TODO 敌机射击
        for(AbstractEnemyAircraft obj : enemyAircrafts){
            if(obj instanceof EliteEnemy){
                enemyBullets.addAll(((EliteEnemy)obj).executeStrategy());
            }
            if(obj instanceof BossEnemy){
                enemyBullets.addAll(((BossEnemy)obj).executeStrategy());
            }
        }

        // 英雄射击
        heroBullets.addAll(heroAircraft.executeStrategy());
        if(chooseDifficulty.isSoundOpen()){
            new MusicThread("src/videos/bullet.wav").start();
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
//            if(enemyAircraft instanceof EliteEnemy){
//                enemyAircraft.setSpeedX((int)(Math.random() * 11 - 5 ));// 给精英敌机-5~5的横向速度
//            }
            enemyAircraft.forward();
        }
    }

    private void propMoveAction() {
        for (AbstractProp prop : props) {
            prop.forward();
        }
    }


    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // TODO 敌机子弹攻击英雄
        for (BaseBullet enemyBullet : enemyBullets) {
            if (enemyBullet.notValid()) {
                continue;
            }
            if (heroAircraft.notValid()) {
                // 英雄机已被其他子弹击毁不再检测
                // 避免多个子弹重复击毁英雄机的判定
                continue;
            }
            if (heroAircraft.crash(enemyBullet)) {
                // 英雄机撞击到敌机子弹
                // 英雄机损失一定生命值
                heroAircraft.decreaseHp(enemyBullet.getPower());
                enemyBullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractEnemyAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    if(chooseDifficulty.isSoundOpen()){
                        new MusicThread("src/videos/bullet_hit.wav").start();
                    }
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        // TODO 获得分数，产生道具补给
                        if(enemyAircraft instanceof EliteEnemy || enemyAircraft instanceof BossEnemy){
                            //如果击落的是精英敌机或boss敌机，则多获得10分
                            score += 10;
                            counter += 10;
                            if(enemyAircraft instanceof BossEnemy){
                                score += 20;    // 击落Boss敌机，多加20分
                                counter += 20;
                                if(chooseDifficulty.isSoundOpen()){
                                    boss_bgm.setStop(true);
                                }
                            }
                            // 如果被击落的是精英敌机或boss，则随机产生道具或不产生道具
                            Random rd = new Random();
                            int x = rd.nextInt(10);
//                            System.out.println(x);
                            if(x >= 0 && x < 3){//30%概论获得加血道具
                                propFactory = new BloodPropFactory();
                                props.add(propFactory.createProp(
                                        enemyAircraft.getLocationX(),
                                        enemyAircraft.getLocationY(),
                                        0,
                                        5));
                            }
                            else if( x >= 3 && x < 6){//30%概论获得爆炸道具
                                propFactory = new BombPropFactory();
                                props.add(propFactory.createProp(
                                        enemyAircraft.getLocationX(),
                                        enemyAircraft.getLocationY(),
                                        0,
                                        5));
                            }
                            else if( x >= 6 && x < 9){//30%概论获得子弹道具
                                propFactory = new BulletPropFactory();
                                props.add(propFactory.createProp(
                                        enemyAircraft.getLocationX(),
                                        enemyAircraft.getLocationY(),
                                        0,
                                        5));
                            }
                            else{//未获得道具

                            }

                        }
                        score += 10;
                        counter += 10;
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
//                    boss_bgm.setStop(true);
                }
            }
        }

        // Todo: 我方获得道具，道具生效
        for(AbstractProp prop : props){
            if(prop.crash(heroAircraft) || heroAircraft.crash(prop)){
                if(!prop.notValid()){
//                    prop.propWork();
                    prop.vanish();
//                    System.out.println(prop.notValid());
                    if(prop instanceof BloodProp){  //获得加血道具，增加30血
                        ((BloodProp)prop).propWork(heroAircraft);
                    }else if(prop instanceof BombProp){
                        ((BombProp)prop).propWork(heroAircraft);
                    }else{
                        ((BulletProp)prop).propWork(heroAircraft);
                        BulletPropStart = time;
                    }
                }

            }
        }
        // 道具持续3秒后失效
        if(BulletPropStart!=0 && time-BulletPropStart > 3000){
            heroAircraft.setStrategy(new DirectShoot());
            BulletPropStart = 0;
        }
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     *    删除无效的道具
     * 3. 检查英雄机生存
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }


    //***********************
    //      Paint 各部分
    //***********************

    /**
     * 重写paint方法
     * 通过重复调用paint方法，实现游戏动画
     *
     * @param  g
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 绘制背景,图片滚动
        if(chooseDifficulty.getMode() == 1){
            g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
            g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop, null);
        }else if(chooseDifficulty.getMode() == 2){
            g.drawImage(ImageManager.BACKGROUND_IMAGE2, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
            g.drawImage(ImageManager.BACKGROUND_IMAGE2, 0, this.backGroundTop, null);
        }else if(chooseDifficulty.getMode() == 3){
            g.drawImage(ImageManager.BACKGROUND_IMAGE5, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
            g.drawImage(ImageManager.BACKGROUND_IMAGE5, 0, this.backGroundTop, null);
        }

        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 先绘制子弹，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);

        paintImageWithPositionRevised(g, enemyAircrafts);
        paintImageWithPositionRevised(g, props);

        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        //绘制得分和生命值
        paintScoreAndLife(g);

    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(new Color(16711680));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE:" + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
    }


}
