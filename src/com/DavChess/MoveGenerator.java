package com.DavChess;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {

    /**
     * Returns a list of valid movement positions given a boardstate
     * @param p piece to move
     * @param b current board State
     * @return
     */
    public List<Board.BoardUnit> GenerateMove(Piece p, Board b){
        switch (p.getType()){
            case Pawn:
                List<Board.BoardUnit> list = new ArrayList<>();
                if (p.isFirstMove()){
                    list.add(b.getBoardUnit(p.getBoardPosition().add(new Vector2Int(0,2*-p.getRaceSign()))));
                }
                list.add(b.getBoardUnit(p.getBoardPosition().add(new Vector2Int(0,1*-p.getRaceSign()))));
                return list;
        }
        return null;
    }
}
