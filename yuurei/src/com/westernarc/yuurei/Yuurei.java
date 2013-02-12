package com.westernarc.yuurei;

import java.util.ArrayList;
import java.util.Iterator;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.loaders.ModelLoaderRegistry;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.westernarc.util.FastMath;
import com.westernarc.yuurei.graphics.AnimNode;
import com.westernarc.yuurei.graphics.BossNodeMarisa;
import com.westernarc.yuurei.graphics.BossNodeReimu;
import com.westernarc.yuurei.graphics.BossNodeSakuya;
import com.westernarc.yuurei.graphics.Node;
import com.westernarc.yuurei.entities.*;
import com.westernarc.yuurei.graphics.PlayerNode;

public class Yuurei implements ApplicationListener {
	//Debug draws wireframes
	boolean debug = false;
	
	Music bgMusic;
	Sound shotSound;
	Sound killSound;
	Sound bombSound;
	Sound deathSound;
	
	boolean sakuyaAlive;
	boolean marisaAlive;
	boolean reimuAlive;
	int bossWave;
	
	float SCREEN_WIDTH;
	float SCREEN_HEIGHT;
	float EXPECTED_SCREEN_WIDTH;
	float EXPECTED_SCREEN_HEIGHT;
	float SCREEN_FACTOR_X;
	float SCREEN_FACTOR_Y;
	
	float MAGICCIRCLE_MAXRAD = 2;
	float MAGICCIRCLE_MINRAD = 0.4f;
	
	private PerspectiveCamera camera;
	
	private SpriteBatch gameSpriteBatch;
	private ShapeRenderer uiRenderer;
	private SpriteBatch batch;
	private BitmapFont largeFont;
	private BitmapFont defaultFont;
	private BitmapFont smallFont;
	private BitmapFont boldFont;
	
	private static final int MAX_ENEMYDEATHEFFECT = 8;
	private static final int MAX_ENEMIES = 10;
	private ParticleEffect[] enemyDeathEffects;
	private ParticleEffect playerDeathEffect, playerBombEffect, spawnEffect, bossDeathEffect;
	
	private float SCALE_PLAYER = 13f;
	private float SCALE_BULLET = 0.3f;
	private float SCALE_SHOT = 0.8f;
	private float SCALE_ENEMY = 8;
	//Items start scaled 0; Item class expands it
	private float SCALE_ITEM = 0.01f;
	
	//Amount of damage a bomb inflicts
	private int BOMB_DAMAGE = 50;
	
	//Maximum amount of bullets
	private static final int MAX_BULLETS = 40;
	
	//3D assets.
	//Sky model.  This will fade through a number of shades of black to simulate fading in
	//when the game starts
	private static final int SKY_SHADES = 32;
	Material[] skyMatShade;
	StillModel skyModel;
	//StillModel groundModel;
	Node skyNode;
	//Node groundNode;
	
	//Boundary
	AnimNode boundaryNode;
	
	//Player node is for the player character
	//Consists of torso and two arms
	PlayerNode playerNode;
	StillModel torso;
	StillModel lArm;
	StillModel rArm;
	
	//Boss nodes
	Enemy sakuyaEnemy;
	BossNodeSakuya sakuyaNode;
	
	Enemy reimuEnemy;
	BossNodeReimu reimuNode;
	
	Enemy marisaEnemy;
	BossNodeMarisa marisaNode;
	
	//Enemy models
	private static final int ENEMY_FAIRYG = 0;
	private static final int ENEMY_FAIRYB = 1;
	private static final int ENEMY_FAIRYR = 2;
	private static final int ENEMY_TYPEMAX = 8;
	StillModel[] enemyModel;
	
	//Bullet models
	private static final int BULLET_BALLB = 0;
	private static final int BULLET_BALLP = 1;
	private static final int BULLET_BALLR = 2;
	private static final int BULLET_BALLG = 3;
	private static final int BULLET_KNIFE = 4;
	private static final int BULLET_TREE = 6;
	private static final int BULLET_TALISMAN = 5;
	private static final int BULLET_TYPEMAX = 8;
	//StillModel[] bulletModel;
	Texture[] bulletTexture;
	
	//Shot models
	private static final int SHOT_TYPEMAX = 8;
	private static final int SHOT_0 = 0, SHOT_1 = 1, SHOT_2 = 2, SHOT_3 = 3;
	//StillModel[] shotModel;
	Texture[] shotTexture;
		
	//Item models
	/*StillModel powerItemModel;
	StillModel scoreItemModel;
	StillModel bombItemModel;
	StillModel lifeItemModel;*/
	Texture powerItemTexture;
	Texture scoreItemTexture;
	Texture bombItemTexture;
	Texture lifeItemTexture;

	//Objects
	//Player object
	Player player;
	//List of enemies
	ArrayList<Enemy> enemies;
	//List of projectiles
	ArrayList<Bullet> bullets; //From enemy
	ArrayList<Bullet> shots; //From player
	//List of items
	ArrayList<Item> items;
	
	private static final int MAX_CURSORS = 3;
	Vector3[] cursor; //Stores 2 cursors
	
	float RIGHT_MOUSE_X;
	float RIGHT_MOUSE_Y;
	float RIGHT_MOUSE_RADIUS;
	float LEFT_MOUSE_X;
	float LEFT_MOUSE_Y;
	float LEFT_MOUSE_RADIUS;
	float BOMB_MOUSE_X;
	float BOMB_MOUSE_Y;
	float BOMB_MOUSE_RADIUS;
	
	Vector3 RIGHT_MOUSE;
	Vector3 LEFT_MOUSE;
	Vector3 BOMB_MOUSE;
	
	float PLAYFIELD_WIDTH;
	float PLAYFIELD_HEIGHT;
	
	//GAME VARIABLES
	int gameLife;
	int gameScore;
	int gameBomb;
	//Cooldown for bombing
	float BOMB_COOLDOWN = 2f;
	float bombTimer;
	boolean bombReady;
	
	int gameLevel;
	float gameTime;
	//Stores whether or not the player is bombing
	boolean bombing;
	
	//State structure:
	//Title -(continue)> Game.play -(death)> Game.score -(retry)> Game.play
	//State of application
	private enum APP_STATE {
		SPLASH, GAME
	}
	//State within game state
	private enum GAME_STATE {
		TITLE, PLAY, SCORE
	}
	APP_STATE appState;
	GAME_STATE gameState;
	
	private boolean gamePaused;
	
	//Timer for score screen
	private float stateChangeTime;
	private final float STATECHANGE_MIN_TIME = 2;
	
	//Sprite used by batch to make screen fade in and out
	//Decal fadeDecal;
	
	//Fade value
	private float fadeValue;
	private float FADE_MIN = 0;
	private float FADE_MAX = 1;
	private boolean fading;
	//Timer for fading the splash screen
	//Tracks the time spent after being clicked
	private float splashScreenTime; 
	private static final float SPLASHSCREEN_MAX_TIME = 1;
	private boolean splashScreenToEnd;
	
	//Angle that camera is rotated at the beginning of the game
	//The angle starts at an initial angle the first time the game starts.
	//The angle turns downward during gameplay, and stays there.
	//The angle starts at the init angle and ends at the targ angle.
	private final static float CAM_INIT_ANGLE = 70;
	private final static float CAM_TARG_ANGLE = 0;
	private float camAngle;
	
	//Sprites for the UI
	Texture textureUiCircle;
	Texture textureUiCircleSmall;
	Sprite spriteUiCircleLeft;
	Sprite spriteUiCircleRight;
	Sprite spriteUiCircleBomb;
	
	Sprite spriteMagicCircle;
	Sprite spriteMarisaMagicCircle;
	Sprite spriteSakuyaMagicCircle;
	Sprite spriteReimuMagicCircle;
	Texture textureMagicCircle;
	Texture textureMarisaMagicCircle;
	Texture textureSakuyaMagicCircle;
	Texture textureReimuMagicCircle;
	
	private void loadAudio() {
		bgMusic = Gdx.audio.newMusic(Gdx.files.internal("music/drunk as I likev4.mp3"));
		shotSound = Gdx.audio.newSound(Gdx.files.internal("music/se_plst00.wav"));
		killSound = Gdx.audio.newSound(Gdx.files.internal("music/se_enep00.wav"));
		bombSound = Gdx.audio.newSound(Gdx.files.internal("music/se_cat00.wav"));
		deathSound = Gdx.audio.newSound(Gdx.files.internal("music/se_pldead00.wav"));
	}

	private void loadModels() {
		//Load sky
		Texture skyTex = new Texture(Gdx.files.internal("textures/env.png"));
		
		//Create array of sky shade materials
		skyMatShade = new Material[SKY_SHADES];
		
		//Create the shades for the sky
		for(int i = 0; i < SKY_SHADES; i++) {
			float shadeValue = i / (float)SKY_SHADES;
			Color shadeColor = new Color(shadeValue,shadeValue,shadeValue,1);
			skyMatShade[i] = new Material("mat", new TextureAttribute(skyTex, 0, "s_tex"), new ColorAttribute(shadeColor, "diffuseColor"));
		}
		
		skyModel = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/skybox.g3dt"));
		//Set the material to shade 0; black.  Fade it in when the game starts
		skyModel.setMaterial(skyMatShade[0]);
		
		skyNode = new Node();
		skyNode.setModel(skyModel);
		skyNode.setScale(16,16,16);
		/*
		groundModel = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/ground.g3dt"));
		groundModel.setMaterial(skyMatShade[0]);
		
		groundNode = new Node();
		groundNode.setModel(groundModel);
		groundNode.setScale(16,16,16);
		*/
		//Boundary
		Texture boundaryTex = new Texture(Gdx.files.internal("textures/boundary.png"));
		Material boundaryMat = new Material("mat", new TextureAttribute(boundaryTex, 0, "s_tex"), new ColorAttribute(Color.WHITE, "diffuseColor"));
		
		//boundaryNode = new AnimNode(0,18);
		boundaryNode = new AnimNode();
		/*
		for(int i = 0; i <= 18; i++) {
			boundaryNode.models[i] = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/animBoundary/animboundary" + i + ".g3dt"));
			boundaryNode.models[i].setMaterial(boundaryMat);
		}
		boundaryNode.setCycleTime(1/20f);*/
		boundaryNode.setModel(ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/animBoundary/animboundary" + 0 + ".g3dt")));
		boundaryNode.getModel().setMaterial(boundaryMat);
		
		boundaryNode.setScale(13,13,13);
		
		//Texture playerTex = new Texture(Gdx.files.internal("textures/yuyuko.png"));
		//Material playerMat = new Material("mat", new TextureAttribute(playerTex, 0, "s_tex"), new ColorAttribute(Color.WHITE, "diffuseColor"));
		
		//Player assets
		//torso = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/yutorso.g3dt"));
		//rArm = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/yuarmsR.g3dt"));
		//lArm = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/yuarmsL.g3dt"));
		
		//torso.setMaterial(playerMat);
		//rArm.setMaterial(playerMat);
		//lArm.setMaterial(playerMat);
		
		Texture enemy1Tex = new Texture(Gdx.files.internal("textures/enemy.png"));
		Material enemy1Mat = new Material("mat", new TextureAttribute(enemy1Tex, 0, "s_tex"), new ColorAttribute(Color.WHITE, "diffuseColor"));
		
		//Enemy assets
		enemyModel = new StillModel[ENEMY_TYPEMAX];
		enemyModel[ENEMY_FAIRYG] = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/enemy1.g3d"));
		enemyModel[ENEMY_FAIRYG].setMaterial(enemy1Mat);
		
		enemyModel[ENEMY_FAIRYR] = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/enemy2.g3d"));
		enemyModel[ENEMY_FAIRYR].setMaterial(enemy1Mat);
		
		enemyModel[ENEMY_FAIRYB] = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/enemy3.g3d"));
		enemyModel[ENEMY_FAIRYB].setMaterial(enemy1Mat);
		
		//Bullets
		/*
		bulletModel = new StillModel[BULLET_TYPEMAX];
		bulletModel[BULLET_BALLB] = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/bullet0.g3dt"));
		Texture bullTex1 = new Texture(Gdx.files.internal("textures/bullet.png"));
		Material bullMat1 = new Material("mat", new TextureAttribute(bullTex1, 0, "s_tex"), new ColorAttribute(Color.WHITE, "diffuseColor"));
		bulletModel[BULLET_BALLB].setMaterial(bullMat1);
		
		bulletModel[BULLET_BALLR] = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/bullet1.g3dt"));
		bulletModel[BULLET_BALLR].setMaterial(bullMat1);
		*/
		bulletTexture = new Texture[BULLET_TYPEMAX];
		bulletTexture[BULLET_BALLB] = new Texture(Gdx.files.internal("textures/bullet1.png"));
		bulletTexture[BULLET_BALLR] = new Texture(Gdx.files.internal("textures/bullet2.png"));
		bulletTexture[BULLET_BALLG] = new Texture(Gdx.files.internal("textures/bullet3.png"));
		bulletTexture[BULLET_BALLP] = new Texture(Gdx.files.internal("textures/bullet4.png"));
		bulletTexture[BULLET_KNIFE] = new Texture(Gdx.files.internal("textures/knife.png"));
		bulletTexture[BULLET_TREE] = new Texture(Gdx.files.internal("textures/tree.png"));
		bulletTexture[BULLET_TALISMAN] = new Texture(Gdx.files.internal("textures/talisman.png"));
		
		//bulletModel[BULLET_TALISMAN] = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/bullet1.g3dt"));
		//bulletModel[BULLET_TALISMAN].setMaterial(bullMat1);
		
		//Shots
		//Load all shots.  All use same material
		//Texture shotTex1 = new Texture(Gdx.files.internal("textures/shot.png"));
		//Material shotMat1 = new Material("mat", new TextureAttribute(shotTex1, 0, "s_tex"), new ColorAttribute(Color.WHITE, "diffuseColor"));

		//Only 4 types of shot now, otherwise use SHOT_TYPEMAX
		/*
		shotModel = new StillModel[SHOT_TYPEMAX];
		for(int currentModel = 0; currentModel < 4; currentModel++) {
			shotModel[currentModel] = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/shot" + currentModel + ".g3dt"));
			shotModel[currentModel].setMaterial(shotMat1);
		}*/
		shotTexture = new Texture[SHOT_TYPEMAX];
		for(int currentTexture = 0; currentTexture < 4; currentTexture++) {
			shotTexture[currentTexture] = new Texture(Gdx.files.internal("textures/shot"+currentTexture+".png"));
		}
		
		//Set up player
		playerNode = new PlayerNode();
		playerNode.setScale(0.1f);
		
		//Link the graph node to the abstract entity
		player.setNode(playerNode);
		
		//Set up sakuya
		sakuyaNode = new BossNodeSakuya();
		sakuyaNode.scale(SCALE_PLAYER);
		
		//Link sakuya node and enemy
		sakuyaEnemy = new Enemy();
		sakuyaEnemy.setType(Enemy.TYPE.BOSS_SAKUYA);
		sakuyaEnemy.setNode(sakuyaNode);
		
		//Set up Marisa
		marisaNode = new BossNodeMarisa();
		marisaNode.scale(SCALE_PLAYER);
		marisaEnemy = new Enemy();
		marisaEnemy.setType(Enemy.TYPE.BOSS_MARISA);
		marisaEnemy.setNode(marisaNode);
		
		//Set up Reimu
		reimuNode = new BossNodeReimu();
		reimuNode.scale(SCALE_PLAYER);
		reimuEnemy = new Enemy();
		reimuEnemy.setType(Enemy.TYPE.BOSS_REIMU);
		reimuEnemy.setNode(reimuNode);
				
		//Items
		/*
		powerItemModel = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/powerItem.g3dt"));
		scoreItemModel = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/scoreItem.g3dt"));
		lifeItemModel = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/lifeItem.g3dt"));
		bombItemModel = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/bombItem.g3dt"));
		*/
		powerItemTexture = new Texture(Gdx.files.internal("textures/itemPower.png"));
		scoreItemTexture = new Texture(Gdx.files.internal("textures/itemScore.png"));
		lifeItemTexture = new Texture(Gdx.files.internal("textures/itemLife.png"));
		bombItemTexture = new Texture(Gdx.files.internal("textures/itemBomb.png"));
		/*
		Texture itemTex = new Texture(Gdx.files.internal("textures/item.png"));
		Material itemMat = new Material("mat", new TextureAttribute(itemTex, 0, "s_tex"), new ColorAttribute(Color.WHITE, "diffuseColor"));
		
		powerItemModel.setMaterial(itemMat);
		scoreItemModel.setMaterial(itemMat);
		lifeItemModel.setMaterial(itemMat);
		bombItemModel.setMaterial(itemMat);
		*/
	}
	@Override
	public void create() {
		SCREEN_WIDTH = Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();
		System.out.println("Screen dimensions: " + SCREEN_WIDTH + " x " + SCREEN_HEIGHT);
		//From Sony Xperia Ion
		EXPECTED_SCREEN_WIDTH = 1280;
		EXPECTED_SCREEN_HEIGHT = 720;
		SCREEN_FACTOR_X = SCREEN_WIDTH / EXPECTED_SCREEN_WIDTH;
		SCREEN_FACTOR_Y = SCREEN_HEIGHT / EXPECTED_SCREEN_HEIGHT;
		initGame();
	}
	private void initGame() {
		appState = APP_STATE.SPLASH;
		gameState = GAME_STATE.TITLE;
		
		RIGHT_MOUSE_RADIUS = SCREEN_HEIGHT / 4;
		LEFT_MOUSE_RADIUS = RIGHT_MOUSE_RADIUS;
		RIGHT_MOUSE_X = SCREEN_WIDTH - RIGHT_MOUSE_RADIUS - 10;
		
		RIGHT_MOUSE_Y = RIGHT_MOUSE_RADIUS + 10;
		LEFT_MOUSE_X = LEFT_MOUSE_RADIUS + 10;
		LEFT_MOUSE_Y = LEFT_MOUSE_RADIUS + 10;
		
		BOMB_MOUSE_RADIUS = RIGHT_MOUSE_RADIUS / 2;
		BOMB_MOUSE_X = SCREEN_WIDTH - BOMB_MOUSE_RADIUS - 10;
		BOMB_MOUSE_Y = SCREEN_HEIGHT - BOMB_MOUSE_RADIUS - 10;
		
		RIGHT_MOUSE = new Vector3(RIGHT_MOUSE_X, RIGHT_MOUSE_Y, 0);
		LEFT_MOUSE = new Vector3(LEFT_MOUSE_X, LEFT_MOUSE_Y, 0);
		BOMB_MOUSE = new Vector3(BOMB_MOUSE_X, BOMB_MOUSE_Y, 0);
		
		PLAYFIELD_WIDTH = 600;
		PLAYFIELD_HEIGHT = 600;
		
		//Set up the 3d camera
		camera = new PerspectiveCamera(40, SCREEN_WIDTH, SCREEN_HEIGHT);
		camera.position.set(0,0,800);
		camera.far = 10000;
		camAngle = CAM_INIT_ANGLE;
		camera.rotate(Vector3.X, camAngle);
		
		batch = new SpriteBatch();
		defaultFont = new BitmapFont(Gdx.files.internal("font/SegoeUiLight32.fnt"), false);
		smallFont = new BitmapFont(Gdx.files.internal("font/SegoeUiLight16.fnt"), false);
		largeFont = new BitmapFont(Gdx.files.internal("font/SegoeUiLight72.fnt"), false);
		boldFont = new BitmapFont(Gdx.files.internal("font/SegoeUiLight72.fnt"), false);
		gameSpriteBatch = new SpriteBatch();
		uiRenderer = new ShapeRenderer();
		uiRenderer.translate(SCREEN_WIDTH/2, SCREEN_HEIGHT/2, 0);

		player = new Player();
		
		//Set up particle effects
		enemyDeathEffects = new ParticleEffect[MAX_ENEMYDEATHEFFECT];
		for(int i = 0; i < MAX_ENEMYDEATHEFFECT; i++) {
			ParticleEffect newParticleEffect = new ParticleEffect();
			newParticleEffect.load(Gdx.files.internal("data/particledef/8burst.p"), Gdx.files.internal("textures/"));
			enemyDeathEffects[i] = newParticleEffect;
		}
		playerDeathEffect = new ParticleEffect();
		playerDeathEffect.load(Gdx.files.internal("data/particledef/32burst.p"), Gdx.files.internal("textures/"));
		
		playerBombEffect = new ParticleEffect();
		playerBombEffect.load(Gdx.files.internal("data/particledef/64burst.p"), Gdx.files.internal("textures/"));
		
		spawnEffect = new ParticleEffect();
		spawnEffect.load(Gdx.files.internal("data/particledef/starburst.p"), Gdx.files.internal("textures/"));
		
		bossDeathEffect = new ParticleEffect();
		bossDeathEffect.load(Gdx.files.internal("data/particledef/starburst2.p"), Gdx.files.internal("textures/"));
		
		//Create the array lists
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<Bullet>();
		shots = new ArrayList<Bullet>();
		items = new ArrayList<Item>();
		
		cursor = new Vector3[MAX_CURSORS];
		cursor[0] = new Vector3();
		cursor[1] = new Vector3();
		cursor[2] = new Vector3();
		
		gameLife = 100;
		gameBomb = 4;
		bombReady = false;
		bombTimer = 0;
		gameLevel = 1;
		
		stateChangeTime = 0;
		
		splashScreenTime = 0;
		splashScreenToEnd = false;
		
		hiScoreCalculated = false;
		
		textureMagicCircle = new Texture(Gdx.files.internal("textures/magiccirclepurp.png"));
		textureSakuyaMagicCircle = new Texture(Gdx.files.internal("textures/magiccirclewhite.png"));
		textureMarisaMagicCircle = new Texture(Gdx.files.internal("textures/magiccircleblue.png"));
		textureReimuMagicCircle = new Texture(Gdx.files.internal("textures/magiccirclered.png"));
		
		spriteMagicCircle = new Sprite(textureMagicCircle);
		spriteSakuyaMagicCircle = new Sprite(textureSakuyaMagicCircle);
		spriteMarisaMagicCircle = new Sprite(textureMarisaMagicCircle);
		spriteReimuMagicCircle = new Sprite(textureReimuMagicCircle);
		
		spriteMagicCircle.setScale(MAGICCIRCLE_MAXRAD);
		spriteSakuyaMagicCircle.setScale(MAGICCIRCLE_MAXRAD);
		spriteMarisaMagicCircle.setScale(MAGICCIRCLE_MAXRAD);
		spriteReimuMagicCircle.setScale(MAGICCIRCLE_MAXRAD);
		
		//loadModels();
		loadAudio();
		unfade();
		//fadeDecal = Decal.newDecal(new TextureRegion(new Texture(Gdx.files.internal("textures/black.png"))));
		//fadeDecal.setScaleX(SCREEN_WIDTH);
		//fadeDecal.setScaleY(SCREEN_HEIGHT);
		
		//Set up UI sprites
		/*
		textureUiCircle = new Texture(Gdx.files.internal("textures/uicircle.png"));
		textureUiCircleSmall = new Texture(Gdx.files.internal("textures/uicirclesmall.png"));
		spriteUiCircleRight = new Sprite(textureUiCircle);
		spriteUiCircleLeft = new Sprite(textureUiCircle);
		spriteUiCircleBomb = new Sprite(textureUiCircleSmall);
		
		spriteUiCircleRight.setScale(1.45f * SCREEN_FACTOR_X);
		spriteUiCircleLeft.setScale(1.45f * SCREEN_FACTOR_X);
		spriteUiCircleBomb.setScale(1.44f * SCREEN_FACTOR_X);
		
		spriteUiCircleLeft.setPosition(62 * SCREEN_FACTOR_X, 62 * SCREEN_FACTOR_Y);
		spriteUiCircleLeft.setPosition(0, 0);
		spriteUiCircleRight.setPosition(SCREEN_WIDTH - 321 * SCREEN_FACTOR_X, 62 * SCREEN_FACTOR_Y);
		spriteUiCircleBomb.setPosition(SCREEN_WIDTH - 165 * SCREEN_FACTOR_X, SCREEN_HEIGHT - 165 * SCREEN_FACTOR_Y);
		*/
		fadeValue = 1;
	
		sakuyaAlive = false;
		marisaAlive = false;
		reimuAlive = false;
		bossWave = 1;
		
		unpauseGame();
	}
	private void update(float tpf) {
		handleTouchInput(tpf);
		handleKeyInput();
		//Update loop.  Switch depending on state.
		switch(appState) {
			case SPLASH:
				updateSplashState(tpf);
				break;
			case GAME:
				updateGameState(tpf);
				break;
		}
	}
	
	//Keeps track of splash state dialog alpha
	boolean updateSplashStateFading;
	float updateSplashStateTime;
	final static float updateSplashStateCycleTime = 1;
	float splashStateAlphaTitle;
	float timeStep = 1/60f;
	private void updateSplashState(float tpf) {
		
		if(!splashScreenToEnd) {
			splashStateAlphaTitle += timeStep;
		} else {
			splashStateAlphaTitle -= timeStep;
		}
		if(splashStateAlphaTitle > 1) {
			splashStateAlphaTitle = 1;
			//Load models here
			loadModels();
		} else if(splashStateAlphaTitle < 0) splashStateAlphaTitle = 0;
		batch.begin();
		largeFont.setScale(SCREEN_FACTOR_X, SCREEN_FACTOR_Y);
		smallFont.setScale(SCREEN_FACTOR_X, SCREEN_FACTOR_Y);
		boldFont.setScale(SCREEN_FACTOR_X, SCREEN_FACTOR_Y);
		largeFont.setColor(1f, 1f, 1f, splashStateAlphaTitle);
		smallFont.setColor(1f, 1f, 1f, splashStateAlphaTitle);
		largeFont.draw(batch, "WESTERN",400 * SCREEN_FACTOR_X,440 * SCREEN_FACTOR_Y);
		boldFont.setColor(0.3f,0,0,splashStateAlphaTitle);
		boldFont.draw(batch, "|",710 * SCREEN_FACTOR_X,440 * SCREEN_FACTOR_Y);
		largeFont.setColor(1,1,1,splashStateAlphaTitle);
		largeFont.draw(batch, "ARC",730 * SCREEN_FACTOR_X,440 * SCREEN_FACTOR_Y);
		
		smallFont.draw(batch, "l o a d i n g . . .", 1000 * SCREEN_FACTOR_X, 40 * SCREEN_FACTOR_Y);
		//Fade code
		updateSplashStateTime += timeStep;
		if(updateSplashStateTime > 1) {
			if(!splashScreenToEnd){
				splashScreenToEnd = true;
				fade();
			}
		}

		batch.end();
		if(splashScreenToEnd) {
			splashScreenTime += timeStep;
		}
		if(splashScreenTime > SPLASHSCREEN_MAX_TIME) {
			appState = APP_STATE.GAME;
			pauseGame();
			
			//Start music 
			bgMusic.play();
			bgMusic.setLooping(true);
			
			//Create an alpha value for each title element
			//Reset the variables for use in the next phase
			titleAlpha = new float[3];
			for(int i = 0; i < 3; i++) titleAlpha[i] = -1;
			titleStateTime = 0;
			titleFadedIn = false;
			titleFadedOut = false;
		}
	}
	private void updateEntities(float tpf) {
		player.update(tpf);
		
		//If player is out of bounds, kill it
		if(player.getX() > PLAYFIELD_WIDTH/2 || player.getX() < -PLAYFIELD_WIDTH/2 || player.getY() > PLAYFIELD_HEIGHT/2 || player.getY() < -PLAYFIELD_HEIGHT/2) onPlayerDeath();

		//Update boundary animation
		//boundaryNode.update(tpf);
	}
	
	
	//Keeping track of title text
	float titleStateTime;
	float titleAlpha[];
	//The final alphas of each line of text
	float titleAlphaMax[] = {0.7f, 1f, 0.4f};
	boolean titleFadedIn;
	boolean titleFadedOut;
	//Make the camera bob up and down in the title screen
	float cameraSpeed;
	private static final float cameraMaxSpeed = 0.01f;
	boolean bobUp;
	
	private void updateGameTitleState(float tpf) {
		//Bob the camera up and down
		if(bobUp) {
			cameraSpeed += (tpf / 128);
		} else {
			cameraSpeed -= (tpf / 128);
		}
		if(cameraSpeed > cameraMaxSpeed) {
			bobUp = false;
		} else if(cameraSpeed < -cameraMaxSpeed) {
			bobUp = true;
		}
		camera.rotate(Vector3.X, cameraSpeed);
		
		//Calculate the alphas of each line
		if(!titleFadedIn) {
			for(int i = 0; i < 3; i++) {
				if(titleAlpha[i] < titleAlphaMax[i]) {
					titleAlpha[i] += tpf;
				} 
				if(titleAlpha[i] > 1) {
					titleAlpha[i] = 1;
					titleFadedIn = true;
				}
			}
		}
		
		if(titleToPlayTransition) {
			if(titleToPlayTransitionTimer > titleToPlayTransitionMaxTime) {
				unpauseGame();
				gameState = GAME_STATE.PLAY;
				stateChangeTime = 0;
				titleToPlayTransitionTimer = 0;
				titleToPlayTransition = false;
			} else {
				titleToPlayTransitionTimer += tpf;
				for(int i = 0; i < 3; i++) {
					if(titleAlpha[i] > 0) {
						titleAlpha[i] -= tpf;
					} 
					if(titleAlpha[i] < 0) {
						titleAlpha[i] = 0;
					}
				}
			}
		}
		
		batch.begin();
		if(titleAlpha[0] < 0) {
			smallFont.setColor(1f, 1f, 1f, 0);
		} else {
			smallFont.setColor(1f, 1f, 1f, titleAlpha[0]);
		}

		smallFont.draw(batch, "t o u c h   t o   s t a r t !", 1000 * SCREEN_FACTOR_X, 40 * SCREEN_FACTOR_Y);
		smallFont.draw(batch, "t o u h o u", 320 * SCREEN_FACTOR_X, 540 * SCREEN_FACTOR_Y);
		defaultFont.setScale(1f * SCREEN_FACTOR_Y);
		smallFont.setScale(SCREEN_FACTOR_X, SCREEN_FACTOR_Y);
		if(titleAlpha[1] < 0) {
			defaultFont.setColor(1f, 1f, 1f, 0);
		} else {
			defaultFont.setColor(1f, 1f, 1f, titleAlpha[1]);
		}
		defaultFont.draw(batch, "n e t h e r w o r l d  w i n t e r", 170 * SCREEN_FACTOR_X, 520 * SCREEN_FACTOR_Y);
		
		defaultFont.setScale(0.3f * SCREEN_FACTOR_Y);
		if(titleAlpha[2] < 0) {
			smallFont.setColor(1f, 1f, 1f, 0);
		} else {
			smallFont.setColor(1f, 1f, 1f, titleAlpha[2]);
		}
		smallFont.draw(batch, "2012-2013 another perspective of ZUN's perfect cherry blossom", 160 * SCREEN_FACTOR_Y, 488 * SCREEN_FACTOR_Y);
		smallFont.draw(batch, "2.5d top down arena shooting game", 240 * SCREEN_FACTOR_X, 474 * SCREEN_FACTOR_Y);
		batch.end();
	}
	private void updateGameState(float tpf) {
		updateGameVars(tpf);

		switch(gameState) {
			case TITLE:
				updateGameTitleState(tpf);
				drawBackground();
				break;
			case SCORE:
				showScore();
			case PLAY:
				drawBackground();
				
				//Move camera to follow player
				//camera.update(true);
				camera.position.x = player.getX();
				camera.position.y = player.getY() - 260;
				
				//Camera starts at CAM INIT ANGLE at the start of PLAY state.
				//If the camera hasn't shifted down yet, shift it down
				if(camAngle > CAM_TARG_ANGLE+15) {
					float delta = camAngle;
					camAngle -= (tpf) * (camAngle - CAM_TARG_ANGLE);
					delta -= camAngle;
					camera.rotate(Vector3.X, -delta);
				}
				//Update if not paused
				if(!isPaused()) {
					updateEntities(tpf);
				}
				
				//Only check collisions every 2 frames
				currentFrame++;
				if(currentFrame % collisionRate == 0) checkCollisions(tpf);
				updateEnemies(tpf);
				
				updateItems(tpf);
				if(playerNode.getScale().x > 1) drawUI(tpf);
				break;
		}
		//If it's score state, allow clicking on the button to replay
		//Disable moving the controller for the player
		//Show end text and prompt to click again
		/*if(gameState == GAME_STATE.SCORE) {
			showScore();
		}*/
	}
	
	boolean hiScoreCalculated;
	int hiScore;
	private void calculateHiScore() {
		Preferences prefs = Gdx.app.getPreferences("Game Preferences");
		hiScore = prefs.getInteger("hiscore", 0);
		
		if(gameScore > hiScore) {
			hiScore = gameScore;
			prefs.putInteger("hiscore", hiScore);
		}
		hiScoreCalculated = true;
		prefs.flush();
	}
	private void showScore() {
		if(!hiScoreCalculated) {
			calculateHiScore();
		}
		batch.begin();
		defaultFont.setColor(1f, 1f, 1f, 1.0f);
		largeFont.setColor(1f, 1f, 1f, 1.0f);
		//font.setScale(1.8f);
		defaultFont.setScale(1 * SCREEN_FACTOR_Y);
		defaultFont.draw(batch, "y o u r   s c o r e ", SCREEN_WIDTH / 2 - 80 * SCREEN_FACTOR_X, 550 * SCREEN_FACTOR_Y);
		defaultFont.draw(batch, "h i   s c o r e", SCREEN_WIDTH / 2 - 45 * SCREEN_FACTOR_X, 420 * SCREEN_FACTOR_Y);
		//Center the score based on how large it is
		int indent = 0;
		int gameScoreCopy = gameScore;
		while((gameScoreCopy /= 10) > 1) indent++;
		largeFont.draw(batch, "" + gameScore, SCREEN_WIDTH / 2 - (largeFont.getSpaceWidth() * indent) * SCREEN_FACTOR_X, 510 * SCREEN_FACTOR_Y);
		
		indent = 0;
		gameScoreCopy = hiScore;
		while((gameScoreCopy /= 10) > 1) indent++;
		largeFont.draw(batch,""+hiScore, SCREEN_WIDTH / 2 - (largeFont.getSpaceWidth() * indent) * SCREEN_FACTOR_X, 370 * SCREEN_FACTOR_Y);
		
		defaultFont.draw(batch, "t o u c h   t o   r e s t a r t", SCREEN_WIDTH / 2 - 150 * SCREEN_FACTOR_X, 160 * SCREEN_FACTOR_Y);
		batch.end();
	}
	@Override
	public void dispose() {
	}
	private void drawParticles(float tpf) {
		gameSpriteBatch.begin();
		
		for(int i = 0; i < MAX_ENEMYDEATHEFFECT; i++) {
			enemyDeathEffects[i].draw(gameSpriteBatch, tpf);
		}
		
		if(!playerDeathEffect.isComplete()) playerDeathEffect.draw(gameSpriteBatch,  tpf);
		if(!playerBombEffect.isComplete()) playerBombEffect.draw(gameSpriteBatch, tpf);
		if(!playerBombEffect.isComplete()) shakeScreen();
		if(!bossDeathEffect.isComplete()) bossDeathEffect.draw(gameSpriteBatch, tpf);
			
		Iterator<Bullet> shotItr = shots.iterator();
		while(shotItr.hasNext()) {
			Bullet curShot = shotItr.next();
			if(curShot.getSprite() != null) {
				curShot.getSprite().draw(gameSpriteBatch);
			}
		}
		
		Iterator<Bullet> bulletItr = bullets.iterator();
		while(bulletItr.hasNext()) {
			Bullet curBullet = bulletItr.next();
			if(curBullet.getSprite() != null) {
				curBullet.getSprite().draw(gameSpriteBatch);
			}
		}
		
		Iterator<Item> itemItr = items.iterator();
		while(itemItr.hasNext()) {
			Item curItem = itemItr.next();
			if(curItem.getSprite() != null) {
				curItem.getSprite().draw(gameSpriteBatch);
			}
		}

		if(gameState == GAME_STATE.PLAY && camAngle <= CAM_TARG_ANGLE+25) {
			if(spriteMagicCircle.getScaleX() > MAGICCIRCLE_MINRAD) {
				spriteMagicCircle.scale(-0.04f);
				spawnEffect.setPosition(player.getX(), player.getY());
				if(spawnEffect.isComplete()) {
					spawnEffect.start();
					playerNode.setScale(SCALE_PLAYER);
				}
			} 
			spriteMagicCircle.setPosition(player.getX() - spriteMagicCircle.getWidth()/2, player.getY() - spriteMagicCircle.getWidth()/2 - 10);
			spriteMagicCircle.rotate(tpf*300);
			spriteMagicCircle.draw(gameSpriteBatch);
			if(marisaAlive) {
				if(spriteMarisaMagicCircle.getScaleX() > MAGICCIRCLE_MINRAD) spriteMarisaMagicCircle.scale(-0.04f);
				spriteMarisaMagicCircle.setPosition(marisaEnemy.getX() - spriteMarisaMagicCircle.getWidth()/2, marisaEnemy.getY() - spriteMarisaMagicCircle.getWidth()/2 - 10);
				spriteMarisaMagicCircle.rotate(tpf*300);
				spriteMarisaMagicCircle.draw(gameSpriteBatch);
			}
			if(sakuyaAlive) {
				if(spriteSakuyaMagicCircle.getScaleX() > MAGICCIRCLE_MINRAD) spriteSakuyaMagicCircle.scale(-0.04f);
				spriteSakuyaMagicCircle.setPosition(sakuyaEnemy.getX() - spriteSakuyaMagicCircle.getWidth()/2, sakuyaEnemy.getY() - spriteSakuyaMagicCircle.getWidth()/2 - 10);
				spriteSakuyaMagicCircle.rotate(tpf*300);
				spriteSakuyaMagicCircle.draw(gameSpriteBatch);
			}
			if(reimuAlive) {
				if(spriteReimuMagicCircle.getScaleX() > MAGICCIRCLE_MINRAD) spriteReimuMagicCircle.scale(-0.04f);
				spriteReimuMagicCircle.setPosition(reimuEnemy.getX() - spriteReimuMagicCircle.getWidth()/2, reimuEnemy.getY() - spriteReimuMagicCircle.getWidth()/2 - 10);
				spriteReimuMagicCircle.rotate(tpf*300);
				spriteReimuMagicCircle.draw(gameSpriteBatch);
			}
		}
		
		spawnEffect.draw(gameSpriteBatch,tpf);
		gameSpriteBatch.end();
	}
	
	int[] indentation = {0, 7, 14, 21, 28, 35, 42, 49, 56};
	float lifeAlpha, bombAlpha, powerAlpha, scoreAlpha;
	private void drawUI(float tpf) {
		batch.begin();
		if(lifeAlpha < 0.5f) lifeAlpha = 0.5f; else if(lifeAlpha > 0.5f) lifeAlpha -= tpf;
		if(bombAlpha < 0.5f) bombAlpha = 0.5f; else if(bombAlpha > 0.5f) bombAlpha -= tpf;
		if(powerAlpha < 0.5f) powerAlpha = 0.5f; else if(powerAlpha > 0.5f) powerAlpha -= tpf;
		if(scoreAlpha < 0.8f) scoreAlpha = 0.8f; else if(scoreAlpha > 0.8f) scoreAlpha -= tpf;
		
		defaultFont.setColor(1f, 1f, 1f, 0.5f);
		defaultFont.setScale(1 * SCREEN_FACTOR_Y);
		/*
		font.draw(batch, "vector: " + player.getVector().toString(), 0, 160);
		font.draw(batch, "destination: " + player.getDestination().toString(), 0, 150);
		defaultFont.draw(batch, "direction: " + player.getDirection().toString(), 0, 140);
		defaultFont.draw(batch, "rotation: " + player.getRotation().toString(), 0, 130);
		font.draw(batch, "cursor1: " + cursor[0].toString(), 200, 160);
		font.draw(batch, "cursor2: " + cursor[1].toString(), 200, 150);
		*/
		//defaultFont.draw(batch, ""+tpf * 3600, 400 * SCREEN_FACTOR_X, 20 * SCREEN_FACTOR_Y);
		
		defaultFont.setColor(1f, 1f, 1f, lifeAlpha);
		defaultFont.draw(batch, ""+gameLife, 190 * SCREEN_FACTOR_X, SCREEN_HEIGHT - 20 * SCREEN_FACTOR_Y);
		defaultFont.setColor(1f, 1f, 1f, bombAlpha);
		defaultFont.draw(batch, ""+gameBomb, 190 * SCREEN_FACTOR_X, SCREEN_HEIGHT - 45 * SCREEN_FACTOR_Y);
		defaultFont.setColor(1f, 1f, 1f, powerAlpha);
		defaultFont.draw(batch, "" + player.getPower(), 190 * SCREEN_FACTOR_X, SCREEN_HEIGHT - 70 * SCREEN_FACTOR_Y);
		defaultFont.setColor(1f, 1f, 1f, scoreAlpha);
		defaultFont.draw(batch, "" + gameScore, SCREEN_WIDTH/2, SCREEN_HEIGHT - 40 * SCREEN_FACTOR_Y);

		defaultFont.setColor(1f, 1f, 1f, 0.5f);
		defaultFont.draw(batch, "l i f e" , 20 * SCREEN_FACTOR_X, SCREEN_HEIGHT - 20 * SCREEN_FACTOR_Y);
		defaultFont.draw(batch, "b o m b  ", 20 * SCREEN_FACTOR_X, SCREEN_HEIGHT - 45 * SCREEN_FACTOR_Y);
		defaultFont.draw(batch, "p o w e r", 20 * SCREEN_FACTOR_X, SCREEN_HEIGHT - 70 * SCREEN_FACTOR_Y);
		defaultFont.draw(batch, "m o v e" , LEFT_MOUSE_X - 50 * SCREEN_FACTOR_X, LEFT_MOUSE_Y - 130 * SCREEN_FACTOR_Y);
		defaultFont.draw(batch, "s h o o t" , RIGHT_MOUSE_X - 50 * SCREEN_FACTOR_X, RIGHT_MOUSE_Y - 130 * SCREEN_FACTOR_Y);
		defaultFont.draw(batch, "b o m b" , BOMB_MOUSE_X - 55 * SCREEN_FACTOR_X, BOMB_MOUSE_Y - 40 * SCREEN_FACTOR_Y);


		/*
		spriteUiCircleLeft.draw(batch);
		spriteUiCircleRight.draw(batch);
		spriteUiCircleBomb.draw(batch);
		*/
		batch.end();
		
		//Draw touch circles
		uiRenderer.setColor(0.7f,0.7f,0.7f,1);
		uiRenderer.begin(ShapeType.Circle);
		uiRenderer.circle(RIGHT_MOUSE_X - SCREEN_WIDTH / 2, RIGHT_MOUSE_Y - SCREEN_HEIGHT / 2, RIGHT_MOUSE_RADIUS);
		//uiRenderer.circle(RIGHT_MOUSE_X - SCREEN_WIDTH / 2, RIGHT_MOUSE_Y - SCREEN_HEIGHT / 2, RIGHT_MOUSE_RADIUS-1);
		//uiRenderer.circle(RIGHT_MOUSE_X - SCREEN_WIDTH / 2, RIGHT_MOUSE_Y - SCREEN_HEIGHT / 2, RIGHT_MOUSE_RADIUS-2);

		uiRenderer.circle(LEFT_MOUSE_X - SCREEN_WIDTH / 2, LEFT_MOUSE_Y - SCREEN_HEIGHT / 2, LEFT_MOUSE_RADIUS);
		//uiRenderer.circle(LEFT_MOUSE_X - SCREEN_WIDTH / 2, LEFT_MOUSE_Y - SCREEN_HEIGHT / 2, LEFT_MOUSE_RADIUS-1);
		//uiRenderer.circle(LEFT_MOUSE_X - SCREEN_WIDTH / 2, LEFT_MOUSE_Y - SCREEN_HEIGHT / 2, LEFT_MOUSE_RADIUS-2);

		uiRenderer.circle(BOMB_MOUSE_X - SCREEN_WIDTH / 2, BOMB_MOUSE_Y - SCREEN_HEIGHT / 2, BOMB_MOUSE_RADIUS);
		//uiRenderer.circle(BOMB_MOUSE_X - SCREEN_WIDTH / 2, BOMB_MOUSE_Y - SCREEN_HEIGHT / 2, BOMB_MOUSE_RADIUS-1);
		//uiRenderer.circle(BOMB_MOUSE_X - SCREEN_WIDTH / 2, BOMB_MOUSE_Y - SCREEN_HEIGHT / 2, BOMB_MOUSE_RADIUS-2);
		uiRenderer.end();

	}
	private void drawBackground() {
		gameSpriteBatch.begin();

		gameSpriteBatch.end();
	}
	private Vector3 createSpawnVector() {
		Vector3 spawnVector = new Vector3(((float)Math.random()-0.5f)*PLAYFIELD_WIDTH, ((float)Math.random()-0.5f)*PLAYFIELD_HEIGHT,0);
		//50-50 chance for the vector to be aligned with x or y boundaries
		if(Math.random() > 0.5) {
			if(spawnVector.x >= 0) spawnVector.x = PLAYFIELD_WIDTH/2; else spawnVector.x = -PLAYFIELD_WIDTH/2;
		} else {
			if(spawnVector.y >= 0) spawnVector.y = PLAYFIELD_HEIGHT/2; else spawnVector.y = -PLAYFIELD_HEIGHT/2;
		}
		
		while(spawnVector.cpy().sub(player.getVector()).len() < 200) {
			spawnVector.set(((float)Math.random()-0.5f)*PLAYFIELD_HEIGHT, ((float)Math.random()-0.5f)*PLAYFIELD_HEIGHT,0);
			//50-50 chance for the vector to be aligned with x or y boundaries
			if(Math.random() > 0.5) {
				if(spawnVector.x >= 0) spawnVector.x = PLAYFIELD_WIDTH/2; else spawnVector.x = -PLAYFIELD_WIDTH/2;
			} else {
				if(spawnVector.y >= 0) spawnVector.y = PLAYFIELD_HEIGHT/2; else spawnVector.y = -PLAYFIELD_HEIGHT/2;
			}
		}
		return spawnVector;
	}
	private void createEnemy() {
		Enemy newEnemy = new Enemy();
		
		//Ensure the enemy does not spawn on the player within 200 units, and that it spawns on the borders of the field
		Vector3 spawnVector = createSpawnVector();
		newEnemy.setVector(spawnVector);
		
		//At enemy creation, set the target and destination to the player's vector
		newEnemy.setTarget(player.getVector());
		newEnemy.setDestination(player.getVector());
		
		//Set the enemy type
		newEnemy.setType(Enemy.TYPE.FAIRY_STRAIGHT);
		
		//If the level is > 5, give a chance to spawn a circle firing enemy
		if(gameLevel > 5) {
			if(Math.random() > 0.5) {
				newEnemy.setType(Enemy.TYPE.FAIRY_CIRCLE);
			}
		}
		//If the level is > 10, give a chance to spawn spiral enemies
		if(gameLevel > 10) {
			if(Math.random() > 0.8) {
				newEnemy.setType(Enemy.TYPE.FAIRY_SPIRAL);
			}
		}
		
		Node eNode = new Node();
		switch(newEnemy.getType()) {
			case FAIRY_STRAIGHT: 
				eNode.setModel(enemyModel[ENEMY_FAIRYG]);
				break;
			case FAIRY_CIRCLE:
				eNode.setModel(enemyModel[ENEMY_FAIRYB]);
				newEnemy.setDestination((float)Math.random() * 2 - 1, (float)Math.random() * 2 - 1, 0);
				break;
			case FAIRY_SPIRAL: 
				eNode.setModel(enemyModel[ENEMY_FAIRYR]);
				break;
			default:
				break;
		}

		enemies.add(newEnemy);
		eNode.setTranslation(newEnemy.getVector());
		newEnemy.setNode(eNode);
		eNode.setScale(SCALE_ENEMY);
		
		//Scale enemy health
		//if(gameLevel > 10) newEnemy.scaleHealth(gameLevel / 10);
		
		//Spawn bosses
		if(( (gameLevel > 15 && bossWave == 1) || (gameLevel > 135 && bossWave == 4) || gameLevel > 260)&& !sakuyaAlive) {
			sakuyaEnemy.setVector(createSpawnVector());
			sakuyaEnemy.setDestination(player.getVector());
			sakuyaEnemy.setTarget(player.getVector());
			sakuyaNode.setTranslation(sakuyaEnemy.getVector());
			enemies.add(sakuyaEnemy);
			if(gameLevel < 260) 
				sakuyaEnemy.setHealth(BossNodeSakuya.MAXHP);
			else 
				sakuyaEnemy.setHealth((int)(BossNodeSakuya.MAXHP/3f));
			sakuyaAlive = true;
			spriteSakuyaMagicCircle.setScale(MAGICCIRCLE_MAXRAD);
			spawnEffect.setPosition(sakuyaEnemy.getX(), sakuyaEnemy.getY());
			if(spawnEffect.isComplete()) {
				spawnEffect.start();
			}
			
			bossWave++;
		}
		if(( (gameLevel > 45 && bossWave == 2) || (gameLevel > 165 && bossWave == 5) || gameLevel > 260) && !marisaAlive) {
			marisaEnemy.setVector(createSpawnVector());
			marisaEnemy.setDestination(-player.getVector().x, -player.getVector().y,0);
			marisaEnemy.setTarget(player.getVector().x, player.getVector().y, 0);
			marisaNode.setTranslation(marisaEnemy.getVector());
			enemies.add(marisaEnemy);
			if(gameLevel < 260)
				marisaEnemy.setHealth(BossNodeMarisa.MAXHP);
			else 
				marisaEnemy.setHealth((int)(BossNodeMarisa.MAXHP/3f));
			marisaAlive = true;
			spriteMarisaMagicCircle.setScale(MAGICCIRCLE_MAXRAD);
			spawnEffect.setPosition(marisaEnemy.getX(), marisaEnemy.getY());
			if(spawnEffect.isComplete()) {
				spawnEffect.start();
			}
			
			bossWave++;
		}
		if(((gameLevel > 85 && bossWave == 3) || (gameLevel > 205 && bossWave == 6) || gameLevel > 260) && !reimuAlive) {
			reimuEnemy.setVector(createSpawnVector());
			reimuEnemy.setDestination(player.getVector().x/2, player.getVector().y/2, 0);
			reimuEnemy.setTarget(player.getVector());
			reimuNode.setTranslation(reimuEnemy.getVector());
			enemies.add(reimuEnemy);
			if(gameLevel < 260)
				reimuEnemy.setHealth(BossNodeReimu.MAXHP);
			else
				reimuEnemy.setHealth((int)(BossNodeReimu.MAXHP/3f));
			reimuAlive = true;
			spriteReimuMagicCircle.setScale(MAGICCIRCLE_MAXRAD);
			spawnEffect.setPosition(reimuEnemy.getX(), reimuEnemy.getY());
			if(spawnEffect.isComplete()) {
				spawnEffect.start();
			}
			
			bossWave++;
		}
	}
	private void createItem(Item.TYPE type, Vector3 position) {
		//Create a new item
		Item newItem = new Item(type);
		//Set its position to a copy of the one specified as an argument
		//Presumably, it is where an enemy has died
		newItem.setVector(position.cpy());
		
		//Prepare a 3d node for the item
		Sprite iSprite;
		switch(type) {
			case BOMB:
				iSprite = new Sprite(bombItemTexture);
				break;
			case LIFE:
				iSprite = new Sprite(lifeItemTexture);
				break;
			case POWER:
				iSprite = new Sprite(powerItemTexture);
				break;
			case SCORE:
				iSprite = new Sprite(scoreItemTexture);
				break;
			default:
				iSprite = new Sprite(scoreItemTexture);
				break;
		}
		
		//Add this item to the list of items
		items.add(newItem);
		//Set the 3d node's vector to the item's vector
		
		//Link the object and node
		newItem.setSprite(iSprite);
		iSprite.setScale(SCALE_ITEM);
		newItem.update(0);
	}
	//Update each item.  Each item should have a copy of the player's vector.
	//Its update will recalculate its magnitude based on how far it is from the player
	private void updateItems(float tpf) {
		Iterator<Item> itemItr = items.iterator();
		while(itemItr.hasNext()) {
			Item curItem = itemItr.next();
			if(!isPaused()) curItem.update(tpf);
			
			//If the item dies(times out), remove it
			if(curItem.isDead()) {
				itemItr.remove();
				continue;
			}
			
			//Set the item's destination to the player's position
			curItem.setDestination(player.getVector());
			
			//Check if the item collides with the player
			//Do different things depending on what kind of item it was
			if(curItem.collidesWith(player)) {
				switch(curItem.getType()) {
				case BOMB:
					bombAlpha = 1;
					gameBomb++;
					break;
				case LIFE:
					lifeAlpha = 1;
					gameLife += 10;
					break;
				case POWER:
					powerAlpha = 1;
					scoreAlpha = 1;
					gameScore += 5;
					player.setPower(player.getPower() + 25);
					break;
				case SCORE:
					scoreAlpha = 1;
					gameScore += 10;
					break;
				default:
					break;
				}
				itemItr.remove();
			}
		}
	}
	
	//Variable to flip between setting target and doing nothing
	int enemyAdjustRate;
	private void updateEnemies(float tpf) {
		//If there are less than gameLevel*2 enemies, give a chance to spawn a new enemy
		if(enemies.size() <= gameLevel*2 && enemies.size() < MAX_ENEMIES) {
			createEnemy();
		}
		
		enemyAdjustRate++;
		Iterator<Enemy> enem = enemies.iterator();
		while(enem.hasNext()) {
			Enemy curEnemy = enem.next();
			//Update each enemy, if the game is not paused
			if(!isPaused()) curEnemy.update(tpf);
			
			
			if(enemyAdjustRate % 3 == 0) {
				curEnemy.setTarget(player.getVector().x, player.getVector().y, 0);
				//if(curEnemy.getType() != Enemy.TYPE.BOSS_SAKUYA) 
				
				switch(curEnemy.getType()) {
				case BOSS_MARISA:
					curEnemy.setDestination(-player.getVector().x, -player.getVector().y,0);
					break;
				case BOSS_REIMU:
					curEnemy.setDestination(player.getVector().x/2, player.getVector().y/2, 0);
					break;
				case BOSS_SAKUYA:
				case FAIRY_SPIRAL:
				case FAIRY_STRAIGHT:
					curEnemy.setDestination(player.getVector());
					break;
				case FAIRY_CIRCLE:
					curEnemy.setDestination(curEnemy.getVector().cpy().add(curEnemy.getDestination()));
					if(curEnemy.getVector().x > PLAYFIELD_WIDTH/2 && curEnemy.getDestination().x > PLAYFIELD_WIDTH/2) curEnemy.setDestination(-curEnemy.getDestination().x, curEnemy.getDestination().y, 0);
					else if(curEnemy.getVector().x < -PLAYFIELD_WIDTH/2 && curEnemy.getDestination().x < -PLAYFIELD_WIDTH/2) curEnemy.setDestination(-curEnemy.getDestination().x, curEnemy.getDestination().y, 0);
					if((curEnemy.getVector().y > PLAYFIELD_HEIGHT/2 && curEnemy.getDestination().y > PLAYFIELD_HEIGHT/2)|| (curEnemy.getVector().y < -PLAYFIELD_HEIGHT/2 && curEnemy.getDestination().y < -PLAYFIELD_HEIGHT/2)) curEnemy.setDestination(curEnemy.getDestination().x, -curEnemy.getDestination().y, 0);
					break;
				default:
					break;
				}
			} else {
				//Spin the enemy on frames the enemy isn't updated
				switch(curEnemy.getType()) {
				case FAIRY_CIRCLE:
					curEnemy.getNode().rotate(0,0,4);
					break;
				case FAIRY_SPIRAL:
					curEnemy.getNode().rotate(0,0,6);
					break;
				case FAIRY_STRAIGHT:
					curEnemy.getNode().rotate(0,0,2);
					break;
				default:
					break;
				}
			}
			if(enemyAdjustRate > 12) {
				enemyAdjustRate = 0;
			}
			//Shoot from enemy
			handleEnemyFiring(curEnemy);
		}
	}
	
	private void handleEnemyFiring(Enemy e) {
		switch(e.getType()) {
		case FAIRY_CIRCLE:
			if(!marisaAlive && !sakuyaAlive && !reimuAlive){
			//Fire a circle of 8 vertices
			Vector3 newTarg = e.getVector();
			e.setTarget(newTarg.cpy().add(10,0,0));
			fireBullet(e);
			e.setTarget(newTarg.cpy().add(10,10,0));
			fireBullet(e);
			e.setTarget(newTarg.cpy().add(0,10,0));
			fireBullet(e);
			e.setTarget(newTarg.cpy().add(-10,10,0));
			fireBullet(e);
			e.setTarget(newTarg.cpy().add(-10,-10,0));
			fireBullet(e);
			e.setTarget(newTarg.cpy().add(-10,0,0));
			fireBullet(e);
			e.setTarget(newTarg.cpy().add(0,-10,0));
			fireBullet(e);
			e.setTarget(newTarg.cpy().add(10,-10,0));
			fireBullet(e);
			}
			break;
		case FAIRY_SPIRAL:

			fireBullet(e);
			break;
		case FAIRY_STRAIGHT:
			//If it is a straight fire fairy, give chance to fire straight at player
			fireBullet(e);
			break;
		case BOSS_SAKUYA:
			Vector3 newTarg2 = e.getVector();
			float origMag = e.getShotMagnitude();

			if(bossWave > 2) {
				e.setShotMagnitude(origMag * 4);
				e.setTarget(player.getVector().x + 20, player.getVector().y, 0);
				fireBullet(e);
				e.setTarget(player.getVector().x - 20, player.getVector().y, 0);
				fireBullet(e);
				e.setTarget(player.getVector().x, player.getVector().y + 20, 0);
				fireBullet(e);
				e.setTarget(player.getVector().x, player.getVector().y - 20, 0);
				fireBullet(e);
			}
			
			int maxShots = 4;
			if(bossWave > 2)
				maxShots--;
			for(int i = 0; i < maxShots; i++) {
				e.setShotMagnitude(origMag * (1 + (i/10f)));
				e.setTarget(newTarg2.cpy().add(FastMath.sin(e.getLifetime() + i/8f),FastMath.cos(e.getLifetime() + i/8f),0));
				fireBullet(e);
				e.setTarget(newTarg2.cpy().add(-FastMath.sin(e.getLifetime() + i/8f),-FastMath.cos(e.getLifetime() + i/8f),0));
				fireBullet(e);
				e.setTarget(newTarg2.cpy().add(FastMath.sin(e.getLifetime() + i/8f + FastMath.HALFPI),FastMath.cos(e.getLifetime() + i/8f + FastMath.HALFPI),0));
				fireBullet(e);
				e.setTarget(newTarg2.cpy().add(-FastMath.sin(e.getLifetime() + i/8f + FastMath.HALFPI),-FastMath.cos(e.getLifetime() + i/8f + FastMath.HALFPI),0));
				fireBullet(e);
			}

			
			e.setTarget(player.getVector());
			e.setShotMagnitude(origMag);

			break;
		case BOSS_MARISA:
			if(bossWave > 3) {
				e.setTarget(player.getVector().x + (float)(Math.random() * 80  - 40), player.getVector().y + (float)(Math.random() * 80 - 40), 0);
			}
			fireBullet(e);
			e.setTarget(player.getVector().x, player.getVector().y, 0);
			break;
		case BOSS_REIMU:
			//Fire a circle of 8 vertices
			Vector3 newTarg3 = player.getVector();
			origMag = e.getShotMagnitude();
			if(bossWave > 4) {
				
				e.setShotMagnitude(origMag*3);
				e.setTarget(newTarg3.cpy().add(10,0,0));
				fireBullet(e);
				e.setTarget(newTarg3.cpy().add(-10,0,0));
				fireBullet(e);
				e.setTarget(newTarg3.cpy().add(0,10,0));
				fireBullet(e);
				e.setTarget(newTarg3.cpy().add(0,-10,0));
				fireBullet(e);
				
				e.setShotMagnitude(origMag);
				e.setTarget(e.getVector().cpy().add(10,0,0));
				fireBullet(e);
				e.setTarget(e.getVector().cpy().add(-10,0,0));
				fireBullet(e);
				e.setTarget(e.getVector().cpy().add(0,10,0));
				fireBullet(e);
				e.setTarget(e.getVector().cpy().add(0,-10,0));
				fireBullet(e);
			} else {
				newTarg3 = e.getVector();
				e.setTarget(newTarg3.cpy().add(10,0,0));
				fireBullet(e);
				e.setTarget(newTarg3.cpy().add(10,10,0));
				fireBullet(e);
				e.setTarget(newTarg3.cpy().add(0,10,0));
				fireBullet(e);
				e.setTarget(newTarg3.cpy().add(-10,10,0));
				fireBullet(e);
				e.setTarget(newTarg3.cpy().add(-10,-10,0));
				fireBullet(e);
				e.setTarget(newTarg3.cpy().add(-10,0,0));
				fireBullet(e);
				e.setTarget(newTarg3.cpy().add(0,-10,0));
				fireBullet(e);
				e.setTarget(newTarg3.cpy().add(10,-10,0));
				fireBullet(e);
			}
			e.setShotMagnitude(origMag);
			e.setTarget(player.getVector());
			break;
		default:
			break;
		}
	}
	private void checkCollisions(float tpf) {
		
		//Check shot-to-enemy collisions
		Iterator<Enemy> ent = enemies.iterator();
		while(ent.hasNext()) {
			//To avoid illegalstateexceptions when removing
			//entities, exit the inner loop after a collision
			//is detected
			boolean removeEnemy = false;

			Enemy e = ent.next();
			
			//If a bomb was used, deal the appropriate damage to all enemies
			if(bombing) {
				e.onHit(BOMB_DAMAGE);
				if(e.getHealth() <= 0) {
					onEnemyKilled(e);
					removeEnemy = true;
				}
			}
			
			//Check each enemy if it collides with the player.
			//If the enemy touches the player, count it as a hit
			//on the player and destroy the enemy
			if(e.collidesWith(player)) {
				removeEnemy = true;
				onEnemyKilled(e);
				onPlayerHit(e);
			}
			
			//For each entity in the arraylist, check if each entity
			//has a collision with each bullet
			
			Iterator<Bullet> shotIterator = shots.iterator();
			while(shotIterator.hasNext()) {
				if(removeEnemy) break;
				
				Bullet b = shotIterator.next();
				
				//If there is a collision, remove the shot
				//Deal the appropriate damage to the enemy
				//Check if the enemy dies here.
				if(e.collidesWith(b)) {
					e.onHit(b.getDamage());
					//Check if the enemy's health reaches 0
					if(e.getHealth() <= 0) {
						onEnemyKilled(e);
						removeEnemy = true;
					}
					shotIterator.remove();
				}
			}
			if(removeEnemy) ent.remove();
		}
		
		//Update each bullet
		Iterator<Bullet> shotIterator = shots.iterator();
		while(shotIterator.hasNext()) {
			Bullet b = shotIterator.next();
			if(b.getX() > PLAYFIELD_WIDTH/2 || b.getX() < -PLAYFIELD_WIDTH/2 || b.getY() > PLAYFIELD_HEIGHT/2 || b.getY() < -PLAYFIELD_HEIGHT/2) {
				shotIterator.remove();
				continue;
			}
			
			//Update each bullet
			if(!isPaused()) b.update(tpf);
		}
		
		//Check bullet-to-player collisions
		Iterator<Bullet> bulletIterator = bullets.iterator();
		while(bulletIterator.hasNext()) {
			Bullet b = bulletIterator.next();
			
			if(b.getX() > PLAYFIELD_WIDTH/2 || b.getX() < -PLAYFIELD_WIDTH/2 || b.getY() > PLAYFIELD_HEIGHT/2 || b.getY() < -PLAYFIELD_HEIGHT/2) {
				bulletIterator.remove();
				continue;
			}
			//Update each bullet if the game is not paused
			if(!isPaused()) b.update(tpf);
			
			//If a bomb was used, destroy all bullets 
			if(bombing) {
				bulletIterator.remove();
				continue;
			}
			
			//If there is a collision, register hit on player
			if(b.collidesWith(player)) {
				bulletIterator.remove();
				onPlayerHit(b);
			}
		}
		
		//Reset bombing to false at the end of this loop
		if(bombing) {
			bombing = false;
			bombReady = false;
			bombTimer = 0;
		}
	}
	//Called when a bullet hits player
	private void onPlayerHit(Entity e) {
		if(gameLife - e.getDamage() <= 0) {
			gameLife = 0;
			onPlayerDeath();
		} else {
			gameLife -= e.getDamage();
			player.setPower(player.getPower() - e.getDamage() * 5);
			powerAlpha = 1;
			lifeAlpha = 1;
		}
		//Create a new particle effect when player is hit
		//Go through the effect array until it finds one that is not complete
		int curEffect = 0;
		while(curEffect < (MAX_ENEMYDEATHEFFECT - 1) && !enemyDeathEffects[curEffect].isComplete()) curEffect++;
		enemyDeathEffects[curEffect].setPosition(player.getX(), player.getY());
		enemyDeathEffects[curEffect].start();
	}
	//Called when player health reaches 
	private void onPlayerDeath() {
		if(!debug) {
			gameState = GAME_STATE.SCORE;
			deathSound.play(2);
			//Activate player death particle effect
			playerDeathEffect.setPosition(player.getX(), player.getY());
			if(playerDeathEffect.isComplete()) playerDeathEffect.start();
			
			//Pause game on death
			pauseGame();
		}
	}
	
	//Called when a player shot hits an enemy
	private void onEnemyKilled(Enemy e) {
		//Increase the score
		switch(e.getType()) {
		case BOSS_MARISA:
			scoreAlpha = 1;
			gameScore += 3000;
			marisaAlive = false;
			bossDeathEffect.setPosition(marisaEnemy.getX(), marisaEnemy.getY());
			bossDeathEffect.start();
			createItem(Item.TYPE.BOMB, e.getVector());
			break;
		case BOSS_REIMU:
			scoreAlpha = 1;
			gameScore += 4000;
			reimuAlive = false;
			bossDeathEffect.setPosition(reimuEnemy.getX(), reimuEnemy.getY());
			bossDeathEffect.start();
			createItem(Item.TYPE.BOMB, e.getVector());
			break;
		case BOSS_SAKUYA:
			scoreAlpha = 1;
			gameScore += 2000;
			sakuyaAlive = false;
			bossDeathEffect.setPosition(sakuyaEnemy.getX(), sakuyaEnemy.getY());
			bossDeathEffect.start();
			createItem(Item.TYPE.BOMB, e.getVector());
			break;
		case FAIRY_CIRCLE:
		case FAIRY_SPIRAL:
		case FAIRY_STRAIGHT:
			scoreAlpha = 1;
			gameScore += 10;
			break;
		default:
			scoreAlpha = 1;
			gameScore += 10;
			break;
		}
		//Drop items
		//Item type is decided by chance.
		double rand = Math.random();
		if(rand < 0.4) {
			createItem(Item.TYPE.SCORE, e.getVector());
		} else if(rand < 0.5) {
			createItem(Item.TYPE.POWER, e.getVector());
		} else if(rand < 0.52) {
			createItem(Item.TYPE.LIFE, e.getVector());
		} else if(rand < 0.54) {
			createItem(Item.TYPE.BOMB, e.getVector());
		}
		//The remaining chance means that no item is spawned
		
		//Create a new particle effect when an enemy is killed
		//Go through the effect array until it finds one that is not complete
		int curEffect = 0;
		while(curEffect < (MAX_ENEMYDEATHEFFECT - 1) && !enemyDeathEffects[curEffect].isComplete()) curEffect++;
		enemyDeathEffects[curEffect].setPosition(e.getX(), e.getY());
		enemyDeathEffects[curEffect].start();
		
		//Play sound
		if(!bombing)killSound.play();
	}
	//Called when, during game_state.score,
	//the player chooses to replay
	private void onGameReplay() {
		camera.position.set(0,0,800);
		
		gameState = GAME_STATE.PLAY;
		unpauseGame();
		player = new Player();
		gameLife = 100;
		gameBomb = 4;
		bombTimer = 0;
		bombReady = false;
		gameScore = 0;
		gameLevel = 1;
		gameTime = 0;
		player.setVector(0,0,0);
		player.setDestination(0,0,0);
		player.setTarget(0,0,0);
		player.setDirection(0,0,0);
		player.setRotation(0, 0, 0);
		player.setPower(0);
		enemies.clear();
		bullets.clear();
		shots.clear();
		items.clear();
		
		stateChangeTime = 0;
		fadeValue = 0;
		fading = false;
		hiScoreCalculated = false;
		
		sakuyaEnemy.setHealth(BossNodeSakuya.MAXHP);
		reimuEnemy.setHealth(BossNodeReimu.MAXHP);
		marisaEnemy.setHealth(BossNodeMarisa.MAXHP);
		
		sakuyaAlive = false;
		marisaAlive = false;
		reimuAlive = false;
		bossWave = 1;
		
		spriteMagicCircle.setScale(MAGICCIRCLE_MAXRAD);
		spriteSakuyaMagicCircle.setScale(MAGICCIRCLE_MAXRAD);
		spriteMarisaMagicCircle.setScale(MAGICCIRCLE_MAXRAD);
		spriteReimuMagicCircle.setScale(MAGICCIRCLE_MAXRAD);
	}
	private void updateGameVars(float tpf) {
		//If game is showing the score, increment the score
		//timer.  This score time must be over the SCORE_MIN time
		//for the game to continue from the score screen.
		if(!isPaused()) gameTime += Gdx.graphics.getDeltaTime();
		else stateChangeTime += Gdx.graphics.getDeltaTime(); 
			
		if(((float)gameScore / (100*gameLevel))> 1) {
			gameLevel++;
		}
		
		//If the bomb is not ready, count up to the cooldown period
		if(bombTimer >= BOMB_COOLDOWN) bombReady = true; else bombReady = false;
		if(!bombReady && bombTimer < BOMB_COOLDOWN) bombTimer += tpf;
	}
	private void updateFade(float tpf) {
		if(fading) {
			fadeValue -= tpf;
		} else {
			fadeValue += tpf;
		}
		if(fadeValue > FADE_MAX) fadeValue = FADE_MAX;
		if(fadeValue < FADE_MIN) fadeValue = FADE_MIN;
		
		int shadeNumber = (int)((fadeValue + FADE_MIN) / FADE_MAX * SKY_SHADES);
		if(shadeNumber >= SKY_SHADES) shadeNumber = SKY_SHADES - 1;
		if(skyModel != null) skyModel.setMaterial(skyMatShade[shadeNumber]);
		//groundModel.setMaterial(skyMatShade[shadeNumber]);
	}
	int currentFrame = 0;
	int collisionRate = 1;
	@Override
	public void render() {		
		Gdx.gl10.glClearColor(0, 0f, 0f, 1);
		Gdx.gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		float tpf = Gdx.graphics.getDeltaTime();
		
		camera.update(true);
		gameSpriteBatch.setProjectionMatrix(camera.combined);
		
		Gdx.gl10.glLoadIdentity();
		Gdx.gl10.glMatrixMode(GL10.GL_PROJECTION);
		if(appState == APP_STATE.GAME) draw3D(tpf);
		update(tpf);
		
		drawParticles(tpf);
		//Draw fade overlay
		//Move this to the front when fill is corrected
		//Currently fills at a constant alpha value, no fading		
		updateFade(tpf);
	}
	private void draw3D(float tpf) {
		Gdx.gl10.glEnable(GL10.GL_DEPTH_TEST);
		Gdx.gl10.glEnable(GL10.GL_TEXTURE_2D);
		Gdx.gl10.glEnable(GL10.GL_COLOR_MATERIAL);

		playerNode.setTranslation(player.getVector());
		playerNode.setDestination(player.getDestination());
		playerNode.setTarget(player.getTarget());
		//If game is past splash screen, draw the background
		//Boundary is split into 3 sections
		if(appState == APP_STATE.GAME) {
			drawNode(skyNode);
			//drawNode(groundNode);
			drawNode(boundaryNode);
		}
		if(gameState == GAME_STATE.PLAY) {

			playerNode.update(tpf);
			drawNode(playerNode.skirtNode);
			drawNode(playerNode.torsoNode);
			drawNode(playerNode);
			
			//Draw enemies
			Iterator<Enemy> enem = enemies.iterator();
			while(enem.hasNext()) {
				Enemy curEnemy = enem.next();
				if(curEnemy.getNode() != null) {
					if(curEnemy.getType() == Enemy.TYPE.BOSS_SAKUYA) {
						BossNodeSakuya sakuya = (BossNodeSakuya)curEnemy.getNode();
						sakuya.setDestination(sakuyaEnemy.getDestination());
						sakuya.setTarget(sakuyaEnemy.getTarget());
						sakuya.update(tpf);
						curEnemy.calcAngleToTarget();
						sakuya.getTorso().setRotation(0, 0, curEnemy.getAngleToTarget());
						
					} else if (curEnemy.getType() == Enemy.TYPE.BOSS_MARISA) {
						BossNodeMarisa marisa = (BossNodeMarisa)curEnemy.getNode();
						marisa.setDestination(marisaEnemy.getDestination());
						marisa.setTarget(marisaEnemy.getTarget());
						marisa.update(tpf);
						curEnemy.calcAngleToTarget();
						marisa.getTorso().setRotation(0, 0, curEnemy.getAngleToTarget());
						
					} else if(curEnemy.getType() == Enemy.TYPE.BOSS_REIMU) {
						BossNodeReimu reimu = (BossNodeReimu)curEnemy.getNode();
						reimu.setDestination(reimuEnemy.getDestination());
						reimu.setTarget(reimuEnemy.getTarget());
						reimu.update(tpf);
						curEnemy.calcAngleToTarget();
						reimu.getTorso().setRotation(0, 0, curEnemy.getAngleToTarget());
					}
					drawNode(curEnemy.getNode());
				}
			}
			//End draw enemies
		}
		
		playerNode.rotateZ(player.getAngleToTarget());

		//playerNode.getRArm().setRotation(0,0,90);

		Gdx.gl10.glDisable(GL10.GL_DEPTH_TEST);
		Gdx.gl10.glDisable(GL10.GL_TEXTURE_2D);
		Gdx.gl10.glDisable(GL10.GL_COLOR_MATERIAL);
	}
	private void drawNode(Node node) {
		Gdx.gl10.glPushMatrix();
		
		Gdx.gl10.glTranslatef(node.getTranslation().x, node.getTranslation().y, node.getTranslation().z);
		Gdx.gl10.glRotatef(node.getRotation().x, 1, 0, 0);
		Gdx.gl10.glRotatef(node.getRotation().y, 0, 1, 0);
		Gdx.gl10.glRotatef(node.getRotation().z, 0, 0, 1);

		Gdx.gl10.glScalef(node.getScale().x, node.getScale().y, node.getScale().z);
		
		if(node.getModel() != null) node.getModel().render();
		
		Iterator<Node> nodeItr = node.children.iterator();
		while(nodeItr.hasNext()) {
			drawNode(nodeItr.next());
		}
		
		Gdx.gl10.glPopMatrix();
	}
	
	boolean shakeDir;
	float shakeAngle;
	int shakeCount;
	private void shakeScreen() {
		if(shakeDir) {
			shakeAngle += 4f;
		} else {
			shakeAngle -= 4f;
		}
		if(shakeAngle > 8 || shakeAngle < -8) {
			shakeDir = !shakeDir;
			shakeCount++;
		}
		//After a certain amount of shakes, set the camera back to its
		//original spot
		if(shakeCount <= 8) {
			camera.translate(shakeAngle,0,0);
		} else {
			if(camera.position.x > 0) {
				camera.translate(-0.1f,0,0);
			} else if(camera.position.x < 0){
				camera.translate(0.1f,0,0);
			}
			if(camera.position.x <= 0.2 || camera.position.x >= -0.2) camera.position.set(player.getX(),player.getY() - 260,800);
		} 
	}
	
	private void onBomb() {
		if(gameBomb >= 1 && !bombing && bombReady) {
			shakeCount = 0;
			bombing = true;
			bombReady = false;
			gameBomb--;
			playerBombEffect.setPosition(player.getX(), player.getY());
			if(playerBombEffect.isComplete()) playerBombEffect.start();
			
			bombSound.play();
		}
	}
	private void handleKeyInput() {
		if(Gdx.input.isKeyPressed(Keys.A)) {
			player.setMagnitude(Player.MAX_MAGNITUDE/2);
			player.setDestination(player.getDestination().cpy().add(-LEFT_MOUSE_RADIUS/2,0,0));
		}
		if(Gdx.input.isKeyPressed(Keys.D)){
			player.setMagnitude(Player.MAX_MAGNITUDE/2);
			player.setDestination(player.getDestination().cpy().add(LEFT_MOUSE_RADIUS/2,0,0));
		}
		if(Gdx.input.isKeyPressed(Keys.W)){
			player.setMagnitude(Player.MAX_MAGNITUDE/2);
			player.setDestination(player.getDestination().cpy().add(0,LEFT_MOUSE_RADIUS/2,0));
		}
		if(Gdx.input.isKeyPressed(Keys.S)) {
			player.setMagnitude(Player.MAX_MAGNITUDE/2);
			player.setDestination(player.getDestination().cpy().add(0,-LEFT_MOUSE_RADIUS/2,0));
		}
		//Debug: press q to gain power
		if(Gdx.input.isKeyPressed(Keys.Q)) {
			player.setPower(player.getPower()+1);
			gameScore += 100;
			gameLife += 100;
		}
		//Debug: press e to bomb
		if(Gdx.input.isKeyPressed(Keys.E)) {
			onBomb();
		}
		if(Gdx.input.isKeyPressed(Keys.J)) player.getTarget().add(-1,0,0);
		if(Gdx.input.isKeyPressed(Keys.L)) player.getTarget().add(1,0,0);
		if(Gdx.input.isKeyPressed(Keys.I)) player.getTarget().add(0,1,0);
		if(Gdx.input.isKeyPressed(Keys.K)) player.getTarget().add(0,-1,0);
		
		if(Gdx.input.isKeyPressed(Keys.SPACE)) {
			//player.setTarget(Gdx.input.getX() - Gdx.graphics.getWidth()/2, -Gdx.input.getY() + Gdx.graphics.getHeight()/2, 0);
			if(player.isReadyToFire()) {
				fireBullet(player);
				player.fire();
				player.setPower(player.getPower() - 1);
			}
		}
	}
	private void handleTouchInput(float tpf) {
		switch(appState) {
			case SPLASH:
				//if(Gdx.input.isTouched()) {
				//	if(!splashScreenToEnd){
				//		splashScreenToEnd = true;
				//		fade();
				//	}
				//}
				break;
			case GAME:
				unfade();
				switch(gameState) {
					case TITLE:
						handleTitleTouchInput();
						break;
					case PLAY:
						handlePlayTouchInput(tpf);
						break;
					case SCORE:
						handleScoreTouchInput();
						break;
				}
				break;
		}
	}
	
	boolean titleToPlayTransition;
	private final static float titleToPlayTransitionMaxTime = 1.5f;
	float titleToPlayTransitionTimer;
	private void handleTitleTouchInput() {
		if(Gdx.input.isTouched() && stateChangeTime > STATECHANGE_MIN_TIME) {
			titleToPlayTransition = true;
		}
	}
	
	//Variables to regulate shots sound playing
	boolean shotSoundReady;
	float shotSoundTimer;
	float SHOTSOUNDTIMER_MAX = 0.15f;
	float shotVariance = 0;
	private void handlePlayerFiring(float tpf) {
		shotSoundTimer += tpf;
		if(shotSoundTimer > SHOTSOUNDTIMER_MAX) {
			shotSoundReady = true; 
			shotSoundTimer = 0;
			//player.setPower(player.getPower() - 1);
		} else {
			shotSoundReady = false; 
		}
		if(shotSoundReady) shotSound.play(0.3f);
		
		//Default: Shoot 
		fireBullet(player);
		playerNode.setFiring(true);
		
		if(player.getNumOfShots() > 1) {
			Vector3 origTarg = player.getTarget().cpy();
			
			//Get the normalized vector from player to target
			Vector3 targDir = origTarg.cpy().sub(player.getVector());
			targDir.nor();
			
			//Change this angle to set new targets
			float angle = player.getAngleToTarget();
			
			//This is the resulting angle
			float newAngle = 0;
			
			//Change the shot magnitude for outer shots
			float originalMag = player.getShotMagnitude();
			
			shotVariance += tpf*3;
			if(shotVariance>10)shotVariance = 0;
			//Increases in power will increase the player's number of shots
			//fired per cycle.  Loop for each shot to make a spray of shots
			for(int i = 1; i <= player.getNumOfShots(); i++) {
				if(i%2 == 0) {
					newAngle = (angle + 20*(i-1)/2) * 6.2831f / 360 + 1.5707f+FastMath.sin(shotVariance)/2;
				} else {
					newAngle = (angle - 20*i/2) * 6.2831f / 360 + 1.5707f-FastMath.sin(shotVariance)/2;
				}
				player.setTarget(player.getVector().cpy().add(FastMath.cos(newAngle), FastMath.sin(newAngle), 0));
				fireBullet(player);
				if(i > 1) player.setShotMagnitude(originalMag*2/i);
			}
			player.setShotMagnitude(originalMag);
			player.setTarget(origTarg);
		}
	}
	private void handlePlayTouchInput(float tpf) {
		//Test if there is any contact in the left touch field
		boolean isMoving = false;
		playerNode.setFiring(false);
		if(Gdx.input.isTouched()) {
			for(int i = 0; i < MAX_CURSORS; i++) {
				cursor[i].x = Gdx.input.getX(i) - SCREEN_WIDTH / 2;
				cursor[i].y = -(Gdx.input.getY(i) - SCREEN_HEIGHT / 2);
			}
			
			//shapeRenderer = new ShapeRenderer();
			//shapeRenderer.translate(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, 0);
			uiRenderer.setColor(0f,0.3f,0,1);

			//Test both cursors whether they are in the movement fields
			for(int currentCursor = 0; currentCursor < MAX_CURSORS; currentCursor++) {
				if(Gdx.input.isTouched(currentCursor)) {
					Vector3 mouseDiffRight = cursor[currentCursor].cpy().sub(RIGHT_MOUSE.x - SCREEN_WIDTH / 2, RIGHT_MOUSE.y - SCREEN_HEIGHT / 2, 0);
					//If cursor is within right mouse
					
					Vector3 mouseDiffLeft = cursor[currentCursor].cpy().sub(LEFT_MOUSE.x - SCREEN_WIDTH / 2, LEFT_MOUSE.y - SCREEN_HEIGHT / 2, 0);
					//If cursor is within left mouse
					
					Vector3 mouseDiffBomb = cursor[currentCursor].cpy().sub(BOMB_MOUSE.x - SCREEN_WIDTH / 2, BOMB_MOUSE.y - SCREEN_HEIGHT / 2, 0);
					//If bomb area is pressed
					
					//If within right
					if(Math.abs(mouseDiffRight.len()) < RIGHT_MOUSE_RADIUS && playerNode.getScale().x > 1) {
						player.setTarget(player.getVector().cpy().add(mouseDiffRight));
						handlePlayerFiring(tpf);
						uiRenderer.setColor(0.6f,0,0,1);
					}
					//If cursor is within left mouse
					if(Math.abs(mouseDiffLeft.len()) < LEFT_MOUSE_RADIUS) {
						isMoving = true;
						player.setMagnitude((mouseDiffLeft.len()/LEFT_MOUSE_RADIUS) * Player.MAX_MAGNITUDE);
						player.setDestination(player.getVector().cpy().add(mouseDiffLeft));
						uiRenderer.setColor(0.6f,0,0,1);
					}
					
					//If cursor is within bomb
					if(Math.abs(mouseDiffBomb.len()) < BOMB_MOUSE_RADIUS) {
						onBomb();
						uiRenderer.setColor(0.6f,0,0,1);
					}
					
					//Draw contact point circle
					//uiRenderer.begin(ShapeType.Circle);
					//uiRenderer.circle(cursor[currentCursor].x, cursor[currentCursor].y, 15);
					//uiRenderer.end();
				}
			}
		}
		if(!isMoving) {
			player.setMagnitude(0); 
			player.setDestination(player.getVector());
		}
	}
	private void handleScoreTouchInput() {
		//If the screen is touched during the 
		//score display phase, replay the game
		if(Gdx.input.isTouched() && stateChangeTime > STATECHANGE_MIN_TIME) {
			onGameReplay();
		}
	}
	private void fireBullet(Entity source) {
		if(source.isReadyToFire()) {
			Bullet newBull = new Bullet();
			newBull.setVector(source.getVector());
			newBull.setDirection(new Vector3().set(source.getTarget()).sub(source.getVector()));
			//newBull.setTarget(newBull.getDirection());
			newBull.setTarget(source.getTarget());
			//Set bullet's magnitude based on source
			newBull.setMagnitude(source.getShotMagnitude());
			
			//Make new scene graph node to link to bullet abstraction
			Sprite bulletSprite;

			//Depending on the source, the bullet is either a shot or bullet
			if(source instanceof Player) {
				shots.add(newBull);
				
				//Change the shot model depending on player power
				switch(player.getPowerLevel()) {
					case 0: bulletSprite = new Sprite(shotTexture[SHOT_0]); break;
					case 1: bulletSprite = new Sprite(shotTexture[SHOT_1]); break;
					case 2: bulletSprite = new Sprite(shotTexture[SHOT_2]); break;
					case 3: bulletSprite = new Sprite(shotTexture[SHOT_3]); break;
					default: bulletSprite = new Sprite(shotTexture[SHOT_0]); 
				}
				
				bulletSprite.setScale(SCALE_SHOT);
				newBull.calcAngleToTarget();
				bulletSprite.rotate(newBull.getAngleToTarget());
				//bulletSprite.rotate(source.getAngleToTarget());
				//bulletSprite.rotate(90);
				
				//Link the bulletnode to the new bullet abstraction
				newBull.setSprite(bulletSprite);
				
				newBull.update(0);
				
			} else if((source instanceof Enemy) && bullets.size() < MAX_BULLETS) {
				//Change details about the bullet depending on what fired it
				switch(((Enemy)source).getType()) {
					case FAIRY_CIRCLE:
						bulletSprite = new Sprite(bulletTexture[BULLET_BALLP]);
						break;
					case FAIRY_SPIRAL:
						bulletSprite = new Sprite(bulletTexture[BULLET_BALLG]);
						break;
					case FAIRY_STRAIGHT:
						bulletSprite = new Sprite(bulletTexture[BULLET_BALLB]);
						break;
				case BOSS_MARISA:
					bulletSprite = new Sprite(bulletTexture[BULLET_TREE]);
					newBull.calcAngleToTarget();
					bulletSprite.rotate(newBull.getAngleToTarget());
					break;
				case BOSS_REIMU:
					bulletSprite = new Sprite(bulletTexture[BULLET_TALISMAN]);
					newBull.calcAngleToTarget();
					bulletSprite.rotate(newBull.getAngleToTarget());
					break;
				case BOSS_SAKUYA:
					bulletSprite = new Sprite(bulletTexture[BULLET_KNIFE]);
					newBull.calcAngleToTarget();
					bulletSprite.rotate(newBull.getAngleToTarget());
					break;
				default:
					bulletSprite = new Sprite(bulletTexture[BULLET_BALLB]);
					break;
				}
				bullets.add(newBull);
				
				//bulletSprite.setPosition(newBull.getX(),newBull.getY());
				bulletSprite.setScale(SCALE_BULLET);
				
				//Link the bulletnode to the new bullet abstraction
				newBull.setSprite(bulletSprite);
				newBull.update(0);
			}
			source.fire();
		}
	}
	@Override
	public void resize(int width, int height) {
	}
	@Override
	public void pause() {
	}
	@Override
	public void resume() {
	}
	private void pauseGame() {
		gamePaused = true;
	}
	private void unpauseGame() {
		gamePaused = false;
	}
	private boolean isPaused() {
		return gamePaused;
	}
	private void fade() {
		fading = true;
	}
	private void unfade() {
		fading = false;
	}
}