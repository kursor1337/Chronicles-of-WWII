package com.kypcop.chroniclesofwwii.game.screen.fragments.menuFragments;

import android.content.Context;
import android.content.Intent;
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
import com.kypcop.chroniclesofwwii.game.logic.missions.AllMissions;
import com.kypcop.chroniclesofwwii.game.logic.missions.Mission;
import com.kypcop.chroniclesofwwii.game.screen.activities.GameActivity;
import com.kypcop.chroniclesofwwii.game.screen.activities.MenuActivity;

import static com.kypcop.chroniclesofwwii.game.Const.connection.HOST;
import static com.kypcop.chroniclesofwwii.game.Const.game.MISSION;
import static com.kypcop.chroniclesofwwii.game.Const.game.MULTIPLAYER_GAME_MODE;

public class LaunchFragment extends Fragment {

    Button makeHost;
    Button joinGame;
    Button overView;
    Button gameActivity;

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
        gameActivity = view.findViewById(R.id.game_activity);
        overView = view.findViewById(R.id.overview);

        overView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //menuActivity.changeFragment(new OverviewFragment(), true, false);

                menuActivity.changeFragment(new MissionFragment(), true, false);

            }
        });

        makeHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WifiManager wifiManager = menuActivity.getWifiManager();
                if(!wifiManager.isWifiEnabled()){
                    Toast.makeText(getActivity(), "Пожалуйства включите Wi-Fi", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "Пожалуйства включите Wi-Fi", Toast.LENGTH_SHORT).show();
                    return;
                }

                menuActivity.changeFragment(new JoinGameFragment(), true, false);
            }
        });
        gameActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GameActivity.class)
                        .putExtra(MULTIPLAYER_GAME_MODE, HOST)
                        .putExtra(MISSION, Mission.toJson(AllMissions.getDefaultMission()));
                startActivity(intent);
            }
        });
        return view;
    }
}
