package com.kypcop.chroniclesofwwii.game.Logic.Board;


import android.content.Context;

import com.kypcop.chroniclesofwwii.game.Engine;
import com.kypcop.chroniclesofwwii.game.Logic.Player.Player;

import java.util.List;

public class Board {
    public final Tile[][] tiles;
    private int size;


    public Board(Context context, int size) {
        int length = Engine.getScreenWidth() / size;
        tiles = new Tile[size][size];
        this.size = size;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                tiles[row][column] = new Tile(row, column, context, length);
            }
        }
    }

    public void showLegalMovesOnBoard(List<Move> legalMoves) {
        for (Move move : legalMoves) {
            move.getDestination().addChosen();
        }
    }

    public void hideLegalMoves(List<Move> legalMoves) {
        for (Move move : legalMoves) {
            move.getDestination().removeChosen();
        }
    }


    /**
     * disables buttons which are occupied by enemy and enables player's
     * @param enemy
     */
    public void disableEnemyButtons(Player enemy){
        for(Tile[] row: tiles){
            for(Tile tile: row){
                if(tile.isOccupied()){
                    if(tile.getDivision().getAttachment() == enemy){
                        tile.getButton().setEnabled(false);
                    } else{
                        tile.getButton().setEnabled(true);
                    }
                } else{
                    tile.getButton().setEnabled(true);
                }
            }
        }
    }

    public int getSize() {
        return size;
    }
}


