package com.company;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Pacman {


    private int score;                                  //score tarolasa
    private boolean pacmanAttack = false;               //pacman tamadasi kepessege, kepes e megenni a ghostot
    private int pacmanX, pacmanY, pacmandX, pacmandY;   //pacman koordinatai es haladasi iranya
    private int reqDx, reqDy, viewDx, viewDy;           //jatekos altal megadott haladas
    private int pacmanAnimPos = 0;
    private boolean soundPlaying = false;               //zene lejatszasa
    private short[] screenData;                         //map adatok

    //pacman kepei a rajzolashoz
    private final Image pacman1, pacman2Up, pacman2Left, pacman2Right, pacman2Down;
    private final Image pacman3Up, pacman3Down, pacman3Left, pacman3Right;
    private final Image pacman4Up, pacman4Down, pacman4Left, pacman4Right;

    protected Clip eating;      //zene
    protected Clip eatbig;

    private final GameBoard board;    //GameBoard amina a pacman van

    public Pacman(GameBoard board){         //konstruktor
        this.board = board;                 //az a panel amelyikena pacman van
        this.screenData = board.getScreenData();        //adatok lekerese
        this.reqDx = board.getReqDx();                  //jatekos altal megadott iranyok kezelese
        this.reqDy = board.getReqDy();
        //kepek betoltese
        pacman1 = new ImageIcon("src/resources/images/pacman.png").getImage();
        pacman2Up = new ImageIcon("src/resources/images/up1.png").getImage();
        pacman3Up = new ImageIcon("src/resources/images/up2.png").getImage();
        pacman4Up = new ImageIcon("src/resources/images/up3.png").getImage();
        pacman2Down = new ImageIcon("src/resources/images/down1.png").getImage();
        pacman3Down = new ImageIcon("src/resources/images/down2.png").getImage();
        pacman4Down = new ImageIcon("src/resources/images/down3.png").getImage();
        pacman2Left = new ImageIcon("src/resources/images/left1.png").getImage();
        pacman3Left = new ImageIcon("src/resources/images/left2.png").getImage();
        pacman4Left = new ImageIcon("src/resources/images/left3.png").getImage();
        pacman2Right = new ImageIcon("src/resources/images/right1.png").getImage();
        pacman3Right = new ImageIcon("src/resources/images/right2.png").getImage();
        pacman4Right = new ImageIcon("src/resources/images/right3.png").getImage();
    }

    public void movePacman() {  //pacman mozgatasa
        int pos;
        short ch;
        this.screenData = board.getScreenData();
        this.reqDx = board.getReqDx();
        this.reqDy = board.getReqDy();

        if (reqDx == -pacmandX && reqDy == -pacmandY) { //ha a jatekos mas iranyt ad meg akkor forduljon meg a pacman
            pacmandX = reqDx;
            pacmandY = reqDy;
            viewDx = pacmandX;
            viewDy = pacmandY;
        }

        int blockSize = 24;
        if (pacmanX % blockSize == 0 && pacmanY % blockSize == 0) { //megnezi hogy jo uton halad e a pacman(blockokat lep-e)
            int nBlocks = 28;
            pos = pacmanX / blockSize + nBlocks * (pacmanY / blockSize);    // megkapja hogy a tomben hanyiadik indexu elemen van a pacman
            ch = screenData[pos];

            //sima golyot vesz fel a pacman
            if ((ch & 16) != 0) {
                pacmanEatSmall(pos, ch);
            }

            //nagy golyot vesz fel a pacman
            if ((ch & 32) != 0) {
                pacmanEatBig(pos,ch);
            }

            //esetek kezelese amerre a pacman mehet
            if (reqDx != 0 || reqDy != 0) {
                if (!((reqDx == -1 && reqDy == 0 && (ch & 1) != 0)          //egyike se teljesul es akkor mozog
                        || (reqDx == 1 && reqDy == 0 && (ch & 4) != 0)
                        || (reqDx == 0 && reqDy == -1 && (ch & 2) != 0)
                        || (reqDx == 0 && reqDy == 1 && (ch & 8) != 0))) {
                    pacmandX = reqDx;
                    pacmandY = reqDy;
                    viewDx = pacmandX;
                    viewDy = pacmandY;
                }
            }


            if ((pacmandX == -1 && pacmandY == 0 && (ch & 1) != 0)          //falba utkoes eseten, a pacman pozicioja ne valtozzon
                    || (pacmandX == 1 && pacmandY == 0 && (ch & 4) != 0)
                    || (pacmandX == 0 && pacmandY == -1 && (ch & 2) != 0)
                    || (pacmandX == 0 && pacmandY == 1 && (ch & 8) != 0)) {
                pacmandX = 0;
                pacmandY = 0;
            }
        }
        int PACMAN_SPEED = 4;
        pacmanX = pacmanX + PACMAN_SPEED * pacmandX;        // hany egyseget lep egy idopillanatban
        pacmanY = pacmanY + PACMAN_SPEED * pacmandY;
    }

    public void pacmanEatSmall(int pos, short ch){
        boolean musicMute;
        int auxiliari;
        screenData[pos] = (short) (ch & 15);
        auxiliari = board.getScore();
        board.setScore(auxiliari);      //score novelese a board-ban is
        score++;                        //score novelese
        musicMute = board.getMusicMute();  //esetleges mute lekredezese
        if(!musicMute){             //zene lejatszasa
            File file = new File("src/resources/sounds/pacman_chomp.wav");
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                AudioFormat format = audioInputStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);

                eating = (Clip) AudioSystem.getLine(info);

                eating.open(audioInputStream);

                if (!soundPlaying) {
                    eating.start();
                    soundPlaying = true;
                }

                if (score % 4 == 0) {       //csak minden negyedik golyo evesekor jatsza le a hangot kulomben tul idegesito
                    soundPlaying = false;
                }


            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    public void pacmanEatBig(int pos, short ch){
        boolean musicMute;
        screenData[pos] = (short) (ch & 31);
        pacmanAttack=true;
        setPacmanAttack(true);//tamadasi kepesseg igazra allitasa
        AttackReminder attck = new AttackReminder(this, 15);          //15 masodpercig tart ez a hatas aztan visszallitja false-ra
        musicMute = board.getMusicMute();
        if(!musicMute){                         //zene lejatszasa
            File file = new File("src/resources/sounds/pacman_eatbig.wav");
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                AudioFormat format = audioInputStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);

                eatbig = (Clip) AudioSystem.getLine(info);

                eatbig.open(audioInputStream);

                eatbig.start();
                Thread.sleep(1);

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void drawPacman(Graphics2D g2d) {        //pacman kirajzolasa

        if (viewDx == -1) {
            drawPacnanLeft(g2d);
        } else if (viewDx == 1) {
            drawPacmanRight(g2d);
        } else if (viewDy == -1) {
            drawPacmanUp(g2d);
        } else {
            drawPacmanDown(g2d);
        }
    }

    public void drawPacmanUp(Graphics2D g2d) {  //pacman felfele mutato kirajzolasa

        switch (pacmanAnimPos) {
            case 1 -> g2d.drawImage(pacman2Up, pacmanX + 1, pacmanY + 1, null);
            case 2 -> g2d.drawImage(pacman3Up, pacmanX + 1, pacmanY + 1, null);
            case 3 -> g2d.drawImage(pacman4Up, pacmanX + 1, pacmanY + 1, null);
            default -> g2d.drawImage(pacman1, pacmanX + 1, pacmanY + 1, null);
        }
    }

    public void drawPacmanDown(Graphics2D g2d) {    //pacman lefele mutato kirajzolasa

        switch (pacmanAnimPos) {
            case 1 -> g2d.drawImage(pacman2Down, pacmanX + 1, pacmanY + 1, null);
            case 2 -> g2d.drawImage(pacman3Down, pacmanX + 1, pacmanY + 1, null);
            case 3 -> g2d.drawImage(pacman4Down, pacmanX + 1, pacmanY + 1, null);
            default -> g2d.drawImage(pacman1, pacmanX + 1, pacmanY + 1, null);
        }
    }

    public void drawPacnanLeft(Graphics2D g2d) {    //pacman balra mutato kirajzolasa

        switch (pacmanAnimPos) {
            case 1 -> g2d.drawImage(pacman2Left, pacmanX + 1, pacmanY + 1, null);
            case 2 -> g2d.drawImage(pacman3Left, pacmanX + 1, pacmanY + 1, null);
            case 3 -> g2d.drawImage(pacman4Left, pacmanX + 1, pacmanY + 1, null);
            default -> g2d.drawImage(pacman1, pacmanX + 1, pacmanY + 1, null);
        }
    }

    public void drawPacmanRight(Graphics2D g2d) {   //pacman jobbra mutato kirajzolasa

        switch (pacmanAnimPos) {
            case 1 -> g2d.drawImage(pacman2Right, pacmanX + 1, pacmanY + 1, null);
            case 2 -> g2d.drawImage(pacman3Right, pacmanX + 1, pacmanY + 1, null);
            case 3 -> g2d.drawImage(pacman4Right, pacmanX + 1, pacmanY + 1, null);
            default -> g2d.drawImage(pacman1, pacmanX + 1, pacmanY + 1, null);
        }
    }

    public int getPacmanAnimPos() {     //pacman animaciohoz szukseges ertek, mivel tobb kepet rajzol ki hogy ugy tunjon mintha ki es be csukna a szjat
        return this.pacmanAnimPos;
    }

    public boolean getPacmanAttack(){
        return this.pacmanAttack;
    }  //pacman tamadasi kepesseg lekerdezese

    public int getPacmanX(){
        return this.pacmanX;
    }   //pacman x koordinata lekerese

    public int getPacmanY(){
        return this.pacmanY;
    }   //pacman y koordinata lekerese

    public void setPacmanX(int value){
        this.pacmanX = value;
    }   //pacman x koordinata beallitasa

    public void setPacmanY(int value){
        this.pacmanY = value;
    }   //pacman y koordinata beallitasa

    public void setPacmandX(int value){
        this.pacmandX = value;
    }   //pacman x tnegelyen valo iranyanak bealiitasa

    public void setPacmandY(int value){
        this.pacmandY = value;
    }   //pacman y tnegelyen valo iranyanak bealiitasa

    public void setPacmanReqDx(int value){
        this.reqDx = value;
    }   //a jatekos altal megadott x irany beallitasa

    public void setPacmanReqDy(int value){
        this.reqDy = value;
    }   //a jatekos altal megadott y irany beallitasa

    public void setPacmanViewDx(int value){
        this.viewDx = value;
    }   //pacman iranya

    public void setPacmanViewDy(int value){
        this.viewDy = value;
    }   //pacman iranya

    public void setPacmanAnimPos(int value){
        this.pacmanAnimPos = value;
    }   //animaciohoz szukseges ertek allitasa

    public void setPacmanAttack( boolean value){
        this.pacmanAttack = value;
    }   //tamadasi kepesseg beallitasa
}
