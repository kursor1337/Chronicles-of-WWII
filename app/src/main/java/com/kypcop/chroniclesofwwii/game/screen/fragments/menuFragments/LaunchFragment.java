package com.kypcop.chroniclesofwwii.game.screen.fragments.menuFragments;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.screen.activities.MenuActivity;

public class LaunchFragment extends Fragment {

    Button makeHost;
    Button joinGame;

    MenuActivity menuActivity;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        menuActivity = ((MenuActivity) context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_launch, new LinearLayout(getActivity()), false);

        makeHost = view.findViewById(R.id.server);
        joinGame = view.findViewById(R.id.connect);
        makeHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WifiManager wifiManager = menuActivity.getWifiManager();
                if(!wifiManager.isWifiEnabled()){
                    Toast.makeText(getActivity(), R.string.turn_wifi, Toast.LENGTH_SHORT).show();
                    return;
                }
                menuActivity.changeFragment(new CreateHostFragment(), true, false);
            }
        });
        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WifiManager wifiManager = menuActivity.getWifiManager();
                if(!wifiManager.isWifiEnabled()){
                    Toast.makeText(getActivity(), R.string.turn_wifi, Toast.LENGTH_SHORT).show();
                    return;
                }

                menuActivity.changeFragment(new JoinGameFragment(), true, false);
            }
        });
        return view;
    }
}
