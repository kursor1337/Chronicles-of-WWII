package com.kypcop.chroniclesofwwii.game.Screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.Engine;
import com.kypcop.chroniclesofwwii.game.Logic.Missions.AllMissions;
import com.kypcop.chroniclesofwwii.game.Logic.Missions.Mission;

import java.util.List;


public class MissionMenuActivity extends Activity implements View.OnClickListener {
    List<Mission> missions;
    LinearLayout layout;
    LinearLayout.LayoutParams params;
    int mode;
    int role;
    private static boolean missionsShown = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missions);

        mode = getIntent().getIntExtra(Engine.MODE, Engine.SINGLE_PLAYER);

        missions = AllMissions.createMissions(this);
        layout = findViewById(R.id.layout);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //setting buttons on the missions
        if(!missionsShown) {
            for (int i = 0; i < AllMissions.quantity(); i++) {
                Button button = new Button(this);
                button.setText(missions.get(i).getMissionName());
                button.setLayoutParams(params);
                button.setId(missions.get(i).getId());
                layout.addView(button);
                button.setOnClickListener(this);
            }
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        if(mode == Engine.MULTI_PLAYER){
            intent = new Intent(MissionMenuActivity.this, ServerGameActivity.class);
        } else{
            intent = new Intent(MissionMenuActivity.this, SingleGameActivity.class);
        }
        intent.putExtra(Engine.MISSION_ID, id);
        startActivity(intent);
    }
}
