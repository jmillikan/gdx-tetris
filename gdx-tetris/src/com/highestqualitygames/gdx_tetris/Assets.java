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
	
	public static Rectangle gameScreenPreview, gameScreenPreview2, gameScreenGrid;
	public static Rectangle gameScreenRotLeft, gameScreenRotRight, gameScreenLeft, gameScreenRight,
		gameScreenDrop, gameScreenPause1, overMenu, overRestart, pauseMenu, pauseResume;
	
	public static BitmapFont font;
	
	public static Color color1, color2, color1a, color1b;
	
	private static Map<Rectangle, int[]> keyDongles = new HashMap<Rectangle, int[]>();
	
	static Color rgb(int r, int g, int b){
		return new Color(r / 255f, g / 255f, b / 255f, 1.0f);
	}
	
	static Color bright(Color c){
		return c.lerp(1f, 1f, 1f, 1f, 0.5f);
	}
	
	// So I don't have to think when copying these from Inkscape
	static Rectangle grid_r(float x, float y, float w, float h, float blocks_wide, float blocks_high){
		return new Rectangle(x + 3, y - 32 + 3, (w - 6) / blocks_wide, (h - 6) / blocks_high);
	}
	
	static Rectangle button_r(float x, float y, float w, float h){
		return new Rectangle(x, y  - 32f, w, h);
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
		// Note: The width and height store a block size rather than... uh, something reasonable
		gameScreenGrid = new Rectangle(300, 39, 20f * (10f/11f), 20f * (10f/11f));
		gameScreenDrop = new Rectangle(277, 10, 243, 94);
		gameScreenPause1 = new Rectangle(102, 380, 138, 78);
		
		keyDongles.put(gameScreenRotLeft, new int[]{Keys.SLASH, Keys.Z});
		keyDongles.put(gameScreenRotRight, new int[]{Keys.SHIFT_RIGHT, Keys.X});
		keyDongles.put(gameScreenDrop, new int[]{Keys.DOWN, Keys.S});
		keyDongles.put(gameScreenLeft, new int[]{Keys.LEFT, Keys.A});
		keyDongles.put(gameScreenRight, new int[]{Keys.RIGHT, Keys.D});
		keyDongles.put(gameScreenPause1, new int[]{Keys.P});
		
		gameScreenPreview = grid_r(510, 390, 95, 95, 5, 5);
		gameScreenPreview2 = grid_r(610, 417, 74, 74, 5, 5);
		
		texture = new Texture(Gdx.files.internal("data/main-screen.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		mainScreen = new TextureRegion(texture, 0, 0, 800, 480);
		mainScreenStart = new Rectangle(581, 34, 164, 84);
		mainScreenMode1 = new Rectangle(128, 337, 250, 122);
		mainScreenMode2 = new Rectangle(128, 205, 250, 122);
		mainScreenMode3 = new Rectangle(128, 71, 250, 122);
		mainScreenTest = button_r(280, 43, 112, 52);
		
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
		
		block = new TextureRegion(texture, 1, 1, 18, 18);
		
		font = new BitmapFont(Gdx.files.internal("data/ubuntu-mono-48.fnt"),
		         Gdx.files.internal("data/ubuntu-mono-48.png"), false);
	}

	// hack hack hack...
	static HashSet<Integer> currentlyPressed = new HashSet<Integer>();
	// hack hack hack hack hack..
	public static void clearKeyDongles(){
		currentlyPressed = new HashSet<Integer>();
	}
	
	// Doesn't belong in assets - more like a "platform" file or something
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
	
	final static boolean X = true;
	final static boolean O = false;
	
	public static boolean[][][] threePieces = new boolean[][][]{
		{{O,X,O},
			{O,X,O},
			{O,X,O}},
			{{X,O},{X,X}}
	};
	
	public static boolean[][][] niceFives = new boolean[][][]{
		{{X,X,X},
			{X,X,O},
			{O,O,O}
		},
		{{X,X,X},
			{O,X,X},
			{O,O,O}
		},
		{{O,O,O,O},
			{X,X,O,O},
			{O,X,X,X},
			{O,O,O,O}
		},
		{{O,O,O,O},
			{O,X,X,X},
			{X,X,O,O},
			{O,O,O,O}
		},
		{{X,X,X},
			{X,O,O},
			{X,O,O}
		},
		{{X,X,O},
			{O,X,X},
			{O,O,X}

		},
		{{X,O,O},
			{X,X,X},
			{O,X,O}
		},
		{{O,O,X},
			{X,X,X},
			{O,X,O}
		},
		{{O,O,O,O,O},
			{X,X,X,X,X},
			{O,O,O,O,O},
			{O,O,O,O,O},
			{O,O,O,O,O}
		},
		{{O,O,O,O,O},
			{X,X,X,X,X},
			{O,O,O,O,O},
			{O,O,O,O,O},
			{O,O,O,O,O}
		}
	};
	
	public static boolean[][][] modFives = new boolean[][][]{
		{{O,O,O,O,O},
			{X,X,X,X,X},
			{O,O,O,O,O},
			{O,O,O,O,O},
			{O,O,O,O,O}
		},
		{{X,O,X},{X,X,X},{O,O,O}},
		{{X,X,X},
			{O,X,O},
			{O,X,O}
		},
		{{O,O,O,O},
			{X,X,X,X},
			{O,O,X,O},
			{O,O,O,O}
		},
		{{O,O,O,O},
			{X,X,X,X},
			{O,O,O,X},
			{O,O,O,O}

		},
		{{O,O,O,O},
			{O,O,O,X},
			{X,X,X,X},
			{O,O,O,O}
		},
		{{O,O,O,O},
			{O,O,X,O},
			{X,X,X,X},
			{O,O,O,O}
		},
		{{O,X,O},
			{X,X,X},
			{O,X,O}
		},
		{{X,X,O},
			{O,X,O},
			{O,X,X}
		},
		{{O,X,X},
			{O,X,O},
			{X,X,O}
		}

	};
	
	public static boolean[][][] classicPieces = new boolean[][][]{
		{{O,X,O,O},
		 {O,X,O,O},
		 {O,X,O,O},
		 {O,X,O,O}},
		   {{X,X},
			{X,X}},
		   {{O,O,O},
			{X,X,X},
			{O,X,O}},
		   {{O,X,O},
			{O,X,O},
			{O,X,X}},
		   {{O,X,O},
			{O,X,O},
			{X,X,O}},
		   {{O,X,O},
			{X,X,O},
			{X,O,O}},
		   {{O,X,O},
			{O,X,X},
			{O,O,X}}
		};
	
	static boolean[] b3 = {O,O,O};
	static boolean[] b4 = {O,O,O,O};
	
	public static boolean[][][] fours = new boolean[][][]{
		{b4, {X,X,X,O},{O,O,O,X}, b4},
		{b4, {O,O,O,X},{X,X,X,O}, b4},
		{b4, {X,X,O,O}, {O,O,X,X}, b4},
		{b4, {X,X,O,O}, {X,O,X,O}, b4},
		{b4, {X,O,X,O}, {X,X,O,O}, b4},
		{{X,O,O,O}, {O,X,X,O}, {O,X,O,O}, b4},
		{{O,O,O,X}, {O,X,X,O}, {O,X,O,O}, b4},
		{b4, {O,X,O,O}, {O,X,X,O}, {O,O,O,X}}
	};
}
