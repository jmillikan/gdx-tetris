package com.highestqualitygames.gdx_tetris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Assets {
	public static TextureRegion gameScreen, mainScreen, block, overScreen, pauseScreen;
	
	// These rectangles are relative to the BG (and so will need adjusted if the camera is not 1:1 with it)
	public static Rectangle mainScreenStart;
	
	public static Rectangle gameScreenRotLeft, gameScreenRotRight, gameScreenLeft, gameScreenRight,
		gameScreenGrid, gameScreenDrop, gameScreenPause1, gameScreenPause2, overMenu, overRestart,
		pauseMenu, pauseResume;
	
	public static BitmapFont font;
	
	public static void load() {
		Texture texture;
		
		texture = new Texture(Gdx.files.internal("data/game-screen.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		// From game-screen.svg - x is SVG x, y is SVG y - 32 (y points up)
		// Width and height are 1:1 for now
		gameScreen = new TextureRegion(texture, 0, 0, 800, 480);
		gameScreenRotLeft = new Rectangle(103, 200, 138, 143); 
		gameScreenRotRight = new Rectangle(562, 200, 138, 143); 
		gameScreenLeft = new Rectangle(123, 32, 138, 143); 
		gameScreenRight = new Rectangle(542, 32, 138, 143); 
		gameScreenGrid = new Rectangle(300, 39, 10 * 20, 22 * 20);
		gameScreenDrop = new Rectangle(277, 10, 243, 94);
		gameScreenPause1 = new Rectangle(102, 380, 138, 78);
		gameScreenPause2 = new Rectangle(562, 380, 138, 78);
		
		texture = new Texture(Gdx.files.internal("data/main-screen.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		mainScreen = new TextureRegion(texture, 0, 0, 800, 480);
		mainScreenStart = new Rectangle(120, 187, 320, 84);

		texture = new Texture(Gdx.files.internal("data/game-over.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		overScreen = new TextureRegion(texture, 0, 0, 800, 480);
		overMenu = new Rectangle(251, 198, 138, 78);
		overRestart = new Rectangle(413, 198, 138, 78);

		texture = new Texture(Gdx.files.internal("data/game-pause.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		pauseScreen = new TextureRegion(texture, 0, 0, 800, 480);
		pauseMenu = new Rectangle(251, 198, 138, 78);
		pauseResume = new Rectangle(413, 198, 138, 78);

		texture = new Texture(Gdx.files.internal("data/block.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		block = new TextureRegion(texture, 0, 0, 20, 20);
		
		font = new BitmapFont(Gdx.files.internal("data/ubuntu-mono-48.fnt"),
		         Gdx.files.internal("data/ubuntu-mono-48.png"), false);
	}
}
