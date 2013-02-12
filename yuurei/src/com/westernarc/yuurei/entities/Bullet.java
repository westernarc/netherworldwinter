package com.westernarc.yuurei.entities;

public class Bullet extends Entity {
	public Bullet() {
		setVector(0,0,0);
		setRotation(0,0,0);
		setSize(9);
		setDirection(0,1,0);
		setMagnitude(40);
		
		damage = 2;
	}
	@Override
	public void update(float tpf) {
		super.update(tpf);
	}
}
