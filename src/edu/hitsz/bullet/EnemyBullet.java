package edu.hitsz.bullet;

import edu.hitsz.Observer.Subscriber;

/**
 * @Author hitsz
 */
public class EnemyBullet extends BaseBullet implements Subscriber {

    public EnemyBullet(int locationX, int locationY, int speedX, double speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    @Override
    public void bombWork(){
        vanish();
    }

}
