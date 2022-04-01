package edu.hitsz.test;

import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.prop.BombProp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EliteEnemyTest {
    EliteEnemy eliteEnemy;
    @BeforeEach
    void setUp() {
        eliteEnemy = new EliteEnemy(10,20,5,10,30);
    }

    @AfterEach
    void tearDown() {
        System.out.println();
    }

    @Test
    @DisplayName("Test decreaseHp")
    void decreaseHp(){
        // 默认血量为30
        System.out.println("Test decreaseHp method executed");
        // 减少20血量
        eliteEnemy.decreaseHp(20);
        // 判断血量是否为期待值
        assertEquals(10,eliteEnemy.getHp());
        System.out.println("Pass!");
    }

    @Test
    @DisplayName("Test getLocationX")
    void getLocationX(){
        System.out.println("Test getLacationX method executed");
        assertEquals(10,eliteEnemy.getLocationX());
        System.out.println("Pass!");
    }
}