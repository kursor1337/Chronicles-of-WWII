package com.kypcop.chroniclesofwwii.game.Screen;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.Logic.Missions.AllMissions;
import com.kypcop.chroniclesofwwii.game.Logic.Nation;

public class CreateScenarioActivity extends Activity {

    Nation[] nations = Nation.values();
    Spinner nation_spinner1, nation_spinner2;
    String newMissionName = "Default";
    String newMissionIntro = "";
    Nation nation1, nation2;
    int player1infantry, player1armored, player1artillery;
    int player2infantry, player2armored, player2artillery;
    EditText name, intro;
    EditText infantry1, armored1, artillery1;
    EditText infantry2, armored2, artillery2;
    Button ready;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        ArrayAdapter<Nation> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nation_spinner1.setAdapter(adapter);
        nation_spinner2.setAdapter(adapter);

        findViews();

        nation_spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nation1 = Nation.values()[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                nation1 = null;
            }
        });
        nation_spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nation2 = Nation.values()[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                nation2 = null;
            }
        });

        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nation1 != nation2){
                    if(!name.getText().toString().equals("")){
                        newMissionName = name.getText().toString();
                    }
                    newMissionIntro = intro.getText().toString();
                    player1infantry = Integer.parseInt(infantry1.getText().toString());
                    player1armored = Integer.parseInt(armored1.getText().toString());
                    player1artillery = Integer.parseInt(artillery1.getText().toString());
                    player2infantry = Integer.parseInt(infantry2.getText().toString());
                    player2armored = Integer.parseInt(armored2.getText().toString());
                    player2artillery = Integer.parseInt(artillery2.getText().toString());
                    AllMissions.createNewMission(newMissionName, newMissionIntro,
                            nation1, player1infantry, player1armored, player1artillery,
                            nation2, player2infantry, player2armored, player2artillery);

                    finish();
                }
                if(nation1 == nation2){
                    Toast.makeText(CreateScenarioActivity.this,
                            "Страна не может воевать против самой себя!", Toast.LENGTH_LONG).show();
                }
                if(player1infantry < 1 && player1armored < 1 && player1artillery < 1 &&
                player2infantry < 1 && player2armored < 1 && player2artillery < 1){
                    Toast.makeText(CreateScenarioActivity.this,
                            "Необходимо минимум по одной дивизии кождой стороне", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void findViews(){
        name = findViewById(R.id.name);
        intro = findViewById(R.id.intro);
        nation_spinner1 = findViewById(R.id.nation1);
        nation_spinner2 = findViewById(R.id.nation2);
        infantry1 = findViewById(R.id.infantry1);
        infantry2 = findViewById(R.id.infantry2);
        armored1 = findViewById(R.id.armored1);
        armored2 = findViewById(R.id.armored2);
        artillery1 = findViewById(R.id.artillery1);
        artillery2 = findViewById(R.id.artillery2);
        ready = findViewById(R.id.ready);
    }
}
