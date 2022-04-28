package edu.hitsz.aircraft;

import edu.hitsz.strategy.DirectShoot;

public class EliteEnemyFactory implements EnemyFactory{
    @Override
    public AbstractEnemyAircraft createEnemy(int locationX, int locationY, int speedX, double speedY, int hp){
        return new EliteEnemy(locationX,locationY,speedX,speedY,hp,new DirectShoot());
    }
}
