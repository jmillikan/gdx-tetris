package com.highestqualitygames.gdx_tetris;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class GameScreen implements Screen {
	SpriteBatch batch = new SpriteBatch();
	Vector3 touchPoint = new Vector3();
	
	// row-major grid, 0 at the bottom, 21/20 hidden at the top
	boolean[][] grid = new boolean[22][10];
	
	Game game;
	OrthographicCamera cam;
	Sprite mm_sprite;
	
	public GameScreen(Game game) {
		this.game = game;
		
		// For getting grid rendering...
		this.grid[21][0] = true;
		this.grid[21][1] = true;
		this.grid[21][2] = true;
		this.grid[22][1] = true;
		
		cam = new OrthographicCamera();
		cam.setToOrtho(false, 800, 480);
		
		mm_sprite = new Sprite(Assets.gameScreen);
		mm_sprite.setPosition(0, 0);
	}

	@Override
	public void render(float delta) {
		if (Gdx.input.justTouched()) {
			cam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			if (Assets.gameScreenLeft.contains(touchPoint.x, touchPoint.y)) {
				throw new java.lang.Error("Yep, left clicked");
			}
			
			if (Assets.gameScreenRight.contains(touchPoint.x, touchPoint.y)) {
				throw new java.lang.Error("Yep, right clicked");
			}
			
			if (Assets.gameScreenRotLeft.contains(touchPoint.x, touchPoint.y)) {
				throw new java.lang.Error("Yep, rotate left clicked");
			}
			
			if (Assets.gameScreenRotRight.contains(touchPoint.x, touchPoint.y)) {
				throw new java.lang.Error("Yep, rotate right clicked");
			}
		}
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(cam.combined);
		
		batch.begin();
		mm_sprite.draw(batch);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
