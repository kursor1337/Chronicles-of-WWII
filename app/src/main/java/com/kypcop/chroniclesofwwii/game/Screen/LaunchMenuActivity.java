package com.kypcop.chroniclesofwwii.game.Screen;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.Network.ConnectActivity;

import java.util.ArrayList;
import java.util.List;

import static com.kypcop.chroniclesofwwii.game.Engine.MODE;
import static com.kypcop.chroniclesofwwii.game.Engine.MULTI_PLAYER;
import static com.kypcop.chroniclesofwwii.game.Engine.SINGLE_PLAYER;

public class LaunchMenuActivity extends Activity {
    Button start, server, connect, new_scenario; //main menu with some buttons
    private LocationManager locationManager;
    private WifiManager wifiManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        start = findViewById(R.id.start);
        server = findViewById(R.id.server);
        connect = findViewById(R.id.connect);
        new_scenario = findViewById(R.id.new_scenario);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null){
            wifiManager.setWifiEnabled(false);
        }

        //listener on the button to go to the mission menu screen

         start.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(LaunchMenuActivity.this, MissionMenuActivity.class);
                 intent.putExtra(MODE, SINGLE_PLAYER);
                 startActivity(intent);
             }
         });
        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationTurnOn();

                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    wifiManager.setWifiEnabled(true);
                    requestLocationPermission();

                    if(isLocationPermissionGranted() &&  wifiManager.isWifiEnabled()){
                        Intent intent = new Intent(LaunchMenuActivity.this, MissionMenuActivity.class);
                        intent.putExtra(MODE, MULTI_PLAYER);
                        startActivity(intent);
                    } else if(!wifiManager.isWifiEnabled()){
                        Toast.makeText(LaunchMenuActivity.this, "Не удалось включить Wi-Fi :(", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiManager.setWifiEnabled(true);
                requestLocationTurnOn();

                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    requestLocationPermission();

                    if(isLocationPermissionGranted() && wifiManager.isWifiEnabled()){
                        Intent intent = new Intent(LaunchMenuActivity.this, ConnectActivity.class);
                        startActivity(intent);
                    } else if(!wifiManager.isWifiEnabled()){
                        Toast.makeText(LaunchMenuActivity.this, "Не удалось включить Wi-Fi :(", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void requestLocationTurnOn(){
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("У вас не включен GPS. Он необходим для этой программы, так как на данный " +
                "момент Wi-Fi Direct API является частью Location API. Хотите включить GPS?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isLocationPermissionGranted() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission(){
        if(isLocationPermissionGranted()){
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults){
        List<Integer> grantResultsList = new ArrayList<>();
        for(int i: grantResults){
            grantResultsList.add(i);
        }
        if(requestCode == 101){
            if(!grantResultsList.contains(PackageManager.PERMISSION_DENIED)){
                Intent intent = new Intent(LaunchMenuActivity.this, ConnectActivity.class);
                startActivity(intent);
            }
        }
    }
}
