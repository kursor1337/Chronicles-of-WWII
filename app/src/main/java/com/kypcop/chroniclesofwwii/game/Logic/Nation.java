package com.kypcop.chroniclesofwwii.game.Logic;

import android.graphics.drawable.Drawable;

import com.kypcop.chroniclesofwwii.R;

public enum Nation {
    BRITAIN, GERMANY, USSR, FRANCE, ITALY, USA, JAPAN;

    public static final String britain = "BRITAIN";
    public static final String germany = "GERMANY";
    public static final String ussr = "USSR";
    public static final String france = "FRANCE";
    public static final String italy = "ITALY";
    public static final String usa = "USA";
    public static final String japan = "JAPAN";

    public static Nation fromString(String stringNation) {
        switch (stringNation) {
            case britain:
                return BRITAIN;
            case germany:
                return GERMANY;
            case ussr:
                return USSR;
            case france:
                return FRANCE;
            case italy:
                return ITALY;
            case usa:
                return USA;
            case japan:
                return JAPAN;
            default:
                return null;
        }
    }

    public static int getImageID(Nation nation){
        Drawable image;
        switch(nation){
            case BRITAIN:
                return R.drawable.britain;
            case GERMANY:
                return R.drawable.germany;
            case USSR:
                return R.drawable.ussr;
            case FRANCE:
                return R.drawable.france;
            case ITALY:
                return R.drawable.italy;
            case USA:
                return R.drawable.usa;
            case JAPAN:
                return R.drawable.japan;
            default:
                return 0;
        }
    }

    public static String[] getNationNames(){
        String[] nations = new String[Nation.values().length];
        for(int i = 0; i < Nation.values().length; i++){
            nations[i] = Nation.values()[i].toString();
        }
        return nations;

    }
}
