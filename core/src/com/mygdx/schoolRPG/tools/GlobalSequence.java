package com.mygdx.schoolRPG.tools;


import java.util.Iterator;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public class GlobalSequence {
	String sName;
	ThreadTexture[] bImages = null;
	
	public GlobalSequence(String sName) {
		this.sName = sName;
		importSequence(sName);
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
	
	public Texture getFrame(int i) {
		if (bImages == null) {
			return null;
		}
		if (i < 0 || i >= bImages.length) {
			return null;
		}
		return bImages[i].getTexture();
	}
	
	public String getName() {
		return sName;
	}

	public void setSequence() {
		for (ThreadTexture t:bImages) {
			t.setTexture();
		}
	}
	
	private void importSequence (String sName) {
        FileHandle f = Gdx.files.internal("data/" + sName);
		if (!f.exists() || !f.isDirectory()) {
			return;
		}
		TreeMap<String, FileHandle> tm = new TreeMap<String, FileHandle>();
		for (FileHandle file : f.list()) {
			if (file.isDirectory()) {
				continue;
			}
			tm.put(file.name(), file);
		}
		Iterator<String> i = tm.keySet().iterator();
		bImages = new ThreadTexture[tm.size()];
		int c = 0;
		while (i.hasNext()) {
				String key = i.next();
				FileHandle file = tm.get(key);
				bImages[c++] = new ThreadTexture(file, true);
		} 
	}
}
