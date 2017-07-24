package com.mygdx.schoolRPG.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.schoolRPG.SchoolRPG;

import java.io.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		int soundVolume = 50;
		int musicVolume = 50;
		int currentLanguage = 0;
		config.fullscreen = true;
		File f1 = new File("../../default.cfg");
		File f2 = new File("../../current.cfg");
		if(!f1.exists()) {
			PrintWriter writer = null;
			try {
				writer = new PrintWriter("../../default.cfg", "UTF-8");
				writer.println("fullscreen");
				writer.println("true");
				writer.println("sound");
				writer.println("50");
				writer.println("music");
				writer.println("50");
                writer.println("language");
                writer.println("0");
                writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			BufferedReader in = null;
			try {
                if(!f2.exists()) {
                    in = new BufferedReader(new FileReader("../../default.cfg"));
                } else {
                    in = new BufferedReader(new FileReader("../../current.cfg"));
                }
				in.readLine();
				String line = in.readLine();
				config.fullscreen = Boolean.parseBoolean(line);
				in.readLine();
				line = in.readLine();
				soundVolume = Integer.parseInt(line);
				in.readLine();
				line = in.readLine();
				musicVolume = Integer.parseInt(line);
                in.readLine();
                line = in.readLine();
                currentLanguage = Integer.parseInt(line);
                in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new SchoolRPG(false, config.fullscreen, soundVolume, musicVolume, currentLanguage), config);
	}
}
