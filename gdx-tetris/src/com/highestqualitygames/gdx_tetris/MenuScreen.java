package com.highestqualitygames.gdx_tetris;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;



public class MenuScreen implements Screen {
	Game game;
	SpriteBatch batch;
	OrthographicCamera cam;
	Sprite mm_sprite;
	Rectangle startRect;
	Vector3 touchPoint;
	
	public MenuScreen(Game game) {
		this.game = game;
		
		cam = new OrthographicCamera(1, ((float) Gdx.graphics.getHeight())/((float) Gdx.graphics.getWidth()));
		
		batch = new SpriteBatch();
		
		Texture texture = new Texture(Gdx.files.internal("data/main-screen.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		TextureRegion region = new TextureRegion(texture, 0, 0, 800, 480);
		
		startRect = new Rectangle(120,187, 320, 84);
		startRect = new Rectangle(0,0,1.0f,1.0f);
		
		mm_sprite = new Sprite(region);
		
		float aspect = mm_sprite.getHeight() / mm_sprite.getWidth();
		// TODO: Figure out whether we need any of this
		mm_sprite.setBounds(-0.5f, -0.5f * aspect, 1.0f, aspect);
		
		touchPoint = new Vector3();
	}

	@Override
	public void render(float delta) {
		if (Gdx.input.justTouched()) {
			cam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			if (startRect.contains(touchPoint.x, touchPoint.y)) {
				game.setScreen(new GameScreen(this.game));
				return;
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
