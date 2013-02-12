package com.westernarc.yuurei.entities;

import com.badlogic.gdx.math.Vector3;
import com.westernarc.yuurei.graphics.BossNodeMarisa;
import com.westernarc.yuurei.graphics.BossNodeReimu;
import com.westernarc.yuurei.graphics.BossNodeSakuya;

public class Enemy extends Entity {
	public static enum TYPE {FAIRY_STRAIGHT,FAIRY_CIRCLE,FAIRY_SPIRAL,BOSS_SAKUYA, BOSS_MARISA, BOSS_REIMU};
	private TYPE type;
	private float lifetime;
	
	//Health value for each enemy
	private int health;
	
	public int getHealth() {
		return health;
	}
	public Enemy() {
		setType(TYPE.FAIRY_STRAIGHT);
		setVector(50,50,0);
		setRotation(0,0,0);
		setSize(15);
		setDirection(0,1,0);
		
		shotsFired = 0;
		shotTimer = 0;
		readyToFire = false;
		lifetime = 0;
	}

	public TYPE getType() {
		return type;
	}
	public void setType(TYPE t) {
		type = t;
		switch(type) {
		case FAIRY_CIRCLE:
			numOfShots = 8;
			shotDelay = 4f;
			setMagnitude(130);
			setShotMagnitude(60);
			health = 20;
			
			damage = 10;
			
			break;
		case FAIRY_SPIRAL:
			numOfShots = 1;
			shotDelay = 2f;
			setMagnitude(220);
			setShotMagnitude(120);
			health = 25;
			
			damage = 10;
			
			break;
		case FAIRY_STRAIGHT:
			numOfShots = 1;
			shotDelay = 1f;
			setMagnitude(20);
			setShotMagnitude(200);
			health = 5;
			
			damage = 10;
			
			break;
		case BOSS_SAKUYA:
			numOfShots = 16;
			shotDelay = 1f;
			setMagnitude(BossNodeSakuya.MAGNITUDE);
			setShotMagnitude(BossNodeSakuya.SHOTMAGNITUDE);
			health = BossNodeSakuya.MAXHP;
			
			damage = 1000;
			break;
		case BOSS_MARISA:
			numOfShots = 2;
			shotDelay = 0.1f;
			setMagnitude(BossNodeMarisa.MAGNITUDE);
			setShotMagnitude(BossNodeMarisa.SHOTMAGNITUDE);
			health = BossNodeMarisa.MAXHP;
			
			damage = 1000;
			break;
		case BOSS_REIMU:
			numOfShots = 8;
			shotDelay = 0.5f;
			setMagnitude(BossNodeReimu.MAGNITUDE);
			setShotMagnitude(BossNodeReimu.SHOTMAGNITUDE);
			health = BossNodeReimu.MAXHP;
			
			damage = 1000;
			break;
		default:
			break;
		}
	}
	Vector3 oldDest;
	
	@Override
	public void update(float tpf) {
		super.update(tpf);
		switch(type) {
			case BOSS_SAKUYA:
				if(destination.cpy().sub(vector).len() < 20) magnitude = 0; else magnitude = BossNodeSakuya.MAGNITUDE;
				break;
			case BOSS_MARISA:
				if(destination.cpy().sub(vector).len() < 30) magnitude = 0; else magnitude = BossNodeMarisa.MAGNITUDE;
				break;
			case BOSS_REIMU:
				if(destination.cpy().sub(vector).len() < 30) magnitude = 0; else magnitude = BossNodeReimu.MAGNITUDE;
				break;
			case FAIRY_CIRCLE:
				break;
			case FAIRY_SPIRAL:
				if(destination.cpy().sub(vector).len() < 40) magnitude = 0; else magnitude = 220;
				break;
			case FAIRY_STRAIGHT:
				break;
			default:
				break;
		}
		lifetime += tpf;
	}
	public float getLifetime() {
		return lifetime;
	}
	//Call this when enemies get hit by a shot
	public void onHit(int damage) {
		health -= damage;
	}
	public void scaleHealth(float factor) {
		health = Math.round(((float)health) * factor);
	}
	public void setHealth(int newhp) {
		health = newhp;
	}
}