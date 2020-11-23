package com.kypcop.chroniclesofwwii.game.logic.divisions;

import com.kypcop.chroniclesofwwii.game.logic.board.MotionMove;
import com.kypcop.chroniclesofwwii.game.logic.player.Player;

public class ArmoredDivision extends Division {

    private static final int MAX_HEALTH = 200;

    public ArmoredDivision(int id, Player attachment) {
        super(id, attachment);
        this.health = MAX_HEALTH;
        this.softAttack = 200;
        this.hardAttack = 100;
        type = Type.ARMORED;
    }

    @Override
    public boolean isValidMove(MotionMove move) {
        return move.getDiff() < 3;
    }
}
