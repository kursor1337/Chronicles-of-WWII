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


public class MissionMenuScreen extends Activity implements View.OnClickListener {
    List<Mission> missions;
    LinearLayout layout;
    LinearLayout.LayoutParams params;
    int mode;
    int role;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missions);

        mode = getIntent().getIntExtra(Engine.MODE, Engine.SINGLE_PLAYER);
        if(mode == Engine.MULTI_PLAYER){
            role = getIntent().getIntExtra(Engine.ROLE, Engine.IS_SERVER);
        }

        missions = AllMissions.createMissions(this);
        layout = findViewById(R.id.layout);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //setting buttons on the missions
        for(int i = 0; i < AllMissions.quantity(); i++){
            Button button = new Button(this);
            button.setText(missions.get(i).getMissionName());
            button.setLayoutParams(params);
            button.setId(missions.get(i).getId());
            layout.addView(button);
            button.setOnClickListener(this);
        }

        //setting click listeners
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent(MissionMenuScreen.this, GameScreen.class);
        intent.putExtra("mission", id);
        intent.putExtra(Engine.MODE, mode);
        if(mode == Engine.MULTI_PLAYER){
            intent.putExtra(Engine.ROLE, role);
        }
        startActivity(intent);
    }
}
