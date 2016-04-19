package com.mygdx.schoolRPG.tools;


import java.util.Iterator;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GlobalSequence {
	String sName;
    Texture texture;
	TextureRegion[] bImages = null;
	
	public GlobalSequence(AssetManager assets, String sName, int framesCount) {
		this.sName = sName;
		importSequence(assets, sName, framesCount);
	}
	
	public int getLength() {
		if (bImages == null) {
			return 0;
		}
		return bImages.length;
	}
	
	public int getWidth() {
		if (bImages == null) {
			return 0;
		}
		return bImages[0].getTexture().getWidth();
	}
	
	public int getHeight() {
		if (bImages == null) {
			return 0;
		}
		return bImages[0].getTexture().getHeight();
	}
	
	public TextureRegion getFrame(int i) {
		if (bImages == null) {
			return null;
		}
		if (i < 0 || i >= bImages.length) {
			return null;
		}
		return bImages[i];
	}
	
	public String getName() {
		return sName;
	}

	/*public void setSequence() {
		for (TextureRegion t:bImages) {
			t.setTexture();
		}
	}*/
	
	private void importSequence (AssetManager assets, String sName, int count) {
		texture = assets.get(sName);
        int imgCount = count;//(int)Math.ceil(texture.getWidth()/texture.getHeight());
		bImages = new TextureRegion[imgCount];
		for (int i =0; i < imgCount; ++i) {
			int width = texture.getWidth()/imgCount;
            bImages[i] = new TextureRegion(texture, width*i, 0, width, texture.getHeight());
        }
	}
}
