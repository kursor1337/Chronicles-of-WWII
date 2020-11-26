package com.kypcop.chroniclesofwwii.game.screen.fragments.menuFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.logic.missions.AllMissions;
import com.kypcop.chroniclesofwwii.game.logic.missions.Mission;
import com.kypcop.chroniclesofwwii.game.screen.activities.MenuActivity;
import com.kypcop.chroniclesofwwii.game.screen.fragments.CreateNewScenarioFragment;

import java.util.List;

import static com.kypcop.chroniclesofwwii.game.Const.game.MISSION;

public class MissionFragment extends Fragment implements CreateNewScenarioFragment.OnScenarioListener{

    public static final String MISSION_FRAGMENT = "MISSION_FRAGMENT";

    public static final String CUSTOM_MISSION_INFO = "customMissionInfo";

    String[] missionNames;
    List<Mission> missionList;
    ArrayAdapter<String> missionAdapter;
    ListView missionListView;
    Button button_defaultMission;
    Button button_customMission;
    private MenuActivity menuActivity;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        menuActivity = (MenuActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missions, new LinearLayout(getActivity()), false);

        button_defaultMission = view.findViewById(R.id.default_mission);
        button_customMission = view.findViewById(R.id.custom_mission);
        missionListView = view.findViewById(R.id.mission_list);
        missionList = AllMissions.getMissionList();
        missionNames = new String[missionList.size()];

        for(int i = 0; i < missionList.size(); i++) {
            missionNames[i] = missionList.get(i).getMissionName();
        }

        missionAdapter = new ArrayAdapter<>(getActivity(), R.layout.listview_missions, missionNames);
        missionListView.setAdapter(missionAdapter);


        missionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle result = new Bundle();
                result.putString(MISSION, Mission.toJson(AllMissions.getMissionList().get(position)));
                getParentFragmentManager().setFragmentResult(CreateHostFragment.MISSION_INFO, result);

                getParentFragmentManager().popBackStackImmediate();

            }
        });

        button_defaultMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle result = new Bundle();
                result.putString(MISSION, Mission.toJson(AllMissions.getDefaultMission()));
                getParentFragmentManager().setFragmentResult(CreateHostFragment.MISSION_INFO, result);
                getParentFragmentManager().popBackStackImmediate();
            }
        });

        button_customMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewScenarioFragment fragment = new CreateNewScenarioFragment();
                fragment.show(getParentFragmentManager(), "CreateNewScenarioFragment");
            }
        });

        return view;
    }

    @Override
    public void sendScenario(Mission mission) {

    }
}
