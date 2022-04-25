package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.MusicThread;
import edu.hitsz.strategy.ScatteredShoot;
import edu.hitsz.swing.chooseDifficulty;

public class BulletProp extends AbstractProp{

    public BulletProp (int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void propWork(HeroAircraft heroAircraft) {
        System.out.println("FireSupply active!");
        heroAircraft.setStrategy(new ScatteredShoot());
        if(chooseDifficulty.soundOpen) {
            new MusicThread("src/videos/get_supply.wav").start();
        }
    }
}
