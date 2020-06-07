package com.kypcop.chroniclesofwwii.game.Logic.Missions;

import com.kypcop.chroniclesofwwii.game.Logic.Nation;
import com.kypcop.chroniclesofwwii.game.Logic.Player.Player;

public class Mission{

    private String intro;
    private final int id;
    private String name;
    private Nation enemy1;
    private Nation enemy2;
    private int player1Infantry;
    private int player1Armored;
    private int player1Artillery;
    private int player2Infantry;
    private int player2Armored;
    private int player2Artillery;
    private Player player1;
    private Player player2;

    Mission(int id, String name, String intro,
            Nation enemy1, int player1Infantry, int player1Armored, int player1Artillery,
            Nation enemy2, int player2Infantry, int player2Armored, int player2Artillery){
        this.id = id;
        this.name = name;
        this.intro = intro;
        this.player1Infantry = player1Infantry;
        this.player1Armored = player1Armored;
        this.player1Artillery = player1Artillery;
        this.player2Infantry = player2Infantry;
        this.player2Armored = player2Armored;
        this.player2Artillery = player2Artillery;
        this.enemy1 = enemy1;
        this.enemy2 = enemy2;
        player1 = new Player(Player.FIRST, enemy1, player1Infantry, player1Armored, player1Artillery);
        player2 = new Player(Player.SECOND, enemy2, player2Infantry, player2Armored, player2Artillery);
    }



    public void setIntro(String id){
        intro = id;
    }

    public String getIntro(){
        return intro;
    }


    public Player getPlayer1() {
        if(player1 == null){
            player1 = new Player(Player.FIRST, enemy1, player1Infantry, player1Armored, player1Artillery);
        }
        return player1;
    }

    public Player getPlayer2(){
        if(player2 == null){
            player2 = new Player(Player.SECOND, enemy2, player2Infantry, player2Armored, player2Artillery);
        }
        return player2;
    }
    public String getMissionName() {
        return name;
    }

    public int getId(){
        return id;
    }
}
