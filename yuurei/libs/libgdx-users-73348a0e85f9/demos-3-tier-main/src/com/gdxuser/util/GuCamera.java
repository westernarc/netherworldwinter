package com.gdxuser.util;

import com.badlogic.gdx.math.Vector3;

public interface GuCamera {
	
	public void push();
	public void pop();
	public void handleKeys();
	public void spin(float delta, float dir);
	public void spin(float delta, Vector3 dir);

}
