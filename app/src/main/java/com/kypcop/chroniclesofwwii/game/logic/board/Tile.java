package com.kypcop.chroniclesofwwii.game.logic.board;

import android.util.Log;

import com.kypcop.chroniclesofwwii.game.logic.divisions.Division;

import org.jetbrains.annotations.NotNull;

public class Tile{

    public interface ChangeListener{
        void onDivisionChanged(Tile tile);
        void onTileCleared(Tile tile);
    }

    private ChangeListener changeListener;
    private final int row;
    private final int column;
    private final int coordinate;
    private Division division;

    public Tile(int row, int column){
        this.row = row;
        this.column = column;
        this.coordinate = Integer.parseInt(Integer.toString(row) + column);
    }


    public void setChangeListener(ChangeListener listener){
        Log.i("Tile", "setChangeListener");
        changeListener = listener;
    }

    public Division getDivision(){
        return division;
    }


    public int getCoordinate(){
        return coordinate;
    }

    public boolean isOccupied(){
        return division != null;
    }

    public void setDivision(@NotNull Division division){
        Log.i("Tile", "setDivision: " + division.getType());
        if(!isOccupied()) {
            this.division = division;
            changeListener.onDivisionChanged(this);
        }
        division.setTileCoordinate(this);
    }
    public void clearTile(){
        Log.i("Tile", "clearTile");
        division = null;
        changeListener.onTileCleared(this);
    }
    public int getRow() {
        return row;
    }
    public int getColumn() {
        return column;
    }

}
