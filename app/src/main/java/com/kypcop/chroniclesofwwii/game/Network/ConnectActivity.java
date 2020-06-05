package com.kypcop.chroniclesofwwii.game.Network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.Engine;
import com.kypcop.chroniclesofwwii.game.Screen.GameScreen;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static android.net.wifi.p2p.WifiP2pManager.ActionListener;
import static android.net.wifi.p2p.WifiP2pManager.Channel;
import static android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION;

public class ConnectActivity extends Activity {
    private final IntentFilter intentFilter = new IntentFilter();
    WifiManager wifiManager;
    public WifiP2pManager p2pManager;
    Channel channel;
    WifiDirectBroadcastReceiver receiver;
    List<WifiP2pDevice> peers = new ArrayList<>();
    String[] deviceNames;
    WifiP2pDevice[] devices;
    ListView listView;
    boolean isWifiP2pEnabled;
    TextView status;
    WiFiNetwork network = Engine.NETWORK;

    final WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if (!peerList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peerList.getDeviceList());
                deviceNames = new String[peerList.getDeviceList().size()];
                devices = new WifiP2pDevice[peerList.getDeviceList().size()];

                int index = 0;
                for (WifiP2pDevice device : peerList.getDeviceList()) {
                    deviceNames[index] = device.deviceName;
                    devices[index] = device;
                    index++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ConnectActivity.this, android.R.layout.simple_list_item_1, deviceNames);
                listView.setAdapter(adapter);
                status.setText("Devices have been found (^_^)");
            }
            if (peerList.getDeviceList().size() == 0) {
                status.setText("No devices found");
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        listView = findViewById(R.id.listView);
        status = findViewById(R.id.status);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        p2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        if (p2pManager != null) {
            channel = p2pManager.initialize(this, getMainLooper(), null);
        }
        else{
            Toast.makeText(this, "Ваш телефон не поддерживает Wi-Fi Direct :(", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        intentFilter.addAction(WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        p2pManager.discoverPeers(channel, new ActionListener() {
            @Override
            public void onSuccess() {
                status.setText("Discovery started");
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(ConnectActivity.this, "Ошибочка вышла :(", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final WifiP2pDevice device = peers.get(position);
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                p2pManager.connect(channel, config, new ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ConnectActivity.this,
                                "Подключено к " + device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(ConnectActivity.this,
                                "Не удалось подключиться к " + device.deviceName, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    ConnectionInfoListener connectionInfoListener = new ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress hostAddress = info.groupOwnerAddress;
            Toast.makeText(ConnectActivity.this, "onConnectionInfoAvailable", Toast.LENGTH_SHORT).show();
            if(info.groupFormed){
                Gson gson = new Gson();
                String inetAddress = gson.toJson(hostAddress);
                Intent intent = new Intent(ConnectActivity.this, GameScreen.class);
                intent.putExtra(WiFiNetwork.SERVER, false).putExtra(WiFiNetwork.HOST_ADDRESS, inetAddress);
                startActivity(intent);
                Toast.makeText(ConnectActivity.this, "Successful", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        receiver = new WifiDirectBroadcastReceiver(p2pManager, channel, this);
        registerReceiver(receiver, intentFilter);


    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);

    }

    public void setIsWifiP2pEnabled(boolean b) {
        isWifiP2pEnabled = b;
    }

}
