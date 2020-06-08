package com.kypcop.chroniclesofwwii.game.Screen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.Engine;
import com.kypcop.chroniclesofwwii.game.Logic.Missions.AllMissions;
import com.kypcop.chroniclesofwwii.game.Logic.Missions.Mission;
import com.kypcop.chroniclesofwwii.game.Logic.Player.Player;
import com.kypcop.chroniclesofwwii.game.Network.WiFiNetwork;

public class GameActivity extends Activity {

    //That's for multiplayer
    protected WifiManager wifiManager = null;
    protected WifiP2pManager p2pManager = null;
    protected final WiFiNetwork network = Engine.NETWORK;


    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
            (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    LinearLayout layout1;
    LinearLayout layout2;
    HUD hud;
    Mission mission;
    Player player1;
    Player player2;
    Engine engine;
    TableLayout tableLayout;


    public void goToMainScreen(){
        Intent intent = new Intent(this, LaunchMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        System.gc();
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);
        tableLayout = findViewById(R.id.table);
        layout1 = findViewById(R.id.hud1);
        layout2 = findViewById(R.id.hud2);


    }

    protected void initializeMultiplayerFeatures(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        p2pManager = (WifiP2pManager) getApplicationContext().getSystemService(WIFI_P2P_SERVICE);
    }

    protected void initializeEngineAndHud(){
        hud = new HUD(this, mission, layout1, layout2);
        hud.setButtonParams(params);
        hud.setTextParams(params);
        hud.initializeHud();
        engine = new Engine(this, hud);
        engine.initializeRows(tableLayout);
    }

    protected void prepareMission(){
        int missionId = getIntent().getIntExtra(Engine.MISSION_ID, AllMissions.DEFAULT_MISSION_ID);
        try{
            mission = AllMissions.missionList.get(missionId);
        } catch(Exception e){
            mission = AllMissions.getDefaultMission();
        }
    }

    public void buildAlertMessageEndOfTheGame(boolean win) {
        String result;
        if(win){
            result = "Вы победили!\nЮХУУУУУ!!!";
        } else{
            result = "Вы проиграли!\nАХАХАХ, ЛОХ!!!";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(result).setCancelable(false).
                setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToMainScreen();
                    }
                });
        builder.create().show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    }

}

