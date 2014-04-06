package com.highestqualitygames.gdx_tetris;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Input.Keys;

public class Assets {
	public static TextureRegion gameScreen, mainScreen, block, overScreen, pauseScreen;
	
	// These rectangles are relative to the BG (and so will need adjusted if the camera is not 1:1 with it)
	public static Rectangle mainScreenStart, mainScreenMode1, mainScreenMode2, mainScreenMode3, mainScreenTest;
	
	public static Rectangle gameScreenRotLeft, gameScreenRotRight, gameScreenLeft, gameScreenRight,
		gameScreenGrid, gameScreenDrop, gameScreenPause1, gameScreenPause2, overMenu, overRestart,
		pauseMenu, pauseResume;
	
	public static BitmapFont font;
	
	public static Color color1, color2, color1a, color1b;
	
	private static Map<Rectangle, int[]> keyDongles = new HashMap<Rectangle, int[]>();
	
	static Color rgb(int r, int g, int b){
		return new Color(r / 255f, g / 255f, b / 255f, 1.0f);
	}
	
	static Color bright(Color c){
		return c.lerp(1f, 1f, 1f, 1f, 0.5f);
	}
	
	public static void load(){
		// http://colorschemedesigner.com/#3i61TuRhNw0w0
		color1 = rgb(47,170,170);
		color2 = rgb(198,120,55);
		color1a = rgb(13,38,95);
		color1b = rgb(4,113,4);
		
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
		keyDongles.put(gameScreenRotLeft, new int[]{Keys.SLASH, Keys.Z});
		keyDongles.put(gameScreenRotRight, new int[]{Keys.SHIFT_RIGHT, Keys.X});
		keyDongles.put(gameScreenDrop, new int[]{Keys.DOWN, Keys.S});
		keyDongles.put(gameScreenLeft, new int[]{Keys.LEFT, Keys.A});
		keyDongles.put(gameScreenRight, new int[]{Keys.RIGHT, Keys.D});
		keyDongles.put(gameScreenPause1, new int[]{Keys.P});
		
		texture = new Texture(Gdx.files.internal("data/main-screen.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		mainScreen = new TextureRegion(texture, 0, 0, 800, 480);
		mainScreenStart = new Rectangle(581, 34, 164, 84);
		mainScreenMode1 = new Rectangle(128, 337, 250, 122);
		mainScreenMode2 = new Rectangle(128, 205, 250, 122);
		mainScreenMode3 = new Rectangle(128, 71, 250, 122);
		mainScreenTest = new Rectangle(280, 10, 100, 30);
		
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

	// hack hack hack...
	static HashSet<Integer> currentlyPressed = new HashSet<Integer>();
	// hack hack hack hack hack..
	public static void clearKeyDongles(){
		currentlyPressed = new HashSet<Integer>();
	}
	
	public static boolean isKeyDonglePressed(Rectangle r){
		if(!keyDongles.containsKey(r))
			return false;
		
		for(int k : keyDongles.get(r)){
			if(Gdx.input.isKeyPressed(k)) {
				if(!currentlyPressed.contains(k)){
					currentlyPressed.add(k);
					return true;		
				}		
			}
			else {
				// Note: Can only be removed when it's checked! Good thing that happens a lot...
				currentlyPressed.remove(k);
			}
		}
		
		return false;
	}
}
