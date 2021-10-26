package com.DavChess;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private BoardUnit[][] worldGrid = new BoardUnit[8][8];
    private BoardUnit[] boardState = new BoardUnit[64];
    private List<BoardUnit> boardUnits = new ArrayList<>();

    public List<BoardUnit> getBoardUnits() {
        return boardUnits;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    private List<Piece> pieces = new ArrayList<>();

    public class BoardUnit{
        Piece currentPiece = null;
        Vector2Int worldPosition;
        Vector2Int boardPosition;
        boolean highlighted;

        public Vector2Int getWorldPosition() {
            return worldPosition;
        }
        public boolean isHighlighted() {return highlighted;}

        public void setHighlighted(boolean b) {highlighted = b;}
    }

    public Board() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                BoardUnit unit = new BoardUnit();
                worldGrid[i][j] = unit;
                boardUnits.add(unit);
            }
        }
    }

    /**
     * Assign a world position to a board tile
     * @param pos the relative board pos
     * @param worldPos the absolute world pos
     */
    public void InitBoardUnit(Vector2Int pos, Vector2Int worldPos){
        BoardUnit unit =  worldGrid[pos.x][pos.y];
        unit.worldPosition = worldPos;
        unit.boardPosition = pos;
    }

    /**
     * Get a Board Unit, which is one of the grid tiles which make up the chess board
     * @param pos Relative position of board unit in chess space
     * @return Board Unit
     */
    public BoardUnit getBoardUnit(Vector2Int pos){
        return worldGrid[pos.x][pos.y];
    }

    /**
     * Place pieces on a board given a configuration string
     * @param configuration the string which holds data about chess pieces and their position
     */
    public void InitialisePieces(String configuration) {
        String[] configurationArr = configuration.split("-");
        try {
            for (String s:
                    configurationArr) {
                String coord = s.substring(0,2);
                String pieceLetter = s.substring(2,4);
                AddPiece(coord, CharacterStringToPiece(pieceLetter));
            }
        }catch (Exception e){
            //Board Config cannot be parsed
            Main.print("\u001B[31m" + "Board Config cannot be parsed. Reason:" + "\u001B[0m" + "\n" );
            e.printStackTrace();
        }

    }

    /**
     * Add a new piece to the chess board
     * @param chessCoord Coordinate of the piece using chess coords
     * @param piece The piece to place
     */
    public void AddPiece(String chessCoord, Piece piece){
        Vector2Int p = Board.ChessCoordToWorld(chessCoord);
        worldGrid[p.x][p.y].currentPiece = piece;
        piece.InitPiece(p);
        pieces.add(piece);
    }

    public void removePiece(Piece p){
        pieces.remove(p);
    }

    /**
     * Convert Chess Space coordinates to World Space
     * @param s chess coordinate string i.e. "A5"
     * @return World space coordinate
     */
    public static Vector2Int ChessCoordToWorld(String s){
        char[] arr =  s.toCharArray();
        char c = arr[0];
        int y = 8-Integer.parseInt(Character.toString(arr[1]));
        int x = "abcdefghijkmnlopqrstuvwxyz".indexOf(Character.toLowerCase(c));
        return new Vector2Int(x, y);
    }

    /**
     * Convert world space coordinates to Chess space
     * @param x x component
     * @param y y component
     * @return Chess space coordinate
     */
    public static String WorldCoordToChess(int x, int y){
        char c = "abcdefghijkmnlopqrstuvwxyz".toCharArray()[x];
        return Character.toUpperCase(c) + Integer.toString(y);
    }

    /**
     * Map chess pieces to a piece data string i.e. Nb means black Knight
     * @param piece data concerning piece e.g. Pw
     * @return Piece object
     */
    public static Piece CharacterStringToPiece(String piece){
        char c = piece.toUpperCase().charAt(0);
        char r = piece.toUpperCase().charAt(1);
        Piece.Race race;
        if (r=='B'){
            race = Piece.Race.B;
        }else  {
            race = Piece.Race.W;
        }
        switch (c) {
            case 'K':
                return new Piece(Piece.Type.King,race);
            case 'Q':
                return new Piece(Piece.Type.Queen, race);
            case 'N':
                return new Piece(Piece.Type.Knight, race);
            case 'B':
                return new Piece(Piece.Type.Bishop, race);
            case 'R':
                return new Piece(Piece.Type.Rook, race);
            case 'P':
                return new Piece(Piece.Type.Pawn, race);
            default:
                return null;
        }
    }
}
