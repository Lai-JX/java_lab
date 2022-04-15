package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.strategy.ScatteredShoot;

public class BulletProp extends AbstractProp{

    public BulletProp (int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void propWork(HeroAircraft heroAircraft) {
        System.out.println("FireSupply active!");
        heroAircraft.setStrategy(new ScatteredShoot());
    }
}
