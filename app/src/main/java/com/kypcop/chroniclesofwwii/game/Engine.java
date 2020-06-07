package com.kypcop.chroniclesofwwii.game;

import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.kypcop.chroniclesofwwii.game.Logic.Board.Board;
import com.kypcop.chroniclesofwwii.game.Logic.Board.Move;
import com.kypcop.chroniclesofwwii.game.Logic.Board.Tile;
import com.kypcop.chroniclesofwwii.game.Logic.Divisions.Division;
import com.kypcop.chroniclesofwwii.game.Logic.Missions.Mission;
import com.kypcop.chroniclesofwwii.game.Logic.Player.Player;
import com.kypcop.chroniclesofwwii.game.Network.WiFiNetwork;
import com.kypcop.chroniclesofwwii.game.Screen.GameScreen;
import com.kypcop.chroniclesofwwii.game.Screen.HUD;

public class Engine {

    public static final WiFiNetwork NETWORK = new WiFiNetwork();

    public static final int IS_SERVER = 712;
    public static final int IS_CLIENT = 713;
    public static final int SINGLE_PLAYER = 1714;
    public static final int MULTI_PLAYER = 1715;
    public static final String MODE = "mode";
    public static final String ROLE = "role";

    private static int turn = 0;
    private Division chosenDivision;
    private Move currentMove;
    private final Board board;
    private Tile[] tile = new Tile[2]; //tile[0] - previous tile, tile[1] - current tile

    private final OnClickListener player2divClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(turn % 2 == 0 && chosenDivision == null){
                int index = id % 10;
                chosenDivision = hud.getPlayerControls(Player.SECOND).get(index).getOneDivision();
            } else{
                showText("Не ваш ход");
            }
            NETWORK.sendIdByNet(id);
        }
    };

    private final OnClickListener tileClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            handleAction(id);
            int i = id / 10, j = id % 10;
            i = board.getSize() - 1 - i;
            j = board.getSize() - 1 - j;
            String s = i + Integer.toString(j);
            id = Integer.parseInt(s);
            NETWORK.sendIdByNet(id);
        }
    };

    private void handleAction(int id){
        int i = id / 10, j = id % 10;
        if(isMotionMove(id)) {
            motionMove(id);
            return;
        }
        if(board.tiles[i][j].isOccupied()){
            if(!rightOwnership(turn, board.tiles[i][j].getDivision()) && chosenDivision == null){
                showText("Не ваш ход");
            }
            if(isPickingMove(id)){
                pickingMove(id);
            }
        } else if(isSettingMove(id)) {
            settingMove(id);
        }
        nextTurn();
    }


    private HUD hud;
    private Player player1;
    private Player player2;
    private Mission mission;
    private GameScreen gameScreen;

    public Engine(GameScreen gameScreen, HUD hud){
        this.gameScreen = gameScreen;
        this.hud = hud;
        mission = hud.getMission();
        player1 = mission.getPlayer1();
        player2 = mission.getPlayer2();
        hud.setControlListeners(player2divClick);
        board = new Board(gameScreen, 6);
    }

    /**
     *
     * @param turn (number of turn)
     * @param division (division on a tile which's button was clicked)
     * @return true if it's right ownership / false if not
     */
    private boolean rightOwnership(int turn, Division division){
        if(division == null){
            return false;
        }
        if(turn % 2 == 0 && division.getAttachment() == player2){
            return true;
        }
        if(turn % 2 == 1 && division.getAttachment() == player1){
            return true;
        }
        return false;
    }


    private void showText(String text){
        Toast.makeText(gameScreen, text, Toast.LENGTH_SHORT).show();
    }


    /**
     * different types of moves
     * @param id (id of the clicked button(1 number which characterizes
     *           row and column of the button))
     * @return true/false if it's that type of move
     */

    private boolean isMotionMove(int id){
        if(tile[0] == null) return false;
        int i = id / 10, j = id % 10;
        return !rightOwnership(turn, board.tiles[i][j].getDivision()) && chosenDivision != null;
    }
    private boolean isPickingMove(int id){
        int i = id /10, j = id % 10;
        return rightOwnership(turn, board.tiles[i][j].getDivision());
    }
    private boolean isSettingMove(int id){
        int i = id / 10, j = id % 10;
        if(chosenDivision != null && tile[0] == null){
            if(i == board.getSize() - 1 && turn % 2 == 0){
                return true;
            }
            if(i == 0 && turn % 2 == 1){
                return true;
            }
        }
        return false;
    }

    private void motionMove(int id){
        int i = id / 10, j = id % 10;
        tile[1] = board.tiles[i][j];
        currentMove = new Move(tile[0], tile[1]);
        if(chosenDivision.isValidMove(currentMove)){
            chosenDivision.move(new Move(tile[0], tile[1]));
        } else{
            return;
        }
        board.hideLegalMoves(chosenDivision.calculateLegalMoves(board, tile[0]));
        tile[0].getButton().setAlpha(1);
        tile[0] = null;
        tile[1] = null;
        chosenDivision = null;
        turn++;
        showText(turn + "");
    }

    private void pickingMove(int id){
        int i = id / 10, j = id % 10;
        if (isPickingMove(id)) {
            if (chosenDivision != null) {
                board.hideLegalMoves(chosenDivision.calculateLegalMoves(board, tile[0]));
                tile[0].getButton().setAlpha(1);
            }
            chosenDivision = board.tiles[i][j].getDivision();
            tile[0] = board.tiles[i][j];
            tile[0].getButton().setAlpha((float) 0.6);
            board.showLegalMovesOnBoard(chosenDivision.calculateLegalMoves(board, tile[0]));
            showText("Picking Move");
        }
    }

    private void settingMove(int id){
        int i = id / 10, j = id % 10;
        board.tiles[i][j].setDivision(chosenDivision);
        chosenDivision = null;
        turn++;
        showText(turn + "");

    }

    public static int getScreenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    public void initializeRows(TableLayout layout){
        int width = getScreenWidth() / board.getSize();
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, width);
        TableRow[] rows = new TableRow[board.getSize()];
        for(int i = 0; i < board.getSize(); i++){
            rows[i] = new TableRow(gameScreen);
            rows[i].setLayoutParams(params);
            layout.addView(rows[i]);
            for(int j = 0; j < board.getSize(); j++){
                rows[i].addView(board.tiles[i][j].getButton());
                board.tiles[i][j].getButton().setOnClickListener(tileClick);
            }
        }
    }

    private void nextTurn(){
        board.disableEnemyButtons(player1);
        turn++;
        if(player1.lost()){
            showText("Вы победили!");
            gameScreen.buildAlertMessageEndOfTheGame(true);
        } else if(player2.lost()){
            showText("Вы проиграли!");
            gameScreen.buildAlertMessageEndOfTheGame(false);
        }

    }

    public void receiveId(int id) {
        if(id > 600000 && turn % 2 == 1 && chosenDivision == null) {
            int index = id % 10;
            chosenDivision = hud.getPlayerControls(Player.FIRST).get(index).getOneDivision();
        }
        handleAction(id);
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }
}
