package com.company;

import java.util.Timer;
import java.util.TimerTask;

public class AttackReminder {       //ez az osztaly tuljadonkeppen egy visszaszamlalo, ami 15 masodpercig szamol vissza
                                    //ez akkor indul el ha a pacman egy nagy golyot ves fel ezzel kepes megenni a szornyeket, 15 masodpercig
                                    //ha ez az ido letelt ismet meghal ha utkozik veluk
    Timer timer;
    Pacman pacman;

    public AttackReminder(Pacman pacman, int seconds) {
        this.pacman = pacman;
        timer = new Timer();
        timer.schedule(new RemindTask(), seconds*1000);
    }

    class RemindTask extends TimerTask {
        public void run() {
            pacman.setPacmanAttack(false);  //tamadasi kepesseg atallitasa
            timer.cancel(); //megallitas
        }
    }


}
