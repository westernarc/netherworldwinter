package com.gdxuser.util;

import com.badlogic.gdx.math.Vector3;

public class Cube extends MeshHelper {

	public Cube(Vector3 sz) {
		super("data/3d/cube.obj");
		
		//TODO - add scale for rendering
		size = sz;
		Log.out("cube bounds: " + mesh.calculateBoundingBox());

		// Texture texture = new
		// Texture(Gdx.files.internal("data/badlogic.jpg"), true);
		// texture.setFilter(TextureFilter.MipMap, TextureFilter.Linear);
	}
	
	public Cube() {
		super("data/3d/cube.obj");
	}


}
