package com.kypcop.chroniclesofwwii.game.logic.divisions;

import com.kypcop.chroniclesofwwii.game.logic.board.MotionMove;
import com.kypcop.chroniclesofwwii.game.logic.player.Player;

public class InfantryDivision extends Division {

    private static final int MAX_HEALTH = 250;

    public InfantryDivision(int id, Player attachment){
        super(id, attachment);
        this.health = MAX_HEALTH;
        this.softAttack = 100;
        this.hardAttack = 20;
        type = Type.INFANTRY;
    }

    @Override
    public boolean isValidMove(MotionMove move) {
        return move.getColumnDiff() < 2 && move.getRowDiff() < 2;
    }

}
