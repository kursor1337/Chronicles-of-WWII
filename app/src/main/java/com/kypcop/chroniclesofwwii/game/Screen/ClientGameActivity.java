package com.kypcop.chroniclesofwwii.game.Screen;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.kypcop.chroniclesofwwii.game.Logic.Missions.Mission;
import com.kypcop.chroniclesofwwii.game.Network.WiFiNetwork;

import java.net.InetAddress;

public class ClientGameActivity extends GameActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initializeMultiplayerFeatures();
        initializeClientSide();
        initializeEngineAndHud();
    }

    private void requestInfoAboutMission(){
        network.sendMissionInfoToClientGameActivity(this);
    }

    public void receiveMissionInfo(Mission mission){
        this.mission = mission;
    }

    private void initializeClientSide(){
        try{
            String info = getIntent().getExtras().getString(WiFiNetwork.HOST_ADDRESS);
            InetAddress inetAddress = WiFiNetwork.GSON.fromJson
                    (info, InetAddress.class);
            network.initializeClient(inetAddress);
        } catch(Exception e){
            Toast.makeText(this, "Extras == null", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        requestInfoAboutMission();
        synchronized (network){
            while(mission == null){
                Log.i("Kypcop1337", "HEROVO");
                requestInfoAboutMission();
                try{
                    network.wait();
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }

        player1 = mission.getPlayer2();
        player2 = mission.getPlayer1();

        Log.i("Kypcop1337", "Vse Norm");
    }
}
