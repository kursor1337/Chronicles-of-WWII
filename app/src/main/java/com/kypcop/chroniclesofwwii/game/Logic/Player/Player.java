package com.kypcop.chroniclesofwwii.game.Logic.Player;

import com.kypcop.chroniclesofwwii.game.Logic.Divisions.Type;
import com.kypcop.chroniclesofwwii.game.Logic.Nation;

public class Player {
    public static final int FIRST = 601;
    public static final int SECOND = 602;

    protected final int playerId;
    private Nation nation;
    private int infantry;
    private int armored;
    private int artillery;

    public Player(int id, Nation nation, int infantry, int armored, int artillery){
        playerId = id;
        this.nation = nation;
        this.infantry = infantry;
        this.armored = armored;
        this.artillery = artillery;

    }


    public Nation getNation() {
        return nation;
    }

    public int getPlayerId(){
        return playerId;
    }

    public int getNumberOfDivisions(Type type){
        switch(type){
            case INFANTRY:
                return infantry;
            case ARMORED:
                return armored;
            case ARTILLERY:
                return artillery;
            default:
                return infantry + armored + artillery;
        }
    }

    public void deleteDivision(Type type){
        switch(type){
            case INFANTRY:
                infantry--;
            case ARMORED:
                armored--;
            case ARTILLERY:
                artillery--;
        }
    }

    public boolean lost() {
        return infantry + armored + artillery < 1;
    }
}
