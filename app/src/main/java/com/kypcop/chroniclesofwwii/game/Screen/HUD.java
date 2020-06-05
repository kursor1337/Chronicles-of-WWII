package com.kypcop.chroniclesofwwii.game.Screen;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.kypcop.chroniclesofwwii.game.Logic.Divisions.ArmoredDivision;
import com.kypcop.chroniclesofwwii.game.Logic.Divisions.ArtilleryDivision;
import com.kypcop.chroniclesofwwii.game.Logic.Divisions.Division;
import com.kypcop.chroniclesofwwii.game.Logic.Divisions.InfantryDivision;
import com.kypcop.chroniclesofwwii.game.Logic.Divisions.Type;
import com.kypcop.chroniclesofwwii.game.Logic.Missions.Mission;
import com.kypcop.chroniclesofwwii.game.Logic.Nation;
import com.kypcop.chroniclesofwwii.game.Logic.Player.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class HUD {
    private static final int INFANTRY_ID = 100;
    private static final int ARMORED_ID = 101;
    private static final int ARTILLERY_ID = 102;


    private Mission mission;
    private LinearLayout layout1;
    private LinearLayout layout2;
    private Player player1;
    private Player player2;
    private Control infantry1;
    private Control armored1;
    private Control artillery1;
    private Control infantry2;
    private Control armored2;
    private Control artillery2;

    HUD(Context context, Mission mission, LinearLayout layout1, LinearLayout layout2){
        this.mission = mission;
        player1 = mission.getPlayer1();
        player2 = mission.getPlayer2();
        this.layout1 = layout1;
        this.layout2 = layout2;
        this.layout2.setGravity(Gravity.BOTTOM);
        infantry1 = new Control(INFANTRY_ID, context, player1, Type.INFANTRY);
        armored1 = new Control(ARMORED_ID, context, player1, Type.ARMORED);
        artillery1 = new Control(ARTILLERY_ID, context, player1, Type.ARTILLERY);
        infantry2 = new Control(INFANTRY_ID, context, player2, Type.INFANTRY);
        armored2 = new Control(ARMORED_ID, context, player2, Type.ARMORED);
        artillery2 = new Control(ARTILLERY_ID, context, player2, Type.ARTILLERY);
        this.layout1.setBackgroundResource(Nation.getImageID(player1.getNation()));
        this.layout1.getBackground().setAlpha(80);
        this.layout2.setBackgroundResource(Nation.getImageID(player2.getNation()));
        this.layout2.getBackground().setAlpha(80);
    }

    public List<Control> getPlayerControls(int player) {
        List<Control> controls = new ArrayList<>();
        switch(player){
            case Player.FIRST:
                controls.add(infantry1);
                controls.add(armored1);
                controls.add(artillery1);
                return controls;
            case Player.SECOND:
                controls.add(infantry2);
                controls.add(armored2);
                controls.add(artillery2);
                return controls;
        }
        return null;
    }
    void setButtonParams(LinearLayout.LayoutParams params){
        infantry1.button.setLayoutParams(params);
        armored1.button.setLayoutParams(params);
        artillery1.button.setLayoutParams(params);
        infantry2.button.setLayoutParams(params);
        armored2.button.setLayoutParams(params);
        artillery2.button.setLayoutParams(params);
    }
    void setTextParams(LinearLayout.LayoutParams params){
        infantry1.textView.setLayoutParams(params);
        armored1.textView.setLayoutParams(params);
        artillery1.textView.setLayoutParams(params);
        infantry2.textView.setLayoutParams(params);
        armored2.textView.setLayoutParams(params);
        artillery2.textView.setLayoutParams(params);
    }

    public Mission getMission() {
        return mission;
    }

    void initializeHud(){
        layout1.addView(infantry1.getButton());
        layout1.addView(infantry1.getTextView());
        layout1.addView(armored1.getButton());
        layout1.addView(armored1.getTextView());
        layout1.addView(artillery1.getButton());
        layout1.addView(artillery1.getTextView());

        layout2.addView(infantry2.getButton());
        layout2.addView(infantry2.getTextView());
        layout2.addView(armored2.getButton());
        layout2.addView(armored2.getTextView());
        layout2.addView(artillery2.getButton());
        layout2.addView(artillery2.getTextView());
    }

    public void setControlListeners(View.OnClickListener listener2){
        infantry2.getButton().setOnClickListener(listener2);
        armored2.getButton().setOnClickListener(listener2);
        artillery2.getButton().setOnClickListener(listener2);
    }


    public static class Control {
        final int id;
        Stack<Division> divisionStack = new Stack<>();
        Button button;
        TextView textView;
        int quantity;
        Type type;
        Player player;


        Control(int id, Context context, Player player, Type type){
            this.id = id;
            this.player = player;
            button = new Button(context);
            button.setId(Integer.parseInt(Integer.toString(player.getPlayerId()) + id));
            textView = new TextView(context);
            this.type = type;
            quantity = player.getNumberOfDivisions(type);
            textView.setTextSize(30);
            switch (type){
                case INFANTRY:
                    for(int i = 0; i < quantity; i++){
                        divisionStack.add(new InfantryDivision(i, player)); }
                    break;
                case ARMORED:
                    for(int i = 0; i < quantity; i++){
                        divisionStack.add(new ArmoredDivision(i, player)); }
                    break;
                case ARTILLERY:
                    for(int i = 0; i < quantity; i++){
                        divisionStack.add(new ArtilleryDivision(i, player)); }
                    break;
                default:
                    return;
            }
            button.setText(Type.toString(type));
            update();
        }


        private void update(){
            textView.setText(divisionStack.size() + "");
        }

        public Division getOneDivision(){
            if(!divisionStack.isEmpty()) {
                Division division = divisionStack.peek();
                divisionStack.pop();

                update();
                return division;
            }
            return null;
        }

        public void addDivision(Division division){
            divisionStack.push(division);
        }

        View getButton() {
            return button;
        }

        View getTextView(){
            return textView;
        }
    }
}
