package com.example.iceinwonderland.data;

import android.widget.ImageView;

import com.example.iceinwonderland.ui.block.AreaType;

public class AreaInfo {
    private AreaType areaType;
    private ImageView imageView;
    private int posX;
    private int posY;

    public boolean isMoveable(){
        return areaType == AreaType.FREE;
    }

    public boolean isIceMoveable(){
        return areaType == AreaType.FREE || areaType == AreaType.GOAL;
    }
    public AreaInfo(int posX, int posY, AreaType areaType, ImageView imageView){
        this.posX = posX;
        this.posY = posY;
        this.areaType = areaType;
        this.imageView = imageView;
    }

    public int getPosX() {
        return posX;
    }
    public void setPosX(){
        this.posX = posX;
    }

    public int getPosY(){
        return posY;
    }

    public void setPosY(){
        this.posY = posY;
    }

    public AreaType getAreaType(){
        return areaType;
    }

    public void setAreaType(AreaType areaType){
        this.areaType = areaType;
    }

    public ImageView getImageView(){
        return imageView;
    }

    public void setImageView(ImageView imageView){
        this.imageView = imageView;
    }

}
