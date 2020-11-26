package com.kypcop.chroniclesofwwii.game.screen.fragments.menuFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.Const;
import com.kypcop.chroniclesofwwii.game.connection.Connection;
import com.kypcop.chroniclesofwwii.game.connection.Host;
import com.kypcop.chroniclesofwwii.game.connection.NsdHelper;
import com.kypcop.chroniclesofwwii.game.connection.Server;
import com.kypcop.chroniclesofwwii.game.logic.missions.Mission;
import com.kypcop.chroniclesofwwii.game.screen.activities.GameActivity;
import com.kypcop.chroniclesofwwii.game.screen.activities.MenuActivity;
import com.kypcop.chroniclesofwwii.game.screen.fragments.SimpleDialogFragment;

import org.jetbrains.annotations.NotNull;

import static com.kypcop.chroniclesofwwii.game.Const.connection.ACCEPTED;
import static com.kypcop.chroniclesofwwii.game.Const.connection.CANCEL_CONNECTION;
import static com.kypcop.chroniclesofwwii.game.Const.connection.CONNECTED_DEVICE;
import static com.kypcop.chroniclesofwwii.game.Const.connection.HOST;
import static com.kypcop.chroniclesofwwii.game.Const.connection.INVALID_JSON;
import static com.kypcop.chroniclesofwwii.game.Const.connection.REJECTED;
import static com.kypcop.chroniclesofwwii.game.Const.connection.REQUEST_FOR_ACCEPT;
import static com.kypcop.chroniclesofwwii.game.Const.connection.REQUEST_MISSION_INFO;
import static com.kypcop.chroniclesofwwii.game.Const.game.MISSION;
import static com.kypcop.chroniclesofwwii.game.Const.game.MULTIPLAYER_GAME_MODE;


public class CreateHostFragment extends Fragment {

    public static final String MISSION_INFO = "MISSION_INFO";

    EditText hostName;
    Button chooseMissionButton;
    Button ready;
    TextView statusChosenMissionName;
    DialogFragment dialog;
    String chosenMissionJson;
    MenuActivity menuActivity;
    Server server;
    Connection connection;
    NsdHelper nsdHelper;

    private boolean isHostReady = false;

    private final NsdHelper.BroadcastListener broadcastListener = new NsdHelper.BroadcastListener() {
        @Override
        public void onServiceRegistered(@NotNull NsdServiceInfo serviceInfo) {
            dialog = buildMessageWaitingForConnections();
        }
        @Override
        public void onRegistrationFailed(@NotNull NsdServiceInfo arg0, int arg1) {
            Toast.makeText(menuActivity, R.string.registration_failed, Toast.LENGTH_SHORT).show();
        }
    };

    private final Connection.SendListener sendListener = new Connection.SendListener() {
        @Override
        public void onSendSuccess() { }
        @Override
        public void onSendFailure(@NotNull Exception e) { }
    };

    private final Connection.ReceiveListener receiveListener = new Connection.ReceiveListener() {
        @Override
        public void onReceive(@NotNull String string) {
            switch(string){
                case REQUEST_FOR_ACCEPT:
                    if(isHostReady){
                        dialog.dismiss();
                        buildMessageConnectionRequest(connection.getHost());
                    }
                    break;
                case REQUEST_MISSION_INFO:
                    Log.i("Server", "Client sent " + REQUEST_MISSION_INFO);
                    connection.send(chosenMissionJson);
                    Intent intent = new Intent(menuActivity, GameActivity.class);
                    intent.putExtra(CONNECTED_DEVICE, connection.getHost())
                            .putExtra(MULTIPLAYER_GAME_MODE, HOST)
                            .putExtra(MISSION, chosenMissionJson);
                    Const.connection.setCurrentConnection(connection);
                    nsdHelper.shutdown();
                    startActivity(intent);
                    break;
                case CANCEL_CONNECTION:
                    Log.i("Server", CANCEL_CONNECTION);
                    connection.dispose();
                case INVALID_JSON:
                    Log.i("Server", "Sent invalid json");

            }
        }
    };

    private final Server.Listener serverListener = new Server.Listener() {
        @Override
        public void onListeningStartError(@NotNull Exception e) {
            Toast.makeText(menuActivity, R.string.error_start_listening, Toast.LENGTH_SHORT).show();
            ready.setEnabled(true);
        }
        @Override
        public void onConnected(@NotNull Connection conn) {
            connection = conn;
        }
        @Override
        public void onServerInfoObtained(@NotNull String hostName, int port) {
            nsdHelper.registerService(hostName, port);
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        menuActivity = (MenuActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener(MISSION_INFO, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                chosenMissionJson = bundle.getString(MISSION);
                Mission mission = Mission.fromJson(chosenMissionJson);
                if(mission != null && mission.getMissionName() != null){
                    statusChosenMissionName.setText(mission.getMissionName());
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_host, new LinearLayout(menuActivity), false);

        nsdHelper = new NsdHelper(menuActivity, broadcastListener);
        hostName = view.findViewById(R.id.host_name_edittext);
        chooseMissionButton = view.findViewById(R.id.choose_mission_button);
        ready = view.findViewById(R.id.ready);
        statusChosenMissionName = view.findViewById(R.id.chosen_mission);

        chooseMissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuActivity.changeFragment(new MissionFragment(), true, false);
            }
        });

        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chosenMissionJson == null || chosenMissionJson.equals("")
                || !(hostName.getText().length() > 0)) return;
                server = new Server(hostName.getText().toString(), menuActivity, sendListener, receiveListener, serverListener);
                server.startListening();
                v.setEnabled(false);
                isHostReady = true;
            }
        });
        return view;
    }

    private SimpleDialogFragment buildMessageWaitingForConnections(){
        final SimpleDialogFragment dialog = new SimpleDialogFragment.Builder(menuActivity).setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nsdHelper.unregisterService();
                        server.stopListening();
                        ready.setEnabled(true);
                    }
                }).setMessage("Waiting for Connections...")
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        nsdHelper.unregisterService();
                        server.stopListening();
                        ready.setEnabled(true);
                    }
                })
                .build();
        dialog.show(getParentFragmentManager(), "Waiting for connections");
        return dialog;
    }

    private void buildMessageConnectionRequest(final Host host){
        SimpleDialogFragment dialog = new SimpleDialogFragment.Builder(menuActivity)
                .setMessage(host.getName() + " wants to connect to this  device. Do you agree?")
                .setCancelable(false)
                .setNegativeButton("Refuse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        connection.send(REJECTED);
                    }})
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        connection.send(ACCEPTED);
                    }})
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        connection.send(REJECTED);
                    }
                })
                .build();
        dialog.show(getParentFragmentManager(), "Waiting for Connected");
        Toast.makeText(menuActivity, R.string.waiting_for_connected, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(server != null){
            server.stopListening();
        }
        nsdHelper.shutdown();
    }
}

