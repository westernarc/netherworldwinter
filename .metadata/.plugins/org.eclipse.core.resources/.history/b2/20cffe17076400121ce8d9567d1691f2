package com.westernarc.yuurei.graphics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.utils.Disposable;

public class Model implements Disposable {
	Mesh mesh;
	public Model() {
	}
	
	public void rotate(float rx, float ry, float rz, float rw) {
		mesh.transform(new Matrix4(new Quaternion(rx,ry,rz,rw)));
	}
	@Override
	public void dispose() {
		mesh.dispose();
	}
	public void render() {
		
	}
	
}
