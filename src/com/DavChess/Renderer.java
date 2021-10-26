package com.DavChess;

import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.List;

public class Renderer {

    public static class RenderJob {
        public Image image;
        public Vector2 position;
        public float scale;
        public Animation animation;

        public RenderJob(Image image, Vector2 position, float scale, Animation animation) {
            this.image = image;
            this.position = position;
            this.scale = scale;
            this.animation = animation;
        }
    }
    private static List<RenderJob> renderJobs = new ArrayList<>();

    public void RenderJobs(){
        for (RenderJob r:
                renderJobs) {
            r.animation.Animate(r.image,r.position, r.scale);
        }
    }

    public void addRenderJob(RenderJob jb){
        renderJobs.add(jb);
    }
}
