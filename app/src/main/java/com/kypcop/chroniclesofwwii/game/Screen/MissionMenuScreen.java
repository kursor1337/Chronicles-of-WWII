package com.kypcop.chroniclesofwwii.game.Screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.Logic.Missions.AllMissions;
import com.kypcop.chroniclesofwwii.game.Logic.Missions.Mission;
import com.kypcop.chroniclesofwwii.game.Network.WiFiNetwork;

import java.util.List;


public class MissionMenuScreen extends Activity implements View.OnClickListener {
    List<Mission> missions;
    LinearLayout layout;
    LinearLayout.LayoutParams params;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missions);
        missions = AllMissions.createMissions(this);
        layout = findViewById(R.id.layout);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //setting buttons on the missions
        for(int i = 0; i < AllMissions.quantity(); i++){
            missions.get(i).createButton(this);
            Button button = missions.get(i).getButton();
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
        intent.putExtra(WiFiNetwork.SERVER, true);
        startActivity(intent);
    }
}
