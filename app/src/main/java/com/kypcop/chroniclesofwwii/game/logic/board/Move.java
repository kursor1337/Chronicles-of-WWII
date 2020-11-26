package com.kypcop.chroniclesofwwii.game.logic.board;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.kypcop.chroniclesofwwii.game.screen.gameElements.HUD;

import org.jetbrains.annotations.NotNull;

public class Move {


    private static final String ADD_MOVE = "add";
    private static final String MOTION_MOVE = "motion";


    private final static transient Gson gson = new Gson();
    private final MotionMove motionMove;
    private final AddMove addMove;


    public Move(@NotNull MotionMove move){
        motionMove = move;
        addMove = null;
    }

    Move(@NotNull AddMove move){
        addMove = move;
        motionMove = null;
    }

    public Move(@NotNull HUD.Control start, @NotNull Tile destination){
        this(new AddMove(start, destination));
    }

    public Move(@NotNull Tile start, @NotNull Tile destination){
        this(new MotionMove(start, destination));
    }

    public MotionMove getMotionMove(){
        return motionMove;
    }

    public AddMove getAddMove(){
        return addMove;
    }

    public boolean hasMotionMove(){
        return motionMove != null;
    }

    public boolean hasAddMove(){
        return addMove != null;
    }

    public static Move fromString(String s){
        return gson.fromJson(s, Move.class);
    }

    @NonNull
    @Override
    public String toString() {
        return gson.toJson(this);
    }


    public byte[] toBytes() {
        String s = toString();
        return s.getBytes();
    }


    public static Move fromBytes(byte[] bytes) {
        String s = new String(bytes);
        return Move.fromString(s);
    }

    public static Move decodeString(String string, Board board, HUD hud){
        String type = string.substring(0, string.indexOf('-'));
        String body = string.substring(string.indexOf('-') + 1);
        if(type.equals(ADD_MOVE)){
            AddMove.Simplified simplified = gson.fromJson(body, AddMove.Simplified.class);
            return new Move(simplified.returnToFullState(hud, board));
        } else{
            MotionMove.Simplified simplified = gson.fromJson(body, MotionMove.Simplified.class);
            return new Move(simplified.returnToFullState(board));
        }
    }

    public String toCode(){
        String code = "";
        String type;
        if(addMove != null){
            type = ADD_MOVE;
            code = type + "-" + gson.toJson(addMove.simplify());
        } else{
            type = MOTION_MOVE;
            code += type + "-" + gson.toJson(motionMove.simplify());
        }
        Log.i("Move", "Coded : " + code);
        return code;
    }

}
