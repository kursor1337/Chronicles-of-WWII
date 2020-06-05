package com.kypcop.chroniclesofwwii.game.Logic.Divisions;

import android.graphics.Bitmap;

import com.kypcop.chroniclesofwwii.game.Logic.Board.Board;
import com.kypcop.chroniclesofwwii.game.Logic.Board.Tile;
import com.kypcop.chroniclesofwwii.game.Logic.Board.Move;
import com.kypcop.chroniclesofwwii.game.Logic.Player.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class Division {
    protected final int id;
    int drawableResource;
    int chosenDrawableResource;
    private boolean isDead = false;
    private Player attachment;
    int health;
    int softattack;
    int hardattack;
    Type type;

    Division(int id, Player attachment) {
        this.attachment = attachment;
        this.id = id;
    }

    public Player getAttachment(){
        return attachment;
    }

    public void move(Move move){
        if(!isValidMove(move)) return;
        if (move.getDestination().isOccupied()){
            attack(move.getDestination());
            move.getStart().setImage(drawableResource);
        } else{
            move.getDestination().setDivision(this);
            move.getStart().clearTile();
        }
    }

    private void attack(Tile tile){
        if(!tile.isOccupied()) return;
        int damage;
        if(tile.getDivision().getDivisionType() == Type.ARMORED){
            damage = hardattack;
        } else{
            damage = softattack;
        }
        tile.getDivision().damage(damage);
        if(tile.getDivision().isDead()){
            tile.getDivision().getAttachment().deleteDivision(type);
            tile.clearTile();
        }
    }

    private boolean isDead(){
        return isDead;
    }

    public abstract boolean isValidMove(Move move);

    private void damage(int damage){
        health -= damage;
        if(health < 1){
            isDead = true;
        }
    }

    private Type getDivisionType(){
        return type;
    }

    public int getId() {
        return id;
    }

    public int getDrawableResource(){
        return drawableResource;
    }

    public int getChosenDrawableResource(){
        return chosenDrawableResource;
    }

    public List<Move> calculateLegalMoves(Board board, Tile tile){
        List<Move> legalMoves = new ArrayList<>();
        for(int i = 0; i < board.tiles.length; i++){
            for(int j = 0; j < board.tiles[i].length; j++){
                if(!(tile == board.tiles[i][j])){
                    Move move = new Move(tile, board.tiles[i][j]);
                    if(isValidMove(move)){
                        legalMoves.add(move);
                    }
                }
            }
        }
        return legalMoves;
    }
}
