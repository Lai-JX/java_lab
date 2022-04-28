package edu.hitsz.strategy;

import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public interface StrategyInterface {
    List<BaseBullet> shoot(int LocationX, int LocationY, double SpeedY, int direction, int AircraftType);
    // direction -1 向上，1向下
    // AircraftType 1 英雄机 2 敌机
}
