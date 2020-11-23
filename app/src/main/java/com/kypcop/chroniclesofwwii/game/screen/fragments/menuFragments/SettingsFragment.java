package com.kypcop.chroniclesofwwii.game.screen.fragments.menuFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.Const;

public class SettingsFragment extends Fragment {

    TextView ipTextView;
    TextView portTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, new LinearLayout(getActivity()));
        ipTextView = v.findViewById(R.id.ip_address);
        portTextView = v.findViewById(R.id.port);

        ipTextView.setText(Const.connection.getSelfIpAddress());
        //portTextView.setText(Const.connection.localPort + "");

        return v;
    }
}
