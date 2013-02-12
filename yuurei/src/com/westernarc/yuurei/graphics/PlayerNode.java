package com.westernarc.yuurei.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.loaders.ModelLoaderRegistry;
import com.badlogic.gdx.graphics.g3d.loaders.g3d.chunks.G3dExporter;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Model;
import com.badlogic.gdx.math.Vector3;
import com.westernarc.util.FastMath;
import com.westernarc.yuurei.entities.Player;

public class PlayerNode extends Node {
	AnimNode torso, head, skirt;
	public Node torsoNode, skirtNode;
	
	private static final float resetRate = 1.4f;
	private float rotX, rotY;
	private float[] rotZ;//Current component rotation
	private float[] anglelog;//log of past angles
	
	//Farthest away that input will matter for
	//destination and rotation calculations
	float inputMax = 100;
	
	//Model floats up and down
	float floatMax = 0.5f; 
	float floatMin = 0f;
	float floatRate = 0.005f;
	float currentFloat;
	boolean rising;
	
	
	private Vector3 destination;
	private Vector3 target;
	
	//Stores whether or not the player is firing bullets
	private boolean firing;
	
	public boolean isFiring() {
		return firing;
	}
	public void setFiring(boolean f) {
		firing = f;
	}	
	public Vector3 getTarget() {
		return target;
	}
	public void setTarget(Vector3 target) {
		this.target = target;
	}
	public Vector3 getDestination () {
		return destination;
	}
	public void setDestination (Vector3 destination) {
		this.destination = destination;
	}
	public PlayerNode() {
		destination = new Vector3();
		head = new AnimNode(1,14);
		skirt = new AnimNode(1,14);
		torso = new AnimNode(1,14);
		
		Texture texture = new Texture(Gdx.files.internal("textures/yuyuko.png"));
		Material mat = new Material("mat", new TextureAttribute(texture, 0, "s_tex"), new ColorAttribute(Color.WHITE, ColorAttribute.diffuse));

		for(int i = 1; i <= 14; i++) {
			torso.models[i] = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/animYuyuko/animTorso/yuanimtorso" + i + ".g3d"));
			//G3dExporter.export(torso.models[i], Gdx.files.absolute("models/animTorso/yuanimtorso"+i+".g3d"));
			torso.models[i].setMaterial(mat);
		}
		torso.setCycleTime(1/30f);
		torsoNode = new Node();
		torsoNode.addChild(torso);
		
		for(int i = 1; i <= 14; i++) {
			head.models[i] = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/animYuyuko/animHead/yuanimhead" + i + ".g3d"));
			//G3dExporter.export(head.models[i], Gdx.files.absolute("models/animHead/yuanimhead"+i+".g3d"));
			head.models[i].setMaterial(mat);
		}
		head.setCycleTime(1/30f);
		addChild(head);

		for(int i = 1; i <= 14; i++) {
			skirt.models[i] = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/animYuyuko/animSkirt/yuanimskirt" + i + ".g3d"));
			//G3dExporter.export(skirt.models[i], Gdx.files.absolute("models/animSkirt/yuanimskirt"+i+".g3d"));
			skirt.models[i].setMaterial(mat);
		}
		skirt.setCycleTime(1/30f);
		
		skirtNode = new Node();
		skirtNode.addChild(skirt);
		//skirt.translate(0,3,0);
		skirtNode.translation.z = -19f;
		//20,0.2f,-1.6f);
		
		currentFloat = 0;
		rising = true;
		rotZ = new float[3];
		anglelog = new float[30];
	}
	public AnimNode getHead() {
		return head;
	}
	public AnimNode getTorso() {
		return torso;
	}
	public AnimNode getSkirt() {
		return skirt;
	}
	public void update(float tpf) {
		
		//Handle floating
		if(!rising) {
			torso.translate(0, 0, -floatRate);
			head.translate(0, 0, -floatRate);
			skirt.translate(0, 0, -floatRate);
		} else {
			torso.translate(0, 0, floatRate);
			head.translate(0, 0, floatRate);
			skirt.translate(0, 0, floatRate);
		}
		if(torso.getTranslation().z > floatMax) rising = false;
		if(torso.getTranslation().z < floatMin) rising = true;
		
		//Handle destination calculation and model rotation
		Vector3 diff = destination.cpy().sub(translation).mul(0.15f);
		
		//float dist = diff.len();
		//if(dist > diffMax) diff.nor().mul(diffMax); 

		//Rotate by the destination vector
		if(rotX - resetRate > diff.x) rotX -= resetRate; else if(rotX +resetRate < diff.x) rotX += resetRate;
		if(rotY - resetRate> diff.y) rotY -= resetRate; else if(rotY + resetRate < diff.y) rotY += resetRate; 
		
		setRotation(-rotY/2f, rotX/2f, 0);
		skirtNode.setRotation(-rotY*3f, rotX*3f, 0);
		torsoNode.setRotation(-rotY, rotX, 0);

		//Update each animation
		torso.update(tpf);
		head.update(tpf);
		skirt.update(tpf);
		skirtNode.translation.x = translation.x - diff.x/3f;
		skirtNode.translation.y = translation.y - diff.y/2f;
		torsoNode.translation.x = translation.x;
		torsoNode.translation.y = translation.y;
	}
	
	@Override
	public void setScale(float s) {
		super.setScale(s);
		skirtNode.setScale(s);
		torsoNode.setScale(s);
	}
	
	public void rotateZ(float angle) {
		
		//Update angle log
		for(int i = anglelog.length - 1; i > 0; i--) {
			anglelog[i] = anglelog[i - 1]; 
		}
		anglelog[0] = rotZ[0];
		rotZ[1] = anglelog[10];
		rotZ[2] = anglelog[20];

		//Rotate components towards their target angles

		if(rotZ[0] > 270) rotZ[0] -= 360;
		if(rotZ[0] < -90) rotZ[0] += 360;
		
		//Calculate direction of rotation
		float opp;
		int cw;
		if(angle > 90) {
			opp = angle - 180;
			cw = 1;
		} else {
			opp = angle + 180;
			cw = -1;
		}
		
		//Rotate each component towards its target
		if(cw == 1){
			if(rotZ[0] < angle && rotZ[0] > opp) {
				rotZ[0] += 4;
			} else if (rotZ[0] > angle || rotZ[0] < opp) {
				rotZ[0] -= 4;
			}
		} else if(cw == -1) {
			if(rotZ[0] < angle || rotZ[0] > opp) {
				rotZ[0] += 4;
			} else if (rotZ[0] > angle && rotZ[0] < opp) {
				rotZ[0] -= 4;
			}
		}
		
		
		//TODO: fix skirt and torso rotation.

		getHead().setRotation(0, 0, rotZ[0]);
		getSkirt().setRotation(0, 0, rotZ[2]);
		getTorso().setRotation(0, 0, rotZ[1]);
	}
}