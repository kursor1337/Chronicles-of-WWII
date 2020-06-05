package com.kypcop.chroniclesofwwii.game.Logic.Divisions;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.Logic.Board.Board;
import com.kypcop.chroniclesofwwii.game.Logic.Board.Tile;
import com.kypcop.chroniclesofwwii.game.Logic.Board.Move;
import com.kypcop.chroniclesofwwii.game.Logic.Player.Player;

import java.util.ArrayList;
import java.util.List;

public class ArtilleryDivision extends Division {

    public ArtilleryDivision(int id, Player attachment){
        super(id, attachment);
        drawableResource = R.drawable.artillery;
        chosenDrawableResource = R.drawable.artillery_chosen;
        this.health = 120;
        this.softattack = 200;
        this.hardattack = 200;
        type = Type.ARTILLERY;
    }


    @Override
    public boolean isValidMove(Move move) {
        if(move.getDiff() < 2){
            return true;
        }
        return move.isAttacking && move.getDiff() < 3;
    }
}
