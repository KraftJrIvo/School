package com.mygdx.schoolRPG;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.schoolRPG.menus.*;
import com.mygdx.schoolRPG.tools.LoadingScreen;

import java.util.ArrayList;

public class SchoolRPG extends ApplicationAdapter {
	SpriteBatch batch;
	boolean android;
	Texture img;
	AssetManager assets;
	ShapeRenderer renderer;
	ArrayList<Menu> menus;
	LoadingScreen loadingScreen;
	int curMenuId = 0;

	public SchoolRPG(boolean android) {
		this.android = android;
	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		assets = new AssetManager();
		batch = new SpriteBatch();
		renderer = new ShapeRenderer();
		menus = new ArrayList<Menu>();
		menus.add(new MainMenu(0, android));
		//menus.add(new GameMenu(0));
		menus.get(0).load(assets);
		loadingScreen = new LoadingScreen();
	}

	@Override
	public void render () {
		if (menus.get(curMenuId).nextMenu != curMenuId) {
			changeMenu(menus.get(curMenuId).nextMenu, menus.get(curMenuId).nextMenuSetting);
		}
		if (assets.update()) {
			menus.get(curMenuId).initialiseResources();
			if (menus.get(curMenuId).initialised) {
				menus.get(curMenuId).draw(batch, renderer);
			}
		} else {
			loadingScreen.draw(batch);
		}
	}

	private int findId(int id) {
		for (int i=0; i<menus.size(); ++i) {
			if (menus.get(i).ID == id) {
				return i;
			}
		} return 0;
	}

	private void changeMenu(int newId, int newSetting) {
		menus.get(curMenuId).nextMenu = curMenuId;
		if (newId == 0) {
			curMenuId = 0;
			return;
		}
		if (findId(newId) > 0) {
			curMenuId = findId(newId);
		} else {
			if (newId == 1) {
				menus.add(new GameMenu(menus.size(), android));
			} else if (newId == 2) {
				menus.add(new OptionsMenu(menus.size(), android));
			} else if (newId == 3) {
				menus.add(new CreditsMenu(menus.size(), android));
			}
			menus.get(menus.size()-1).load(assets);
			curMenuId = menus.size()-1;
		}
		//System.out.println(menus.get(curMenuId).ID);
		if (newId == 1) {
			menus.get(curMenuId).changeSetting(newSetting);
		}
		//System.out.println(menus.get(curMenuId).nextMenu);

	}
}
