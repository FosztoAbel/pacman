package com.company;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Ghost {

    private int nGhosts = 6;    //ghostok szama
    private final int[] dx;
    private final int[] dy;
    private int[] ghostX,  ghostY, ghostDx, ghostDy, ghostSpeed;      //ghost aktualis sebessege, helyzete illetve az a pozicio ahova lepni fog
    private short[] screenData;     //teljes map

    private final boolean inGame;         //jatek allapota

    private final Image ghost;            //ghost kep tarolasa

    protected Clip eatghost;        // hangfajl tarolasa

    private final GameBoard board;        //GameBoard objektum
    private final Pacman pacman;          //Pacman objektum


    public Ghost (GameBoard board, Pacman pacman){  // konstruktor

        ghost = new ImageIcon("src/resources/images/ghost.png").getImage();     //ghost kepenek betoltese

        this.pacman = pacman;       //Pacamn beallitasa
        this.board = board;         //Board beallitasa
        int maxGhosts = 12;         //maximalis ghostok szama
        this.ghostX = new int[maxGhosts];       //valtozok letrehozasa
        this.ghostDx = new int[maxGhosts];
        this.ghostY = new int[maxGhosts];
        this.ghostDy = new int[maxGhosts];
        this.ghostSpeed = new int[maxGhosts];
        this.dx = new int[4];
        this.dy = new int[4];
        this.screenData = board.getScreenData();    //map adatainak betoltese
        this.inGame = true;
    }

    public void moveGhosts(Graphics2D g2d) {            //ghostok mozgatasa

        short i;
        int pos;
        int wallNumber;
        this.screenData = board.getScreenData();
        int pacmanX = pacman.getPacmanX();          //pacman aktualis pozicioi
        int pacmanY = pacman.getPacmanY();


        for (i = 0; i < nGhosts; i++) {         //minden egyes ghostra meg kell nezni kulon
            //blokkmeret
            int blockSize = 24;
            int nBlocks = 28;
            if (ghostX[i] % blockSize == 0 && ghostY[i] % blockSize == 0) {         //tombbeli pozicioja a ghostnak, mint a pacmannel
                pos = (ghostX[i] / blockSize) + ((nBlocks *  ghostY[i]) / blockSize);

                wallNumber = 0;

                //lehetseges iranyok beallitasai
                if ((screenData[pos] & 1) == 0 && ghostDx[i] != 1) {        //ha nincs balra fal akkor menjen balra
                    dx[wallNumber] = -1;
                    dy[wallNumber] = 0;
                    wallNumber++;
                }

                if ((screenData[pos] & 2) == 0 && ghostDy[i] != 1) {       //ha felfele megy es nincs fal akkor menjen tovabb felfele
                    dx[wallNumber] = 0;
                    dy[wallNumber] = -1;
                    wallNumber++;
                }

                if ((screenData[pos] & 4) == 0 && ghostDx[i] != -1) {       //ha nincs jobbra feal menjen jobbra
                    dx[wallNumber] = 1;
                    dy[wallNumber] = 0;
                    wallNumber++;
                }

                if ((screenData[pos] & 8) == 0 && ghostDy[i] != -1) {       //ha nincs lefele fal akkor menjen lefele
                    dx[wallNumber] = 0;
                    dy[wallNumber] = 1;
                    wallNumber++;
                }

                if (wallNumber == 0) {      //ha a fenti feltetelk kozul egyik sem teljesul a ghostra akkor azt jelenti hogy egy zsakutcaba keveredett, tehat itt megfordul

                    if ((screenData[pos] & 15) == 15) {
                        ghostDx[i] = 0;
                        ghostDy[i] = 0;
                    } else {
                        ghostDx[i] = -ghostDx[i];
                        ghostDy[i] = -ghostDy[i];
                    }

                } else {            //valamelyik iranyba elmehet a fenti feltelek szerint, ezek kozul valaszt random egyet

                    wallNumber = (int) (Math.random() * wallNumber);

                    if (wallNumber > 3) {
                        wallNumber = 3;
                    }

                    ghostDx[i] = dx[wallNumber];        //valaszt egy random iranyt a lehetsegesek kozul
                    ghostDy[i] = dy[wallNumber];
                }
            }

            ghostX[i] = ghostX[i] + (ghostDx[i] * ghostSpeed[i]);
            ghostY[i] = ghostY[i] + (ghostDy[i] * ghostSpeed[i]);
            drawGhost(g2d, ghostX[i] + 1, ghostY[i] + 1);           //ghost kirajzolasa

            boolean pacmanAttack = pacman.getPacmanAttack();             //ha a pacman vesz fel nagyobb golyot akkor 15 masopercig megoli a ghostokat, ezt hogy epp van e ilyen kepessege a getPacmanAttack kerdezi le
            //meghal a pacman
            if (pacmanX > (ghostX[i] - 12) && pacmanX < (ghostX[i] + 12)
                    && pacmanY > (ghostY[i] - 12) && pacmanY < (ghostY[i] + 12)
                    && inGame && !pacmanAttack) {
                board.setDying(true);           //ha utkoznek es nem vett fel nagy golyot meghal es veszit eletet
            }
            //megeszi a szornyet
             else if (pacmanX > (ghostX[i] - 12) && pacmanX < (ghostX[i] + 12)
                    && pacmanY > (ghostY[i] - 12) && pacmanY < (ghostY[i] + 12)
                    && inGame && pacmanAttack) {
                 board.setDying(false);
                 int auxiliari =  board.getScore();
                 auxiliari +=99;
                 board.setScore(auxiliari);
                ghostY[i] = blockSize * nBlocks / 2;
                ghostX[i] = blockSize * nBlocks / 2;
                                                                        //ha vett fel nagy golyot az elmult 15 masodpercbena akkor megeszi a szornyet es bonus pontot is kap erte, a szorny ujra spawnolasa
                //hang nemitasa
                boolean musicMute = board.getMusicMute();
                if(!musicMute){                                         //hang lejatszasa ha nincs nemitas
                File file = new File("src/resources/sounds/pacman_eatghost.wav");
                try {
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                    AudioFormat format = audioInputStream.getFormat();
                    DataLine.Info info = new DataLine.Info(Clip.class, format);

                    eatghost = (Clip) AudioSystem.getLine(info);

                    eatghost.open(audioInputStream);

                    eatghost.start();
                    Thread.sleep(1);

                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
                    e.printStackTrace();
                }
                }
            }
            }
    }

    public void drawGhost(Graphics2D g2d, int x, int y) {
        g2d.drawImage(ghost, x, y, null);
    }   //ghost kirajzolasa

    public int getNumberOfGhosts(){
        return this.nGhosts;
    }       //visszater a szornyek szamaval

    public int[] getGhostX(){
        return this.ghostX;
    }   //ghost x kordinataja

    public int[] getGhostY(){
        return this.ghostY;
    }   //ghost y koordinataja

    public int[] getGhostDx(){
        return this.ghostDx;
    }   //ghost haladasi iranya x tengelyen

    public int[] getGhostDy(){
        return this.ghostDy;
    }   //ghost haladasi iranya y tengelyen

    public int[] getGhostSpeed(){
        return this.ghostSpeed;
    }  //ghost sebessegenek lekerdezese

    public void setNumberOfGhosts(int value){this.nGhosts = value;}     //ghostok szamanak beallitasa
}
