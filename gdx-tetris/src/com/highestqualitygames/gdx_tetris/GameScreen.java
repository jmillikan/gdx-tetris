package com.highestqualitygames.gdx_tetris;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;

public class GameScreen implements Screen {
	interface GameState {
		void draw();
		void update(float delta);
	}
	
	SpriteBatch batch = new SpriteBatch();
	Vector3 touchPoint = new Vector3();

	Game game;
	OrthographicCamera cam;
	Sprite mm_sprite, block_sprite;
	BitmapFont font = new BitmapFont();
	
	final int BLOCK_SIZE = 20;
	
	final int FASTER_COUNT = 1;
	final float FASTER_RATIO = 0.98f;
	
	float drop_timeout = 1;
	int rows = 0;
	int score = 0;
	
	// row-major grid, 0 at the bottom, 21/20 hidden at the top
	final int GRID_HEIGHT = 22;
	final int GRID_WIDTH = 10;
	
	GameState state;

	boolean[][] grid = new boolean[GRID_HEIGHT][GRID_WIDTH];
	
	// A square array of length 2/3/4
	boolean[][] piece;
	
	// These are interpreted as grid position of the top left corner
	// of the piece (which give the odd-looking "- i" bits) -
	// should be changed to bottom left corner at some point
	int piece_x;
	int piece_y;
	
	float next_drop;
	
	public GameScreen(Game game) {
		this.game = game;
		
		state = new RunningState();
		
		spawnPiece();
		
		cam = new OrthographicCamera();
		cam.setToOrtho(false, 800, 480);
		
		mm_sprite = new Sprite(Assets.gameScreen);
		mm_sprite.setPosition(0, 0);
		
		block_sprite = new Sprite(Assets.block);
		block_sprite.setPosition(0, 0);
		
		next_drop = drop_timeout;
	}
	
	class RunningState implements GameState {
		public void draw(){
			drawGame();
			drawPiece();
		}
		
		public void update(float delta){
			if(touched(Assets.gameScreenPause1) || touched(Assets.gameScreenPause2)){
				state = new PausedState(RunningState.this);		
			}
			
			next_drop -= delta;
			
			if (next_drop <= 0) {
				dropPiece(false);
			}
			
			handleGameKeys();
		}
	}
	
	class PausedState implements GameState {
		GameState returnState;
		public PausedState(GameState returnState){
			this.returnState = returnState;
		}
		
		public void draw(){
			returnState.draw();
		}
		
		public void update(float delta){
			if(touched(Assets.gameScreenPause1) || touched(Assets.gameScreenPause2)){
				state = returnState;
			}
		}
	}
	
	class ClearingState implements GameState {
		float left = 0.8f;
		boolean cleared = false;
		
		public void draw(){
			drawGame();
		}
		
		public void update(float delta){
			if(touched(Assets.gameScreenPause1) || touched(Assets.gameScreenPause2)){
				state = new PausedState(this);
			}

			left -= delta;
			
			if(left > 0) return;
			
			if(left <= 0 && !cleared){
				clearRows();
				cleared = true;
				left = 0.5f;
			}
			else {
				spawnPiece();
				
				if(collidesWithGridOrWall(piece, piece_x, piece_y)){
					gameOver();
				}
				else {
					state = new RunningState();
				}
			}
		}
	}

	@Override
	public void render(float delta) {
		state.draw();
		state.update(delta);
	}
	
	boolean touched(com.badlogic.gdx.math.Rectangle r){
		if (!Gdx.input.justTouched())
			return false;
		
		// If this could possibly be slow, I could move it t...
		// It won't be slow
		cam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
		
		return r.contains(touchPoint.x, touchPoint.y);
	}
	
	public void handlePauseKeys(Runnable action) {
		if(touched(Assets.gameScreenPause1) || touched(Assets.gameScreenPause2)){
			action.run();
		}
	}

	public void handleGameKeys() {
		if (touched(Assets.gameScreenDrop)){
				dropPiece(true);
		}
			
		if (touched(Assets.gameScreenLeft)){
			if(!collidesWithGridOrWall(piece, piece_x - 1, piece_y)){
				piece_x -= 1;
			}
		}

		if (touched(Assets.gameScreenRight)){
			if(!collidesWithGridOrWall(piece, piece_x + 1, piece_y)){
				piece_x += 1;
			}
		}	

		if (touched(Assets.gameScreenRotLeft)){
			boolean[][] new_piece = this.rotate(false, piece);
			if(!collidesWithGridOrWall(new_piece, piece_x, piece_y)){
				piece = new_piece;
			}
		}

		if (touched(Assets.gameScreenRotRight)){ 
			boolean[][] new_piece = this.rotate(true, piece);
			if(!collidesWithGridOrWall(new_piece, piece_x, piece_y)){
				piece = new_piece;
			}
		}
	}
	
	public void spawnPiece(){
		this.piece = randomPiece();
		this.piece_x = EASY ? 0 : 3;
		this.piece_y = 21;	
		
		next_drop = drop_timeout;
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
	
	public void dropPiece(boolean byUser){
		if(!collidesWithGridOrWall(piece, piece_x, piece_y - 1)){
			piece_y = piece_y - 1;
			
			next_drop = drop_timeout - (byUser ? 0 : next_drop);
		}
		else {
			// TODO: Pause game for this...
			attachPiece();

			state = new ClearingState();
		}
	}
	
	public void clearRows(){
		int rowsCleared = 0;
		
		for(int i = 0; i < GRID_HEIGHT;){
			boolean full = true;
			
			for(int j = 0; j < GRID_WIDTH; j++) {
				if(!grid[i][j]) 
					full = false;
			}
			
			
			if(full){
				// Move rows down...
				rows++;
				rowsCleared++;
				
				if(rows >= FASTER_COUNT){
					drop_timeout = drop_timeout * FASTER_RATIO;
					rows = 0;
				}

				for(int k = i; k < GRID_HEIGHT - 1; k++){
					grid[k] = grid[k + 1];
				}
				
				grid[GRID_HEIGHT - 1] = new boolean[GRID_WIDTH];
			}
			else {
				i++;
			}
			
		}

		score += rowsCleared * rowsCleared * 10;
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
	
	public void drawGame(){
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(cam.combined);
		
		batch.begin();
		mm_sprite.draw(batch);
		batch.end();
		
		batch.begin();
		
		float grid_x = Assets.gameScreenGrid.x;
		float grid_y = Assets.gameScreenGrid.y;
		
		// Render all but top two rows
		for(int i = 0; i < GRID_HEIGHT - 2; i++){
			for(int j = 0; j < GRID_WIDTH; j++){
				if(grid[i][j])
					batch.draw(	
							block_sprite, 
							grid_x + j * BLOCK_SIZE,
							grid_y + i * BLOCK_SIZE);
			}
		}
		
		Matrix4 bigger = new Matrix4().scale(3.0f, 3.0f, 1.0f);
		
		batch.setTransformMatrix(bigger);
		String scoreString = String.format("%06d", score);
	
		for(int i = 0; i < 6; i++){
			font.draw(batch, scoreString.subSequence(i, i+1), 750f / 3.0f, (400.0f - i * 40.0f) / 3.0f);			
		}

		batch.setTransformMatrix(new Matrix4().idt());

		batch.end();
	}

	// Do not call before drawGame
	public void drawPiece(){
		batch.begin();

		float grid_x = Assets.gameScreenGrid.x;
		float grid_y = Assets.gameScreenGrid.y;
		
		int size = piece.length;
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				if(piece[i][j] && piece_y - i < GRID_HEIGHT - 2){
					batch.draw(
							block_sprite, 
							grid_x + BLOCK_SIZE * (piece_x + j),
							grid_y + BLOCK_SIZE * (piece_y - i));
				}
			}
		}
		
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
	

	final static boolean X = true;
	final static boolean O = false;
	// Note: To keep things 'simple', don't write over pieces. Copy instead.
	private static boolean[][][] pieces = new boolean[][][]{
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
	
	public static final boolean EASY = false;
	
	// Return a random piece in first position
	private static boolean[][] randomPiece(){
		if(EASY) {
			boolean easy[][] = new boolean[10][10];
			for(int i = 0; i < 10;i++){
				easy[0][i] = true;
				easy[1][i] = true;
				easy[2][i] = true;
				easy[3][i] = true;
			}
			
			return easy;
		}
		
		return pieces[(int) Math.floor(Math.random() * pieces.length)];
	}
	
	private boolean[][] rotate(boolean right, boolean[][] piece){
		// Assume that piece is a square (not ragged or rectangular)
		boolean[][] new_piece = new boolean[piece.length][piece[0].length];
		
		for(int i = 0; i < piece.length; i++){
			for(int j = 0; j < piece.length; j++){
				new_piece[right ? j : piece.length - 1 - j][right ? piece.length - 1 - i : i] = piece[i][j]; 
			}
		}
		
		return new_piece;
	}
}