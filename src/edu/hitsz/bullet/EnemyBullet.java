package edu.hitsz.bullet;

import edu.hitsz.Observer.Subscriber;

/**
 * @Author hitsz
 */
public class EnemyBullet extends BaseBullet implements Subscriber {

    public EnemyBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    @Override
    public void bombWork(){
        System.out.println("bullet失效");
        vanish();
    }

}
