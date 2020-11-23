package com.kypcop.chroniclesofwwii.game.logic.divisions;

import com.kypcop.chroniclesofwwii.game.logic.board.MotionMove;
import com.kypcop.chroniclesofwwii.game.logic.player.Player;

public class ArtilleryDivision extends Division {
    private static final int MAX_HEALTH = 120;

    public ArtilleryDivision(int id, Player attachment){
        super(id, attachment);
        this.health = MAX_HEALTH;
        this.softAttack = 70;
        this.hardAttack = 200;
        type = Type.ARTILLERY;
    }


    @Override
    public boolean isValidMove(MotionMove move) {
        if(move.getDiff() < 2){
            return true;
        }
        return move.isAttacking() && move.getDiff() < 4;
    }
}
