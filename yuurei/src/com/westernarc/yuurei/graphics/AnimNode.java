package com.westernarc.yuurei.graphics;

import com.badlogic.gdx.graphics.g3d.model.still.StillModel;

public class AnimNode extends Node {
	//Max and min frames of animation
	protected int maxFrame;
	protected int minFrame;
	
	protected int curFrame;
	protected float cycleTime = 1;
	protected float timer;
	
	public StillModel[] models;
	public AnimNode() {
		this(0,1);
	}
	public AnimNode(int min, int max) {
		maxFrame = max;
		minFrame = min;
		if(minFrame == 0) {
			models = new StillModel[maxFrame + 1];
		} else {
			models = new StillModel[maxFrame + minFrame];
		}
		curFrame = minFrame;
		model = models[min];
	}
	public void update(float tpf) {
		timer += tpf;
		if(timer > cycleTime) {
			timer = 0;
			curFrame++;
			if(curFrame > maxFrame) {
				curFrame = minFrame;
			}
		}
		
		model = models[curFrame];
	}
	
	public int getCurFrame() {
		return curFrame;
	}
	public void setCycleTime(float ct) {
		cycleTime = ct;
	}
}
