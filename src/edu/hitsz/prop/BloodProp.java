package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.MusicThread;
import edu.hitsz.swing.chooseDifficulty;

public class BloodProp extends AbstractProp{

    public BloodProp (int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);

    }

    @Override
    public void propWork(HeroAircraft heroAircraft) {
        heroAircraft.gainHp(30);
        if(chooseDifficulty.isSoundOpen()){
            new MusicThread("src/videos/get_supply.wav").start();
        }
    }
}
