package com.kypcop.chroniclesofwwii.game.Logic.Missions;

import android.content.Context;

import com.kypcop.chroniclesofwwii.game.Logic.Nation;

import java.util.ArrayList;
import java.util.List;

import static com.kypcop.chroniclesofwwii.game.Logic.Nation.BRITAIN;
import static com.kypcop.chroniclesofwwii.game.Logic.Nation.FRANCE;
import static com.kypcop.chroniclesofwwii.game.Logic.Nation.GERMANY;
import static com.kypcop.chroniclesofwwii.game.Logic.Nation.ITALY;
import static com.kypcop.chroniclesofwwii.game.Logic.Nation.JAPAN;
import static com.kypcop.chroniclesofwwii.game.Logic.Nation.USA;
import static com.kypcop.chroniclesofwwii.game.Logic.Nation.USSR;

public class AllMissions {

    public static final List<Mission> missionList = new ArrayList<>();
    private static int size = 0;
    public static final int DEFAULT_MISSION_ID = Integer.MAX_VALUE;
    public static final String DEFAULT_MISSION_NAME = "Default";

    private static final Mission DEFAULT_MISSION = new Mission
            (DEFAULT_MISSION_ID, DEFAULT_MISSION_NAME, Intros.DEFAULT_INTRO,
            BRITAIN, 6, 6, 6,
            BRITAIN, 6, 6, 6);



    public static List<Mission> createMissions(Context context){
        createNewMission(MissionNames.get(size), Intros.get(size),
                BRITAIN, 6, 3, 2,
                GERMANY, 6, 3, 2);
        createNewMission(MissionNames.get(size), Intros.get(size),
                GERMANY, 6, 4, 2,
                FRANCE, 8, 2, 4);
        createNewMission(MissionNames.get(size), Intros.get(size),
                GERMANY, 6, 4, 4,
                USSR, 8, 2, 4);
        createNewMission(MissionNames.get(size), Intros.get(size),
                JAPAN, 10, 0, 0,
                USA, 6, 0, 4);
        createNewMission(MissionNames.get(size), Intros.get(size),
                ITALY, 6, 4, 4,
                BRITAIN, 8, 2, 4);
        createNewMission(MissionNames.get(size), Intros.get(size),
                GERMANY, 6, 4, 4,
                USA, 8, 2, 4);
        return missionList;
    }

    public static void createNewMission(String name, String intro,
                                        Nation enemy1, int infantry1, int armored1, int artillery1,
                                        Nation enemy2, int infantry2, int armored2, int artillery2){
        missionList.add(new Mission(size, name, intro,
                enemy1, infantry1, armored1, artillery1,
                enemy2, infantry2, armored2, artillery2));
        size++;
    }

    public static int quantity() {
        return size;
    }

    public static Mission getDefaultMission(){
        return DEFAULT_MISSION;
    }
}
