package edu.hitsz.prop;

public class BombProp extends AbstractProp{

    public BombProp (int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void propWork(){
            System.out.println("BombSupply active!");
    }
}
