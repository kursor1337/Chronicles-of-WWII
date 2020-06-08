package com.kypcop.chroniclesofwwii.game.Screen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class ServerGameActivity extends GameActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeMultiplayerFeatures();
        initializeServerSide();
        initializeEngineAndHud();
    }



    private void initializeServerSide(){
        WifiP2pManager p2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        if(p2pManager != null){
            WifiP2pManager.Channel channel = p2pManager.initialize(this, getMainLooper(), null);
            p2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ServerGameActivity.this, "Discover success", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(int reason) {
                    Toast.makeText(ServerGameActivity.this, "Discover failure", Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            Toast.makeText(this, "Упс, видимо ваш телефон не поддерживает Wi-Fi Direct :(", Toast.LENGTH_SHORT).show();
            goToMainScreen();
        }

        prepareMission();
        network.setServerMission(mission);
        network.initializeServer(buildMessageWaitingForConnected());
        Toast.makeText(this, "Server Initialized", Toast.LENGTH_SHORT).show();
        player1 = mission.getPlayer1();
        player2 = mission.getPlayer2();
    }

    private Handler buildMessageWaitingForConnected() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Ждем подключения...").setCancelable(false).
                setNeutralButton("Выйти", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
        Toast.makeText(this, "Waiting for connected", Toast.LENGTH_SHORT).show();
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if(msg.what == 1){
                    dialog.cancel();
                }
                return false;
            }
        });
        return handler;
    }


}
