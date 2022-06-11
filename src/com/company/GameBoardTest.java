package com.company;

import com.company.GameBoard;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameBoardTest {
    GameBoard board;

    @Before
    public void BeforeTest(){
        board = new GameBoard();
    }

    @Test
    public void TestInitializeMap() {
        assertNotEquals(board.getLevelData(),null);
    }

    @Test
    public void TestInitializeVariables() {
        assertNotEquals(board.getMazeColor(), null);
        assertNotEquals(board.getScreenData(), null);
        assertNotEquals(board.getDimension(),null);
        assertNotEquals(board.getTimer(), null);
    }

    @Test
    public void TestInitializeGame() {
        assertEquals(board.getCurrentSpeed(),3);
        assertEquals(board.getLifeLeft(),3);
        assertEquals(board.getScore(),0);
        assertEquals(board.getnGhosts(), 6);
        assertEquals(board.isSoundPlaying(), false);
    }

    @Test
    public void TestInitializeLevel() {
        assertNotEquals(board.getScreenData(),null);
    }

    @Test
    public void TestChechDeath() {
        int asd = board.getLifeLeft();
        board.chechDeath();
        assertNotEquals(asd, board.getLifeLeft());
    }

    @Test
    public void TestisInGame() {
        board.setInGame(true);
        assertEquals(board.isInGame(),true);
    }

    @Test
    public void TestSetInGame() {
        board.setInGame(true);
        assertEquals(board.isInGame(),true);
    }

    @Test
    public void TestGetMusicMute() {
        board.setMusicMute(true);
        assertEquals(board.getMusicMute(),true);
    }

    @Test
    public void TestSetMusicMute() {
        board.setMusicMute(true);
        assertEquals(board.getMusicMute(),true);
    }
}