package com.DavChess;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.DavChess.Exceptions.OutOfBoundsException;
import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class Main extends BasicGame
{
    public static final SoundEffectManager soundEffectManager = new SoundEffectManager();
    public static final MoveGenerator moveGenerator = new MoveGenerator();
    public static final Renderer renderer = new Renderer();
    public static Vector2 mousePos;
    public static Input input;
    private static int height = 720;
    private static int width = 1280;
    private static float boardScale = 0.8f;
    private static int[] offset = new int[] {1,1};
    private static Color whiteColor = new Color(120,72,57);
    private static Color blackColor = new Color(92,50,48);
    private static Board board;
    private String configuration =
            "a1Rw-b1Nw-c1Bw-d1Qw-e1Kw-f1Bw-g1Nw-h1Rw-a2Pw-b2Pw-c2Pw-d2Pw-e2Qw-f2Pw-g2Pw-h2Pw-"+
            "a8Rb-b8Nb-c8Bb-d8Qb-e8Kb-f8Bb-g8Nb-h8Rb-a7Pb-b7Pb-c7Pb-d7Pb-e7Pb-f7Pb-g7Pb-h7Pb-";

    public static Board getBoard() {
        return board;
    }

    public Main(String gamename)
    {
        super(gamename);
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        SetMousePos(gc);
        board = new Board();
        board.InitialisePieces(configuration);
    }

    @Override
    public void update(GameContainer gc, int i) throws SlickException {
        SetMousePos(gc);
        Queue<Piece> pieceUpdateQueue = new LinkedList<Piece>(board.getPieces());
        while (!pieceUpdateQueue.isEmpty()){
            pieceUpdateQueue.remove().Update();;
        }
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException
    {
        try {
            RenderBoard(g);
        } catch (OutOfBoundsException e) {
            e.printStackTrace();
        }
        DrawPieces();
        g.drawString(mousePos.toString(),000,400);
        g.setColor(Color.black);
        renderer.RenderJobs();

        /*for (Board.BoardUnit u:
                Main.getBoard().getBoardUnits()) {
           g.drawOval (u.worldPosition.x-3,u.worldPosition.y-3,3,3);
        }*/
    }

    private void SetMousePos(GameContainer gc){
        input = gc.getInput();
        int xpos = input.getMouseX();
        int ypos = input.getMouseY();
        mousePos = new Vector2(xpos,ypos);
    }

    /**
     * Renders the gridded chess board on game initialisation
     * @param g
     */
    public void RenderBoard(Graphics g) throws OutOfBoundsException {
        int h = Math.round(height*boardScale);
        boolean black = false;  
        int middleOfScreen = width/2;
        int middleOfBoard=((5) * h/8);
        int boardOffsetX = middleOfScreen-middleOfBoard;
        for (int y = 1; y <= 8; y++) {
            black = !black;
            for (int x = 1; x <= 8; x++) {
                if (black){
                    g.setColor(blackColor);
                }else {
                    g.setColor(whiteColor);
                }
                black = !black;
                //render different color if board tile is highlighted
                //g.drawRect((x * h/8)+boardOffsetX,(y * h/8),h/8,h/8);
                g.fillRect((x * h/8)+boardOffsetX,(y * h/8),h/8,h/8);

                if (board.getBoardUnit(new Vector2Int(x-1,y-1)).highlightType == MoveGenerator.Move.MoveType.move) {
                    g.setColor(Color.lightGray);
                    int size = 30;
                    g.fillOval(((x) * h/8)+(h/16)+boardOffsetX-size/2,((y) * h/8)+(h/16)-size/2,size,size);
                }
                if (board.getBoardUnit(new Vector2Int(x-1,y-1)).highlightType == MoveGenerator.Move.MoveType.capture) {
                    g.setColor(Color.lightGray);
                    g.fillRect((x * h/8)+boardOffsetX,(y * h/8),h/8,h/8);
                }

                board.InitBoardUnit(new Vector2Int(x-1,y-1),new Vector2Int(((x) * h/8)+(h/16)+boardOffsetX,((y) * h/8)+(h/16)));

            }
        }
    }


    /**
     * Renders all the pieces in the world onto the screen
     */
    public void DrawPieces(){
        for (Piece piece: board.getPieces()) {
            if (piece.getPos() != null) {
                piece.getImage().draw(piece.getPos().x, piece.getPos().y, 0.17f);
            }
        }
    }

    public static void AnimateCapture(Image image, Vector2 position, float scale){
        Renderer.RenderJob renderJob = new Renderer.RenderJob(image, position,scale,(i,p,s)->{
            Vector2 currPos = p;
            Random r = new Random();
           // currPos.add(new Vector2(r.nextInt()*5,r.nextInt()*5))
            i.draw(p.x,p.y,s);
            i.rotate(i.getRotation()+0.01f);
            i.setCenterOfRotation(p.x,p.y);
        });
        renderer.addRenderJob(renderJob);
    }



    /**
     * Print helper function
     * @param ss the messages to print
     */
    public static void print(Object... ss){
        StringBuilder result = new StringBuilder();
        for (Object s:
             ss) {
            result.append(s);
        }
        System.out.println(result);
    }

    public static void main(String[] args)
    {
        try
        {
            AppGameContainer appgc;
            appgc = new AppGameContainer(new Main("DavChess"));
            appgc.setDisplayMode(width, height, false);

            appgc.start();
        }
        catch (SlickException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}