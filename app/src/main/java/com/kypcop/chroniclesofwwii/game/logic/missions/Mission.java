package com.kypcop.chroniclesofwwii.game.logic.missions;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.kypcop.chroniclesofwwii.game.logic.Nation;
import com.kypcop.chroniclesofwwii.game.logic.player.Player;

public class Mission implements Cloneable{

    private static final transient Gson gson = new Gson();
    public static final int DEFAULT = -865;

    private boolean isCompleted = false;
    private String intro;
    private final int id;
    private final String name;
    private Player playerEnemy;
    private Player playerMe;


    public Mission(int id, String name, String intro,
                   Nation enemy1, int player1Infantry, int player1Armored, int player1Artillery,
                   Nation enemy2, int player2Infantry, int player2Armored, int player2Artillery){
        this.id = id;
        this.name = name;
        this.intro = intro;
        playerEnemy = new Player(Player.ROLE_ENEMY, 1, enemy1, player1Infantry, player1Armored, player1Artillery);
        playerMe = new Player(Player.ROLE_ME, 0, enemy2, player2Infantry, player2Armored, player2Artillery);
    }

    public void setIntro(String id){
        intro = id;
    }

    public String getIntro(){
        return intro;
    }


    public Player getEnemyPlayer() {
        return playerEnemy;
    }

    public void invertPlayers(){
        Player inter = playerEnemy;
        playerEnemy = playerMe;
        playerEnemy.invertRole();
        playerMe = inter;
        playerMe.invertRole();
    }

    public Player getMePlayer(){
        return playerMe;
    }
    public String getMissionName() {
        return name;
    }

    public int getId(){
        return id;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted() {
        isCompleted = true;
    }

    @NonNull
    @Override
    public String toString() {
        return getMissionName();
    }

    public static String toJson(Mission mission){
        return gson.toJson(mission);
    }

    public static Mission fromJson(String string){
        try{
            return gson.fromJson(string, Mission.class);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    @Override
    public Mission clone() throws CloneNotSupportedException {
        return ((Mission) super.clone());
    }
}
