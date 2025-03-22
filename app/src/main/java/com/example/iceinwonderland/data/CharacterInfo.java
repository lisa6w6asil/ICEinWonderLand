package com.example.iceinwonderland.data;

import android.media.Image;
import android.widget.ImageView;

import com.example.iceinwonderland.ui.block.Direction;

/**
 * キャラクター情報
 */
public class CharacterInfo {
    private int positionX;
    private int positionY;
    private int imageResId;
    private ImageView imageView;
    private Direction direction;

    public CharacterInfo(int positionX, int positionY, int imageResId, ImageView imageView, Direction direction) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.imageResId = imageResId;
        this.imageView = imageView;
        this.direction = direction;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }


    public int getPositionX() {
       return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getImageResId() {
        return imageResId;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Direction getDirection() {
        return direction;
    }
}
