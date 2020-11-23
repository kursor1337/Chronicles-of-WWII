package com.kypcop.chroniclesofwwii.game.logic;

import android.content.res.Resources;
import android.util.Log;

import com.kypcop.chroniclesofwwii.game.Const;
import com.kypcop.chroniclesofwwii.game.logic.board.AddMove;
import com.kypcop.chroniclesofwwii.game.logic.board.Board;
import com.kypcop.chroniclesofwwii.game.logic.board.MotionMove;
import com.kypcop.chroniclesofwwii.game.logic.board.Move;
import com.kypcop.chroniclesofwwii.game.logic.divisions.Division;
import com.kypcop.chroniclesofwwii.game.logic.player.Player;
import com.kypcop.chroniclesofwwii.game.screen.activities.GameActivity;

public class Engine {

    private static final String TAG = "Engine";

    public enum Status{
        PICKED_DIVISION_FROM_TILE, PICKED_DIVISION_FROM_HUD_CONTROL, SET_DIVISION_ON_TILE
    }

    private final Player enemy;
    private final Player me;
    private final GameActivity.EventListener eventListener;

    private int turn = 0;
    private final Board board;

    public Engine(GameActivity gameActivity){
        Log.i(TAG, "Initialized");
        this.eventListener = gameActivity.getEventListener();
        this.enemy = gameActivity.getEnemy();
        this.me = gameActivity.getMe();
        board = new Board();
    }

    public Player getEnemy(){
        return enemy;
    }

    public Player getMe(){
        return me;
    }

    public static int getScreenWidth(){
        Log.i(TAG, "Getting screen width");
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(){
        Log.i(TAG, "Getting screen height");
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public int getCurrentTurn(){
        return turn;
    }

    public Board getBoard(){
        return board;
    }

    public static boolean isTileId(int value){
        int i = value / 10;
        int j = value % 10;
        return 0 < i && i < Const.game.BOARD_SIZE &&
                0 < j && j < Const.game.BOARD_SIZE;
    }

    public boolean myOwnership(Division division){
        return myTurn() && division.getKeeper() == me;
    }

    private boolean myTurn() {
        return turn % 2 == me.getTurn();
    }

    private void nextTurn(){
        Log.i(TAG, "Next turn");
        turn++;
        if(enemy.lost()){
            eventListener.onGameEnd(true);
        } else if(me.lost()){
            eventListener.onGameEnd(false);
        }

    }

    public void handleEnemyMove(Move move){
        if (move.getAddMove() != null) {
            handleEnemyAddMove(move.getAddMove());
        } else {
            handleEnemyMotionMove(move.getMotionMove());
        }
        eventListener.onEnemyMoveComplete(move);
    }

    public void handleEnemyMotionMove(MotionMove move) {
        Log.i(TAG, "Handle enemy motion move");
        Division division = move.getDestination().getDivision();
        if(division.isValidMove(move)){
            division.move(move, board);
        } else{
            return;
        }
        nextTurn();
    }

    public void handleEnemyAddMove(AddMove move){
        Log.i(TAG, "Handle enemy add move");
        board.setDivisionOnTileWithCoordinate(
                move.getAddingDivision(), move.getDestination().getCoordinate());
        nextTurn();
    }

    public void handleMyMove(Move move){
        if (move.getMotionMove() != null) {
            handleMyMotionMove(move.getMotionMove());
        } else {
            handleMyAddMove(move.getAddMove());
        }
        eventListener.onMyMoveComplete(move);
    }

    private void handleMyAddMove(AddMove move){
        Log.i(TAG, "Handle my add move");
        move.getDestination().setDivision(move.getStart().getOneDivision());
        nextTurn();
    }

    private void handleMyMotionMove(MotionMove move){
        Log.i(TAG, "Handle my motion move");
        move.getStart().getDivision().move(move, board);
        nextTurn();
    }

}
