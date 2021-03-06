package edu.hitsz.prop;

import edu.hitsz.Observer.Subscriber;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.MusicThread;
import edu.hitsz.swing.chooseDifficulty;

import java.util.ArrayList;

public class BombProp extends AbstractProp{

    private ArrayList<Subscriber> enemyList;

    public BombProp (int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
        enemyList = new ArrayList<Subscriber>();
    }

    @Override
    public void propWork(HeroAircraft heroAircraft){
        System.out.println("BombSupply active!");
        if(chooseDifficulty.isSoundOpen()) {
            new MusicThread("src/videos/bomb_explosion.wav").start();
        }
        // 唤醒生产者，使除boss外的所有敌机和子弹消失
        for(Subscriber subscriber : enemyList){
            subscriber.bombWork();
        }
    }

    // 增加观察者（爆炸道具生效时，需要消失的敌机和子弹）
    public void addSubscriber(Subscriber s){
        enemyList.add(s);
    }

    // 减少观察者
    public void unSubscriber(){
//        enemyList.remove(s);
        enemyList.removeAll(enemyList);
    }
}
