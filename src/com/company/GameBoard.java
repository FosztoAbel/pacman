package com.company;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;
import java.util.Scanner;

public class GameBoard extends JPanel implements ActionListener, Serializable {

    private Dimension d;    //a panelem dimenzioi egy objektumban tarolva(szelesseg es magassag)
    private final Font smallFont = new Font("Helvetica", Font.BOLD, 22);    //betutipus beallitasa

    private Color dotColor;       //a palyan elhelzett golyok szinenek inicializalasa
    private Color mazeColor;                            //a palya szinenek valtozoja

    private Timer timer;        //timer object

    private int lifeLeft,score; //hatralevo eletek illetve pontszam valtozoi

    private boolean inGame = false;     //a jatek folyik e epp
    private boolean dying = false;      //pacman halalanka ellenorzesere hasznalt valtozo
    private boolean nextLevel = false;  //kovetkezo szint ellenorzese
    private boolean soundPlaying = false;   //epp van e lejatszott hang
    private boolean musicMute;          //jatek hangjanak esetleges nemitasa

    private final int blockSize = 24;   //meretek
    private final int nBlocks = 28;
    private final int screenSize = nBlocks * blockSize;
    private final int pacmanAnimDelay = 2;
    private int diffi = 1;  //nehezseg
    private final int[] validSpeeds = {1, 2, 3, 4, 6, 8};  // lehetseges sebessegek

    private int pacmanAnimPos;  // pacman animaciojanak a pozicioja
    private int pacmanAnimCount = pacmanAnimDelay;
    private int pacmanAnimDir = 1;
    private int reqDx;
    private int reqDy;
    private int nGhosts;        //jelenlegi ghostok szama
    private int currentSpeed = 3;   //sebesseg
    private int chosenMap = 1;      //valasztott map

    private short[] screenData;     //palya betoltesere hasznalt tomb
    private short[] levelData;

    private final Ghost ghost;        //Ghost objektum
    private final Pacman pacman;      //Pacman objektum

    public GameBoard() {    //konstruktor
        initializeVariables();
        pacman = new Pacman(this);          //Pacman osztaly peldanyositasa
        ghost = new Ghost(this, pacman);    //Ghost osztalz peldanyositasa
        initializeGameBoard();
        nGhosts = ghost.getNumberOfGhosts();      //ghostok szamanak beallitasa
        pacmanAnimPos = pacman.getPacmanAnimPos();  //pacman animaciojanak a pozicioja
    }

    protected void initializeGameBoard(){         //panel inicilizalasa
        setFocusable(true);         //focus igazra allitasa, key listenerek mukodesenek erdekeben
        setBackground(Color.black); //hatter beallitasa
        initializeGame();
    }

    protected void initializeMap(){           //map betoltese
         chosenMap= getRandomNumberInRange(1, 3);   // random szam generalasa [1,3] intervallumbol, majd ez alapjan egy elore letrehozott map betoltese
        if(chosenMap == 1){
            try {
                Scanner scanner = new Scanner(new File("src/resources/textfile/maze1.txt"));
                levelData = new short[nBlocks * nBlocks];
                int i = 0;
                while (scanner.hasNextShort()) {
                    levelData[i] = scanner.nextShort();
                    System.out.println(levelData[i]);
                    i++;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(chosenMap == 2){
            try {
                Scanner scanner = new Scanner(new File("src/resources/textfile/maze2.txt"));
                levelData = new short[nBlocks * nBlocks];
                int i = 0;
                while (scanner.hasNextShort()) {
                    levelData[i] = scanner.nextShort();
                    System.out.println(levelData[i]);
                    i++;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(chosenMap == 3){
            try {
                Scanner scanner = new Scanner(new File("src/resources/textfile/maze3.txt"));
                levelData = new short[nBlocks * nBlocks];
                int i = 0;
                while (scanner.hasNextShort()) {
                    levelData[i] = scanner.nextShort();
                    System.out.println(levelData[i]);
                    i++;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    protected void initializeVariables(){ //valtozok inicializalasa
        initializeMap();
        screenData = new short[nBlocks * nBlocks];  //egy screenData tomb letrehozasa a megfelelo merettel, a palya adatianka eltarolasara
        mazeColor = new Color(5, 5, 250);   //szin beallitasa
        dotColor = Color.YELLOW;
        d = new Dimension(700, 700);    //magassag szelleseg beallitasa es a d objektumba valo tarolasa

        timer = new Timer(40, this);    //timer inditasa
        timer.start();

    }

    protected void initializeGame() {       //jatek inicializalasa
        soundPlaying = false;
        lifeLeft = 3;           //kezdeti 3 elet beallitasa
        score = 0;              //kezdeti pontok beallitasa
        nGhosts = 6;            //kezdeti szornyek beallitasa
        currentSpeed = 3;       //sebesseg beallitasa
        initializeLevel();
    }

    protected void initializeLevel() {        //szint inicializalasa
        int i;
        for (i = 0; i < nBlocks * nBlocks; i++) {
            screenData[i] = levelData[i];
        }

        continueLevel();
    }

    private void playGame(Graphics2D g2d) {
        if (!dying) {                   //ha a pacman nem utkozott szornnyel akkor mozgatja a pacmant es a ghostot, kirajzolja illetve ellenotzi hogy helyesen lep
            pacman.movePacman();        //pacman mozgatasa
            pacman.drawPacman(g2d);     //pacman kirajzolasa
            ghost.moveGhosts(g2d);      //ghostok mozgatasa
            checkMaze();                //ellenorzi hogy van e meg felszedni valo golyo a palyan
        } else chechDeath();            //kulomben a pacman szorennyel utkozott ezert tobb muveletet kell vegzni
    }

    private void doAnim() {             //animacio elvegzese
        pacmanAnimCount--;

        if (pacmanAnimCount <= 0) {
            pacmanAnimCount = pacmanAnimDelay;
            pacmanAnimPos = pacmanAnimPos + pacmanAnimDir;
            pacman.setPacmanAnimPos(pacmanAnimPos + pacmanAnimDir);

            int PACMAN_ANIM_COUNT = 4;
            if (pacmanAnimPos == (PACMAN_ANIM_COUNT - 1) || pacmanAnimPos == 0) {
                pacmanAnimDir = -pacmanAnimDir;
            }
        }
    }

    public void chechDeath   () {      //azon feladatok elvegzese ami akkor szukseges ha a pacman utkozik egy szornyel
        lifeLeft--;     //elet csokkentes

        if (lifeLeft == 0) {
            inGame = false;     //halal
        }
        if(!musicMute){     //zene lejatszasa, ha a jatekos nem nemitotta le a jatekot
        File file = new File("src/resources/sounds/pacman_death.wav");
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            Clip dead = (Clip) AudioSystem.getLine(info);

            dead.open(audioInputStream);

            dead.start();
            Thread.sleep(1000);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            e.printStackTrace();
        }
        }
        continueLevel();  //mivel a pacman nem vesztette el az osszes eletet ezert a jatek folytatodik
    }

    private void checkMaze() {              //ellenorzi hogy minden golyo eltunt e mar a palyaro, ha igen ujra betolti a palyat feltoltvem golyokkal, de ezuttal tobb ghost lesz es a sebbesseg is megno
        short i = 0;
        boolean finished = true;

        while (i < nBlocks * nBlocks && finished) {
            if ((screenData[i] & 48) != 0) {
                finished = false;               //meg van golyo a palyan
            }
            i++;
        }

        if (finished) {         //ha a szintet teljesitette kovetkezo szintre lep amit ujra betolt

            score += 50;        //bonus pontok

            //maximalis ghostok szama
            int maxGhosts = 12;
            if (nGhosts < maxGhosts) {
                nGhosts++;          //szornyek szamanak novelese
                ghost.setNumberOfGhosts(nGhosts);
            }

            int maxSpeed = 6;
            if (currentSpeed < maxSpeed) {
                currentSpeed++;     //gyorsasag novelese
            }

            inGame = false;
            nextLevel = true;
            initializeLevel();  //palya ismetelt betoltese
        }
    }

    private void continueLevel() {      //ha apacman ghostal utkozik de meg nem hal meg folytatjuk a jatekot a ghostokat a kezdeti poziciora allitjuk es a pacmant is

        short i;
        int dx = 1;
        int random;         //ghost adatinak lekerese getterekkel a ghost osztalybol
        int[] ghostX = ghost.getGhostX();
        int[] ghostY = ghost.getGhostY();
        int[] ghostDx = ghost.getGhostDx();
        int[] ghostDy = ghost.getGhostDy();
        int[] ghostSpeed = ghost.getGhostSpeed();


        for (i = 0; i < nGhosts; i++) {

            ghostY[i] = blockSize * nBlocks / 2;
            ghostX[i] = blockSize * nBlocks / 2;
            ghostDy[i] = 0;
            ghostDx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random] * diffi;        //ghost sebessegenek beallitasa
        }
        //pacman poziciojanak beallitasa
        pacman.setPacmanX(336);
        pacman.setPacmanY(504);
        pacman.setPacmandX(0);
        pacman.setPacmandY(0);
        pacman.setPacmanReqDx(0);
        pacman.setPacmanReqDy(0);
        pacman.setPacmanViewDx(-1);
        pacman.setPacmanViewDy(0);
        reqDx = 0;
        reqDy = 0;              // a jatekos gombnyomasai altal megadott irany
        dying = false;
    }

    public void setDying(boolean value){    //ghostal valo utkozeskor a pacman allapotanak beallitasa dying mivel lehet halott, de elhet hogy ez az ertek hamis, mivel megolte a ghostot
        this.dying = value;
    }

    public short[] getScreenData(){         //a ghost illetve pacman osztaly innen kapja meg a mezok adatait
        return this.screenData;
    }

    public void setDifficulty(String s){    //nehezseg beallitasa, vagyis a speed valtoztatasa
        switch (s) {
            case "hard" -> this.diffi = 2;
            case "easy" -> this.diffi = 1;
            case "normal" -> this.diffi = 1;
        }
    }

    public void setScore(int score) {       //score beallitasa
        score++;
        this.score= score;
    }

    @Override
    public void paintComponent(Graphics g) {    //kirajzolas
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) { //rajzolas

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        drawScore(g2d);
        doAnim();

        if (inGame) {
            if (!timer.isRunning()) {
                shopPauseScreen(g2d);
            } else {
                playGame(g2d);
            }
        } else {
            if (nextLevel) {
                showNextLevel(g2d);
            } else {
                timer.start();
                showIntroScreen(g2d);
            }
        }

        g2d.dispose();
    }

    private void drawMaze(Graphics2D g2d) {  //palya kiralyzolasa

        short i = 0;
        int x, y;

        for (y = 0; y < screenSize; y += blockSize) {
            for (x = 0; x < screenSize; x += blockSize) {

                g2d.setColor(mazeColor);
                g2d.setStroke(new BasicStroke(2));

                //balra rajzol vonalat
                if ((screenData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + blockSize - 1);
                }
                //felulre rajzol vonalat
                if ((screenData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + blockSize - 1, y);
                }
                //jobbra rajzol vonalat
                if ((screenData[i] & 4) != 0) {
                    g2d.drawLine(x + blockSize - 1, y, x + blockSize - 1, y + blockSize - 1);
                }
                // alulra rajzol vonalat
                if ((screenData[i] & 8) != 0) {
                    g2d.drawLine(x, y + blockSize - 1, x + blockSize - 1, y + blockSize - 1);
                }
                // kicsi golyokat rajzol
                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(dotColor);
                    g2d.fillOval(x + 11, y + 11, 6, 6);
                }
                //nagy golyokat rajzol
                if ((screenData[i] & 32) != 0){
                    g2d.setColor(dotColor);
                    g2d.fillOval(x + 4, y + 9, 15, 15);
                }

                i++;
            }
        }
    }

    private void drawScore(Graphics2D g) {      //pontszam kiralyzolasa
        String s;

        if(pacman.getPacmanAttack()) {  //lekerdezem hogy meddig tart a pacmanen a nagy golyok altal adott KILL hatas
            g.setFont(smallFont);
            g.setColor(Color.WHITE);
            g.drawString("KILL", screenSize / 2 - 96, screenSize + 25);
        }
            else {                              //ha mar lejart a 15 masodperc amig olni tud kiirja hogy RUN
            g.setFont(smallFont);
            g.setColor(Color.WHITE);
            g.drawString("RUN", screenSize / 2 - 96, screenSize + 25);
        }

        Image pacman4Left = new ImageIcon("src/resources/images/left3.png").getImage();
            //score kirajzolasa
        g.setFont(smallFont);
        g.setColor(Color.WHITE);
        s = "Score: " + score;
        g.drawString(s, screenSize / 2 + 96, screenSize + 25);

        for (int i = 0; i < lifeLeft; i++) {        //hatralevo eletek kirajzolasa
            g.drawImage(pacman4Left, i * 28 + 8, screenSize + 8, this);
        }
    }

    private void shopPauseScreen (Graphics2D g2d) {       //jatek megallitasa eseten a Game Paused felirat megjelenitese
        g2d.setColor(new Color(190, 90, 48));
        g2d.fillRect(50, screenSize / 2 - 30, screenSize - 100, 50);
        g2d.setColor(Color.RED);
        g2d.drawRect(50, screenSize / 2 - 30, screenSize - 100, 50);

        String s = "Game PAUSED.";
        Font small = new Font("Helvetica", Font.BOLD, 30);
        FontMetrics metr = this.getFontMetrics(small);

        g2d.setColor(Color.YELLOW);
        g2d.setFont(small);
        g2d.drawString(s, (screenSize - metr.stringWidth(s)) / 2, screenSize / 2);
    }

    private void showNextLevel (Graphics2D g2d) {           //Next level felirat kiirasa abban az esetben ha a pacman felszedte az osszes golyot
        g2d.setColor(new Color(190, 90, 48));
        g2d.fillRect(50, screenSize / 2 - 30, screenSize - 100, 50);
        g2d.setColor(Color.RED);
        g2d.drawRect(50, screenSize / 2 - 30, screenSize - 100, 50);

        String s = "Press SPACE to start NEXT LEVEL.";
        Font small = new Font("Helvetica", Font.BOLD, 30);
        FontMetrics metr = this.getFontMetrics(small);

        g2d.setColor(Color.YELLOW);
        g2d.setFont(small);
        g2d.drawString(s, (screenSize - metr.stringWidth(s)) / 2, screenSize / 2);

    }

    private void showIntroScreen(Graphics2D g2d) {          //jatek elinditasa elott felirat, space billentyu lenyomasara indul a jatek

        g2d.setColor(new Color(190, 90, 48));
        g2d.fillRect(50, screenSize / 2 - 30, screenSize - 100, 50);
        g2d.setColor(Color.RED);
        g2d.drawRect(50, screenSize / 2 - 30, screenSize - 100, 50);

        String s = "Press SPACE to start.";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        g2d.setColor(Color.YELLOW);
        g2d.setFont(small);
        g2d.drawString(s, (screenSize - metr.stringWidth(s)) / 2, screenSize / 2);

        if(!musicMute) {            //zene lejatszasa ha nincs nemitva a jatek
            File file = new File("src/resources/sounds/pacman_beginning.wav");
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                AudioFormat format = audioInputStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);

                //hangfajlok szukseges objektumai
                Clip intro = (Clip) AudioSystem.getLine(info);

                intro.open(audioInputStream);
                if (!soundPlaying) {
                    intro.start();
                    soundPlaying = true;
                }

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }


    }

    private static int getRandomNumberInRange(int min, int max) {   //egy adott intervallumon beluli random szam generalasa

        if (min >= max) {
            throw new IllegalArgumentException("max nagyobb kell legyen mint ");
        }

        Random r = new Random();
        return r.nextInt((max - 1) + 1) + 1;
    }

    @Override
    public void actionPerformed(ActionEvent e) { repaint(); }

    public boolean isInGame() {return inGame; }     //jatek allapot lekerdezes

    protected void setReqDx(int reqDx) {this.reqDx = reqDx;}    //jatekos billentyu lenyomas altal megadott irany beallitasa

    protected void setReqDy(int reqDy) {this.reqDy = reqDy;}    //jatekos billentyu lenyomas altal megadott irany beallitasa

    public void setInGame(boolean inGame) { this.inGame = inGame; }     //a jatek aktualis allapota

    public Timer getTimer() { return timer; }

    public int getScore() { return score; }     //score lekredezese amit a GameFrame, Ghost es Pacman hasznal

    public int getReqDx(){return this.reqDx;}   //jatekos altal megadott irany lekerdezes

    public int getReqDy(){return this.reqDy;}   //jatekos altal megadott irany lekerdezes

    public boolean getMusicMute(){return this.musicMute;}   //esetleges mute allapot lekerdezese

    public Color getMazeColor() {return mazeColor;}

    public short[] getLevelData(){return this.levelData;}

    public int getLifeLeft() {return lifeLeft;}

    public int getnGhosts() {return nGhosts;}

    public int getCurrentSpeed(){return this.currentSpeed;}

    public boolean isDying() {return dying;}

    public Dimension getDimension() {return this.d;}

    public boolean isSoundPlaying() {return soundPlaying;}

    public void setMusicMute(boolean musicMute) {this.musicMute = musicMute;}   //mute allapot beallitasa gombnyomas eseten

    public void setChosenMap(int value){this.chosenMap = value;}    //kivalasztott map beallitasa
}