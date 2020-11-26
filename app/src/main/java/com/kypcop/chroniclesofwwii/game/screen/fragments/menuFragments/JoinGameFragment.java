package com.kypcop.chroniclesofwwii.game.screen.fragments.menuFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.Const;
import com.kypcop.chroniclesofwwii.game.connection.Client;
import com.kypcop.chroniclesofwwii.game.connection.Connection;
import com.kypcop.chroniclesofwwii.game.connection.Host;
import com.kypcop.chroniclesofwwii.game.connection.HostsAdapter;
import com.kypcop.chroniclesofwwii.game.connection.NsdHelper;
import com.kypcop.chroniclesofwwii.game.logic.missions.Mission;
import com.kypcop.chroniclesofwwii.game.screen.activities.GameActivity;
import com.kypcop.chroniclesofwwii.game.screen.activities.MenuActivity;
import com.kypcop.chroniclesofwwii.game.screen.fragments.SimpleDialogFragment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static com.kypcop.chroniclesofwwii.game.Const.connection.ACCEPTED;
import static com.kypcop.chroniclesofwwii.game.Const.connection.CANCEL_CONNECTION;
import static com.kypcop.chroniclesofwwii.game.Const.connection.CLIENT;
import static com.kypcop.chroniclesofwwii.game.Const.connection.CONNECTED_DEVICE;
import static com.kypcop.chroniclesofwwii.game.Const.connection.INVALID_JSON;
import static com.kypcop.chroniclesofwwii.game.Const.connection.REJECTED;
import static com.kypcop.chroniclesofwwii.game.Const.connection.REQUEST_FOR_ACCEPT;
import static com.kypcop.chroniclesofwwii.game.Const.connection.REQUEST_MISSION_INFO;
import static com.kypcop.chroniclesofwwii.game.Const.game.MISSION;
import static com.kypcop.chroniclesofwwii.game.Const.game.MULTIPLAYER_GAME_MODE;

public class JoinGameFragment extends Fragment {

    NsdHelper nsdHelper;
    Connection connection;
    boolean isAccepted = false;
    MenuActivity menuActivity;
    Client client;
    ListView hostListView;
    Map<String, Host> hostMap = new TreeMap<>();
    HostsAdapter hostAdapter;
    Host host;
    TextView statusTextView;
    EditText clientNameEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect, new LinearLayout(menuActivity), false);

        clientNameEditText = view.findViewById(R.id.client_name_edittext);
        nsdHelper = new NsdHelper(menuActivity, nsdListener);
        nsdHelper.startDiscovery();
        hostListView = view.findViewById(R.id.list_view);
        statusTextView = view.findViewById(R.id.status_text_view);
        statusTextView.setText(R.string.finding);

        hostAdapter = new HostsAdapter(menuActivity, new ArrayList<Host>());
        hostListView.setAdapter(hostAdapter);
        hostListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String string = clientNameEditText.getText().toString();
                if(string.length() > 0){
                    client = new Client(string, menuActivity, sendListener, receiveListener, clientListener);
                    host = hostAdapter.getItem(position);
                    client.connectTo(host);
                } else{
                    Toast.makeText(menuActivity, R.string.fill_the_gaps, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        menuActivity = (MenuActivity) context;
    }

    public void disposeNetworkStuff() {
        nsdHelper.shutdown();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposeNetworkStuff();
    }

    private final NsdHelper.DiscoveryListener nsdListener = new NsdHelper.DiscoveryListener() {
        @Override
        public void onResolveFailed(int errorCode) {
            Toast.makeText(menuActivity, R.string.resolve_failed, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onDiscoveryFailed(int errorCode) {
            Toast.makeText(menuActivity, R.string.discovery_failed, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onHostFound(@NotNull Host host) {
            statusTextView.setText(R.string.found);
            hostMap.put(host.getName(), host);
            hostAdapter.clear();
            hostAdapter.addAll(hostMap.values());
            hostAdapter.notifyDataSetChanged();
        }
        @Override
        public void onHostLost(@NotNull String name) {
            hostMap.remove(name);
            hostAdapter.clear();
            hostAdapter.addAll(hostMap.values());
            hostAdapter.notifyDataSetChanged();
            hostAdapter.notifyDataSetChanged();
            if(hostAdapter.getCount() == 0){
                statusTextView.setText(R.string.not_found);
            }
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
            switch (string){
                case ACCEPTED:
                    Log.i("Client", ACCEPTED);
                    isAccepted = true;
                    connection.send(REQUEST_MISSION_INFO);
                    Log.i("Client", REQUEST_MISSION_INFO);
                    buildMessageWaitingForAccepted();
                    break;
                case REJECTED:
                    Toast.makeText(menuActivity, R.string.connection_refused, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Log.i("Client", "Default branch");
                    if(isAccepted) {
                        if (Mission.fromJson(string) == null) {
                            Log.i("Client", "Invalid Json");
                            connection.send(INVALID_JSON);
                            return;
                        }
                        Intent intent = new Intent(menuActivity, GameActivity.class);
                        intent.putExtra(CONNECTED_DEVICE, host)
                                .putExtra(MULTIPLAYER_GAME_MODE, CLIENT)
                                .putExtra(MISSION, string);
                        Const.connection.setCurrentConnection(connection);
                        nsdHelper.stopDiscovery();
                        startActivity(intent);
                    }
            }
        }
    };

    private final Client.Listener clientListener = new Client.Listener() {
        @Override
        public void onException(@NotNull Exception e) {
            Toast.makeText(menuActivity, R.string.error_connectiong, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectionEstablished(@NotNull Connection c) {
            connection = c;
            connection.send(REQUEST_FOR_ACCEPT);
            Log.i("Client", REQUEST_FOR_ACCEPT);
        }
    };

    void buildMessageWaitingForAccepted(){
        SimpleDialogFragment dialog = new SimpleDialogFragment.Builder(menuActivity)
                .setMessage(R.string.waiting_for_accepted)
                .setNegativeButton(R.string.cancel_request_for_accepted, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        connection.send(CANCEL_CONNECTION);
                        dialog.dismiss();
                    }
                }).build();
        dialog.show(getParentFragmentManager(), "WaitingForAccepted");
    }
}
