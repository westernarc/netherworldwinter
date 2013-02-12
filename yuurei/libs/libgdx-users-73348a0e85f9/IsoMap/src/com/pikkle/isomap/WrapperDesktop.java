package com.pikkle.isomap;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class WrapperDesktop {

	public static void main(String[] args) {
		new LwjglApplication(new IsoMap(), "IsoMap", 800, 600, false);
	}

}
