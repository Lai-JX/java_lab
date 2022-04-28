package edu.hitsz.test;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;


class HeroAircraftTest {
    HeroAircraft heroAircraft;
    @BeforeEach
    void setUp() {
        heroAircraft = HeroAircraft.getInstance(1000);
    }

    @Test
    @DisplayName("Test gainHp")
    void gainHp(){
        // 默认血量为10000
        System.out.println("Test gainHp method executed");
        //增加10血量
        heroAircraft.gainHp(10);
        //判断血量是否为期待值
        assertEquals(10010,heroAircraft.getHp());
        System.out.println("Pass!");
    }

    @Test
    @DisplayName("Test getSpeedY")
    void getSpeedY(){
        System.out.println("Test getSpeedY method executed");
        // 英雄机竖直方向速度为0
        assertEquals(0,heroAircraft.getSpeedY());
        System.out.println("Pass!");
    }

    @AfterEach
    void tearDown() {
        System.out.println();

    }



}