package com.westernarc.yuurei.entities;

public class Player extends Entity {
	//Maximum movement speed.  Magnitude will vary depending
	//on how far the destination is
	public static final float MAX_MAGNITUDE = 400;
	
	//Thresholds for shot power; at each threshold shot will
	//become different
	public static final int POWERLIMIT1 = 25;
	public static final int POWERLIMIT2 = 75;
	public static final int POWERLIMIT3 = 125;
	private int powerLevel;
	
	//Player shot power
	private int power;
	
	public void setPower(int p) {
		power = p;
		//Preserve non-negative power
		if(power < 0) power = 0;
		
		powerLevel = 0;
		numOfShots = 1;
		//Set different levels of offensive ability for power
		if(power >= POWERLIMIT1) {
			powerLevel = 1;
			//2 shots
			numOfShots = 3;
			setShotMagnitude(900f);
		} 
		if(power >= POWERLIMIT2) {
			powerLevel = 2;
			numOfShots = 3;
			setShotMagnitude(1000f);
		}
		if(power >= POWERLIMIT3) {
			powerLevel = 3;
			numOfShots = 5;
			setShotMagnitude(1100f);
		}
	}
	public int getPowerLevel() {
		return powerLevel;
	}
	public int getPower() {
		return power;
	}
	public Player() {
		setVector(0,0,0);
		setRotation(0,0,0);
		setSize(5);
		setDirection(0,0,0);
		setDestination(0,0,0);
		setMagnitude(100);
		setTarget(0,0,0);
		
		setPower(0);
		numOfShots = 1;
		shotDelay = 0.15f;
		shotTimer = 0;
		setShotMagnitude(800);
		readyToFire = false;
	}
	
	@Override
	public void update(float tpf) {
		super.update(tpf);
		target.add(getVelocity().mul(tpf));
		calcAngleToTarget();
	}
}
