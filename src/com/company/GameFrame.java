package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class GameFrame extends JFrame implements ActionListener {
    private final JMenuItem newGame;            // deklaralok egy JMenuItem tipusu objektumot, amit kesobb az ablakom(frame) menubarjaba illesztek, uj jatek inditasara ad lehetoseget
    private final JMenuItem exit;               // deklaralok egy JMenuItem tipusu objektumot, amit kesobb az ablakom(frame) menubarjaba illesztek, kilepest vegzi el
    private final JMenuItem scores;             // deklaralok egy JMenuItem tipusu objektumot, amit kesobb az ablakom(frame) menubarjaba illesztek, pontszamok megjelenitesere es mentesere szolgal
    private final JMenuItem saveScore;          // deklaralok egy JMenuItem tipusu objektumot, amit kesobb az ablakom(frame) menubarjaba illesztek, ez fogja elmenteni a pontszamokat
    private final GameBoard board;              // deklaralok egy GameBoard tipusu objektumot, ami a jatek fo panelje lesz
    private final JMenuItem easy;               // deklaralok egy JMenuItem tipusu objektumot, amit kesobb az ablakom(frame) menubarjaba illesztek, a nehezseget lehet majd ezzel allitani
    private final JMenuItem hard;               // deklaralok egy JMenuItem tipusu objektumot, amit kesobb az ablakom(frame) menubarjaba illesztek, a nehezseget lehet majd ezzel allitani
    private final JMenuItem normal;             // deklaralok egy JMenuItem tipusu objektumot, amit kesobb az ablakom(frame) menubarjaba illesztek, a nehezseget lehet majd ezzel allitani
    private final JTable scoreBoard;            // deklaralok egy JMenuItem tipusu objektumot, amit kesobb az ablakom(frame) menubarjaba illesztek, ez fogja megjeleniteni az elert pontszamokat

    DefaultTableModel model = new DefaultTableModel();  // letrehozok egy tablazatot aminek 0 sora es 0 oszlopa van

    private final JPanel settingsPanel, cardPanel;
    private final CardLayout card;                    //CardLayoutot hasznalok a panelek kozotti valtasra

    public GameFrame() throws HeadlessException {
        cardPanel = new JPanel();
        settingsPanel = new JPanel();
        board = new GameBoard();    //board letrehozasa, ezen fog futni a jatek
        settingsPanel.setBackground(Color.BLACK);
        settingsPanel.setFocusable(false);
        settingsPanel.setLayout(null);                                  //layout nullazasa annak erdekeben hogy egyenileg tudjam a gombokat elhelyezni

        playButton();
        muteAndUnMute();
        mapButtons();
        difficultyButtons();

        card = new CardLayout();    //CardLayout letrehozas
        cardPanel.setLayout(card);

        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenu score = new JMenu("Score");
        JMenu difficulty = new JMenu("Difficulty");
        newGame = new JMenuItem("New game");
        exit = new JMenuItem("Exit");
        scores = new JMenuItem("Scoreboard");
        saveScore = new JMenuItem("Save score");
        easy = new JMenuItem("Easy");
        normal = new JMenuItem("Normal");
        hard = new JMenuItem("Hard");
        //action listenerek hozzaadasa a menupontokhoz
        newGame.addActionListener(this);
        exit.addActionListener(this);
        scores.addActionListener(this);
        saveScore.addActionListener(this);
        easy.addActionListener(this);
        normal.addActionListener(this);
        hard.addActionListener(this);
        //almenupontok beillesztese a menupontokhoz
        menu.add(newGame);
        menu.add(exit);
        score.add(scores);
        score.add(saveScore);
        difficulty.add(easy);
        difficulty.add(normal);
        difficulty.add(hard);
        //menupontok hozzaadasa a menubarhoz
        mb.add(menu);
        mb.add(difficulty);
        mb.add(score);
        setJMenuBar(mb);

        //kezdeti score tomb letrehozasa
        Object[][] data = {
                {"Armand", 420},
                {"Alexa", 69},
                {"Z.Danny", 0},
        };

        Object[] columnNames = {"Name", "Score"};
        Font smallFont = new Font("Helvetica", Font.BOLD, 14);

        //scoreBoard tabla feltolteses a kezdeti adatokkal
        scoreBoard = new JTable();
        scoreBoard.setForeground(Color.YELLOW);
        scoreBoard.setBackground(Color.BLACK);
        model.setColumnIdentifiers(columnNames);
        scoreBoard.setFont(smallFont);
        scoreBoard.setGridColor(Color.BLUE);
        scoreBoard.setRowHeight(30);
        scoreBoard.setModel(model);
        for (int i = 0; i < 3; i++) {
            model.addRow(data[i]);
        }

        addKeyListener(new PacAdapter(board));          //key listenerek hozzaadasa a boardhoz amin a jatek futni fog
        cardPanel.add(settingsPanel);                   //cardLayouthoz valo panel hozzaadas
        cardPanel.add(board);                           //cardLayouthoz valo panel hozzaadas
        frameSettings();
    }

    private void playButton(){
        JButton changeButton = new JButton("Play");                 //play gomb letrehozasa, ami atvisz majd minket a board panelre amin a jatek fut
        changeButton.addActionListener(this);                         //action listener hozzaadasa
        changeButton.setFocusable(false);                               //focus beallitasa hamis ertekre, hogy ez a gomb ne zavarja a jatek mukodeset
        changeButton.setBounds(225, 600,200,60);      //pozicio es meret beallitasa
        changeButton.setBackground(Color.BLUE);                         //szin beallitasa
        changeButton.setForeground(Color.YELLOW);                       //szoveg szinenek a beallitasa
        settingsPanel.add(changeButton);
    }

    private void mapButtons(){
        //mapok kepeinek betoltese illetve gombok letrehozasa ezek vkivalasztasanak erdekeben
        ImageIcon map1Icon = new ImageIcon("src/resources/images/map1.png");
        JButton map1Button = new JButton();
        map1Button.setIcon(map1Icon);
        map1Button.setFocusable(false);
        map1Button.setBounds(10,40,200,250);
        map1Button.addActionListener(e -> board.setChosenMap(1));

        ImageIcon map2Icon = new ImageIcon("src/resources/images/map2.png");
        JButton map2Button = new JButton();
        map2Button.setIcon(map2Icon);
        map2Button.setFocusable(false);
        map2Button.setBounds(230,40,200,250);
        map2Button.addActionListener(e -> board.setChosenMap(2));

        ImageIcon map3Icon = new ImageIcon("src/resources/images/map3.png");
        JButton map3Button = new JButton();
        map3Button.setIcon(map3Icon);
        map3Button.setFocusable(false);
        map3Button.setBounds(450,40,200,250);
        map3Button.addActionListener(e -> board.setChosenMap(3));

        ImageIcon mapRandomIcon = new ImageIcon("src/resources/images/random.png");
        JButton mapRandomButton = new JButton();
        mapRandomButton.setIcon(mapRandomIcon);
        mapRandomButton.setFocusable(false);
        mapRandomButton.setBounds(290,300,80,40);
        mapRandomButton.setBorderPainted(false);
        mapRandomButton.addActionListener(e -> board.setChosenMap(4));

        settingsPanel.add(map1Button);
        settingsPanel.add(map2Button);
        settingsPanel.add(map3Button);
        settingsPanel.add(mapRandomButton);
    }

    private void difficultyButtons(){
        JButton easyButton = new JButton("Easy");
        easyButton.addActionListener(this);
        easyButton.setFocusable(false);
        easyButton.setBounds(250, 370,150,50);
        easyButton.setBackground(Color.YELLOW);
        easyButton.setForeground(Color.BLUE);

        JButton normalButton = new JButton("Normal");
        normalButton.addActionListener(this);
        normalButton.setFocusable(false);
        normalButton.setBounds(250, 430,150,50);
        normalButton.setBackground(Color.YELLOW);
        normalButton.setForeground(Color.BLUE);

        JButton hardButton = new JButton("Hard");
        hardButton.addActionListener(this);
        hardButton.setFocusable(false);
        hardButton.setBounds(250, 490,150,50);
        hardButton.setBackground(Color.YELLOW);
        hardButton.setForeground(Color.BLUE);

        settingsPanel.add(easyButton);
        settingsPanel.add(normalButton);
        settingsPanel.add(hardButton);
    }

    private void muteAndUnMute(){
        //a kovetezokben szinten gombokat hozok letre ugyanugz mint fentebb
        ImageIcon muteIcon = new ImageIcon("src/resources/images/mute.png");        //kep betoltese
        JButton muteButton = new JButton();
        muteButton.setIcon(muteIcon);                                                       // kep hozzaadasa a gombhoz
        muteButton.setFocusable(false);
        muteButton.setBounds(550,600,60,60);
        muteButton.setBorderPainted(false);
        muteButton.addActionListener(e -> {
            board.setMusicMute(true);                //a setMusicMute parancs meghivasa gombnyomas eseten
        });

        ImageIcon unMuteIcon = new ImageIcon("src/resources/images/unmute.png");
        JButton unMuteButton = new JButton();
        unMuteButton.setIcon(unMuteIcon);
        unMuteButton.setFocusable(false);
        unMuteButton.setBounds(30,602,60,60);
        unMuteButton.setBorderPainted(false);
        unMuteButton.addActionListener(e -> board.setMusicMute(false));

        settingsPanel.add(muteButton);
        settingsPanel.add(unMuteButton);
    }

    private void frameSettings(){
        ImageIcon image = new ImageIcon("src/resources/images/pacmanicon.png"); //az applikaciohoz egy icon beallitasa
        setIconImage(image.getImage());

        this.setFocusable(true);                        //focus igazra allitasa, hogy a key listenerek mukodjenek
        this.add(cardPanel);                            //a cardok hozzaadasa a framehez

        //frame alapveto beallitasai
        this.setTitle("Pacman");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(672, 760);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == newGame) {
            board.setInGame(false);         //uj jatek kezdete
            repaint();                      //map ujrarajzolasa
        }

        if(e.getSource() == exit) {
            dispose();                      //kilepes
            System.exit(0);
        }

        if(e.getSource() == scores) {       //scoreboard frame letrehozasa es megjelenites
            board.setInGame(false);
            JFrame frame = new JFrame("Scoreboard");
            frame.add(scoreBoard);
            frame.setLocationRelativeTo(null);
            frame.setSize(400, 610);
            frame.setResizable(false);
            frame.setVisible(true);

        }

        if(e.getSource() == saveScore) {        //uj score hozzaadasa
            board.setInGame(false);
            String name = JOptionPane.showInputDialog(this, "Enter name champ!");
            Object[] row = {name, board.getScore()};
            model.addRow(row);
        }
        if(e.getSource() == easy) {
            board.setDifficulty("easy");        //nehezseg beallitasa a menupontbol
        }
        if(e.getSource() == normal) {
            board.setDifficulty("normal");      //nehezseg beallitasa a menupontbol
        }
        if(e.getSource() == hard) {
            board.setDifficulty("hard");        //nehezseg beallitasa a menupontbol
        }
        if(e.getActionCommand().equals("Play")) {   //play gomb megnyomasa altal megjeleni a jatek panelje
                card.next(cardPanel);
        }
        if(e.getActionCommand().equals("Easy")) {
            board.setDifficulty("easy");        //nehezseg beallitasa jatek elott gombokkal
        }
        if(e.getActionCommand().equals("Normal")) {
            board.setDifficulty("normal");      //nehezseg beallitasa jatek elott gombokkal
        }
        if(e.getActionCommand().equals("Hard")) {
            board.setDifficulty("hard");        //nehezseg beallitasa jatek elott gombokkal
        }
    }
}


