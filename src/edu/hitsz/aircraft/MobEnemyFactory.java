package edu.hitsz.aircraft;

public class MobEnemyFactory implements EnemyFactory{
    public EnemyAircraft createEnemy(int locationX, int locationY, int speedX, int speedY,int hp){
        return new MobEnemy(locationX,locationY,speedX,speedY,hp);
    }
}
