package edu.hitsz.application;

import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.*;
import edu.hitsz.strategy.DirectShoot;
import edu.hitsz.swing.chooseDifficulty;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;


public abstract class AbstractGame extends JPanel {
    protected int backGroundTop = 0;

    /**
     * Scheduled 线程池，用于任务调度
     */
    protected final ScheduledExecutorService executorService;


    /**
     * 时间间隔(ms)，控制刷新频率
     */
    protected int timeInterval = 30;

    protected final HeroAircraft heroAircraft;

    protected final List<AbstractEnemyAircraft> enemyAircrafts;
    protected final List<BaseBullet> heroBullets;
    protected final List<BaseBullet> enemyBullets;
    protected final List<AbstractProp> props;
    protected PropFactory propFactory;
    protected EnemyFactory enemyFactory;
    //    private RecordDao recordDao;
    protected MusicThread boss_bgm;
    protected MusicThread bgm;


    protected int enemyNumber = 0;

    protected boolean gameOverFlag = false;



    protected static int score = 0;
    protected int time = 0;
    protected int counter = 0;// 标志产生敌机的阈值
    protected int BulletPropStart = 0;
    /**
     * 周期（ms)
     * 指示子弹的发射
     */
    protected int cycleDuration = 500;
    private int cycleTime = 0;
    /**
     * 周期（ms)
     * 指示敌机的产生频率
     */
    protected int enemyCycleDuration = 400;
    protected int enemyCycleTime = 0;
    // 敌机速度提升倍率
    protected double enemyImproveRate = 1.0;
    // 不产生道具的概率
    protected double noPropProbability = 0.1;
    // 子弹道具持续时间
    protected int bulletPropTime = 8000;
    // 产生精英敌机的概率
    protected double eliteEnemyProbability = 0.3;




    public AbstractGame() {
        // 英雄机初始化
        heroAircraft = HeroAircraft.getInstance(1000);
        heroAircraft.valid();
        heroAircraft.setHp(1000);
        heroAircraft.setLocation(Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight() );
        heroAircraft.setStrategy(new DirectShoot());
        // 分数初始化
        score = 0;
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

    public static void setScore(int score) {
        AbstractGame.score = score;
    }

    public static int getScore() {
        return score;
    }

    public boolean isGameOverFlag() {
        return gameOverFlag;
    }

    /**
     * 游戏启动入口，执行游戏逻辑
     * 需要随游戏模式改变
     * 精英敌机出现的概论eliteEnemyProbability，产生boss机的阈值
     */
    public abstract void action();//
//
//                System.out.println(time);
//
//
//                // 产生敌机
//                // 参数:精英敌机出现的概论eliteEnemyProbability，产生boss机的阈值
//                creatEnemyAircraft(5,600,600,60,10,30,10);
//
//                // 飞机射出子弹
//                shootAction();
//            }
//
//            // 子弹移动
//            bulletsMoveAction();
//
//            // 飞机移动
//            aircraftsMoveAction();
//
//            // 道具移动
//            propMoveAction();
//
//            // 子弹道具失效
//            bullutPropWorkTime();
//
//            // 撞击检测
//            crashCheckAction(10,20,40);
//
//            // 后处理
//            postProcessAction();
//
//            //每个时刻重绘界面
//            repaint();
//
//
//
//
//            // 游戏结束检查
//            if (heroAircraft.getHp() <= 0) {
//                // 游戏结束音乐
//                if(chooseDifficulty.isSoundOpen()){
//                    new MusicThread("src/videos/game_over.wav").start();
//                    bgm.setStop(true);
//                    if(BossEnemy.bossNum == 1){
//                        boss_bgm.setStop(true);
//                    }
//                }
//
//                // 游戏结束
//                gameOverFlag = true;
//                executorService.shutdown();
//
//                System.out.println("Game Over!");
//                synchronized (FrameThread.class){// 释放线程game，回到main
//                    notifyAll();
//                }
//            }
//
//        };
//
//        /**
//         * 以固定延迟时间进行执行
//         * 本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
//         */
//        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);
    //}

    //***********************
    //      Action 各部分
    //***********************

    /** 产生敌机
    * 参数:精英敌机出现的概论eliteEnemyProbability,产生boss机的阈值creatBoss_Score,最大敌机数量enemyMaxnumber,boss敌机的血量boosBlood
    *    精英敌机的血量eliteEnemyBlood，精英敌机的竖直方向速度精英敌机的血量eliteEnemySpeedY,普通敌机的血量mobEnemyBlood，普通敌机的竖直方向速度精英敌机的血量mobEnemySpeedY
    *
     */
    protected void creatEnemyAircraft(int creatBoss_Score, int enemyMaxNumber,int boosBlood,
                                        int eliteEnemyBlood,int eliteEnemySpeedY,int mobEnemyBlood,int mobEnemySpeedY){
        if(eliteEnemyProbability>0.95){eliteEnemyProbability=0.95;}
        // 新敌机产生 随机产生一架普通敌机或精英敌机
        if (enemyAircrafts.size() < enemyMaxNumber) {
            enemyNumber++;
            if(Math.random()< eliteEnemyProbability) {
                enemyFactory = new EliteEnemyFactory();
                enemyAircrafts.add(enemyFactory.createEnemy(
                        (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.ELITE_ENEMY_IMAGE.getWidth())) * 1,
                        (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2) * 1,
                        (int)(Math.random() * 11 -5),
                        eliteEnemySpeedY*enemyImproveRate,
                        eliteEnemyBlood
                ));
            } else {
                enemyFactory = new MobEnemyFactory();
                enemyAircrafts.add(enemyFactory.createEnemy(
                        (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())) * 1,
                        (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2) * 1,
                        0,
                        mobEnemySpeedY,
                        mobEnemyBlood
                ));
            }
            // 超过阈值产生一架boss敌机,以bossBlood=0标志简单模式，不产生boss机
            if(boosBlood!=0 && BossEnemy.bossNum == 0 && counter >= creatBoss_Score){
                counter = 0;                                            // 恢复记录阶段得分
                enemyFactory = new BossEnemyFactory();
                enemyAircrafts.add(enemyFactory.createEnemy(
                        (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.BOSS_ENEMY_IMAGE.getWidth()/4)) * 1,
                        (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05 + ImageManager.BOSS_ENEMY_IMAGE.getHeight()) * 1,
                        1,
                        0,
                        boosBlood
                ));
                if(chooseDifficulty.isSoundOpen()){
                    boss_bgm = new MusicThread("src/videos/bgm_boss.wav");
                    boss_bgm.start();
                }

            }

        }
    }

    protected boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration && cycleTime - timeInterval < cycleTime) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    protected void bullutPropWorkTime(){
        // 道具持续bulletPropTime后失效
        if(BulletPropStart!=0 && time-BulletPropStart > bulletPropTime){
            heroAircraft.setStrategy(new DirectShoot());
            BulletPropStart = 0;
        }
    }

    // 产生敌机的周期
    protected boolean enemy_timeCountAndNewCycleJudge() {
        enemyCycleTime += timeInterval;
        if (enemyCycleTime >= enemyCycleDuration && enemyCycleTime - timeInterval < enemyCycleTime) {
            // 跨越到新的周期
            enemyCycleTime %= enemyCycleDuration;
            return true;
        } else {
            return false;
        }
    }

    // 飞机射出子弹
    protected void shootAction() {
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
    }

    // 子弹移动
    protected void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }
    // 飞机移动
    protected void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }
    // 道具移动
    protected void propMoveAction() {
        for (AbstractProp prop : props) {
            prop.forward();
        }
    }


    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     * 需要根据模式对应改变
     * 参数：击落Mod敌机增加的分数mobScore，击落Elite敌机增加的分数eliteScore，击落boss敌机增加的分数，子弹道具持续时间：bulletPropTime
     *      不产生道具的概率：noPropProbability
     */
    protected void crashCheckAction(int mobScore, int eliteScore, int bossScore) {
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
                heroAircraft.decreaseHp(enemyBullet.getPower()*enemyImproveRate);
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
                        score += mobScore;
                        counter += mobScore;
                        // TODO 获得分数，产生道具补给
                        if(enemyAircraft instanceof EliteEnemy || enemyAircraft instanceof BossEnemy){
                            //如果击落的是精英敌机
                            if(enemyAircraft instanceof EliteEnemy){
                                score += eliteScore-mobScore;
                                counter += eliteScore-mobScore;
                            }
                            // 击落Boss敌机
                            if(enemyAircraft instanceof BossEnemy){
                                score += bossScore-mobScore;
                                counter += bossScore-mobScore;
                                if(chooseDifficulty.isSoundOpen()){
                                    boss_bgm.setStop(true);
                                }
                            }
                            // 如果被击落的是精英敌机或boss，则随机产生道具或不产生道具
//                            Random rd = new Random();
//                            int x = rd.nextInt(10);
                            double x = Math.random();
                            double step = (1-noPropProbability)/3;
                            if(x >= 0 && x < step){//获得加血道具
                                propFactory = new BloodPropFactory();
                                props.add(propFactory.createProp(
                                        enemyAircraft.getLocationX(),
                                        enemyAircraft.getLocationY(),
                                        0,
                                        5));
                            }
                            else if( x >= step && x < 2*step){//获得爆炸道具
                                propFactory = new BombPropFactory();
                                BombProp bombProp = (BombProp) propFactory.createProp(
                                        enemyAircraft.getLocationX(),
                                        enemyAircraft.getLocationY(),
                                        0,
                                        5);
                                props.add(bombProp);
                            }
                            else if( x >= 2*step && x < 3*step){//获得子弹道具
                                propFactory = new BulletPropFactory();
                                props.add(propFactory.createProp(
                                        enemyAircraft.getLocationX(),
                                        enemyAircraft.getLocationY(),
                                        0,
                                        5));
                            }
                            else{//未获得道具
                                System.out.println("No prop generated!");
                            }

                        }
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
                    prop.vanish();
                    if(prop instanceof BloodProp){  //获得加血道具，增加30血
                        ((BloodProp)prop).propWork(heroAircraft);
                    }else if(prop instanceof BombProp){
                        // 为炸弹道具增加观察者（子弹和非boss敌机）,并增加得分
                        addEnemyBulletSubscribe((BombProp) prop,mobScore,eliteScore);
                        ((BombProp)prop).propWork(heroAircraft);
                        // 移除所有观察者
                        ((BombProp)prop).unSubscriber();
                    }else{
                        ((BulletProp)prop).propWork(heroAircraft);
                        BulletPropStart = time;
                    }
                }

            }
        }
    }

    // 为炸弹道具增加观察者（子弹和非boss敌机）
    protected void addEnemyBulletSubscribe(BombProp bombProp,int mobScore, int eliteScore){
        // 添加观察者（道具生效需要销毁的敌机）
        for(AbstractEnemyAircraft enemy : enemyAircrafts){
            if(enemy instanceof EliteEnemy){
                bombProp.addSubscriber((EliteEnemy)enemy);
                score += eliteScore;
                counter +=eliteScore;
            }else if(enemy instanceof MobEnemy){
                bombProp.addSubscriber((MobEnemy)enemy);
                score += mobScore;
                counter += mobScore;
            }
        }

        // 添加观察者（道具生效需要销毁的敌机子弹）
        for(BaseBullet baseBullet : enemyBullets){
            bombProp.addSubscriber((EnemyBullet)baseBullet);
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
    protected void postProcessAction() {
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
     * 需要根据模式改变
     * @param  g
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 绘制背景,图片滚动
        drawBackground(g);

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
    //绘制背景
    abstract void drawBackground(Graphics g);

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
        g.drawString("SCORE:" + score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + Double.parseDouble(String.format("%.2f",this.heroAircraft.getHp())), x, y);
    }

}
