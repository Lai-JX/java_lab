package edu.hitsz.aircraft;

import edu.hitsz.Observer.Subscriber;
import edu.hitsz.bullet.BaseBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机
 * 不可射击
 *
 * @author hitsz
 */
public class MobEnemy extends AbstractEnemyAircraft implements Subscriber {

    public MobEnemy(int locationX, int locationY, int speedX, double speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void bombWork(){
        vanish();
    }


}
