package com.example.iceinwonderland.ui.block;

// ブロックの種類
public enum AreaType {
    FREE(0), WALL(1), GOAL(2), CHARACTER(3), BLOCK(4), ICE(5);

    private final int value;

    AreaType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
