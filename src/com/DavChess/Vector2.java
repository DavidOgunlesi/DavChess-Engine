package com.DavChess;

public class Vector2 {
    public float x,y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 add(Vector2 v){
        return new Vector2(x+v.x,y+v.y);
    }

    public Vector2 add(Vector2Int v){
        return new Vector2(x+v.x,y+v.y);
    }

    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
