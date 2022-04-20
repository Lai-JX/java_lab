package edu.hitsz.aircraft;



public interface EnemyFactory {
     AbstractEnemyAircraft createEnemy(int locationX, int locationY, int speedX, int speedY, int hp);
}
