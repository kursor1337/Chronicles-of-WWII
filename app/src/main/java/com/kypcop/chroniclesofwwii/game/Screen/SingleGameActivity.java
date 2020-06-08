package com.kypcop.chroniclesofwwii.game.Screen;

import android.os.Bundle;

import com.kypcop.chroniclesofwwii.game.Logic.Player.AI;

public class SingleGameActivity extends GameActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeSinglePlayerGame();
        initializeEngineAndHud();
    }


    private void initializeSinglePlayerGame(){
        prepareMission();
        player2 = mission.getPlayer1();
        player1 = new AI(mission.getPlayer1());
    }

}
