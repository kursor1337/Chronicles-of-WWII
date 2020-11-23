package com.kypcop.chroniclesofwwii.game.logic.player;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;

import com.kypcop.chroniclesofwwii.game.logic.Nation;
import com.kypcop.chroniclesofwwii.game.logic.divisions.Type;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Player {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ROLE_ENEMY, ROLE_ME})
    @interface Role {}
    public static final int ROLE_ENEMY = 601;
    public static final int ROLE_ME = 602;


    private int role;
    private int turn;
    private final Nation nation;
    private int infantry;
    private int armored;
    private int artillery;

    public Player(@Role int role, @IntRange(from = 0, to = 1) int turn,
                  Nation nation, int infantry, int armored, int artillery){
        this.role = role;
        this.nation = nation;
        this.infantry = infantry;
        this.armored = armored;
        this.artillery = artillery;
    }

    public int getTurn(){
        return turn;
    }

    public Nation getNation() {
        return nation;
    }

    private void setRole(@Role int role){
        this.role = role;
    }

    @Role
    public int getRole(){
        return role;
    }

    public void invertRole(){
        if(getRole() == ROLE_ENEMY){
            setRole(ROLE_ME);
        } else{
            setRole(ROLE_ENEMY);
        }
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
