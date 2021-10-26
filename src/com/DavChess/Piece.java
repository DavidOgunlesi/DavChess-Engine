package com.DavChess;

import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import java.util.List;
import java.util.Random;

public class Piece {
    private static Piece currentlyGrabbedPiece;
    private Image image;
    private Vector2 position;
    private Vector2Int boardPosition;
    private Type type;
    private Race race;
    private boolean isGrabbed;
    private float scale = 0.17f;
    private Board.BoardUnit currentBoardUnit;
    private boolean firstMove = true;

    public Image getImage() {
        return image;
    }

    public Vector2Int getBoardPosition() {
        return boardPosition;
    }
    public Vector2 getPos() {
        return position;
    }

    public Type getType() {
        return type;
    }
    public boolean isFirstMove() {return firstMove;}

    public int getRaceSign(){
        switch (race){
            case B -> {return -1;}
            case W -> {return 1;}
        }
        return 1;
    }

    public enum Type{
        King,
        Queen,
        Knight,
        Bishop,
        Rook,
        Pawn

    }
    public enum Race{
        B,
        W
    }
    public Piece(Type type, Race race) {
        SetImage("resources/sprites/pieces/"+race.toString().toLowerCase()+"_"+ type.toString().toLowerCase()+".png");
        this.type = type;
        this.race = race;

    }

    public void Update(){
        SetPosition();
        CheckForGrabbed();
    }

    /**
     * Set the piece's image  so it can be displayed on screen
     * @param resourcePath
     */
    private void SetImage(String resourcePath){
        try {
            image = new Image(resourcePath);
        }catch(SlickException e){
            //Do nothing but cry
        }
    }

    private void SetPosition(){
        Vector2Int wpos = Main.getBoard().getBoardUnit(boardPosition).worldPosition;
        float centreOffsetX = (image.getWidth() / 2) * scale;
        float centreOffsetY = (image.getHeight() / 2) * scale;
        if (!isGrabbed) {
            position = new Vector2(wpos.x - centreOffsetX, wpos.y - centreOffsetY);
        }else {
            position = new Vector2(Main.mousePos.x - centreOffsetX, Main.mousePos.y - centreOffsetY);
        }
    }

    private List<Board.BoardUnit> GetValidMoves(){
        return Main.moveGenerator.GenerateMove(this,Main.getBoard());
    }


    private void HighlightValidMoves(){
        if (GetValidMoves() == null) {
            return;
        }
        for (Board.BoardUnit b:
                GetValidMoves()) {
            b.setHighlighted(true);
        }
    }

    private void ClearValidMoveHighlight(){
        if (GetValidMoves() == null) {
            return;
        }
        for (Board.BoardUnit b:
                GetValidMoves()) {
            b.setHighlighted(false);
        }
    }

    private void CheckForGrabbed(){
        if (!isGrabbed && inBoundingBox(Main.mousePos) && Main.input.isMousePressed(Main.input.MOUSE_LEFT_BUTTON)
        &&  currentlyGrabbedPiece == null){
            Grab();
        }
        if (Main.input.isKeyDown(Input.KEY_ESCAPE)) {
            UnGrab();
        }
        if (isGrabbed && Main.input.isMousePressed(Main.input.MOUSE_LEFT_BUTTON)) {
            UnGrab();
            Board.BoardUnit closestBoardUnit = returnClosest();
            MovePiece(closestBoardUnit.boardPosition);
        }
    }

    private void Grab(){
        Main.soundEffectManager.playSound(SoundEffectManager.chessPick);
        isGrabbed = true;
        currentlyGrabbedPiece = this;
        HighlightValidMoves();
    }

    private void UnGrab(){
        isGrabbed = false;
        currentlyGrabbedPiece = null;
        ClearValidMoveHighlight();
    }

    private Board.BoardUnit returnClosest(){
        double currClosest = Double.POSITIVE_INFINITY;
        Board.BoardUnit closestBU = null;
        for (Board.BoardUnit u:
                Main.getBoard().getBoardUnits()) {
            double dist = Distance(Vector2Int.toInt(getPositionCentre()),u.worldPosition);
            if (dist < currClosest) {
                currClosest = dist;
                closestBU = u;
            }
        }
        return closestBU;
    }

    private Vector2 getPositionCentre(){
       return position.add(new Vector2((image.getWidth() / 2) * scale, (image.getHeight() / 2) * scale));
    }

    private double Distance(Vector2Int v1, Vector2Int v2){
        return Math.sqrt(Math.pow(v1.x-v2.x,2)+Math.pow(v1.y-v2.y,2));
    }

    public boolean inBoundingBox(Vector2 pos){
        Vector2 TopRight = getPositionCentre().add(new Vector2((image.getWidth() / 2) * scale, (image.getHeight() / 2) * scale));// (image.getWidth() / 2) * scale;
        Vector2 BotLeft =  getPositionCentre().add(new Vector2(-(image.getWidth() / 2) * scale, -(image.getHeight() / 2) * scale));
        if (pos.x > BotLeft.x && pos.y > BotLeft.y && pos.x<TopRight.x && pos.y < TopRight.y) {
            return true;
        }
        return false;
    }

    /**
     * Initialises piece position at start
     * @param position
     */
    public void InitPiece(Vector2Int position){
            this.boardPosition = position;
            currentBoardUnit = Main.getBoard().getBoardUnit(position);
    }

    private boolean IsValidMove(Board.BoardUnit newBoardUnit){
        if (GetValidMoves().contains(newBoardUnit)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Move the piece to a new position
     * @param newPosition
     */
    public void MovePiece(Vector2Int newPosition){
        Board.BoardUnit newBoardUnit = Main.getBoard().getBoardUnit(newPosition);
        Board.BoardUnit oldBoardUnit = currentBoardUnit;
        boolean spaceEmpty = Main.getBoard().getBoardUnit(newPosition).currentPiece==null;

        boolean enemyPiece = false;
        Piece enemy = Main.getBoard().getBoardUnit(newPosition).currentPiece;
        if (enemy != null) {
            enemyPiece = !Main.getBoard().getBoardUnit(newPosition).currentPiece.race.equals(race);
        }

        if ((enemyPiece || spaceEmpty) && IsValidMove(newBoardUnit)) {
            this.boardPosition = newPosition;
            oldBoardUnit.currentPiece = null;
            newBoardUnit.currentPiece = this;
            currentBoardUnit = newBoardUnit;
            if (firstMove) {
                firstMove = false;
            }
            if (enemyPiece) {
                Main.getBoard().removePiece(enemy);
                //Main.AnimateCapture(enemy.getImage(),enemy.getPositionCentre(), 0.17f);
                Main.soundEffectManager.playRandomSound(SoundEffectManager.takePiece,SoundEffectManager.takePiece2);
            }
            //play sound effect
            Main.soundEffectManager.playRandomSound(SoundEffectManager.chessPlace,SoundEffectManager.chessPlace2);
        }else {
            //invalid placement
            Main.soundEffectManager.playSound(SoundEffectManager.badBeep);
        }
    }

}
