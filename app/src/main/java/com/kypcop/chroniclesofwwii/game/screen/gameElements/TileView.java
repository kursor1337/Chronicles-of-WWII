package com.kypcop.chroniclesofwwii.game.screen.gameElements;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableRow;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.kypcop.chroniclesofwwii.R;
import com.kypcop.chroniclesofwwii.game.logic.board.Tile;
import com.kypcop.chroniclesofwwii.game.logic.divisions.Division;

import java.util.TreeMap;

public class TileView {

    private static final int STATE_NORMAL = 507;
    private static final int STATE_ATTACKED = 720;
    private static final int STATE_LEGAL = 709;

    private boolean isFogged = true;

    private LayerDrawable layerDrawable;
    private final TreeMap<Integer, Drawable> layerMap = new TreeMap<>();
    private final Tile tile;
    private int state;
    private final Context context;
    ImageView imageView;

    TileView(Context context, Tile tile, int size){
        imageView = new ImageView(context);
        state = STATE_NORMAL;

        tile.setChangeListener(new Tile.ChangeListener() {
            @Override
            public void onDivisionChanged(Tile tile) {
                Division division = tile.getDivision();
                Log.i("TileView", "Callback from Tile");
                Log.i("TileView", "drawable resource = " + Division.getDrawableResource(division.getType()));
                addLayer(Division.getDrawableResource(division.getType()));
            }

            @Override
            public void onTileCleared(Tile tile) {
                Log.i("TileView", "Callback from Tile");
                clearTileView();
            }
        });

        this.tile = tile;
        this.context = context;
        imageView.setId(tile.getCoordinate());
        imageView.setLayoutParams(new TableRow.LayoutParams(size, size));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        setImage(R.drawable.empty);
    }

    public Division getDivision(){
        return tile.getDivision();
    }

    public boolean isOccupied(){
        return tile.isOccupied();
    }

    public void setDivision(Division division){
        if(!tile.isOccupied()) {
            tile.setDivision(division);
            Log.i("TileView", "drawable resource = " + Division.getDrawableResource(division.getType()));
            addLayer(Division.getDrawableResource(division.getType()));
        }
    }

    public void setImage(@DrawableRes int id){
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Log.i("TileView", "Setting image... " + drawable);
        layerMap.clear();
        layerMap.put(id, drawable);
        Drawable[] ds = new Drawable[0];
        ds = layerMap.values().toArray(ds);
        layerDrawable = new LayerDrawable(ds);
        imageView.setImageResource(0);
        imageView.setImageDrawable(layerDrawable);
        Log.i("TileView", "Set image " + layerDrawable + " length = " + layerDrawable.getNumberOfLayers());
    }

    public int getCoordinate(){
        return tile.getCoordinate();
    }

    public void clearTileView(){
        if(tile.isOccupied()) tile.clearTile();
        setImage(R.drawable.empty);
    }

    void hideIsLegal(){
        Log.i("TileView", "Hide is legal");
        removeLayer(R.drawable.attacked);
        removeLayer(R.drawable.legal);
        state = STATE_NORMAL;
    }

    void showIsLegal(){
        if(!tile.isOccupied()){
            addLayer(R.drawable.legal);
            state = STATE_LEGAL;
            Log.i("TileView", "Show is legal");
        } else{
            showIsAttacked();
        }
    }

    private void showIsAttacked(){
        addLayer(R.drawable.attacked);
        state = STATE_ATTACKED;
        Log.i("TileView", "Show is attacked");
    }

    public int getRow() {
        return tile.getRow();
    }

    public int getColumn() {
        return tile.getColumn();
    }

    public Tile getTile(){
        return tile;
    }

    private void addLayer(@DrawableRes int id){
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Log.i("TileView", drawable.toString());
        layerMap.put(id, drawable);
        Log.i("TileView", "addLayer");
        Drawable[] ds = new Drawable[0];
        ds = layerMap.values().toArray(ds);
        layerDrawable = new LayerDrawable(ds);
        Log.i("TileView", "Set image " + layerDrawable + " length = " + layerDrawable.getNumberOfLayers());
        imageView.setImageDrawable(layerDrawable);
    }

    private void removeLayer(@DrawableRes int id){
        Drawable d = layerMap.remove(id);
        if(d != null) d.setAlpha(0);
        else return;
        Log.i("TileView", d + "");
        Drawable[] ds = new Drawable[layerMap.size()];
        ds = layerMap.values().toArray(ds);
        layerDrawable = new LayerDrawable(ds);
    }

    public int state(){
        return state;
    }

    public boolean isFogged() {
        return isFogged;
    }

    void setFog() {
        isFogged = true;
    }

    void removeFog(){
        isFogged = false;
    }

    public ImageView getImageView() {
        return imageView;
    }
}
