package com.kypcop.chroniclesofwwii.game.screen.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.logic.Nation;
import com.kypcop.chroniclesofwwii.game.logic.missions.Mission;
import com.kypcop.chroniclesofwwii.game.screen.fragments.menuFragments.MissionFragment;

import static com.kypcop.chroniclesofwwii.game.Const.game.MISSION;


public class CreateNewScenarioFragment extends DialogFragment {

    Nation[] nations = Nation.values();
    private String newMissionName = "Default";
    private String newMissionIntro = "";
    private Nation nation1, nation2;
    private OnScenarioListener listener;
    private int player1infantry, player1armored, player1artillery;
    private int player2infantry, player2armored, player2artillery;

    //widgets
    private Spinner nation_spinner1, nation_spinner2;
    private EditText name, intro;
    private EditText infantry1, armored1, artillery1;
    private EditText infantry2, armored2, artillery2;
    private Button ready;



    public interface OnScenarioListener{
        void sendScenario(Mission mission);
    }




    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_mission, new LinearLayout(getActivity()), false);

        nation_spinner1 = view.findViewById(R.id.nation1);
        nation_spinner2 = view.findViewById(R.id.nation2);

        ArrayAdapter<Nation> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, nations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nation_spinner1.setAdapter(adapter);
        nation_spinner2.setAdapter(adapter);

        findViews(view);

        nation_spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nation1 = Nation.values()[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                nation1 = null;
            }
        });
        nation_spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nation2 = Nation.values()[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                nation2 = null;
            }
        });

        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nation1 != nation2){
                    if(!name.getText().toString().equals("")){
                        newMissionName = name.getText().toString();
                    }

                    newMissionIntro = intro.getText().toString();
                    player1infantry = Integer.parseInt(infantry1.getText().toString());
                    player1armored = Integer.parseInt(armored1.getText().toString());
                    player1artillery = Integer.parseInt(artillery1.getText().toString());
                    player2infantry = Integer.parseInt(infantry2.getText().toString());
                    player2armored = Integer.parseInt(armored2.getText().toString());
                    player2artillery = Integer.parseInt(artillery2.getText().toString());
                    Mission mission = new Mission(Mission.DEFAULT, newMissionName, newMissionIntro,
                            nation1, player1infantry, player1armored, player1artillery,
                            nation2, player2infantry, player2armored, player2artillery);


                    Bundle result = new Bundle();
                    result.putString(MISSION, Mission.toJson(mission));
                    getParentFragmentManager().setFragmentResult(MissionFragment.CUSTOM_MISSION_INFO, result);

                    dismiss();
                }
                if(nation1 == nation2){
                    Toast.makeText(getContext(),
                            "Страна не может воевать против самой себя!", Toast.LENGTH_LONG).show();
                }
                if(player1infantry < 1 && player1armored < 1 && player1artillery < 1 &&
                        player2infantry < 1 && player2armored < 1 && player2artillery < 1){
                    Toast.makeText(getContext(),
                            "Необходимо минимум по одной дивизии кождой стороне", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (OnScenarioListener) getActivity();
        } catch (ClassCastException exception){
            exception.printStackTrace();
        }
    }


    private void findViews(View v){
        name = v.findViewById(R.id.name);
        intro = v.findViewById(R.id.intro);
        nation_spinner1 = v.findViewById(R.id.nation1);
        nation_spinner2 = v.findViewById(R.id.nation2);
        infantry1 = v.findViewById(R.id.infantry1);
        infantry2 = v.findViewById(R.id.infantry2);
        armored1 = v.findViewById(R.id.armored1);
        armored2 = v.findViewById(R.id.armored2);
        artillery1 = v.findViewById(R.id.artillery1);
        artillery2 = v.findViewById(R.id.artillery2);
        ready = v.findViewById(R.id.ready);
    }
}