package edu.hitsz.aircraft;

public abstract class AbstractEnemyAircraft extends AbstractAircraft{
    public AbstractEnemyAircraft(int locationX, int locationY, int speedX, double speedY, int hp){
        super(locationX, locationY, speedX, speedY, hp);
    }
}
