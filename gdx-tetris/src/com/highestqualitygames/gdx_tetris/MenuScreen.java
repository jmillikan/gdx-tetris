package com.highestqualitygames.gdx_tetris;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class MenuScreen implements Screen {
	SpriteBatch batch = new SpriteBatch();
	Vector3 touchPoint = new Vector3();

	OrthographicCamera cam;
	Sprite mm_sprite;
	Game game;
	
	GameType gameType = GameType.Classic;
	
	public MenuScreen(Game game) {
		this.game = game;
		
		cam = new OrthographicCamera();
		cam.setToOrtho(false, 800, 480);
		
		mm_sprite = new Sprite(Assets.mainScreen);
		mm_sprite.setPosition(0, 0);
		// width and height are automatic...
	}

	boolean touched(com.badlogic.gdx.math.Rectangle r){
		if (!Gdx.input.justTouched())
			return false;
		
		// If this could possibly be slow, I could move it t...
		// It won't be slow
		cam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
		
		return r.contains(touchPoint.x, touchPoint.y);
	}
	
	@Override
	public void render(float delta) {
		if (touched(Assets.mainScreenMode1)){
			gameType = GameType.Classic;
		}

		if (touched(Assets.mainScreenMode2)){
			gameType = GameType.EasyFives;
		}

		if (touched(Assets.mainScreenMode3)){
			gameType = GameType.ThreesAndFives;
		}

		if (touched(Assets.mainScreenStart)){
			game.setScreen(new GameScreen(game, gameType));
		}

		if (touched(Assets.mainScreenTest)){
			gameType = GameType.Test;
		}

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(cam.combined);
		
		batch.begin();
		
		batch.setColor(Assets.color2);
		
		batch.draw(mm_sprite, 0, 0);

		String typeName1 = gameType.title();
		String typeName2 = gameType.description();
		
		Assets.font.draw(batch, typeName1, 410, 420);
		Assets.font.draw(batch, typeName2, 410, 360);

		//mm_sprite.draw(batch);
		
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
