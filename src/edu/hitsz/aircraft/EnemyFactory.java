package edu.hitsz.aircraft;



public interface EnemyFactory {
     AbstractEnemyAircraft createEnemy(int locationX, int locationY, int speedX, double speedY, int hp);
}
