package com.kypcop.chroniclesofwwii.game.Logic.Board;

import android.view.MotionEvent;

public class Move {
    private int row_diff;
    private int column_diff;
    private Tile destinationTile;
    private Tile startTile;
    public final boolean isAttacking;

    public Move(Tile start, Tile destination){
        startTile = start;
        destinationTile = destination;
        row_diff = Math.abs(destinationTile.getRow() - startTile.getRow());
        column_diff = Math.abs(destinationTile.getColumn() - startTile.getColumn());
        isAttacking = destination.isOccupied();
    }

    public int getRowDiff(){
        return row_diff;
    }

    public int getColumnDiff() {
        return column_diff;
    }

    public int getDiff(){
        return column_diff + row_diff;
    }

    public Tile getDestination() {
        return destinationTile;
    }

    public Tile getStart() {
        return startTile;
    }
}
