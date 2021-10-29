package com.DavChess;

import com.DavChess.Exceptions.OutOfBoundsException;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {

    public class Move{
        public enum MoveType{
            none,
            move,
            capture
        }
        public MoveType moveType;
        public Board.BoardUnit boardUnit;

        public Move(MoveType moveType, Board.BoardUnit boardUnit) {
            this.moveType = moveType;
            this.boardUnit = boardUnit;
        }
    }
    /**
     * Returns a list of valid movement positions given a boardstate
     * @param p piece to move
     * @param b current board State
     * @return
     */
    public List<Move> GenerateMove(Piece p, Board b) throws OutOfBoundsException {
        List<Move> list = new ArrayList<>();
        switch (p.getType()){
            case Pawn:
                PawnMove(list,p,b);
                break;
            case Queen:
                SlidingMove(true, true,true,list,p,b);
                break;
            case King:
                SlidingMove(true, true,false,list,p,b);
                break;
            case Rook:
                SlidingMove(true,false,true,list,p,b);
                break;
            case Bishop:
                SlidingMove(false,true,true,list,p,b);
                break;
            case Knight:
                KnightMove(list,p,b);
                break;
        }
        return list;
    }

    public Board.BoardUnit GetBoardUnit(Vector2Int pos, Piece p, Board b) throws OutOfBoundsException {
        return b.getBoardUnit(pos);
    }

    public Piece PieceAt(Vector2Int pos,Piece p, Board b) throws OutOfBoundsException {
        return  GetBoardUnit(pos, p, b).currentPiece;
    }

    public boolean isEnemyPiece(Piece myPiece, Piece theirPiece){
        if (myPiece.getRaceSign() != theirPiece.getRaceSign()) {
            return true;
        }
        return false;
    }

    public boolean TryAddCapture(Vector2Int pos, List<Move> list, Piece p, Board b) throws OutOfBoundsException {
        if (b.outofBounds(pos)){
            return false;
        }
        Piece other;
        if ((other = PieceAt(pos, p, b)) != null) {
            if (isEnemyPiece(p,other)) {
                list.add(new Move(Move.MoveType.capture, GetBoardUnit(pos, p, b)));
                return true;
            }
        }
        return false;
    }

    public void AddMove(Vector2Int pos, List<Move> list, Piece p, Board b) throws OutOfBoundsException {
        if (PieceAt(pos,p,b) == null) {
            list.add(new Move(Move.MoveType.move, GetBoardUnit(pos, p, b)));
        }
    }

    public void PawnMove(List<Move> list, Piece p, Board b) throws OutOfBoundsException{
        //TODO: En passant https://en.wikipedia.org/wiki/En_passant
        Vector2Int piecePos = p.getBoardPosition();
        for (int i = 1; i <= 2; i++) {
            //Allow pawn to move forward
            Vector2Int newPos = piecePos.add(new Vector2Int(0,i*-p.getRaceSign()));
            if ( PieceAt(newPos,p,b) == null) {
                AddMove(newPos,list,p,b);
            }
            //Capture diagonal pieces
            TryAddCapture(piecePos.add(new Vector2Int(-1,-p.getRaceSign())),list,p,b);
            TryAddCapture(piecePos.add(new Vector2Int(1,-p.getRaceSign())),list,p,b);
            //stop pawn from moving 2 spaces if not on first move
            if (!p.isFirstMove()){
                break;
            }
        }
    }

    public void KnightMove(List<Move> list, Piece p, Board b) throws OutOfBoundsException{
        Vector2Int piecePos = p.getBoardPosition();
        int i1 ,i2;
        for (int i = 0; i < 2; i++) {
            //swap between i1 and i2 to mask movements
            i1 = i;
            i2 = 1-i;
            for (int ySign = -1; ySign < 2; ySign++) {
                for (int xSign = -1; xSign < 2; xSign++) {
                    if (xSign == 0 || ySign == 0) {
                        continue;
                    }
                    Vector2Int newPos = piecePos.add(new Vector2Int(xSign * 2 * i1, ySign * 2 * i2)).add(new Vector2Int(xSign * i2, ySign * i1));
                    //if out of bounds
                    if (b.outofBounds(newPos)) {
                        continue;
                    }
                    //if we hit a piece try and add capture else add move
                    if (!TryAddCapture(newPos, list, p, b)) {
                        AddMove(newPos,list,p,b);
                    }
                }
            }
        }
    }

    public void SlidingMove(boolean straights, boolean diagonals, boolean fullRange,List<Move> list, Piece p, Board b) throws OutOfBoundsException{
        Vector2Int pos = p.getBoardPosition();
        int range = 1;
        if (fullRange){
            range = 1000;
        }
        Vector2Int[] directions = new Vector2Int[]{
                new Vector2Int(0,1),
                new Vector2Int(1,1),
                new Vector2Int(1,0),
                new Vector2Int(1,-1),
                new Vector2Int(0,-1),
                new Vector2Int(-1,-1),
                new Vector2Int(-1,0),
                new Vector2Int(-1,1),
        };
        //Loop through all directions
        for (int dir = 0; dir < 8; dir++) {

            Vector2Int direction = directions[dir];
            //skip diagonals
            if (!diagonals && dir % 2 != 0) {
                continue;
            }
            if (!straights  && dir % 2 == 0) {
                continue;
            }
            Vector2Int currPos = pos;
            //loop along direction till we reach end of board
            for (int i = 0; i < range; i++) {
                currPos = currPos.add(direction);
                //if out of bounds
                if (b.outofBounds(currPos)){
                    break;
                }
                //if we hit a piece try and add capture then break
                if (PieceAt(currPos, p, b) != null) {
                        TryAddCapture(currPos, list, p, b);
                    break;
                }
                AddMove(currPos,list,p,b);
            }
        }
    }

}
