package com.kypcop.chroniclesofwwii.game.Logic.Board;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.Logic.Divisions.Division;

public class Tile {
    private final int row;
    private final int column;
    private Division division = null;
    private final ImageButton button;
    private boolean occupied = false;
    private Context context;
    private int imageId;


    Tile(int row, int column, Context context, int size){
        this.context = context;
        int buttonId;
        this.row = row;
        this.column = column;
        button = new ImageButton(context);
        TableRow.LayoutParams params = new TableRow.LayoutParams(size, size);
        button.setLayoutParams(params);
        buttonId = Integer.parseInt(row + Integer.toString(column));
        button.setId(buttonId);
        setImage(R.drawable.empty);
        button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

    }

    public Division getDivision(){
        return division;
    }

    public boolean isOccupied(){
        return occupied;
    }

    public void setDivision(Division division){
        if(!occupied) {
            this.division = division;
            occupied = true;
            setImage(division.getDrawableResource());
        }
    }

    public void clearTile(){
        division = null;
        occupied = false;
        setImage(R.drawable.empty);
    }

    void removeChosen(){
        if(division == null){
            setImage(R.drawable.empty);
        } else{
            setImage(division.getDrawableResource());
        }
    }
    void addChosen(){
        if(division == null){
            setImage(R.drawable.empty_chosen);
        } else{
            setImage(division.getChosenDrawableResource());
        }
    }


    int getRow() {
        return row;
    }

    int getColumn() {
        return column;
    }

    public View getButton() {
        return button;
    }


    public void setImage(int imageId) {
        button.setBackgroundResource(imageId);
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }
}
