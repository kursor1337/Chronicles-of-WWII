package com.kypcop.chroniclesofwwii.game.Logic.Divisions;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.Logic.Board.Tile;
import com.kypcop.chroniclesofwwii.game.Logic.Board.Move;
import com.kypcop.chroniclesofwwii.game.Logic.Player.Player;

public class InfantryDivision extends Division {

    public InfantryDivision(int id, Player attachment){
        super(id, attachment);
        drawableResource = R.drawable.infantry;
        chosenDrawableResource = R.drawable.infantry_chosen;
        this.health = 250;
        this.softattack = 100;
        this.hardattack = 20;
        type = Type.INFANTRY;
    }

    @Override
    public boolean isValidMove(Move move) {
        return move.getColumnDiff() < 2 && move.getRowDiff() < 2;
    }

}
