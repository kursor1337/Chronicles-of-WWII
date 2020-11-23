package com.kypcop.chroniclesofwwii.game.logic.board;


import android.util.Log;

import com.kypcop.chroniclesofwwii.game.logic.divisions.Division;
import com.kypcop.chroniclesofwwii.game.logic.player.Player;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int DEFAULT_BOARD_SIZE = 8;
    private static final String TAG = "Board";

    private final Tile[][] tiles;
    private final int size;


    public Board(){
        this(DEFAULT_BOARD_SIZE);
    }

    public Board(int size) {
        tiles = new Tile[size][size];
        this.size = size;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                tiles[row][column] = new Tile(row, column);
            }
        }
    }

    public List<Tile> getCoordinatesOfDivisions(@NotNull Player player) {
        return getCoordinatesOfDivisions(player.getRole());
    }

    public List<Tile> getCoordinatesOfDivisions(int role){
        String roleName = role == Player.ROLE_ENEMY ? "Enemy" : "Me";
        Log.i(TAG, "Getting coordinates of divisions of " + roleName);
        List<Tile> coordinates = new ArrayList<>();
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(tiles[i][j].isOccupied() && tiles[i][j].getDivision().getKeeper().getRole() == role){
                    coordinates.add(tiles[i][j]);
                }
            }
        }
        return coordinates;
    }


    public int size() {
        return size;
    }

    public Tile getTileByCoordinate(int coordinate) {
        return getTileByCoordinate(coordinate / 10, coordinate % 10);
    }

    public Tile getTileByCoordinate(int i, int j){
        return tiles[i][j];
    }


    public void setDivisionOnTileWithCoordinate(Division division, int coordinate){
        Log.i(TAG, "Setting division on coordinate " + coordinate);
        getTileByCoordinate(coordinate).setDivision(division);
    }

    public void setDivisionOnTileWithCoordinate(Division division, int i, int j){
        Log.i(TAG, "Setting division on coordinate " + i + "" + j);
        getTileByCoordinate(i, j).setDivision(division);


    }

}


