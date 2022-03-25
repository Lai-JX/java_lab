package edu.hitsz.aircraft;



public interface EnemyFactory {
    public EnemyAircraft createEnemy(int locationX, int locationY, int speedX, int speedY,int hp);
}
