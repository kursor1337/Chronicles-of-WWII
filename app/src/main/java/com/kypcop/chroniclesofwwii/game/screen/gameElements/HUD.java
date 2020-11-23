package com.kypcop.chroniclesofwwii.game.screen.gameElements;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.logic.Engine;
import com.kypcop.chroniclesofwwii.game.logic.Nation;
import com.kypcop.chroniclesofwwii.game.logic.divisions.ArmoredDivision;
import com.kypcop.chroniclesofwwii.game.logic.divisions.ArtilleryDivision;
import com.kypcop.chroniclesofwwii.game.logic.divisions.Division;
import com.kypcop.chroniclesofwwii.game.logic.divisions.InfantryDivision;
import com.kypcop.chroniclesofwwii.game.logic.divisions.Type;
import com.kypcop.chroniclesofwwii.game.logic.player.Player;
import com.kypcop.chroniclesofwwii.game.screen.activities.GameActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class HUD {
    private static final int INFANTRY_ID = 100;
    private static final int ARMORED_ID = 101;
    private static final int ARTILLERY_ID = 102;
    private static final String TAG = "HUD";

    private static final int[] possibleIds = {
            Integer.parseInt(Player.ROLE_ENEMY + "" + INFANTRY_ID),
            Integer.parseInt(Player.ROLE_ENEMY + "" + ARMORED_ID),
            Integer.parseInt(Player.ROLE_ENEMY + "" + ARTILLERY_ID),
            Integer.parseInt(Player.ROLE_ME + "" + INFANTRY_ID),
            Integer.parseInt(Player.ROLE_ENEMY + "" + ARMORED_ID),
            Integer.parseInt(Player.ROLE_ENEMY + "" + ARTILLERY_ID)
    };

    private final PlayerInterface enemyInterface;
    private final PlayerInterface myInterface;

    private final Player enemy, me; //I, me, my refers to the player

    public HUD(Player enemy, Player me, GameActivity gameActivity){
        Log.i(TAG, "Initialized");
        this.enemy = enemy;
        this.me = me;
        LinearLayout enemyLayout = gameActivity.findViewById(R.id.hud1);
        LinearLayout myLayout = gameActivity.findViewById(R.id.hud2);
        enemyInterface = new PlayerInterface(enemy, enemyLayout);
        myInterface = new PlayerInterface(me, myLayout);


    }


    public List<Control> getMyControls() {
        List<Control> controls = new ArrayList<>();
        controls.add(myInterface.getControl(Type.INFANTRY));
        controls.add(myInterface.getControl(Type.ARMORED));
        controls.add(myInterface.getControl(Type.ARTILLERY));
        return controls;
    }

    public List<Control> getEnemyControls(){
        List<Control> controls = new ArrayList<>();
        controls.add(enemyInterface.getControl(Type.INFANTRY));
        controls.add(enemyInterface.getControl(Type.ARMORED));
        controls.add(enemyInterface.getControl(Type.ARTILLERY));
        return controls;
    }

    public Control getControlByButtonId(int id){
        switch (id){
            case R.id.infantry_button_enemy:
                return enemyInterface.getControl(Type.INFANTRY);
            case R.id.armored_button_enemy:
                return enemyInterface.getControl(Type.ARMORED);
            case R.id.artillery_button_enemy:
                return enemyInterface.getControl(Type.ARTILLERY);
            case R.id.infantry_button_me:
                return myInterface.getControl(Type.INFANTRY);
            case R.id.armored_button_me:
                return myInterface.getControl(Type.ARMORED);
            case R.id.artillery_button_me:
                return myInterface.getControl(Type.ARTILLERY);
            default:
                return null;
        }
    }

    public Control getControlByTypeAnPlayer(int role, Type type){
        if(role == Player.ROLE_ENEMY){
            return enemyInterface.getControl(type);
        } else{
            return myInterface.getControl(type);
        }
    }

    public PlayerInterface getInterfaceByPlayer(Player player){
        return getInterfaceByPlayerRole(player.getRole());
    }

    public PlayerInterface getInterfaceByPlayerRole(int role){
        switch(role) {
            case Player.ROLE_ME:
                return myInterface;
            case Player.ROLE_ENEMY:
                return enemyInterface;
        }
        return null;
    }

    public PlayerInterface getEnemyInterface(){
        return enemyInterface;
    }

    public PlayerInterface getMyInterface(){
        return myInterface;
    }

    public static boolean isHudId(int id){
        for(int i: possibleIds){
            if(id == i){
                return true;
            }
        }
        return false;
    }

    public void setControlListeners(View.OnClickListener listener){
        myInterface.getControl(Type.INFANTRY).getButton().setOnClickListener(listener);
        myInterface.getControl(Type.ARMORED).getButton().setOnClickListener(listener);
        myInterface.getControl(Type.ARTILLERY).getButton().setOnClickListener(listener);
    }



    public static class PlayerInterface {
        Player player;
        Control infantry;
        Control armored;
        Control artillery;
        boolean isEnabled = true;

        public PlayerInterface(Player player, LinearLayout layout){
            this.player = player;
            Button infantryButton;
            Button armoredButton;
            Button artilleryButton;


            setNationFlag(Nation.getImageID(player.getNation()), layout);

            if (player.getRole() == Player.ROLE_ME) {
                infantryButton = layout.findViewById(R.id.infantry_button_me);
                armoredButton = layout.findViewById(R.id.armored_button_me);
                artilleryButton = layout.findViewById(R.id.artillery_button_me);
            } else {
                infantryButton = layout.findViewById(R.id.infantry_button_enemy);
                armoredButton = layout.findViewById(R.id.armored_button_enemy);
                artilleryButton = layout.findViewById(R.id.artillery_button_enemy);
            }
            this.infantry = new Control(infantryButton, player, Type.INFANTRY);
            this.armored = new Control(armoredButton, player, Type.ARMORED);
            this.artillery = new Control(artilleryButton, player, Type.ARTILLERY);
            if(player.getRole() == Player.ROLE_ME){
                enable();
            } else{
                disable();
            }

        }


        private void setNationFlag(@DrawableRes int id, LinearLayout linearLayout){
            Context context = linearLayout.getContext();
            if(context != null){
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
                Bitmap nBitmap = Bitmap.createScaledBitmap(bitmap, Engine.getScreenWidth(),
                        (Engine.getScreenHeight() - Engine.getScreenWidth()) / 2, false);
                BitmapDrawable drawable = new BitmapDrawable(context.getResources(), nBitmap);
                drawable.setAlpha(120);
                linearLayout.setBackground(drawable);
            }

        }

        public Player getPlayer() {
            return player;
        }

        Control getControl(Type type){
            switch (type){
                case INFANTRY:
                    return infantry;
                case ARMORED:
                    return armored;
                default:
                    return artillery;
            }
        }

        public void enable(){
            infantry.getButton().setEnabled(true);
            armored.getButton().setEnabled(true);
            artillery.getButton().setEnabled(true);
            isEnabled = true;
        }
        public void disable(){
            infantry.getButton().setEnabled(false);
            armored.getButton().setEnabled(false);
            artillery.getButton().setEnabled(false);
            isEnabled = false;
        }

        public boolean isEnabled(){
            return isEnabled;
        }

    }

    public static class Control {
        final transient Button button;
        public final Type type;
        final Player player;
        final String buttonText;
        Stack<Division> divisionStack = new Stack<>();


        Control(Button button, Player player, Type type){
            this.player = player;
            this.button = button;
            this.type = type;
            int quantity = player.getNumberOfDivisions(type);
            switch (type){
                default:
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
            }
            buttonText = button.getText().toString();
            button.setText(buttonText + ": " + divisionStack.size());
        }

        private void update(){
            button.setText(buttonText + ": " + divisionStack.size());
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

        public int getId(){
            return button.getId();
        }

        public int getQuantity(){
            return divisionStack.size();
        }

        public List<Division> getDivisions() {
            return divisionStack;
        }

        public void addDivision(Division division){
            divisionStack.push(division);
            update();
        }

        Button getButton() {
            return button;
        }
    }
}
