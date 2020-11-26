package com.kypcop.chroniclesofwwii.game.screen.gameElements;

import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.kypcop.chroniclesofwwii.game.logic.Engine;
import com.kypcop.chroniclesofwwii.game.logic.board.Board;
import com.kypcop.chroniclesofwwii.game.logic.board.MotionMove;
import com.kypcop.chroniclesofwwii.game.logic.divisions.Division;
import com.kypcop.chroniclesofwwii.game.screen.activities.GameActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BoardLayout {

    private final Board board;
    private final TableLayout tableLayout;
    private final TileView[][] tileViews;
    private final GameActivity gameActivity;
    private final List<TileView> shownTileViews = new ArrayList<>();
    private boolean movesShown = false;

    public BoardLayout(@NotNull TableLayout layout, @NotNull GameActivity gameActivity, @NotNull Board board){
        this.tableLayout = layout;
        this.gameActivity = gameActivity;
        this.board = board;
        tileViews = new TileView[board.size()][board.size()];
    }

    public void setClickListeners(View.OnClickListener listener){
        for (TileView[] tileView : tileViews) {
            for (TileView view : tileView) {
                view.getImageView().setOnClickListener(listener);
            }
        }
    }

    public TileView[][] getTileViews(){
        return tileViews;
    }

    public void initializeBoardLayout(boolean invert){
        int width = Engine.getScreenWidth() / board.size();
        TableRow[] tableRows = new TableRow[board.size()];
        TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, width);

        if(invert){
            for(int row = tileViews.length - 1; row >= 0; row--) {
                tableRows[row] = new TableRow(gameActivity);
                tableRows[row].setLayoutParams(rowParams);
                tableLayout.addView(tableRows[row]);
                for (int column = tileViews.length - 1; column >= 0; column--) {
                    tileViews[row][column] = new TileView(gameActivity, board.getTileByCoordinate(row, column), width);
                    tableRows[row].addView(tileViews[row][column].getImageView());
                }
            }
        } else{
            for(int row = 0; row < tileViews.length; row++) {
                tableRows[row] = new TableRow(gameActivity);
                tableRows[row].setLayoutParams(rowParams);
                tableLayout.addView(tableRows[row]);
                for (int column = 0; column < tileViews.length; column++) {
                    tileViews[row][column] = new TileView(gameActivity, board.getTileByCoordinate(row, column), width);
                    tableRows[row].addView(tileViews[row][column].getImageView());
                }
            }
        }
    }

    public void showLegalMoves(List<MotionMove> legalMotionMoves) {
        for (MotionMove move : legalMotionMoves) {
            TileView tileView = move.getDestinationTilePlate(this);
            tileView.showIsLegal();
            shownTileViews.add(tileView);
        }
        movesShown = true;
    }

    public void showLegalMoves(Division division){
        showLegalMoves(division.calculateLegalMoves(board));
    }

    public void showLegalMoves(int row){
        for(TileView tileView : tileViews[row]){
            shownTileViews.add(tileView);
            tileView.showIsLegal();
            movesShown = true;
        }
    }

    public void hideLegalMoves(){
        for(TileView tileView: shownTileViews){
            tileView.hideIsLegal();
        }
        shownTileViews.clear();
        movesShown = false;
    }

    public boolean isMovesShown(){
        return movesShown;
    }

    public TileView getTileViewByCoordinate(int coordinate) {
        return getTileViewByCoordinate(coordinate / 10, coordinate % 10);
    }

    public TileView getTileViewByCoordinate(int i, int j){
        return tileViews[i][j];
    }

    public Board getBoard() {
        return board;
    }

    public void setEnabled(boolean b) {
        for(TileView[] tileViewArray : tileViews){
            for(TileView tileView : tileViewArray){
                tileView.getImageView().setEnabled(b);
            }
        }
    }
}
