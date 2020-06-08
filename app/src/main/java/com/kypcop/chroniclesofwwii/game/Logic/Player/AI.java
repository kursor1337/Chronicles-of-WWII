package com.kypcop.chroniclesofwwii.game.Logic.Player;

import com.kypcop.chroniclesofwwii.game.Engine;
import com.kypcop.chroniclesofwwii.game.Logic.Board.Board;
import com.kypcop.chroniclesofwwii.game.Logic.Divisions.Type;
import com.kypcop.chroniclesofwwii.game.Logic.Nation;
import com.kypcop.chroniclesofwwii.game.Screen.HUD;

public class AI extends Player{

    public AI(int id, Nation nation, int infantry, int armored, int artillery) {
        super(id, nation, infantry, armored, artillery);
    }

    public AI(Player player){
        super(player.getPlayerId(), player.getNation(),
                player.getNumberOfDivisions(Type.INFANTRY),
                player.getNumberOfDivisions(Type.ARMORED),
                player.getNumberOfDivisions(Type.ARTILLERY));
    }

    public int getMoveFromAI(Engine engine){
        int move = 0;
        HUD hud = engine.getHud();
        Board board = engine.getBoard();


        return move;

    }
}
