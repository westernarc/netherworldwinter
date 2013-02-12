package com.westernarc.yuurei.entities;

public interface Fireable {
	public float SHOT_DELAY = 0.1f;
	public float shotTimer = 0;
	public boolean readyToFire = false;
	
	public boolean isReadyToFire();
}
