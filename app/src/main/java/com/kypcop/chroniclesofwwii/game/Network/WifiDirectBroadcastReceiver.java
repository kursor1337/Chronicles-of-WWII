package com.kypcop.chroniclesofwwii.game.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.net.wifi.p2p.WifiP2pManager.Channel;
import static android.net.wifi.p2p.WifiP2pManager.EXTRA_NETWORK_INFO;
import static android.net.wifi.p2p.WifiP2pManager.EXTRA_WIFI_STATE;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_ENABLED;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    WifiP2pManager manager;
    Channel channel;
    ConnectActivity activity;
    List<WifiP2pDevice> peers = new ArrayList<>();

    public WifiDirectBroadcastReceiver
            (WifiP2pManager manager, Channel channel, ConnectActivity activity) {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(EXTRA_WIFI_STATE, -1);
            if (state == WIFI_P2P_STATE_ENABLED) {
                activity.setIsWifiP2pEnabled(true);
            } else {
                activity.setIsWifiP2pEnabled(false);
            }
        } else if (WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if(manager != null){
                manager.requestPeers(channel, activity.peerListListener);
            }

        } else if (WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if(manager == null){
                return;
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(EXTRA_NETWORK_INFO);
            if(networkInfo !=null && networkInfo.isConnected()){
                manager.requestConnectionInfo(channel, activity.connectionInfoListener);
            } else{
                Toast.makeText(activity, "Not connected", Toast.LENGTH_SHORT).show();
            }

        } else if (WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }
    }
}
