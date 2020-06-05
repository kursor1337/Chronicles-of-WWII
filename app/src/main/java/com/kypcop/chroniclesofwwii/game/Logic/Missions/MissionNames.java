package com.kypcop.chroniclesofwwii.game.Logic.Missions;

import java.util.TreeMap;

public class MissionNames {

    public static TreeMap<Integer, String> NAMES = new TreeMap<>();

    static {
        NAMES.put(NAMES.size(), "Морской лев");
        NAMES.put(NAMES.size(), "Арденнская операция 1940");
        NAMES.put(NAMES.size(), "План Барбаросса");
        NAMES.put(NAMES.size(), "Битва за Мидвей");
        NAMES.put(NAMES.size(), "Высадка в Сицилии");
        NAMES.put(NAMES.size(), "Высадка в Нормандии");
    }

    public static void addMissionName(String name){
        NAMES.put(NAMES.size(), name);
    }

    static String get(int id){
        return NAMES.get(id);
    }
}
