package com.kypcop.chroniclesofwwii.game.Logic.Divisions;

public enum Type {
    ARMORED, INFANTRY, ARTILLERY;


    public static String toString(Type type){
        switch (type){
            case INFANTRY:
                return "INFANTRY";
            case ARMORED:
                return "ARMORED";
            case ARTILLERY:
                return "ARTILLERY";
            default:
                return ":(";
        }
    }
}
