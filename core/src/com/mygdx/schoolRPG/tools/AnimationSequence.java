package com.mygdx.schoolRPG.tools;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AnimationSequence {
	int fps;
	boolean looping;
	Coords pos;
	float scale = 1.0f;
	GlobalSequence gs = null;
	private int currentFrame = 0;
	long startTime;
	long animationTime;
	
	public AnimationSequence(String sName, int fps, boolean looping) {
		this.fps = fps;
		this.looping = looping;
		pos = new Coords();
		gs = new GlobalSequence(sName);
	}
		
	public AnimationSequence(GlobalSequence gs, int fps, boolean looping) {
		this.fps = fps;
		this.looping = looping;
		pos = new Coords();
		this.gs = gs;
	}

	public void draw(SpriteBatch sb, int x, int y, int width, int height) {
		sb.draw(getCurrentFrame(), x, y, width, height);
	}
	
	public void setSequence() {
		gs.setSequence();
	}
	
	public Texture getCurrentFrame() {

		if (gs == null || (gs.getLength() < 1)) {
			return null;
		}	
		
		long delta = System.currentTimeMillis() - startTime;
		
		if (!looping && delta >= animationTime) {
			currentFrame = gs.getLength() - 1;
		} else {
			double doubleDelta = (double)delta/1000.0;
			currentFrame = (int)((long)(doubleDelta * (double)fps) % gs.getLength());
		}
		return gs.getFrame(currentFrame);
	}

	public Texture getFirstFrame() {
		return gs.getFrame(0);
	}

	public void start() {
		startTime = System.currentTimeMillis();
		animationTime = (1000 / fps) * gs.getLength();
		
	}
}
