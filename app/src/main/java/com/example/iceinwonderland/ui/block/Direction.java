package com.example.iceinwonderland.ui.block;

public enum Direction {
    UP(180f), RIGHT(270f), DOWN(0f), LEFT(90f);

    float rotation;

    Direction(float rotation) {
        this.rotation = rotation;
    }

    public float getRotation(){
        return rotation;
    }
}
