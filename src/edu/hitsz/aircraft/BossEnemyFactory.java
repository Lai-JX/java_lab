package edu.hitsz.aircraft;

import edu.hitsz.strategy.ScatteredShoot;

public class BossEnemyFactory implements EnemyFactory{
    @Override
    public AbstractEnemyAircraft createEnemy(int locationX, int locationY, int speedX, double speedY, int hp){
        return new BossEnemy(locationX,locationY,speedX,speedY,hp,new ScatteredShoot());
    }
}
