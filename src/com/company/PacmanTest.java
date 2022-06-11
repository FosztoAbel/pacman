package com.company;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PacmanTest {
    GameBoard board;
    Pacman pacman;

    @Before
    public void TestBefore(){
        board = new GameBoard();
        pacman = new Pacman(board);
    }

    @Test
    public void testMovePacman() {
        pacman.setPacmanX(336);
        pacman.setPacmanY(504);
        pacman.setPacmandX(1);
        pacman.setPacmandY(0);
        pacman.setPacmanReqDx(1);
        pacman.setPacmanReqDy(0);

        pacman.movePacman();

        assertEquals(pacman.getPacmanX(), 340);           //  pacmanX = pacmanX + PACMAN_SPEED * pacmandX ===> 336+ 4 * 1 = 340;
                                                                //  mivel megegyezik ezert mukodik a move fgv.
     }

    @Test
    public void testPacmanEatSmall() {
        int i = 1;
        short j = 16;
        pacman.pacmanEatSmall(i,j);
        assertNotEquals(board.getScore(),0);
    }

    @Test
    public void testPacmanEatBig() {
        int i = 1;
        short j = 32;
        pacman.pacmanEatBig(i,j);
        assertEquals(pacman.getPacmanAttack(),true);
    }

    @Test
    public void testGetPacmanAnimPos() {
        pacman.setPacmanAnimPos(444);
        assertEquals(pacman.getPacmanAnimPos(),444);
    }

    @Test
    public void testGetPacmanAttack() {
        pacman.setPacmanAttack(true);
        assertEquals(pacman.getPacmanAttack(),true);
    }

    @Test
    public void testGetPacmanX() {
        pacman.setPacmanX(1);
        assertEquals(pacman.getPacmanX(),1);
    }

    @Test
    public void testGetPacmanY() {
        pacman.setPacmanY(1);
        assertEquals(pacman.getPacmanY(),1);
    }

    @Test
    public void testSetPacmanX() {
        pacman.setPacmanX(1);
        assertEquals(pacman.getPacmanX(),1);
    }

    @Test
    public void testSetPacmanY() {
        pacman.setPacmanY(1);
        assertEquals(pacman.getPacmanY(),1);
    }

    @Test
    public void testSetPacmanAnimPos() {
        pacman.setPacmanAnimPos(444);
        assertEquals(pacman.getPacmanAnimPos(),444);
    }

    @Test
    public void testSetPacmanAttack() {
        pacman.setPacmanAttack(true);
        assertEquals(pacman.getPacmanAttack(),true);
    }
}