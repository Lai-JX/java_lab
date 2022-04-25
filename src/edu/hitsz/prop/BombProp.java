package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.MusicThread;
import edu.hitsz.swing.chooseDifficulty;

public class BombProp extends AbstractProp{

    public BombProp (int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void propWork(HeroAircraft heroAircraft){
        System.out.println("BombSupply active!");
        if(chooseDifficulty.isSoundOpen()) {
            new MusicThread("src/videos/bomb_explosion.wav").start();
        }

    }
}
