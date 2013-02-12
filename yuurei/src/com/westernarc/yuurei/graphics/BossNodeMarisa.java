package com.westernarc.yuurei.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.loaders.ModelLoaderRegistry;
import com.badlogic.gdx.graphics.g3d.loaders.g3d.chunks.G3dExporter;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Model;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.math.Vector3;

public class BossNodeMarisa extends Node {
	public static final int MAXHP = 600;
	public static final int SHOTMAGNITUDE = 540;
	public static final int MAGNITUDE = 60;
	Node torso;
	
	private static final float resetRate = 0.4f;
	
	//Largest angle rotation can be
	private static final float rotateMax = 30;
	
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

	public BossNodeMarisa() {
		destination = new Vector3();
		torso = new Node();
		
		Texture texture = new Texture(Gdx.files.internal("textures/marisa.png"));
		Material mat = new Material("mat", new TextureAttribute(texture, 0, "s_tex"), new ColorAttribute(Color.WHITE, ColorAttribute.diffuse));

		torso.setModel( ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/marisa.g3dt")));
		torso.getModel().setMaterial(mat);
		addChild(torso);

		currentFloat = 0;
		rising = true;
	}

	public void setTModel(Model m) {
		torso.setModel(m);
	}
	
	public Node getTorso() {
		return torso;
	}
	
	public void update(float tpf) {
		if(rotation.x > 0) rotate(-resetRate,0,0);
		if(rotation.x < 0) rotate(resetRate,0,0);
		if(rotation.y > 0) rotate(0,-resetRate,0);
		if(rotation.y < 0) rotate(0,resetRate,0);
		if(rotation.z > 0) rotate(0,0,-resetRate);
		if(rotation.z < 0) rotate(0,0,resetRate);
		
		//Handle floating
		if(!rising) {
			torso.translate(0, 0, -floatRate);
		} else {
			torso.translate(0, 0, floatRate);
		}
		if(torso.getTranslation().z > floatMax) rising = false;
		if(torso.getTranslation().z < floatMin) rising = true;
		
		//Handle destination calculation and model rotation
		Vector3 diff = destination.cpy().sub(translation);
		diff = diff.nor();
		//Rotate by the destination vector
		rotate(-diff.y,diff.x,0);
	}
	
	@Override
	public void rotate(float x, float y, float z) {
		//Rotate the arms at a different rate than the torso
		//Rotates all of the body
		Vector3 vec = new Vector3(x,y,z);
		
		//If the rotation would put the total rotation over the max,
		//set the to-rotate value to 0
		if(rotation.x > rotateMax && vec.x > 0) vec.x = 0;
		if(rotation.x < -rotateMax && vec.x < 0) vec.x = 0;
		if(rotation.y > rotateMax && vec.y > 0) vec.y = 0;
		if(rotation.y < -rotateMax && vec.y < 0) vec.y = 0;
		
		super.rotate(vec.x,vec.y,vec.z);
	}
}
