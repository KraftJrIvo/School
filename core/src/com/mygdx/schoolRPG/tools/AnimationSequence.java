package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationSequence {
	int fps;
	boolean looping;
	Coords pos;
	float scale = 1.0f;
	GlobalSequence gs = null;
	public int currentFrame=0;
	int seed;
	long startTime;
	long animationTime;
	public String path;
    int firstFrame;
	
	public AnimationSequence(AssetManager assets, String sName, int fps, boolean looping, int framesCount) {
		path = sName;
		this.fps = fps;
		this.looping = looping;
		pos = new Coords();
		gs = new GlobalSequence(assets, sName, framesCount);
		seed = (int)Math.floor(Math.random()*gs.getLength());
	}

	public void reseed() {
		seed = (int)Math.floor(Math.random()*gs.getLength());
	}

    public void changeMode(int fps, boolean looping) {
        this.fps = fps;
        this.looping = looping;
        //start(currentFrame);
    }
		
	public AnimationSequence(GlobalSequence gs, int fps, boolean looping) {
		this.fps = fps;
		this.looping = looping;
		pos = new Coords();
		this.gs = gs;
		seed = (int)Math.floor(Math.random()*gs.getLength());
	}

	public void draw(SpriteBatch sb, int x, int y, int width, int height) {
        TextureRegion temp = getCurrentFrame(false);
		sb.draw(temp, x, y, width, height);
	}

    public void draw(SpriteBatch sb, float x, float y, int fps, boolean flip) {
        changeMode(fps, looping);
        TextureRegion temp = getCurrentFrame(false);
        temp.flip(flip, false);
        sb.draw(temp, x, y, gs.getWidth()/gs.bImages.length, gs.getHeight());
        temp.flip(flip, false);
    }

    public void drawReversed(SpriteBatch sb, float x, float y, int fps, boolean flip) {
        changeMode(fps, looping);
        TextureRegion temp = getCurrentFrame(true);
        temp.flip(flip, false);
        sb.draw(temp, x, y, gs.getWidth()/gs.bImages.length, gs.getHeight());
        temp.flip(flip, false);
    }
	
	/*public void setSequence() {
		gs.setSequence();
	}*/
	
	public TextureRegion getCurrentFrame(boolean reversed) {

		if (gs == null || (gs.getLength() < 1)) {
			return null;
		}	
		
		long delta = System.currentTimeMillis() - startTime;
		
		if (!looping && delta >= animationTime) {
			currentFrame = (gs.getLength() - 1 + seed)%(gs.getLength());
		} else {
			double doubleDelta = (double)delta/1000.0;
			currentFrame = (int)((long)(doubleDelta * (double)fps) % gs.getLength() + seed)%(gs.getLength());
		}
        if (reversed) {
            currentFrame = (gs.getLength() - currentFrame-1 + seed)%(gs.getLength());
            //if (currentFrame)
        }
		return gs.getFrame(currentFrame);
	}



	public TextureRegion getFirstFrame() {
		return gs.getFrame(0);
	}

	public void start(int firstFrame) {
		startTime = System.currentTimeMillis();
		animationTime = (1000 / fps) * gs.getLength();
		firstFrame = firstFrame;
	}
}
