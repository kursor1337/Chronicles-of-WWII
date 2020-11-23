package com.kypcop.chroniclesofwwii.game.screen.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.logic.missions.AllMissions;
import com.kypcop.chroniclesofwwii.game.screen.fragments.SimpleDialogFragment;
import com.kypcop.chroniclesofwwii.game.screen.fragments.menuFragments.CreateHostFragment;
import com.kypcop.chroniclesofwwii.game.screen.fragments.menuFragments.JoinGameFragment;
import com.kypcop.chroniclesofwwii.game.screen.fragments.menuFragments.LaunchFragment;
import com.kypcop.chroniclesofwwii.game.screen.fragments.menuFragments.OverviewFragment;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "TAG";

    Button makeHost;
    Button joinGame;
    Button overView;
    Button gameActivity;

    WifiManager wifiManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        AllMissions.initMissions(this);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        LaunchFragment launchFragment = new LaunchFragment();
        fragmentTransaction.addToBackStack("Kypcop_LaunchFragment");
        fragmentTransaction.add(R.id.fragment_container, launchFragment, launchFragment.getClass().getName());
        fragmentTransaction.commit();


    }

    public WifiManager getWifiManager(){
        return wifiManager;
    }

    private void buildMessageQuit(){
        final SimpleDialogFragment simpleDialogFragment = new SimpleDialogFragment.Builder(this)
                .setMessage("Do you really want to quit?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("NO", null)
                .setCancelable(true).setTitle("Quit?").build();
        simpleDialogFragment.show(getSupportFragmentManager(), "buildMessageQuit");
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().findFragmentByTag(CreateHostFragment.class.getName()) != null ||
                getSupportFragmentManager().findFragmentByTag(JoinGameFragment.class.getName()) != null ||
                getSupportFragmentManager().findFragmentByTag(OverviewFragment.class.getName()) != null) super.onBackPressed();
        else buildMessageQuit();
    }

    public void changeFragment(Fragment frag, boolean saveInBackStack, boolean animate) {
        String backStateName = ((Object) frag).getClass().getName();

        try {
            FragmentManager manager = getSupportFragmentManager();
            boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

            if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) {
                //fragment not in back stack, create it.
                FragmentTransaction transaction = manager.beginTransaction();

                if (animate) {
                    Log.d(TAG, "Change Fragment: animate");
                    //transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                }

                transaction.replace(R.id.fragment_container, frag, backStateName);

                if (saveInBackStack) {
                    Log.d(TAG, "Change Fragment: addToBackTack " + backStateName);
                    transaction.addToBackStack(backStateName);
                } else {
                    Log.d(TAG, "Change Fragment: NO addToBackTack");
                }

                transaction.commit();
            } else {
                // custom effect if fragment is already instanciated
            }
        } catch (IllegalStateException exception) {
            Log.w(TAG, "Unable to commit fragment, could be activity as been killed in background. " + exception.toString());
        }
    }
}
