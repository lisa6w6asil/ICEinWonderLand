package com.example.iceinwonderland.data;

import android.widget.ImageView;

public class IceInfo {
    private int positionX;
    private int positionY;
    private int imageResId;
    private ImageView imageView;

    public IceInfo(int positionX, int positionY, int imageResId, ImageView imageView) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.imageResId = imageResId;
        this.imageView = imageView;
    }

    public  int getPositionX(){
        return positionX;
    }
    public void setPositionX(int positionX){
        this.positionX = positionX;
    }

    public int getPositionY(){
        return positionY;
    }
    public void setPositionY(int positionY){
        this.positionY = positionY;
    }

    public int getImageResId(){
        return imageResId;
    }
    public void setImageResId(int imageResId){
        this.imageResId = imageResId;
    }

    public ImageView getImageView(){
        return imageView;
    }
    public void setImageView(ImageView imageView){
        this.imageView = imageView;
    }

}
