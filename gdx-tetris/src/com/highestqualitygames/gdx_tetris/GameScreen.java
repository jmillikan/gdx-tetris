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
	final int BLOCK_SIZE = 20;
	final int GRID_HEIGHT = 22;
	final int GRID_WIDTH = 10;
	final float DROP_TIMEOUT = 1;
	
	// row-major grid, 0 at the bottom, 21/20 hidden at the top
	
	// Currently blocks are 20px, rendered from (300,39) "up"
	boolean[][] grid = new boolean[GRID_HEIGHT][GRID_WIDTH];
	
	// A square array of length 2/3/4
	boolean[][] piece;
	// These are interpreted as grid position of the top left corner.
	int piece_x;
	int piece_y;
	float next_drop;
	
	Game game;
	OrthographicCamera cam;
	Sprite mm_sprite, block_sprite;
	
	public GameScreen(Game game) {
		this.game = game;
		
		spawnPiece();
		
		cam = new OrthographicCamera();
		cam.setToOrtho(false, 800, 480);
		
		mm_sprite = new Sprite(Assets.gameScreen);
		mm_sprite.setPosition(0, 0);
		
		block_sprite = new Sprite(Assets.block);
		block_sprite.setPosition(0, 0);
		
		next_drop = DROP_TIMEOUT;
	}

	@Override
	public void render(float delta) {
		next_drop -= delta;
		
		if (next_drop <= 0) {
			if(!collidesWithGridOrWall(piece, piece_x, piece_y - 1)){
				piece_y = piece_y - 1;
				
				next_drop = DROP_TIMEOUT - next_drop;
			}
			else {
				attachPiece();
				// Attach, new piece...
				spawnPiece();
				
				if(collidesWithGridOrWall(piece, piece_x, piece_y)){
					gameOver();
				}
			}
		}
		
		if (Gdx.input.justTouched()) {
			cam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			if (Assets.gameScreenLeft.contains(touchPoint.x, touchPoint.y)) {
				if(!collidesWithGridOrWall(piece, piece_x - 1, piece_y)){
					piece_x -= 1;
				}
			}
			
			if (Assets.gameScreenRight.contains(touchPoint.x, touchPoint.y)) {
				if(!collidesWithGridOrWall(piece, piece_x + 1, piece_y)){
					piece_x += 1;
				}
			}
			
			if (Assets.gameScreenRotLeft.contains(touchPoint.x, touchPoint.y)) {
				boolean[][] new_piece = this.rotate(true, piece);
				if(!collidesWithGridOrWall(new_piece, piece_x, piece_y)){
					piece = new_piece;
				}
			}
			
			if (Assets.gameScreenRotRight.contains(touchPoint.x, touchPoint.y)) {
				boolean[][] new_piece = this.rotate(false, piece);
				if(!collidesWithGridOrWall(new_piece, piece_x, piece_y)){
					piece = new_piece;
				}
			}
		}
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(cam.combined);
		
		batch.begin();
		mm_sprite.draw(batch);
		batch.end();
		
		batch.begin();
		
		float grid_x = Assets.gameScreenGrid.x;
		float grid_y = Assets.gameScreenGrid.y;
		
		for(int i = 0; i < 20; i++){
			for(int j = 0; j < 10; j++){
				if(grid[i][j])
					batch.draw(	
							block_sprite, 
							grid_x + j * BLOCK_SIZE,
							grid_y + i * BLOCK_SIZE);
			}
		}
		
		int size = piece.length;
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				if(piece[i][j]){
					batch.draw(
							block_sprite, 
							grid_x + BLOCK_SIZE * (piece_x + j),
							grid_y + BLOCK_SIZE * (piece_y - i));
				}
			}
		}
		
		batch.end();
	}
	
	public void spawnPiece(){
		this.piece = randomPiece();
		this.piece_x = 3;
		this.piece_y = 20;	
	}
	
	public void attachPiece(){
		int size = piece.length;
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				if(piece[i][j]){
					grid[piece_y - i][piece_x + j] = true;
				}
			}
		}
	}
	
	public void gameOver(){
		game.setScreen(new MenuScreen(game));
	}
	
	private boolean collidesWithGridOrWall(boolean[][] p, int p_x, int p_y){
		// i is p row, j is p column
		for(int i = 0; i < p.length; i++){
			for(int j = 0; j < p.length; j++){
				if(p[i][j]){
					int b_x = p_x + j;
					int b_y = p_y - i;
					
					if(b_y < 0 || b_y >= GRID_HEIGHT ||
							b_x < 0 || b_x >= GRID_WIDTH ||
							grid[b_y][b_x])
						return true;
				}
			}
		}
		
		return false;
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
	

	
	// Note: To keep things 'simple', don't write over pieces. Copy instead.
	private static boolean[][][] pieces = new boolean[][][]{
		   {{false,false,false},{true,true,true},{false,true,false}},
		   {{false,true,false,false},{false,true,false,false},{false,true,false,false},{false,true,false,false}},
		   {{false,true,false},{false,true,false},{false,true,true}},
		   {{false,true,false},{false,true,false},{true,true,false}},
		   {{false,true,false},{true,true,false},{true,false,false}},
		   {{false,true,false},{false,true,true},{false,false,true}},
		   {{true,true},{true,true}}
		};
	
	// Return a random piece in first position
	private static boolean[][] randomPiece(){
		return pieces[(int) Math.floor(Math.random() * 7)];
	}
	
	private boolean[][] rotate(boolean left, boolean[][] piece){
		// Assume that piece is a square (not ragged or rectangular)
		boolean[][] new_piece = new boolean[piece.length][piece[0].length];
		
		for(int i = 0; i < piece.length; i++){
			for(int j = 0; j < piece.length; j++){
				new_piece[left ? j : piece.length - 1 - j][left ? piece.length - 1 - i : i] = piece[i][j]; 
			}
		}
		
		return new_piece;
	}
}
