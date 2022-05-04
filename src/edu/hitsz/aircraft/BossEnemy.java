package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.strategy.StrategyInterface;

import java.util.LinkedList;
import java.util.List;

public class BossEnemy extends AbstractEnemyAircraft {

    /** 攻击方式 */
//    private int shootNum = 1;     //子弹一次发射数量
//    private int power = 30;       //子弹伤害
//    private int direction = 1;  //子弹射击方向 (向上发射：-1，向下发射：1)
    private StrategyInterface strategy;
    public static int bossNum = 0;        //boss敌机数量

    public BossEnemy(int locationX, int locationY, int speedX, double speedY, int hp, StrategyInterface strategy) {
        super(locationX, locationY, speedX, speedY, hp);
        this.strategy = strategy;
        bossNum++;
    }

    public void setStrategy(StrategyInterface strategy){
        this.strategy = strategy;
    }

    public List<BaseBullet> executeStrategy(){
        return strategy.shoot(this.locationX,this.locationY,this.speedY,1, 2);
    }


    @Override
    public void decreaseHp(double decrease){
        hp -= decrease;
        if(hp <= 0) {
            hp = 0;
            vanish();
            bossNum--;
        }
    }

    /**
     * 通过射击产生子弹
     * @return 射击出的子弹List
     */
//    public List<BaseBullet> shoot() {
//        List<BaseBullet> res = new LinkedList<>();
//        int x = this.getLocationX();
//        int y = this.getLocationY() + direction*2;
//        int speedX = 0;
//        int speedY = this.getSpeedY() + direction*5;
//        BaseBullet baseBullet;
//        for(int i=0; i<shootNum; i++){
//            // 子弹发射位置相对飞机位置向前偏移
//            // 多个子弹横向分散
//            baseBullet = new EnemyBullet(x + (i*2 - shootNum + 1)*10, y, speedX, speedY, power);
//            res.add(baseBullet);
//        }
//        return res;
//    }
}
