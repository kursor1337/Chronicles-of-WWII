package com.kypcop.chroniclesofwwii.game.logic.board;

import com.kypcop.chroniclesofwwii.game.logic.divisions.ArmoredDivision;
import com.kypcop.chroniclesofwwii.game.logic.divisions.ArtilleryDivision;
import com.kypcop.chroniclesofwwii.game.logic.divisions.Division;
import com.kypcop.chroniclesofwwii.game.logic.divisions.InfantryDivision;
import com.kypcop.chroniclesofwwii.game.logic.divisions.Type;
import com.kypcop.chroniclesofwwii.game.logic.player.Player;
import com.kypcop.chroniclesofwwii.game.screen.gameElements.HUD;
import com.kypcop.chroniclesofwwii.game.screen.gameElements.TileView;

import org.jetbrains.annotations.NotNull;

public class AddMove{

    private final HUD.Control start;
    private final Tile destination;
    Division addingDivision;

    AddMove(@NotNull HUD.Control start, @NotNull Tile destination){
        this.start = start;
        this.destination = destination;
        Division division = start.getOneDivision();
        switch(division.getType()){
            case ARMORED:
                addingDivision = ((ArmoredDivision) division);
                break;
            case INFANTRY:
                addingDivision = ((InfantryDivision) division);
                break;
            case ARTILLERY:
                addingDivision = ((ArtilleryDivision) division);
                break;
        }
    }

    AddMove(@NotNull HUD.Control start, @NotNull TileView destination){
        this(start, destination.getTile());
    }


    public Tile getDestination(){
        return destination;
    }

    public HUD.Control getStart(){
        return start;
    }

    public Division getAddingDivision(){
        return addingDivision;
    }


    public Simplified simplify(){
        return new Simplified(start.type, destination.getCoordinate());
    }

    public static class Simplified{
        Type type;
        int destination;
        Simplified(Type type, int destination){
            this.type = type;
            this.destination = destination;
        }

        public AddMove returnToFullState(HUD hud, Board board){
            return new AddMove(hud.getControlByTypeAnPlayer(Player.ROLE_ENEMY, type),
                    board.getTileByCoordinate(destination));
        }
    }


}
