package com.kypcop.chroniclesofwwii.game.Logic.Divisions;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.Logic.Board.Tile;
import com.kypcop.chroniclesofwwii.game.Logic.Board.Move;
import com.kypcop.chroniclesofwwii.game.Logic.Player.Player;

import java.util.ArrayList;
import java.util.List;

public class ArmoredDivision extends Division {

    public ArmoredDivision(int id, Player attachment) {
        super(id, attachment);
        drawableResource = R.drawable.armored;
        chosenDrawableResource = R.drawable.armored_chosen;
        this.health = 200;
        this.softattack = 200;
        this.hardattack = 100;
        type = Type.ARMORED;
    }

    @Override
    public boolean isValidMove(Move move) {
        return move.getDiff() < 3;
    }
}
