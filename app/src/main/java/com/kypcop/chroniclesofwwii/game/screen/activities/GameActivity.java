package com.kypcop.chroniclesofwwii.game.screen.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.Const;
import com.kypcop.chroniclesofwwii.game.connection.Connection;
import com.kypcop.chroniclesofwwii.game.connection.Host;
import com.kypcop.chroniclesofwwii.game.logic.Engine;
import com.kypcop.chroniclesofwwii.game.logic.board.Move;
import com.kypcop.chroniclesofwwii.game.logic.missions.Mission;
import com.kypcop.chroniclesofwwii.game.logic.player.Player;
import com.kypcop.chroniclesofwwii.game.screen.fragments.SimpleDialogFragment;
import com.kypcop.chroniclesofwwii.game.screen.gameElements.BoardLayout;
import com.kypcop.chroniclesofwwii.game.screen.gameElements.HUD;
import com.kypcop.chroniclesofwwii.game.screen.gameElements.TileView;

import org.jetbrains.annotations.NotNull;

import static com.kypcop.chroniclesofwwii.game.Const.connection.CANCEL_CONNECTION;
import static com.kypcop.chroniclesofwwii.game.Const.connection.CLIENT;
import static com.kypcop.chroniclesofwwii.game.Const.connection.CONNECTED_DEVICE;
import static com.kypcop.chroniclesofwwii.game.Const.game.MISSION;
import static com.kypcop.chroniclesofwwii.game.Const.game.MULTIPLAYER_GAME_MODE;


public class GameActivity extends AppCompatActivity{

    private static final int PICKING_MOVE = 42;
    private static final int MOTION_MOVE = 558;
    private static final int ADDING_MOVE = 407;
    private static final int SETTING_MOVE = 363;
    private static final String TAG = "GameActivity";

    private Host host;
    Connection connection;
    Gson gson = new Gson();

    private final Connection.ReceiveListener receiveListener = new Connection.ReceiveListener() {
        @Override
        public void onReceive(@NotNull String string) {
            if(string.equals(CANCEL_CONNECTION)) {
                Toast.makeText(GameActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            processReceivedMove(Move.decodeString(string, engine.getBoard(), hud));
        }
    };

    View.OnClickListener tileClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TileView tileView = boardLayout.getTileViewByCoordinate(view.getId());
            Log.i(TAG, "" + view.getId());
            Log.i(TAG, tileView.getTile() + "");
            if(isMotionMoveNow(tileView)){
                motionMove(tileView);
            } else if(isAddMoveNow(tileView)){
                addMove(tileView);
            } else if(isPickingClickNow(tileView)){
                pickingClick(tileView);
            }

        }
    };

    View.OnClickListener uiClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(chosenControl != null && selectedTileView != null){
                chosenControl.addDivision(selectedTileView.getDivision());
                boardLayout.hideLegalMoves();
            }
            selectedTileView = null;
            chosenControl = hud.getControlByButtonId(v.getId());
            Log.i(TAG, "HUD click, type = " + chosenControl.type);
            int row;
            if(isInverted()) row = 0;
            else row = 7;
            boardLayout.showLegalMoves(row);
        }
    };

    private int previousMoveType;
    private String mode;

    protected static final int previous = 0;
    protected static final int current = 1;

    protected TileView selectedTileView;
    protected HUD.Control chosenControl;

    //tile[0] - previous, tile[1] - current

    LinearLayout layout1;
    LinearLayout layout2;
    HUD hud;
    Mission mission;
    Player enemy;
    Player me;
    Engine engine;
    BoardLayout boardLayout;

    EventListener eventListener = new EventListener() {
        @Override
        public void onMyMoveComplete(Move move) {
            Log.i("EventListener", "onMyMoveComplete");
            connection.send(move.toCode());
            disableScreen();
            boardLayout.hideLegalMoves();
        }

        @Override
        public void onEnemyMoveComplete(Move move) {
            Log.i("EventListener", "onEnemyMoveComplete");
            enableScreen();
        }
    };

    public EventListener getEventListener() {
        return eventListener;
    }

    public View.OnClickListener getUiClickListener() {
        return uiClickListener;
    }



    protected void disableScreen(){
        boardLayout.setEnabled(false);
        hud.getMyInterface().disable();
    }

    protected void enableScreen(){
        boardLayout.setEnabled(true);
        hud.getMyInterface().enable();
    }

    protected Mission getMission() {
        return Mission.fromJson(getIntent().getStringExtra(MISSION));
    }

    public View.OnClickListener getTileClickListener(){
        return tileClickListener;
    }

    public void goToMainScreen(){
        Log.i(TAG, "Going to main screen");
        Intent intent = new Intent(this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        System.gc();
        startActivity(intent);
    }

    public boolean isInverted(){
        return mode.equals(CLIENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        //SimpleDialogFragment loadingDialog = showLoadingDialog();
        initializeMultiPlayerFeatures();
        initializeGameFeatures(isInverted());

        TableLayout sqr = findViewById(R.id.table);
        boardLayout = new BoardLayout(sqr, this, engine.getBoard());

        layout1 = findViewById(R.id.hud1);
        layout2 = findViewById(R.id.hud2);
        boardLayout.initializeBoardLayout(isInverted());
        boardLayout.setClickListeners(getTileClickListener());
        if(isInverted()){
            disableScreen();
        }
        //loadingDialog.dismiss();
        boardLayout.hideLegalMoves();
    }

    public SimpleDialogFragment showLoadingDialog(){
        SimpleDialogFragment simpleDialogFragment = new SimpleDialogFragment.Builder(this)
                .setCancelable(true)
                .setMessage("Loading...")
                .build();
        simpleDialogFragment.show(getSupportFragmentManager(), "showLoadingDialog()");
        return simpleDialogFragment;
    }

    //init of hud and engine
    //engine and hud are separated now, so gameActivity needs
    //to be the mediator(посредник)
    protected void initializeGameFeatures(boolean invert){
        mission = getMission();
        if(invert){
            mission.invertPlayers();
        }
        enemy = mission.getEnemyPlayer();
        me = mission.getMePlayer();
        hud = new HUD(enemy, me, this);
        hud.setControlListeners(getUiClickListener());
        engine = new Engine(this);
    }

    private void initializeMultiPlayerFeatures(){
        mode = getIntent().getStringExtra(MULTIPLAYER_GAME_MODE);
        host = getIntent().getParcelableExtra(CONNECTED_DEVICE);
        if (host != null) {
            connection = Const.connection.getCurrentConnection();
            if(connection == null){
                Toast.makeText(this, "Some problems", Toast.LENGTH_SHORT).show();
                finish();
            } else{
                connection.setMReceiveListener(receiveListener);
            }
        } else{
            Toast.makeText(this, "Host == null", Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG, "Vse Norm");
    }

    public void buildAlertMessageEndOfTheGame(boolean win) {
        String result;
        if(win){
            result = getString(R.string.you_won);
        } else{
            result = getString(R.string.you_lose);
        }
        SimpleDialogFragment dialog = new SimpleDialogFragment.Builder(this)
                .setMessage(result).setCancelable(false)
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToMainScreen();
                    }
                }).build();
        dialog.show(getSupportFragmentManager(), "");
    }

    public Player getEnemy(){
        return enemy;
    }

    public Player getMe(){
        return me;
    }

    //Event listener helps to receive info from engine
    //Abstract coz single, server and client game activities need to behave differently

    /**
     * abstract class to receive info from the engine
     */
    public abstract class EventListener{
        public void onGameEnd(boolean win){
            buildAlertMessageEndOfTheGame(win);
        }
        //it's for redrawing the board

        public abstract void onMyMoveComplete(Move move);
        public abstract void onEnemyMoveComplete(Move move);
        public void showText(String s){
            Toast.makeText(GameActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }

    protected void processReceivedMove(Move move){
        Log.i(TAG, "Process received move");
        engine.handleEnemyMove(move);
    }

    /*
    Some engine stuff that I decided to move into activity coz idk
     */

    protected boolean isMotionMoveNow(TileView tileV){
        if (selectedTileView == null || !selectedTileView.isOccupied()) return false;
        if(selectedTileView == tileV) return false;
        if(selectedTileView.isOccupied()){
            if(tileV.getDivision() == null){
                return true;
            } else return !engine.myOwnership(tileV.getDivision());
        }
        return false;
    }

    protected boolean isAddMoveNow(TileView tileV){
        if(tileV.isOccupied()) return false;
        if(!(tileV.getRow() == 7 && !isInverted()) && !(tileV.getRow() == 0 && isInverted())){
            Log.i("GameActivity", "isAddMove" + tileV.getCoordinate());
            return false;
        }
        return chosenControl != null && selectedTileView == null;
    }

    protected boolean isPickingClickNow(TileView tileV){
        if(!tileV.isOccupied()) return false;
        if(tileV.getDivision().getKeeper() != me) return false;
        if(tileV == selectedTileView){
            boardLayout.hideLegalMoves();
            selectedTileView = null;
            return false;
        }
        if (selectedTileView != null && selectedTileView.getDivision().getKeeper() == me) {
            boardLayout.hideLegalMoves();
            selectedTileView = tileV;
            return true;
        }
        return chosenControl == null;
    }

    protected void motionMove(TileView tileV){
        Log.i(TAG, "Motion move");
        Move move = new Move(selectedTileView.getTile(), tileV.getTile());
        engine.handleMyMove(move);
        selectedTileView = null;
        previousMoveType = MOTION_MOVE;
    }

    protected void addMove(TileView tileV){
        Log.i(TAG, "Add move");
        Move move = new Move(chosenControl, tileV.getTile());
        engine.handleMyMove(move);
        previousMoveType = ADDING_MOVE;
        chosenControl = null;

    }

    protected void pickingClick(TileView tileV){
        if(chosenControl != null) chosenControl = null;
        if(!tileV.isOccupied()) return;
        selectedTileView = tileV;
        boardLayout.showLegalMoves(selectedTileView.getDivision());
        Log.i(TAG, "Picking move");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(connection != null){
            connection.send(Const.connection.CANCEL_CONNECTION);
            connection.dispose();
        }
        if(Const.connection.getCurrentConnection() != null){
            Log.i(TAG, "Disposing global connection");
            Const.connection.getCurrentConnection().dispose();
        }
        Const.connection.setCurrentConnection(null);
    }
}

