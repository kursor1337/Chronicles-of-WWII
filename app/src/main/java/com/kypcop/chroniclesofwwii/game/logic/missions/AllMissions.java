package com.kypcop.chroniclesofwwii.game.logic.missions;

import android.content.Context;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.logic.Nation;

import java.util.ArrayList;
import java.util.List;

import static com.kypcop.chroniclesofwwii.game.logic.Nation.BRITAIN;
import static com.kypcop.chroniclesofwwii.game.logic.Nation.FRANCE;
import static com.kypcop.chroniclesofwwii.game.logic.Nation.GERMANY;
import static com.kypcop.chroniclesofwwii.game.logic.Nation.ITALY;
import static com.kypcop.chroniclesofwwii.game.logic.Nation.JAPAN;
import static com.kypcop.chroniclesofwwii.game.logic.Nation.USA;
import static com.kypcop.chroniclesofwwii.game.logic.Nation.USSR;

public class AllMissions {

    private static final List<Mission> missionList = new ArrayList<>();
    private static int counter = 0;
    private static final String DEFAULT_MISSION_NAME = "Default";
    private static String[] mission_names;
    private static String[] mission_intros;
    private static boolean missions_created;

    private static final Mission DEFAULT_MISSION = new Mission
            (0, DEFAULT_MISSION_NAME, "",
            BRITAIN, 1, 1, 1,
            BRITAIN, 1, 1, 1);

    public static void initMissions(Context context){
        if(missions_created) return;
        mission_names = context.getResources().getStringArray(R.array.missions);
        mission_intros = context.getResources().getStringArray(R.array.intros);
        createNewMission(BRITAIN, 6, 3, 2,
                GERMANY, 6, 3, 2);
        createNewMission(GERMANY, 6, 4, 2,
                FRANCE, 8, 2, 4);
        createNewMission(GERMANY, 6, 4, 4,
                USSR, 8, 2, 4);
        createNewMission(JAPAN, 10, 0, 0,
                USA, 6, 0, 4);
        createNewMission(ITALY, 6, 4, 4,
                BRITAIN, 8, 2, 4);
        createNewMission(GERMANY, 6, 4, 4,
                USA, 8, 2, 4);
        missions_created = true;
    }

    private static void createNewMission(
            Nation enemy1, int infantry1, int armored1, int artillery1,
            Nation enemy2, int infantry2, int armored2, int artillery2){
        Mission mission = new Mission(counter, mission_names[counter], mission_intros[counter],
                enemy1, infantry1, armored1, artillery1,
                enemy2, infantry2, armored2, artillery2);
        missionList.add(mission);
        counter++;
    }

    public static int quantity() {
        return counter;
    }

    public static Mission getDefaultMission(){
        try {
            return DEFAULT_MISSION.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return DEFAULT_MISSION;
        }
    }

    public static List<Mission> getMissionList() {
        return new ArrayList<>(missionList);
    }
}
