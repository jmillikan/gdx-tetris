package com.highestqualitygames.gdx_tetris;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Assets {
	public static TextureRegion gameScreen, mainScreen;
	
	// These rectangles are relative to the BG (and so will need adjusted if the camera is not 1:1 with it)
	public static Rectangle mainScreenStart;
	
	public static Rectangle gameScreenRotLeft, gameScreenRotRight, gameScreenLeft, gameScreenRight;
	
	public static void load() {
		Texture texture;
		
		texture = new Texture(Gdx.files.internal("data/game-screen.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		gameScreen = new TextureRegion(texture, 0, 0, 800, 480);
		gameScreenRotLeft = new Rectangle(123, 232, 138, 143); 
		gameScreenRotRight = new Rectangle(542, 232, 138, 143); 
		gameScreenLeft = new Rectangle(123, 32, 138, 143); 
		gameScreenRight = new Rectangle(542, 32, 138, 143); 
		
		texture = new Texture(Gdx.files.internal("data/main-screen.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		mainScreen = new TextureRegion(texture, 0, 0, 800, 480);
		mainScreenStart = new Rectangle(120, 187, 320, 84);
	}
}
