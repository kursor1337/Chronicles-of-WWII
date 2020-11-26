package com.kypcop.chroniclesofwwii.game.logic.divisions;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.logic.board.Board;
import com.kypcop.chroniclesofwwii.game.logic.board.MotionMove;
import com.kypcop.chroniclesofwwii.game.logic.board.Tile;
import com.kypcop.chroniclesofwwii.game.logic.player.Player;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Division {

    protected final int id;
    private boolean isDead = false;
    private final Player keeper;
    int health;
    int softAttack; /** легкая атака дивизии(против пехоты) */
    int hardAttack; /** тяжелая атака дивизии(против танков) */
    private int tilePlateCoordinate; /** координата дивизии */
    Type type; /** тип дивизии */

    Division(int id, Player keeper) {
        this.keeper = keeper;
        this.id = id;
    }

    public Type getType(){
        return type;
    }

    public void setTileCoordinate(Tile tile){
        this.tilePlateCoordinate = tile.getCoordinate();
    }

    public Tile getTile(Board board){
        return board.getTileByCoordinate(tilePlateCoordinate);
    }

    public Player getKeeper(){
        return keeper;
    }

    /** переместить дивизию, если же нужная клетка занята, то атаковать ее*/
    public void move(@NotNull MotionMove move, @NotNull Board board){
        if(!isValidMove(move)) return;
        if (move.getDestination().isOccupied()){
            attack(move.getDestination());
        } else{
            move.getDestination().setDivision(this);
            move.getStart().clearTile();
        }
    }

    /** метод атаки */
    private void attack(@NotNull Tile tile){
        if(!tile.isOccupied()) return;
        int damage;
        if(tile.getDivision().getDivisionType() == Type.ARMORED){
            damage = hardAttack;
        } else{
            damage = softAttack;
        }
        tile.getDivision().damage(damage);
        if(tile.getDivision().isDead()){
            tile.getDivision().getKeeper().deleteDivision(type);
            tile.clearTile();
        }
    }
    /** проверка если дивизия уже умерла */
    private boolean isDead(){
        return isDead;
    }

    /** проверка на правильность хода, этот метод переопределяется в наследниках данного класса */
    public boolean isValidMove(MotionMove move){
        return true;
    }

    /** если эту дивизию атаковали */
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

    public static int getDrawableResource(Type type){
        switch(type){
            case ARTILLERY:
                return R.drawable.unit_artillery;
            case ARMORED:
                return R.drawable.unit_armored;
            default:
                return R.drawable.unit_infantry;
        }
    }

    /** вычислить все возможные ходы для это йдивизии */
    public List<MotionMove> calculateLegalMoves(Board board){
        List<MotionMove> legalMotionMoves = new ArrayList<>();
        for(int i = 0; i < board.size(); i++){
            for(int j = 0; j < board.size(); j++){
                if(!(tilePlateCoordinate == board.getTileByCoordinate(i, j).getCoordinate())){
                    MotionMove move = new MotionMove(getTile(board), board.getTileByCoordinate(i, j));
                    if(isValidMove(move)){
                        legalMotionMoves.add(move);
                    }
                }
            }
        }
        return legalMotionMoves;
    }
}
