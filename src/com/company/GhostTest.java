package com.company;

import com.company.GameBoard;
import com.company.Ghost;
import com.company.Pacman;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class GhostTest {
    GameBoard board;
    Pacman pacman;
    Ghost ghost;

    @Before
    public void BeforeTest(){
        board = new GameBoard();
        pacman = new Pacman(board);
        ghost =new Ghost(board, pacman);
    }

    @Test(expected = NullPointerException.class)
    public void testMoveGhosts() {
        Graphics2D g2d = null;
        ghost.moveGhosts(g2d);

        assertNotEquals(ghost.getGhostX(), 0);
        assertNotEquals(ghost.getGhostY(), 0);
    }

    @Test
    public void testGetNumberOfGhosts() {
        ghost.setNumberOfGhosts(12);
        assertEquals(ghost.getNumberOfGhosts(),12);
    }

    @Test
    public void TestSetNumberOfGhosts() {
        ghost.setNumberOfGhosts(12);
        assertEquals(ghost.getNumberOfGhosts(),12);
    }
}