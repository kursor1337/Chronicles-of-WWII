package com.kypcop.chroniclesofwwii.game.logic.player;

import android.os.Handler;

import com.kypcop.chroniclesofwwii.game.logic.Engine;
import com.kypcop.chroniclesofwwii.game.logic.Nation;
import com.kypcop.chroniclesofwwii.game.logic.board.Board;
import com.kypcop.chroniclesofwwii.game.logic.board.MotionMove;
import com.kypcop.chroniclesofwwii.game.logic.board.Move;
import com.kypcop.chroniclesofwwii.game.logic.board.Tile;
import com.kypcop.chroniclesofwwii.game.logic.divisions.Division;
import com.kypcop.chroniclesofwwii.game.logic.divisions.Type;
import com.kypcop.chroniclesofwwii.game.screen.gameElements.HUD;
import com.kypcop.chroniclesofwwii.game.screen.gameElements.TileView;

import java.util.List;

public class AI extends Player{

    private static final int AI_ID = Player.ROLE_ENEMY;
    private static final int PLAYER_ID = Player.ROLE_ME;

    private AI(int role, Nation nation, int infantry, int armored, int artillery) {
        super(role, 1, nation, infantry, armored, artillery);
    }

    public AI(Player player){
        this(player.getRole(), player.getNation(),
                player.getNumberOfDivisions(Type.INFANTRY),
                player.getNumberOfDivisions(Type.ARMORED),
                player.getNumberOfDivisions(Type.ARTILLERY));
    }

    public Move getMoveFromAI(Engine engine, HUD hud){
        Move move = null;
        List<HUD.Control> aiControls = hud.getMyControls();
        Board board = engine.getBoard();

        int ais_infantry = aiControls.get(0).getQuantity();
        int ais_armored = aiControls.get(1).getQuantity();
        int ais_artillery = aiControls.get(2).getQuantity();

        List<Tile> aiDivisionsOnBoard = board.getCoordinatesOfDivisions(AI_ID);
        List<Tile> playerDivisionsOnBoard = board.getCoordinatesOfDivisions(PLAYER_ID);

        if(aiDivisionsOnBoard.size() == 0){
            move = new Move(hud.getEnemyControls().get(0), board.getTileByCoordinate(0));
            return move;
        }
        Division division = aiDivisionsOnBoard.get(0).getDivision();
        Tile start = division.getTile(board);
        Tile stop;
        try {
            stop = board.getTileByCoordinate(start.getCoordinate() + 10);
        } catch(Exception e){
            stop = board.getTileByCoordinate(start.getCoordinate() + 1);
        }

        move = new Move(new MotionMove(start, stop));
        return move;
    }

    private class AIThread extends Thread{

        int ais_infantry;
        int ais_armored;
        int ais_artillery;

        int player_infantry;
        int player_armored;
        int player_artillery;

        AIThread(Handler handler,
                 int ais_infantry, int ais_armored, int ais_artillery,
                 int player_infantry, int player_armored, int player_artillery,
                 List<TileView> aiDivOnBoard, List<TileView> playerDivOnBoard){


            this.ais_infantry = ais_infantry;
            this.ais_armored = ais_armored;
            this.ais_artillery = ais_artillery;
            this.player_infantry = player_infantry;
            this.player_armored = player_armored;
            this.player_artillery = player_artillery;


        }
    }


}
