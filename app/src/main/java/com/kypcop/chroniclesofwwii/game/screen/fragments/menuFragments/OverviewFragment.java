package com.kypcop.chroniclesofwwii.game.screen.fragments.menuFragments;

import android.content.Context;
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
import com.kypcop.chroniclesofwwii.game.screen.activities.MenuActivity;

public class OverviewFragment extends Fragment {

    public static final String OVERVIEW_FRAGMENT = "OVERVIEW_FRAGMENT";

    TextView textView;
    private MenuActivity menuActivity;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        menuActivity = (MenuActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, new LinearLayout(getActivity()), false);
        textView = view.findViewById(R.id.instruction);
        return view;
    }
}
