package com.DavChess;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import java.util.Random;

public class SoundEffectManager {

    public static Sound soundtrack;
    public static Sound chessPlace, chessPlace2, chessPick, badBeep,takePiece,takePiece2;
    public static float FXVolume = 1f ,MXVolume = 0.5f;

    public SoundEffectManager() {
        try {
            chessPlace=new Sound("resources/sounds/FX/chessPlace.ogg");
            chessPlace2=new Sound("resources/sounds/FX/chessPlace2.ogg");
            chessPick=new Sound("resources/sounds/FX/chessPick.ogg");
            badBeep=new Sound("resources/sounds/FX/badBeep.ogg");
            takePiece=new Sound("resources/sounds/FX/takePiece.ogg");
            takePiece2=new Sound("resources/sounds/FX/takePiece2.ogg");
            soundtrack=new Sound("resources/sounds/soundtrack/Lonelyhood.ogg");
        }catch (SlickException e){
            Main.print("\u001B[31m" + "Could not load sounds. Reason:" + "\u001B[0m" + "\n" );
        }
        PlaySoundTrack();
    }

    public void playSound(Sound sound){
        if (sound==null){
            return;
        }
        sound.play(1f, FXVolume);
        sound.play();
    }

    public void playRandomSound(Sound... sounds){
        Sound sound = sounds[new Random().nextInt(sounds.length)];
        if (sound==null){
            return;
        }
        playSound(sound);
    }

    private void PlaySoundTrack(){
        if (soundtrack.playing() == false) {
            soundtrack.play(1f, MXVolume);
            soundtrack.play();
        }
    }
}
