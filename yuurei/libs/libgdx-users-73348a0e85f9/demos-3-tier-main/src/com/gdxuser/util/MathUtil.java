package com.gdxuser.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actors.Image;



public class MathUtil {

	// private int imgCount = 0;

	static Image addImage(String path, String name, Group g) {
		Texture tex = new Texture(Gdx.files.internal(path));
		tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		Image img = new Image(name, tex);
		img.touchable = true;
		g.addActor(img);
		return img;
	}

	static Image loadImage(String path, String name) {
		Texture tex = new Texture(Gdx.files.internal(path));
		tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		Image img = new Image(name, tex);
		img.touchable = true;
		return img;
	}

	static int randSign() {
		if (Math.random() < 0.5f)
			return 1;
		return -1;
	}

	public static String randomElem(String[] opts) {
		String str = opts[random(opts.length)];
		return str;
	}


	public static int random(int f) {
		return (int) (Math.random() * f);
	}

	public static float random(float f) {
		return (int) (Math.random() * f);
	}
	
	// util method to check files are in the right place
	public void checkFiles() {
		FileHandle dir = Gdx.files.internal("data/");
		FileHandle[] list = dir.list();
		Log.out("filecount:" + list.length);
		for (int i = 0; i < list.length; i++) {
			Log.out("f:" + list[i]);
		}
	}

}
