package com.kypcop.chroniclesofwwii.game.logic.board;

import com.kypcop.chroniclesofwwii.game.screen.gameElements.BoardLayout;
import com.kypcop.chroniclesofwwii.game.screen.gameElements.TileView;

import org.jetbrains.annotations.NotNull;

public class MotionMove{

    private final Tile start;
    private final Tile destination;
    private final int rowDiff;
    private final int columnDiff;


    public MotionMove(@NotNull Tile start, @NotNull Tile destination) {
        this.start = start;
        this.destination = destination;
        this.rowDiff = Math.abs(start.getRow() - destination.getRow());
        this.columnDiff = Math.abs(start.getColumn() - destination.getColumn());
    }

    public MotionMove(@NotNull TileView start, @NotNull TileView destination){
        this(start.getTile(), destination.getTile());
    }

    public MotionMove(int startCoordinate, int destCoordinate, @NotNull Board board){
        this(board.getTileByCoordinate(startCoordinate),
                board.getTileByCoordinate(destCoordinate));

    }

    public Simplified simplify(){
        return new Simplified(start.getCoordinate(), destination.getCoordinate());
    }

    public Tile getStart(){
        return start;
    }

    public Tile getDestination() {
        return destination;
    }

    public TileView getStartTilePlate(@NotNull BoardLayout boardLayout){
        return boardLayout.getTileViewByCoordinate(start.getCoordinate());
    }

    public TileView getDestinationTilePlate(@NotNull BoardLayout boardLayout){
        return boardLayout.getTileViewByCoordinate(destination.getCoordinate());
    }

    public boolean isAttacking(){
        return destination.isOccupied();
    }

    public int getRowDiff(){
        return rowDiff;
    }
    public int getColumnDiff(){
        return columnDiff;
    }
    public int getDiff(){
        return columnDiff + rowDiff;
    }


    public static class Simplified{
        int start;
        int destination;

        Simplified(int start, int destination){
            this.start = start;
            this.destination = destination;
        }

        public MotionMove returnToFullState(Board board){
            return new MotionMove(board.getTileByCoordinate(start),
                    board.getTileByCoordinate(destination));
        }



    }

}
