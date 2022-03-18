package edu.hitsz.application;

import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.AbstractBullet;
import edu.hitsz.basic.FlyingObject;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BloodProp;
import edu.hitsz.prop.BombProp;
import edu.hitsz.prop.BulletProp;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private int timeInterval = 40;

    private final HeroAircraft heroAircraft;

    private final List<AbstractAircraft> enemyAircrafts;
    private final List<AbstractBullet> heroBullets;
    private final List<AbstractBullet> enemyBullets;
    private final List<AbstractProp> props;

    private int enemyMaxNumber = 5;
    private int enemyNumber = 0;

    private boolean gameOverFlag = false;
    private int score = 0;
    private int time = 0;
    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    private int cycleDuration = 600;
    private int cycleTime = 0;


    public Game() {
        heroAircraft = new HeroAircraft(
                Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight() ,
                0, 0, 10000);

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        //Scheduled 线程池，用于定时任务调度
        executorService = new ScheduledThreadPoolExecutor(1);

        //启动英雄机鼠标监听
        new HeroController(this, heroAircraft);

    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {

        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            time += timeInterval;

            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                System.out.println(time);
//                System.out.println(enemyAircrafts.size() + "" + enemyNumber);
                // 新敌机产生 随机产生一架普通敌机或精英敌机
                if (enemyAircrafts.size() < enemyMaxNumber) {
                    enemyNumber++;
                    if(Math.random()< 0.5) {
                        enemyAircrafts.add(new MobEnemy(
                                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())) * 1,
                                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2) * 1,
                                0,
                                10,
                                30
                        ));
                    } else {
                        enemyAircrafts.add(new EliteEnemy(
                                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.ELITE_ENEMY_IMAGE.getWidth())) * 1,
                                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2) * 1,
                                0,
                                10,
                                30
                        ));
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
                // 游戏结束
                executorService.shutdown();
                gameOverFlag = true;
                System.out.println("Game Over!");
            }

        };

        /**
         * 以固定延迟时间进行执行
         * 本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
         */
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);

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
        for(AbstractAircraft obj : enemyAircrafts){
            if(obj instanceof EliteEnemy){
                enemyBullets.addAll(((EliteEnemy)obj).shoot());
            }
        }

        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
    }

    private void bulletsMoveAction() {
        for (AbstractBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (AbstractBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
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
        for (AbstractBullet enemy_bullet : enemyBullets) {
            if (enemy_bullet.notValid()) {
                continue;
            }
            if (heroAircraft.notValid()) {
                // 英雄机已被其他子弹击毁不再检测
                // 避免多个子弹重复击毁英雄机的判定
                continue;
            }
            if (heroAircraft.crash(enemy_bullet)) {
                // 英雄机撞击到敌机子弹
                // 英雄机损失一定生命值
                heroAircraft.decreaseHp(enemy_bullet.getPower());
                enemy_bullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        for (AbstractBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        // TODO 获得分数，产生道具补给
                        if(enemyAircraft instanceof EliteEnemy){
                            //如果击落的是精英敌机，则多获得10分
                            score += 10;
                            // 如果被击落的是精英敌机，则随机产生道具或不产生道具
                            Random rd = new Random();
                            int x = rd.nextInt(4);
//                            System.out.println(x);
                            if(x == 0){//获得加血道具
                                props.add(new BloodProp(
                                        enemyAircraft.getLocationX(),
                                        enemyAircraft.getLocationY(),
                                        0,
                                        5));
                            }
                            else if( x == 1){//获得爆炸道具
                                props.add(new BombProp(
                                        enemyAircraft.getLocationX(),
                                        enemyAircraft.getLocationY(),
                                        0,
                                        5));
                            }
                            else if( x == 2){//获得子弹道具
                                props.add(new BulletProp(
                                        enemyAircraft.getLocationX(),
                                        enemyAircraft.getLocationY(),
                                        0,
                                        5));
                            }
                            else{//未获得道具

                            }

                        }
                        score += 10;
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
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
                    if(prop instanceof BloodProp){  //获得加血道具，增加10血
                        heroAircraft.gainHp(10);
                    }else if(prop instanceof BombProp){
                        ((BombProp)prop).propWork();
                    }else{
                        ((BulletProp)prop).propWork();
                    }
                }

            }
        }
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 检查英雄机生存
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyBullets.removeIf(FlyingObject::notValid);
        heroBullets.removeIf(FlyingObject::notValid);
        enemyAircrafts.removeIf(FlyingObject::notValid);
        props.removeIf(FlyingObject::notValid);
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
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop, null);
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

    private void paintImageWithPositionRevised(Graphics g, List<? extends FlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (FlyingObject object : objects) {
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
