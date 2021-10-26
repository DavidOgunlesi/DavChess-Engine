package com.DavChess;

public class Vector2Int {
    public int x,y;

    public Vector2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2Int add(Vector2Int v){
        return new Vector2Int(x+v.x,y+v.y);
    }

    public static Vector2Int toInt(Vector2 v){
        return new Vector2Int((int)v.x,(int)v.y);
    }

    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
