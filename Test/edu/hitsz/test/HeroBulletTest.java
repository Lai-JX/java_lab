package edu.hitsz.test;

import edu.hitsz.bullet.HeroBullet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeroBulletTest {
    HeroBullet heroBullet;
    @BeforeEach
    void setUp() {
        heroBullet = new HeroBullet(0,0,0,0,10);
    }

    @AfterEach
    void tearDown() {
        System.out.println();
    }

    @Test
    @DisplayName("Test vanish")
    void vanish(){
        System.out.println("Test vanish method executed");
        heroBullet.vanish();    //子弹失效
        assertTrue(heroBullet.notValid());
        System.out.println("Pass!");
    }

    @Test
    @DisplayName("Test getPower")
    void getPower(){
        System.out.println("Test getPower method executed");
        assertEquals(10,heroBullet.getPower());
        System.out.println("Pass!");
    }


}