package com.example.iceinwonderland.ui;

public enum StageInfo {
   //yuki
    Snowmountain(1),
   //ie
    House(2),
   //ki
    Tree(3),
   //shiro
    Castle(4);

    private int id;

    StageInfo(int id){
        this.id = id;
    }

    public  int getId(){
        return id;
    }
}
