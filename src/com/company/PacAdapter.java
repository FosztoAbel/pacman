package com.company;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PacAdapter extends KeyAdapter {            //key adapterek letrehozasa amik megadott billenytuk lenyomasakor utasitasokat vegeznek
    GameBoard board;                                    //a panel amelyen szukseg van ezekre az adapterekre

    public PacAdapter(GameBoard board)
    {
        this.board = board;     //aktualis jatek panel beallitasa
    }

    @Override
    public void keyPressed(KeyEvent e) {        //ha lenyomjuk a gombot akkor tortenik valami annka megfeleloen mit nyomtunk le

        int key = e.getKeyCode();

        if (board.isInGame()) {
            if (key == KeyEvent.VK_LEFT) {  //pacman balra mozgatasa
                board.setReqDx(-1);
                board.setReqDy(0);
            } else if (key == KeyEvent.VK_RIGHT) {  //pacman jobbra mozgatasa
                board.setReqDx(1);
                board.setReqDy(0);
            } else if (key == KeyEvent.VK_UP) {     //pacman felfele mozgatasa
                board.setReqDx(0);
                board.setReqDy(-1);
            } else if (key == KeyEvent.VK_DOWN) {   //pacman lefele mozgatasa
                board.setReqDx(0);
                board.setReqDy(1);
            } else if (key == KeyEvent.VK_ESCAPE && board.getTimer().isRunning()) {  //escape gombra valo kilepes
                board.setInGame(false);
            } else if (key == KeyEvent.VK_P) {      //P billentyuvel a jatek megallitasa
                if (board.getTimer().isRunning()) {
                    board.getTimer().stop();
                    board.repaint();
                } else {
                    board.getTimer().start();
                }
            }
        } else {
            if (key == ' ') {               //jatek inditasa a SPACE billentyuvel
                board.setInGame(true);
                board.initializeGame();
            }
        }
    }
}